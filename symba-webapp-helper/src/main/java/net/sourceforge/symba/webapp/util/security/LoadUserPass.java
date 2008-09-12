package net.sourceforge.symba.webapp.util.security;

import org.jdom.JDOMException;

import java.util.List;
import java.io.IOException;
import java.sql.SQLException;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/security/CreateUserPassAndLoad.java $
 *
 */

public class LoadUserPass {

    public static void main( String[] args ) {
        try {
            if ( args.length != 1 )
                throw new java.lang.Exception(
                        "You must provide 1 argument containing the location of the input plaintext file in format \"username\tpassword\tendurant\"." );

            System.out.println( "plaintext file containing the people to load: " + args[0] );
            SecurityPeopleLoader securityPeopleLoader = new SecurityPeopleLoader();

            List<UserPassword> userPasswords = securityPeopleLoader.loadUserPassFromFile( args[0] );
            for ( UserPassword up : userPasswords ) {
                System.out.println( up.getUsername() + "\t" + up.getPassword() + "\t" + up.getEndID() );
            }
            securityPeopleLoader.loadIntoSecurityDB( userPasswords );
        } catch ( JDOMException e ) {
            System.err.println( "Error parsing XML" );
            e.printStackTrace();
//            System.out.println( e.getMessage() );
        }
        catch ( IOException e ) {
            System.err.println( "Error in I/O" );
            e.printStackTrace();
//            System.out.println( e );
        } catch ( SQLException se ) {
            System.err.println( "Couldn't connect to the database, or error with query." );
            se.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }
}

