package net.sourceforge.symba.util;

import net.sourceforge.symba.util.conversion.xml.XMLMarshaler;
import net.sourceforge.symba.util.conversion.xml.XMLUnmarshaler;

/**
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

// please note that there will always be auditing differences between the input and output xml files,

// which should be taken into account.
public class XMLRoundtrip {
    public static void main( String[] args ) throws Exception {
        if ( args.length != 3 )
            throw new java.lang.Exception(
                    "You must provide 3 arguments in this order: schema-file input-xml-file output-xml-file" );

        XMLUnmarshaler unTest = new XMLUnmarshaler( args[0], args[1] );
        String fugeIdentifier = unTest.Jaxb2ToFuGE();

        XMLMarshaler marshalTest = new XMLMarshaler( args[0] );
        marshalTest.FuGEToJaxb2( fugeIdentifier, args[2] );
    }
}
