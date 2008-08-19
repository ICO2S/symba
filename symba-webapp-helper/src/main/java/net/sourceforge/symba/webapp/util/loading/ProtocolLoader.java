package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.material.GenericMaterialMeasurement;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.ontology.ObjectProperty;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.ontology.OntologyProperty;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.OntologyCollectionMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.ProtocolCollectionMappingHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.GenericProtocolApplicationSummary;
import net.sourceforge.symba.webapp.util.MaterialFactorsStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionInformation;

import java.util.*;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/loading/LoadFuge.java $
 */
public class ProtocolLoader {

    public static ProtocolCollection prepareProtocolCollection( FuGE fuge, EntityService entityService ) {

        ProtocolCollection protocolCollection = ( ProtocolCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.ProtocolCollection" );

        Set<Equipment> equipments = new HashSet<Equipment>();
        Set<Software> softwares = new HashSet<Software>();
        Set<ProtocolApplication> protocolApplications = new HashSet<ProtocolApplication>();
        Set<Protocol> protocols = new HashSet<Protocol>();
        if ( fuge.getProtocolCollection() != null ) {
            if ( fuge.getProtocolCollection().getAllEquipment() != null ) {
                equipments = ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment();
            }
            if ( fuge.getProtocolCollection().getAllSoftwares() != null ) {
                softwares = ( Set<Software> ) fuge.getProtocolCollection().getAllSoftwares();
            }
            if ( fuge.getProtocolCollection().getProtocolApplications() != null ) {
                protocolApplications =
                        ( Set<ProtocolApplication> ) fuge.getProtocolCollection().getProtocolApplications();
            }
            if ( fuge.getProtocolCollection().getProtocols() != null ) {
                protocols = ( Set<Protocol> ) fuge.getProtocolCollection().getProtocols();
            }
        }
        protocolCollection.setAllEquipment( equipments );
        protocolCollection.setAllSoftwares( softwares );
        protocolCollection.setProtocolApplications( protocolApplications );
        protocolCollection.setProtocols( protocols );

        return protocolCollection;
    }

    public static FuGE loadMaterialTransformationProtocols( FuGE fuge,
                                                            EntityService entityService,
                                                            Person auditor,
                                                            SymbaFormSessionBean symbaFormSessionBean,
                                                            SymbaEntityService symbaEntityService ) {


        ActionHierarchyScheme ahs = new ActionHierarchyScheme();
        ahs.parse( symbaFormSessionBean.getSpecimenActionHierarchy() );

        GenericProtocolApplication clonedGPA =
                ( GenericProtocolApplication ) symbaEntityService.getLatestByIdentifier( ahs.getGpaIdentifier() );

        // always add the MT GPA.
        GenericProtocolApplication mtGpa =
                createMtGPA( symbaFormSessionBean.getSpecimenToBeUploaded(), clonedGPA.getProtocol(), entityService,
                        auditor, symbaEntityService );

        ProtocolCollection protocolCollection = fillProtocolCollection( fuge, entityService, symbaFormSessionBean );

        protocolCollection.getProtocolApplications().add( mtGpa );

        // move upwards from the GPA, ensuring at each stage that protocol at each level above the gpa has
        // its own gpa with an action application pointing downwards towards the one we want.
        protocolCollection = createAllParentsOfGpa( protocolCollection, mtGpa, auditor,
                ahs, symbaEntityService, entityService );


        fuge.setProtocolCollection( protocolCollection );
        return fuge;
    }

    /**
     * Create a GPA for each action listed in the hierarchy, if not already there. The top of the hierarchy
     * is not an action, but instead is the top-level protocol.
     *
     * @param protocolCollection what to store the new GPAs in, and where any existing ones would be
     * @param childGPA           the already-created GPA whose parent GPA you're looking for
     * @param auditor            the person to assign the creation of the object to in the database
     * @param ahs                the full listing of all actions that lead to the creation of childGPA
     * @param symbaEntityService the connection to the database
     * @param entityService      the fuge connection to the database
     * @return the modified protocol collection
     */
    private static ProtocolCollection createAllParentsOfGpa( ProtocolCollection protocolCollection,
                                                             GenericProtocolApplication childGPA,
                                                             Person auditor,
                                                             ActionHierarchyScheme ahs,
                                                             SymbaEntityService symbaEntityService,
                                                             EntityService entityService ) {

        // read from the bottom upwards
        for ( int i = ahs.getActionHierarchy().size() - 1; i >= 0; i-- ) {
            ActionInformation ai = ahs.getActionHierarchy().get( i );

            GenericAction genericAction =
                    ( GenericAction ) symbaEntityService.getLatestByEndurant( ai.getActionEndurant() );

            // Check and see if a GPA is already present for parent protocol.
            GenericProtocolApplication parentProtocolGpa = null;

            // The GPA that owns the childGPA will be identified via its protocol reference (which matches
            // ai.getParentProtocol*) and its ActionApplication, which references ai.getAction* 
            for ( ProtocolApplication protocolApplication : protocolCollection.getProtocolApplications() ) {
                if ( protocolApplication instanceof GenericProtocolApplication ) {
                    GenericProtocolApplication gpa = ( GenericProtocolApplication ) protocolApplication;
                    if ( gpa.getProtocol().getIdentifier().equals( ai.getParentProtocolIdentifier() ) ) {
                        // this is the GPA of the parentProtocol. It doesn't need to be created. We need
                        // to check that the appropriate ActionApplication has already been added. To do this,
                        // we ensure that the correct Action has been referenced, AND that the childGPA is also
                        // linked.
                        boolean actionAppPresent = false;
                        if ( !gpa.getActionApplications().isEmpty() ) {
                            for ( ActionApplication actionApplication : gpa.getActionApplications() ) {
                                if ( actionApplication.getAction().getIdentifier()
                                        .equals( genericAction.getIdentifier() ) &&
                                                                                 actionApplication
                                                                                         .getChildProtocolApplication()
                                                                                         .getIdentifier().equals(
                                                                                         childGPA.getIdentifier() ) ) {
                                    actionAppPresent = true;
                                    break;
                                }
                            }
                        }
                        if ( !actionAppPresent ) {
                            // if we get as far as here, then we need to add an ActionApplication
                            parentProtocolGpa = addActionApplication( childGPA, genericAction, gpa, auditor );
                            // remove the old gpa from the experiment, then add the new one in the db and exp.
                            protocolCollection.getProtocolApplications().remove( gpa );
                            parentProtocolGpa = ( GenericProtocolApplication ) DatabaseObjectHelper
                                    .save( "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                                            parentProtocolGpa,
                                            auditor );
                            protocolCollection.getProtocolApplications().add( parentProtocolGpa );
                        }
                        break; // we've done what needed to be done - we will only be finding one matched GPA, so move to the next
                    }
                }
            }
            if ( parentProtocolGpa == null ) {
                // haven't found the parent GPA. Make it.
                parentProtocolGpa = createUpperGpa( ai, entityService, auditor );

                // now add the action application
                parentProtocolGpa = addActionApplication( childGPA, genericAction, parentProtocolGpa, auditor );
                parentProtocolGpa = ( GenericProtocolApplication ) DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                                parentProtocolGpa,
                                auditor );
                protocolCollection.getProtocolApplications().add( parentProtocolGpa );
            }

            // now, the parentProtocolGpa becomes the childGPA for the next round of checks
            childGPA = parentProtocolGpa;
        }
        return protocolCollection;
    }

    public static boolean characteristicsMatch( MaterialFactorsStore specimenToBeUploaded,
                                                GenericProtocolApplication clonedGPA ) {
        // the descriptor set must match, and the environmental conditions. Only the output material
        // has to be checked.
        // if *anything* in the GPA can't be found in the MFS, then it isn't a match.
        for ( OntologyTerm ontologyTerm : clonedGPA.getOutputMaterials().iterator().next().getCharacteristics() ) {
            if ( ontologyTerm instanceof OntologyIndividual ) {
                OntologyIndividual topOI = ( OntologyIndividual ) ontologyTerm;
                for ( OntologyProperty ontologyProperty : topOI.getProperties() ) {
                    // if it contains ObjectProperties, then it is the descriptor set.
                    if ( ontologyProperty instanceof ObjectProperty ) {
                        ObjectProperty objectProperty = ( ObjectProperty ) ontologyProperty;
                        // the descriptor identifier etc isn't changed by the form, so doesn't need checking.
                        // move on to the OntologyIndividual objects stored within the ObjectProperty
                        for ( OntologyIndividual unsharedOI : objectProperty.getContent() ) {
                            if ( !foundOIMatch( unsharedOI, specimenToBeUploaded.getNovelCharacteristics(),
                                    specimenToBeUploaded.getNovelMultipleCharacteristics() ) ) {
                                return false;
                            }
                        }
                    }
                }
                // check the OI itself, if there were no ontology properties. these are "standard" characteristics.
                if ( topOI.getProperties() == null || topOI.getProperties().isEmpty() ) {
                    if ( !foundOIMatch( topOI, specimenToBeUploaded.getCharacteristics(),
                            specimenToBeUploaded.getMultipleCharacteristics() ) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean foundOIMatch( OntologyIndividual unsharedOI,
                                         HashMap<String, String> characteristics,
                                         HashMap<String, LinkedHashSet<String>> multipleCharacteristics ) {
        Set<String> termAndTermACs = new LinkedHashSet<String>();
        String singleTermAndTermAC =
                characteristics.get( unsharedOI.getOntologySource().getEndurant().getIdentifier() );
        if ( singleTermAndTermAC == null ) {
            if ( multipleCharacteristics.get( unsharedOI.getOntologySource().getEndurant().getIdentifier() ) != null ) {
                termAndTermACs.addAll(
                        multipleCharacteristics.get( unsharedOI.getOntologySource().getEndurant().getIdentifier() ) );
            } else {
                // Couldn't find the ontology source anywhere in the session info. Shouldn't happen,
                // no match
                return false;
            }
        } else {
            termAndTermACs.add( singleTermAndTermAC );
        }

        return termAndTermACs.contains(
                unsharedOI.getTerm().trim() + "::" + unsharedOI.getTermAccession().trim() );
    }

    // the default protocol loader
    // all names must include the symbaFormSessionBean.getTopLevelProtocolName() in order to be found
    public static FuGE loadAssayProtocols( FuGE fuge,
                                           EntityService entityService,
                                           Person auditor,
                                           SymbaFormSessionBean symbaFormSessionBean,
                                           SymbaEntityService symbaEntityService ) {

        ProtocolCollection protocolCollection = fillProtocolCollection( fuge, entityService, symbaFormSessionBean );

        String expName;
        if ( symbaFormSessionBean.getExperimentName() != null ) {
            expName = symbaFormSessionBean.getExperimentName();
        } else {
            expName = fuge.getName();
        }

        // Get the top-level protocol, which has no matching GenericAction.
        GenericProtocol topLevelProtocol =
                ( GenericProtocol ) symbaEntityService
                        .getLatestByEndurant( symbaFormSessionBean.getTopLevelProtocolEndurant() );
        if ( topLevelProtocol == null ) {
            System.err.println(
                    "Error finding top-level protocol for the fuge investigation type: " +
                    symbaFormSessionBean.getTopLevelProtocolEndurant() + " (" +
                    symbaFormSessionBean.getTopLevelProtocolName() + ")" );
            return fuge;
        }

        // get the GPA attached to the top level protocol, if present.
        GenericProtocolApplication gpaOfTopLevelProtocol = null;
        // first, find the highest-level GPA: that is, the GPA of the top-level investigation
        for ( Object gpaObject : protocolCollection.getProtocolApplications() ) {
            if ( gpaObject instanceof GenericProtocolApplication ) {
                GenericProtocolApplication gpa = ( GenericProtocolApplication ) gpaObject;
                if ( gpa.getProtocol()
                        .getEndurant()
                        .getIdentifier()
                        .equals( topLevelProtocol.getEndurant().getIdentifier() ) ) {
                    gpaOfTopLevelProtocol = gpa;
                    break;
                }
            }
        }

        // There will either be two levels or three levels of GPAs: two levels (assay/MT and top-level protocol) if
        // it is a two level investigation, and three levels (assay/MT, one level up from assay/MT, and top-level
        // protocol) otherwise.
        for ( DatafileSpecificMetadataStore store : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
            boolean threeLevelInvestigation = false;
            GenericProtocol assayProtocol = ( GenericProtocol ) symbaEntityService
                    .getLatestByEndurant( store.getAssayActionSummary().getChosenChildProtocolEndurant() );
            GenericAction assayAction = ( GenericAction ) symbaEntityService
                    .getLatestByEndurant( store.getAssayActionSummary().getChosenActionEndurant() );

            GenericProtocol oneAboveAssayProtocol = null;
            GenericAction oneAboveAssayAction = null;
            if ( store.getOneLevelUpActionSummary() != null &&
                 store.getOneLevelUpActionSummary().getChosenActionName() != null ) {
                threeLevelInvestigation = true;
                oneAboveAssayProtocol = ( GenericProtocol ) symbaEntityService
                        .getLatestByEndurant( store.getOneLevelUpActionSummary().getChosenChildProtocolEndurant() );
                oneAboveAssayAction = ( GenericAction ) symbaEntityService
                        .getLatestByEndurant( store.getOneLevelUpActionSummary().getChosenActionEndurant() );
            }

            // there is a problem if there isn't a matching protocol for the assay
            if ( assayProtocol == null ) {
                System.err.println(
                        "Error finding parent protocol for assay action endurant " +
                        store.getAssayActionSummary().getChosenActionEndurant() + " (" +
                        store.getAssayActionSummary().getChosenActionName() + ")" );
                return fuge;
            }

            // there is a problem if there isn't a matching protocol for one level up from the assay, if this is a
            // two-level investigation
            if ( threeLevelInvestigation && oneAboveAssayProtocol == null ) {
                System.err.println(
                        "Error finding parent protocol for assay for action endurant " +
                        store.getOneLevelUpActionSummary().getChosenActionEndurant() + " (" +
                        store.getOneLevelUpActionSummary().getChosenActionName() + ")" );
                return fuge;
            }

            // Next, we need to check and see if a GPA is already present for the top two levels if a 3-level investigation,
            // or in the top-level only if it is a 2-level investigation. We will ALWAYS want to create a new
            // assay-level GPA.
            GenericProtocolApplication gpaOfOneAboveAssayProtocol = null;

            // If it is a three-level investigation, then the gpaOfOneAboveAssayProtocol can be determined by looking at
            // the ActionApplication objects of the gpaOfTopLevelProtocol. Otherwise, the gpa found will be that of
            // the assay gpa. As we are going to make a new assayGPA anyway, then we will only run this step if
            // this investigation is a threeLevelInvestigation
            if ( threeLevelInvestigation && gpaOfTopLevelProtocol != null ) {
                for ( Object obj2 : gpaOfTopLevelProtocol.getActionApplications() ) {
                    ActionApplication actionApplication = ( ActionApplication ) obj2;
                    // search for the action referenced by the AA, as otherrwise you might catch the right
                    // protocol but the wrong action
                    Action referencedAction = actionApplication.getAction();
                    if ( referencedAction instanceof GenericAction ) {
                        GenericAction genericAction = ( GenericAction ) referencedAction;
                        if ( genericAction.getEndurant()
                                .getIdentifier()
                                .equals( assayAction.getEndurant().getIdentifier() ) ) {
                            if ( actionApplication
                                    .getChildProtocolApplication() instanceof GenericProtocolApplication ) {
                                gpaOfOneAboveAssayProtocol =
                                        ( GenericProtocolApplication ) actionApplication.getChildProtocolApplication();
                            }
                        }
                    }
                }
            }

            // always create the assay GPA. This will get added, via an ActionApplication, to the higher-level
            // GPA or GPAs.
            GenericProtocolApplication assayGPA =
                    createAssayGPA( store, assayProtocol, entityService, auditor, symbaEntityService );

            // add the assayGPA to the parse of all GPAs
            protocolCollection.getProtocolApplications().add( assayGPA );

            // ensure that the GPA of the top level protocol is not null
            // first, ensure that the GPA is not null, and if it is null, create a new object
            if ( gpaOfTopLevelProtocol == null ) {
                gpaOfTopLevelProtocol =
                        ( GenericProtocolApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", auditor );
                gpaOfTopLevelProtocol.setName( "Actual Application/Performance of " + topLevelProtocol.getName() +
                                               " for Investigation " + expName );
                gpaOfTopLevelProtocol.setProtocol( topLevelProtocol );
                // todo We should really ask them the Activity Date, but for now assume current date.
                gpaOfTopLevelProtocol.setActivityDate( new java.sql.Timestamp( ( new Date() ).getTime() ) );
            }

            // now, add a link to the assayGPA to the GPA one level up from here. Which GPA that is,
            // is determined by the value of threeLevelInvestigation
            if ( threeLevelInvestigation ) {
                // first, ensure that the GPA is not null, and if it is null, create a new object
                if ( gpaOfOneAboveAssayProtocol == null ) {
                    gpaOfOneAboveAssayProtocol =
                            ( GenericProtocolApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                    "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", auditor );

                    gpaOfOneAboveAssayProtocol
                            .setName( "Actual Application/Performance of " + oneAboveAssayAction.getName() +
                                      " for Investigation " + expName );

                    gpaOfOneAboveAssayProtocol.setProtocol( oneAboveAssayProtocol );
                    // todo We should really ask them the Activity Date, but for now assume current date.
                    gpaOfOneAboveAssayProtocol.setActivityDate( new java.sql.Timestamp( ( new Date() ).getTime() ) );
                }
                // add the ActionApplication to the gpaOfOneAboveAssayProtocol
                gpaOfOneAboveAssayProtocol =
                        addActionApplication( assayGPA, assayAction, gpaOfOneAboveAssayProtocol, auditor );

                // If gpaOfOneAboveAssayProtocol is new, add to database. Otherwise, put a new version in.
                if ( gpaOfOneAboveAssayProtocol.getId() != null ) {
                    // Each time we load a new version of the one-level up GPA, we need to clean the allGPAs Set.
                    // We need to remember that any child GPA *must* be removed
                    // from the parse of allGPAs *if* it, too, is being overwritten.
                    protocolCollection.getProtocolApplications().remove( gpaOfOneAboveAssayProtocol );
                }
                DatabaseObjectHelper.save(
                        "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                        gpaOfOneAboveAssayProtocol,
                        auditor );
                // associate the new assays with the protocol collection.
                protocolCollection.getProtocolApplications().add( gpaOfOneAboveAssayProtocol );

                // then add the ActionApplication linking the gpaOfTopLevelProtocol to the gpaOfOneAboveAssayProtocol
                gpaOfTopLevelProtocol = addActionApplication(
                        gpaOfOneAboveAssayProtocol, oneAboveAssayAction, gpaOfTopLevelProtocol, auditor );
            } else {
                // add the ActionApplication to the gpaOfTopLevelProtocol
                gpaOfTopLevelProtocol = addActionApplication( assayGPA, assayAction, gpaOfTopLevelProtocol, auditor );
            }

            // the gpaOfTopLevelProtocol is now finished. load in the database.
            // If gpaOfTopLevelProtocol is new, add to database. Otherwise, put a new version in.
            // todo move this and associated code out of the for-loop and only do this after all data files are processed: it's currently loading unnecessary intermediaries into the db
            if ( gpaOfTopLevelProtocol.getId() != null ) {
                // Each time we load a new version of the top level GPA, we need to clean the allGPAs Set.
                // The "old" top-level GPA will be replaced with the new version of the top-level GPA with the new
                // ActionApplication objects.
                protocolCollection.getProtocolApplications().remove( gpaOfTopLevelProtocol );
            }
            DatabaseObjectHelper.save(
                    "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                    gpaOfTopLevelProtocol,
                    auditor );

            protocolCollection.getProtocolApplications().add( gpaOfTopLevelProtocol );
        }

//        System.err.println( "ABOUT TO PRINT OUT PROTOCOL COLLECTION MATERIAL INFO DIRECTLY AFTER SETTING GPAs" );
//        RetrieveSimple retrieveSimple = new RetrieveSimple();
//        try {
//            retrieveSimple.runSimpleRetrieval( entityService, protocolCollection );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            System.err.println( "Error trying to print out the material contents of the fuge protocol collection." );
//        }

        if ( fuge.getProtocolCollection() != null ) {
            protocolCollection.setAllSoftwares( fuge.getProtocolCollection().getAllSoftwares() );
        }

        // load the fuge object into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.ProtocolCollection", protocolCollection, auditor );
        fuge.setProtocolCollection( protocolCollection );

//        System.err.println( "ABOUT TO PRINT OUT PROTOCOL COLLECTION MATERIAL INFO DIRECTLY AFTER LOADING INTO DB" );
//        try {
//            retrieveSimple.runSimpleRetrieval( entityService, fuge.getProtocolCollection() );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            System.err.println( "Error trying to print out the material contents of the fuge protocol collection." );
//        }

        OntologyCollectionMappingHelper och = new OntologyCollectionMappingHelper();
        fuge = och.addRelevantOntologyTerms( fuge, auditor );

        return fuge;
    }

    private static ProtocolCollection fillProtocolCollection( FuGE fuge,
                                                              EntityService entityService,
                                                              SymbaFormSessionBean symbaFormSessionBean ) {

        // create a new protocol collection based on what is already extant (if present at all)
        ProtocolCollection protocolCollection = prepareProtocolCollection( fuge, entityService );

        // Now we need to associate the Data with the appropriate protocol applications and protocols.
        // We know which factors these are for because we don't give
        // the user the choice of protocols, but instead the choice of Actions available.

        ProtocolCollectionMappingHelper cpc = new ProtocolCollectionMappingHelper();

        // The GenericProtocol whose GenericAction is stored from the form will get the data item added to it.
        protocolCollection.setProtocols( cpc.addRelevantProtocols(
                ( Set<Protocol> ) protocolCollection.getProtocols(),
                symbaFormSessionBean.getTopLevelProtocolEndurant().trim() ) );

        // Now retrieve all equipment associated with these protocols
        protocolCollection.setAllEquipment( cpc.addRelevantEquipment(
                ( Set<Equipment> ) protocolCollection.getAllEquipment(),
                ( Set<Protocol> ) protocolCollection.getProtocols() ) );

        // Now retrieve all equipment associated with these protocols
        protocolCollection.setAllSoftwares( cpc.addRelevantSoftware(
                ( Set<Software> ) protocolCollection.getAllSoftwares(),
                ( Set<Protocol> ) protocolCollection.getProtocols() ) );

        return protocolCollection;
    }

    private static GenericProtocolApplication createAssayGPA( DatafileSpecificMetadataStore store,
                                                              GenericProtocol assayProtocol,
                                                              EntityService entityService,
                                                              Person auditor,
                                                              SymbaEntityService symbaEntityService ) {

        GenericProtocolApplication assayGPA =
                ( GenericProtocolApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                        "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", auditor );
        assayGPA.setName( "Actual Application/Performance of a " + assayProtocol.getName() );
        assayGPA.setProtocol( assayProtocol );
        // todo We should really ask them the Activity Date, but for now assume current date.
        assayGPA.setActivityDate( new java.sql.Timestamp( ( new Date() ).getTime() ) );

        // add the data
        // todo - allow multiple data files to be produced from a single run of the assay protocol.
        Set<Data> externalDataSet = new HashSet<Data>();
        externalDataSet.add( ( Data ) symbaEntityService.getLatestByEndurant( store.getEndurantLsid() ) );
        assayGPA.setOutputData( externalDataSet );

        if ( !store.getGenericProtocolApplicationInfo().isEmpty() ) {

            // add any deviations from the zero or more generic parameters associated with the protocol assigned
            // to this data item
            // todo add more types of parameter values, as currently only valid for atomic values
            Set<ParameterValue> parameterValues = new HashSet<ParameterValue>();
            GenericProtocolApplicationSummary summary = store.getGenericProtocolApplicationInfo()
                    .get( assayProtocol.getEndurant().getIdentifier() );
            if ( summary != null ) {
                if ( summary.getParameterAndAtomics() != null && !summary.getParameterAndAtomics().isEmpty() ) {
                    for ( String parameterKey : summary.getParameterAndAtomics().keySet() ) {
                        ParameterValue parameterValue =
                                ( ParameterValue ) entityService.createDescribable(
                                        "net.sourceforge.fuge.common.protocol.ParameterValue" );
                        AtomicValue atomicValue = ( AtomicValue ) entityService.createDescribable(
                                "net.sourceforge.fuge.common.measurement.AtomicValue" );
                        atomicValue.setValue( summary.getParameterAndAtomics().get( parameterKey ) );
                        DatabaseObjectHelper
                                .save( "net.sourceforge.fuge.common.measurement.AtomicValue", atomicValue, auditor );
                        parameterValue.setValue( atomicValue );
                        parameterValue.setParameter(
                                ( GenericParameter ) symbaEntityService.getLatestByEndurant(
                                        parameterKey ) );
                        DatabaseObjectHelper.save(
                                "net.sourceforge.fuge.common.protocol.ParameterValue", parameterValue,
                                auditor );
                        parameterValues.add( parameterValue );
                    }
                    assayGPA.setParameterValues( parameterValues );
                }

                // add any descriptions. As this is a brand-new GPA, there will be no existing descriptions
                if ( summary.getDescriptions() != null && !summary.getDescriptions().isEmpty() ) {
                    Set<Description> descriptions = new HashSet<Description>();
                    for ( String currentDescriptionKey : summary.getDescriptions().keySet() ) {
                        Description description = ( Description ) entityService.createDescribable(
                                "net.sourceforge.fuge.common.description.Description" );
                        description.setText(
                                currentDescriptionKey + " = " +
                                summary.getDescriptions().get( currentDescriptionKey ) );
                        DatabaseObjectHelper
                                .save( "net.sourceforge.fuge.common.description.Description", description, auditor );
                        descriptions.add( description );
                    }
                    assayGPA.setDescriptions( descriptions );
                }
            }
        }

        // add any deviations from the zero or more generic parameters associated with the equipment associated
        // with the protocol assigned to this data item
        if ( !store.getGenericEquipmentInfo().isEmpty() ) {
            // unchecked cast warning provided by javac when using generics in Lists/Sets and
            // casting from Object, even though runtime can handle this.
            // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
            @SuppressWarnings( "unchecked" )
            Set<EquipmentApplication> eaSet = ( Set<EquipmentApplication> ) assayGPA.getEquipmentApplications();

            for ( String equipmentKey : ( store.getGenericEquipmentInfo() ).keySet() ) {
                // Now we can create an EquipmentApplication in the GPA of the second-level (assay)protocol that is
                // associated with the correct Equipment object. However, make sure we don't delete any existing
                // EquipmentApplications by adding to them rather than by creating a new collection.

                EquipmentApplication application =
                        ( EquipmentApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                "net.sourceforge.fuge.common.protocol.EquipmentApplication", auditor );
                application.setName( "Application of " +
                                     ( store.getGenericEquipmentInfo() ).get( equipmentKey ).getEquipmentName() +
                                     " Equipment" );
                application.setAppliedEquipment( ( Equipment ) symbaEntityService.getLatestByEndurant( equipmentKey ) );

                // now add the appropriate changes to the original equipment as parameters. We assume that if
                // there is a value in getGenericEquipmentInfo, that a new parameterValue parse must be made and
                // further, will be non-empty
                Set<ParameterValue> pvSet = new HashSet<ParameterValue>();
                // look for complex values
                if ( ( store.getGenericEquipmentInfo() ).get( equipmentKey ).getParameterAndTerms() != null &&
                     !( store.getGenericEquipmentInfo() ).get( equipmentKey )
                             .getParameterAndTerms()
                             .isEmpty() ) {
                    for ( String parameterKey : ( store.getGenericEquipmentInfo() ).get( equipmentKey )
                            .getParameterAndTerms()
                            .keySet() ) {
                        ParameterValue complexParameterValue =
                                ( ParameterValue ) entityService.createDescribable(
                                        "net.sourceforge.fuge.common.protocol.ParameterValue" );
                        ComplexValue complexValue = ( ComplexValue ) entityService.createDescribable(
                                "net.sourceforge.fuge.common.measurement.ComplexValue" );
                        complexValue.setValue( ( OntologyTerm ) symbaEntityService.getLatestByEndurant(
                                ( store.getGenericEquipmentInfo() ).get( equipmentKey )
                                        .getParameterAndTerms().get( parameterKey ) ) );
                        DatabaseObjectHelper
                                .save( "net.sourceforge.fuge.common.measurement.ComplexValue", complexValue, auditor );
                        complexParameterValue.setValue( complexValue );
                        complexParameterValue.setParameter(
                                ( GenericParameter ) symbaEntityService.getLatestByEndurant(
                                        parameterKey ) );
                        DatabaseObjectHelper.save(
                                "net.sourceforge.fuge.common.protocol.ParameterValue", complexParameterValue,
                                auditor );
                        pvSet.add( complexParameterValue );
                    }
                }
                // look for atomic values
                if ( ( store.getGenericEquipmentInfo() ).get( equipmentKey ).getParameterAndAtomics() != null &&
                     !( store.getGenericEquipmentInfo() ).get( equipmentKey )
                             .getParameterAndAtomics()
                             .isEmpty() ) {
                    for ( String parameterKey : ( store.getGenericEquipmentInfo() ).get( equipmentKey )
                            .getParameterAndAtomics()
                            .keySet() ) {
                        ParameterValue parameterValue =
                                ( ParameterValue ) entityService.createDescribable(
                                        "net.sourceforge.fuge.common.protocol.ParameterValue" );
                        AtomicValue atomicValue = ( AtomicValue ) entityService.createDescribable(
                                "net.sourceforge.fuge.common.measurement.AtomicValue" );
                        atomicValue.setValue( ( store.getGenericEquipmentInfo() ).get( equipmentKey )
                                .getParameterAndAtomics().get( parameterKey ) );
                        DatabaseObjectHelper
                                .save( "net.sourceforge.fuge.common.measurement.AtomicValue", atomicValue, auditor );
                        parameterValue.setValue( atomicValue );
                        parameterValue.setParameter(
                                ( GenericParameter ) symbaEntityService.getLatestByEndurant(
                                        parameterKey ) );
                        DatabaseObjectHelper.save(
                                "net.sourceforge.fuge.common.protocol.ParameterValue", parameterValue,
                                auditor );
                        pvSet.add( parameterValue );
                    }
                }
                application.setParameterValues( pvSet );

                // add any descriptions. As this is a brand-new EA, there will be no existing descriptions
                Set<Description> descriptions = new HashSet<Description>();
                if ( ( store.getGenericEquipmentInfo() ).get( equipmentKey ).getFreeTextDescription() != null &&
                     ( store.getGenericEquipmentInfo() ).get( equipmentKey )
                             .getFreeTextDescription()
                             .length() > 0 ) {
                    Description description = ( Description ) entityService.createDescribable(
                            "net.sourceforge.fuge.common.description.Description" );
                    description.setText(
                            ( store.getGenericEquipmentInfo() ).get( equipmentKey ).getFreeTextDescription() );
                    DatabaseObjectHelper
                            .save( "net.sourceforge.fuge.common.description.Description", description, auditor );
                    descriptions.add( description );
                }
                application.setDescriptions( descriptions );

                // load equipment application into database
                DatabaseObjectHelper.save(
                        "net.sourceforge.fuge.common.protocol.EquipmentApplication",
                        application,
                        auditor );
                eaSet.add( application );

            }
            // add the list of EquipmentApplication objects to the assay protocol
            assayGPA.setEquipmentApplications( eaSet );
        }

        // add the materials: add all as input complete materials
        if ( store.getGenericProtocolApplicationInfo() != null ) {
            Set<Material> set2 = new HashSet<Material>();
            for ( MaterialFactorsStore mfs : store.getGenericProtocolApplicationInfo()
                    .get( assayGPA.getProtocol().getEndurant().getIdentifier() )
                    .getInputCompleteMaterialFactors() ) {
                if ( mfs.getCreatedMaterialEndurant() != null && mfs.getCreatedMaterialEndurant().length() > 0 ) {
                    set2.add( ( Material ) symbaEntityService
                            .getLatestByEndurant( mfs.getCreatedMaterialEndurant() ) );
                }
            }
            assayGPA.setInputCompleteMaterials( set2 );
        }
        // load into the database
        DatabaseObjectHelper.save(
                "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", assayGPA, auditor );

        if ( assayGPA.getOutputMaterials() != null &&
             !assayGPA.getOutputMaterials().isEmpty() ) {
            System.out.println( "Found Output Materials directly after loading (assay)" );
        } else {
            System.out.println( "Found No Output Materials directly after loading (assay)" );

        }
        return assayGPA;
    }

    private static GenericProtocolApplication createMtGPA( MaterialFactorsStore specimenFactors,
                                                           Protocol mtProtocol,
                                                           EntityService entityService,
                                                           Person auditor,
                                                           SymbaEntityService symbaEntityService ) {

        GenericProtocolApplication mtGpa =
                ( GenericProtocolApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                        "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", auditor );
        mtGpa.setName(
                "Material Transformation of " + specimenFactors.getMaterialName() );
        mtGpa.setProtocol( mtProtocol );
        // todo We should really ask them the Activity Date, but for now assume current date.
        mtGpa.setActivityDate( new java.sql.Timestamp( ( new Date() ).getTime() ) );

        // todo parameters and equipment are ignored in this GPA currently.

        // add the materials: the inputs are GMMs, and the outputs are normal Output materials.
        // the output has all variables, the input just has the descriptor set
        // In this special instance, there are two createdmaterialendurants, separated by "::" and
        // structured as inputmaterialendurant::outputmaterialendurant
        Set<Material> outputMaterials = new HashSet<Material>();
        Set<GenericMaterialMeasurement> inputMaterials = new HashSet<GenericMaterialMeasurement>();
        if ( specimenFactors.getCreatedMaterialEndurant() != null &&
             specimenFactors.getCreatedMaterialEndurant().length() > 0 ) {
            String[] parsedStrings = specimenFactors.getCreatedMaterialEndurant().split( "::" );

            GenericMaterialMeasurement gmm = ( GenericMaterialMeasurement ) entityService
                    .createDescribable( "net.sourceforge.fuge.bio.material.GenericMaterialMeasurement" );
            Material inputMaterial = ( Material ) symbaEntityService.getLatestByEndurant( parsedStrings[0] );
            gmm.setMeasuredMaterial( inputMaterial );
            gmm = ( GenericMaterialMeasurement ) DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.bio.material.GenericMaterialMeasurement", gmm, auditor );

            inputMaterials.add( gmm );

            outputMaterials.add( ( Material ) symbaEntityService
                    .getLatestByEndurant( parsedStrings[1] ) );
        }
        mtGpa.setInputMaterials( inputMaterials );
        mtGpa.setOutputMaterials( outputMaterials );

        // load into the database
        mtGpa = ( GenericProtocolApplication ) DatabaseObjectHelper.save(
                "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", mtGpa, auditor );

        return mtGpa;
    }

    /**
     * Contrary to createMtGpa or createAssayGpa (which both create bottom-level GPAs), this creates
     * a GPA with an action application pointing further on, but without any inputs or outputs.
     * Does not add the action application, therefore doesn't load into the database. THis allows you to load
     * as many action applications as you wish prior to saving in the database
     *
     * @param actionInformation holds the information required to create the new GPA.
     * @param entityService     the connection to the database
     * @param auditor           the person to assign as the creator of the objects in the database
     * @return the new gpa, not yet loaded in the database
     */
    private static GenericProtocolApplication createUpperGpa( ActionInformation actionInformation,
                                                              EntityService entityService,
                                                              Person auditor ) {

        GenericProtocolApplication gpa =
                ( GenericProtocolApplication ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                        "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", auditor );
        gpa.setName(
                "Performance of " + actionInformation.getParentProtocolName() );
        gpa.setProtocol(
                ( Protocol ) entityService.getIdentifiable( actionInformation.getParentProtocolIdentifier() ) );

        return gpa;
    }

    /**
     * Adds the specified ActionApplication, both to the database and to the gpaToAddActionApplication. However, it does
     * NOT any new version of that GPA into the database, as you may have multiple ActionApplications to add and there
     * is no reason to make excess database calls.
     *
     * @param gpaToBeAdded                the gpa to assign as an ActionApplication to gpaToAddActionApplication
     * @param actionAssociatedWithGpaToBeAdded
     *                                    the action that will be linked to the gpaToBeAdded
     * @param gpaToAddActionApplicationTo the gpa to have the ActionApplication added to
     * @param auditor                     the person to assign to the creation of the object in the database
     * @return the modified version of gpaToAddActionApplication, and null if gpaToAddActionApplication was null
     */
    private static GenericProtocolApplication addActionApplication( GenericProtocolApplication gpaToBeAdded,
                                                                    GenericAction actionAssociatedWithGpaToBeAdded,
                                                                    GenericProtocolApplication gpaToAddActionApplicationTo,
                                                                    Person auditor ) {

        // do NOTHING if they are sending a null GPA.
        if ( gpaToAddActionApplicationTo == null ) {
            return null;
        }

        // Add an ActionApplication. However, make sure we don't delete any existing ActionApplications by adding
        // to them rather than by creating a new collection.
        Set<ActionApplication> aaSet;
        if ( gpaToAddActionApplicationTo.getActionApplications() != null ) {
            aaSet = ( Set<ActionApplication> ) gpaToAddActionApplicationTo.getActionApplications();
        } else {
            aaSet = new HashSet<ActionApplication>();
        }

        ActionApplication application = ( ActionApplication ) DatabaseObjectHelper
                .createEndurantAndIdentifiable( "net.sourceforge.fuge.common.protocol.ActionApplication", auditor );
        application.setAction( actionAssociatedWithGpaToBeAdded );
        application.setChildProtocolApplication( gpaToBeAdded );
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.ActionApplication", application, auditor );
        aaSet.add( application );

        gpaToAddActionApplicationTo.setActionApplications( aaSet );

        return gpaToAddActionApplicationTo;
    }

}
