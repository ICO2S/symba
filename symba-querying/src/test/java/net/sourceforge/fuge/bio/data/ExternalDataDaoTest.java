package net.sourceforge.fuge.bio.data;

import org.testng.annotations.Test;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.versioning.Endurant;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;
import net.sourceforge.fuge.service.EntityService;

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
 * $HeadURL$
 */
public class ExternalDataDaoTest {

    @Test( groups = { "counters", "data", "hibernate" } )
    public void countDataSimpleTest() {

        // we don't know how many data files are already in the database, so instead we will compare the
        // number of experiments before and after this test has been run.

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        long startNumber = ses.countData();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 experiments, each of which has its own Endurant as ExternalData objects always will.
        // Further, none of these are Dummy objects, so all should get counted.
        writeExternalData( "countDataSimpleTest1", false, gc.getTime() );
        writeExternalData( "countDataSimpleTest2", false, gc.getTime() );
        writeExternalData( "countDataSimpleTest3", false, gc.getTime() );

        long endNumber = ses.countData();

        assert ( endNumber - startNumber == 3 ) :
                "3 ExternalData objects, none with the Dummy keyword, have been loaded. However, " +
                "the starting number of ExternalData objects was " + startNumber + " and the " +
                "ending number of ExternalData objects was " + endNumber + ". The difference in these values " +
                "should have been 3";

    }

    @Test( groups = { "counters", "data", "hibernate" } )
    public void countDataComplexTest() {

        // we don't know how many experiments are already in the database, so instead we will compare the
        // number of experiments before and after this test has been run.

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        long startNumber = ses.countData();

        // We're not testing the Audit Trail here, so just use the same date for each.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 experiments, each of which has its own Endurant as ExternalData objects always will.
        // One of these is a Dummy object, so the total count should be 2.
        writeExternalData( "countDataComplexTest1", false, gc.getTime() );
        writeExternalData( "countDataComplexTest2", false, gc.getTime() );
        writeExternalData( "countDataComplexTest2a", true, gc.getTime() );

        long endNumber = ses.countData();

        assert ( endNumber - startNumber == 2 ) :
                "3 ExternalData objects, two without the Dummy keyword and one with, have been loaded. However, " +
                "the starting number of ExternalData objects was " + startNumber + " and the " +
                "ending number of ExternalData objects was " + endNumber + ". The difference in these values should " +
                "have been 2";
    }

    @Test( groups = { "data", "hibernate" } )
    public void getLatestExternalDataSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        ExternalData one = writeExternalData( "getLatestExternalDataSimpleTest1", false, gc.getTime() );
        ExternalData two = writeExternalData( "getLatestExternalDataSimpleTest2", false, gc.getTime() );
        ExternalData three = writeExternalData( "getLatestExternalDataSimpleTest3", true, gc.getTime() );

        List fullResults = ses.getLatestExternalData( false );

        assert ( fullResults.contains( one ) ) :
                "ExternalDatas do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "ExternalDatas do not contain experiment " + two.getIdentifier();
        assert ( fullResults.contains( three ) ) :
                "ExternalDatas do not contain experiment " + three.getIdentifier();
    }


    @Test( groups = { "data", "hibernate" } )
    public void getLatestExternalDataComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        ExternalData one = writeExternalData( "getLatestExternalDatasComplexTest1", false, gc.getTime() );
        ExternalData three =
                writeExternalData( "getLatestExternalDatasComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        ExternalData two =
                writeExternalData( "getLatestExternalDatasComplexTest2", false, gc.getTime(), one.getEndurant() );
        ExternalData four =
                writeExternalData( "getLatestExternalDatasComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestExternalData( false );

        assert ( !fullResults.contains( one ) ) :
                "ExternalDatas contain experiment that is an older version: " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "ExternalDatas do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "ExternalDatas contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "ExternalDatas do not contain experiment " + four.getIdentifier();
    }

    @Test( groups = { "data", "hibernate" } )
    public void getLatestExternalDataDummiesSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        ExternalData one = writeExternalData( "getLatestExternalDatasDummiesSimpleTest1", true, gc.getTime() );
        ExternalData two = writeExternalData( "getLatestExternalDatasDummiesSimpleTest2", true, gc.getTime() );
        ExternalData three = writeExternalData( "getLatestExternalDatasDummiesSimpleTest3", false, gc.getTime() );

        List fullResults = ses.getLatestExternalData( true );

        assert ( fullResults.contains( one ) ) :
                "ExternalDatas do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "ExternalDatas do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "ExternalDatas contains experiment " + three.getIdentifier();
    }


    @Test( groups = { "data", "hibernate" } )
    public void getLatestExternalDataDummiesComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        ExternalData one = writeExternalData( "getLatestExternalDatasDummiesComplexTest1", false, gc.getTime() );
        ExternalData three =
                writeExternalData( "getLatestExternalDatasDummiesComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        ExternalData two =
                writeExternalData( "getLatestExternalDatasDummiesComplexTest2", false, gc.getTime(), one.getEndurant() );
        ExternalData four =
                writeExternalData( "getLatestExternalDatasDummiesComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestExternalData( true );

        assert ( !fullResults.contains( one ) ) :
                "ExternalDatas contain experiment that is an older version: " + one.getIdentifier();
        assert ( !fullResults.contains( two ) ) :
                "ExternalDatas contains experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "ExternalDatas contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "ExternalDatas do not contain experiment " + four.getIdentifier();
    }


    private ExternalData writeExternalData( String name, boolean setupDummies, Date time  ) {
        
        return writeExternalData( name, setupDummies, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private ExternalData writeExternalData( String name, boolean setupDummies, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        ExternalData externalData = ( ExternalData ) es.createIdentifiable( name + ":ExternalData:" + String.valueOf( Math.random() * 10000 ), name,
                "net.sourceforge.fuge.bio.data.ExternalData" );

        if ( setupDummies ) {
            externalData.setName( externalData.getName() + " Dummy" );
        }

        // add an Endurant value (required) after saving the endurant to the database
        externalData.setEndurant( endurant );

        // ExternalData objects must have a location.
        externalData.setLocation( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        externalData.setAuditTrail( audits );
        externalData = ( ExternalData ) es.create( "net.sourceforge.fuge.bio.data.ExternalData", externalData );

        return externalData;

    }


}
