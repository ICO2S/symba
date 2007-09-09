package uk.ac.cisban.symba.backend.util;

import uk.ac.cisban.symba.backend.util.conversion.xml.PeopleUnmarshaler;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.IOException;
import java.net.URISyntaxException;

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

public class UnmarshalPeople {

    public static void main( String[] args ) throws Exception {
        try {

            if ( args.length != 1 )
                throw new java.lang.Exception( "You must provide 1 argument: input-xml-file" );

            System.out.println( "args[0]: " + args[0] );
            PeopleUnmarshaler peopleUnmarshaler = new PeopleUnmarshaler( args[0] );
            peopleUnmarshaler.Jaxb2ToFuGE();
        } catch ( UnmarshalException ue ) {
            // The JAXB specification does not mandate how the JAXB provider
            // must behave when attempting to unmarshal invalid XML data.  In
            // those cases, the JAXB provider is allowed to terminate the
            // call to unmarshal with an UnmarshalException.
            System.err.println( "Caught UnmarshalException:" );
            ue.printStackTrace();
        } catch ( JAXBException je ) {
            System.err.println( "Caught JAXBException:" );
            je.printStackTrace();
        } catch ( IOException ioe ) {
            System.err.println( "Caught IOException:" );
            ioe.printStackTrace();
        } catch ( URISyntaxException e ) {
            System.err.println( "Caught URIException:" );
            e.printStackTrace();
        } catch ( fugeOM.service.RealizableEntityServiceException e ) {
            System.err.println( "Caught RealizableEntityServiceException:" );
            e.printStackTrace();
        } catch ( Exception e ) {
            System.err.println( "Caught General Exception:" );
            e.printStackTrace();
        }
    }
}
