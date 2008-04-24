package uk.ac.cisban.symba.backend.util;

import com.ibm.lsid.LSID;
import com.ibm.lsid.LSIDException;
import com.ibm.lsid.client.LSIDAssigner;
import com.ibm.lsid.wsdl.SOAPLocation;
import fugeOM.Collection.*;
import fugeOM.Common.Audit.Audit;
import fugeOM.Common.Audit.AuditAction;
import fugeOM.Common.Audit.Person;
import fugeOM.Common.Describable;
import fugeOM.Common.Endurant;
import fugeOM.Common.Identifiable;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
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

// These are wrapper methods that set the Audit Trail at the appropriate time. Use these when using

// the front-end to contact the database, rather than using the reService methods directly.
public class CisbanHelper {
    private static final boolean DEFAULT_DEBUG = false;
    private static final Properties EMPTY_PROPS = new Properties();

    //     PRODUCTION SERVER
    private static final LSIDAssigner DEFAULT_ASSIGNER = new LSIDAssigner(
            new SOAPLocation( "http://metagenome.ncl.ac.uk:8081/authority/assigning" ) );


    private static final String DEFAULT_DOMAIN_NAME = "cisban.cisbs.org";

    public static CisbanHelper create( RealizableEntityService reService ) {
        return create( reService, DEFAULT_ASSIGNER, DEFAULT_DOMAIN_NAME, DEFAULT_DEBUG );
    }

    public static CisbanHelper create( RealizableEntityService reService,
                                       LSIDAssigner assigner,
                                       String domainName,
                                       boolean debugValue ) {
        return new CisbanHelper( reService, assigner, domainName, debugValue );
    }

    private final boolean verbose;
    private final LSIDAssigner assigner;
    private final String domainName;
    private final RealizableEntityService reService;

    private CisbanHelper( RealizableEntityService reService,
                          LSIDAssigner assigner,
                          String domainName,
                          boolean debugValue ) {
        this.reService = reService;
        this.assigner = assigner;
        this.domainName = domainName;
        this.verbose = debugValue;
    }

    private void printMessage( String s, PrintStream printStream ) {
        if ( printStream != null && verbose ) {
            printStream.println( s );
        }
    }

    public Endurant getOrCreateEndurant( String endurantId,
                                         String className,
                                         PrintStream printStream ) throws RealizableEntityServiceException, LSIDException {
        Endurant endurant;
        try {
            endurant = ( Endurant ) reService.findEndurant( endurantId );
            printMessage( "Endurant found, object loaded: " + endurantId, printStream );
        } catch ( fugeOM.service.RealizableEntityServiceException e ) {
            printMessage( "Endurant not yet in database: creating now: " + endurantId, printStream );
            endurant = createAndLoadEndurant( endurantId, className, printStream );
        }
        return endurant;
    }

    // creates a new endurant object and loads it into the database without performing any checks.
    private Endurant createAndLoadEndurant( String endurantId,
                                            String className,
                                            PrintStream printStream ) throws RealizableEntityServiceException, LSIDException {
        Endurant endurant;

        if ( endurantId == null ) {
            endurant = ( Endurant ) reService.createEndurantOb( getLSID( className ), className );
        } else {
            endurant = ( Endurant ) reService.createEndurantOb( endurantId, className );
        }
        loadDescribable( endurant, className, printStream );

        return endurant;
    }

    // possibleId/possibleName is only used if an identifiable object is not found in the database.
    // identifiable will have no value in getId() if it was just created (and therefore NOT already in the database).
    public Identifiable getOrCreateLatest( String endurantId,
                                           String endurantClassName,
                                           String possibleName,
                                           String className ) throws RealizableEntityServiceException, LSIDException {
        return getOrCreateLatest( endurantId, endurantClassName, possibleName, className, null );
    }

    public Identifiable getOrCreateLatest( String endurantId,
                                           String endurantClassName,
                                           String possibleName,
                                           String className,
                                           PrintStream printStream ) throws RealizableEntityServiceException, LSIDException {

        Endurant endurant = getOrCreateEndurant( endurantId, endurantClassName, printStream );

        Identifiable identifiable;
        try {
            identifiable = ( Identifiable ) reService.findLatestByEndurant( endurant.getIdentifier() );
            printMessage(
                    "Identifiable object (" + identifiable.getIdentifier() + ") found, attached to endurant = " +
                            endurant.getIdentifier(),
                    printStream );
        } catch ( fugeOM.service.RealizableEntityServiceException e ) {
            printMessage( "No identifiable object found for endurant = " + endurant.getIdentifier(), printStream );
            identifiable = ( Identifiable ) reService.createIdentifiableOb(
                    getLSID( className ), possibleName, endurant, className );
        }
        return identifiable;
    }

    public void loadIdentifiable( Identifiable i,
                                  String className,
                                  PrintStream printStream ) throws RealizableEntityServiceException {
        printMessage( "Identifiable " + i.getIdentifier() + " is being created in the database...", printStream );
        loadDescribable( i, className, printStream );
    }

    // BE CAREFUL with this method! It will create objects in the database, and overwrite ALL the
    // describable "*Collection"'s in your FuGE object (it will retain all existing components of those
    // objects though).
    // todo does not copy any describable information of each collection themselves to the new collection.
    public FuGE reorganizeCollections( FuGE fuge ) throws RealizableEntityServiceException {

        if ( fuge.getAuditCollection() != null ) {
            AuditCollection collection = ( AuditCollection ) reService
                    .createDescribableOb( "fugeOM.Collection.AuditCollection" );
            collection.setAllContacts( fuge.getAuditCollection().getAllContacts() );
            collection.setSecurityCollection( fuge.getAuditCollection().getSecurityCollection() );
            collection.setSecurityGroups( fuge.getAuditCollection().getSecurityGroups() );
            reService.createObInDB( "fugeOM.Collection.AuditCollection", collection );
            fuge.setAuditCollection( collection );
        }

        if ( fuge.getOntologyCollection() != null ) {
            OntologyCollection collection = ( OntologyCollection ) reService
                    .createDescribableOb( "fugeOM.Collection.OntologyCollection" );
            collection.setOntologySources( fuge.getOntologyCollection().getOntologySources() );
            collection.setOntologyTerms( fuge.getOntologyCollection().getOntologyTerms() );
            reService.createObInDB( "fugeOM.Collection.OntologyCollection", collection );
            fuge.setOntologyCollection( collection );
        }

        if ( fuge.getReferenceableCollection() != null ) {
            ReferenceableCollection collection = ( ReferenceableCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.ReferenceableCollection" );
            collection.setRcToDatabase( fuge.getReferenceableCollection().getRcToDatabase() );
            collection.setAllBibliographicReferences( fuge.getReferenceableCollection().getAllBibliographicReferences() );
            reService.createObInDB( "fugeOM.Collection.ReferenceableCollection", collection );
            fuge.setReferenceableCollection( collection );
        }

        if ( fuge.getMaterialCollection() != null ) {
            MaterialCollection collection = ( MaterialCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.MaterialCollection" );
            collection.setMaterials( fuge.getMaterialCollection().getMaterials() );
            reService.createObInDB( "fugeOM.Collection.MaterialCollection", collection );
            fuge.setMaterialCollection( collection );
        }

        if ( fuge.getDataCollection() != null ) {
            DataCollection collection = ( DataCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.DataCollection" );
            collection.setAllData( fuge.getDataCollection().getAllData() );
            collection.setAllDataPartitions( fuge.getDataCollection().getAllDataPartitions() );
            collection.setAllDimensions( fuge.getDataCollection().getAllDimensions() );
            collection.setHigherLevelAnalyses( fuge.getDataCollection().getHigherLevelAnalyses() );
            reService.createObInDB( "fugeOM.Collection.DataCollection", collection );
            fuge.setDataCollection( collection );
        }

        if ( fuge.getProtocolCollection() != null ) {
            ProtocolCollection collection = ( ProtocolCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.ProtocolCollection" );
            collection.setAllEquipment( fuge.getProtocolCollection().getAllEquipment() );
            collection.setAllProtocolApps( fuge.getProtocolCollection().getAllProtocolApps() );
            collection.setAllSoftwares( fuge.getProtocolCollection().getAllSoftwares() );
            collection.setProtocols( fuge.getProtocolCollection().getProtocols() );
            reService.createObInDB( "fugeOM.Collection.ProtocolCollection", collection );
            fuge.setProtocolCollection( collection );
        }

        if ( fuge.getInvestigationCollection() != null ) {
            InvestigationCollection collection = ( InvestigationCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.InvestigationCollection" );
            collection.setInvestigations( fuge.getInvestigationCollection().getInvestigations() );
            collection.setFactorCollection( fuge.getInvestigationCollection().getFactorCollection() );
            reService.createObInDB( "fugeOM.Collection.InvestigationCollection", collection );
            fuge.setInvestigationCollection( collection );
        }
        return fuge;
    }

    public Identifiable assignAndLoadIdentifiable( Identifiable i,
                                                   Person p,
                                                   String className,
                                                   PrintStream printStream ) throws RealizableEntityServiceException, LSIDException {
        String lsid = getLSID( className );

        // set the new LSID
        i.setIdentifier( lsid );

        // make sure to remove the old database ID
        i.setId( null );

        loadIdentifiable( i, p, className, printStream );

        return i;
    }

    public String getLSID( String className ) throws LSIDException {
        // create the new LSID
        String entityType = className.substring( className.lastIndexOf( "." ) + 1 );
        LSID lsid = assigner.assignLSID( domainName, entityType, EMPTY_PROPS );
        return lsid.toString();
    }

    public Identifiable assignAndLoadIdentifiable( Identifiable i,
                                           String className,
                                           PrintStream printStream ) throws RealizableEntityServiceException, LSIDException {
        return assignAndLoadIdentifiable( i, null, className, printStream );
    }

    // Person must already be loaded in the database
    public void loadIdentifiable( Identifiable i,
                                  Person p,
                                  String className,
                                  PrintStream printStream ) throws RealizableEntityServiceException {
        printMessage( "Identifiable " + i.getIdentifier() + " is being created in the database...", printStream );
        if ( p != null )
            loadDescribable( i, p, className, printStream );
        else
            loadDescribable( i, null, className, printStream );
    }

    public void loadDescribable( Describable d,
                                 String className,
                                 PrintStream printStream ) throws RealizableEntityServiceException {
        loadDescribable( d, null, className, printStream );
    }

    // Person must already be loaded in the database
    public void loadDescribable( Describable d,
                                 Person p,
                                 String className,
                                 PrintStream printStream ) throws RealizableEntityServiceException {
        // Add the audit information
        if ( p != null )
            d.setAuditTrail( getNewAuditTrail( p ) );
        else
            d.setAuditTrail( getNewAuditTrail() );

        reService.createObInDB( className, d );
        printMessage( "Object loaded in database with audit trail.", printStream );
    }

    private Set<Audit> getNewAuditTrail() throws RealizableEntityServiceException {
        return getNewAuditTrail( null );
    }

    // Person must already be loaded in the database
    private Set<Audit> getNewAuditTrail( Person p ) throws RealizableEntityServiceException {
        // Prepare an audit message: each audit object MUST be made anew for EACH OBJECT WITH AN AUDITTRAIL!!
        // If you decide to add a performer, make sure that the person/organization used is ALREADY loaded into
        // the database when you assign it to the Audit instance.
        Set<Audit> auditTrail = new HashSet<Audit>();
        Audit auditInstance = ( Audit ) reService.createDescribableOb( "fugeOM.Common.Audit.Audit" );
        auditInstance.setAction( AuditAction.CREATION );
        auditInstance.setDate( new Date() );
        if ( p != null ) {
            auditInstance.setPerformer( p );
        }
        reService.createObInDB( "fugeOM.Common.Audit.Audit", auditInstance );
        auditTrail.add( auditInstance );
        return auditTrail;
    }

    // Here we want a standalone copy of the object - everything remains the same EXCEPT the endurant, identifier
    // and id. Then the new object is loaded into the database.
    public Identifiable overwriteIdsAndLoad( Identifiable identifiable,
                                             String endurantClassName,
                                             String identifiableClassName,
                                             Person person ) throws RealizableEntityServiceException, LSIDException {

        // create a new endurant object and load it in the database
        Endurant endurant = createAndLoadEndurant( null, endurantClassName, null);

        // overwrite the existing endurant in identifiable with the new one
        identifiable.setEndurant( endurant );

        // Load the newly-identified object after assigning a new identifier and setting id to null
        assignAndLoadIdentifiable( identifiable, person, identifiableClassName, null );
        return identifiable;
    }
}


