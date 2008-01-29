package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Data.ExternalData;
import fugeOM.Bio.Data.InternalData;
import fugeOM.Collection.DataCollection;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

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

public class CisbanDataCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanDataHelper cdat;
    private final RealizableEntityService reService;

    public CisbanDataCollectionHelper( RealizableEntityService reService,
                                       CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.cd = ci.getCisbanDescribableHelper();
        this.ci = ci;
        this.cdat = new CisbanDataHelper( reService, ci );
    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public DataCollection unmarshalDataCollection(
            FugeOMCollectionDataCollectionType datCollXML ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // create the fuge data collection object
        DataCollection datColl = ( DataCollection ) reService.createDescribableOb( "fugeOM.Collection.DataCollection" );

        // set describable information
        datColl = ( DataCollection ) cd.unmarshalDescribable( datCollXML, datColl );

        datColl = unmarshalCollectionContents(datCollXML, datColl);

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.DataCollection", datColl );

        return datColl;
    }

    public DataCollection unmarshalCollectionContents( FugeOMCollectionDataCollectionType datCollXML,
                                                        DataCollection datColl ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {
        // Create collection of data for addition to the data collection
        Set<Data> datas = new HashSet<Data>();

        for ( JAXBElement<? extends FugeOMBioDataDataType> DataElementXML : datCollXML.getData() ) {
            FugeOMBioDataDataType dataXML = DataElementXML.getValue();

            // As RawData objects do not appear in the XML, there is no need to code it here.
            datas.add( cdat.unmarshalData( dataXML ) );
        }

        // Add the set of generic datas to the Data collection
        datColl.setAllData( datas );

        return datColl;
    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionDataCollectionType marshalDataCollection(
            DataCollection datColl ) throws URISyntaxException {

        // create the jaxb Data collection object
        FugeOMCollectionDataCollectionType datCollXML = new FugeOMCollectionDataCollectionType();

        // set describable information
        datCollXML = ( FugeOMCollectionDataCollectionType ) cd.marshalDescribable( datCollXML, datColl );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( Object DataObj : datColl.getAllData() ) {
            Data data = ( Data ) DataObj;

            // As RawData objects do not appear in the XML, there is no need to code it here.
            if ( data instanceof ExternalData ) {
                datCollXML.getData()
                        .add( factory.createExternalData( ( FugeOMBioDataExternalDataType ) cdat.marshalData( data ) ) );
            } else if ( data instanceof InternalData ) {
                datCollXML.getData()
                        .add( factory.createInternalData( ( FugeOMBioDataInternalDataType ) cdat.marshalData( data ) ) );
            }
        }
        return datCollXML;
    }

    // todo deal with InternalData
    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType frXML ) {

        // create the jaxb Data collection object
        FugeOMCollectionDataCollectionType datCollXML = new FugeOMCollectionDataCollectionType();

        // set describable information
        datCollXML = ( FugeOMCollectionDataCollectionType ) cd.generateRandomXML( datCollXML, ci );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

            // As RawData objects do not appear in the XML, there is no need to code it here.
            datCollXML.getData().add(
                    factory.createExternalData(
                            ( FugeOMBioDataExternalDataType ) cdat.generateRandomXML(
                                    new FugeOMBioDataExternalDataType(), frXML ) ) );
        }

        frXML.setDataCollection( datCollXML );
        return frXML;
    }

    public DataCollection getLatestVersion( DataCollection dataCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        dataCollection = ( DataCollection ) cd.getLatestVersion( dataCollection, ci );

        // prepare updated set
        Set<Data> set = new HashSet<Data>();

        // load all the latest versions into the new set.
        for ( Object obj : dataCollection.getAllData() ) {
            set.add( cdat.getLatestVersion( ( Data ) obj ) );
        }
        dataCollection.setAllData( set );

        return dataCollection;
    }
}
