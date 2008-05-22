package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
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

public class CisbanParameterHelper implements MappingHelper<Parameter, FugeOMCommonProtocolParameterType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanGenericParameterHelper cgp;
    private final CisbanDefaultValueHelper cdv;
    private final CisbanHelper helper;

    public CisbanParameterHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cgp = new CisbanGenericParameterHelper();
        this.cdv = new CisbanDefaultValueHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Parameter unmarshal( FugeOMCommonProtocolParameterType parameterXML, Parameter parameter )
            throws RealizableEntityServiceException {

        if ( parameterXML instanceof FugeOMCommonProtocolGenericParameterType ) {

            // Retrieve latest version from the database.
            GenericParameter genericParameter = ( GenericParameter ) parameter;

            // get parameter attributes
            genericParameter = ( GenericParameter ) ci.unmarshal( parameterXML, genericParameter );

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
                genericParameter.setDefaultValue( cdv.unmarshal( defaultValueXML, null ) );
            }

            // get generic parameter attributes
            genericParameter = cgp.unmarshal(
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
    public FugeOMCommonProtocolParameterType marshal( FugeOMCommonProtocolParameterType parameterXML, Parameter parameter ) {

        if ( parameter instanceof GenericParameter ) {
            FugeOMCommonProtocolGenericParameterType genericParameterXML = ( FugeOMCommonProtocolGenericParameterType ) parameterXML;

            // get parameter attributes
            genericParameterXML = ( FugeOMCommonProtocolGenericParameterType ) ci.marshal(
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
                if ( parameter.getDefaultValue() instanceof AtomicValue ) {
                    genericParameterXML.setDefaultValue( ( new ObjectFactory() ).createAtomicValue( ( FugeOMCommonProtocolAtomicValueType ) cdv.marshal( new FugeOMCommonProtocolAtomicValueType(), parameter.getDefaultValue() ) ) );
                } else if ( parameter.getDefaultValue() instanceof BooleanValue ) {
                    genericParameterXML.setDefaultValue( ( new ObjectFactory() ).createBooleanValue( ( FugeOMCommonProtocolBooleanValueType ) cdv.marshal( new FugeOMCommonProtocolBooleanValueType(), parameter.getDefaultValue() ) ) );
                } else if ( parameter.getDefaultValue() instanceof ComplexValue ) {
                    genericParameterXML.setDefaultValue( ( new ObjectFactory() ).createComplexValue( ( FugeOMCommonProtocolComplexValueType ) cdv.marshal( new FugeOMCommonProtocolComplexValueType(), parameter.getDefaultValue() ) ) );
                }
            }

            // get generic parameter attributes
            genericParameterXML = cgp.marshal( genericParameterXML, ( GenericParameter ) parameter );

            return genericParameterXML;
        }
        return null; // shouldn't get here as there is currently only one type of Parameter allowed.
    }

    public FugeOMCommonProtocolParameterType generateRandomXML( FugeOMCommonProtocolParameterType parameterXML ) {
        // get parameter attributes
        parameterXML = ( FugeOMCommonProtocolParameterType ) ci.generateRandomXML( parameterXML );

        return parameterXML;
    }

    public FugeOMCommonProtocolParameterType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolParameterType parameterXML,
            FugeOMCollectionFuGEType frXML ) {

        parameterXML = generateRandomXML( parameterXML );

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
        parameterXML.setDefaultValue( ( new ObjectFactory() ).createAtomicValue( ( FugeOMCommonProtocolAtomicValueType ) cdv.generateRandomXMLWithLinksOut( aValue, frXML ) ) );

        // get generic parameter attributes
        parameterXML = cgp.generateRandomXMLWithLinksOut( ( FugeOMCommonProtocolGenericParameterType ) parameterXML, frXML );
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
            parameter.setDefaultValue( cdv.getLatestVersion( parameter.getDefaultValue() ) );
        }

        // get generic parameter attributes
        parameter = cgp.getLatestVersion( ( GenericParameter ) parameter );

        return parameter;
    }
}
