package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.ontology.OntologySource;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.LinkedHashSet;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp-helper/src/main/java/uk/ac/cisban/symba/webapp/util/LoadFuge.java $
 */
public class MaterialTemplateParser {

    public static StringBuffer parse( DatafileSpecificMetadataStore info,
                                      int currentDataFile,
                                      SymbaFormSessionBean symbaFormSessionBean,
                                      PersonBean personBean,
                                      HttpSession session ) {

        StringBuffer buffer = new StringBuffer();

        buffer.append(
                "<input type=\"hidden\" id=\"hiddennewterminfofield\" name=\"hiddennewterminfofield\" value=\"\">" );

        // sometimes, we may get factors from Materials associated with the experiment. Search the materials
        // for dummies named with the addition of "XXX Dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        for ( Object gmObj : personBean.getSymbaEntityService().getLatestGenericMaterials( true ) ) {
            GenericMaterial genericMaterial = ( GenericMaterial ) gmObj;
            boolean requestName = false, requestTreatment = false;
            if ( !genericMaterial.getName().contains( " Noname" ) ) {
                requestName = true;
            }
            if ( !genericMaterial.getName().contains( " Notreatment" ) ) {
                requestTreatment = true;
            }

            // the dummy genericMaterial name should contain info.getAssayActionSummary().getChosenChildProtocolName()
            if ( genericMaterial.getName()
                    .trim()
                    .contains( info.getAssayActionSummary().getChosenChildProtocolName() ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Material Characteristics".
                //                buffer.append( "Match Found<br/>" );
                String displayName = genericMaterial.getName()
                        .substring( 0, genericMaterial.getName().indexOf( " Dummy" ) )
                        .trim();

                buffer.append( "<fieldset>" );
                buffer.append( "<legend>" ).append( displayName ).append( "</legend>" );
                buffer.append( "<ol>" );
                buffer.append( System.getProperty( "line.separator" ) );

                // Print out any descriptions that are OntologyReplacements as a normal text field.
                for ( Object descriptionObj : genericMaterial.getDescriptions() ) {
                    Description description = ( Description ) descriptionObj;
                    if ( description.getText() != null && description.getText().length() > 0 &&
                         description.getText().startsWith( "OntologyReplacement::" ) ) {
                        // this is a field that does not have enough information yet to be promoted to
                        // a material characteristic, so it is currently a free-text field.
                        String[] parsedStrings = description.getText().split( "::" );
                        String descriptionLabel = parsedStrings[0] + "::" + parsedStrings[1] + "::" + currentDataFile;
                        buffer.append( "<li>" );

                        if ( parsedStrings.length == 4 && parsedStrings[2].equals( "Help" ) ) {
                            buffer.append( "<label for=\"" ).append( descriptionLabel )
                                    .append( "\">Enter the <SPAN title=\" " ).append( parsedStrings[3] )
                                    .append( " \" class=\"symba.simplepopup\">" ).append( parsedStrings[1] )
                                    .append( "</SPAN>: </label>" );
                        } else {
                            String tmpOutputVariable =
                                    "<label for=\"" + descriptionLabel + "\">Enter the " + parsedStrings[1] +
                                    ": </label>";
                            buffer.append( tmpOutputVariable );
                        }
                        String inputStartValue =
                                "<input type=\"text\" id=\"" + descriptionLabel + "\" name=\"" + descriptionLabel +
                                "\"";
                        if ( info.getMaterialFactorsStore() != null &&
                             info.getMaterialFactorsStore().getOntologyReplacements().get( parsedStrings[1] ) !=
                             null ) {
                            inputStartValue = inputStartValue + " value=\"" +
                                              info.getMaterialFactorsStore()
                                                      .getOntologyReplacements()
                                                      .get( parsedStrings[1] ) +
                                                                               "\"";
                        }
                        buffer.append( inputStartValue ).append( "><br/>" );
                        buffer.append( "</li>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }

                }

                // Will show the user-inputted material name if already present
                if ( requestName ) {
                    String matName = "materialName" + currentDataFile;
                    buffer.append( "<li>" );
                    buffer.append( "<label for=\"" ).append( matName )
                            .append( "\">Name/ID of this Material (optional): </label>" );
                    if ( info.getMaterialFactorsStore() != null &&
                         info.getMaterialFactorsStore().getMaterialName() != null ) {
                        buffer.append( "<input id=\"" ).append( matName ).append( "\" name=\"" ).append( matName )
                                .append( "\" value=\"" ).append( info.getMaterialFactorsStore().getMaterialName() )
                                .append( "\"><br/>" );
                    } else {
                        buffer.append( "<input id=\"" ).append( matName ).append( "\" name=\"" ).append( matName )
                                .append( "\"><br/>" );
                    }
                    buffer.append( "<br/>" );
                    buffer.append( "</li>" );
                    buffer.append( System.getProperty( "line.separator" ) );
                }

                // will provide a treatment box, if requested
                if ( requestTreatment ) {
                    buffer.append( "<li>" );
                    buffer.append( "<p class=\"bigger\">Please enter some information about the " )
                            .append( displayName ).append( ", starting " +
                                                           "with treatment information. There should be a separate box for each " +
                                                           "treatment performed (optional)." );

                    if ( info.getMaterialFactorsStore() != null &&
                         info.getMaterialFactorsStore().getTreatmentInfo() != null &&
                         !info.getMaterialFactorsStore().getTreatmentInfo().isEmpty() ) {
                        buffer.append(
                                "<em>NOTE: Treatments already entered will remain in the system (see list below).</em>" );
                        buffer.append( "<ol>" );
                        for ( String singleTreatment : info.getMaterialFactorsStore().getTreatmentInfo() ) {
                            buffer.append( "<li>Treatment already recorded: " ).append( singleTreatment )
                                    .append( "</li>" );
                            buffer.append( System.getProperty( "line.separator" ) );
                            // If we are using the template store right now, then we need to copy the
                            // treatments from the template store to the session bean, as otherwise (because they
                            // don't get stored in the next jsp) they will be wiped.
                            if ( session.getAttribute( "templateStore" ) != null ) {
                                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                                        .get( currentDataFile )
                                        .getMaterialFactorsStore()
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
                    String moreTreatments = "moreTreatments" + currentDataFile;
                    String treatmentLabel = "treatment" + currentDataFile + "-";
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
                }

                // Now, retrieve the materialType (singular) and all characteristics of this material.
                // Each references an OntologyTerm, which in turn is associated with an OntologySource.
                // Instead of displaying just the referenced OntologyTerm, a pull-down menu should be displayed
                // of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( genericMaterial.getMaterialType() != null ) {
                    // unchecked cast warning provided by javac when using generics in Lists/Sets and
                    // casting from Object, even though runtime can handle this.
                    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                    @SuppressWarnings( "unchecked" )
                    List<OntologyTerm> ontologyTerms = personBean.getSymbaEntityService().getLatestTermsWithSource(
                            genericMaterial.getMaterialType()
                                    .getOntologySource()
                                    .getEndurant().getIdentifier() );
                    if ( !ontologyTerms.isEmpty() ) {

                        // the ontology source for the material type may have a description which tells the user
                        // how to select the correct ontology term. Check for that.
                        OntologySource ontologySource = genericMaterial.getMaterialType().getOntologySource();
                        ontologySource = ( OntologySource ) personBean.getSymbaEntityService()
                                .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                        // Never allow multiple choices for material types.
                        String matType = "materialType" + currentDataFile;
                        String instructions =
                                "<label for=\"" + matType + "\">Please select your material type:</label>";
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                instructions =
                                        "<label for=\"" + matType + "\">" + desc.getText().substring( 14 ) +
                                        "</label>";
                            }
                        }
                        buffer.append( System.getProperty( "line.separator" ) );

                        buffer.append( "<li>" );
                        buffer.append( instructions );
                        buffer.append( "<select name=\"" ).append( matType ).append( "\">" );
                        buffer.append( "<option value=\"\" ></option>" );//js now always an empty option on pos 0
                        for ( OntologyTerm ot : ontologyTerms ) {
                            String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                            if ( info.getMaterialFactorsStore() != null &&
                                 info.getMaterialFactorsStore().getMaterialType() != null && info
                                    .getMaterialFactorsStore().getMaterialType()
                                    .equals( ot.getEndurant().getIdentifier() ) ) {
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
                }

                int ontoCount = 0;
                boolean newTermButtonWanted; //used for extending the ontology
                for ( OntologyTerm currentCharacteristic : genericMaterial.getCharacteristics() ) {
                    // each ontology source for a characteristic may have a description which tells the user
                    // how to select the correct ontology term. Check for that.
                    OntologySource ontologySource = currentCharacteristic.getOntologySource();
                    if ( ontologySource != null ) {
                        ontologySource = ( OntologySource ) personBean.getSymbaEntityService()
                                .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );
                        // unchecked cast warning provided by javac when using generics in Lists/Sets and
                        // casting from Object, even though runtime can handle this.
                        // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                        @SuppressWarnings( "unchecked" )
                        List<OntologyTerm> ontologyTerms = personBean.getSymbaEntityService()
                                .getLatestTermsWithSource( ontologySource.getEndurant().getIdentifier() );

                        boolean multipleAllowed = false;

                        //js now we check and note if additional field necessary (i.e. term contains "Dummy") and if dropdown or not:
                        OntologyTerm oterm = ( OntologyTerm ) personBean.getSymbaEntityService()
                                .getLatestByEndurant( currentCharacteristic.getEndurant().getIdentifier() );
                        newTermButtonWanted = oterm.getName().contains( " Dummy" );

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

                        if ( !newTermButtonWanted || ( ontologyTerms.size() > 1 && newTermButtonWanted ) ) {
                            String characteristicLabel = "characteristic" + currentDataFile + "-" + ontoCount;
                            buffer.append( "<li>" );
                            buffer.append( "<label for=\"" ).append( characteristicLabel )
                                    .append( "\">Please select your " ).append( ontologySource.getName() )
                                    .append( "</label>" );
                            buffer.append( "<table><tr><td>" );
                            if ( multipleAllowed ) {
                                characteristicLabel = "characteristicMultiple" + currentDataFile + "-" + ontoCount;
                                buffer.append( "<select multiple name=\"" ).append( characteristicLabel )
                                        .append( "\">" );
                            } else {
                                buffer.append( "<select name=\"" ).append( characteristicLabel ).append( "\">" );
                                buffer.append(
                                        " <option value=\"\">" );//js for single selections now allways an empty option on pos 0
                            }


                            buffer.append( System.getProperty( "line.separator" ) );
                            for ( OntologyTerm ot : ontologyTerms ) {
                                if ( !ot.getName().contains( " Dummy" ) ) {
                                    String inputStartValue = "<option value=\"" +
                                                             ontologySource.getEndurant().getIdentifier() + "::" +
                                                             ot.getEndurant().getIdentifier() + "\"";
                                    if ( info.getMaterialFactorsStore() != null &&
                                         info.getMaterialFactorsStore().getCharacteristics() != null &&
                                         info.getMaterialFactorsStore().getMultipleCharacteristics() != null ) {
                                        if ( multipleAllowed &&
                                             info.getMaterialFactorsStore().getMultipleCharacteristics() != null ) {
                                            for ( String mfbKey : info.getMaterialFactorsStore()
                                                    .getMultipleCharacteristics().keySet() ) {
                                                LinkedHashSet<String> allOTs = info.getMaterialFactorsStore()
                                                        .getMultipleCharacteristics().get( mfbKey );
                                                // there could be more than one match, if allowing multiple selects
                                                if ( allOTs.contains( ot.getEndurant().getIdentifier() ) ) {
                                                    inputStartValue += " selected=\"selected\"";
                                                }
                                            }
                                        } else if ( info.getMaterialFactorsStore().getCharacteristics() !=
                                                    null ) { // if single selection
                                            for ( String mfbKey : info.getMaterialFactorsStore()
                                                    .getCharacteristics().keySet() ) {
                                                // there could be more than one match, if allowing multiple selects

                                                if ( info.getMaterialFactorsStore().getCharacteristics()
                                                        .get( mfbKey )
                                                        .equals( ot.getEndurant().getIdentifier() ) ) {
                                                    inputStartValue += " selected=\"selected\"";
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    buffer.append( inputStartValue ).append( ">" ).append( ot.getTerm() )
                                            .append( "</option>" );
                                    buffer.append( System.getProperty( "line.separator" ) );
                                }// end if (OTs.name doesnt contains dummy)
                                else {
                                    //?
                                }

                            }//end for (over ot)
                            buffer.append( "</select>" );
                            buffer.append( "</td>" );                            
                        }//end if (not newtermbutton wanted or (wanted and newTermsize>1))

                        buffer.append( System.getProperty( "line.separator" ) );

                        //JS Create button for inputfield to allow extension of ontology:
                        if ( newTermButtonWanted ) {
                            buffer.append( "<td>" );
                            String characteristicLabel = "characteristic" + currentDataFile + "-" + ontoCount;
                            String v1 = "ontologyTextfield::" + currentDataFile + "::" +
                                        ontologySource.getEndurant().getIdentifier() + "::" +
                                        ontoCount;
                            String v2 = "selectMetadata";
                            buffer.append(
                                    "<input type=\"button\" name=\"addNewCharacteristic\" value=\"New\" onclick=\"doNewTermEntered('" )
                                    .append( v1 ).append( "','" ).append( v2 ).append( "','" )
                                    .append( characteristicLabel ).append( "')\">" );
                            buffer.append( System.getProperty( "line.separator" ) );
                            buffer.append( "</td>" );
                            buffer.append( "</tr></table>" );
                        }
                    }
                    ontoCount++;
                }//end for loop characteristics
                buffer.append( "</ol>" );
                buffer.append( "</fieldset>" );
                buffer.append( System.getProperty( "line.separator" ) );

                // only allow one dummy generic material per experiment, for now only!
                break;
            }
        }

        return buffer;
    }
}
