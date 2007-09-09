package uk.ac.cisban.symba.backend.util;

import com.ibm.lsid.LSID;
import com.ibm.lsid.LSIDException;
import com.ibm.lsid.client.LSIDAssigner;
import com.ibm.lsid.wsdl.SOAPLocation;

import java.util.Properties;

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
    private static final Properties EMPTY_PROPS = new Properties();
    private static final String soapLocation = "http://metagenome.ncl.ac.uk:8081/authority/assigning";
//    private static final String soapLocation = "http://localhost:8180/authority/assigning";

    //    private static final LSIDAssigner DEFAULT_ASSIGNER = new LSIDAssigner( new SOAPLocation( "http://cisbclust:8080/authority/assigning" ) );
    private static final LSIDAssigner DEFAULT_ASSIGNER = new LSIDAssigner(
            new SOAPLocation( soapLocation ) );
    private static final String DEFAULT_DOMAIN_NAME = "cisban.cisbs.org";

    private static final LSIDAssigner assigner = DEFAULT_ASSIGNER;
    private static final String domainName = DEFAULT_DOMAIN_NAME;

    public static String getLSID( String namespace ) throws LSIDException {
        // create the new LSID
        LSID lsid = assigner.assignLSID( domainName, namespace, EMPTY_PROPS );
        return lsid.toString();
    }

    public static void main( String[] args ) throws LSIDException {

        System.err.println( "Retrieving LSIDs from " + soapLocation );
        for ( int i = 0; i < 5; i++ ) {
            String lsid = getLSID( args[0] );
            System.err.println( lsid );
        }
    }
}
