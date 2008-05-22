package net.sourceforge.symba.util;

import org.xml.sax.SAXException;
import net.sourceforge.symba.util.conversion.xml.RandomXMLGenerator;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/*
 * This file is part of SyMBA.
 *  SyMBA is covered under the GNU Lesser General Public License (LGPL).
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

public class GenerateRandomXML {
    public static void main( String[] args ) throws Exception {

        try {
            if ( args.length != 2 )
                throw new java.lang.Exception( "You must provide 2 arguments in this order: schema-file output-xml-file" );

            RandomXMLGenerator xml = new RandomXMLGenerator( args[0], args[1] );
            xml.generate();

        } catch ( JAXBException je ) {
            System.err.println( "JAXB Exception:" );
//            try {
//                os.flush();
//                System.err.println( "Output buffer flushed." );
//            } catch ( IOException e ) {
//                System.err.println( "Internal IO Exception when flushing buffer" );
//                e.printStackTrace();
//            } catch ( NullPointerException e ) {
//                System.err.println( "Null Pointer Exception when flushing buffer" );
//                e.printStackTrace();
//            }
            je.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
