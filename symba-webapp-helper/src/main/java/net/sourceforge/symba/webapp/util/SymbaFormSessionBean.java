package net.sourceforge.symba.webapp.util;

import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionInformation;
import net.sourceforge.symba.webapp.util.forms.MaterialTemplateParser;
import net.sourceforge.symba.webapp.util.forms.ActionTemplateParser;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Stores all of the information needed for a single user's session that is directly dealing with data and metadata
 * upload.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */

public class SymbaFormSessionBean implements Serializable {

    // the four booleans below are checkpoint markers.
    // Will be parse to true after the first time the user makes it to the confirmation page.
    // (technically, once the validation is complete on the page immediately prior to the confirmation
    // page, so please make sure this is always parse in that position, which is currently inside metaDataValidate.jsp)
    private boolean confirmationReached;

    // will be parse to true after all metadata-defining protocols have been chosen, and the first metadata form has
    // been submitted. This is currently also metaDataValidate.jsp, but it won't always be, so we need two variables
    // for the two semantically different meanings.
    private boolean protocolLocked;

    // will be parse to true after at least one datafile has been successfully loaded
    private boolean dataPresent;

    // will be parse to true if ever information about material characteristics is tried to be loaded from a form
    // without having all of the characteristics parse with *some* value.
    private boolean materialCharacteristicsIncomplete;

    // parse to true if the metadata was copied from a pre-existing experiment. Important to know as, if true, the
    // first time the data files are filled, all the metadata stored in the first data file's metadata needs to be
    // copied to all of the others.
    private boolean metadataFromAnotherExperiment;

    // sets the type of experiment. Set within materialTransformationOrAssay.jsp
    private ActionTemplateParser.PROTOCOL_TYPE protocolType;

    // information that is specific to each uploaded data file, including the data file itself
    private List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores;

    // only used when adding specimens - not used in the part of the form where rawData is processed.
    private MaterialFactorsStore specimenToBeUploaded;
    // only used when adding specimens - not used in the part of the form where rawData is processed.
    // it is the identifier of the gpa plus the entire hierachy chosen by the user to attach the specimen to.
    // Later, when loading the specimen into the database, we need to
    // use this to build up the new GPA in the right position in the hierarchy of the exp.
    private String specimenActionHierarchy;

    // information that is generally applicable across all data files
    private String topLevelProtocolName;
    private String topLevelProtocolEndurant; // the endurant's identifier.
    private String experimentName;
    private String hypothesis;
    private String conclusion;

    // These items are only filled to identify the pre-existing experiment a user is adding to.
    // They are left unfilled when the user is creating a new experiment as well as uploading a new data file.
    private FuGE fuGE;
    private String fugeEndurant;
    private String fugeIdentifier;

    public SymbaFormSessionBean() {
        datafileSpecificMetadataStores = new ArrayList<DatafileSpecificMetadataStore>();
        specimenToBeUploaded = new MaterialFactorsStore();
        confirmationReached = false;
        protocolLocked = false;
        dataPresent = false;
        metadataFromAnotherExperiment = false;
        materialCharacteristicsIncomplete = false;
        protocolType = ActionTemplateParser.PROTOCOL_TYPE.NONE;
    }

    public boolean isConfirmationReached() {
        return confirmationReached;
    }

    public void setConfirmationReached( boolean confirmationReached ) {
        this.confirmationReached = confirmationReached;
    }

    public boolean isProtocolLocked() {
        return protocolLocked;
    }

    public void setProtocolLocked( boolean protocolLocked ) {
        this.protocolLocked = protocolLocked;
    }

    public boolean isDataPresent() {
        return dataPresent;
    }

    public void setDataPresent( boolean dataPresent ) {
        this.dataPresent = dataPresent;
    }

    public boolean isMaterialCharacteristicsIncomplete() {
        return materialCharacteristicsIncomplete;
    }

    public void setMaterialCharacteristicsIncomplete( boolean materialCharacteristicsIncomplete ) {
        this.materialCharacteristicsIncomplete = materialCharacteristicsIncomplete;
    }

    public boolean isMetadataFromAnotherExperiment() {
        return metadataFromAnotherExperiment;
    }

    public void setMetadataFromAnotherExperiment( boolean metadataFromAnotherExperiment ) {
        this.metadataFromAnotherExperiment = metadataFromAnotherExperiment;
    }

    public ActionTemplateParser.PROTOCOL_TYPE getProtocolType() {
        return protocolType;
    }

    public void setProtocolType( ActionTemplateParser.PROTOCOL_TYPE protocolType ) {
        this.protocolType = protocolType;
    }

    public List<DatafileSpecificMetadataStore> getDatafileSpecificMetadataStores() {
        return datafileSpecificMetadataStores;
    }

    public void setDatafileSpecificMetadataStores( List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores ) {
        this.datafileSpecificMetadataStores = datafileSpecificMetadataStores;
    }

    public void addDatafileSpecificMetadataStore( DatafileSpecificMetadataStore info ) {
        this.datafileSpecificMetadataStores.add( info );
    }

    public void setDatafileSpecificMetadataStore( DatafileSpecificMetadataStore info, int value ) {
        this.datafileSpecificMetadataStores.set( value, info );
    }

    public MaterialFactorsStore getSpecimenToBeUploaded() {
        return specimenToBeUploaded;
    }

    public void setSpecimenToBeUploaded( MaterialFactorsStore specimenToBeUploaded ) {
        this.specimenToBeUploaded = specimenToBeUploaded;
    }

    public String getSpecimenActionHierarchy() {
        return specimenActionHierarchy;
    }

    public void setSpecimenActionHierarchy( String specimenActionHierarchy ) {
        this.specimenActionHierarchy = specimenActionHierarchy;
    }

    public String getTopLevelProtocolName() {
        return topLevelProtocolName;
    }

    public void setTopLevelProtocolName( String topLevelProtocolName ) {
        this.topLevelProtocolName = topLevelProtocolName;
    }

    public String getTopLevelProtocolEndurant() {
        return topLevelProtocolEndurant;
    }

    public void setTopLevelProtocolEndurant( String topLevelProtocolEndurant ) {
        this.topLevelProtocolEndurant = topLevelProtocolEndurant;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName( String experimentName ) {
        this.experimentName = experimentName;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis( String hypothesis ) {
        this.hypothesis = hypothesis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion( String conclusion ) {
        this.conclusion = conclusion;
    }

    public FuGE getFuGE() {
        return fuGE;
    }

    public void setFuGE( FuGE fuGE ) {
        this.fuGE = fuGE;
    }

    public String getFugeEndurant() {
        return fugeEndurant;
    }

    public void setFugeEndurant( String fugeEndurant ) {
        this.fugeEndurant = fugeEndurant;
    }

    public String getFugeIdentifier() {
        return fugeIdentifier;
    }

    public void setFugeIdentifier( String fugeIdentifier ) {
        this.fugeIdentifier = fugeIdentifier;
    }

    public void displayHtml( JspWriter out, PersonBean personBean ) throws IOException {

        SymbaEntityService symbaEntityService = personBean.getSymbaEntityService();

        if ( fuGE == null ) {
            // the user has created a new experiment
            out.println( "<p class=\"bigger\">" );
            out.println( "The experiment you are loading has the following details:" );
            out.println( "<ul>" );
            if ( experimentName != null ) {
                out.println(
                        "<li>Name: <a class=\"bigger\" href=\"newExperiment.jsp\">" + experimentName + "</a></li>" );
            }
            if ( hypothesis != null ) {
                out.println(
                        "<li>Hypothesis: <a class=\"bigger\" href=\"newExperiment.jsp\">" + hypothesis + "</a></li>" );
            }
            if ( conclusion != null ) {
                out.println(
                        "<li>Conclusions: <a class=\"bigger\" href=\"newExperiment.jsp\">" + conclusion + "</a></li>" );
            }
            out.println( "</ul>" );
            out.println( "</p>" );
        } else {
            // the user has used an existing experiment.
            out.println( "<p class=\"bigger\">" );
            out.println( "You are adding to the following experiment: " );
            out.println( "<a href=\"experiment.jsp\">" + fuGE.getName() + "</a>" );
            out.println( "</p>" );
        }

        if ( datafileSpecificMetadataStores != null && !datafileSpecificMetadataStores.isEmpty() ) {
            // information specific to each uploaded data file
            out.println( "<p class=\"bigger\">" );
            out.println( "This experiment has the following data file(s) for your" );
            out.println( " <a class=\"bigger\" href=\"rawData.jsp\">" );
            out.println( topLevelProtocolName );
            out.println( "</a>:" );
            out.println( "</p>" );

            for ( DatafileSpecificMetadataStore info : datafileSpecificMetadataStores ) {
                out.println( "<hr/>" );
                out.println( "<p class=\"bigger\">Information for " );
                out.println( " <a class=\"bigger\" href=\"rawData.jsp\">" );
                out.println( info.getFriendlyId() );
                out.println( "</a>" );
                out.println( "</p>" );
                out.println( "<ul>" );
                if ( info.getDataFileDescription() != null && info.getDataFileDescription().length() > 0 ) {
                    out.println( "<li>" );
                    out.println( "You have described this file as follows: " );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    out.println( info.getDataFileDescription() );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }
                if ( info.getNestedActions() != null ) {
                    out.println( "<li>" );
                    out.println( "You have also assigned the data file to a particular set of steps in your " );
                    out.println( " workflow. The steps you have assigned the file to are " );
                    out.println( "<a class=\"bigger\" href=\"ChooseAction.jsp\">" );
                    String toPrint = "";
                    for ( ActionInformation actionInformation : info.getNestedActions().getActionHierarchy() ) {
                        String modified = actionInformation.getActionName();
                        if ( modified.startsWith( "Step Containing the" ) ) {
                            modified = modified.substring( 20 );
                        }
                        toPrint += modified + " -> ";
                    }
                    toPrint = toPrint.substring( 0, toPrint.length() - 4 );
                    out.println( toPrint );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }
                if ( info.getFileFormat() != null ) {
                    out.println( "<li>You have specified a file format for the data file: " );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    OntologyTerm ot = ( OntologyTerm ) symbaEntityService.getLatestByEndurant( info.getFileFormat() );
                    out.println( ot.getTerm() );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }
                if ( info.getGenericProtocolApplicationInfo() != null &&
                     !info.getGenericProtocolApplicationInfo().isEmpty() ) {
                    for ( GenericProtocolApplicationSummary value : info.getGenericProtocolApplicationInfo()
                            .values() ) {
                        for ( String parameterEndurant : value.getParameterAndAtomics().keySet() ) {

                            out.println( "<li>" );
                            out.println(
                                    ( symbaEntityService.getLatestByEndurant( parameterEndurant ) ).getName() +
                                    ": " );
                            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                            out.println( value.getParameterAndAtomics().get( parameterEndurant ) );
                            out.println( "</a>" );
                            out.println( "</li>" );
                        }
                        for ( String descriptionKey : value.getDescriptions().keySet() ) {
                            out.println( "<li>" );
                            out.println( "Description of this stage in the investigation: " );
                            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                            out.println( descriptionKey + " = " + value.getDescriptions().get( descriptionKey ) );
                            out.println( "</a>" );
                            out.println( "</li>" );
                        }
                    }
                    out.println( "</li>" );
                }
                if ( info.getGenericEquipmentInfo() != null && !info.getGenericEquipmentInfo().isEmpty() ) {
                    // print out information on each of the associated equipment items
                    out.println( "<li>" );
                    out.println( "You have provided information about the equipment used with this data file. " );
                    out.println( "The equipment has the following properties: " );
                    out.println( "<ul>" );

                    for ( GenericEquipmentSummary value : info.getGenericEquipmentInfo().values() ) {
                        out.println( "<li>Information for the " + value.getEquipmentName() + ": " );

                        out.println( "<ul>" );

                        out.println( "<li>Free-Text Description: " );
                        out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                        out.println( value.getFreeTextDescription() );
                        out.println( "</a>" );
                        out.println( "</li>" );

                        if ( !value.getParameterAndTerms().isEmpty() ) {
                            out.println(
                                    "<li>Ontology Terms further describing this " + value.getEquipmentName() + ":" );
                            out.println( "<ul>" );
                            for ( String paramValue : value.getParameterAndTerms().values() ) {
                                out.println( "<li><a class=\"bigger\" href=\"metaData.jsp\">" );
                                out.println(
                                        ( ( OntologyTerm ) symbaEntityService
                                                .getLatestByEndurant( paramValue ) ).getTerm() );
                                out.println( "</a>" );
                                out.println( "</li>" );
                            }
                            out.println( "</ul>" );
                            out.println( "</li>" );
                        }
                        if ( !value.getParameterAndAtomics().isEmpty() ) {
                            out.println( "<li>Parameters further describing this " + value.getEquipmentName() + ":" );
                            out.println( "<ul>" );
                            for ( String parameterEndurant : value.getParameterAndAtomics().keySet() ) {

                                out.println( "<li>" );
                                out.println(
                                        ( symbaEntityService.getLatestByEndurant( parameterEndurant ) ).getName() +
                                        ": " );
                                out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                                out.println( value.getParameterAndAtomics().get( parameterEndurant ) );
                                out.println( "</a>" );
                                out.println( "</li>" );
                            }
                            out.println( "</ul>" );
                            out.println( "</li>" );
                        }

                        out.println( "</ul>" );

                        out.println( "</li>" );
                    }
                    out.println( "</ul>" );
                    out.println( "</li>" );
                }
                for ( String currentKey : info.getGenericProtocolApplicationInfo().keySet() ) {
                    displayInputMaterials( personBean,
                            info.getGenericProtocolApplicationInfo().get( currentKey ), out );
                }
            }
            out.println( "</ul>" );
        }
        if ( specimenToBeUploaded != null ) {
            out.println( "</ul>" );
            displayMaterialFactorsStore( symbaEntityService, out, specimenToBeUploaded, true );
            out.println( "</ul>" );
        }
    }

    private void displayMaterialFactorsStore( SymbaEntityService symbaEntityService,
                                              JspWriter out,
                                              MaterialFactorsStore mfs, boolean isMaterialTransformation ) throws
            IOException {

        if ( mfs == null ) {
            return;
        }

        String changeJsp = "metaData.jsp";
        if ( isMaterialTransformation ) {
//            changeJsp = "enterSpecimen.jsp";
            changeJsp = "";
        }

        if ( mfs.getMaterialName().length() > 0 ) {

            out.println( "<li>Name/Identifying Number: " );
            // todo temporary removal of linking for material transformations
            if (!isMaterialTransformation) {
            out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
            }
            out.println( mfs.getMaterialName() );
            if (!isMaterialTransformation) {
            out.println( "</a>" );
            }
            out.println( "</li>" );
        }

        if ( !mfs.getOntologyReplacements().isEmpty() ) {
            out.println( "<li>Free-text descriptions:" );
            for ( String key : mfs.getOntologyReplacements().keySet() ) {
                out.println( "<ul>" );
                out.println( "<li>" );
                if (!isMaterialTransformation) {
                out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
                }
                out.println(
                        key + " = " + mfs.getOntologyReplacements().get( key ) );
                if (!isMaterialTransformation) {
                out.println( "</a>" );
                }
                out.println( "</li>" );
                out.println( "</ul>" );
            }
            out.println( "</li>" );
        }

        if ( mfs.getMaterialType().length() > 0 ) {
            out.println( "<li>Material Type: " );
            if (!isMaterialTransformation) {
            out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
            }
            OntologyTerm ot = ( OntologyTerm ) symbaEntityService.getLatestByEndurant( mfs.getMaterialType() );
            out.println( ot.getTerm() );
            if (!isMaterialTransformation) {
            out.println( "</a>" );
            }
            out.println( "</li>" );
        }

        if ( !mfs.getTreatmentInfo().isEmpty() ) {
            out.println( "<li>Treatments: " );
            out.println( "<ol>" );
            for ( String treatment : mfs.getTreatmentInfo() ) {
                out.println( "<li>" );
                if (!isMaterialTransformation) {
                out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
                }
                out.println( treatment );
                if (!isMaterialTransformation) {
                out.println( "</a>" );
                }
                out.println( "</li>" );
            }
            out.println( "</ol>" );
            out.println( "</li>" );
        }
        if ( atLeastOnePresent( mfs.getCharacteristics(), mfs.getMultipleCharacteristics(),
                mfs.getNovelCharacteristics(), mfs.getNovelMultipleCharacteristics() ) ) {
            out.println( "<li>Characteristics: " );
            out.println( "<ol>" );
        }

        displayCharacteristics( symbaEntityService, out, mfs.getCharacteristics(), mfs.getMultipleCharacteristics(),
                changeJsp, false );
        displayCharacteristics( symbaEntityService, out, mfs.getNovelCharacteristics(),
                mfs.getNovelMultipleCharacteristics(), changeJsp, true );

        if ( atLeastOnePresent( mfs.getCharacteristics(), mfs.getMultipleCharacteristics(),
                mfs.getNovelCharacteristics(), mfs.getNovelMultipleCharacteristics() ) ) {
            out.println( "</ol>" );
            out.println( "</li>" );
        }
    }

    private boolean atLeastOnePresent( Map<String, String> characteristics,
                                       Map<String, LinkedHashSet<String>> multipleCharacteristics,
                                       Map<String, String> novelCharacteristics,
                                       Map<String, LinkedHashSet<String>> novelMultipleCharacteristics ) {
        return ( characteristics != null && !characteristics.isEmpty() ) ||
               ( multipleCharacteristics != null && !multipleCharacteristics.isEmpty() ) ||
               ( novelCharacteristics != null && !novelCharacteristics.isEmpty() ) ||
               ( novelMultipleCharacteristics != null && !novelMultipleCharacteristics.isEmpty() );
    }

    private void displayCharacteristics( SymbaEntityService symbaEntityService,
                                         JspWriter out,
                                         Map<String, String> characteristics,
                                         Map<String, LinkedHashSet<String>> multipleCharacteristics,
                                         String changeJsp, boolean isNovel ) throws
            IOException {

        if ( characteristics != null && !characteristics.isEmpty() ) {
            for ( String mfbKey : characteristics.keySet() ) {
                out.println( "<li>" );
                // todo temporarily disable for material transformations
                if (changeJsp.length() > 0) {
                out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
                }
                if ( isNovel ) {
                    String[] parsed = characteristics.get( mfbKey ).split( "::" );
                    out.println( parsed[0] );
                } else {
                    OntologyTerm ot = ( OntologyTerm ) symbaEntityService
                            .getLatestByEndurant( characteristics.get( mfbKey ) );
                    out.println( ot.getTerm() );
                }
                if (changeJsp.length() > 0) {
                out.println( "</a>" );
                }
                out.println( "</li>" );
            }
        }
        if ( multipleCharacteristics != null && !multipleCharacteristics.isEmpty() ) {
            for ( String mfbKey : multipleCharacteristics.keySet() ) {
                for ( String currentValue : multipleCharacteristics.get( mfbKey ) ) {
                    out.println( "<li>" );
                    if (changeJsp.length() > 0) {
                    out.println( "<a class=\"bigger\" href=\"" + changeJsp + "\">" );
                    }
                    if ( isNovel ) {
                        String[] parsed = currentValue.split( "::" );
                        out.println( parsed[0] );
                    } else {
                        OntologyTerm ot = ( OntologyTerm ) symbaEntityService.getLatestByEndurant( currentValue );
                        out.println( ot.getTerm() );
                    }
                    if (changeJsp.length() > 0) {
                    out.println( "</a>" );
                    }
                    out.println( "</li>" );
                }
            }
        }
    }

    private void displayInputMaterials( PersonBean personBean,
                                        GenericProtocolApplicationSummary gpaSummary,
                                        JspWriter out ) throws IOException {

        if ( gpaSummary.getInputCompleteMaterialFactors().size() > 0 ||
             gpaSummary.getInputMeasuredMaterialFactors().size() > 0 ||
             gpaSummary.getInputIdentifiersFromMaterialTransformations().size() > 0 ) {
            out.println( "<li>" );
            out.println(
                    "You have provided information about the materials used in this step of the workflow. " );
            out.println( "This material has the following properties " );
            out.println( "<ul>" );

            for ( MaterialFactorsStore mfs : gpaSummary.getInputCompleteMaterialFactors() ) {
                displayMaterialFactorsStore( personBean.getSymbaEntityService(), out, mfs, false );
            }

            for ( MaterialFactorsStore mfs : gpaSummary.getInputMeasuredMaterialFactors() ) {
                displayMaterialFactorsStore( personBean.getSymbaEntityService(), out, mfs, false );
            }

            // alternatively, there may be material information inside the InputIdentifiersFromMaterialTransformations
            if ( !gpaSummary.getInputIdentifiersFromMaterialTransformations().isEmpty() ) {
            }
            displayInputsFromMts( personBean.getEntityService(), out,
                    gpaSummary.getInputIdentifiersFromMaterialTransformations() );

            out.println( "</ul>" );
        }
    }

    private void displayInputsFromMts( EntityService entityService,
                                       JspWriter out,
                                       Set<String> inputIdentifiersFromMaterialTransformations ) throws IOException {

        if ( inputIdentifiersFromMaterialTransformations == null ) {
            return;
        }
        for ( String identifier : inputIdentifiersFromMaterialTransformations ) {
            out.println( "<li>" );
            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
            out.println( MaterialTemplateParser
                    .printMaterialPairSummary( ( GenericMaterial ) entityService.getIdentifiable( identifier ), "" ) );
            out.println( "</a>" );
            out.println( "</li>" );
        }
    }
}
