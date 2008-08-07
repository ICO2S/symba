package net.sourceforge.symba.mapping.hibernatejaxb2.xml;

import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.collection.DataCollection;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.collection.MaterialCollection;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.OntologyCollectionMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.MaterialCollectionMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.DataCollectionMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.ProtocolCollectionMappingHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
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

// Unmarshals Workflow XML and loads into the database. This is one way to get pre-prepared protocols and

// workflows into the database for the front-end to use.
public class WorkflowUnmarshaler {
    private final EntityService entityService;

    public WorkflowUnmarshaler() {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.entityService = serviceLocator.getEntityService();
    }

    public void Jaxb2ToFuGE( String[] inputFilenames ) throws FileNotFoundException, JAXBException, URISyntaxException {

        for ( int iii = 0; iii < inputFilenames.length; iii++ ) {

            System.err.println( "file number: " + iii );

            // create a JAXBContext capable of handling classes generated into
            // the net.sourceforge.fuge.util.generatedJAXB2 package
            JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generatedJAXB2" );

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            // unmarshal JUST what is normally available within an AuditCollection
            JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u
                    .unmarshal( new FileInputStream( inputFilenames[iii] ) );

            // Get the fuge collection root object. REMEMBER that we will not be loading the Collection, JUST its contents.
            FuGECollectionFuGEType fuGEType = ( FuGECollectionFuGEType ) genericTopLevelElement.getValue();

            if ( fuGEType.getOntologyCollection() != null ) {
                // retrieve the ontology terms
                FuGECollectionOntologyCollectionType collectionXML = fuGEType.getOntologyCollection();
                // get and store all information in the database
                OntologyCollectionMappingHelper coc = new OntologyCollectionMappingHelper();

                // unmarshall the jaxb object - by accessing this method we do NOT load the OntologyCollection into the
                // database. We are only interested in the contents of the collection.
                coc.unmarshalCollectionContents( collectionXML, ( OntologyCollection ) entityService
                        .createDescribable( "net.sourceforge.fuge.collection.OntologyCollection" ), null );
            }

            if ( fuGEType.getMaterialCollection() != null ) {
                // retrieve the material dummies
                FuGECollectionMaterialCollectionType collectionXML = fuGEType.getMaterialCollection();
                // get and store all information in the database
                MaterialCollectionMappingHelper cmc = new MaterialCollectionMappingHelper();

                // unmarshall the jaxb object - by accessing this method we do NOT load the MaterialCollection into the
                // database. We are only interested in the contents of the collection.
                cmc.unmarshalCollectionContents( collectionXML, ( MaterialCollection ) entityService
                        .createDescribable( "net.sourceforge.fuge.collection.MaterialCollection" ), null );
            }

            if ( fuGEType.getDataCollection() != null ) {
                // retrieve the external data dummies
                FuGECollectionDataCollectionType collectionXML = fuGEType.getDataCollection();
                // get and store all information in the database
                DataCollectionMappingHelper collectionHelper = new DataCollectionMappingHelper();

                // unmarshall the jaxb object - by accessing this method we do NOT load the DataCollection into the
                // database. We are only interested in the contents of the collection.
                collectionHelper.unmarshalCollectionContents( collectionXML, ( DataCollection ) entityService
                        .createDescribable( "net.sourceforge.fuge.collection.DataCollection" ), null );
            }

            // retrieve the protocols
            FuGECollectionProtocolCollectionType protocolCollectionXML = fuGEType.getProtocolCollection();

            // get and store all information in the database
            ProtocolCollectionMappingHelper cpc = new ProtocolCollectionMappingHelper();

            // unmarshall the jaxb object without loading the collection into the database
            cpc.unmarshalCollectionContents( protocolCollectionXML, ( ProtocolCollection ) entityService
                    .createDescribable( "net.sourceforge.fuge.collection.ProtocolCollection" ), null );
        }
    }
}
