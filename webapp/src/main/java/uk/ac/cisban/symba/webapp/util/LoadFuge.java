package uk.ac.cisban.symba.webapp.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Data.ExternalData;
import fugeOM.Bio.Investigation.Investigation;
import fugeOM.Bio.Material.GenericMaterial;
import fugeOM.Bio.Material.Material;
import fugeOM.Collection.*;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Audit.Person;
import fugeOM.Common.Description.Description;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.OntologyIndividual;
import fugeOM.Common.Ontology.OntologySource;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import uk.ac.cisban.symba.backend.util.CisbanHelper;
import uk.ac.cisban.symba.backend.util.RetrieveSimple;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanOntologyCollectionHelper;
import uk.ac.cisban.symba.backend.util.conversion.helper.CisbanProtocolCollectionHelper;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */
public class LoadFuge {
    private final CisbanHelper helper;
    private final RealizableEntityService reService;
    private final ExperimentBean eb;
    private final InvestigationBean rdb;
    private final ScpBean scp;
    private final PersonBean pb;
    private Person auditor;

    /**
     * Creates a new instance of LoadFuge
     */
    public LoadFuge( ExperimentBean eb, InvestigationBean rdb, PersonBean pb, ScpBean scp ) {
        this.pb = pb;
        this.reService = pb.getReService();
        this.helper = CisbanHelper.create( reService );

        this.eb = eb;

        this.rdb = rdb;

        this.scp = scp;
    }

    public void load() throws IOException, LSIDException, RealizableEntityServiceException {

        if ( eb.getFuGE() == null ) {
            loadNew();
        } else {
            loadExisting();
        }
    }

    private void loadExisting() throws LSIDException, RealizableEntityServiceException, IOException {
        FuGE fuge = eb.getFuGE();
        fuge = loadPerson( fuge, true );
        fuge = loadMaterial( fuge );
        fuge = loadData( fuge );
        fuge = loadProtocols( fuge );
        // Before loading the fuge object into the database, reorganize the collections (only
        // needs to be done when the experiment already existed in the database and we are
        // about to load a new version of the experiment.
        fuge = helper.reorganizeCollections( fuge );
        loadExperiment( fuge );
    }

    private void loadNew() throws RealizableEntityServiceException, IOException, LSIDException {

        FuGE fuge = ( FuGE ) reService.createIdentifiableAndEndurantObs(
                helper.getLSID( "fugeOM.Collection.FuGE" ),
                eb.getExperimentName(),
                helper.getLSID( "fugeOM.Collection.FuGEEndurant" ),
                "fugeOM.Collection.FuGE",
                "fugeOM.Collection.FuGEEndurant" );
        fuge = loadPerson( fuge, false );
        fuge = loadInvestigation( fuge );
        fuge = loadMaterial( fuge );
        fuge = loadData( fuge );
        fuge = loadProtocols( fuge );
        fuge = loadProvider( fuge );
        loadExperiment( fuge );
    }

    private FuGE loadPerson( FuGE fuge,
                             boolean isExisting ) throws RealizableEntityServiceException, LSIDException {

        // Create the Audit Collection
        Set contacts;
        if ( !isExisting ) {
            contacts = new HashSet();
        } else {
            contacts = ( Set ) fuge.getAuditCollection().getAllContacts();
        }

        AuditCollection auditColl = ( AuditCollection ) reService.createDescribableOb(
                "fugeOM.Collection.AuditCollection" );

        Person person = ( Person ) helper.getOrCreateLatest(
                pb.getEndurantLsid(),
                "fugeOM.Common.Audit.PersonEndurant",
                null,
                "fugeOM.Common.Audit.Person" );

        auditor = person;

        // check if person already in the contact list
        if ( !isExisting ) {
            // We know that there is a single organization affiliated to each person
            // that we need to add to the experiment.
            Set affiliations = ( Set ) person.getAffiliations();
            if ( affiliations != null && !affiliations.isEmpty() ) {

                // And we can now add the latestOrg to the list of contacts
                contacts.add( affiliations.iterator().next() );
            }

            // And we can now add the person to the list of contacts
            contacts.add( person );
        } else {
            boolean matchFound = false;
            for ( Object obj : contacts ) {
                Identifiable iden = ( Identifiable ) obj;
                if ( iden.getEndurant()
                        .getIdentifier()
                        .equals( person.getEndurant().getIdentifier() ) ) {
                    matchFound = true;
                    break;
                }
            }
            if ( !matchFound ) {
                // And we can now add the person to the list of contacts
                contacts.add( person );
            }
        }

        // And now that we're finished adding contacts, we can add the list of contacts
        // to the audit collection...
        auditColl.setAllContacts( contacts );
        reService.createObInDB( "fugeOM.Collection.AuditCollection", auditColl );

        fuge.setAuditCollection( auditColl );

        return fuge;
    }

    // this method only applicable to new experiments
    private FuGE loadInvestigation( FuGE fuge ) throws RealizableEntityServiceException, LSIDException {
        InvestigationCollection investigationCollection = ( InvestigationCollection ) reService
                .createDescribableOb( "fugeOM.Collection.InvestigationCollection" );

        Set<Investigation> investigations = new HashSet<Investigation>();

        Set<Description> descriptions = new HashSet<Description>();
        Description description1 = ( Description ) reService.createDescribableOb(
                "fugeOM.Common.Description.Description" );
        Description description2 = ( Description ) reService.createDescribableOb(
                "fugeOM.Common.Description.Description" );
        if ( eb.getHypothesis() != null ) {
            description1.setText( "Hypothesis: " + eb.getHypothesis() );
        } else {
            description1.setText( "Hypothesis: None" );
        }
        reService.createObInDB( "fugeOM.Common.Description.Description", description1 );
        descriptions.add( description1 );
        if ( eb.getConclusion() != null ) {
            description2.setText( "Conclusions: " + eb.getConclusion() );
        } else {
            description2.setText( "Conclusions: None" );
        }
        reService.createObInDB( "fugeOM.Common.Description.Description", description2 );
        descriptions.add( description2 );

        // Retrieve latest investigation (or create a new one) from the database.
        Investigation investigation = ( Investigation ) reService.createIdentifiableAndEndurantObs(
                helper.getLSID( "fugeOM.Bio.Investigation.Investigation" ),
                "Investigation for " + eb.getExperimentName(),
                helper.getLSID( "fugeOM.Bio.Investigation.InvestigationEndurant" ),
                "fugeOM.Bio.Investigation.Investigation",
                "fugeOM.Bio.Investigation.InvestigationEndurant" );

        investigation.setName( "Investigation Details for " + eb.getExperimentName() );
        investigation.setDescriptions( descriptions );

        if ( investigation.getId() != null ) {
            // Assume this object has changed, assign a new LSID and getNewAuditTrail( person ), and load into the database
            helper.assignAndLoadIdentifiable( investigation, auditor, "fugeOM.Bio.Investigation.Investigation", null );
        } else {
            helper.loadIdentifiable( investigation, auditor, "fugeOM.Bio.Investigation.Investigation", null );
        }

        investigations.add( investigation );

        investigationCollection.setInvestigations( investigations );

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.InvestigationCollection", investigationCollection );
        fuge.setInvestigationCollection( investigationCollection );

        return fuge;
    }

    // Add a raw data file, and the external data that refers to it
    // We are always positive that these data files have not been loaded before.
    private FuGE loadData( FuGE fuge ) throws LSIDException, RealizableEntityServiceException, IOException {

        DataCollection dataCollection = ( DataCollection ) reService
                .createDescribableOb( "fugeOM.Collection.DataCollection" );

        Set<Data> datas;
        // this line ensures that, if there is any data in the collection, that it is retained.
        if ( fuge.getDataCollection() != null && !fuge.getDataCollection().getAllData().isEmpty() ) {
            datas = ( Set ) fuge.getDataCollection().getAllData();
        } else {
            datas = new HashSet();
        }

        int iii = 0;
        for ( RawDataInfoBean rdib : rdb.getAllDataBeans() ) {

            // we no longer store data within the database - instead, we store in a file store and use scp.

            /* Create a connection instance */
            Connection conn = new Connection( scp.getHostname() );
            conn.connect();

            boolean isAuthenticated = conn.authenticateWithPassword( scp.getUsername(), scp.getPassword() );

            if ( !isAuthenticated )
                throw new IOException( "Authentication failed." );

            String directoryForFile = scp.getDirectory() + "/data";
            Session sess = conn.openSession();
            sess.execCommand( "mkdir -p " + directoryForFile );
            sess.close();

            SCPClient scpClient = new SCPClient( conn );
            // "cisban." just there to get removed.
            String fileLSID = helper.getLSID( "cisban.RawData" );
            scpClient.put( rdib.getBarray(), fileLSID, directoryForFile );

            // make a link with the friendly identifier, to make them easier to find.
            sess = conn.openSession();
            sess.execCommand(
                    "ln -sf " + directoryForFile + "/" + fileLSID + " " + directoryForFile + "/" +
                            rdib.getFriendlyId() );
            sess.close();

            // Create a new external data object.
            ExternalData externalData = ( ExternalData ) reService.createIdentifiableAndEndurantObs(
                    helper.getLSID( "fugeOM.Bio.Data.ExternalData" ),
                    rdib.getFriendlyId(),
                    helper.getLSID( "fugeOM.Bio.Data.ExternalDataEndurant" ),
                    "fugeOM.Bio.Data.ExternalData",
                    "fugeOM.Bio.Data.ExternalDataEndurant" );
            externalData.setLocation( fileLSID );
            if ( rdib.getFileFormat() != null ) {

                // we will be updating the ontology collection.

                OntologyCollection ontologyCollection = ( OntologyCollection ) reService.createDescribableOb(
                        "fugeOM.Collection.OntologyCollection" );

                Set<OntologyTerm> ontologyTerms;
                Set<OntologySource> ontologySources;
                if ( fuge.getOntologyCollection() == null ) {
                    ontologyTerms = new HashSet<OntologyTerm>();
                    ontologySources = new HashSet<OntologySource>();
                } else {
                    if ( fuge.getOntologyCollection().getOntologyTerms() != null ) {
                        ontologyTerms = ( Set<OntologyTerm> ) fuge.getOntologyCollection().getOntologyTerms();
                    } else {
                        ontologyTerms = new HashSet<OntologyTerm>();
                    }
                    if ( fuge.getOntologyCollection().getOntologySources() != null ) {
                        ontologySources = ( Set<OntologySource> ) fuge.getOntologyCollection().getOntologySources();
                    } else {
                        ontologySources = new HashSet<OntologySource>();
                    }
                }
                // todo proper algorithm
                boolean matchFound = findMatchingEndurant( rdib.getFileFormat(), ontologyTerms );
                // irrespective of whether or not we found a match, we still need to add the term to the new material
                OntologyTerm termToAdd = ( OntologyTerm ) reService.findLatestByEndurant( rdib.getFileFormat() );

                externalData.setFileFormat( termToAdd );

                // todo this won't catch cases where the ontology source was added at a later date
                if ( !matchFound ) {
                    // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                    // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                    ontologyTerms.add( termToAdd );
                    if ( termToAdd.getOntologySource() != null ) {
                        ontologySources.add(
                                ( OntologySource ) reService.findLatestByEndurant(
                                        termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                    }
                    ontologyCollection.setOntologyTerms( ontologyTerms );
                    ontologyCollection.setOntologySources( ontologySources );

                    // load the fuge object into the database
                    reService.createObInDB( "fugeOM.Collection.OntologyCollection", ontologyCollection );
                    fuge.setOntologyCollection( ontologyCollection );
                }
            }

            // load the external data into the database
            helper.loadIdentifiable( externalData, auditor, "fugeOM.Bio.Data.ExternalData", System.out );

            rdib.setEndurantLsid( externalData.getEndurant().getIdentifier() );
            rdb.setDataItem( rdib, iii );

            datas.add( externalData );
            iii++;
        }

        dataCollection.setAllData( datas );

        if ( fuge.getDataCollection() != null ) {
            dataCollection.setAllDataPartitions( fuge.getDataCollection().getAllDataPartitions() );
            dataCollection.setAllDimensions( fuge.getDataCollection().getAllDimensions() );
            dataCollection.setHigherLevelAnalyses( fuge.getDataCollection().getHigherLevelAnalyses() );
        }

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.DataCollection", dataCollection );
        fuge.setDataCollection( dataCollection );

        return fuge;
    }

    // for now, a new GenericMaterial each time.
    private FuGE loadMaterial( FuGE fuge ) throws RealizableEntityServiceException, LSIDException {

        OntologyCollection ontologyCollection = ( OntologyCollection ) reService.createDescribableOb(
                "fugeOM.Collection.OntologyCollection" );

        Set<OntologyTerm> ontologyTerms;
        Set<OntologySource> ontologySources;
        if ( fuge.getOntologyCollection() == null ) {
            ontologyTerms = new HashSet<OntologyTerm>();
            ontologySources = new HashSet<OntologySource>();
        } else {
            if ( fuge.getOntologyCollection().getOntologyTerms() != null ) {
                ontologyTerms = ( Set<OntologyTerm> ) fuge.getOntologyCollection().getOntologyTerms();
            } else {
                ontologyTerms = new HashSet<OntologyTerm>();
            }
            if ( fuge.getOntologyCollection().getOntologySources() != null ) {
                ontologySources = ( Set<OntologySource> ) fuge.getOntologyCollection().getOntologySources();
            } else {
                ontologySources = new HashSet<OntologySource>();
            }
        }

        MaterialCollection materialCollection = ( MaterialCollection ) reService
                .createDescribableOb( "fugeOM.Collection.MaterialCollection" );

        Set<Material> materials;
        // this line ensures that, if there is any material in the collection, that it is retained.
        if ( fuge.getMaterialCollection() != null && !fuge.getMaterialCollection().getMaterials().isEmpty() ) {
            materials = ( Set<Material> ) fuge.getMaterialCollection().getMaterials();
        } else {
            materials = new HashSet<Material>();
        }

        // each rdib instance has its own material
        boolean hasMaterial = false;
        int iii = 0;
        for ( RawDataInfoBean rdib : rdb.getAllDataBeans() ) {
            if ( rdib.getMaterialFactorsBean() != null && rdib.getMaterialFactorsBean().getMaterialType() != null ) {
                hasMaterial = true;
                // the material needs to be made, and each ontology term needs to be added if it hasn't already been added
                GenericMaterial genericMaterial = ( GenericMaterial ) reService.createIdentifiableAndEndurantObs(
                        helper.getLSID(
                                "fugeOM.Bio.Material.GenericMaterial" ),
                        rdib.getMaterialFactorsBean().getMaterialName(),
                        helper.getLSID( "fugeOM.Bio.Material.GenericMaterialEndurant" ),
                        "fugeOM.Bio.Material.GenericMaterial",
                        "fugeOM.Bio.Material.GenericMaterialEndurant" );

                // The first ontology term is the materialType

                // todo proper algorithm
                boolean matchFound = findMatchingEndurant(
                        rdib.getMaterialFactorsBean().getMaterialType(), ontologyTerms );
                // irrespective of whether or not we found a match, we still need to add the term to the new material
                OntologyTerm termToAdd = ( OntologyTerm ) reService.findLatestByEndurant( rdib.getMaterialFactorsBean().getMaterialType() );
                genericMaterial.setMaterialType( termToAdd );
                // todo this won't catch cases where the ontology source was added at a later date
                if ( !matchFound ) {
                    // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                    // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                    ontologyTerms.add( termToAdd );
                    if ( termToAdd.getOntologySource() != null ) {
                        ontologySources.add(
                                ( OntologySource ) reService.findLatestByEndurant(
                                        termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                    }
                }

                // Now we have the characteristics to load.
                Set<OntologyTerm> characteristics;
                if ( genericMaterial.getCharacteristics() == null ) {
                    characteristics = new HashSet<OntologyTerm>();
                } else {
                    characteristics = ( Set<OntologyTerm> ) genericMaterial.getCharacteristics();
                }
                if ( rdib.getMaterialFactorsBean().getCharacteristics() != null ) {
                    for ( String endurant : rdib.getMaterialFactorsBean().getCharacteristics() ) {
                        // todo proper algorithm
                        matchFound = findMatchingEndurant( endurant, ontologyTerms );
                        // irrespective of whether or not we found a match, we still need to add the term to the new material
                        termToAdd = ( OntologyTerm ) reService.findLatestByEndurant( endurant );
                        characteristics.add( termToAdd );
                        // todo this won't catch cases where the ontology source was added at a later date
                        if ( !matchFound ) {
                            // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                            // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                            ontologyTerms.add( termToAdd );
                            if ( termToAdd.getOntologySource() != null ) {
                                ontologySources.add(
                                        ( OntologySource ) reService.findLatestByEndurant(
                                                termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                            }
                        }
                    }
                }
                genericMaterial.setCharacteristics( characteristics );

                // Next are the treatments, which get loaded as individual descriptions on the GenericMaterial.
                // These are always added, without checks.
                Set<Description> descriptions;
                if ( genericMaterial.getDescriptions() == null ) {
                    descriptions = new HashSet<Description>();
                } else {
                    descriptions = ( Set<Description> ) genericMaterial.getDescriptions();
                }
                if ( rdib.getMaterialFactorsBean().getTreatmentInfo() != null ) {
                    for ( String treatmentDesc : rdib.getMaterialFactorsBean().getTreatmentInfo() ) {
                        Description description = ( Description ) reService.createDescribableOb(
                                "fugeOM.Common.Description.Description" );
                        description.setText( "Treatment: " + treatmentDesc );
                        reService.createObInDB( "fugeOM.Common.Description.Description", description );
                        descriptions.add( description );
                    }
                }
                genericMaterial.setDescriptions( descriptions );

                // a final step for this rdib material info: add the material to the list of materials after storing
                helper.loadIdentifiable( genericMaterial, auditor, "fugeOM.Bio.Material.GenericMaterial", System.out );

                MaterialFactorsBean mfb = rdib.getMaterialFactorsBean();
                mfb.setCreatedMaterial( genericMaterial.getEndurant().getIdentifier() );
                rdib.setMaterialFactorsBean( mfb );
                rdb.setDataItem( rdib, iii );

                materials.add( genericMaterial );
            }
            iii++;
        }

        // if there has been at least one material, then we need to load the updated ontology terms into the ontology collection
        if ( hasMaterial ) {
            ontologyCollection.setOntologyTerms( ontologyTerms );
            ontologyCollection.setOntologySources( ontologySources );

            // load the fuge object into the database
            reService.createObInDB( "fugeOM.Collection.OntologyCollection", ontologyCollection );
            fuge.setOntologyCollection( ontologyCollection );

            // load the collection object into the database if we've added materials
            materialCollection.setMaterials( materials );
            reService.createObInDB( "fugeOM.Collection.MaterialCollection", materialCollection );
            fuge.setMaterialCollection( materialCollection );
            System.out.println( "Materials and Material Collection loaded in the database" );
        }

        return fuge;
    }

    // toSearch must be identifiable
    private boolean findMatchingEndurant( String endurant, Set toSearch ) {
        for ( Object obj : toSearch ) {
            Identifiable iden = ( Identifiable ) obj;
            if ( iden.getEndurant().getIdentifier().equals( endurant ) )
                return true;
        }
        return false;
    }

    private void loadExperiment( FuGE fuge ) throws LSIDException, RealizableEntityServiceException {
        // Load the entire fuge entry into the database
        if ( fuge.getId() != null ) {
            // Assume this object has changed, assign a new LSID and getNewAuditTrail( person ), and load into the database
            helper.assignAndLoadIdentifiable( fuge, auditor, "fugeOM.Collection.FuGE", null );
        } else {
            helper.loadIdentifiable( fuge, auditor, "fugeOM.Collection.FuGE", null );
        }
    }

    // the default protocol loader
    // if not Microarray protocol, rdib.getProtocolEndurant() does not get filled.
    // Also assumes a flat structure of only two levels:
    // all names must include the rdb.getTopLevelProtocolName() in order to be found
    // first-level protocol contains a full list of steps
    // second-level protocols are those referenced in the top-level protocol, containing the word "Component", and themselves have no actions
    private FuGE loadProtocols( FuGE fuge ) throws RealizableEntityServiceException, LSIDException {

        ProtocolCollection protocolCollection = ( ProtocolCollection ) reService
                .createDescribableOb( "fugeOM.Collection.ProtocolCollection" );

        // Now we need to associate the Data with the appropriate protocol applications and protocols.
        // We know which factors these are for because we don't give
        // the user the choice of protocols, but instead the choice of Actions available.

        CisbanProtocolCollectionHelper cpc = new CisbanProtocolCollectionHelper(
                reService, new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) ) );
        // The GenericProtocol whose GenericAction is stored from the form will get the data item added to it.
        Set<Protocol> protocolSet = cpc.addRelevantProtocols( fuge, rdb.getTopLevelProtocolIdentifier().trim() );

        // the only thing we cannot figure out at runtime currently is which are "complex" protocols
        // and which are "simple" protocols. This is why there is hard-coding here.
        boolean treatSecondLevelAsAssay = false;
        GenericProtocol assayProtocol = null;
        // All Microarray Experiments are complex, and others are simple (currently)
        if ( rdb.getTopLevelProtocolName().contains( "Example mutant/WT Microarray Investigation" ) ) {
            // The next two are "real" CISBAN protocols, but this one is the particular example for the sandbox
            assayProtocol = ( GenericProtocol ) reService.findLatestByEndurant(
                    "urn:lsid:cisban.cisbs.org:GenProtocolEndurant:71fbd89e-13c7-4ced-9d0d-199abf9956f2" );
            protocolSet.add( assayProtocol );
        } else {
            treatSecondLevelAsAssay = true;
        }

        protocolCollection.setProtocols( protocolSet );

        // Now retrieve all equipment associated with these protocols
        protocolCollection.setAllEquipment(
                cpc.addRelevantEquipment(
                        fuge, ( Set<Protocol> ) protocolCollection.getProtocols() ) );

        // for each data file that has just been added, it must be assigned to the appropriate GenericAction
        // from the factor Protocol
        Set allGPAs;
        if ( fuge.getProtocolCollection() != null ) {
            allGPAs = ( Set ) fuge.getProtocolCollection().getAllProtocolApps();
        } else {
            allGPAs = new HashSet();
        }
        String expName;
        if ( eb.getExperimentName() != null ) {
            expName = eb.getExperimentName();
        } else {
            expName = fuge.getName();
        }
        for ( RawDataInfoBean rdib : rdb.getAllDataBeans() ) {
            GenericAction actionSelectedFromFirstLevel = ( GenericAction ) reService.findLatestByEndurant( rdib.getActionEndurant() );
            GenericAction actionSelectedFromSecondLevel = null;
            if ( !treatSecondLevelAsAssay ) {
                actionSelectedFromSecondLevel = ( GenericAction ) reService.findLatestByEndurant( rdib.getFactorChoice() );
            }

            // first, find the containing protocols for each action described.
            GenericProtocol firstLevelParentProtocol = null, secondLevelParentProtocol = null;
            for ( Object obj2 : protocolCollection.getProtocols() ) {
                if ( obj2 instanceof GenericProtocol ) {
                    GenericProtocol gp = ( GenericProtocol ) obj2;
                    if ( gp.getGenericActions() != null ) {
                        for ( Object obj3 : gp.getGenericActions() ) {
                            GenericAction ga = ( GenericAction ) obj3;
                            if ( ga.getEndurant()
                                    .getIdentifier()
                                    .equals( actionSelectedFromFirstLevel.getEndurant().getIdentifier() ) ) {
                                firstLevelParentProtocol = gp;
                            } else if ( !treatSecondLevelAsAssay && ga.getEndurant()
                                    .getIdentifier()
                                    .equals( actionSelectedFromSecondLevel.getEndurant().getIdentifier() ) ) {
                                secondLevelParentProtocol = gp;
                            }

                        }
                    }
                }
            }
            // there is a problem if there isn't a matching protocol
            if ( firstLevelParentProtocol == null ) {
                System.err.println(
                        "Error finding parent protocol for first-level action endurant " +
                                rdib.getActionEndurant() );
                return fuge;
            }

            // there is a problem if there isn't a matching protocol and we aren't treating the second level as an assay
            if ( !treatSecondLevelAsAssay && secondLevelParentProtocol == null ) {
                System.err.println(
                        "Error finding parent protocol and not treating second level as an assay for first-level action endurant " +
                                rdib.getFactorChoice() );
                return fuge;
            }

            // Next, we need to check and see if a GPA is already present for these two levels.
            // Look for a GPA whose protocol reference contains one of these two actions.
            GenericProtocolApplication gpaOfFirstLevelParentProtocol = null;
            boolean gpaOfFirstLevelParentProtocolMatch = false;
            if ( allGPAs != null ) {
                for ( Object obj2 : allGPAs ) {
                    if ( obj2 instanceof GenericProtocolApplication ) {
                        GenericProtocolApplication gpa = ( GenericProtocolApplication ) obj2;
                        if ( gpa.getGenericProtocol()
                                .getEndurant()
                                .getIdentifier()
                                .equals( firstLevelParentProtocol.getEndurant().getIdentifier() ) ) {
                            gpaOfFirstLevelParentProtocol = gpa;
                            gpaOfFirstLevelParentProtocolMatch = true;
                            break;
                        }
                    }
                }
            }

            // if there is a gpaOfFirstLevelParentProtocol, then we need to see if there is already an appropriate
            // second-level GPA present. It is appropriate if the AA of gpaOfFirstLevelParentProtocol contains a
            // GPA Reference whose Protocol_ref is the secondLevelParentProtocol and the corresponding Action_ref is
            // actionSelectedFromFirstLevel
            boolean gpaOfSecondLevelParentProtocolMatch = false;
            GenericProtocolApplication gpaOfSecondLevelParentProtocol = null;
            if ( !treatSecondLevelAsAssay ) {
                if ( gpaOfFirstLevelParentProtocolMatch ) {
                    for ( Object obj2 : gpaOfFirstLevelParentProtocol.getActionApplications() ) {
                        ActionApplication actionApplication = ( ActionApplication ) obj2;
                        ProtocolApplication referencedGPA = actionApplication.getProtAppRef();
                        if ( referencedGPA instanceof GenericProtocolApplication ) {
                            GenericProtocolApplication gpa = ( GenericProtocolApplication ) referencedGPA;
                            if ( gpa.getGenericProtocol()
                                    .getEndurant()
                                    .getIdentifier()
                                    .equals( secondLevelParentProtocol.getEndurant().getIdentifier() ) ) {
                                // this is a possible choice. Also check the GA attached to this AA is the
                                // actionSelectedFromFirstLevel
                                if ( actionApplication.getAction()
                                        .getEndurant()
                                        .getIdentifier()
                                        .equals( actionSelectedFromFirstLevel.getEndurant().getIdentifier() ) ) {
                                    gpaOfSecondLevelParentProtocol = gpa;
                                    gpaOfSecondLevelParentProtocolMatch = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // If the appropriate gpas are not found, we should create it now.
            if ( !gpaOfFirstLevelParentProtocolMatch ) {
                gpaOfFirstLevelParentProtocol = ( GenericProtocolApplication ) reService.createIdentifiableAndEndurantObs(
                        helper.getLSID( "fugeOM.Common.Protocol.GenericProtocolApplication" ),
                        "Actual Application/Performance of " + firstLevelParentProtocol.getName() +
                                " for Investigation " +
                                expName,
                        helper.getLSID( "fugeOM.Common.Protocol.GenPrtclAppEndurant" ),
                        "fugeOM.Common.Protocol.GenericProtocolApplication",
                        "fugeOM.Common.Protocol.GenPrtclAppEndurant" );
                gpaOfFirstLevelParentProtocol.setGenericProtocol( firstLevelParentProtocol );
                // We should really ask them the Activity Date, but for now assume current date.
                gpaOfFirstLevelParentProtocol.setActivityDate( new Date() );
            }

            // If the appropriate gpa is not found, we should create it now UNLESS
            // we are already at the level of the assay protocol
            // in some simpler cases, the gpaOfSecondLevelParentProtocol is also the "assay" GPA, or the one
            // where the output data should be set. In this case, we should ALWAYS
            // create a new GPA at this step. In more complex cases, there
            // is an Assay gpa in addition to the factor GPA. Currently, we have to
            // hard-code which ones have this extra step.
            if ( treatSecondLevelAsAssay || !gpaOfSecondLevelParentProtocolMatch ) {
                gpaOfSecondLevelParentProtocol = ( GenericProtocolApplication ) reService.createIdentifiableAndEndurantObs(
                        helper.getLSID( "fugeOM.Common.Protocol.GenericProtocolApplication" ),
                        "Actual Application/Performance of " +
                                actionSelectedFromFirstLevel.getGenProtocolRef().getName() +
                                " for Experiment " +
                                expName,
                        helper.getLSID( "fugeOM.Common.Protocol.GenPrtclAppEndurant" ),
                        "fugeOM.Common.Protocol.GenericProtocolApplication",
                        "fugeOM.Common.Protocol.GenPrtclAppEndurant" );
                gpaOfSecondLevelParentProtocol.setGenericProtocol( actionSelectedFromFirstLevel.getGenProtocolRef() );
                // We should really ask them the Activity Date, but for now assume current date.
                gpaOfSecondLevelParentProtocol.setActivityDate( new Date() );
            }

            // if we are treating the factor GPA as an assay, then add the output here
            // and load into the database.
            if ( treatSecondLevelAsAssay ) {
                // add the data, using a temporary set as it expects a collection.
                Set<ExternalData> set = new HashSet<ExternalData>();
                set.add( ( ExternalData ) reService.findLatestByEndurant( rdib.getEndurantLsid() ) );

                // add the output data
                gpaOfSecondLevelParentProtocol.setGenericOutputData( set );

                // add any deviations from the parameter values, if the associated action has any parameters.
                // Currently only set up to store one parameter change, and therefore we know that if there
                // is a new parameter (rdib.getAtomicValue()) then it goes in this GPA.
                // todo currently only assumes that there will be only one parameter total, if we need to change parameters
                if ( rdib.getAtomicValue() != null && rdib.getAtomicValue().length() > 0 ) {
                    Set<AtomicParameterValue> pvSet = new HashSet<AtomicParameterValue>();
                    AtomicParameterValue atomicParameterValue = ( AtomicParameterValue ) reService.createDescribableOb(
                            "fugeOM.Common.Protocol.AtomicParameterValue" );
                    atomicParameterValue.setValue( rdib.getAtomicValue() );
                    atomicParameterValue.setParameter( ( GenericParameter ) reService.findIdentifiable( rdib.getAtomicValueIdentifier() ) );
                    reService.createObInDB( "fugeOM.Common.Protocol.AtomicParameterValue", atomicParameterValue );
                    pvSet.add( atomicParameterValue );
                    gpaOfSecondLevelParentProtocol.setParameterValues( pvSet );
                }

                // Print out the types of material present here
                if ( gpaOfSecondLevelParentProtocol.getGenericOutputMaterials() != null &&
                        !gpaOfSecondLevelParentProtocol.getGenericOutputMaterials().isEmpty() ) {
                    System.out.println( "Found Output Materials directly before adding GICM" );
                } else {
                    System.out.println( "Found No Output Materials directly before adding GICM" );

                }

                // add the material
                if ( rdib.getMaterialFactorsBean() != null ) {
                    // add the material
                    Set set2 = new HashSet();
                    set2.add( reService.findLatestByEndurant( rdib.getMaterialFactorsBean().getCreatedMaterial() ) );
                    System.out.println( "Received lsid: " + rdib.getMaterialFactorsBean().getCreatedMaterial() );
                    gpaOfSecondLevelParentProtocol.setGenericInputCompleteMaterials( set2 );
                }

                // Print out the types of material present here
                if ( gpaOfSecondLevelParentProtocol.getGenericOutputMaterials() != null &&
                        !gpaOfSecondLevelParentProtocol.getGenericOutputMaterials().isEmpty() ) {
                    System.out.println( "Found Output Materials directly before loading" );
                } else {
                    System.out.println( "Found No Output Materials directly before loading" );

                }

                // load in database
                helper.loadIdentifiable(
                        gpaOfSecondLevelParentProtocol,
                        auditor,
                        "fugeOM.Common.Protocol.GenericProtocolApplication",
                        System.out );

                // Print out the types of material present here
                if ( gpaOfSecondLevelParentProtocol.getGenericOutputMaterials() != null &&
                        !gpaOfSecondLevelParentProtocol.getGenericOutputMaterials().isEmpty() ) {
                    System.out.println( "Found Output Materials directly after loading" );
                } else {
                    System.out.println( "Found No Output Materials directly after loading" );

                }

                // Now we can create an ActionApplication in the GPA of the first-level parent protocol that is associated with
                // the newly created assay GPA. However, make sure we don't delete any existing ActionApplications by
                // adding to them rather than by creating a new collection.

                Set aaSet = ( Set ) gpaOfFirstLevelParentProtocol.getActionApplications();

                ActionApplication application = ( ActionApplication ) reService
                        .createDescribableOb( "fugeOM.Common.Protocol.ActionApplication" );
                application.setAction( actionSelectedFromFirstLevel );
                application.setProtAppRef( gpaOfSecondLevelParentProtocol );
                reService.createObInDB( "fugeOM.Common.Protocol.ActionApplication", application );
                aaSet.add( application );

                gpaOfFirstLevelParentProtocol.setActionApplications( aaSet );

                // If the GPA of the first-level parent protocol is new, add to database. Otherwise, put a new version in.
                if ( gpaOfFirstLevelParentProtocol.getId() != null ) {
                    // Assume this object has changed, assign a new LSID, and load into the database
                    helper.assignAndLoadIdentifiable(
                            gpaOfFirstLevelParentProtocol,
                            auditor,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                } else {
                    helper.loadIdentifiable(
                            gpaOfFirstLevelParentProtocol,
                            auditor,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                }
                // associate the new assay with the protocol collection.
                allGPAs.add( gpaOfSecondLevelParentProtocol );

            } else {
                // Otherwise, we now need to create a separate assay gpa.
                GenericProtocolApplication assayGPA = ( GenericProtocolApplication ) reService.createIdentifiableAndEndurantObs(
                        helper.getLSID( "fugeOM.Common.Protocol.GenericProtocolApplication" ),
                        "Actual Application/Performance of a " + assayProtocol.getName(),
                        helper.getLSID( "fugeOM.Common.Protocol.GenPrtclAppEndurant" ),
                        "fugeOM.Common.Protocol.GenericProtocolApplication",
                        "fugeOM.Common.Protocol.GenPrtclAppEndurant" );
                assayGPA.setGenericProtocol( assayProtocol );
                // We should really ask them the Activity Date, but for now assume current date.
                assayGPA.setActivityDate( new Date() );

                // add the data
                Set set = new HashSet();
                set.add( reService.findLatestByEndurant( rdib.getEndurantLsid() ) );
                assayGPA.setGenericOutputData( set );

                // add any deviations from the parameter values, if the associated action has any parameters.
                // Currently only set up to store one parameter change, and therefore we know that if there
                // is a new parameter (rdib.getAtomicValue()) then it goes in this GPA.
                // todo currently only assumes that there will be only one parameter total, if we need to change parameters
                if ( rdib.getAtomicValue() != null && rdib.getAtomicValue().length() > 0 ) {
                    Set<AtomicParameterValue> pvSet = new HashSet<AtomicParameterValue>();
                    AtomicParameterValue atomicParameterValue = ( AtomicParameterValue ) reService.createDescribableOb(
                            "fugeOM.Common.Protocol.AtomicParameterValue" );
                    atomicParameterValue.setValue( rdib.getAtomicValue() );
                    atomicParameterValue.setParameter( ( GenericParameter ) reService.findIdentifiable( rdib.getAtomicValueIdentifier() ) );
                    reService.createObInDB( "fugeOM.Common.Protocol.AtomicParameterValue", atomicParameterValue );
                    pvSet.add( atomicParameterValue );
                    assayGPA.setParameterValues( pvSet );
                }

                // add the material
                if ( rdib.getMaterialFactorsBean() != null ) {
                    // add the material
                    Set set2 = new HashSet();
                    set2.add( reService.findLatestByEndurant( rdib.getMaterialFactorsBean().getCreatedMaterial() ) );
                    assayGPA.setGenericInputCompleteMaterials( set2 );
                }
                // load into the database
                helper.loadIdentifiable(
                        assayGPA, auditor, "fugeOM.Common.Protocol.GenericProtocolApplication", null );

                if ( assayGPA.getGenericOutputMaterials() != null &&
                        !assayGPA.getGenericOutputMaterials().isEmpty() ) {
                    System.out.println( "Found Output Materials directly after loading (assay)" );
                } else {
                    System.out.println( "Found No Output Materials directly after loading (assay)" );

                }

                // Now we can create an ActionApplication in the GPA of the second-level parent protocol that
                // is associated with the assay GPA. However, make sure we don't delete any existing ActionApplications
                // by adding to them rather than by creating a new collection.
                Set aaSet = ( Set ) gpaOfSecondLevelParentProtocol.getActionApplications();

                ActionApplication application = ( ActionApplication ) reService
                        .createDescribableOb( "fugeOM.Common.Protocol.ActionApplication" );
                application.setAction( actionSelectedFromSecondLevel );
                application.setProtAppRef( assayGPA );
                reService.createObInDB( "fugeOM.Common.Protocol.ActionApplication", application );
                aaSet.add( application );

                gpaOfSecondLevelParentProtocol.setActionApplications( aaSet );

                // If new, add to database. Otherwise, put a new version in.
                if ( gpaOfSecondLevelParentProtocol.getId() != null ) {
                    // Assume this object has changed, assign a new LSID, and load into the database
                    helper.assignAndLoadIdentifiable(
                            gpaOfSecondLevelParentProtocol,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                } else {
                    helper.loadIdentifiable(
                            gpaOfSecondLevelParentProtocol,
                            auditor,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                }
                // Now we can create an ActionApplication in the GPA of the first-level parent protocol that is associated with
                // the GPA of the second-level parent protocol. However, make sure we don't delete any existing
                // ActionApplications by adding to them rather than by creating a new collection.
                // However, there is one existing AA that we would want to remove: the one that may be about to be
                // replaced. Any AA of the completeGPA gets removed if it has an identical completeAction.

                Set bbSet = new HashSet();
                for ( Object aaObj : gpaOfFirstLevelParentProtocol.getActionApplications() ) {
                    ActionApplication aa = ( ActionApplication ) aaObj;
                    if ( !aa.getAction()
                            .getEndurant()
                            .getIdentifier()
                            .equals( actionSelectedFromFirstLevel.getEndurant().getIdentifier() ) ) {
                        bbSet.add( aa );
                    }
                }

                ActionApplication holdingApplication = ( ActionApplication ) reService
                        .createDescribableOb( "fugeOM.Common.Protocol.ActionApplication" );
                holdingApplication.setAction( actionSelectedFromFirstLevel );
                // to find the correct generic action, we search through the generic actions, finding the one whose
                // strain choice matches.
                holdingApplication.setProtAppRef( gpaOfSecondLevelParentProtocol );
                reService.createObInDB( "fugeOM.Common.Protocol.ActionApplication", holdingApplication );
                bbSet.add( holdingApplication );

                gpaOfFirstLevelParentProtocol.setActionApplications( bbSet );
                // If the time series is new, add to database. Otherwise, put a new version in.
                if ( gpaOfFirstLevelParentProtocol.getId() != null ) {
                    // Assume this object has changed, assign a new LSID, and load into the database
                    helper.assignAndLoadIdentifiable(
                            gpaOfFirstLevelParentProtocol,
                            auditor,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                } else {
                    helper.loadIdentifiable(
                            gpaOfFirstLevelParentProtocol,
                            auditor,
                            "fugeOM.Common.Protocol.GenericProtocolApplication",
                            null );
                }

                // associate the new assays with the protocol collection.
                allGPAs.add( assayGPA );

                // Finally, associate the new factor with the protocol collection. If it was already
                // in the database, then loading the new GPA will put a new version in with the same
                // endurant, and the update will be noted when viewing the experiment.
                if ( !gpaOfSecondLevelParentProtocolMatch ) {
                    allGPAs.add( gpaOfSecondLevelParentProtocol );
                }
            }

            // Finally, associate the new holding gpa with the protocol collection. If it was already
            // in the database, then loading the new GPA will put a new version in with the same
            // endurant, and the update will be noted when viewing the experiment.
            if ( !gpaOfFirstLevelParentProtocolMatch ) {
                allGPAs.add( gpaOfFirstLevelParentProtocol );
            }
        }
        protocolCollection.setAllProtocolApps( allGPAs );
//        System.err.println( "ABOUT TO PRINT OUT PROTOCOL COLLECTION MATERIAL INFO DIRECTLY AFTER SETTING GPAs" );
//        RetrieveSimple retrieveSimple = new RetrieveSimple();
//        try {
//            retrieveSimple.runSimpleRetrieval( reService, protocolCollection );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            System.err.println( "Error trying to print out the material contents of the fuge protocol collection." );
//        }

        if ( fuge.getProtocolCollection() != null ) {
            protocolCollection.setAllSoftwares( fuge.getProtocolCollection().getAllSoftwares() );
        }

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.ProtocolCollection", protocolCollection );
        fuge.setProtocolCollection( protocolCollection );

//        System.err.println( "ABOUT TO PRINT OUT PROTOCOL COLLECTION MATERIAL INFO DIRECTLY AFTER LOADING INTO DB" );
//        try {
//            retrieveSimple.runSimpleRetrieval( reService, fuge.getProtocolCollection() );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            System.err.println( "Error trying to print out the material contents of the fuge protocol collection." );
//        }

        CisbanOntologyCollectionHelper och = new CisbanOntologyCollectionHelper(
                reService, new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) ) );
        fuge = och.addRelevantOntologyTerms( fuge );

        return fuge;
    }

    private String removeDummyString( String name ) {
        return name.substring( 0, name.indexOf( " Dummy" ) ) + name.substring( name.indexOf( " Dummy" ) + 6 );
    }

    // this method assumes that the experiment is new, and not existing already in the database.
    private FuGE loadProvider
            ( FuGE
                    fuge ) throws RealizableEntityServiceException, LSIDException {
        // We will assign the current Person to be the Provider. The link between the Provider
        // and the Person is called a ContactRole.

        // first, create the role
        ContactRole contactRole = ( ContactRole ) reService.createDescribableOb( "fugeOM.Common.Audit.ContactRole" );
        contactRole.setContact( auditor );

        // the provider contains an ontology term, so we need to add that term to the experiment
        OntologyCollection ontologyCollection = ( OntologyCollection ) reService.createDescribableOb(
                "fugeOM.Collection.OntologyCollection" );
        Set ontologyTerms;
        if ( fuge.getOntologyCollection() != null ) {
            ontologyTerms = ( Set ) fuge.getOntologyCollection().getOntologyTerms();
        } else {
            ontologyTerms = new HashSet();
        }
        // then create an ontology term to identify the role, or load the most recent version of that term
        OntologyIndividual ontologyIndividual = ( OntologyIndividual ) reService.createIdentifiableAndEndurantObs(
                helper.getLSID( "fugeOM.Common.Ontology.OntologyIndividual" ),
                null,
                helper.getLSID( "fugeOM.Common.Ontology.OntoIndvEndurant" ),
                "fugeOM.Common.Ontology.OntologyIndividual",
                "fugeOM.Common.Ontology.OntoIndvEndurant" );

        ontologyIndividual.setName( "Principal Investigator" );
        ontologyIndividual.setTerm( "Principal Investigators" );
        ontologyIndividual.setTermAccession( "accession to come" );

        // and then load that term into the database
        helper.loadIdentifiable( ontologyIndividual, auditor, "fugeOM.Common.Ontology.OntologyIndividual", null );

        // add the ontology term to the list of terms.
        ontologyTerms.add( ontologyIndividual );
        // then set that ontology term in the role
        contactRole.setRole( ontologyIndividual );
        ontologyCollection.setOntologyTerms( ontologyTerms );

        if ( fuge.getOntologyCollection() != null ) {
            ontologyCollection.setOntologySources( fuge.getOntologyCollection().getOntologySources() );
        }
        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.OntologyCollection", ontologyCollection );
        fuge.setOntologyCollection( ontologyCollection );

        //  load the role into the database
        reService.createObInDB( "fugeOM.Common.Audit.ContactRole", contactRole );

        Provider provider = ( Provider ) reService.createIdentifiableAndEndurantObs(
                helper.getLSID( "fugeOM.Collection.Provider" ),
                null,
                helper.getLSID( "fugeOM.Collection.ProviderEndurant" ),
                "fugeOM.Collection.Provider",
                "fugeOM.Collection.ProviderEndurant" );

        // set any part of the provider that we are interested in.
        provider.setName( "Main Provider of Experiment: " + eb.getExperimentName() );
        provider.setProvider( contactRole );

        // load the provider into the database
        helper.loadIdentifiable( provider, auditor, "fugeOM.Collection.Provider", null );
        // add the role to the provider
        fuge.setProvider( provider );
        // ..and add the ontology collection to the experiment
        fuge.getOntologyCollection().setOntologyTerms( ontologyTerms );

        return fuge;
    }
}
