package net.sourceforge.symba.webapp.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class FileRetrieve {

    public FileRetrieve() {

    }

    public File getFile( String LSID,
                         String friendly,
                         String path,
                         ScpBean scp ) throws IOException {

        /* Create a connection instance */
        Connection conn = new Connection( scp.getHostname() );
        conn.connect();

        boolean isAuthenticated = conn.authenticateWithPassword( scp.getUsername(), scp.getPassword() );

        if ( !isAuthenticated ) {
            throw new IOException( "Authentication failed." );
        }
        
        String directoryForFile = scp.getDirectory() + "/";

        SCPClient scpClient = new SCPClient( conn );

        File returnFile = new File( path + File.separator + friendly );
        FileOutputStream fos = new FileOutputStream( returnFile );
        scpClient.get( directoryForFile + "/" + LSID, fos );
        fos.flush();
        fos.close();
        return returnFile;
    }

}
