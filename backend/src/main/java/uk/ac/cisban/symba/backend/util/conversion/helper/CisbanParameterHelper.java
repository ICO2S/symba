package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.GenericParameter;
import fugeOM.Common.Protocol.Parameter;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.net.URISyntaxException;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanParameterHelper.java $
 *
 */

public class CisbanParameterHelper {
    private final CisbanIdentifiableHelper ci;
    private final CisbanGenericParameterHelper cgp;
    private final CisbanDefaultValueHelper cdv;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanParameterHelper( RealizableEntityService reService,
                                  CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cgp = new CisbanGenericParameterHelper( reService );
        this.cdv = new CisbanDefaultValueHelper( reService, ci.getCisbanDescribableHelper() );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Parameter unmarshalParameter( FugeOMCommonProtocolParameterType parameterXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        if ( parameterXML instanceof FugeOMCommonProtocolGenericParameterType ) {

            // Retrieve latest version from the database.
            GenericParameter genericParameter = ( GenericParameter ) helper.getOrCreateLatest(
                    parameterXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenParamEndurant",
                    parameterXML.getName(),
                    "fugeOM.Common.Protocol.GenericParameter",
                    System.err );

            // get parameter attributes
            genericParameter = ( GenericParameter ) ci.unmarshalIdentifiable( parameterXML, genericParameter );

            // data type
            if ( parameterXML.getDataType() != null ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                genericParameter.setDataType( ( OntologyTerm ) reService.findIdentifiable( parameterXML.getDataType().getOntologyTermRef() ) );
            }

            // unit
            if ( parameterXML.getUnit() != null ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                genericParameter.setUnit( ( OntologyTerm ) reService.findIdentifiable( parameterXML.getUnit().getOntologyTermRef() ) );
            }

            // default value
            if ( parameterXML.getDefaultValue() != null ) {
                // set up the converters
                FugeOMCommonProtocolDefaultValueType defaultValueXML = parameterXML.getDefaultValue().getValue();
                genericParameter.setDefaultValue( cdv.unmarshalDefaultValue( defaultValueXML ) );
            }

            // get generic parameter attributes
            genericParameter = cgp.unmarshalGenericParameter(
                    ( FugeOMCommonProtocolGenericParameterType ) parameterXML, genericParameter );

            if ( genericParameter.getId() != null ) {
                helper.assignAndLoadIdentifiable(
                        genericParameter, "fugeOM.Common.Protocol.GenericParameter", System.err );
            } else {
                helper.loadIdentifiable( genericParameter, "fugeOM.Common.Protocol.GenericParameter", System.err );
            }
            return genericParameter;
        }
        return null; // shouldn't get here as there is currently only one type of Parameter allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolParameterType marshalParameter( Parameter parameter )
            throws URISyntaxException {

        if ( parameter instanceof GenericParameter ) {
            FugeOMCommonProtocolGenericParameterType genericParameterXML = new FugeOMCommonProtocolGenericParameterType();

            // get parameter attributes
            genericParameterXML = ( FugeOMCommonProtocolGenericParameterType ) ci.marshalIdentifiable(
                    genericParameterXML, parameter );

            // data type
            if ( parameter.getDataType() != null ) {
                FugeOMCommonProtocolParameterType.DataType dtXML = new FugeOMCommonProtocolParameterType.DataType();
                dtXML.setOntologyTermRef( parameter.getDataType().getIdentifier() );
                genericParameterXML.setDataType( dtXML );
            }

            // unit
            if ( parameter.getUnit() != null ) {
                FugeOMCommonProtocolParameterType.Unit unitXML = new FugeOMCommonProtocolParameterType.Unit();
                unitXML.setOntologyTermRef( parameter.getUnit().getIdentifier() );
                genericParameterXML.setUnit( unitXML );
            }

            // default value
            if ( parameter.getDefaultValue() != null ) {
                genericParameterXML.setDefaultValue( cdv.marshalDefaultValue( parameter.getDefaultValue() ) );
            }

            // get generic parameter attributes
            genericParameterXML = cgp.marshalGenericParameter( genericParameterXML, ( GenericParameter ) parameter );

            return genericParameterXML;
        }
        return null; // shouldn't get here as there is currently only one type of Parameter allowed.
    }

    public FugeOMCommonProtocolParameterType generateRandomXML(
            FugeOMCommonProtocolParameterType parameterXML,
            FugeOMCollectionFuGEType frXML ) {

        // get parameter attributes
        parameterXML = ( FugeOMCommonProtocolParameterType ) ci.generateRandomXML( parameterXML );

        // data type
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMCommonProtocolParameterType.DataType dtXML = new FugeOMCommonProtocolParameterType.DataType();
            dtXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            parameterXML.setDataType( dtXML );
        }

        // unit
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMCommonProtocolParameterType.Unit unitXML = new FugeOMCommonProtocolParameterType.Unit();
            unitXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            parameterXML.setUnit( unitXML );
        }

        // default value
        FugeOMCommonProtocolAtomicValueType aValue = new FugeOMCommonProtocolAtomicValueType();
        parameterXML.setDefaultValue( cdv.generateRandomXML( aValue, frXML ) );

        // get generic parameter attributes
        parameterXML = cgp.generateRandomXML( ( FugeOMCommonProtocolGenericParameterType ) parameterXML, frXML );
        return parameterXML;
    }

    public Parameter getLatestVersion( Parameter parameter ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        parameter = ( Parameter ) reService.findLatestByEndurant( parameter.getEndurant().getIdentifier() );
        parameter = ( Parameter ) ci.getLatestVersion( parameter );

        // get parameter attributes
        if ( parameter.getDataType() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    parameter.getDataType().getEndurant().getIdentifier() );
            parameter.setDataType( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        if ( parameter.getUnit() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    parameter.getUnit().getEndurant().getIdentifier() );
            parameter.setUnit( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        if ( parameter.getDefaultValue() != null ) {
            parameter.setDefaultValue( cdv.getLatestVersion( parameter.getDefaultValue(), ci ) );
        }

        // get generic parameter attributes
        parameter = cgp.getLatestVersion( ( GenericParameter ) parameter, ci );

        return parameter;
    }
}
