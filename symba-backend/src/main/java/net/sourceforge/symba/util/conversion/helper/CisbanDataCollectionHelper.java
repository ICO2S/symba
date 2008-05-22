package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Data.ExternalData;
import fugeOM.Bio.Data.InternalData;
import fugeOM.Collection.DataCollection;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import net.sourceforge.symba.util.CisbanHelper;

import javax.xml.bind.JAXBElement;
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

public class CisbanDataCollectionHelper implements MappingHelper<DataCollection, FugeOMCollectionDataCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanDataHelper cdat;
    private final CisbanHelper helper;

    public CisbanDataCollectionHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cd = ci.getCisbanDescribableHelper();
        this.cdat = new CisbanDataHelper();
        this.helper = CisbanHelper.create( reService );

    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public DataCollection unmarshal(
            FugeOMCollectionDataCollectionType datCollXML, DataCollection datColl )
            throws RealizableEntityServiceException {

        // set describable information
        datColl = ( DataCollection ) cd.unmarshal( datCollXML, datColl );

        datColl = unmarshalCollectionContents( datCollXML, datColl );

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.DataCollection", datColl );

        return datColl;
    }

    public DataCollection unmarshalCollectionContents( FugeOMCollectionDataCollectionType datCollXML,
                                                       DataCollection datColl )
            throws RealizableEntityServiceException {
        // Create collection of data for addition to the data collection
        Set<Data> datas = new HashSet<Data>();

        for ( JAXBElement<? extends FugeOMBioDataDataType> DataElementXML : datCollXML.getData() ) {
            FugeOMBioDataDataType dataXML = DataElementXML.getValue();

            // As RawData objects do not appear in the XML, there is no need to code it here.
            ExternalData externalData = ( ExternalData ) helper.getOrCreateLatest(
                    dataXML.getEndurant(),
                    "fugeOM.Bio.Data.ExternalDataEndurant",
                    dataXML.getName(),
                    "fugeOM.Bio.Data.ExternalData",
                    System.err );
            datas.add( cdat.unmarshal( dataXML, externalData ) );
        }

        // Add the set of generic datas to the Data collection
        datColl.setAllData( datas );

        return datColl;
    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionDataCollectionType marshal(
            FugeOMCollectionDataCollectionType datCollXML, DataCollection datColl )
            throws RealizableEntityServiceException {

        // set describable information
        datCollXML = ( FugeOMCollectionDataCollectionType ) cd.marshal( datCollXML, datColl );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( Object DataObj : datColl.getAllData() ) {
            Data data = ( Data ) DataObj;

            // As RawData objects do not appear in the XML, there is no need to code it here.
            if ( data instanceof ExternalData ) {
                datCollXML.getData()
                        .add( factory.createExternalData( ( FugeOMBioDataExternalDataType ) cdat.marshal( new FugeOMBioDataExternalDataType(), data ) ) );
            } else if ( data instanceof InternalData ) {
                datCollXML.getData()
                        .add( factory.createInternalData( ( FugeOMBioDataInternalDataType ) cdat.marshal( new FugeOMBioDataInternalDataType(), data ) ) );
            }
        }
        return datCollXML;
    }

    public FugeOMCollectionDataCollectionType generateRandomXML( FugeOMCollectionDataCollectionType datCollXML ) {
        // set describable information
        datCollXML = ( FugeOMCollectionDataCollectionType ) cd.generateRandomXML( datCollXML );

        return datCollXML;
    }

    // todo deal with InternalData
    public FugeOMCollectionFuGEType generateRandomXMLWithLinksOut( FugeOMCollectionFuGEType frXML ) {

        // create the jaxb Data collection object
        FugeOMCollectionDataCollectionType datCollXML = generateRandomXML( new FugeOMCollectionDataCollectionType() );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

            // As RawData objects do not appear in the XML, there is no need to code it here.
            datCollXML.getData().add(
                    factory.createExternalData(
                            ( FugeOMBioDataExternalDataType ) cdat.generateRandomXMLWithLinksOut(
                                    new FugeOMBioDataExternalDataType(), frXML ) ) );
        }

        frXML.setDataCollection( datCollXML );
        return frXML;
    }

    public DataCollection getLatestVersion( DataCollection dataCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        dataCollection = ( DataCollection ) cd.getLatestVersion( dataCollection );

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
