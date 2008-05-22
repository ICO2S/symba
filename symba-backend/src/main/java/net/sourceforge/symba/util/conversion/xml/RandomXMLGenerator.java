package net.sourceforge.symba.util.conversion.xml;

import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import org.xml.sax.SAXException;
import net.sourceforge.symba.util.conversion.helper.CisbanFuGEHelper;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

public class RandomXMLGenerator {
    private final String schemaFilename, XMLFilename;

    public RandomXMLGenerator( String sf, String xf ) {
        this.schemaFilename = sf;
        this.XMLFilename = xf;
    }

    public void generate() throws JAXBException, SAXException, FileNotFoundException {
        OutputStream os;
        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

        // create a Marshaller
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        // set the correct schema
        SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
        Schema schema = sf.newSchema( new File( schemaFilename ) );
        m.setSchema( schema );

        CisbanFuGEHelper cf = new CisbanFuGEHelper();

        // create a jaxb root object
        System.err.println( "Starting generation..." );

        String name = XMLFilename.substring( 0, XMLFilename.lastIndexOf( "." ) );
        String ext = XMLFilename.substring( XMLFilename.lastIndexOf( "." ) );

        // make 3 versions of the file
        for ( int i = 0; i < 3; i++ ) {

            FugeOMCollectionFuGEType frXML = cf.generateRandomXML();

            os = new FileOutputStream( name + String.valueOf( i ) + ext );
            m.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "FuGE" ), FugeOMCollectionFuGEType.class, frXML ),
                    os );
        }
    }
}
