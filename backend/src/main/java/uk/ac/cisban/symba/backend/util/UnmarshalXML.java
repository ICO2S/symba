package uk.ac.cisban.symba.backend.util;

import uk.ac.cisban.symba.backend.util.conversion.xml.XMLUnmarshaler;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/UnmarshalXML.java $
 *
 */

public class UnmarshalXML {
    public static void main( String[] args ) throws Exception {
        if ( args.length != 2 )
            throw new java.lang.Exception( "You must provide 2 arguments in this order: schema-file output-xml-file" );

        XMLUnmarshaler unmarshalTest = new XMLUnmarshaler( args[0], args[1] );
        unmarshalTest.Jaxb2ToFuGE();
    }
}
