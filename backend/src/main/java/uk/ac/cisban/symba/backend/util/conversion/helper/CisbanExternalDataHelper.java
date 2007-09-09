package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Bio.Data.ExternalData;
import fugeOM.Common.Description.Uri;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioDataExternalDataType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonDescriptionExternalURIType;

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

public class CisbanExternalDataHelper {
    private final CisbanDescribableHelper cd;
    private final RealizableEntityService reService;

    public CisbanExternalDataHelper( RealizableEntityService reService, CisbanDescribableHelper cd ) {
        this.reService = reService;
        this.cd = cd;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ExternalData unmarshalExternalData( FugeOMBioDataExternalDataType externalDataXML,
                                               ExternalData externalData ) throws URISyntaxException, RealizableEntityServiceException {

        // set only ExternalData-specific traits

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
            exturi = ( Uri ) cd.unmarshalDescribable( exturiXML, exturi );
//            exturi.setUri( new java.net.URI( exturiXML.getUri() ) );
            exturi.setUri( exturiXML.getUri() );

            // load fuge object into database
            reService.createObInDB( "fugeOM.Common.Description.Uri", exturi );

            // load fuge object into describable
            externalData.setExternalFormatDocumentation( exturi );
        }

        return externalData;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMBioDataExternalDataType marshalExternalData( FugeOMBioDataExternalDataType externalDataXML,
                                                              ExternalData externalData ) throws URISyntaxException {

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
            exturiXML = ( FugeOMCommonDescriptionExternalURIType ) cd.marshalDescribable(
                    exturiXML, externalData.getExternalFormatDocumentation() );
            exturiXML.setUri( externalData.getExternalFormatDocumentation().getUri() );

            // load jaxb object into describableXML
            efdXML.setExternalURI( exturiXML );
            externalDataXML.setExternalFormatDocumentation( efdXML );
        }

        return externalDataXML;
    }

    public FugeOMBioDataExternalDataType generateRandomXML( FugeOMBioDataExternalDataType externalDataXML,
                                                            FugeOMCollectionFuGEType frXML,
                                                            CisbanIdentifiableHelper ci ) {
        // FileFormat
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMBioDataExternalDataType.FileFormat fileformatXML = new FugeOMBioDataExternalDataType.FileFormat();
            fileformatXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }

        // Location
        externalDataXML.setLocation( String.valueOf( Math.random() ) );

        // external format documentation
        FugeOMBioDataExternalDataType.ExternalFormatDocumentation efdXML = new FugeOMBioDataExternalDataType.ExternalFormatDocumentation();
        FugeOMCommonDescriptionExternalURIType exturiXML = new FugeOMCommonDescriptionExternalURIType();

        // set jaxb object
        exturiXML = ( FugeOMCommonDescriptionExternalURIType ) cd.generateRandomXML( exturiXML, ci );
        exturiXML.setUri( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        // load jaxb object into describableXML
        efdXML.setExternalURI( exturiXML );
        externalDataXML.setExternalFormatDocumentation( efdXML );

        return externalDataXML;
    }

    public ExternalData getLatestVersion( ExternalData externalData,
                                          CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        // get the latest version of the file format
        if ( externalData.getFileFormat() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    externalData.getFileFormat().getEndurant().getIdentifier() );
            externalData.setFileFormat( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        // get the latest version of the external format documentation
        if ( externalData.getExternalFormatDocumentation() != null ) {
            externalData.setExternalFormatDocumentation(
                    ( Uri ) cd.getLatestVersion(
                            externalData.getExternalFormatDocumentation(), ci ) );
        }
        return externalData;
    }
}
