package uk.ac.cisban.symba.backend.util.conversion.xml;

import fugeOM.Bio.Data.ExternalData;
import fugeOM.Collection.FuGE;
import fugeOM.Common.Audit.Organization;
import fugeOM.Common.Audit.Person;
import fugeOM.Common.Identifiable;
import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioDataExternalDataType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditOrganizationType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditPersonType;
import org.xml.sax.SAXException;
import uk.ac.cisban.symba.backend.util.conversion.helper.*;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Date;

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
    private static final String CISBAN_EXPERIMENT_NAMESPACE = "FuGE";
    private static final String CISBAN_PERSON_NAMESPACE = "Person";
    private static final String CISBAN_ORGANIZATION_NAMESPACE = "Organization";
    private static final String CISBAN_EXTERNALDATA_NAMESPACE = "ExternalData";
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
                    "FuGE MARSHAL WARNING: experiment is not present in the database. Exiting without marshaling." );
            System.err.println( "FuGE MARSHAL WARNING " );
            System.err.println( "END -- FuGE MARSHAL WARNING -- END " );

            throw new java.lang.Exception( "FuGE object identifier not present in database" );
        }
        FuGEToJaxb2( fr, os );
    }

    public void FuGEToJaxb2( FuGE fr,
                             String xf ) throws JAXBException, RealizableEntityServiceException, URISyntaxException, FileNotFoundException {

        OutputStream os = new FileOutputStream( xf );

        FuGEToJaxb2( fr, new PrintWriter( os ) );
    }

    public void FuGEToJaxb2( FuGE fr,
                             PrintWriter os ) throws JAXBException, RealizableEntityServiceException, URISyntaxException {
        // create a jaxb root object
        CisbanFuGEHelper helper = new CisbanFuGEHelper( reService );
        FugeOMCollectionFuGEType frXML = new FugeOMCollectionFuGEType();

        helper.marshalFuGE( frXML, fr );

        // Marshall the object to the provided PrintWriter
        marshaller.marshal(
                new JAXBElement(
                        new QName( "http://fuge.org/core", "FuGE" ), FugeOMCollectionFuGEType.class, frXML ), os );

    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    public java.io.StringWriter ObjToJaxb2( String namespace,
                                            String identifier,
                                            Date date ) throws RealizableEntityServiceException, URISyntaxException, JAXBException {
        return ObjToJaxb2( namespace, ( Identifiable ) reService.findByIdAndDate( identifier, date ) );
    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    public java.io.StringWriter ObjToJaxb2( String namespace,
                                            String identifier ) throws RealizableEntityServiceException, URISyntaxException, JAXBException {
        return ObjToJaxb2( namespace, ( Identifiable ) reService.findIdentifiable( identifier ) );
    }

    // The various versions of ObjToJaxb2 are helper classes used by the LSID Authority to be able to retrieve specific
    // xml elements beyond the FuGE root object itself. Please note that while the raw HT data does have an LSID,
    // it is NOT part of the FuGE Schema, and therefore will not be returned as a FuGE element.
    private java.io.StringWriter ObjToJaxb2( String namespace,
                                             Identifiable identifiable ) throws RealizableEntityServiceException, URISyntaxException, JAXBException {
        StringWriter writer = new StringWriter( 4096 );

        if ( namespace.equals( CISBAN_EXPERIMENT_NAMESPACE ) ) {

            // Get Experiment from database
            FugeOMCollectionFuGEType frXML = new FugeOMCollectionFuGEType();
            FuGE fuGE = ( FuGE ) identifiable;

            // Convert to XML and send back
            CisbanFuGEHelper cisbanFuGEHelper = new CisbanFuGEHelper( reService );
            frXML = cisbanFuGEHelper.marshalFuGE( frXML, fuGE );
            marshaller.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "FuGE" ), FugeOMCollectionFuGEType.class, frXML ),
                    writer );
        } else if ( namespace.equals( CISBAN_PERSON_NAMESPACE ) ) {
            // Get Person from database
            FugeOMCommonAuditPersonType personXML = new FugeOMCommonAuditPersonType();
            Person person = ( Person ) identifiable;

            // Convert to XML and send back
            CisbanAuditCollectionHelper cisbanAuditCollectionHelper = new CisbanAuditCollectionHelper(
                    reService, new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) ) );
            personXML = ( FugeOMCommonAuditPersonType ) cisbanAuditCollectionHelper.marshalContact( personXML, person );
            personXML = cisbanAuditCollectionHelper.marshalPerson( personXML, person );
            marshaller.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "Person" ),
                            FugeOMCommonAuditPersonType.class,
                            personXML ), writer );
        } else if ( namespace.equals( CISBAN_ORGANIZATION_NAMESPACE ) ) {
            // Get Organization from database
            FugeOMCommonAuditOrganizationType type = new FugeOMCommonAuditOrganizationType();
            Organization organization = ( Organization ) identifiable;

            // Convert to XML and send back
            CisbanAuditCollectionHelper cisbanAuditCollectionHelper = new CisbanAuditCollectionHelper(
                    reService, new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) ) );
            type = ( FugeOMCommonAuditOrganizationType ) cisbanAuditCollectionHelper.marshalContact(
                    type, organization );
            type = cisbanAuditCollectionHelper.marshalOrganization( type, organization );
            marshaller.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "Organization" ),
                            FugeOMCommonAuditOrganizationType.class,
                            type ), writer );
        } else if ( namespace.equals( CISBAN_EXTERNALDATA_NAMESPACE ) ) {
            // Get ExternalData from database
            FugeOMBioDataExternalDataType type;
            ExternalData externalData = ( ExternalData ) identifiable;

            // Convert to XML and send back
            CisbanDataHelper cisbanDataHelper = new CisbanDataHelper(
                    reService, new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) ) );
            type = ( FugeOMBioDataExternalDataType ) cisbanDataHelper.marshalData( externalData );
            marshaller.marshal(
                    new JAXBElement(
                            new QName( "http://fuge.org/core", "ExternalData" ),
                            FugeOMBioDataExternalDataType.class,
                            type ), writer );
        }
        return writer;
    }
}
