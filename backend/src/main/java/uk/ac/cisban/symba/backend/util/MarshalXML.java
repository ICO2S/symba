package uk.ac.cisban.symba.backend.util;

import uk.ac.cisban.symba.backend.util.conversion.xml.XMLMarshaler;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/MarshalXML.java $
 *
 */

public class MarshalXML {
    public static void main( String[] args ) throws Exception {
        if ( args.length != 3 )
            throw new java.lang.Exception(
                    "You must provide 3 arguments in this order: schema-file identifier output-xml-file" );

        XMLMarshaler marshaler = new XMLMarshaler( args[0] );
        marshaler.FuGEToJaxb2( args[1], args[2] );
    }
}
