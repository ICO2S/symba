package net.sourceforge.symba.util.conversion.xml;


import fugeOM.Collection.FuGE;
import net.sourceforge.symba.ServiceLocator;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import org.xml.sax.SAXException;
import net.sourceforge.symba.util.CisbanHelper;
import net.sourceforge.symba.util.conversion.helper.CisbanFuGEHelper;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

// NOTE: this class only works with a single FuGE element in an xml file: not multiple entries in the same file

// fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
public class XMLUnmarshaler {
    private final String schemaFilename, XMLFilename;
    private final RealizableEntityService reService;
    private final CisbanHelper helper;

    public XMLUnmarshaler( String sf, String xf ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.reService = serviceLocator.getRealizableEntityService();
        this.schemaFilename = sf;
        this.XMLFilename = xf;
        this.helper = CisbanHelper.create( reService );
    }

    public String Jaxb2ToFuGE() throws JAXBException, SAXException, RealizableEntityServiceException, URISyntaxException, FileNotFoundException {

        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        // Sort out validation settings
        SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
        Schema schema = sf.newSchema( new File( schemaFilename ) );
        // set a schema for validation.
        u.setSchema( schema );

        // unmarshal a fugeOM.util.generatedJAXB2 instance document into a tree of Java content
        // objects composed of classes from the fugeOM.util.generatedJAXB2 package.
        JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u.unmarshal( new FileInputStream( XMLFilename ) );

        // Get the jaxb root object
        FugeOMCollectionFuGEType frXML = ( FugeOMCollectionFuGEType ) genericTopLevelElement.getValue();

        // Before doing any unmarshaling, check to see if this object is in the database.
        FuGE fr;
        // Retrieve latest version from the database.
        fr = ( FuGE ) helper.getOrCreateLatest(
                frXML.getEndurant(),
                "fugeOM.Collection.FuGEEndurant",
                frXML.getName(),
                "fugeOM.Collection.FuGE",
                System.err );

        CisbanFuGEHelper cf = new CisbanFuGEHelper();
        fr = cf.unmarshal( frXML, fr );

        // Load the entire fuge entry into the database
        if ( fr.getId() != null ) {
            helper.assignAndLoadIdentifiable( fr, "fugeOM.Collection.FuGE", System.err );
            System.out.println(
                    "Successful additional version attached to same endurant in the database: id = " + fr.getId() );
        } else {
            helper.loadIdentifiable( fr, "fugeOM.Collection.FuGE", System.err );
            System.out.println( "Successful first version in the database." );
        }
        return fr.getIdentifier();
    }
}
