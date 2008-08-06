package net.sourceforge.symba.webapp.util.security;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
public class SecurityPeopleLoader {

    /*
    * This parses a file structured like symba-backend/xml/samples/SamplePeople.xml using JDOM and then calls the DB method
    *
    */
    public List<UserPassword> loadPeopleFromFile( String filePath ) throws IOException, JDOMException {
        List<UserPassword> userpassList = new LinkedList<UserPassword>();

        Document doc;
        File afile = new File( filePath );

        SAXBuilder sBuilder = new SAXBuilder();
        doc = sBuilder.build( afile );
        Element root = doc.getRootElement();
        // System.out.println(root);
        List childs = root.getChildren();
        System.out.println( "Size of Children of Root Element: " + childs.size() );

        @SuppressWarnings("unchecked")
        List<Element> people = root.getChildren();
        System.out.println( "Size of People.xml: " + people.size() );
        for ( Element el : people ) {
            if ( el.getName().equals( "Person" ) ) {

//                    System.out.println( "Last Name: " + el.getAttributeValue( "lastName" ) );
//                    System.out.println( "First Name: " + el.getAttributeValue( "firstName" ) );
                String userName = el.getAttributeValue( "firstName" ) +
                        el.getAttributeValue( "lastName" ).subSequence( 0, 1 );

                String password =
                        el.getAttributeValue( "lastName" ) + String.valueOf( Math.random() ).substring( 2, 5 );
                String endurant = el.getAttributeValue( "endurant" );

                //System.out.println(userName  +"\t" + password);
                UserPassword up = new UserPassword( userName, password, endurant );
                userpassList.add( up );
            }
        }
        return userpassList;
    }


    /*
    * This is where the DB update happens
    */
    public void loadIntoSecurityDB( List<UserPassword> upList ) throws SQLException {
        ResourceBundle rb = ResourceBundle.getBundle( "symba" );

        try {
            Class.forName( rb.getString("security.driver") );
        }
        catch ( ClassNotFoundException cnfe ) {
            System.out.println( "Couldn't find the driver! Exiting." );
            cnfe.printStackTrace();
            System.exit( 1 );
        }
        Connection c;

        Statement s;

        // get the connection details from the properties file.

        c = DriverManager.getConnection(
                rb.getString("security.url"), rb.getString("security.username"), rb.getString("security.password") );
        s = c.createStatement();
        for ( UserPassword up : upList ) {
            String username = up.getUsername();
            String password = up.getPassword();
            String endurant = up.getEndID();
            String insertStmt = "INSERT INTO USERS VALUES " +
                    "('" + username + "', '" + password + "', '" + endurant + "')";
            System.out.println( insertStmt );
            s.executeUpdate( insertStmt );
        }

    }

    public List<UserPassword> loadUserPassFromFile( String filename ) throws IOException {

        List<UserPassword> userPasswords = new LinkedList<UserPassword>();

        File file = new File( filename );

        Scanner scanner = new Scanner( file );
        while ( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            System.out.println( "line = " + line );
            StringTokenizer st = new StringTokenizer( line );
            while ( st.hasMoreTokens() ) {
                UserPassword userPassword = new UserPassword( st.nextToken(), st.nextToken(), st.nextToken() );
                userPasswords.add( userPassword );
                if ( st.hasMoreTokens() ) {
                    System.err.println( "Error reading Username/Password file." );
                    throw new IOException();
                }
            }
        }
        return userPasswords;
    }
}

