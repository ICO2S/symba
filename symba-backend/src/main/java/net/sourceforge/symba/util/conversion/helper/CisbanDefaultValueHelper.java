package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Protocol.AtomicValue;
import fugeOM.Common.Protocol.BooleanValue;
import fugeOM.Common.Protocol.ComplexValue;
import fugeOM.Common.Protocol.DefaultValue;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

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
public class CisbanDefaultValueHelper implements MappingHelper<DefaultValue, FugeOMCommonProtocolDefaultValueType> {
    private final CisbanDescribableHelper cd;
    private final CisbanAtomicValueHelper cav;
    private final CisbanBooleanValueHelper cbv;
    private final CisbanComplexValueHelper ccv;

    public CisbanDefaultValueHelper() {
        this.cd = new CisbanDescribableHelper();
        this.cav = new CisbanAtomicValueHelper();
        this.cbv = new CisbanBooleanValueHelper();
        this.ccv = new CisbanComplexValueHelper();
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    // currently not using the defaultValue argument - just pass "null"
    public DefaultValue unmarshal( FugeOMCommonProtocolDefaultValueType defaultValueXML, DefaultValue defaultValue )
            throws RealizableEntityServiceException {

        if ( defaultValueXML instanceof FugeOMCommonProtocolAtomicValueType ) {
            AtomicValue value = ( AtomicValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.AtomicValue" );
            // get default value attributes
            value = ( AtomicValue ) cd.unmarshal( defaultValueXML, value );
            // get atomic value attributes
            value = cav.unmarshal( ( FugeOMCommonProtocolAtomicValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.AtomicValue", value );
            return value;
        } else if ( defaultValueXML instanceof FugeOMCommonProtocolBooleanValueType ) {
            BooleanValue value = ( BooleanValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.BooleanValue" );
            // get default value attributes
            value = ( BooleanValue ) cd.unmarshal( defaultValueXML, value );
            // get boolean value attributes
            value = cbv.unmarshal( ( FugeOMCommonProtocolBooleanValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.BooleanValue", value );
            return value;
        } else if ( defaultValueXML instanceof FugeOMCommonProtocolComplexValueType ) {
            ComplexValue value = ( ComplexValue ) reService.createDescribableOb( "fugeOM.Common.Protocol.ComplexValue" );
            // get default value attributes
            value = ( ComplexValue ) cd.unmarshal( defaultValueXML, value );
            // get complex value attributes
            value = ccv.unmarshal( ( FugeOMCommonProtocolComplexValueType ) defaultValueXML, value );
            reService.createObInDB( "fugeOM.Common.Protocol.ComplexValue", value );
            return value;
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    // defaultValueXML currently unused.
    public FugeOMCommonProtocolDefaultValueType marshal( FugeOMCommonProtocolDefaultValueType defaultValueXML, DefaultValue defaultValue ) {

        if ( defaultValue instanceof AtomicValue ) {
            FugeOMCommonProtocolAtomicValueType valueXML = new FugeOMCommonProtocolAtomicValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolAtomicValueType ) cd.marshal( valueXML, defaultValue );
            // get atomic value attributes
            valueXML = cav.marshal( valueXML, ( AtomicValue ) defaultValue );
            return ( valueXML );
        } else if ( defaultValue instanceof BooleanValue ) {
            FugeOMCommonProtocolBooleanValueType valueXML = new FugeOMCommonProtocolBooleanValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolBooleanValueType ) cd.marshal( valueXML, defaultValue );
            // get boolean value attributes
            valueXML = cbv.marshal( valueXML, ( BooleanValue ) defaultValue );
            return ( valueXML );
        } else if ( defaultValue instanceof ComplexValue ) {
            FugeOMCommonProtocolComplexValueType valueXML = new FugeOMCommonProtocolComplexValueType();
            // get default value attributes
            valueXML = ( FugeOMCommonProtocolComplexValueType ) cd.marshal( valueXML, defaultValue );
            // get complex value attributes
            valueXML = ccv.marshal( valueXML, ( ComplexValue ) defaultValue );
            return ( valueXML );
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    public FugeOMCommonProtocolDefaultValueType generateRandomXML( FugeOMCommonProtocolDefaultValueType defaultXML ) {
        defaultXML = ( FugeOMCommonProtocolDefaultValueType ) cd.generateRandomXML( defaultXML );

        if ( defaultXML instanceof FugeOMCommonProtocolAtomicValueType ) {
            // get atomic value attributes
            defaultXML = cav.generateRandomXML( ( FugeOMCommonProtocolAtomicValueType ) defaultXML );
            return ( defaultXML );
        } else if ( defaultXML instanceof FugeOMCommonProtocolBooleanValueType ) {
            // get boolean value attributes
            defaultXML = cbv.generateRandomXML( ( FugeOMCommonProtocolBooleanValueType ) defaultXML );
            return ( defaultXML );
        }

        return defaultXML;
    }

    public FugeOMCommonProtocolDefaultValueType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolDefaultValueType defaultXML, FugeOMCollectionFuGEType frXML ) {

        defaultXML = generateRandomXML( defaultXML );

        if ( defaultXML instanceof FugeOMCommonProtocolComplexValueType ) {
            // get complex value attributes
            defaultXML = ccv.generateRandomXMLWithLinksOut( ( FugeOMCommonProtocolComplexValueType ) defaultXML, frXML );
            return ( defaultXML );
        }

        return defaultXML;
    }

    public DefaultValue getLatestVersion( DefaultValue defaultValue
    ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        defaultValue = ( DefaultValue ) cd.getLatestVersion( defaultValue );

        // get the attributes for the various versions of DefaultValue 
        if ( defaultValue instanceof AtomicValue ) {
            defaultValue = cav.getLatestVersion( ( AtomicValue ) defaultValue );
        } else if ( defaultValue instanceof BooleanValue ) {
            defaultValue = cbv.getLatestVersion( ( BooleanValue ) defaultValue );
        } else if ( defaultValue instanceof ComplexValue ) {
            defaultValue = ccv.getLatestVersion( ( ComplexValue ) defaultValue );
        }

        return defaultValue;
    }
}
