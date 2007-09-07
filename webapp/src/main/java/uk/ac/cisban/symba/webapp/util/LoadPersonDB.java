package uk.ac.cisban.symba.webapp.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate:$
 * $LastChangedRevision:$
 * $Author:$
 * $HeadURL:$
 *
 */
public class LoadPersonDB {

    /**
     * Creates a new instance of LoadPersonDB
     */
    public static void main( String[] args ) throws FileNotFoundException {
        new LoadPersonDB().loadPeople(
                "/media/share/unsynched/IdeaProjects/DataPortal/trunk/CISBANFugeM3/interface/xml/cisban/People.xml" );
//    new LoadPersonDB().loadPeople("C:\\svnWork\\DataPortal\\trunk\\CISBANFugeM3\\interface\\xml\\cisban\\People.xml");
    }


    /*
    * This parses the file using JDOM and then calls the DB method
    *
    */
    public void loadPeople( String filePath ) throws FileNotFoundException {
        Document doc = null;
        File afile = new File( filePath );
        Scanner scan = new Scanner( afile );
        while ( scan.hasNextLine() ) {
            String line = scan.nextLine();
            //System.out.println(line);
        }

        try {
            SAXBuilder sBuilder = new SAXBuilder();
            doc = sBuilder.build( afile );
            Element root = doc.getRootElement();
            // System.out.println(root);
            List childs = root.getChildren();
            System.out.println( "Size of Children of Root Element: " + childs.size() );
            List<Element> people;
            List<UserPassword> userpassList = new LinkedList<UserPassword>();

            people = root.getChildren();
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
            for ( UserPassword up : userpassList ) {
                System.out.println( up.getUsername() + "\t" + up.getPassword() + "\t" + up.getEndID() );
            }
            loadInDB( userpassList );
        }

        catch ( JDOMException e ) {
            // System.out.println(afile.getName() + " is not well-formed.");
            System.out.println( e.getMessage() );
        }
        catch ( IOException e ) {
            System.out.println( e );
        }
    }


    /*
    * This is where the DB update happens
    */
    public void loadInDB( List<UserPassword> upList ) {
        try {
            Class.forName( "org.postgresql.Driver" );
        }
        catch ( ClassNotFoundException cnfe ) {
            System.out.println( "Couldn't find the driver!" );
            System.out.println( "Let's print a stack trace, and exit." );
            cnfe.printStackTrace();
            System.exit( 1 );
        }
        Connection c = null;

        try {
            // The second and third arguments are the username and password,
            // respectively. They should be whatever is necessary to connect
            // to the database.
            Statement s = null;
            c = DriverManager.getConnection(
                    "jdbc:postgresql://metagenome.ncl.ac.uk:5433/dpi_security",
                    "dpi", "c15b4n_dpi" );
            s = c.createStatement();
            for ( UserPassword up : upList ) {
                String username = up.getUsername();
                String password = up.getPassword();
                String endurant = up.getEndID();
                int m = 0;
                String insertStmt = "INSERT INTO USERS VALUES " +
                        "('" + username + "', '" + password + "', '" + endurant + "')";
                System.out.println( insertStmt );
                m = s.executeUpdate( insertStmt );
            }
        }
        catch ( SQLException se ) {
            System.out.println( "Couldn't connect or error with query: print out a stack trace and exit." );
            se.printStackTrace();
            System.exit( 1 );
        }

        if ( c != null )
            System.out.println( "Hooray! We connected to the database!" );
        else
            System.out.println( "We should never get here." );
    }

}

