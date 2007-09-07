package uk.ac.cisban.symba.backend.util;

import fugeOM.util.generatedJAXB2.*;
import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/GenerateOntologyIndividuals.java $
 *
 */

public class GenerateOntologyIndividuals {
    private final String schemaFilename, inputListFilename, XMLFilename, ontoSourceName;

    private GenerateOntologyIndividuals( String sf, String ilf, String xf, String ontologySourceName ) {
        this.schemaFilename = sf;
        this.inputListFilename = ilf;
        this.XMLFilename = xf;
        this.ontoSourceName = ontologySourceName;
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 4 )
            throw new java.lang.Exception(
                    "You must provide 4 arguments in this order: schema-file input-list output-xml-file OntologySourceName" );

        GenerateOntologyIndividuals xml = new GenerateOntologyIndividuals( args[0], args[1], args[2], args[3] );
        xml.generate();
    }

    private void generate() {
        OutputStream os = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader( new InputStreamReader( new FileInputStream( inputListFilename ) ) );

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

            // create a jaxb root object
            System.err.println( "Starting generation..." );

//            String name = XMLFilename.substring( 0, XMLFilename.lastIndexOf( "." ) );
//            String ext = XMLFilename.substring( XMLFilename.lastIndexOf( "." ) );

//            FugeOMCollectionFuGEType frXML = cf.generateRandomXML();
            FugeOMCollectionFuGEType frXML = new FugeOMCollectionFuGEType();
            frXML.setIdentifier( "Fake Identifier" );
            frXML.setEndurant( "Fake Endurant" );

            FugeOMCollectionOntologyCollectionType ontoCollXML = new FugeOMCollectionOntologyCollectionType();

            // first make the ontology source object
            FugeOMCommonOntologyOntologySourceType sourceType = new FugeOMCommonOntologyOntologySourceType();
            sourceType.setEndurant( RetrieveLsid.getLSID( "OntoSourceEndurant" ) );
            sourceType.setIdentifier( RetrieveLsid.getLSID( "OntologySource" ) );
            sourceType.setName( ontoSourceName );
            sourceType.setOntologyURI( "http://www.cisban.ac.uk" );
            ontoCollXML.getOntologySource().add( sourceType );

            String readIn;
            ObjectFactory factory = new ObjectFactory();
            while ( ( readIn = br.readLine() ) != null ) {
//                System.out.println( "Read in " + readIn + "|" );
                FugeOMCommonOntologyOntologyIndividualType individualType = new FugeOMCommonOntologyOntologyIndividualType();
                individualType.setEndurant( RetrieveLsid.getLSID( "OntoIndvEndurant" ) );
                individualType.setIdentifier( RetrieveLsid.getLSID( "OntologyIndividual" ) );
                individualType.setTermAccession( "accession to come" );
                individualType.setTerm( readIn.trim() );
                individualType.setOntologySourceRef( sourceType.getIdentifier() );
//                FugeOMCommonDescribableType.PropertySets ps = individualType.getPropertySets();
                ontoCollXML.getOntologyTerm().add( factory.createOntologyIndividual( individualType ) );
            }

            frXML.setOntologyCollection( ontoCollXML );
            os = new FileOutputStream( XMLFilename );
            m.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "FuGE" ), FugeOMCollectionFuGEType.class, frXML ), os );

        } catch ( JAXBException je ) {
            System.err.println( "JAXB Exception:" );
            try {
                os.flush();
                System.err.println( "Output buffer flushed." );
            } catch ( IOException e ) {
                System.err.println( "Internal IO Exception when flushing buffer" );
                e.printStackTrace();
            } catch ( NullPointerException e ) {
                System.err.println( "Null Pointer Exception when flushing buffer" );
                e.printStackTrace();
            }
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
