package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.*;
import net.sourceforge.fuge.common.protocol.GenericProtocolApplication;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.MaterialFactorsStore;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.loading.OntologyLoader;
import net.sourceforge.symba.webapp.util.forms.schemes.material.*;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class MaterialTemplateParser {

    public static StringBuffer createMaterialForDataFormContents( HttpSession session,
                                                                  PersonBean personBean,
                                                                  SymbaFormSessionBean symbaFormSessionBean,
                                                                  DatafileSpecificMetadataStore info,
                                                                  int currentDataFile,
                                                                  GenericProtocolApplication genericProtocolApplication ) {

        String gpaParentEndurantId = genericProtocolApplication.getProtocol().getEndurant().getIdentifier();

        StringBuffer buffer = new StringBuffer();

        buffer.append(
                "<input type=\"hidden\" id=\"hiddennewterminfofield\" name=\"hiddennewterminfofield\" value=\"\">" );

        // sometimes, we may get factors from Materials associated with the experiment. Search the materials associated
        // with this GPA for dummies named with the addition of "XXX net.sourceforge.symba.keywords.dummy YYY", where XXX and YYY may be
        // anything.
        int dummyIcmCount = 0;
        for ( Object gmObj : genericProtocolApplication.getInputCompleteMaterials() ) {
            GenericMaterial genericMaterial = ( GenericMaterial ) gmObj;

            if ( genericMaterial.getName().trim().contains( "net.sourceforge.symba.keywords.dummy" ) ) {

                // Retrieve the MaterialFactorsStore list for input complete materials, to be searched when
                // pre-filling metadata into form fields throughout this document.
                MaterialFactorsStore currentMf = null;
                if ( info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ) != null &&
                     info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId )
                             .getInputCompleteMaterialFactors().size() >= dummyIcmCount ) {

                    currentMf = info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId )
                            .getInputCompleteMaterialFactors().get( dummyIcmCount );
                }

                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Material Characteristics".
                //                buffer.append( "Match Found<br/>" );
                String displayName = genericMaterial.getName()
                        .substring( 0, genericMaterial.getName().indexOf( "net.sourceforge.symba.keywords.dummy" ) )
                        .trim();

                buffer.append( "<fieldset>" );
                buffer.append( "<legend>" ).append( displayName ).append( "</legend>" );
                buffer.append( "<ol>" );
                buffer.append( System.getProperty( "line.separator" ) );

                buffer.append(
                        createSingleMaterialForm( currentMf, null, personBean.getSymbaEntityService(), genericMaterial,
                                dummyIcmCount, gpaParentEndurantId, currentDataFile ) );

                // don't include treatment in the above method, as it isn't present when other single materials
                // are made for material transformations.
                if ( !genericMaterial.getName().contains( " Notreatment" ) ) {
                    TreatmentScheme treatmentScheme = new TreatmentScheme();
                    treatmentScheme.setBasic( gpaParentEndurantId, dummyIcmCount, currentDataFile );
                    buffer.append(
                            parseTreatment( session, symbaFormSessionBean, currentMf, displayName, treatmentScheme ) );
                }
                buffer.append( "</ol>" );
                buffer.append( "</fieldset>" );
                buffer.append( System.getProperty( "line.separator" ) );

                // only increment the count if we have found a dummy material, as those are the only ones we're printing
                dummyIcmCount++;
            }
        }

        return buffer;
    }

    /**
     * Use the provided information to create a form for creating a new material transformation GPA, which
     * is seen by the user as a single new specimen.
     *
     * @param personBean     provides access to the database
     * @param specimenMfs    provides information on user's session information for this specimen
     * @param gpaInformation the GPA identifier that the new specimen is to be based on, plus a string containing
     *                       the complete hierarchy of GenericActions chosen by the user as the position in which
     *                       to put the specimen.
     * @param isDummy        whether or not the provided gpaIdentifier points to a dummy GPA
     * @return the form, ready to print in the jsp
     */
    public static StringBuffer createSpecimenFormContents( PersonBean personBean,
                                                           MaterialFactorsStore specimenMfs,
                                                           String gpaInformation,
                                                           boolean isDummy ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(
                "<input type=\"hidden\" id=\"hiddennewterminfofield\" name=\"hiddennewterminfofield\" value=\"\">" );

        ActionHierarchyScheme ahs = new ActionHierarchyScheme();

        // we must provide the information about the gpa back to the form when we return to it
        if ( isDummy ) {
            ahs.setDummy( true );
        }
        String hiddenLabel = ahs.getElementTitle();

        // Store the gpa and the chosen hierarchy as a hidden input value, as it will be used when creating the
        // new GPA
        buffer.append(
                "<input type=\"hidden\" id=\"" ).append( hiddenLabel ).append( "\" name=\"" );
        buffer.append( hiddenLabel ).append( "\" value=\"" );
        buffer.append( gpaInformation );
        buffer.append( "\">" );

        ahs.parse( gpaInformation );

        // as it's the form, we can assume that we've got the most up-to-date identifier.
        GenericProtocolApplication gpa =
                ( GenericProtocolApplication ) personBean.getEntityService().getIdentifiable( ahs.getGpaIdentifier() );

        buffer.append( "<ol>" );

        // create a pull-down for each output material (there is normally only one, which contains the
        // specimen descriptor parse plus experimental conditions). This is how we get characteristics for both
        // input and output: it is assumed that the output is a superset of the input.
        // When loading information from raw data forms, the normal values for these three are:
        // materialCounter(Number)::gpaParentEndurantId(unused)::currentDataFile(Number)
        // When loading a new specimen, the important thing to know is the gpa identifier.
        // the values are unused when adding new ontology individuals for our form, but required for pulldowns
        for ( Material material : gpa.getOutputMaterials() ) {
            if ( material instanceof GenericMaterial ) {
                if ( isDummy ) {
                    // we don't want to put anything in the form fields as default values, unless its in
                    // the session already.
                    buffer.append(
                            createSingleMaterialForm( specimenMfs, null,
                                    personBean.getSymbaEntityService(), ( GenericMaterial ) material, 0,
                                    gpa.getEndurant().getIdentifier(), 0 ) );
                } else {
                    buffer.append(
                            createSingleMaterialForm( specimenMfs, ( GenericMaterial ) material,
                                    personBean.getSymbaEntityService(), ( GenericMaterial ) material, 0,
                                    gpa.getEndurant().getIdentifier(), 0 ) );
                }
            }
        }

        buffer.append( "</ol>" );
        return buffer;
    }

//    public static StringBuffer parseDummyBaseMaterialsChoice( SymbaFormSessionBean symbaFormSessionBean,
//                                                              SymbaEntityService symbaEntityService,
//                                                              String topLevelProtocolName ) {
//
//        HashMap<String, Material> dummyBaseMaterials =
//                getDummyBaseMaterials( symbaEntityService, topLevelProtocolName );
//        StringBuffer buffer = new StringBuffer();
//
//        buffer.append( "<ol>" );
//        boolean noChoice = false;
//        if ( dummyBaseMaterials.size() == 1 ) {
//            noChoice = true;
//        }
//
//        // Now print out a hierarchy of action items. Place each material wherever its
//        // GPA's Protocol is present as an Action.
//        buffer.append( ActionTemplateParser.parse( dummyBaseMaterials, symbaEntityService, symbaFormSessionBean,
//                ActionTemplateParser.PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) );
//        for ( String key : dummyBaseMaterials.keySet() ) {
//            Material dummyBaseMaterial = dummyBaseMaterials.get( key );
//            buffer.append( "<li>" );
//            buffer.append( "<input type=\"radio\" name=\"gpaDummyIdentifier\" value=\"" )
//                    .append( key );
//            if ( noChoice ) {
//                buffer.append( "\" checked=\"checked\">" );
//            } else {
//                buffer.append( "\">" );
//            }
//            buffer.append( printDummyBaseMaterialSummary( dummyBaseMaterial ) );
//            buffer.append( "</li>" );
//        }
//        buffer.append( "</ol>" );
//
//        return buffer;
//    }

    public static StringBuffer printDummyBaseMaterialSummary( Material dummy ) {

        StringBuffer buffer = new StringBuffer();

        // must have the dummy keyword.
        buffer.append( dummy.getName().substring( 0,
                dummy.getName().indexOf( "net.sourceforge.symba.keywords.dummy" ) ).trim() );
        buffer.append( ", consisting of: " );
        // in dummy base materials, there should be only one characteristic, which points to the whole descriptor parse.
        String characteristics = "";
        for ( OntologyTerm ontologyTerm : dummy.getCharacteristics() ) {
            buffer.append( parseOntologyTerm( ontologyTerm ) );
        }
        buffer.append( characteristics );
        return buffer;
    }

    /**
     * @param symbaEntityService   the service to connect to the database
     * @param topLevelProtocolName the name of the top-level protocol for the investigation
     * @return the list of dummy Materials that are inputs to material transformations
     */
    public static HashMap<String, Material> getDummyBaseMaterials( SymbaEntityService symbaEntityService,
                                                                   String topLevelProtocolName ) {

        HashMap<String, Material> materials = new HashMap<String, Material>();

        // material transformations can only be retrieved via the dummy GPAs, as it is only there where the
        // fact that it is a material transformation is stored. So we'll take these and use them later to
        // identify those protocol types we've been asked to print.
        // Dummy material transformations will have exactly one action application.
        for ( Object gpaObj : symbaEntityService.getLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) gpaObj;
            if ( genericProtocolApplication.getName().trim().contains( topLevelProtocolName ) ) {
                if ( genericProtocolApplication.getName().trim()
                        .contains( "net.sourceforge.symba.keywords.materialTransformation" ) ) {
                    materials
                            .put( genericProtocolApplication.getIdentifier() + "::" +
                                  genericProtocolApplication.getProtocol().getIdentifier(),
                                    genericProtocolApplication.getOutputMaterials().iterator().next() );
                }
            }
        }
        return materials;
    }

//    public static StringBuffer parseMaterialTransformations( List<GenericProtocolApplication> materialTransformations ) {
//
//        StringBuffer buffer = new StringBuffer();
//
//        buffer.append( "<ol>" );
//        for ( GenericProtocolApplication mt : materialTransformations ) {
//            buffer.append( "<li>" );
//            buffer.append( "<a href=addSpecimens.jsp?gpaIdentifier=" ).append( mt.getIdentifier() ).append( ">" );
//            buffer.append( printMaterialPairSummary( mt ) );
//            buffer.append( "</li>" );
//        }
//        buffer.append( "</ol>" );
//
//        return buffer;
//    }

    public static StringBuffer printMaterialPairSummary( GenericMaterial outputMaterial ) {

        StringBuffer buffer = new StringBuffer();

        // there is only one output material. show its name as the name of the paired material
        buffer.append( outputMaterial.getName() );
        buffer.append( ", consisting of: " );
        // there will be multiple characteristics. One points to the whole descriptor parse. The others
        // are each one experimental condition. 
        for ( OntologyTerm ontologyTerm : outputMaterial.getCharacteristics() ) {
            buffer.append( parseOntologyTerm( ontologyTerm ) );
        }

        return buffer;
    }

    private static StringBuffer parseOntologyTerm( OntologyTerm ontologyTerm ) {

        StringBuffer buffer = new StringBuffer();

        String names = "";
        if ( ontologyTerm instanceof OntologyIndividual ) {
            for ( OntologyProperty inner : ( ( OntologyIndividual ) ontologyTerm ).getProperties() ) {
                // not interested in data properties for the summary.
                if ( inner instanceof ObjectProperty ) {
                    String cleaned = inner.getTerm();
                    if ( cleaned.contains( "_" ) ) {
                        cleaned = cleaned.replace( "_", " " );
                    }
                    if ( cleaned.startsWith( "has " ) ) {
                        cleaned = cleaned.substring( 4 );
                    }
                    names += cleaned + ", ";
                }
            }
            // if there was nothing in the names, then it is a simple ontologyindividual. Print the term name.
            if ( names.length() == 0 ) {
                buffer.append( ontologyTerm.getTerm() );
            } else {
                if ( names.endsWith( ", " ) ) {
                    names = names.substring( 0, names.length() - 2 );
                }
                buffer.append( names );
            }
            buffer.append( "; " );
        }
        return buffer;
    }

    private static StringBuffer parseCharacteristics( SymbaEntityService symbaEntityService,
                                                      MaterialFactorsStore mfs,
                                                      GenericMaterial useAsMfs, GenericMaterial genericMaterial,
                                                      CharacteristicScheme chs ) {

        StringBuffer buffer = new StringBuffer();

        int ontoCount = 0;
        for ( OntologyTerm currentCharacteristic : genericMaterial.getCharacteristics() ) {
            // Only create the pull-down menu for the current characteristic if it doesn't have
            // any object or data properties. If it does, then you should print out pull-down menus for
            // those properties rather than for the top-level OI.
            if ( currentCharacteristic instanceof OntologyIndividual ) {
                OntologyIndividual oi = ( OntologyIndividual ) currentCharacteristic;
                if ( ( ( OntologyIndividual ) currentCharacteristic ).getProperties() == null ||
                     ( ( OntologyIndividual ) currentCharacteristic ).getProperties().isEmpty() ) {
                    chs.setNovel( false );
                    buffer.append( createOntologyIndividualPullDown( ( OntologyIndividual ) currentCharacteristic,
                            ontoCount, symbaEntityService, mfs, useAsMfs, chs ) );
                } else {
                    for ( OntologyProperty ontologyProperty : oi.getProperties() ) {
                        if ( ontologyProperty instanceof ObjectProperty ) {
                            // print out a pull-down for each ontology individual present within each object property
                            for ( OntologyIndividual ontologyIndividual : ( ( ObjectProperty ) ontologyProperty )
                                    .getContent() ) {
                                // need to pass the name of the object property and its parent OI to
                                // base the new term descriptor on.
                                chs.setNovel( true );
                                chs.setDescriptorOiEndurant( oi.getEndurant().getIdentifier() );
                                buffer.append( createOntologyIndividualPullDown( ontologyIndividual,
                                        ontoCount, symbaEntityService, mfs, useAsMfs, chs ) );
                            }

                        }
                    }
                }
            }
            ontoCount++;
        }

        return buffer;
    }

    private static StringBuffer createOntologyIndividualPullDown( OntologyIndividual ontologyIndividual,
                                                                  int ontoCount,
                                                                  SymbaEntityService symbaEntityService,
                                                                  MaterialFactorsStore mfs,
                                                                  GenericMaterial useAsMfs,
                                                                  CharacteristicScheme chs ) {

        StringBuffer buffer = new StringBuffer();

        OntologySource ontologySource = ontologyIndividual.getOntologySource();
        if ( ontologySource != null ) {
            ontologySource = ( OntologySource ) symbaEntityService
                    .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

            chs.setSourceEndurant( ontologySource.getEndurant().getIdentifier() );

            List<OntologyTerm> ontologyTerms = new ArrayList<OntologyTerm>();
            List<List<String>> termAndAccessionPairs = new ArrayList<List<String>>();
            int listSize;
            if ( !chs.isNovel() ) {
                ontologyTerms = symbaEntityService
                        .getLatestTermsWithSource( chs.getSourceEndurant() );
                listSize = ontologyTerms.size();
            } else {
                termAndAccessionPairs =
                        symbaEntityService.getDistinctTermInfo( chs.getSourceEndurant() );
                listSize = termAndAccessionPairs.size();
            }

            boolean multipleAllowed = false;

            // now we check and note if additional field necessary (i.e. term contains "Dummy") and if dropdown or not:
            OntologyTerm oterm = ( OntologyTerm ) symbaEntityService
                    .getLatestByEndurant( ontologyIndividual.getEndurant().getIdentifier() );
            boolean newTermButtonWanted;
            if ( !chs.isNovel() ) {
                newTermButtonWanted = oterm.getName().contains( "net.sourceforge.symba.keywords.dummy" );
            } else {
                // the dummy may not be in the current individual, but instead somewhere inside
                // the list of term and accession pairs.
                newTermButtonWanted = false;
                for ( List<String> currentList : termAndAccessionPairs ) {
                    if ( currentList.contains( "net.sourceforge.symba.keywords.dummy" ) ) {
                        newTermButtonWanted = true;
                    }
                }
            }

            // each ontology source for a characteristic may have a description which tells the user
            // how to select the correct ontology term. Check for that.
            for ( Object descObj : ontologySource.getDescriptions() ) {
                Description desc = ( Description ) descObj;
                if ( desc.getText().startsWith( "Instructions: " ) ) {
                    buffer.append( "<p class=\"bigger\">" ).append( desc.getText().substring( 14 ) )
                            .append( "</p>" );
                    // check if multiple selections are allowed by searching for the phrase
                    // "please choose all that apply."
                    if ( desc.getText().endsWith( "please choose all that apply." ) ) {
                        multipleAllowed = true;
                    }
                }
            }

            buffer.append( System.getProperty( "line.separator" ) );

            String characteristicLabel = chs.write();
            if ( multipleAllowed ) {
                characteristicLabel = chs.writeMultiple();
            }
            buffer.append( "<li>" );
            boolean characteristicLabelPrinted = false;

            if ( !newTermButtonWanted || ( listSize > 1 && newTermButtonWanted ) ) {
                characteristicLabelPrinted = true;
                buffer.append( "<label for=\"" ).append( characteristicLabel )
                        .append( "\">Please select your <b>" ).append( ontologySource.getName() )
                        .append( "</b></label>" );
                buffer.append( "<table><tr><td>" );
                if ( multipleAllowed ) {
                    buffer.append( "<select multiple name=\"" ).append( characteristicLabel )
                            .append( "\">" );
                } else {
                    buffer.append( "<select name=\"" ).append( characteristicLabel ).append( "\">" );
                    buffer.append(
                            " <option value=\"\">" );// for single selections now always an empty option on pos 0
                }


                buffer.append( System.getProperty( "line.separator" ) );
                if ( !chs.isNovel() ) {
                    for ( OntologyTerm ot : ontologyTerms ) {
                        if ( !ot.getName().contains( "net.sourceforge.symba.keywords.dummy" ) ) {
                            buffer.append( printSingleOption( mfs, useAsMfs,
                                    chs.getSourceEndurant(),
                                    ot.getEndurant().getIdentifier(), ot.getTerm() ) );
                        }
                    }
                } else {
                    for ( List<String> pair : termAndAccessionPairs ) {
                        if ( !pair.get( 0 ).contains( "net.sourceforge.symba.keywords.dummy" ) ) {
                            buffer.append( printSingleOption( mfs, useAsMfs,
                                    chs.getSourceEndurant(),
                                    pair.get( 0 ) + "::" + pair.get( 1 ), pair.get( 0 ) ) );
                        }
                    }
                }
                buffer.append( "</select>" );
                buffer.append( "</td>" );
            }

            buffer.append( System.getProperty( "line.separator" ) );

            // Create button for inputfield to allow extension of ontology:
            if ( newTermButtonWanted ) {
                String buttonName = "addNewCharacteristic";
                if ( !characteristicLabelPrinted ) {
                    buffer.append( "<label for=\"" ).append( buttonName );
                    buffer.append( "\">Please name your <b>" ).append( ontologySource.getName() )
                            .append( "</b> by creating a new term with the button provided" );
                    buffer.append( "</label>" );
                    buffer.append( "<table><tr><td>" );
                }
                buffer.append( "<td>" );
                String v1 = "ontologyTextfield::" + chs.write() + "::" +
                            chs.getSourceEndurant() + "::" + ontoCount;
                buffer.append( "<input type=\"button\" name=\"addNewCharacteristic\" value=\"New Term" )
                        .append( "\" onclick=\"doNewTermEntered('" )
                        .append( v1 ).append( "','" )
                        .append( characteristicLabel ).append( "')\">" );
                buffer.append( System.getProperty( "line.separator" ) );
                buffer.append( "</td>" );
                buffer.append( "</tr></table>" );
            }
            buffer.append( "</li>" );
        }

        return buffer;
    }

    private static StringBuffer printSingleOption( MaterialFactorsStore mfs,
                                                   GenericMaterial useAsMfs,
                                                   String sourceEndurant,
                                                   String endurantOrTermPair,
                                                   String term ) {

        // The provided endurantOrTermPair may be present in any one of the characteristics sets.
        // Go through all until the first match is found. If found, select it. Then finish printing the option.

        boolean matchFound = false;

        StringBuffer buffer = new StringBuffer();

        String inputStartValue = "<option value=\"" +
                                 endurantOrTermPair + "\"";

        if ( mfs != null ) {

            // determine if there is a multiple characteristics set for this ontology source
            LinkedHashSet<String> values = mfs.getMultipleCharacteristics().get( sourceEndurant );
            if ( values != null && values.contains( endurantOrTermPair ) ) {
                inputStartValue += " selected=\"selected\"";
                matchFound = true;
            }

            if ( !matchFound ) {
                // check for presence within the characteristics set
                String value = mfs.getCharacteristics().get( sourceEndurant );
                if ( value != null && value.equals( endurantOrTermPair ) ) {
                    inputStartValue += " selected=\"selected\"";
                    matchFound = true;
                }
            }
            if ( !matchFound ) {
                // determine if there is a multiple characteristics set for this ontology source
                values = mfs.getNovelMultipleCharacteristics().get( sourceEndurant );
                if ( values != null && values.contains( endurantOrTermPair ) ) {
                    inputStartValue += " selected=\"selected\"";
                    matchFound = true;
                }
            }

            if ( !matchFound ) {
                // check for presence within the characteristics set
                String value = mfs.getNovelCharacteristics().get( sourceEndurant );
                if ( value != null && value.equals( endurantOrTermPair ) ) {
                    inputStartValue += " selected=\"selected\"";
                    matchFound = true;
                }
            }
        }

        if ( !matchFound ) {
            if ( useAsMfs != null && useAsMfs.getCharacteristics() != null &&
                 !useAsMfs.getCharacteristics().isEmpty() ) {

                for ( OntologyTerm useAsMfsOT : useAsMfs.getCharacteristics() ) {
                    if ( useAsMfsOT instanceof OntologyIndividual ) {
                        OntologyIndividual oi = ( OntologyIndividual ) useAsMfsOT;
                        if ( oi.getProperties().isEmpty() ) {
                            if ( useAsMfsOT.getEndurant().getIdentifier()
                                    .equals( endurantOrTermPair ) ) {
                                inputStartValue += " selected=\"selected\"";
                                // the value from this matchFound will currently not be used, as there are no more
                                // checks after this. However, in future there may be so this remains.
                                matchFound = true;
                                break;
                            }
                        } else {
                            // look within the OIs that live inside each ObjectProperty
                            String[] parsed = endurantOrTermPair.split( "::" );
                            for ( OntologyProperty ontologyProperty : oi.getProperties() ) {
                                if ( ontologyProperty instanceof ObjectProperty &&
                                     OntologyLoader.objectPropertyhasIndividual( ( ObjectProperty ) ontologyProperty,
                                             parsed[0], parsed[1] ) ) {
                                    inputStartValue += " selected=\"selected\"";
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (matchFound) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        buffer.append( inputStartValue ).append( ">" ).append( term )
                .append( "</option>" );
        buffer.append( System.getProperty( "line.separator" ) );

        return buffer;
    }

    private static StringBuffer parseMaterialType( SymbaEntityService symbaEntityService,
                                                   MaterialFactorsStore currentMf,
                                                   GenericMaterial useAsMfs, GenericMaterial genericMaterial,
                                                   MaterialTypeScheme mts ) {
        // Retrieve the materialType (singular) and all characteristics of this material.
        // Each references an OntologyTerm, which in turn is associated with an OntologySource.
        // Instead of displaying just the referenced OntologyTerm, a pull-down menu should be displayed
        // of ALL of the OntologyTerms in the database associated with that OntologySource.

        StringBuffer buffer = new StringBuffer();

        // unchecked cast warning provided by javac when using generics in Lists/Sets and
        // casting from Object, even though runtime can handle this.
        // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
        @SuppressWarnings( "unchecked" )
        List<OntologyTerm> ontologyTerms = symbaEntityService.getLatestTermsWithSource(
                genericMaterial.getMaterialType()
                        .getOntologySource()
                        .getEndurant().getIdentifier() );
        if ( !ontologyTerms.isEmpty() ) {

            // the ontology source for the material type may have a description which tells the user
            // how to select the correct ontology term. Check for that.
            OntologySource ontologySource = genericMaterial.getMaterialType().getOntologySource();
            ontologySource = ( OntologySource ) symbaEntityService
                    .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

            // Never allow multiple choices for material types.
            String instructions =
                    "<label for=\"" + mts.write() + "\">Please select your material type:</label>";
            for ( Object descObj : ontologySource.getDescriptions() ) {
                Description desc = ( Description ) descObj;
                if ( desc.getText().startsWith( "Instructions: " ) ) {
                    instructions =
                            "<label for=\"" + mts.write() + "\">" + desc.getText().substring( 14 ) +
                            "</label>";
                }
            }
            buffer.append( System.getProperty( "line.separator" ) );

            buffer.append( "<li>" );
            buffer.append( instructions );
            buffer.append( "<select name=\"" ).append( mts.write() ).append( "\">" );
            buffer.append( "<option value=\"\" ></option>" );//js now always an empty option on pos 0
            for ( OntologyTerm ot : ontologyTerms ) {
                String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                if ( currentMf != null && currentMf.getMaterialType() != null && currentMf.getMaterialType()
                        .equals( ot.getEndurant().getIdentifier() ) ) {
                    inputStartValue += " selected=\"selected\"";
                } else if ( useAsMfs != null && useAsMfs.getMaterialType() != null && useAsMfs.getMaterialType()
                        .getEndurant().getIdentifier().equals( ot.getEndurant().getIdentifier() ) ) {
                    inputStartValue += " selected=\"selected\"";
                }
                buffer.append( inputStartValue ).append( ">" ).append( ot.getTerm() )
                        .append( " </option>" );
            }
            buffer.append( "</select>" );
            buffer.append( "<br/>" );
            buffer.append( "</li>" );
            buffer.append( System.getProperty( "line.separator" ) );
        }

        return buffer;
    }

    private static StringBuffer parseTreatment( HttpSession session,
                                                SymbaFormSessionBean symbaFormSessionBean,
                                                MaterialFactorsStore currentMf, String displayName,
                                                TreatmentScheme treatmentScheme ) {

        StringBuffer buffer = new StringBuffer();
        // will provide a treatment box, if requested
        buffer.append( "<li>" );
        buffer.append( "<p class=\"bigger\">Please enter some information about the " )
                .append( displayName ).append( ", starting " +
                                               "with treatment information. There should be a separate box for each " +
                                               "treatment performed (optional)." );

        if ( currentMf != null &&
             currentMf.getTreatmentInfo() != null &&
             !currentMf.getTreatmentInfo().isEmpty() ) {
            buffer.append(
                    "<em>NOTE: Treatments already entered will remain in the system (see list below).</em>" );
            buffer.append( "<ol>" );
            for ( String singleTreatment : currentMf.getTreatmentInfo() ) {
                buffer.append( "<li>Treatment already recorded: " ).append( singleTreatment )
                        .append( "</li>" );
                buffer.append( System.getProperty( "line.separator" ) );
                // If we are using the template store right now, then we need to copy the
                // treatments from the template store to the session bean, as otherwise (because they
                // don't get stored in the next jsp) they will be wiped.
                if ( session.getAttribute( "templateStore" ) != null ) {
                    symbaFormSessionBean.getDatafileSpecificMetadataStores()
                            .get( treatmentScheme.getDatafileNumber() )
                            .getGenericProtocolApplicationInfo().get( treatmentScheme.getParentOfGpaEndurant() )
                            .getInputCompleteMaterialFactors().get( treatmentScheme.getMaterialCount() )
                            .addTreatmentInfo( singleTreatment );
                }
            }
            buffer.append( "</ol>" );
            buffer.append( System.getProperty( "line.separator" ) );
            buffer.append( "You may add additional treatments by entering them below." );
        }

        buffer.append( "</p>" );

        // Each material may have had more than one treatment. Currently this is NOT stored as a controlled
        // vocabulary, but as free text.
        String moreTreatments = "moreTreatments" + treatmentScheme.getDatafileNumber();
        String treatmentLabel = treatmentScheme.write();
        String treatmentLabelFirst = treatmentLabel + "0";
        buffer.append( "<label for=\"" ).append( treatmentLabelFirst )
                .append( "\">Type, Dose and Length of Treatment: </label>" );
        buffer.append( System.getProperty( "line.separator" ) );
        buffer.append( "<textarea id=\"" ).append( treatmentLabelFirst ).append( "\" name=\"" )
                .append( treatmentLabelFirst ).append( "\" rows=\"5\" cols=\"40\"></textarea> " );
        buffer.append( "<br/>" );
        buffer.append( System.getProperty( "line.separator" ) );

        buffer.append( "<div id=\"" ).append( moreTreatments ).append( "\"></div>" );
        buffer.append( "<br/> " );
        buffer.append( System.getProperty( "line.separator" ) );

        buffer.append( "<div id=\"moreTreatmentsLink\">" );
        buffer.append( "<a href=\"javascript:addTreatmentInput('" ).append( treatmentLabel )
                .append( "', '" ).append( moreTreatments ).append( "');\">Add Another Treatment</a>" );
        buffer.append( "</div>" );
        buffer.append( System.getProperty( "line.separator" ) );
        buffer.append( "<br/>" );
        buffer.append( "</li>" );
        buffer.append( System.getProperty( "line.separator" ) );

        return buffer;
    }

    private static StringBuffer parseOntologyReplacements( MaterialFactorsStore mfs,
                                                           GenericMaterial useAsMfs, GenericMaterial genericMaterial,
                                                           OntologyReplacementScheme scheme ) {

        StringBuffer buffer = new StringBuffer();

        // Print out any descriptions that are OntologyReplacements as a normal text field.
        for ( Object descriptionObj : genericMaterial.getDescriptions() ) {
            Description description = ( Description ) descriptionObj;
            if ( description.getText() != null && description.getText().length() > 0 &&
                 description.getText().startsWith( scheme.getElementTitle() ) ) {
                // this is a field that does not have enough information yet to be promoted to
                // a material characteristic, so it is currently a free-text field.
                String[] parsedStrings = description.getText().split( "::" );
                scheme.setSpecial( parsedStrings[1] );
                buffer.append( "<li>" );

                if ( parsedStrings.length == 4 && parsedStrings[2].equals( "Help" ) ) {
                    buffer.append( "<label for=\"" ).append( scheme.write() )
                            .append( "\">Enter the <SPAN title=\" " ).append( parsedStrings[3] )
                            .append( " \" class=\"symba.simplepopup\">" ).append( parsedStrings[1] )
                            .append( "</SPAN>: </label>" );
                } else {
                    String tmpOutputVariable =
                            "<label for=\"" + scheme.write() + "\">Enter the " + parsedStrings[1] +
                            ": </label>";
                    buffer.append( tmpOutputVariable );
                }
                String inputStartValue =
                        "<input type=\"text\" id=\"" + scheme.write() + "\" name=\"" + scheme.write() +
                        "\"";
                if ( mfs != null ) {
                    if ( mfs.getOntologyReplacements().get( parsedStrings[1] ) != null ) {
                        inputStartValue = inputStartValue + " value=\"" + mfs
                                .getOntologyReplacements().get( parsedStrings[1] ) + "\"";
                    }
                } else if ( useAsMfs != null ) {
                    String comparison = parsedStrings[0] + "::" + parsedStrings[1] + "::";
                    for ( Object useAsMfsDescObj : useAsMfs.getDescriptions() ) {
                        Description useAsMfsDesc = ( Description ) useAsMfsDescObj;
                        if ( useAsMfsDesc.getText() != null && useAsMfsDesc.getText().length() > 0 &&
                             useAsMfsDesc.getText().startsWith( comparison ) ) {
                            inputStartValue = inputStartValue + " value=\"" +
                                              useAsMfsDesc.getText().substring( comparison.length() );
                            break;
                        }
                    }

                }
                buffer.append( inputStartValue ).append( "><br/>" );
                buffer.append( "</li>" );
                buffer.append( System.getProperty( "line.separator" ) );
            }

        }

        return buffer;
    }

    /**
     * @param mfs                used to fill in default values as a first choice. If null, will try to find defaults in useAsMfs
     * @param useAsMfs           Will be used to fill in default values if mfs is null. May be the same object as genericMaterial
     * @param symbaEntityService connection to the database
     * @param genericMaterial    the material that provides structure to the form.  May be the same object as useAsMfs
     * @param materialCount      the current material we're in
     * @param parentGpaEndurant  (usually) the parent protocol endurant for the gpa
     * @param currentDatafile    the number of the current data file
     * @return the portion of the form, in html.
     */
    private static StringBuffer createSingleMaterialForm( MaterialFactorsStore mfs,
                                                          GenericMaterial useAsMfs,
                                                          SymbaEntityService symbaEntityService,
                                                          GenericMaterial genericMaterial,
                                                          int materialCount,
                                                          String parentGpaEndurant,
                                                          int currentDatafile ) {
        StringBuffer buffer = new StringBuffer();

        boolean requestName = false;

        if ( !genericMaterial.getName().contains( " Noname" ) ) {
            requestName = true;
        }

        OntologyReplacementScheme ors = new OntologyReplacementScheme();
        ors.setBasic( parentGpaEndurant, materialCount, currentDatafile );
        buffer.append( parseOntologyReplacements( mfs, useAsMfs, genericMaterial, ors ) );

        MaterialNameScheme mns = new MaterialNameScheme();
        mns.setBasic( parentGpaEndurant, materialCount, currentDatafile );
        // Will show the user-inputted material name if already present
        if ( requestName ) {
            buffer.append( "<li>" );
            buffer.append( "<label for=\"" ).append( mns.write() )
                    .append( "\">Name/ID of this Material: </label>" );
            if ( mfs != null && mfs.getMaterialName() != null ) {
                buffer.append( "<input id=\"" ).append( mns.write() ).append( "\" name=\"" ).append( mns.write() )
                        .append( "\" value=\"" ).append( mfs.getMaterialName() )
                        .append( "\"><br/>" );
            } else if ( useAsMfs != null && useAsMfs.getName() != null ) {
                buffer.append( "<input id=\"" ).append( mns.write() ).append( "\" name=\"" ).append( mns.write() )
                        .append( "\" value=\"" ).append( useAsMfs.getName() )
                        .append( "\"><br/>" );
            } else {
                buffer.append( "<input id=\"" ).append( mns.write() ).append( "\" name=\"" ).append( mns.write() )
                        .append( "\"><br/>" );
            }
            buffer.append( "<br/>" );
            buffer.append( "</li>" );
            buffer.append( System.getProperty( "line.separator" ) );
        }

        if ( genericMaterial.getMaterialType() != null ) {
            MaterialTypeScheme mts = new MaterialTypeScheme();
            mts.setBasic( parentGpaEndurant, materialCount, currentDatafile );
            buffer.append( parseMaterialType( symbaEntityService, mfs, useAsMfs, genericMaterial, mts ) );
        }

        CharacteristicScheme chs = new CharacteristicScheme();
        chs.setBasic( parentGpaEndurant, materialCount, currentDatafile );
        buffer.append( parseCharacteristics( symbaEntityService, mfs, useAsMfs, genericMaterial, chs ) );

        return buffer;
    }
//    buffer.append( "<!--" );
//    buffer.append( "debug text = " ).append( someval );
//    buffer.append( "-->" );
//    buffer.append( System.getProperty( "line.separator" ) );

}
