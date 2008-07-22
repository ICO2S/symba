package net.sourceforge.symba.webapp.util.storage;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.SCPClient;

import java.io.*;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ScpSyMBADataCopier extends SyMBADataCopier {

    protected ScpSyMBADataCopier( String hostname, String username, String password, String directory, String remoteDataStoreOs ) {
        super( hostname, username, password, directory, remoteDataStoreOs );
    }

    /**
     * Copy the file using SCP, and also create a link that has the friendly name of the data file.
     *
     * @param fileToCopy       the file that needs to go to the remote location
     * @param newFilename      what to name the copied file
     * @param friendlyFilename used if an extra symbolic link (or similar) should be made to the actual file with a
     *                         nicer filename, that will be easier to read if browsing the directory.
     * @return true if successfully copied, false if there was a problem.
     */
    public boolean copy( File fileToCopy, String newFilename, String friendlyFilename ) {
        // hostname, username, password, and directory must all be filled in.
        if ( getHostname() == null || getHostname().length() == 0 ||
                getUsername() == null || getUsername().length() == 0 ||
                getPassword() == null || getPassword().length() == 0 ||
                getDirectory() == null || getDirectory().length() == 0 ||
                getRemoteDataStoreOs() == null || getRemoteDataStoreOs().length() == 0 ||
                !fileToCopy.canRead() ||
                newFilename == null || newFilename.length() == 0 ) {
            System.err.println( "One of hostname/username/password/directory/input file/new filename/remote data store OS was not provided." );
            return false;
        }

        /* Create a connection instance */
        Connection conn = new Connection( getHostname() );
        try {
            conn.connect();
        } catch ( IOException e ) {
            System.err.println( "Error creating connection to host: " + getHostname() );
            e.printStackTrace();
            return false;
        }

        boolean isAuthenticated;
        try {
            isAuthenticated = conn.authenticateWithPassword( getUsername(), getPassword() );
        } catch ( IOException e ) {
            System.err.println( "Error authenticating connection with username: " + getUsername() );
            e.printStackTrace();
            return false;
        }

        if ( !isAuthenticated ) {
            System.err.println( "Error authenticating connection with username (returned false from authenticateWithPassword): " + getUsername() );
            return false;
        }

        SCPClient scpClient = new SCPClient( conn );
        byte[] local = new byte[( int ) fileToCopy.length()];
        DataInputStream dis;
        try {
            dis = new DataInputStream( new FileInputStream( fileToCopy ) );
        } catch ( FileNotFoundException e ) {
            System.err.println( "Error opening the file: " + fileToCopy.getName() );
            e.printStackTrace();
            return false;
        }

        try {
            dis.readFully( local );
            dis.close();
        } catch ( IOException e ) {
            System.err.println( "Error reading or closing the file: " + fileToCopy.getName() );
            e.printStackTrace();
            return false;
        }

        try {
            scpClient.put( local, newFilename, getDirectory() );
        } catch ( IOException e ) {
            System.err.println( "Error putting the file to the remote host: " + fileToCopy.getName() );
            e.printStackTrace();
            return false;
        }

        if ( getRemoteDataStoreOs().equals( "unix" ) ) {
            // make a link with the friendly identifier, to make them easier to find.
            // Only do this if we are working in a unix file system
            if ( friendlyFilename != null && friendlyFilename.length() > 0 ) {
                try {
                    Session sess = conn.openSession();
                    sess.execCommand(
                            "ln -sf " + getDirectory() + "/" + newFilename + " " + getDirectory() + "/" +
                                    friendlyFilename );
                    sess.close();
                } catch ( IOException e ) {
                    System.err.println( "Error creating the symbolic link to: " + friendlyFilename );
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Link the file using the same SCP package, and make a friendly identifier.
     *
     * @param originalFilename the file that needs to be linked into the SyMBA remote location (the local and remote
     *                         location are the same)
     * @param newFilename      what to name the symbolic link to the real location of the file on the same machine
     *                         (this should be an LSID)
     * @param friendlyFilename used if an extra symbolic link (or similar) should be made to the actual file with a
     *                         nicer filename, that will be easier to read if browsing the directory.
     * @return true if successfully copied, false if there was a problem.
     */
    public boolean link( String originalFilename, String newFilename, String friendlyFilename ) {
        // hostname, username, password, and directory must all be filled in.
        if ( getHostname() == null || getHostname().length() == 0 ||
                getUsername() == null || getUsername().length() == 0 ||
                getPassword() == null || getPassword().length() == 0 ||
                getDirectory() == null || getDirectory().length() == 0 ||
                originalFilename == null || originalFilename.length() == 0 ||
                newFilename == null || newFilename.length() == 0 ) {
            System.err.println( "One of hostname/username/password/directory/input file/new filename was not provided." );
            return false;
        }

        /* Create a connection instance */
        Connection conn = new Connection( getHostname() );
        try {
            conn.connect();
        } catch ( IOException e ) {
            System.err.println( "Error creating connection to host: " + getHostname() );
            e.printStackTrace();
            return false;
        }

        boolean isAuthenticated;
        try {
            isAuthenticated = conn.authenticateWithPassword( getUsername(), getPassword() );
        } catch ( IOException e ) {
            System.err.println( "Error authenticating connection with username: " + getUsername() );
            e.printStackTrace();
            return false;
        }

        if ( !isAuthenticated ) {
            System.err.println( "Error authenticating connection with username (returned false from authenticateWithPassword): " + getUsername() );
            return false;
        }

        Session sess;
        try {
            sess = conn.openSession();
            sess.execCommand( "mkdir -p " + getDirectory() );
        } catch ( IOException e ) {
            System.err.println( "Error opening the session or making the directory: " + getDirectory() );
            e.printStackTrace();
            return false;
        }

        sess.close();

        // make a link from the actual file on the remote server
        try {
            sess = conn.openSession();
            sess.execCommand(
                    "ln -sf " + getDirectory() + "/" + originalFilename + " " + getDirectory() + "/" +
                            newFilename );
        } catch ( IOException e ) {
            System.err.println( "Error creating the symbolic link to: " + newFilename );
            e.printStackTrace();
            return false;
        }

        sess.close();

        // make a link with the friendly identifier, to make them easier to find.
        if ( friendlyFilename != null && friendlyFilename.length() > 0 ) {
            try {
                sess = conn.openSession();
                sess.execCommand(
                        "ln -sf " + getDirectory() + "/" + originalFilename + " " + getDirectory() + "/" +
                                friendlyFilename );
            } catch ( IOException e ) {
                System.err.println( "Error creating the symbolic link to: " + friendlyFilename );
                e.printStackTrace();
                return false;
            }
        }

        sess.close();

        return true;
    }
}
