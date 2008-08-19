package net.sourceforge.symba.lsid;

import net.sourceforge.fuge.util.identification.FuGEIdentifier;
import net.sourceforge.fuge.util.identification.FuGEIdentifierFactory;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Stored within the mapping module to ensure that the Lsid Retrieval works correctly within it.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */

public class RetrieveLsid {

    private static final String DEFAULT_DOMAIN_NAME = "net.sourceforge.symba";

    private static FuGEIdentifier ID_MAKER =
            FuGEIdentifierFactory.createFuGEIdentifier( DEFAULT_DOMAIN_NAME, "/client-beans.xml" );

    public static String getLSID( String namespace ) {
        // create the new LSID
        return ID_MAKER.create( namespace );
    }

    public static void main( String[] args ) {

        System.err.println( "Retrieving LSIDs" );
        for ( int i = 0; i < 20; i++ ) {
            String lsid = getLSID( args[0] );
            System.err.println( lsid );
        }
    }
}
