package net.sourceforge.symba.webapp.util;

import fugeOM.Collection.FuGE;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.GenericParameter;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    // Will be set to true after the first time the user makes it to the confirmation page.
    // (technically, once the validation is complete on the page immediately prior to the confirmation
    // page, so please make sure this is always set in that position, which is currently inside metaDataValidate.jsp)
    private boolean confirmationReached;

    // will be set to true after all metadata-defining protocols have been chosen, and the first metadata form has
    // been submitted. This is currently also metaDataValidate.jsp, but it won't always be, so we need two variables
    // for the two semantically different meanings.
    private boolean protocolLocked;

    // will be set to true after at least one datafile has been successfully loaded
    private boolean dataPresent;

    // set to true if the metadata was copied from a pre-existing experiment. Important to know as, if true, the
    // first time the data files are filled, all the metadata stored in the first data file's metadata needs to be
    // copied to all of the others.
    private boolean metadataFromAnotherExperiment;

    // information that is specific to each uploaded data file, including the data file itself
    private List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores;

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
        confirmationReached = false;
        protocolLocked = false;
        dataPresent = false;
        metadataFromAnotherExperiment = false;
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

    public boolean isMetadataFromAnotherExperiment() {
        return metadataFromAnotherExperiment;
    }

    public void setMetadataFromAnotherExperiment( boolean metadataFromAnotherExperiment ) {
        this.metadataFromAnotherExperiment = metadataFromAnotherExperiment;
    }

    public List<DatafileSpecificMetadataStore> getDatafileSpecificMetadataStores() {
        return datafileSpecificMetadataStores;
    }

    public void setDatafileSpecificMetadataStores( List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores ) {
        this.datafileSpecificMetadataStores = datafileSpecificMetadataStores;
    }

    public String getTopLevelProtocolName() {
        return topLevelProtocolName;
    }

    public void setTopLevelProtocolName( String topLevelProtocolName ) {
        this.topLevelProtocolName = topLevelProtocolName;
    }

    public void addDatafileSpecificMetadataStore( DatafileSpecificMetadataStore info ) {
        this.datafileSpecificMetadataStores.add( info );
    }

    public void setDatafileSpecificMetadataStore( DatafileSpecificMetadataStore info, int value ) {
        this.datafileSpecificMetadataStores.set( value, info );
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

    public void displayHtml( JspWriter out,
                             RealizableEntityService reService ) throws IOException, RealizableEntityServiceException {
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
            if ( info.getAssayActionSummary() != null ) {
                out.println( "<li>" );
                out.println( "You have also assigned the data file to a particular step in your " );
                out.println( " workflow. The step you have assigned the file to is " );
                out.println( "<a class=\"bigger\" href=\"ChooseAction.jsp\">" );
                String modified = info.getAssayActionSummary().getChosenActionName();
                if ( modified.startsWith( "Step Containing the" ) ) {
                    modified = modified.substring( 20 );
                }
                out.println( modified );
                if ( info.getOneLevelUpActionSummary() != null &&
                                    info.getOneLevelUpActionSummary().getChosenActionName() != null) {
                    out.println( ", which belongs to the " + info.getOneLevelUpActionSummary().getChosenActionName() );
                }
                out.println( "</a>" );
                out.println( "</li>" );
            }
            if ( info.getFileFormat() != null ) {
                out.println( "<li>You have specified a file format for the data file: " );
                out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant( info.getFileFormat() );
                out.println( ot.getTerm() );
                out.println( "</a>" );
                out.println( "</li>" );
            }
            if ( info.getGenericProtocolApplicationInfo() != null &&
                    !info.getGenericProtocolApplicationInfo().isEmpty() ) {
                for ( GenericProtocolApplicationSummary value : info.getGenericProtocolApplicationInfo().values() ) {
                    for ( String parameterEndurant : value.getParameterAndAtomics().keySet() ) {

                        out.println( "<li>" );
                        out.println(
                                ( ( GenericParameter ) reService.findLatestByEndurant( parameterEndurant ) ).getName() +
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
                        out.println( "<li>Ontology Terms further describing this " + value.getEquipmentName() + ":" );
                        out.println( "<ul>" );
                        for ( String paramValue : value.getParameterAndTerms().values() ) {
                            out.println( "<li><a class=\"bigger\" href=\"metaData.jsp\">" );
                            out.println(
                                    ( ( OntologyTerm ) reService.findLatestByEndurant( paramValue ) ).getTerm() );
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
                                    ( ( GenericParameter ) reService.findLatestByEndurant( parameterEndurant ) ).getName() +
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
            if ( info.getMaterialFactorsStore() != null ) {
                out.println( "<li>" );
                out.println(
                        "You have provided information about the material used in this step of the workflow with this data file. " );
                out.println( "This material has the following properties " );
                out.println( "<ul>" );

                if ( info.getMaterialFactorsStore().getMaterialName() != null &&
                        info.getMaterialFactorsStore().getMaterialName().length() > 0 ) {
                    out.println( "<li>Name/Identifying Number: " );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    out.println( info.getMaterialFactorsStore().getMaterialName() );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }

                if ( info.getMaterialFactorsStore().getOntologyReplacements() != null &&
                        !info.getMaterialFactorsStore().getOntologyReplacements().isEmpty() ) {
                    out.println( "<li>Free-text descriptions:" );
                    for ( String key : info.getMaterialFactorsStore().getOntologyReplacements().keySet() ) {
                        out.println( "<ul>" );
                        out.println( "<li>" );
                        out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                        out.println(
                                key + " = " + info.getMaterialFactorsStore().getOntologyReplacements().get( key ) );
                        out.println( "</a>" );
                        out.println( "</li>" );
                        out.println( "</ul>" );
                    }
                    out.println( "</li>" );
                }

                if ( info.getMaterialFactorsStore().getMaterialType() != null ) {
                    out.println( "<li>Material Type: " );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant( info.getMaterialFactorsStore().getMaterialType() );
                    out.println( ot.getTerm() );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }

                if ( info.getMaterialFactorsStore().getTreatmentInfo() != null &&
                        !info.getMaterialFactorsStore().getTreatmentInfo().isEmpty() ) {
                    out.println( "<li>Treatments: " );
                    out.println( "<ol>" );
                    for ( String treatment : info.getMaterialFactorsStore().getTreatmentInfo() ) {
                        out.println( "<li>" );
                        out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                        out.println( treatment );
                        out.println( "</a>" );
                        out.println( "</li>" );
                    }
                    out.println( "</ol>" );
                    out.println( "</li>" );
                }
                if ( info.getMaterialFactorsStore().getCharacteristics() != null &&
                        !info.getMaterialFactorsStore().getCharacteristics().isEmpty() ) {
                    out.println( "<li>General Characteristics: " );
                    out.println( "<ol>" );
                    for ( String characteristics : info.getMaterialFactorsStore().getCharacteristics() ) {
                        out.println( "<li>" );
                        out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                        OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant( characteristics );
                        out.println( ot.getTerm() );
                        out.println( "</a>" );
                        out.println( "</li>" );
                    }
                    out.println( "</ol>" );
                    out.println( "</li>" );
                }
                out.println( "</ul>" );
            }
            out.println( "</li>" );
            out.println( "</ul>" );
        }
    }
}
