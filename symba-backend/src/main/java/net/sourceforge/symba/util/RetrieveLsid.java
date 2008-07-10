package net.sourceforge.symba.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import net.sourceforge.symba.lsid.webservices.service.LsidAssigner;

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

public class RetrieveLsid {
    // get the context of the web services
    // use the one below if using within the web interface / tomcat
    private static final ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext( ClassLoader.getSystemClassLoader().getResource("/client-beans.xml").toString() );
    // use the one below if not using within the web interface / tomcat
//    private static final ClassPathXmlApplicationContext context
//            = new ClassPathXmlApplicationContext( "/client-beans.xml" );
    // get the assigner
    private static final LsidAssigner assigner = ( LsidAssigner ) context.getBean( "clientAssigner" );

    public static String getLSID( String namespace ) {
        // create the new LSID
        return assigner.assignLSID( namespace );
    }

    public static void main( String[] args ) {

        System.err.println( "Retrieving LSIDs" );
        for ( int i = 0; i < 5; i++ ) {
            String lsid = getLSID( args[0] );
            System.err.println( lsid );
        }
    }
}
