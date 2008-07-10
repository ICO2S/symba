package net.sourceforge.symba.obiloader.examples;

import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;
import org.xml.sax.SAXException;
import net.sourceforge.symba.obiloader.conversion.ObiManipulator;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.Properties;
import java.net.URISyntaxException;

import fugeOM.util.generatedJAXB2.FugeOMCollectionOntologyCollectionType;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * This file is part of the sbml2owl package.
 * <p/>
 * Copyright (C) 2007-8, Allyson Lister and her employers
 * <p/>
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the Subversion history.
 * <p/>
 * To view the full licensing information for this software and ALL
 * other packages contained in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $Author:  $
 * $HeadURL: $
 */

public class CreateFuGEOntologyCollection {
    public static void main( String[] args ) throws IOException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, OWLOntologyCreationException, OWLReasonerException, IllegalAccessException, InstantiationException, URISyntaxException, JAXBException, SAXException {

        if ( args.length != 1 ) {
            usage();
            return;
        }

        // set all properties
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream( args[0] );
        properties.load( fis );


        ObiManipulator obiManipulator = new ObiManipulator(
                properties.getProperty( "net.sourceforge.symba.obiloader.logicalObiUri" ).trim(),
                properties.getProperty( "net.sourceforge.symba.obiloader.physicalObiDirectory" ).trim() );

        long start = System.currentTimeMillis();

        String[] sources = properties.getProperty( "net.sourceforge.symba.obiloader.ontologySources" ).split( "[,[ ]]" );

        FugeOMCollectionOntologyCollectionType finalOntologyCollection = new FugeOMCollectionOntologyCollectionType();

        for ( String source : sources ) {
            if ( !source.trim().isEmpty() ) {
                Set<OWLDescription> owlDescriptions = obiManipulator.getChildren( source.trim() );
//                Set<OWLDescription> owlDescriptions = obiManipulator.getAllSubclasses( source.trim() );

                System.out.println( "Number of Children of " + source.trim() + " = " + owlDescriptions.size() );

                obiManipulator.printSummaryOWLDescriptions( System.out, owlDescriptions );

                finalOntologyCollection = obiManipulator.createFuGEOntologyCollection( finalOntologyCollection, source.trim(), owlDescriptions );

                float elapsed = System.currentTimeMillis() - start;
                System.out.println( "Time to retrieve children (seconds): " + elapsed / 1000 );
            }
        }

        // print out the final ontology collection
        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

        // create a Marshaller
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        // fixme set the correct schema - temporary workaround as classloader not working for some reason
        SchemaFactory schemaFactory = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
        Schema schema = schemaFactory.newSchema( new File( properties.getProperty( "net.sourceforge.symba.obiloader.physicalSchemaLocation" ).trim() ) );

        marshaller.setSchema( schema );

        @SuppressWarnings( "unchecked" )
        JAXBElement element = new JAXBElement( new QName( "http://fuge.org/core", "OntologyCollection" ), FugeOMCollectionOntologyCollectionType.class, finalOntologyCollection );

        FileOutputStream fos = new FileOutputStream( new File(properties.getProperty( "net.sourceforge.symba.obiloader.outputFile")) );
        marshaller.marshal( element, fos );
    }

    private static void usage() {
        System.out.println( "Usage: CreateFuGEOntologyCollection properties-file" );
    }
}
