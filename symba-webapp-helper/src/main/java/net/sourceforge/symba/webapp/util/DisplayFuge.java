package net.sourceforge.symba.webapp.util;

import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.investigation.Investigation;
import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;
import net.sourceforge.symba.webapp.util.forms.ActionTemplateParser;
import net.sourceforge.symba.service.SymbaEntityService;

import java.io.PrintWriter;
import java.util.*;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * This class holds the code necessary to display fuge objects, in a variety of formats.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */

public class DisplayFuge {

    /**
     * This method will take a fuge object and print to printWriter an HTMl version of the FuGE object. It will also
     * fill a list of SymbaFormSessionBean objects, one for each data file in the FuGE object. The calling method
     * should then set the entire Map into the current user session, and provide a method of choosing between them.
     *
     * @param personBean the session bean holding the endurant identifier for the person requesting the metadata.
     * @param fuge       the object to display
     *                   todo param symbaEntityService  the connection to the database to retrieve up-to-date versions
     * @param out        the location to print out the HTML
     * @return the Map of session beans, one per data file in the FuGE object, with the key being the original
     *         ExternalData object's identifier.
     */
    public static Map<String, SymbaFormSessionBean> displayHtml( PersonBean personBean,
                                                                 FuGE fuge,
                                                                 PrintWriter out ) {

        SymbaFormSessionBean sessionBean = new SymbaFormSessionBean();

        // We assume that the top-level fuge object itself is the newest version.
        out.println( "<h3>" );
        out.println( "Experiment Name: " + fuge.getName() );
        sessionBean.setFuGE( fuge );
        sessionBean.setFugeIdentifier( fuge.getIdentifier() );
        sessionBean.setFugeEndurant( fuge.getEndurant().getIdentifier() );
        out.println( "</h3>" );

        // Now print out the initial creator of the FuGE object
        if ( fuge.getProvider().getProvider().getContact().getName() != null &&
             fuge.getProvider().getProvider().getContact().getName().length() > 0 ) {
            String provider;
            provider = fuge.getProvider().getProvider().getContact().getName();

            out.println( "<h4>" );
            out.println( "Provider of the Experiment: " + provider );
            out.println( "</h4>" );
        }

        // todo insert code that will properly check that the user is allowed to add information to this particular experiment.
        // should use the security client, but for now only allow the setting of the FuGE, FugeEndurant, and
        // FugeIdentifier if the person who made the experiment is the current user.
        // find out if the current user is the one that owns the experiment
        boolean userOwnsExperiment = false;
        if ( fuge.getProvider()
                .getProvider()
                .getContact()
                .getEndurant()
                .getIdentifier()
                .equals( personBean.getEndurantLsid() ) ) {
            userOwnsExperiment = true;
        }

        if ( fuge.getInvestigationCollection() != null ) {
            Collection collection = fuge.getInvestigationCollection().getInvestigations();
            if ( !collection.isEmpty() ) {
                for ( Object obj : collection ) {
                    Investigation inv = ( Investigation ) obj;
                    for ( Object obj2 : inv.getDescriptions() ) {
                        Description de = ( Description ) obj2;
                        out.println( "<p>" );
                        out.println( de.getText() );
                        out.println( "</p>" );
                    }
                }
            }
        }

        out.println("<fieldset>");
        out.println( "<legend>Further information on this experiment</legend>" );
        // ProtocolCollection is describable, so will always be the most up-to-date
        Map<String, SymbaFormSessionBean> sessionBeanHashMap = displayHtmlProtocolCollection(
                userOwnsExperiment, sessionBean, fuge.getProtocolCollection(), personBean.getSymbaEntityService(),
                out );
        out.println("</fieldset>");
        out.flush();

        return sessionBeanHashMap;
    }

    /**
     * display just the information surrounding each data file, from the GPA holding the data file outwards.
     *
     * @param userOwnsExperiment  true if the user who owns the exp is the one viewing it
     * @param templateSessionBean the session bean as provided by the website
     * @param protocolCollection  the fuge experiment's protocol collection
     * @param symbaEntityService  the connection to the database
     * @param out                 where to write all html
     * @return a map of all possible session beans for pre-loading metadata to simplify things for users
     */
    private static Map<String, SymbaFormSessionBean> displayHtmlProtocolCollection( final boolean userOwnsExperiment,
                                                                                    SymbaFormSessionBean templateSessionBean,
                                                                                    ProtocolCollection protocolCollection,
                                                                                    SymbaEntityService symbaEntityService,
                                                                                    PrintWriter out ) {

        Map<String, SymbaFormSessionBean> allBeans = new HashMap<String, SymbaFormSessionBean>();

        Protocol topLevelProtocol = null;
        for ( Protocol protocol : protocolCollection.getProtocols() ) {
            if ( !protocol.getName().contains( "Component" ) ) {
                topLevelProtocol = protocol;
                break;
            }
        }

        if ( protocolCollection.getProtocols() == null || topLevelProtocol == null ) {
            out.println(
                    "<li>Error: this experiment does not have all appropriate protocols. Please contact helpdesk@cisban.ac.uk</li>" );
            return allBeans;
        }

        // the hierarchical list of actions and protocols
        LinkedHashSet<ActionHierarchyScheme> hierarchy =
                ActionTemplateParser
                        .createFullHierarchy( symbaEntityService, new LinkedHashSet<ActionHierarchyScheme>(),
                                new ActionHierarchyScheme(), ( GenericProtocol ) topLevelProtocol );

        // the list of materials in a way appropriate for parsing by the hierarchy display code
        HashMap<String, Material> ownedMaterials = new HashMap<String, Material>();
        for ( ProtocolApplication associatedPA : protocolCollection.getProtocolApplications() ) {
            if ( associatedPA instanceof GenericProtocolApplication ) {
                GenericProtocolApplication gpa = ( GenericProtocolApplication ) associatedPA;
                if ( gpa.getOutputMaterials() != null && !gpa.getOutputMaterials().isEmpty() ) {
                    ownedMaterials.put( gpa.getIdentifier() + "::" + gpa.getProtocol().getIdentifier(),
                            gpa.getOutputMaterials().iterator().next() );
                }
            }
        }

        // the list of data outputs in a way appropriate for parsing by the hierarchy display code
        HashMap<String, Set<Data>> ownedDataItems = new HashMap<String, Set<Data>>();
        for ( ProtocolApplication associatedPA : protocolCollection.getProtocolApplications() ) {
            if ( associatedPA instanceof GenericProtocolApplication ) {
                GenericProtocolApplication gpa = ( GenericProtocolApplication ) associatedPA;
                if ( gpa.getOutputData() != null && !gpa.getOutputData().isEmpty() ) {
                    ownedDataItems.put( gpa.getIdentifier() + "::" + gpa.getProtocol().getIdentifier(),
                            ( Set<Data> ) gpa.getOutputData() );
                }
            }
        }

        int dataNumber = 0;
        out.println(
                ActionTemplateParser.parseHierarchy( templateSessionBean, hierarchy, ownedMaterials, ownedDataItems,
                        false, null ) );
//        for ( Object obj : assayGPAs ) {
//            if ( obj instanceof GenericProtocolApplication ) {
//                GenericProtocolApplication dataFilledGPA = ( GenericProtocolApplication ) obj;
//                // Go through each assay GPA, printing out its protocol history as we go.
//                if ( dataFilledGPA.getOutputData().isEmpty() ) {
//                    out.println(
//                            "<li>There has been an error displaying all of the data files. Please contact helpdesk@cisban.ac.uk</li>" );
//                    return new HashMap<String, SymbaFormSessionBean>();
//                }
//                // Make a new session bean for this particular data file
//                SymbaFormSessionBean currentSessionBean = new SymbaFormSessionBean();
//
//                // parse the values to match those already present in the session bean from the calling method.
//                // these values have already been printed out to the out.
//                // if it isn't the user's experiment, allow them to copy metadata but they must assign it to a new
//                // experiment
//                if ( userOwnsExperiment ) {
//                    currentSessionBean.setExperimentName( templateSessionBean.getExperimentName() );
//                    currentSessionBean.setFuGE( templateSessionBean.getFuGE() );
//                    currentSessionBean.setFugeEndurant( templateSessionBean.getFugeEndurant() );
//                    currentSessionBean.setFugeIdentifier( templateSessionBean.getFugeIdentifier() );
//                }
//
//                // save the various levels of protocol identifiers and names (print later on, where nicer to look at)
//                GenericProtocolApplication gpaAssociatedWithTopLevelProtocol;
//                GenericProtocolApplication oneLevelUpGPA = getLatestParentGPA(
//                        oneAboveAssayGPAs, dataFilledGPA.getIdentifier() );
//
//                // save the child protocol identifier and name (print later on, where nicer to look at)
//                // the protocol one level up from this assay gpa is the current child protocol, and all data files
//                // associated with this assay gpa will have the same values for these.
//                String assayProtocolEndurant = dataFilledGPA.getProtocol().getEndurant().getIdentifier();
//                String assayProtocolName = dataFilledGPA.getProtocol().getName();
//                String oneAboveAssayProtocolEndurant = "";
//                String oneAboveAssayProtocolName = "";
//
//                if ( twoAboveAssayGPAs.isEmpty() ) {
//                    // the protocol referenced by the gpa one level above this assay gpa is the parent protocol.
//                    // retrieve with this method as we have already gotten the latest version
//                    gpaAssociatedWithTopLevelProtocol = oneLevelUpGPA;
//                } else {
//                    // the protocol referenced by the gpa two levels above this assay gpa is the parent protocol.
//                    // retrieve with this method as we have already gotten the latest version.
//                    gpaAssociatedWithTopLevelProtocol = getLatestParentGPA(
//                            twoAboveAssayGPAs, oneLevelUpGPA.getIdentifier() );
//                    oneAboveAssayProtocolEndurant = oneLevelUpGPA.getProtocol().getEndurant().getIdentifier();
//                    oneAboveAssayProtocolName = oneLevelUpGPA.getProtocol().getName();
//                }
//                currentSessionBean.setTopLevelProtocolEndurant(
//                        gpaAssociatedWithTopLevelProtocol.getProtocol().getEndurant().getIdentifier() );
//                currentSessionBean
//                        .setTopLevelProtocolName( gpaAssociatedWithTopLevelProtocol.getProtocol().getName() );
//
//                // herebedragons: currently in symba, there is only every one output data associated with a single GPA
//                // if there are ever more, than what follows is not yet guaranteed to work correctly as it will keep
//                // overwriting the current store object.
//                String externalDataIdentifier = "";
//                List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores =
//                        new ArrayList<DatafileSpecificMetadataStore>();
//                for ( Object outputDataObj : dataFilledGPA.getOutputData() ) {
//                    if ( outputDataObj instanceof ExternalData ) {
//                        dataNumber++;
//
//                        DatafileSpecificMetadataStore store = new DatafileSpecificMetadataStore();
//
//                        // todo put the below into the new ActionHierarchyScheme format
////                        if ( !twoAboveAssayGPAs.isEmpty() ) {
////                            store.getOneLevelUpActionSummary()
////                                    .setChosenChildProtocolEndurant( oneAboveAssayProtocolEndurant );
////                            store.getOneLevelUpActionSummary().setChosenChildProtocolName( oneAboveAssayProtocolName );
////                        }
////                        store.getAssayActionSummary().setChosenChildProtocolEndurant( assayProtocolEndurant );
////                        store.getAssayActionSummary().setChosenChildProtocolName( assayProtocolName );
//
//                        // add the information for this data file to the out and a new SymbaFormSessionBean
//                        ExternalData externalData = ( ExternalData ) outputDataObj;
//                        //store the identifier for later use as the key in the HashMap of SymbaFormSessionBeans
//                        externalDataIdentifier = externalData.getIdentifier();
//                        // externalData.getDataFileDescription is the description of the file.
//                        String fileDescription = "";
//                        for ( Description description : ( Set<Description> ) externalData.getDescriptions() ) {
//                            fileDescription += description.getText();
//                        }
//                        // print the friendly identifier, but don't save it to the session.
//                        out.print( "<li>Data File " + externalData.getName() );
//                        out.println( "<ul>" );
//                        // print the form to allow download of this data file.
//                        String formId = "downloadSingle" + dataNumber;
//                        out.println(
//                                "<br/><form style=\"display:none;\" id=\"" + formId +
//                                "\" action=\"downloadSingleFile.jsp\">" +
//                                "<input type=\"hidden\" name=\"identifier\" value=\"" +
//                                externalData.getLocation() + "\"/>" +
//                                "<input type=\"hidden\" name=\"friendly\" value=\"" +
//                                externalData.getName() + "\"/>" +
//                                "</form>" +
//                                "<li><a href=\"javascript:void(0)\" onclick=\"document.getElementById('" +
//                                formId +
//                                "').submit();\">Download This File</a>" +
//                                "</li>" );
//                        // print the form to allow the upload of a new file with this metadata copied to it.
//                        // All the SymbaFormSessionBean objects will be identified in this form using the
//                        // current external data identifier
//                        formId = "prepare" + dataNumber;
//                        out.println(
//                                "<form style=\"display:none;\" id=\"" + formId +
//                                "\" action=\"prepareCopiedMetadata.jsp\">" +
//                                "<input type=\"hidden\" name=\"identifier\" value=\"" +
//                                externalData.getIdentifier() + "\"/>" +
//                                "</form>" +
//                                "<li><a href=\"javascript:void(0)\" onclick=\"document.getElementById('" +
//                                formId +
//                                "').submit();\">Copy Information from here to a new data file for this Experiment</a>" +
//                                "</li>" );
//
//                        // print and save the file description
//                        if ( fileDescription.length() > 0 ) {
//                            out.println(
//                                    "<li>Your description of this file is: " + fileDescription + "</li>" );
//                            store.setDataFileDescription( fileDescription );
//                        }
//                        // print and save the file format
//                        if ( externalData.getFileFormat() != null ) {
//                            out.println(
//                                    "<li>The format of your file is: " +
//                                    externalData.getFileFormat().getTerm() +
//                                    "</li>" );
//                            store.setFileFormat(
//                                    externalData.getFileFormat().getEndurant().getIdentifier() );
//                        }
//                        // print and save the current GenericAction associated with this data-filled GPA
//                        ActionApplication dataFilledCallingAction = findActionApplication(
//                                oneLevelUpGPA, dataFilledGPA.getIdentifier() );
//                        // todo put the below into the new ActionHierarchyScheme format
////                        if ( dataFilledCallingAction != null ) {
////                            out.println( "<li>This data file was created in the following step of your experiment:" );
////                            store.getAssayActionSummary().setChosenActionEndurant(
////                                    dataFilledCallingAction.getAction().getEndurant().getIdentifier() );
////                            store.getAssayActionSummary()
////                                    .setChosenActionName( dataFilledCallingAction.getAction().getName() );
////                            if ( !twoAboveAssayGPAs.isEmpty() ) {
////                                // if there is a second factor choice associated with this action, fill out that, too.
////                                ActionApplication oneLevelUpCallingAction = findActionApplication(
////                                        getLatestParentGPA(
////                                                twoAboveAssayGPAs, oneLevelUpGPA.getIdentifier() ),
////                                        oneLevelUpGPA.getIdentifier() );
////                                if ( oneLevelUpCallingAction != null ) {
////                                    store.getOneLevelUpActionSummary().setChosenActionEndurant(
////                                            oneLevelUpCallingAction.getAction().getEndurant().getIdentifier() );
////                                    store.getOneLevelUpActionSummary().setChosenActionName(
////                                            oneLevelUpCallingAction.getAction().getName() );
////                                }
////                            }
////                            out.print( store.getAssayActionSummary().getChosenActionName() );
////                            if ( store.getOneLevelUpActionSummary() != null &&
////                                 store.getOneLevelUpActionSummary().getChosenActionName() != null ) {
////                                out.println(
////                                        ", part of the " + store.getOneLevelUpActionSummary().getChosenActionName() );
////                            }
////                            out.println( "</li>" );
////                        }
//
//                        // now get the information from the EquipmentApplications
//
//                        for ( EquipmentApplication equipmentApplication : ( Set<EquipmentApplication> ) dataFilledGPA
//                                .getEquipmentApplications() ) {
//                            GenericEquipmentSummary equipmentSummary = new GenericEquipmentSummary();
//                            // print and save the equipment name;
//                            equipmentSummary.setEquipmentName( equipmentApplication.getAppliedEquipment().getName() );
//                            out.println( "<li>Information about " + equipmentSummary.getEquipmentName() );
//                            out.println( "<ul>" );
//                            // print and save the equipment description
//                            String equipmentDescription = "";
//                            for ( Description description : ( Set<Description> ) equipmentApplication
//                                    .getDescriptions() ) {
//                                equipmentDescription += description.getText();
//                            }
//                            equipmentSummary.setFreeTextDescription( equipmentDescription );
//                            out.println( "<li>" + equipmentSummary.getFreeTextDescription() + "</li>" );
//                            // print and save the equipment ontology terms and atomic values
//                            for ( ParameterValue parameterValue : ( Set<ParameterValue> ) equipmentApplication
//                                    .getParameterValues() ) {
//                                if ( parameterValue.getValue() instanceof AtomicValue ) {
//                                    equipmentSummary.putParameterAndAtomicPair(
//                                            parameterValue.getParameter().getEndurant().getIdentifier(),
//                                            ( ( AtomicValue ) parameterValue.getValue() ).getValue() );
//                                    out.println(
//                                            "<li>" + ( ( AtomicValue ) parameterValue.getValue() ).getValue() +
//                                            "</li>" );
//                                } else if ( parameterValue.getValue() instanceof ComplexValue ) {
//                                    equipmentSummary.putParameterAndTermPair(
//                                            parameterValue.getParameter().getEndurant().getIdentifier(),
//                                            ( ( ComplexValue ) parameterValue.getValue() ).getValue()
//                                                    .getEndurant().getIdentifier() );
//                                    out.println(
//                                            "<li>" + parameterValue.getParameter().getName() + ": " +
//                                            ( ( ComplexValue ) parameterValue.getValue() ).getValue()
//                                                    .getTerm()
//                                            // not sure how to display the ontology sources yet
////                                                    + " (From Ontology Source: " +
////                                                    ( ( ComplexParameterValue ) parameterValue ).getParameterValue().getOntologySource().getName()
////                                                    + ")"
//+ "</li>" );
//                                }
//                            }
//                            out.println( "</ul>" );
//                            out.println( "</li>" );
//                            store.putGenericEquipmentInfoValue(
//                                    equipmentApplication.getAppliedEquipment().getEndurant().getIdentifier(),
//                                    equipmentSummary );
//                        }
//
//                        if ( !dataFilledGPA.getParameterValues().isEmpty() ||
//                             !dataFilledGPA.getDescriptions().isEmpty() ) {
//                            GenericProtocolApplicationSummary protocolApplicationSummary =
//                                    new GenericProtocolApplicationSummary();
//                            // print and save all atomic parameters
//                            out.println( "<li>Information about the protocol selected" );
//                            out.println( "<ul>" );
//                            for ( ParameterValue parameterValue : ( Set<ParameterValue> ) dataFilledGPA
//                                    .getParameterValues() ) {
//                                if ( parameterValue.getValue() instanceof AtomicValue ) {
//                                    protocolApplicationSummary.putParameterAndAtomicPair(
//                                            parameterValue.getParameter().getEndurant().getIdentifier(),
//                                            ( ( AtomicValue ) parameterValue.getValue() ).getValue() );
//                                    out.println(
//                                            "<li>" + ( ( AtomicValue ) parameterValue.getValue() ).getValue() +
//                                            "</li>" );
//                                }
//                            }
//
//                            // print and save the descriptions
//                            for ( Description description : ( Set<Description> ) dataFilledGPA.getDescriptions() ) {
//                                // createMaterialForDataFormContents along the " = "
//                                String[] parsedStrings = description.getText().split( " = " );
//                                protocolApplicationSummary.putDescription( parsedStrings[0], parsedStrings[1] );
//                                out.println( "<li>" + description.getText() + "</li>" );
//                            }
//                            out.println( "</ul>" );
//                            out.println( "</li>" );
//                            store.putGenericProtocolApplicationInfoValue(
//                                    dataFilledGPA.getProtocol().getEndurant().getIdentifier(),
//                                    protocolApplicationSummary );
//                        }
//
//                        // save and print out the material factors
//                        // Currently the *input* only allows for a single dummy material. However,
//                        // we will write the display with it all ready for multiple dummy materials, except for the way
//                        // the MaterialFactorsStore is overwritten with each loop.
//                        // setCreatedMaterialEndurant() doesn't need to be run, as that is something used only within
//                        // LoadFuge
//                        if ( !dataFilledGPA.getInputCompleteMaterials().isEmpty() ) {
//                            out.println(
//                                    "<li>You have associated an input material with the creation of this data file:" );
//                            out.println( "<ul>" );
//                            MaterialFactorsStore materialFactorsStore = new MaterialFactorsStore();
//                            for ( Material material : ( Set<Material> ) dataFilledGPA
//                                    .getInputCompleteMaterials() ) {
//                                if ( material instanceof GenericMaterial ) {
//                                    if ( material.getName() != null && material.getName().length() > 0 ) {
//                                        materialFactorsStore.setMaterialName( material.getName() );
//                                        out.println( "<li>Its name is " + material.getName() + "</li>" );
//                                    }
//                                    if ( material.getMaterialType() != null ) {
//                                        materialFactorsStore.setMaterialType(
//                                                material.getMaterialType().getEndurant().getIdentifier() );
//                                        out.println(
//                                                "<li>" + material.getMaterialType().getTerm() + "<!-- type --></li>" );
//                                    }
//                                    for ( OntologyTerm ontologyTerm : ( Set<OntologyTerm> ) material
//                                            .getCharacteristics() ) {
//                                        materialFactorsStore
//                                                .addCharacteristic(
//                                                        ontologyTerm.getOntologySource().getEndurant().getIdentifier(),
//                                                        ontologyTerm.getEndurant().getIdentifier() );
//                                        out.println(
//                                                "<li>" + ontologyTerm.getTerm() + "<!-- characteristic --></li>" );
//                                    }
//                                    for ( Description description : ( Set<Description> ) material.getDescriptions() ) {
//                                        // Structure for treatment info is "Treatment: "
//                                        if ( description.getText().startsWith( "Treatment: " ) ) {
//                                            materialFactorsStore
//                                                    .addTreatmentInfo( description.getText().substring( 11 ) );
//                                            out.println( "<li>" + description.getText() + "</li>" );
//                                        } else {
//                                            // Structure for OntologyReplacement is "OntologyReplacementTitle = value"
//                                            int equalsPosition = description.getText().indexOf( " = " );
//                                            out.println( "<li>" + description.getText() + "</li>" );
//                                            String title = description.getText().substring( 0, equalsPosition );
//                                            String ontValue = description.getText().substring( equalsPosition + 3 );
//                                            materialFactorsStore.putOntologyReplacementsPair( title, ontValue );
//                                        }
//                                    }
//                                }
//                            }
//                            out.println( "</ul>" );
//                            out.println( "</li>" );
//                            //todo mfs
////                            store.setInputMaterialInfo( materialFactorsStore );
//                        }
//
//                        datafileSpecificMetadataStores.add( store );
//
//                        // close data file details list
//                        out.println( "</ul>" );
//                        // close data file list element
//                        out.println( "</li>" );
//
//                    }
//
//                }
//                currentSessionBean.setDatafileSpecificMetadataStores( datafileSpecificMetadataStores );
//
//                // we're not allowing anyone to modify the protocols - they will only be able to modify the data file.
//                currentSessionBean.setProtocolLocked( true );
//                currentSessionBean.setMetadataFromAnotherExperiment( true );
//
//                if ( externalDataIdentifier.length() > 0 ) {
//                    allBeans.put( externalDataIdentifier, currentSessionBean );
//                } else {
//                    out.println(
//                            "<li>There has been an error storing this data file's metadata. Please " +
//                            "Contact helpdesk@cisban.ac.uk with this error and the name of the experiment you were " +
//                            "trying to display</li>" );
//                }

//                // flush after each data file to give the user something to see
//                out.println();
//                out.flush();
//            }
//        }


        return allBeans;
    }

    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private static GenericProtocolApplication getLatestParentGPA( List<GenericProtocolApplication> gpas,
                                                                  String referencedGpaIdentifier ) {
        for ( GenericProtocolApplication gpa : gpas ) {
            for ( ActionApplication actionApplication : ( Set<ActionApplication> ) gpa.getActionApplications() ) {
                if ( actionApplication.getChildProtocolApplication().getIdentifier()
                        .equals( referencedGpaIdentifier ) ) {
                    return gpa;
                }
            }
        }
        return null;
    }

    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private static ActionApplication findActionApplication( GenericProtocolApplication gpa,
                                                            String referencedGpaIdentifier ) {
        for ( ActionApplication actionApplication : ( Set<ActionApplication> ) gpa.getActionApplications() ) {
            if ( actionApplication.getChildProtocolApplication().getIdentifier().equals( referencedGpaIdentifier ) ) {
                return actionApplication;
            }
        }
        return null;
    }
}

