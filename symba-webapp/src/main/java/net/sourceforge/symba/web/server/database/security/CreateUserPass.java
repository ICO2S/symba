package net.sourceforge.symba.web.server.database.security;

import java.util.List;
import java.io.IOException;
import java.sql.SQLException;

import org.jdom.JDOMException;

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
public class CreateUserPass {
    public static void main( String[] args ) {
        try {
            if ( args.length != 1 )
                throw new java.lang.Exception(
                        "You must provide 1 argument containing the location of the input XML file. Usernames and passwords to stdout." );

            System.out.println( "XML file containing the people to load: " + args[0] );
            SecurityPeopleLoader securityPeopleLoader = new SecurityPeopleLoader();

            List<UserPassword> userPasswords = securityPeopleLoader.loadPeopleFromFile( args[0] );
            for ( UserPassword up : userPasswords ) {
                System.out.println( up.getUsername() + "\t" + up.getPassword() + "\t" + up.getEndID() );
            }
        } catch ( JDOMException e ) {
            System.err.println( "Error parsing XML" );
            e.printStackTrace();
        }
        catch ( IOException e ) {
            System.err.println( "Error in I/O" );
            e.printStackTrace();
        } catch ( SQLException se ) {
            System.err.println( "Couldn't connect to the database, or error with query." );
            se.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }
}
