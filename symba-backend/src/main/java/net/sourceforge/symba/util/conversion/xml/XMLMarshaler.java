package net.sourceforge.symba.util.conversion.xml;

import fugeOM.Collection.FuGE;
import fugeOM.Common.Identifiable;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import net.sourceforge.symba.ServiceLocator;
import net.sourceforge.symba.util.conversion.helper.CisbanFuGEHelper;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

public class XMLMarshaler {
    private final String schemaFilename;
    private final RealizableEntityService reService;
    private static final Map<String, String[]> JAXB_MAPPINGS = new HashMap<String, String[]>();

    static {
        // key : namespace of lsid
        // value : array of needed methods and class names, containing :
        //    0 = JAXB2 FuGE object fully-qualified class name
        //    1 = FuGE Helper class to use to marshal the object to XML
        // todo other identifiable objects, esp rawdata
        JAXB_MAPPINGS.put( "FuGE", new String[]{
                "fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType", "net.sourceforge.symba.util.conversion.helper.CisbanFuGEHelper" } );
        JAXB_MAPPINGS.put( "ExternalData", new String[]{
                "fugeOM.util.generatedJAXB2.FugeOMBioDataExternalDataType", "net.sourceforge.symba.util.conversion.helper.CisbanDataHelper" } );
    }

    private static final String EXTERNALDATA_NAMESPACE = "ExternalData";
    private Marshaller marshaller;

    public XMLMarshaler( String sf ) throws JAXBException, SAXException {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.reService = serviceLocator.getRealizableEntityService();
        this.schemaFilename = sf;
        setupMarshaler();
    }

    private void setupMarshaler() throws JAXBException, SAXException {
        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

        // create a Marshaller
        marshaller = jc.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        // fixme set the correct schema - temporary workaround as classloader not working for some reason
        SchemaFactory schemaFactory = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
        ClassLoader cl = XMLMarshaler.class.getClassLoader();
        java.net.URL url = cl.getResource( schemaFilename );
        Schema schema;
        if ( url == null ) {
            System.err.println(
                    "Could not find resource for: " + schemaFilename + ". Trying to open as a File instead." );
            schema = schemaFactory.newSchema( new File( schemaFilename ) );
//            throw new java.lang.IllegalArgumentException( "Could not find resource for: " + schemaFilename );
        } else {
            schema = schemaFactory.newSchema( url );
        }
        marshaller.setSchema( schema );

    }

    public void FuGEToJaxb2( String identifier, String xf ) throws Exception {
        OutputStream os = new FileOutputStream( xf );
        FuGEToJaxb2( identifier, new PrintWriter( os ) );
    }

    public void FuGEToJaxb2( String identifier, PrintWriter os ) throws Exception {
        // get the fuge root object
        FuGE fr;
        try {
            fr = ( FuGE ) reService.findIdentifiable( identifier );
        } catch ( java.lang.Exception e ) {

            // There SHOULD be such an entry in the database. If there is not,
            // that's when we throw an exception and exit.
            System.err.println( "START -- FuGE MARSHAL WARNING -- START " );
            System.err.println( "FuGE MARSHAL WARNING " );
            System.err.println(
                    "FuGE MARSHAL WARNING: Error retrieving experiment from the database. Exiting without marshaling." );
            System.err.println( "FuGE MARSHAL WARNING " );
            System.err.println( "END -- FuGE MARSHAL WARNING -- END " );

            e.printStackTrace();

            throw new java.lang.Exception( "Error retrieving experiment from the database" );
        }
        FuGEToJaxb2( fr, os );
    }

    public void FuGEToJaxb2( FuGE fr,
                             String xf )
            throws JAXBException, RealizableEntityServiceException, URISyntaxException, FileNotFoundException {

        OutputStream os = new FileOutputStream( xf );

        FuGEToJaxb2( fr, new PrintWriter( os ) );
    }

    public void FuGEToJaxb2( FuGE fr,
                             PrintWriter os )
            throws JAXBException, RealizableEntityServiceException, URISyntaxException {
        // create a jaxb root object
        CisbanFuGEHelper helper = new CisbanFuGEHelper();
        FugeOMCollectionFuGEType frXML = new FugeOMCollectionFuGEType();

        frXML = helper.marshal( frXML, fr );

        // Marshall the object to the provided PrintWriter
        @SuppressWarnings( "unchecked" )
        JAXBElement element = new JAXBElement( new QName( "http://fuge.org/core", "FuGE" ), FugeOMCollectionFuGEType.class, frXML );
        marshaller.marshal( element, os );

    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    public java.io.StringWriter ObjToJaxb2( String namespace,
                                            String identifier,
                                            Date date )
            throws RealizableEntityServiceException, RuntimeException, JAXBException {
        return ObjToJaxb2( namespace, ( Identifiable ) reService.findByIdAndDate( identifier, date ) );
    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    public java.io.StringWriter ObjToJaxb2( String namespace,
                                            String identifier )
            throws RealizableEntityServiceException, RuntimeException, JAXBException {
        return ObjToJaxb2( namespace, ( Identifiable ) reService.findIdentifiable( identifier ) );
    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    private java.io.StringWriter ObjToJaxb2( String namespace,
                                             Identifiable identifiable ) throws RuntimeException, JAXBException {
        StringWriter writer = new StringWriter( 4096 );

        if ( JAXB_MAPPINGS.get( namespace ) == null ) {
            // should be either an unrecognized lsid type or a RawData lsid.
            if ( namespace.equals( EXTERNALDATA_NAMESPACE ) ) {
                throw new RuntimeException( "Raw Data not downloadable at the current time. This is planned." );
            } else {
                throw new RuntimeException( "Unrecognized namespace for data retrieval: " + namespace );
            }
        }

        // get the type of the FuGE JAXB class
        Class referencedJaxbClass;
        try {
            referencedJaxbClass = Class.forName( JAXB_MAPPINGS.get( namespace )[1] );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Class not recognized: " + JAXB_MAPPINGS.get( namespace )[0] );
        }

        // get the marshal method used for this class type
        Method marshalMethod;
        try {
            marshalMethod = Class.forName( JAXB_MAPPINGS.get( namespace )[2] ).getMethod( "marshal" );
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error creating the specified marshaling method: " +
                    JAXB_MAPPINGS.get( namespace )[2] + "." + JAXB_MAPPINGS.get( namespace )[3] );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException(
                    "Error creating the class that holds the marshaling method: " + JAXB_MAPPINGS.get( namespace )[2] );
        }

        Object marshaledXML;
        try {
            marshaledXML = marshalMethod.invoke( Class.forName( JAXB_MAPPINGS.get( namespace )[2] ).newInstance(),
                    referencedJaxbClass.newInstance(), identifiable );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( "Error running the marshal method for " + identifiable.getIdentifier(), e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( "Error running the marshal method for " + identifiable.getIdentifier(), e );
        } catch ( InstantiationException e ) {
            throw new RuntimeException( "Error running the marshal method for " + identifiable.getIdentifier(), e );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Error running the marshal method for " + identifiable.getIdentifier(), e );
        }

        // Convert to XML and send back
        // todo: this shouldn't have "FuGE" in the QName - it has to be the element name that is going to be created.
        @SuppressWarnings( "unchecked" )
        JAXBElement element = new JAXBElement( new QName( "http://fuge.org/core", "FuGE" ), referencedJaxbClass, marshaledXML );
        marshaller.marshal( element, writer );

        return writer;
    }
}
