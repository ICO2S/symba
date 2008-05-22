package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Bio.Data.ExternalData;
import fugeOM.Common.Description.Uri;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioDataExternalDataType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonDescriptionExternalURIType;

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

public class CisbanExternalDataHelper implements MappingHelper<ExternalData, FugeOMBioDataExternalDataType> {
    private final CisbanDescribableHelper cd;

    public CisbanExternalDataHelper() {
        this.cd = new CisbanDescribableHelper();
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ExternalData unmarshal( FugeOMBioDataExternalDataType externalDataXML,
                                   ExternalData externalData ) throws RealizableEntityServiceException {

        // set only ExternalData-specific traits

        try {
            // FileFormat
            if ( externalDataXML.getFileFormat() != null ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                externalData.setFileFormat( ( OntologyTerm ) reService.findIdentifiable( externalDataXML.getFileFormat().getOntologyTermRef() ) );
            }

            // Location
            if ( externalDataXML.getLocation() != null ) {
                externalData.setLocation( externalDataXML.getLocation() );
            }

            // external format documentation
            if ( externalDataXML.getExternalFormatDocumentation() != null ) {
                // create jaxb object
                FugeOMCommonDescriptionExternalURIType exturiXML = externalDataXML.getExternalFormatDocumentation()
                        .getExternalURI();

                // create fuge object
                Uri exturi = ( Uri ) reService.createDescribableOb( "fugeOM.Common.Description.Uri" );

                // set fuge object
                exturi = ( Uri ) cd.unmarshal( exturiXML, exturi );
//            exturi.setUri( new java.net.URI( exturiXML.getUri() ) );
                exturi.setUri( exturiXML.getUri() );

                // load fuge object into database
                reService.createObInDB( "fugeOM.Common.Description.Uri", exturi );

                // load fuge object into describable
                externalData.setExternalFormatDocumentation( exturi );
            }
        } catch ( RealizableEntityServiceException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error making FuGE POJO objects for External Data" );
        }

        return externalData;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMBioDataExternalDataType marshal( FugeOMBioDataExternalDataType externalDataXML,
                                                  ExternalData externalData ) throws RealizableEntityServiceException {

        // set any ExternalData-specific traits

        // FileFormat
        if ( externalData.getFileFormat() != null ) {
            FugeOMBioDataExternalDataType.FileFormat fileformatXML = new FugeOMBioDataExternalDataType.FileFormat();
            fileformatXML.setOntologyTermRef( externalData.getFileFormat().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }

        // Location
        if ( externalData.getLocation() != null ) {
            externalDataXML.setLocation( externalData.getLocation() );
        }

        // external format documentation
        if ( externalData.getExternalFormatDocumentation() != null ) {
            // create jaxb object
            FugeOMBioDataExternalDataType.ExternalFormatDocumentation efdXML = new FugeOMBioDataExternalDataType.ExternalFormatDocumentation();
            FugeOMCommonDescriptionExternalURIType exturiXML = new FugeOMCommonDescriptionExternalURIType();

            // set jaxb object
            exturiXML = ( FugeOMCommonDescriptionExternalURIType ) cd.marshal(
                    exturiXML, externalData.getExternalFormatDocumentation() );
            exturiXML.setUri( externalData.getExternalFormatDocumentation().getUri() );

            // load jaxb object into describableXML
            efdXML.setExternalURI( exturiXML );
            externalDataXML.setExternalFormatDocumentation( efdXML );
        }

        return externalDataXML;
    }

    public FugeOMBioDataExternalDataType generateRandomXML( FugeOMBioDataExternalDataType externalDataXML ) {

        // Location
        externalDataXML.setLocation( String.valueOf( Math.random() ) );

        // external format documentation
        FugeOMBioDataExternalDataType.ExternalFormatDocumentation efdXML = new FugeOMBioDataExternalDataType.ExternalFormatDocumentation();
        FugeOMCommonDescriptionExternalURIType exturiXML = new FugeOMCommonDescriptionExternalURIType();

        // set jaxb object
        exturiXML = ( FugeOMCommonDescriptionExternalURIType ) cd.generateRandomXML( exturiXML );
        exturiXML.setUri( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        // load jaxb object into describableXML
        efdXML.setExternalURI( exturiXML );
        externalDataXML.setExternalFormatDocumentation( efdXML );

        return externalDataXML;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public FugeOMBioDataExternalDataType generateRandomXMLWithLinksOut( FugeOMBioDataExternalDataType externalDataXML,
                                                                        FugeOMCollectionFuGEType frXML ) {
        externalDataXML = generateRandomXML( externalDataXML );

        // FileFormat
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMBioDataExternalDataType.FileFormat fileformatXML = new FugeOMBioDataExternalDataType.FileFormat();
            fileformatXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }

        return externalDataXML;
    }

    public ExternalData getLatestVersion( ExternalData externalData ) throws RealizableEntityServiceException {

        // get the latest version of the file format
        if ( externalData.getFileFormat() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    externalData.getFileFormat().getEndurant().getIdentifier() );
            externalData.setFileFormat( ( OntologyTerm ) ( new CisbanIdentifiableHelper() ).getLatestVersion( ot ) );
        }

        // get the latest version of the external format documentation
        if ( externalData.getExternalFormatDocumentation() != null ) {
            externalData.setExternalFormatDocumentation(
                    ( Uri ) cd.getLatestVersion(
                            externalData.getExternalFormatDocumentation() ) );
        }
        return externalData;
    }
}
