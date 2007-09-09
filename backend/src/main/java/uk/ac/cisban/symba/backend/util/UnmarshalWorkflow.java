package uk.ac.cisban.symba.backend.util;

import uk.ac.cisban.symba.backend.util.conversion.xml.WorkflowUnmarshaler;

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
 */
public class UnmarshalWorkflow {

    public static void main( String[] args ) throws Exception {
        try {
            if ( args.length < 2 )
                throw new java.lang.Exception(
                        "You must provide at least 2 arguments: output-logfile input-xml-file [other input xmlfiles]" );

            System.out.println( "args[0]: " + args[0] );
            WorkflowUnmarshaler unmarshaler = new WorkflowUnmarshaler( args[0] );
            unmarshaler.Jaxb2ToFuGE( args );

        } catch ( UnmarshalException ue ) {
            // The JAXB specification does not mandate how the JAXB provider
            // must behave when attempting to unmarshal invalid XML data.  In
            // those cases, the JAXB provider is allowed to terminate the
            // call to unmarshal with an UnmarshalException.
            System.err.println( "Caught UnmarshalException:" );
            ue.printStackTrace();
        } catch ( JAXBException je ) {
            je.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        } catch ( URISyntaxException e ) {
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
