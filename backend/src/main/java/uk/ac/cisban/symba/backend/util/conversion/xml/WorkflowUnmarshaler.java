package uk.ac.cisban.symba.backend.util.conversion.xml;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.DataCollection;
import fugeOM.Collection.MaterialCollection;
import fugeOM.Collection.OntologyCollection;
import fugeOM.Collection.ProtocolCollection;
import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.conversion.helper.*;

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
    private final String outputFilename;
    private final RealizableEntityService reService;
    private final CisbanIdentifiableHelper ci;

    public WorkflowUnmarshaler( String xf ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.reService = serviceLocator.getRealizableEntityService();
        CisbanDescribableHelper cd = new CisbanDescribableHelper( reService );
        this.ci = new CisbanIdentifiableHelper( reService, cd );
        this.outputFilename = xf;
    }

    public void Jaxb2ToFuGE( String[] inputFilenames ) throws FileNotFoundException, JAXBException, RealizableEntityServiceException, LSIDException, URISyntaxException {

        PrintStream printStream = new PrintStream( new File( outputFilename ) );

        for ( int iii = 1; iii < inputFilenames.length; iii++ ) {

            System.err.println( "file number: " + iii );

            // create a JAXBContext capable of handling classes generated into
            // the fugeOM.util.generatedJAXB2 package
            JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            // unmarshal JUST what is normally available within an AuditCollection
            JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u
                    .unmarshal( new FileInputStream( inputFilenames[iii] ) );

            // Get the fuge collection root object. REMEMBER that we will not be loading the Collection, JUST its contents.
            FugeOMCollectionFuGEType fuGEType = ( FugeOMCollectionFuGEType ) genericTopLevelElement.getValue();

            if ( fuGEType.getOntologyCollection() != null ) {
                // retrieve the ontology terms
                FugeOMCollectionOntologyCollectionType collectionXML = fuGEType.getOntologyCollection();
                OntologyCollection ontologyCollection = ( OntologyCollection ) reService
                        .createDescribableOb( "fugeOM.Collection.OntologyCollection" );
                // get and store all information in the database
                CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper( reService, ci );

                // unmarshall the jaxb object - by accessing this method we do NOT load the OntologyCollection into the
                // database. We are only interested in the contents of the collection.
                ontologyCollection = coc.unmarshalCollectionContents( collectionXML, ontologyCollection );
                coc.prettyPrint( ontologyCollection, printStream );
            }

            if ( fuGEType.getMaterialCollection() != null ) {
                // retrieve the material dummies
                FugeOMCollectionMaterialCollectionType collectionXML = fuGEType.getMaterialCollection();
                MaterialCollection materialCollection = ( MaterialCollection ) reService
                        .createDescribableOb( "fugeOM.Collection.MaterialCollection" );
                // get and store all information in the database
                CisbanMaterialCollectionHelper cmc = new CisbanMaterialCollectionHelper( reService, ci );

                // unmarshall the jaxb object - by accessing this method we do NOT load the MaterialCollection into the
                // database. We are only interested in the contents of the collection.
                materialCollection = cmc.unmarshalCollectionContents( collectionXML, materialCollection );
            }

            if ( fuGEType.getDataCollection() != null ) {
                // retrieve the external data dummies
                FugeOMCollectionDataCollectionType collectionXML = fuGEType.getDataCollection();
                DataCollection collection = ( DataCollection ) reService
                        .createDescribableOb( "fugeOM.Collection.DataCollection" );
                // get and store all information in the database
                CisbanDataCollectionHelper collectionHelper = new CisbanDataCollectionHelper( reService, ci );

                // unmarshall the jaxb object - by accessing this method we do NOT load the MaterialCollection into the
                // database. We are only interested in the contents of the collection.
                collection = collectionHelper.unmarshalCollectionContents( collectionXML, collection );
            }

            // retrieve the protocols
            FugeOMCollectionProtocolCollectionType protocolCollectionXML = fuGEType.getProtocolCollection();
            ProtocolCollection protocolCollection = ( ProtocolCollection ) reService
                    .createDescribableOb( "fugeOM.Collection.ProtocolCollection" );

            // get and store all information in the database
            CisbanProtocolCollectionHelper cpc = new CisbanProtocolCollectionHelper( reService, ci );

            // unmarshall the jaxb object without loading the collection into the database
            protocolCollection = cpc.unmarshalCollectionContents( protocolCollectionXML, protocolCollection );
            cpc.prettyPrint( protocolCollection, printStream );
            printStream.println( "<table>" );
            cpc.prettyHtml( protocolCollection, new PrintWriter( printStream ) );
            printStream.println( "</table>" );
        }
    }
}
