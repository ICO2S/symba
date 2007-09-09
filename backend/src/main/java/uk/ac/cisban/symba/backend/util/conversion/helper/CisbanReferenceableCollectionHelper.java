package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.ReferenceableCollection;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Identifiable;
import fugeOM.Common.References.BibliographicReference;
import fugeOM.Common.References.Database;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.net.URISyntaxException;
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

public class CisbanReferenceableCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanContactRoleHelper ccr;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanReferenceableCollectionHelper( RealizableEntityService reService,
                                                CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.cd = ci.getCisbanDescribableHelper();
        this.ci = ci;
        this.ccr = new CisbanContactRoleHelper( reService, cd );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ReferenceableCollection unmarshalReferenceableCollection(
            FugeOMCollectionReferenceableCollectionType refCollXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        ReferenceableCollection refColl = ( ReferenceableCollection ) reService.createDescribableOb(
                "fugeOM.Collection.ReferenceableCollection" );
        refColl = ( ReferenceableCollection ) cd.unmarshalDescribable( refCollXML, refColl );

        Set<BibliographicReference> bibRefs = new HashSet<BibliographicReference>();
        for ( FugeOMCommonReferencesBibliographicReferenceType bibRefXML : refCollXML.getBibliographicReference() ) {

            // Retrieve latest version from the database.
            BibliographicReference bibRef = ( BibliographicReference ) helper.getOrCreateLatest(
                    bibRefXML.getEndurant(),
                    "fugeOM.Common.References.BibRefEndurant",
                    bibRefXML.getName(),
                    "fugeOM.Common.References.BibliographicReference",
                    System.err );

            bibRef = ( BibliographicReference ) ci.unmarshalIdentifiable( bibRefXML, bibRef );

            // set the non-identifiable traits
            if ( bibRefXML.getAuthors() != null )
                bibRef.setAuthors( bibRefXML.getAuthors() );
            if ( bibRefXML.getEditor() != null )
                bibRef.setEditor( bibRefXML.getEditor() );
            if ( bibRefXML.getIssue() != null )
                bibRef.setIssue( bibRefXML.getIssue() );
            if ( bibRefXML.getPages() != null )
                bibRef.setPages( bibRefXML.getPages() );
            if ( bibRefXML.getPublication() != null )
                bibRef.setPublication( bibRefXML.getPublication() );
            if ( bibRefXML.getPublisher() != null )
                bibRef.setPublisher( bibRefXML.getPublisher() );
            if ( bibRefXML.getTitle() != null )
                bibRef.setTitle( bibRefXML.getTitle() );
            if ( bibRefXML.getVolume() != null )
                bibRef.setVolume( bibRefXML.getVolume() );
            if ( bibRefXML.getYear() != null )
                bibRef.setYear( bibRefXML.getYear() );

            if ( bibRef.getId() != null ) {
                helper.assignAndLoadIdentifiable(
                        bibRef, "fugeOM.Common.References.BibliographicReference", System.err );
            } else {
                helper.loadIdentifiable( bibRef, "fugeOM.Common.References.BibliographicReference", System.err );
            }
            bibRefs.add( bibRef );
        }

        refColl.setAllBibliographicReferences( bibRefs );

        Set<Database> dbs = new HashSet<Database>();
        for ( FugeOMCommonReferencesDatabaseType dbXML : refCollXML.getDatabase() ) {

            // Retrieve latest version from the database.
            Database db = ( Database ) helper.getOrCreateLatest(
                    dbXML.getEndurant(),
                    "fugeOM.Common.References.DatabaseEndurant",
                    dbXML.getName(),
                    "fugeOM.Common.References.Database",
                    System.err );

            db = ( Database ) ci.unmarshalIdentifiable( dbXML, db );

            // set non-identifiable traits
            if ( dbXML.getURI() != null ) {
//                db.setURI( new java.net.URI( dbXML.getURI() ) );
                db.setURI( dbXML.getURI() );
            }
            if ( dbXML.getVersion() != null ) {
                db.setVersion( dbXML.getVersion() );
            }

            Set<ContactRole> contactRoles = new HashSet<ContactRole>();
            for ( FugeOMCommonAuditContactRoleType contactRoleXML : dbXML.getContactRole() ) {
                contactRoles.add( ccr.unmarshalContactRole( contactRoleXML ) );
            }

            db.setDatabaseContact( contactRoles );
            if ( db.getId() != null ) {
                helper.assignAndLoadIdentifiable( db, "fugeOM.Common.References.Database", System.err );
            } else {
                helper.loadIdentifiable( db, "fugeOM.Common.References.Database", System.err );
            }
            dbs.add( db );
        }

        refColl.setRcToDatabase( dbs );
        reService.createObInDB( "fugeOM.Collection.ReferenceableCollection", refColl );
        return refColl;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionReferenceableCollectionType marshalReferenceableCollection( ReferenceableCollection refColl )
            throws URISyntaxException {

        FugeOMCollectionReferenceableCollectionType refCollXML = new FugeOMCollectionReferenceableCollectionType();

        refCollXML = ( FugeOMCollectionReferenceableCollectionType ) cd.marshalDescribable( refCollXML, refColl );

        for ( Object bibRefObj : refColl.getAllBibliographicReferences() ) {
            BibliographicReference bibRef = ( BibliographicReference ) bibRefObj;

            FugeOMCommonReferencesBibliographicReferenceType bibRefXML = new FugeOMCommonReferencesBibliographicReferenceType();
            bibRefXML = ( FugeOMCommonReferencesBibliographicReferenceType ) ci.marshalIdentifiable(
                    bibRefXML, bibRef );

            if ( bibRef.getAuthors() != null )
                bibRefXML.setAuthors( bibRef.getAuthors() );
            if ( bibRef.getEditor() != null )
                bibRefXML.setEditor( bibRef.getEditor() );
            if ( bibRef.getIssue() != null )
                bibRefXML.setIssue( bibRef.getIssue() );
            if ( bibRef.getPages() != null )
                bibRefXML.setPages( bibRef.getPages() );
            if ( bibRef.getPublication() != null )
                bibRefXML.setPublication( bibRef.getPublication() );
            if ( bibRef.getPublisher() != null )
                bibRefXML.setPublisher( bibRef.getPublisher() );
            if ( bibRef.getTitle() != null )
                bibRefXML.setTitle( bibRef.getTitle() );
            if ( bibRef.getVolume() != null )
                bibRefXML.setVolume( bibRef.getVolume() );
            if ( bibRef.getYear() != 0 )
                bibRefXML.setYear( bibRef.getYear() ); // int will not be null, though it may be zero. Assume we won't print if it's zero.

            refCollXML.getBibliographicReference().add( bibRefXML );
        }

        for ( Object dbObj : refColl.getRcToDatabase() ) {
            Database db = ( Database ) dbObj;

            FugeOMCommonReferencesDatabaseType dbXML = new FugeOMCommonReferencesDatabaseType();

            dbXML = ( FugeOMCommonReferencesDatabaseType ) ci.marshalIdentifiable( dbXML, db );
            if ( db.getURI() != null ) {
//                dbXML.setURI( db.getURI().toString() );
                dbXML.setURI( db.getURI() );
            }
            if ( db.getVersion() != null ) {
                dbXML.setVersion( db.getVersion() );
            }

            for ( Object contactRoleObj : db.getDatabaseContact() ) {
                dbXML.getContactRole().add( ccr.marshalContactRole( ( ContactRole ) contactRoleObj ) );
            }
            refCollXML.getDatabase().add( dbXML );
        }

        return refCollXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType fuGEType ) {

        CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper( reService, ci );
        CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper( reService, ci );

        FugeOMCollectionReferenceableCollectionType refCollXML = new FugeOMCollectionReferenceableCollectionType();

        refCollXML = ( FugeOMCollectionReferenceableCollectionType ) cd.generateRandomXML( refCollXML, ci );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonReferencesBibliographicReferenceType bibRefXML = new FugeOMCommonReferencesBibliographicReferenceType();
            bibRefXML = ( FugeOMCommonReferencesBibliographicReferenceType ) ci.generateRandomXML( bibRefXML );

            bibRefXML.setAuthors( String.valueOf( Math.random() ) );
            bibRefXML.setEditor( String.valueOf( Math.random() ) );
            bibRefXML.setIssue( String.valueOf( Math.random() ) );
            bibRefXML.setPages( String.valueOf( Math.random() ) );
            bibRefXML.setPublication( String.valueOf( Math.random() ) );
            bibRefXML.setPublisher( String.valueOf( Math.random() ) );
            bibRefXML.setTitle( String.valueOf( Math.random() ) );
            bibRefXML.setVolume( String.valueOf( Math.random() ) );
            bibRefXML.setYear(
                    ( int ) ( Math.random() *
                            10 ) ); // int will not be null, though it may be zero. Assume we won't print if it's zero.

            refCollXML.getBibliographicReference().add( bibRefXML );
        }

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonReferencesDatabaseType dbXML = new FugeOMCommonReferencesDatabaseType();

            dbXML = ( FugeOMCommonReferencesDatabaseType ) ci.generateRandomXML( dbXML );
            dbXML.setURI( String.valueOf( Math.random() ) );
            dbXML.setVersion( String.valueOf( Math.random() ) );
            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                if ( fuGEType.getAuditCollection() == null ) {
                    fuGEType = cac.generateRandomXML( fuGEType );
                }
                if ( fuGEType.getOntologyCollection() == null ) {
                    fuGEType = coc.generateRandomXML( fuGEType );
                }
                dbXML.getContactRole().add( ccr.generateRandomXML( fuGEType, ci ) );
            }
            refCollXML.getDatabase().add( dbXML );
        }
        fuGEType.setReferenceableCollection( refCollXML );
        return fuGEType;
    }

    public ReferenceableCollection getLatestVersion(
            ReferenceableCollection referenceableCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        referenceableCollection = ( ReferenceableCollection ) cd.getLatestVersion( referenceableCollection, ci );

        // prepare updated set
        Set<BibliographicReference> set = new HashSet<BibliographicReference>();

        // load all the latest versions into the new set.
        for ( Object obj : referenceableCollection.getAllBibliographicReferences() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set.add( ( BibliographicReference ) ci.getLatestVersion( identifiable ) );
        }
        referenceableCollection.setAllBibliographicReferences( set );

        // prepare updated set
        Set<Database> set1 = new HashSet<Database>();

        // load all the latest versions into the new set.
        for ( Object obj : referenceableCollection.getRcToDatabase() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            Database database = ( Database ) ci.getLatestVersion( identifiable );

            // prepare updated set
            Set<ContactRole> set2 = new HashSet<ContactRole>();

            // load all the latest versions into the new set.
            for ( Object obj2 : database.getDatabaseContact() ) {
                set2.add( ccr.getLatestVersion( ( ContactRole ) obj2, ci ) );
            }
            database.setDatabaseContact( set2 );

            set1.add( database );
        }
        referenceableCollection.setRcToDatabase( set1 );

        return referenceableCollection;
    }

}
