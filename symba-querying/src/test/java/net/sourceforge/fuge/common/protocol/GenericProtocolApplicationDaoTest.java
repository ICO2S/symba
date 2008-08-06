package net.sourceforge.fuge.common.protocol;

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
public class GenericProtocolApplicationDaoTest {
    
    @Test( groups = { "protocol", "hibernate" } )
    public void getLatestGenericProtocolApplicationsSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocolApplication one = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsSimpleTest1", false, gc.getTime() );
        GenericProtocolApplication two = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsSimpleTest2", false, gc.getTime() );
        GenericProtocolApplication three = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsSimpleTest3", true, gc.getTime() );

        List fullResults = ses.getLatestGenericProtocolApplications( false );

        assert ( fullResults.contains( one ) ) :
                "GenericProtocolApplications do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericProtocolApplications do not contain experiment " + two.getIdentifier();
        assert ( fullResults.contains( three ) ) :
                "GenericProtocolApplications do not contain experiment " + three.getIdentifier();
    }


    @Test( groups = { "protocol", "hibernate" } )
    public void getLatestGenericProtocolApplicationsComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocolApplication one = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsComplexTest1", false, gc.getTime() );
        GenericProtocolApplication three =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        GenericProtocolApplication two =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsComplexTest2", false, gc.getTime(), one.getEndurant() );
        GenericProtocolApplication four =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestGenericProtocolApplications( false );

        assert ( !fullResults.contains( one ) ) :
                "GenericProtocolApplications contain experiment that is an older version: " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericProtocolApplications do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericProtocolApplications contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "GenericProtocolApplications do not contain experiment " + four.getIdentifier();
    }

    @Test( groups = { "protocol", "hibernate" } )
    public void getLatestGenericProtocolApplicationsDummiesSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocolApplication one = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesSimpleTest1", true, gc.getTime() );
        GenericProtocolApplication two = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesSimpleTest2", true, gc.getTime() );
        GenericProtocolApplication three = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesSimpleTest3", false, gc.getTime() );

        List fullResults = ses.getLatestGenericProtocolApplications( true );

        assert ( fullResults.contains( one ) ) :
                "GenericProtocolApplications do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericProtocolApplications do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericProtocolApplications contains experiment " + three.getIdentifier();
    }


    @Test( groups = { "protocol", "hibernate" } )
    public void getLatestGenericProtocolApplicationsDummiesComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocolApplication one = writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesComplexTest1", false, gc.getTime() );
        GenericProtocolApplication three =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        GenericProtocolApplication two =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesComplexTest2", false, gc.getTime(), one.getEndurant() );
        GenericProtocolApplication four =
                writeGenericProtocolApplication( "getLatestGenericProtocolApplicationsDummiesComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestGenericProtocolApplications( true );

        assert ( !fullResults.contains( one ) ) :
                "GenericProtocolApplications contain experiment that is an older version: " + one.getIdentifier();
        assert ( !fullResults.contains( two ) ) :
                "GenericProtocolApplications contains experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericProtocolApplications contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "GenericProtocolApplications do not contain experiment " + four.getIdentifier();
    }

    private GenericProtocolApplication writeGenericProtocolApplication( String name, boolean setupDummies, Date time ) {
        return writeGenericProtocolApplication( name, setupDummies, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private GenericProtocolApplication writeGenericProtocolApplication( String name, boolean setupDummies, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        GenericProtocolApplication genericProtocolApplication =
                ( GenericProtocolApplication ) es.createIdentifiable( name + ":GenericProtocolApplication:" +
                                                                                     String.valueOf(
                                                                                             Math.random() * 10000 ),
                name, "net.sourceforge.fuge.common.protocol.GenericProtocolApplication" );

        if ( setupDummies ) {
            genericProtocolApplication.setName( genericProtocolApplication.getName() + " Dummy" );
        }

        // add an Endurant value (required) after saving the endurant to the database
        genericProtocolApplication.setEndurant( endurant );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        // GPAs must have a protocol
        GenericProtocol genericProtocol = writeGenericProtocol("writeGenericProtocolApplication::Protocol", time);

        genericProtocolApplication.setProtocol( genericProtocol );

        genericProtocolApplication.setAuditTrail( audits );
        genericProtocolApplication =
                ( GenericProtocolApplication ) es.create( "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", genericProtocolApplication );

        return genericProtocolApplication;

    }

    private GenericProtocol writeGenericProtocol( String name, Date time) {

        EntityService es = ServiceLocator.instance().getEntityService();

        GenericProtocol genericProtocol =
                ( GenericProtocol ) es.createIdentifiable( name + ":GenericProtocol:" +
                                                                                     String.valueOf(
                                                                                             Math.random() * 10000 ),
                name, "net.sourceforge.fuge.common.protocol.GenericProtocol" );

        // add an Endurant value (required) after saving the endurant to the database
        genericProtocol.setEndurant( DatabaseObjectHelper.getOrLoadEndurant( null, null ) );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        genericProtocol.setAuditTrail( audits );
        genericProtocol =
                ( GenericProtocol ) es.create( "net.sourceforge.fuge.common.protocol.GenericProtocol", genericProtocol );

        return genericProtocol;

    }

}