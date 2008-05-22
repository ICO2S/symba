package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Data.ExternalData;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioDataDataType;
import fugeOM.util.generatedJAXB2.FugeOMBioDataExternalDataType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import net.sourceforge.symba.util.CisbanHelper;

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

public class CisbanDataHelper implements MappingHelper<Data, FugeOMBioDataDataType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanExternalDataHelper ced;
    private final CisbanHelper helper;

    public CisbanDataHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.ced = new CisbanExternalDataHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // @todo Dimension not implemented
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Data unmarshal( FugeOMBioDataDataType dataXML, Data data )
            throws RealizableEntityServiceException {

        // RawData will never be in the XML
        if ( dataXML instanceof FugeOMBioDataExternalDataType ) {

            // Retrieve latest version from the database.
            ExternalData externalData = ( ExternalData ) data;

            // set the data attributes.
            externalData = ( ExternalData ) ci.unmarshal( dataXML, externalData );

            // set the external data attributes
            externalData = ced.unmarshal( ( FugeOMBioDataExternalDataType ) dataXML, externalData );

            try {
                if ( externalData.getId() != null ) {
                    helper.assignAndLoadIdentifiable( externalData, "fugeOM.Bio.Data.ExternalData", System.err );
                } else {
                    helper.loadIdentifiable( externalData, "fugeOM.Bio.Data.ExternalData", System.err );
                }

            } catch ( RealizableEntityServiceException e ) {
                e.printStackTrace();
                throw new RuntimeException( "Error making FuGE POJO objects for Data" );
            }

            return externalData;
        }
        return null; // shouldn't get here as there is currently only one type of Data coded - will get here if InternalData is used in the xml. 
    }

    // @todo Dimension and InternalData not implemented
    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMBioDataDataType marshal( FugeOMBioDataDataType dataXML, Data data )
            throws RealizableEntityServiceException {

        // RawData will never be in the XML
        if ( data instanceof ExternalData ) {
            // create fuge object
            FugeOMBioDataExternalDataType externalDataXML = new FugeOMBioDataExternalDataType();

            // set the data attributes
            externalDataXML = ( FugeOMBioDataExternalDataType ) ci.marshal( externalDataXML, data );

            // set the externaldata attributes
            externalDataXML = ced.marshal( externalDataXML, ( ExternalData ) data );

            return externalDataXML;
        }
        return null; // shouldn't get here as there is currently only one type of Data coded - will get here if InternalData is used in the xml.
    }

    public FugeOMBioDataDataType generateRandomXML( FugeOMBioDataDataType dataXML ) {
        // set the data attributes
        dataXML = ( FugeOMBioDataExternalDataType ) ci.generateRandomXML( dataXML );

        return dataXML;
    }

    public FugeOMBioDataDataType generateRandomXMLWithLinksOut( FugeOMBioDataDataType dataXML, FugeOMCollectionFuGEType frXML ) {

        FugeOMBioDataExternalDataType externalDataXML = ( FugeOMBioDataExternalDataType ) generateRandomXML( dataXML );

        // set the externaldata attributes
        externalDataXML = ced.generateRandomXMLWithLinksOut( externalDataXML, frXML );

        return externalDataXML;
    }

    public Data getLatestVersion( Data data ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the identifiable section
        data = ( Data ) reService.findLatestByEndurant( data.getEndurant().getIdentifier() );
        data = ( Data ) ci.getLatestVersion( data );

        // RawData is never versioned, so no need to check it.
        if ( data instanceof ExternalData ) {
            data = ced.getLatestVersion( ( ExternalData ) data );
        }
        return data;
    }
}