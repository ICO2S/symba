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
public class GenericProtocolDaoTest {
    
    @Test( groups = { "protocol", "hibernate" } )
    public void getLatestGenericProtocolsSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocol one = writeGenericProtocol( "getLatestGenericProtocolsSimpleTest1", gc.getTime() );
        GenericProtocol two = writeGenericProtocol( "getLatestGenericProtocolsSimpleTest2", gc.getTime() );
        GenericProtocol three = writeGenericProtocol( "getLatestGenericProtocolsSimpleTest3", gc.getTime() );

        List fullResults = ses.getLatestGenericProtocols( );

        assert ( fullResults.contains( one ) ) :
                "GenericProtocols do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericProtocols do not contain experiment " + two.getIdentifier();
        assert ( fullResults.contains( three ) ) :
                "GenericProtocols do not contain experiment " + three.getIdentifier();
    }


    @Test( groups = {"protocol",  "hibernate" } )
    public void getLatestGenericProtocolsComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericProtocol one = writeGenericProtocol( "getLatestGenericProtocolsComplexTest1", gc.getTime() );
        GenericProtocol three =
                writeGenericProtocol( "getLatestGenericProtocolsComplexTest3", gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        GenericProtocol two =
                writeGenericProtocol( "getLatestGenericProtocolsComplexTest2", gc.getTime(), one.getEndurant() );
        GenericProtocol four =
                writeGenericProtocol( "getLatestGenericProtocolsComplexTest4", gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestGenericProtocols( );

        assert ( !fullResults.contains( one ) ) :
                "GenericProtocols contain experiment that is an older version: " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericProtocols do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericProtocols contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "GenericProtocols do not contain experiment " + four.getIdentifier();
    }

    private GenericProtocol writeGenericProtocol( String name, Date time ) {
        return writeGenericProtocol( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private GenericProtocol writeGenericProtocol( String name, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        GenericProtocol genericProtocol =
                ( GenericProtocol ) es.createIdentifiable( name + ":GenericProtocol:" +
                                                                                     String.valueOf(
                                                                                             Math.random() * 10000 ),
                name, "net.sourceforge.fuge.common.protocol.GenericProtocol" );

        // add an Endurant value (required) after saving the endurant to the database
        genericProtocol.setEndurant( endurant );

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
