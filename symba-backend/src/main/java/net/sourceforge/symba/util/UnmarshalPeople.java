package net.sourceforge.symba.util;

import net.sourceforge.symba.util.conversion.xml.PeopleUnmarshaler;

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

public class UnmarshalPeople {

    public static void main( String[] args ) throws Exception {

        if ( args.length != 1 )
            throw new java.lang.Exception( "You must provide 1 argument: input-xml-file" );

        System.out.println( "args[0]: " + args[0] );
        PeopleUnmarshaler peopleUnmarshaler = new PeopleUnmarshaler( args[0] );
        peopleUnmarshaler.Jaxb2ToFuGE();
    }
}
