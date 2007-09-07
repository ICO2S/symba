package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Protocol.AtomicValue;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolAtomicValueType;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanAtomicValueHelper.java $
 *
 */

public class CisbanAtomicValueHelper {

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public AtomicValue unmarshalAtomicValue( FugeOMCommonProtocolAtomicValueType valueXML, AtomicValue value ) {

        // set any AtomicValue-specific traits

        // default value
        if ( valueXML.getDefaultValue() != null ) {
            value.setDefaultValue( valueXML.getDefaultValue() );
        }

        return value;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolAtomicValueType marshalAtomicValue( FugeOMCommonProtocolAtomicValueType valueXML,
                                                                   AtomicValue value ) {

        // set any AtomicValue-specific traits
        if ( value.getDefaultValue() != null )
            valueXML.setDefaultValue( value.getDefaultValue() );

        return valueXML;
    }

    public FugeOMCommonProtocolAtomicValueType generateRandomXML(
            FugeOMCommonProtocolAtomicValueType valueXML ) {
        valueXML.setDefaultValue( "5" );
        return valueXML;
    }

    // Nothing to do here at present - just a nice placeholder
    public AtomicValue getLatestVersion( AtomicValue atomicValue ) {
        return atomicValue;
    }
}
