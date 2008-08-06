package net.sourceforge.symba.mapping.hibernatejaxb2;

import net.sourceforge.symba.mapping.hibernatejaxb2.xml.WorkflowUnmarshaler;

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
 */
public class UnmarshalWorkflow {

    public static void main( String[] args ) throws Exception {
        if ( args.length < 1 )
            throw new java.lang.Exception(
                    "You must provide at least 1 argument: input-xml-file [other input xmlfiles]" );

        System.out.println( "args[0]: " + args[0] );
        WorkflowUnmarshaler unmarshaler = new WorkflowUnmarshaler();
        unmarshaler.Jaxb2ToFuGE( args );

    }
}
