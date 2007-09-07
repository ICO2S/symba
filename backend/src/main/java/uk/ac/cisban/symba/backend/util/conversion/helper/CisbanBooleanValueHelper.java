package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Protocol.BooleanValue;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolBooleanValueType;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanBooleanValueHelper.java $
 *
 */

public class CisbanBooleanValueHelper {

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public BooleanValue unmarshalBooleanValue( FugeOMCommonProtocolBooleanValueType valueXML, BooleanValue value ) {

        // set any BooleanValue-specific traits

        // default value
        value.setDefaultValue( valueXML.isDefaultValue() );

        return value;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolBooleanValueType marshalBooleanValue( FugeOMCommonProtocolBooleanValueType valueXML,
                                                                     BooleanValue value ) {

        // set any BooleanValue-specific traits
        valueXML.setDefaultValue( value.getDefaultValue() );

        return valueXML;
    }

    public FugeOMCommonProtocolBooleanValueType generateRandomXML( FugeOMCommonProtocolBooleanValueType valueXML ) {
        valueXML.setDefaultValue( true );
        return valueXML;
    }

    // Nothing to do here at present - just a nice placeholder
    public BooleanValue getLatestVersion( BooleanValue booleanValue ) {
        return booleanValue;
    }
}
