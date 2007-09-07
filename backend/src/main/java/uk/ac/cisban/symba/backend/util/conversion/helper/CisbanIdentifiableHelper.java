package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Identifiable;
import fugeOM.Common.References.BibliographicReference;
import fugeOM.Common.References.Database;
import fugeOM.Common.References.DatabaseEntry;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonIdentifiableType;
import fugeOM.util.generatedJAXB2.FugeOMCommonReferencesDatabaseEntryType;

import java.io.PrintStream;
import java.io.PrintWriter;
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
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanIdentifiableHelper.java $
 *
 */

public class CisbanIdentifiableHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final RealizableEntityService reService;
    private final CisbanDescribableHelper cd;

    public CisbanIdentifiableHelper( RealizableEntityService reService, CisbanDescribableHelper cd ) {
        this.reService = reService;
        this.cd = cd;
    }

    public CisbanDescribableHelper getCisbanDescribableHelper() {
        return cd;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Identifiable unmarshalIdentifiable( FugeOMCommonIdentifiableType identifiableXML, Identifiable identifiable )
            throws URISyntaxException, RealizableEntityServiceException {

        identifiable = ( Identifiable ) cd.unmarshalDescribable( identifiableXML, identifiable );
        identifiable.setIdentifier( identifiableXML.getIdentifier() );
        if ( identifiableXML.getName() != null )
            identifiable.setName( identifiableXML.getName() );

        Set<DatabaseEntry> databaseEntries = new HashSet<DatabaseEntry>();
        for ( FugeOMCommonReferencesDatabaseEntryType databaseEntryXML : identifiableXML.getDatabaseEntry() ) {
            DatabaseEntry databaseEntry = ( DatabaseEntry ) reService
                    .createDescribableOb( "fugeOM.Common.References.DatabaseEntry" );
            databaseEntry = ( DatabaseEntry ) cd.unmarshalDescribable( databaseEntryXML, databaseEntry );
            if ( databaseEntryXML.getAccession() != null )
                databaseEntry.setAccession( databaseEntryXML.getAccession() );
            if ( databaseEntryXML.getAccessionVersion() != null )
                databaseEntry.setAccessionVersion( databaseEntryXML.getAccessionVersion() );
            if ( databaseEntryXML.getDatabaseRef() != null ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                databaseEntry
                        .setDatabase( ( Database ) reService.findIdentifiable( databaseEntryXML.getDatabaseRef() ) );
            }
            reService.createObInDB( "fugeOM.Common.References.DatabaseEntry", databaseEntry );
            databaseEntries.add( databaseEntry );
        }

        identifiable.setDatabaseReferences( databaseEntries );

        Set<BibliographicReference> brRefs = new HashSet<BibliographicReference>();
        for ( FugeOMCommonIdentifiableType.BibliographicReferences brRefXML : identifiableXML
                .getBibliographicReferences() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            brRefs.add(
                    ( BibliographicReference ) reService.findIdentifiable( brRefXML.getBibliographicReferenceRef() ) );
        }

        identifiable.setBibliographicReferences( brRefs );

        return identifiable;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonIdentifiableType marshalIdentifiable( FugeOMCommonIdentifiableType identifiableXML,
                                                             Identifiable identifiable )
            throws URISyntaxException {

        identifiableXML = ( FugeOMCommonIdentifiableType ) cd.marshalDescribable( identifiableXML, identifiable );
        identifiableXML.setIdentifier( identifiable.getIdentifier() );
        identifiableXML.setEndurant( identifiable.getEndurant().getIdentifier() );

        if ( identifiable.getName() != null )
            identifiableXML.setName( identifiable.getName() );

        for ( Object databaseEntryObj : identifiable.getDatabaseReferences() ) {
            DatabaseEntry databaseEntry = ( DatabaseEntry ) databaseEntryObj;
            FugeOMCommonReferencesDatabaseEntryType databaseEntryXML = new FugeOMCommonReferencesDatabaseEntryType();
            databaseEntryXML = ( FugeOMCommonReferencesDatabaseEntryType ) cd
                    .marshalDescribable( databaseEntryXML, databaseEntry );
            if ( databaseEntry.getAccession() != null )
                databaseEntryXML.setAccession( databaseEntry.getAccession() );
            if ( databaseEntry.getAccessionVersion() != null )
                databaseEntryXML.setAccessionVersion( databaseEntry.getAccessionVersion() );
            if ( databaseEntry.getDatabase() != null )
                databaseEntryXML.setDatabaseRef( databaseEntry.getDatabase().getIdentifier() );
            identifiableXML.getDatabaseEntry().add( databaseEntryXML );
        }

        for ( Object brRefObj : identifiable.getBibliographicReferences() ) {
            BibliographicReference brRef = ( BibliographicReference ) brRefObj;
            FugeOMCommonIdentifiableType.BibliographicReferences brRefXML = new FugeOMCommonIdentifiableType.BibliographicReferences();
            brRefXML.setBibliographicReferenceRef( brRef.getIdentifier() );
            identifiableXML.getBibliographicReferences().add( brRefXML );
        }
        return identifiableXML;
    }

    // specifically for generating random values for use in testing. Must pass FuGE objects so other objects can be made.
    // Only calls internal to this method may pass things other than FuGE objects.
    public FugeOMCommonIdentifiableType generateRandomXML( FugeOMCommonIdentifiableType type ) {
        CisbanReferenceableCollectionHelper crc = new CisbanReferenceableCollectionHelper( reService, this );
        type = ( FugeOMCommonIdentifiableType ) cd.generateRandomXML( type, this );
        type.setIdentifier( String.valueOf( Math.random() ) );
        type.setEndurant(
                String.valueOf(
                        Math.random() ) ); // this means that, until we start passing endurantids as args, we will only ever have one version per endurant
        type.setName( String.valueOf( Math.random() ) );

        // this ensures that if smaller objects (like DatabaseEntry) are being created, there is no unneccessary attempt
        //  to create sub-objects, and additionally there will be no infinite recursion
        if ( type instanceof FugeOMCollectionFuGEType ) {
            FugeOMCollectionFuGEType fuGEType = ( FugeOMCollectionFuGEType ) type;
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonReferencesDatabaseEntryType databaseEntryXML = new FugeOMCommonReferencesDatabaseEntryType();
                databaseEntryXML = ( FugeOMCommonReferencesDatabaseEntryType ) cd
                        .generateRandomXML( databaseEntryXML, this );
                databaseEntryXML.setAccession( String.valueOf( Math.random() ) );
                databaseEntryXML.setAccessionVersion( String.valueOf( Math.random() ) );

                // This is a reference to another object, so create that object before setting the reference
                if ( fuGEType.getReferenceableCollection() == null ) {
                    fuGEType = crc.generateRandomXML( fuGEType );
                }
                // get the first object and make it what is referred.
                databaseEntryXML
                        .setDatabaseRef( fuGEType.getReferenceableCollection().getDatabase().get( i ).getIdentifier() );
                fuGEType.getDatabaseEntry().add( databaseEntryXML );
            }

            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonIdentifiableType.BibliographicReferences brRefXML = new FugeOMCommonIdentifiableType.BibliographicReferences();
                // This is a reference to another object, so create that object before setting the reference
                if ( fuGEType.getReferenceableCollection() == null ) {
                    fuGEType = crc.generateRandomXML( fuGEType );
                }
                // get the first object and make it what is referred.
                brRefXML.setBibliographicReferenceRef(
                        fuGEType.getReferenceableCollection().getBibliographicReference().get( i ).getIdentifier() );
                fuGEType.getBibliographicReferences().add( brRefXML );
            }
            return fuGEType;
        }
        return type;
    }

    // Get the latest version of any identifiable object present within this object
    public Identifiable getLatestVersion( Identifiable ident ) throws RealizableEntityServiceException {

        // prepare updated set
        Set<DatabaseEntry> set0 = new HashSet<DatabaseEntry>();

        // load all the latest versions into the new set.
        for ( Object obj : ident.getDatabaseReferences() ) {
            DatabaseEntry de = ( DatabaseEntry ) obj;

            // get the latest version of the identifiable objects within the describable object.
            de = ( DatabaseEntry ) cd.getLatestVersion( de, this );

            Identifiable identifiable = de.getDatabase();
            identifiable = ( Identifiable ) reService
                    .findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            identifiable = getLatestVersion( identifiable );
            de.setDatabase( ( Database ) identifiable );
            set0.add( de );
        }
        ident.setDatabaseReferences( set0 );

        // prepare updated set
        Set<BibliographicReference> set = new HashSet<BibliographicReference>();

        // load all the latest versions into the new set.
        for ( Object obj : ident.getBibliographicReferences() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService
                    .findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            identifiable = getLatestVersion( identifiable );
            set.add( ( BibliographicReference ) identifiable );
        }
        ident.setBibliographicReferences( set );

        return ident;
    }

    public void prettyPrint( String comment, Identifiable identifiable, PrintStream printStream ) {
        if ( identifiable.getName() != null ) {
            printStream.println( comment + identifiable.getName() + " (name)" );
        }
    }

    public void prettyHtml( String comment, Identifiable identifiable, PrintWriter printWriter ) {
        if ( comment != null ) {
            printWriter.print( comment );
        }
        // print the name of the object, if present
        if ( identifiable.getName() != null ) {
            printWriter.println( identifiable.getName() );
        }
        printWriter.flush();
    }
}