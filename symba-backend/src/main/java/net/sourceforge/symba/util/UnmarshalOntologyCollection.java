package net.sourceforge.symba.util;

import net.sourceforge.symba.util.conversion.xml.OntologyUnmarshaler;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * Unmarshal an OntologyCollection (but only load its contents, and not the collection itself) into the database.
 *
 * $LastChangedDate: 2008-05-22 15:03:34 +0100 (Thu, 22 May 2008) $
 * $LastChangedRevision: 138 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-backend/src/main/java/net/sourceforge/symba/util/UnmarshalPeople.java $
 *
 */
public class UnmarshalOntologyCollection {
    public static void main( String[] args ) throws Exception {

        if ( args.length != 1 )
            throw new java.lang.Exception( "You must provide 1 argument: input-xml-file" );

        System.out.println( "args[0]: " + args[0] );
        OntologyUnmarshaler ontologyUnmarshaler = new OntologyUnmarshaler( args[0] );
        ontologyUnmarshaler.Jaxb2ToFuGE();
    }
}
