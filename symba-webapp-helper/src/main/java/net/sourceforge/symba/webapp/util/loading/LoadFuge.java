package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.symba.webapp.util.storage.SyMBADataCopier;
import net.sourceforge.symba.webapp.util.storage.SyMBADataCopierFactory;
import net.sourceforge.symba.webapp.util.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.ProtocolCollectionMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.OntologyCollectionMappingHelper;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.audit.Contact;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.Identifiable;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.fuge.collection.*;
import net.sourceforge.fuge.bio.investigation.Investigation;
import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.bio.material.GenericMaterial;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
public class LoadFuge {
    private final EntityService entityService;
    private final SymbaEntityService symbaEntityService;

    private final SymbaFormSessionBean formSessionBean;
    private final ScpBean scpBean;
    private final PersonBean personBean;
    private Person auditor;
    private SoftwareMetaInformationBean softwareMeta;

    /**
     * Creates a new instance of LoadFuge
     *
     * @param formSessionBean the bean that holds all information about the current upload
     * @param personBean      the bean that holds all information about the current user
     * @param scpBean         the bean that holds all information about where and how to secure copy the data files
     * @param softwareMeta
     */
    public LoadFuge( SymbaFormSessionBean formSessionBean,
                     PersonBean personBean,
                     ScpBean scpBean,
                     SoftwareMetaInformationBean softwareMeta ) {
        this.personBean = personBean;
        this.entityService = personBean.getEntityService();
        this.symbaEntityService = personBean.getSymbaEntityService();

        this.formSessionBean = formSessionBean;
        this.scpBean = scpBean;
        this.softwareMeta = softwareMeta;
    }

    public void load() throws IOException {

        // first, we have to set the auditor
        setAuditor();

        if ( formSessionBean.getFuGE() == null ) {
            loadNew();
        } else {
            loadExisting();
        }
    }

    private void loadExisting() throws IOException {

        FuGE fuge = formSessionBean.getFuGE();
        fuge = loadPerson( fuge, true );
        fuge = loadMaterial( fuge );
        fuge = loadData( fuge );
        fuge = loadProtocols( fuge );
        // Before loading the fuge object into the database, reorganize the collections (only
        // needs to be done when the experiment already existed in the database and we are
        // about to load a new version of the experiment.
        // todo - do this in a smarter way
        fuge = ReorganizeCollections.reorganizeCollections( fuge, entityService, auditor );
        loadExperiment( fuge );
    }

    private void loadNew() throws IOException {

        FuGE fuge = ( FuGE ) DatabaseObjectHelper
                .createEndurantAndIdentifiable( "net.sourceforge.fuge.collection.FuGE", auditor );
        fuge.setName( formSessionBean.getExperimentName() );

        fuge = loadPerson( fuge, false );
        fuge = loadInvestigation( fuge );
        fuge = loadMaterial( fuge );
        fuge = loadData( fuge );
        fuge = loadProtocols( fuge );
        fuge = loadProvider( fuge );
        loadExperiment( fuge );
    }

    private void setAuditor() {

        Person person = ( Person ) symbaEntityService.getLatestByEndurant( personBean.getEndurantLsid() );
        if ( person == null ) {
            // should never need to make a new person at this stage
            // todo put in some error handling code here for if the person isn't found in the database
            person =
                    ( Person ) DatabaseObjectHelper
                            .createEndurantAndIdentifiable( "net.sourceforge.fuge.common.audit.Person", null );
        }

        auditor = person;
    }

    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private FuGE loadPerson( FuGE fuge,
                             boolean isExisting ) {

        // Create the Audit Collection
        Set<Contact> contacts;
        if ( !isExisting ) {
            contacts = new HashSet<Contact>();
        } else {
            contacts = ( Set<Contact> ) fuge.getAuditCollection().getAllContacts();
        }

        AuditCollection auditColl = ( AuditCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.AuditCollection" );

        // add any affiliation to the contact list
        if ( auditor.getAffiliations() != null && !auditor.getAffiliations().isEmpty() ) {
            Set affiliations = ( Set ) auditor.getAffiliations();
            if ( affiliations != null && !affiliations.isEmpty() ) {

                // todo - do we need to check for latest version of the affiliations?
                contacts.addAll( affiliations );
            }
        }
        // And we can now add the person to the list of contacts, if not already there
        boolean matchFound = false;
        for ( Contact contact : contacts ) {
            if ( contact.getEndurant()
                    .getIdentifier()
                    .equals( auditor.getEndurant().getIdentifier() ) ) {
                matchFound = true;
                break;
            }
        }
        if ( !matchFound ) {
            // And we can now add the person to the list of contacts
            contacts.add( auditor );
        }

        // And now that we're finished adding contacts, we can add the list of contacts
        // to the audit collection...
        auditColl.setAllContacts( contacts );
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.AuditCollection", auditColl, auditor );

        fuge.setAuditCollection( auditColl );

        return fuge;
    }

    // this method only applicable to new experiments
    private FuGE loadInvestigation( FuGE fuge ) {
        InvestigationCollection investigationCollection = ( InvestigationCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.InvestigationCollection" );

        Set<Investigation> investigations = new HashSet<Investigation>();

        Set<Description> descriptions = new HashSet<Description>();
        Description description1 = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        Description description2 = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        if ( formSessionBean.getHypothesis() != null ) {
            description1.setText( "Hypothesis: " + formSessionBean.getHypothesis() );
        } else {
            description1.setText( "Hypothesis: None" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description1, auditor );

        descriptions.add( description1 );
        if ( formSessionBean.getConclusion() != null ) {
            description2.setText( "Conclusions: " + formSessionBean.getConclusion() );
        } else {
            description2.setText( "Conclusions: None" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description2, auditor );
        descriptions.add( description2 );

        // Create a new investigation in the database.
        Investigation investigation = ( Investigation ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.bio.investigation.Investigation", auditor );

        investigation.setName( "Investigation Details for " + formSessionBean.getExperimentName() );
        investigation.setDescriptions( descriptions );

        DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.investigation.Investigation", investigation, auditor );

        investigations.add( investigation );

        investigationCollection.setInvestigations( investigations );

        // load the fuge object into the database
        DatabaseObjectHelper
                .save( "net.sourceforge.fuge.collection.InvestigationCollection", investigationCollection, auditor );
        fuge.setInvestigationCollection( investigationCollection );

        return fuge;
    }

    // Add a raw data file, and the external data that refers to it
    // We are always positive that these data files have not been loaded before.
    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private FuGE loadData( FuGE fuge ) throws IOException {

        DataCollection dataCollection = ( DataCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.DataCollection" );

        Set<Data> datas;
        // this line ensures that, if there is any data in the collection, that it is retained.
        if ( fuge.getDataCollection() != null && !fuge.getDataCollection().getAllData().isEmpty() ) {
            datas = ( Set<Data> ) fuge.getDataCollection().getAllData();
        } else {
            datas = new HashSet<Data>();
        }

        // we store data in a file store and use scpBean to get the information on where to put it
        SyMBADataCopier copier = SyMBADataCopierFactory.createSyMBADataCopier( "scp", scpBean.getHostname(),
                scpBean.getUsername(), scpBean.getPassword(), scpBean.getDirectory(), scpBean.getRemoteDataStoreOs() );

        int iii = 0;
        for ( DatafileSpecificMetadataStore rdib : formSessionBean.getDatafileSpecificMetadataStores() ) {

            // "cisban." just there to get removed.
            String fileLSID = DatabaseObjectHelper.getLsid( "cisban.RawData" );

            // Change the file LSID *just* for outputting, and just if it is DOS
            if ( scpBean.getRemoteDataStoreOs().equals( "dos" ) ) {
                fileLSID = LsidFilenameConverter.convert( fileLSID, scpBean.getLsidColonReplacement() );
            }

            if ( !copier.copy( rdib.getDataFile(), fileLSID, rdib.getFriendlyId() ) ) {
                // error making the copy
                throw new IOException();
            }

            // Create a new external data object.
            ExternalData externalData = ( ExternalData ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.bio.data.ExternalData", auditor );
            externalData.setName( rdib.getFriendlyId() );
            externalData.setLocation( fileLSID );

            // set the description of the file, if present
            if ( rdib.getDataFileDescription() != null && rdib.getDataFileDescription().length() > 0 ) {
                // add any descriptions. You can see above we have just created a new externaldata object, so
                // no need to check for old descriptions.
                Set<Description> descriptions = new HashSet<Description>();
                Description description = ( Description ) entityService.createDescribable(
                        "net.sourceforge.fuge.common.description.Description" );
                description.setText( rdib.getDataFileDescription() );
                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.description.Description", description, auditor );
                descriptions.add( description );
                externalData.setDescriptions( descriptions );
            }

            // set the file format, if it has one.
            if ( rdib.getFileFormat() != null ) {

                // we will be updating the ontology collection, therefore ensure that we start with everything
                // that was in it to begin with.
                OntologyCollection ontologyCollection = prepareOntologyCollection( fuge );

                // the pre-existing information has been copied. Now add to it in any way you need to
                Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();
                Set<OntologySource> ontologySources = ( Set<OntologySource> ) ontologyCollection.getSources();

                // todo proper algorithm
                boolean matchFound = findMatchingEndurant( rdib.getFileFormat(), ontologyTerms );
                // irrespective of whether or not we found a match, we still need to add the term to the new material
                OntologyTerm termToAdd =
                        ( OntologyTerm ) symbaEntityService.getLatestByEndurant( rdib.getFileFormat() );

                externalData.setFileFormat( termToAdd );

                // todo this won't catch cases where the ontology source was added at a later date
                if ( !matchFound ) {
                    // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                    // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                    ontologyTerms.add( termToAdd );
                    if ( termToAdd.getOntologySource() != null ) {
                        ontologySources.add(
                                ( OntologySource ) symbaEntityService.getLatestByEndurant(
                                        termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                    }
                    ontologyCollection.setOntologyTerms( ontologyTerms );
                    ontologyCollection.setSources( ontologySources );

                    // load the fuge object into the database
                    DatabaseObjectHelper
                            .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
                    fuge.setOntologyCollection( ontologyCollection );
                }
            }

            // load the external data into the database
            DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.data.ExternalData", externalData, auditor );

            rdib.setEndurantLsid( externalData.getEndurant().getIdentifier() );
            formSessionBean.setDatafileSpecificMetadataStore( rdib, iii );

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
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.DataCollection", dataCollection, auditor );
        fuge.setDataCollection( dataCollection );

        return fuge;
    }

    private OntologyCollection prepareOntologyCollection( FuGE fuge ) {

        OntologyCollection ontologyCollection = ( OntologyCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.OntologyCollection" );

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
            if ( fuge.getOntologyCollection().getSources() != null ) {
                ontologySources = ( Set<OntologySource> ) fuge.getOntologyCollection().getSources();
            } else {
                ontologySources = new HashSet<OntologySource>();
            }
        }
        ontologyCollection.setOntologyTerms( ontologyTerms );
        ontologyCollection.setSources( ontologySources );
        return ontologyCollection;
    }

    // for now, a new GenericMaterial each time.
    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private FuGE loadMaterial( FuGE fuge ) {

        OntologyCollection ontologyCollection = prepareOntologyCollection( fuge );

        Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();
        Set<OntologySource> ontologySources = ( Set<OntologySource> ) ontologyCollection.getSources();

        MaterialCollection materialCollection = ( MaterialCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.MaterialCollection" );

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
        for ( DatafileSpecificMetadataStore rdib : formSessionBean.getDatafileSpecificMetadataStores() ) {
            if ( rdib.getMaterialFactorsStore() != null ) {
                hasMaterial = true;

                // the material needs to be made, and each ontology term needs to be added if it hasn't already been added
                String nameToUse = "";
                if ( rdib.getMaterialFactorsStore().getMaterialName() != null &&
                     rdib.getMaterialFactorsStore().getMaterialName().length() > 0 ) {
                    nameToUse = rdib.getMaterialFactorsStore().getMaterialName();
                }

                GenericMaterial genericMaterial =
                        ( GenericMaterial ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                "net.sourceforge.fuge.bio.material.GenericMaterial", auditor );
                genericMaterial.setName( nameToUse );

                // add any OntologyReplacement descriptions.
                if ( ( rdib.getMaterialFactorsStore() ).getOntologyReplacements() != null &&
                     ( rdib.getMaterialFactorsStore() ).getOntologyReplacements().size() > 0 ) {
                    Set<Description> descriptions;
                    if ( genericMaterial.getDescriptions() == null ) {
                        descriptions = new HashSet<Description>();
                    } else {
                        descriptions = ( Set<Description> ) genericMaterial.getDescriptions();
                    }
                    for ( String key : ( rdib.getMaterialFactorsStore() ).getOntologyReplacements().keySet() ) {
                        Description description = ( Description ) entityService.createDescribable(
                                "net.sourceforge.fuge.common.description.Description" );
                        description.setText(
                                key + " = " + ( rdib.getMaterialFactorsStore() ).getOntologyReplacements().get( key ) );
                        DatabaseObjectHelper
                                .save( "net.sourceforge.fuge.common.description.Description", description, auditor );
                        descriptions.add( description );
                    }
                    genericMaterial.setDescriptions( descriptions );
                }

                if ( rdib.getMaterialFactorsStore().getMaterialType() != null ) {
                    // The first ontology term is the materialType

                    // todo proper algorithm
                    boolean matchFound = findMatchingEndurant(
                            rdib.getMaterialFactorsStore().getMaterialType(), ontologyTerms );
                    // irrespective of whether or not we found a match, we still need to add the term to the new material
                    OntologyTerm termToAdd = ( OntologyTerm ) symbaEntityService
                            .getLatestByEndurant( rdib.getMaterialFactorsStore().getMaterialType() );
                    genericMaterial.setMaterialType( termToAdd );
                    // todo this won't catch cases where the ontology source was added at a later date
                    if ( !matchFound ) {
                        // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                        // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                        ontologyTerms.add( termToAdd );
                        if ( termToAdd.getOntologySource() != null ) {
                            ontologySources.add(
                                    ( OntologySource ) symbaEntityService.getLatestByEndurant(
                                            termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                        }
                    }

                    if ( rdib.getMaterialFactorsStore().getCharacteristics() != null ) {
                        // Now we have the characteristics to load.
                        Set<OntologyTerm> characteristics;
                        if ( genericMaterial.getCharacteristics() == null ) {
                            characteristics = new HashSet<OntologyTerm>();
                        } else {
                            characteristics = ( Set<OntologyTerm> ) genericMaterial.getCharacteristics();
                        }
                        for ( String ontologySourceEndurant : rdib.getMaterialFactorsStore().getCharacteristics()
                                .keySet() ) {
                            String ontologyTermEndurant =
                                    rdib.getMaterialFactorsStore().getCharacteristics().get( ontologySourceEndurant );
                            // todo proper algorithm
                            matchFound = findMatchingEndurant( ontologyTermEndurant, ontologyTerms );
                            // irrespective of whether or not we found a match, we still need to add the term to the new material
                            termToAdd =
                                    ( OntologyTerm ) symbaEntityService.getLatestByEndurant( ontologyTermEndurant );
                            characteristics.add( termToAdd );
                            // todo this won't catch cases where the ontology source was added at a later date
                            if ( !matchFound ) {
                                // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                                // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                                ontologyTerms.add( termToAdd );
                                ontologySources.add(
                                        ( OntologySource ) symbaEntityService.getLatestByEndurant(
                                                ontologySourceEndurant ) );
                            }
                        }
                        genericMaterial.setCharacteristics( characteristics );
                    }

                    if ( rdib.getMaterialFactorsStore().getTreatmentInfo() != null ) {
                        // Next are the treatments, which get loaded as individual descriptions on the GenericMaterial.
                        // These are always added, without checks.
                        Set<Description> descriptions;
                        if ( genericMaterial.getDescriptions() == null ) {
                            descriptions = new HashSet<Description>();
                        } else {
                            descriptions = ( Set<Description> ) genericMaterial.getDescriptions();
                        }
                        for ( String treatmentDesc : rdib.getMaterialFactorsStore().getTreatmentInfo() ) {
                            Description description = ( Description ) entityService.createDescribable(
                                    "net.sourceforge.fuge.common.description.Description" );
                            description.setText( "Treatment: " + treatmentDesc );
                            DatabaseObjectHelper.save(
                                    "net.sourceforge.fuge.common.description.Description", description, auditor );
                            descriptions.add( description );
                        }
                        genericMaterial.setDescriptions( descriptions );
                    }

                    // a final step for this rdib material info: add the material to the list of materials after storing
                    DatabaseObjectHelper.save(
                            "net.sourceforge.fuge.bio.material.GenericMaterial", genericMaterial, auditor );

                    MaterialFactorsStore mfb = rdib.getMaterialFactorsStore();
                    mfb.setCreatedMaterial( genericMaterial.getEndurant().getIdentifier() );
                    rdib.setMaterialFactorsStore( mfb );
                    formSessionBean.setDatafileSpecificMetadataStore( rdib, iii );

                    materials.add( genericMaterial );
                }
                iii++;
            }
        }

        // if there has been at least one material, then we need to load the updated ontology terms into the ontology collection
        if ( hasMaterial ) {
            ontologyCollection.setOntologyTerms( ontologyTerms );
            ontologyCollection.setSources( ontologySources );

            // load the fuge object into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
            fuge.setOntologyCollection( ontologyCollection );

            // load the collection object into the database if we've added materials
            materialCollection.setMaterials( materials );
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.MaterialCollection", materialCollection, auditor );
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

    private void loadExperiment( FuGE fuge ) {

        // Load the entire fuge entry into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.FuGE", fuge, auditor );

        // Use the new LSID to create a new policy in the security database
//        try {
        SecurityEngineInterrogator interrogator = new SecurityEngineInterrogator();
        interrogator.createPolicy( "symbaAllUsers", fuge.getIdentifier(), "read", "Permit" );
        interrogator.createPolicy( "symbaAllUsers", fuge.getIdentifier(), "write", "Deny" );

        interrogator.createPolicy( auditor.getEndurant().getIdentifier(), fuge.getIdentifier(), "read", "Permit" );
        interrogator.createPolicy( auditor.getEndurant().getIdentifier(), fuge.getIdentifier(), "write", "Permit" );
//        } catch ( ServiceException e ) {
//            System.out.println( "Was not able to create the SecurityEngineInterrogator: " + e.getMessage() );
//            e.printStackTrace();
//        } catch ( RemoteException e ) {
//            System.out.println( "Was not able to load the policy into the database: " + e.getMessage() );
//            e.printStackTrace();
//        }
    }

    // the default protocol loader
    // all names must include the formSessionBean.getTopLevelProtocolName() in order to be found
    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private FuGE loadProtocols( FuGE fuge ) {

        ProtocolCollection protocolCollection = ( ProtocolCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.ProtocolCollection" );

        // Now we need to associate the Data with the appropriate protocol applications and protocols.
        // We know which factors these are for because we don't give
        // the user the choice of protocols, but instead the choice of Actions available.

        ProtocolCollectionMappingHelper cpc = new ProtocolCollectionMappingHelper();

        // The GenericProtocol whose GenericAction is stored from the form will get the data item added to it.
        Set<Protocol> protocolSet = cpc.addRelevantProtocols(
                fuge, formSessionBean.getTopLevelProtocolEndurant().trim() );

        // used to manually add assay protocol here, but shouldn't have to - should be taken care of by the
        // addRelevantProtocols method above.
        protocolCollection.setProtocols( protocolSet );

        // Now retrieve all equipment associated with these protocols
        protocolCollection.setAllEquipment( cpc.addRelevantEquipment(
                fuge, ( Set<Protocol> ) protocolCollection.getProtocols() ) );

        Set allGPAs;
        if ( fuge.getProtocolCollection() != null ) {
            allGPAs = ( Set ) fuge.getProtocolCollection().getProtocolApplications();
        } else {
            allGPAs = new HashSet();
        }

        String expName;
        if ( formSessionBean.getExperimentName() != null ) {
            expName = formSessionBean.getExperimentName();
        } else {
            expName = fuge.getName();
        }

        // Get the top-level protocol, which has no matching GenericAction.
        GenericProtocol topLevelProtocol =
                ( GenericProtocol ) symbaEntityService
                        .getLatestByEndurant( formSessionBean.getTopLevelProtocolEndurant() );
        if ( topLevelProtocol == null ) {
            System.err.println(
                    "Error finding parent protocol for assay action endurant " +
                    formSessionBean.getTopLevelProtocolEndurant() + " (" +
                    formSessionBean.getTopLevelProtocolName() + ")" );
            return fuge;
        }

        // get the GPA attached to the top level protocol, if present.
        GenericProtocolApplication gpaOfTopLevelProtocol = null;
        // first, find the highest-level GPA: that is, the GPA of the top-level investigation
        for ( Object gpaObject : allGPAs ) {
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

        // There will either be two levels or three levels of GPAs: two levels (assay and top-level protocol) if
        // it is a two level investigation, and three levels (assay, one level up from assay, and top-level protocol)
        // otherwise.
        for ( DatafileSpecificMetadataStore store : formSessionBean.getDatafileSpecificMetadataStores() ) {
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
            GenericProtocolApplication assayGPA = createAssayGPA( store, assayProtocol );

            // add the assayGPA to the set of all GPAs
            allGPAs.add( assayGPA );

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
                gpaOfOneAboveAssayProtocol = addActionApplication( assayGPA, assayAction, gpaOfOneAboveAssayProtocol );

                // If gpaOfOneAboveAssayProtocol is new, add to database. Otherwise, put a new version in.
                if ( gpaOfOneAboveAssayProtocol.getId() != null ) {
                    // Each time we load a new version of the one-level up GPA, we need to clean the allGPAs Set.
                    // We need to remember that any child GPA *must* be removed
                    // from the set of allGPAs *if* it, too, is being overwritten.
                    allGPAs.remove( gpaOfOneAboveAssayProtocol );
                }
                DatabaseObjectHelper.save(
                        "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                        gpaOfOneAboveAssayProtocol,
                        auditor );
                // associate the new assays with the protocol collection.
                allGPAs.add( gpaOfOneAboveAssayProtocol );

                // then add the ActionApplication linking the gpaOfTopLevelProtocol to the gpaOfOneAboveAssayProtocol
                gpaOfTopLevelProtocol = addActionApplication(
                        gpaOfOneAboveAssayProtocol, oneAboveAssayAction, gpaOfTopLevelProtocol );
            } else {
                // add the ActionApplication to the gpaOfTopLevelProtocol
                gpaOfTopLevelProtocol = addActionApplication( assayGPA, assayAction, gpaOfTopLevelProtocol );
            }

            // the gpaOfTopLevelProtocol is now finished. load in the database.
            // If gpaOfTopLevelProtocol is new, add to database. Otherwise, put a new version in.
            // todo move this and associated code out of the for-loop and only do this after all data files are processed: it's currently loading unnecessary intermediaries into the db
            if ( gpaOfTopLevelProtocol.getId() != null ) {
                // Each time we load a new version of the top level GPA, we need to clean the allGPAs Set.
                // The "old" top-level GPA will be replaced with the new version of the top-level GPA with the new
                // ActionApplication objects.
                allGPAs.remove( gpaOfTopLevelProtocol );
            }
            DatabaseObjectHelper.save(
                    "net.sourceforge.fuge.common.protocol.GenericProtocolApplication",
                    gpaOfTopLevelProtocol,
                    auditor );

            allGPAs.add( gpaOfTopLevelProtocol );
        }

        protocolCollection.setProtocolApplications( allGPAs );
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

    /**
     * Adds the specified ActionApplication, both to the database and to the gpaToAddActionApplication. However, it does
     * NOT any new version of that GPA into the database, as you may have multiple ActionApplications to add and there
     * is no reason to make excess database calls.
     *
     * @param gpaToBeAdded                the gpa to assign as an ActionApplication to gpaToAddActionApplication
     * @param actionAssociatedWithGpaToBeAdded
     *                                    the action that will be linked to the gpaToBeAdded
     * @param gpaToAddActionApplicationTo the gpa to have the ActionApplication added to
     * @return the modified version of gpaToAddActionApplication, and null if gpaToAddActionApplication was null
     */
    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private GenericProtocolApplication addActionApplication( GenericProtocolApplication gpaToBeAdded,
                                                             GenericAction actionAssociatedWithGpaToBeAdded,
                                                             GenericProtocolApplication gpaToAddActionApplicationTo ) {

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

    private GenericProtocolApplication createAssayGPA( DatafileSpecificMetadataStore store,
                                                       GenericProtocol assayProtocol ) {

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
                // there is a value in getGenericEquipmentInfo, that a new parameterValue set must be made and
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

        // add the material
        if ( store.getMaterialFactorsStore() != null && store.getMaterialFactorsStore().getCreatedMaterial() != null ) {
            Set<Material> set2 = new HashSet<Material>();
            set2.add( ( Material ) symbaEntityService
                    .getLatestByEndurant( store.getMaterialFactorsStore().getCreatedMaterial() ) );
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

    // this method assumes that the experiment is new, and not existing already in the database.
    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    private FuGE loadProvider( FuGE fuge ) {
        // todo code for allowing multiple people to edit a single experiment and have multiple providers.
        // We will assign the auditor to be the Provider. The link between the Provider
        // and the Person is called a ContactRole.

        // first, create the role
        ContactRole contactRole =
                ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" );
        contactRole.setContact( auditor );

        // the provider contains an ontology term, so we need to add that term to the experiment
        OntologyCollection ontologyCollection = prepareOntologyCollection( fuge );

        // the pre-existing information has been copied. Now add to it in any way you need to
        Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();

        // then create an ontology term to identify the role, or load the most recent version of that term
        String providerAccession = "symba.sourceforge.net#PrincipalInvestigator";
        OntologyIndividual ontologyIndividual = symbaEntityService.getOntologyIndividualByAccession(
                providerAccession );

        if ( ontologyIndividual == null ) {
            ontologyIndividual = ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.common.ontology.OntologyIndividual", auditor );
            ontologyIndividual.setName( "net.sourceforge.symba.PrincipalInvestigator" );
            ontologyIndividual.setTerm( "Principal Investigator" );
            ontologyIndividual.setTermAccession( "net.sourceforge.symba.PrincipalInvestigator" );

            // and then load that term into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, auditor );
        }

        if ( !ontologyTerms.contains( ontologyIndividual ) ) {
            // add the ontology term to the list of terms.
            ontologyTerms.add( ontologyIndividual );

            // then set that ontology term in the role
            contactRole.setRole( ontologyIndividual );
            ontologyCollection.setOntologyTerms( ontologyTerms );

            // load the fuge object into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
            fuge.setOntologyCollection( ontologyCollection );
        }

        //  load the role into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.ContactRole", contactRole, auditor );

        Provider provider = ( Provider ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.collection.Provider", auditor );

        // set any part of the provider that we are interested in.
        provider.setName( "Main Provider of Experiment: " + formSessionBean.getExperimentName() );
        provider.setProvider( contactRole );

        // the provider contains a reference to the producing software, so we need to add that to the experiment
        ProtocolCollection protocolCollection = prepareProtocolCollection( fuge );
        // the pre-existing information has been copied. Now add to it in any way you need to
        Set<Software> softwares = ( Set<Software> ) protocolCollection.getAllSoftwares();
        GenericSoftware producingSoftware = ( GenericSoftware ) symbaEntityService
                .getGenericSoftwareByNameAndVersion( softwareMeta.getName(), softwareMeta.getVersion() );

        if ( producingSoftware == null ) {
            // create and save the producing software in the database
            producingSoftware = ( GenericSoftware ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.common.protocol.GenericSoftware", auditor );
            producingSoftware.setName( softwareMeta.getName() );
            producingSoftware.setVersion( softwareMeta.getVersion() );

            // and then load that term into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.common.protocol.GenericSoftware", producingSoftware, auditor );

        }
        
        if ( !softwares.contains( producingSoftware ) ) {
            // add the software to the list of software.
            softwares.add( producingSoftware );
            protocolCollection.setAllSoftwares( softwares );

            // load the fuge object into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.ProtocolCollection", protocolCollection, auditor );
            fuge.setProtocolCollection( protocolCollection );
        }

        provider.setProducingSoftware( producingSoftware );

        // load the provider into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.Provider", provider, auditor );
        // add the role to the provider
        fuge.setProvider( provider );

        return fuge;
    }

    private ProtocolCollection prepareProtocolCollection( FuGE fuge ) {

        ProtocolCollection protocolCollection = ( ProtocolCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.ProtocolCollection" );

        Set<Equipment> equipments;
        Set<Software> softwares;
        Set<ProtocolApplication> protocolApplications;
        Set<Protocol> protocols;
        if ( fuge.getOntologyCollection() == null ) {
            equipments = new HashSet<Equipment>();
            softwares = new HashSet<Software>();
            protocols = new HashSet<Protocol>();
            protocolApplications = new HashSet<ProtocolApplication>();
        } else {
            if ( fuge.getProtocolCollection().getAllEquipment() != null ) {
                equipments = ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment();
            } else {
                equipments = new HashSet<Equipment>();
            }
            if ( fuge.getProtocolCollection().getAllSoftwares() != null ) {
                softwares = ( Set<Software> ) fuge.getProtocolCollection().getAllSoftwares();
            } else {
                softwares = new HashSet<Software>();
            }
            if ( fuge.getProtocolCollection().getProtocolApplications() != null ) {
                protocolApplications =
                        ( Set<ProtocolApplication> ) fuge.getProtocolCollection().getProtocolApplications();
            } else {
                protocolApplications = new HashSet<ProtocolApplication>();
            }
            if ( fuge.getProtocolCollection().getProtocols() != null ) {
                protocols = ( Set<Protocol> ) fuge.getProtocolCollection().getProtocols();
            } else {
                protocols = new HashSet<Protocol>();
            }
        }
        protocolCollection.setAllEquipment( equipments );
        protocolCollection.setAllSoftwares( softwares );
        protocolCollection.setProtocolApplications( protocolApplications );
        protocolCollection.setProtocols( protocols );
        return protocolCollection;
    }
}
