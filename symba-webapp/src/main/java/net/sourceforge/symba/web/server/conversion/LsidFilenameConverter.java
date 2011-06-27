package net.sourceforge.symba.web.server.conversion;

/**
 * this is just a simple class for now, but kept as its own class in case more complex work needs to be done
 * in future. It simply converts an LSID string, which is incompatible as a filename within DOS, into something
 * more Windows-friendly.
 *
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
public class LsidFilenameConverter {

    public static String convert(String lsid, String replacementForColon) {

        return lsid.replaceAll( ":", replacementForColon );
    }
}
