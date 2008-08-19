package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.webapp.util.SecurityEngineInterrogator;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/loading/LoadFuge.java $
 */
public class FugeLoader {

    public static FuGE loadFugeIntoDatabase( FuGE fuge, Person auditor ) {

        // Load the entire fuge entry into the database
        fuge = ( FuGE ) DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.FuGE", fuge, auditor );

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

        return fuge;
    }

}
