package net.sourceforge.symba.mapping.hibernatejaxb2;

import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionOntologyCollectionType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonOntologyOntologyIndividualType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonOntologyOntologySourceType;
import net.sourceforge.fuge.util.generatedJAXB2.ObjectFactory;
import net.sourceforge.symba.lsid.RetrieveLsid;
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
 * This is a very plain-jane way of generating OntologyCollection xml elements from a very basic list of
 * ontology individuals. Just provide an input file like the following:
 * accession1::term1::name1
 * accession2::term2::name2
 * [...]
 * accessionN::termN::nameN
 *
 * And you'll get out some FuGE-ML to load into your database. The ontologySource URI is hard-coded right now, so you
 * will need to modify that to suit your needs.
 *
 * Same premise as GenerateOntologyIndividuals, but uses LSIDs and allows you to pass the term name as distinct from
 * the name of the element.
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-backend/src/main/java/net/sourceforge/symba/util/GenerateOntologyIndividuals.java $
 *
 */

public class GenerateLsidOntologyIndividuals {
    private final String schemaFilename, inputListFilename, XMLFilename, ontoSourceName;

    private GenerateLsidOntologyIndividuals( String sf, String ilf, String xf, String ontologySourceName ) {
        this.schemaFilename = sf;
        this.inputListFilename = ilf;
        this.XMLFilename = xf;
        this.ontoSourceName = ontologySourceName;
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 4 )
            throw new java.lang.Exception(
                    "You must provide 4 arguments in this order: schema-file input-list output-xml-file OntologySourceName" );

        GenerateLsidOntologyIndividuals xml = new GenerateLsidOntologyIndividuals( args[0], args[1], args[2], args[3] );
        xml.generate();
    }

    private void generate() {
        OutputStream os = null;
        BufferedReader br;
        try {
            br = new BufferedReader( new InputStreamReader( new FileInputStream( inputListFilename ) ) );

            // create a JAXBContext capable of handling classes generated into
            // the net.sourceforge.fuge.util.generatedJAXB2 package
            JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generatedJAXB2" );

            // create a Marshaller
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            // set the correct schema
            SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
            Schema schema = sf.newSchema( new File( schemaFilename ) );
            m.setSchema( schema );

            // create a jaxb root object
            System.err.println( "Starting generation..." );

            FuGECollectionOntologyCollectionType ontoCollXML = new FuGECollectionOntologyCollectionType();

            // first make the ontology source object
            FuGECommonOntologyOntologySourceType sourceType = new FuGECommonOntologyOntologySourceType();
            sourceType.setEndurantRef( RetrieveLsid.getLSID( "Endurant" ) );
            sourceType.setIdentifier( RetrieveLsid.getLSID( "OntologySource" ) );
            sourceType.setName( ontoSourceName );
            sourceType.setOntologyURI( "http://www.carmen.org.uk" );
            ontoCollXML.getOntologySource().add( sourceType );

            String readIn;
            ObjectFactory factory = new ObjectFactory();
            while ( ( readIn = br.readLine() ) != null ) {
//                System.out.println( "Read in " + readIn + "|" );
                String line = readIn.trim();
                String[] columns = line.split( "::" );
                if ( columns.length == 3 ) {
                    FuGECommonOntologyOntologyIndividualType individualType = new FuGECommonOntologyOntologyIndividualType();
                    individualType.setEndurantRef( RetrieveLsid.getLSID( "Endurant" ) );
                    individualType.setIdentifier( RetrieveLsid.getLSID( "OntologyIndividual" ) );
                    individualType.setTermAccession( columns[0] );
                    individualType.setTerm( columns[1] );
                    individualType.setName( columns[2] );
                    individualType.setOntologySourceRef( sourceType.getIdentifier() );
//                FuGECommonDescribableType.PropertySets ps = individualType.getPropertySets();
                    ontoCollXML.getOntologyTerm().add( factory.createOntologyIndividual( individualType ) );
                }
            }

            os = new FileOutputStream( XMLFilename );

            @SuppressWarnings( "unchecked" )
            JAXBElement element = new JAXBElement(new QName( "http://fuge.sourceforge.net/fuge/1.0", "OntologyCollection" ), FuGECollectionOntologyCollectionType.class, ontoCollXML );
            m.marshal( element, os );

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
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
