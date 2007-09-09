package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Protocol.AtomicValue;
import fugeOM.Common.Protocol.BooleanValue;
import fugeOM.Common.Protocol.ComplexValue;
import fugeOM.Common.Protocol.DefaultValue;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;
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

// currently, defaultValue is just describable.
public class CisbanDefaultValueHelper {
    private final CisbanDescribableHelper cd;
    private final CisbanAtomicValueHelper cav;
    private final CisbanBooleanValueHelper cbv;
    private final CisbanComplexValueHelper ccv;
    private final RealizableEntityService reService;

    public CisbanDefaultValueHelper( RealizableEntityService reService, CisbanDescribableHelper cd ) {
        this.reService = reService;
        this.cd = cd;
        this.cav = new CisbanAtomicValueHelper();
        this.cbv = new CisbanBooleanValueHelper();
        this.ccv = new CisbanComplexValueHelper( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public DefaultValue unmarshalDefaultValue( FugeOMCommonProtocolDefaultValueType defaultValueXML )
            throws URISyntaxException, RealizableEntityServiceException {

        if ( defaultValueXML instanceof FugeOMCommonProtocolAtomicValueType ) {
            AtomicValue value = ( AtomicValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.AtomicValue" );
            // get default value attributes
            value = ( AtomicValue ) cd.unmarshalDescribable( defaultValueXML, value );
            // get atomic value attributes
            value = cav.unmarshalAtomicValue( ( FugeOMCommonProtocolAtomicValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.AtomicValue", value );
            return value;
        } else if ( defaultValueXML instanceof FugeOMCommonProtocolBooleanValueType ) {
            BooleanValue value = ( BooleanValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.BooleanValue" );
            // get default value attributes
            value = ( BooleanValue ) cd.unmarshalDescribable( defaultValueXML, value );
            // get boolean value attributes
            value = cbv.unmarshalBooleanValue( ( FugeOMCommonProtocolBooleanValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.BooleanValue", value );
            return value;
        } else if ( defaultValueXML instanceof FugeOMCommonProtocolComplexValueType ) {
            ComplexValue value = ( ComplexValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.ComplexValue" );
            // get default value attributes
            value = ( ComplexValue ) cd.unmarshalDescribable( defaultValueXML, value );
            // get complex value attributes
            value = ccv.unmarshalComplexValue( ( FugeOMCommonProtocolComplexValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.ComplexValue", value );
            return value;
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public JAXBElement<? extends FugeOMCommonProtocolDefaultValueType> marshalDefaultValue( DefaultValue defaultValue )
            throws URISyntaxException {

        if ( defaultValue instanceof AtomicValue ) {
            FugeOMCommonProtocolAtomicValueType valueXML = new FugeOMCommonProtocolAtomicValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolAtomicValueType ) cd.marshalDescribable( valueXML, defaultValue );
            // get atomic value attributes
            valueXML = cav.marshalAtomicValue( valueXML, ( AtomicValue ) defaultValue );
            return ( new ObjectFactory() ).createAtomicValue( valueXML );
        } else if ( defaultValue instanceof BooleanValue ) {
            FugeOMCommonProtocolBooleanValueType valueXML = new FugeOMCommonProtocolBooleanValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolBooleanValueType ) cd.marshalDescribable( valueXML, defaultValue );
            // get boolean value attributes
            valueXML = cbv.marshalBooleanValue( valueXML, ( BooleanValue ) defaultValue );
            return ( new ObjectFactory() ).createBooleanValue( valueXML );
        } else if ( defaultValue instanceof ComplexValue ) {
            FugeOMCommonProtocolComplexValueType valueXML = new FugeOMCommonProtocolComplexValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolComplexValueType ) cd.marshalDescribable( valueXML, defaultValue );
            // get complex value attributes
            valueXML = ccv.marshalComplexValue( valueXML, ( ComplexValue ) defaultValue );
            return ( new ObjectFactory() ).createComplexValue( valueXML );
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    public JAXBElement<? extends FugeOMCommonProtocolDefaultValueType> generateRandomXML(
            FugeOMCommonProtocolDefaultValueType defaultXML, FugeOMCollectionFuGEType frXML ) {

        CisbanIdentifiableHelper ci = new CisbanIdentifiableHelper( reService, cd );

        defaultXML = ( FugeOMCommonProtocolDefaultValueType ) cd.generateRandomXML( defaultXML, ci );

        if ( defaultXML instanceof FugeOMCommonProtocolAtomicValueType ) {
            // get atomic value attributes
            defaultXML = cav.generateRandomXML( ( FugeOMCommonProtocolAtomicValueType ) defaultXML );
            return ( new ObjectFactory() ).createAtomicValue( ( FugeOMCommonProtocolAtomicValueType ) defaultXML );
        } else if ( defaultXML instanceof FugeOMCommonProtocolBooleanValueType ) {
            // get boolean value attributes
            defaultXML = cbv.generateRandomXML( ( FugeOMCommonProtocolBooleanValueType ) defaultXML );
            return ( new ObjectFactory() ).createBooleanValue( ( FugeOMCommonProtocolBooleanValueType ) defaultXML );
        } else if ( defaultXML instanceof FugeOMCommonProtocolComplexValueType ) {
            // get complex value attributes
            defaultXML = ccv.generateRandomXML( ( FugeOMCommonProtocolComplexValueType ) defaultXML, frXML );
            return ( new ObjectFactory() ).createComplexValue( ( FugeOMCommonProtocolComplexValueType ) defaultXML );
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    public DefaultValue getLatestVersion( DefaultValue defaultValue,
                                          CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        defaultValue = ( DefaultValue ) cd.getLatestVersion( defaultValue, ci );

        // get the attributes for the various versions of DefaultValue 
        if ( defaultValue instanceof AtomicValue ) {
            defaultValue = cav.getLatestVersion( ( AtomicValue ) defaultValue );
        } else if ( defaultValue instanceof BooleanValue ) {
            defaultValue = cbv.getLatestVersion( ( BooleanValue ) defaultValue );
        } else if ( defaultValue instanceof ComplexValue ) {
            defaultValue = ccv.getLatestVersion( ( ComplexValue ) defaultValue, ci );
        }

        return defaultValue;
    }
}
