package net.sourceforge.symba.webapp.util.storage;

import java.io.File;

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
abstract public class SyMBADataCopier {

    /*
     The variables below are class variables so that only those that are useful to specific concrete
     implementations of this class need to be filled. If individual concrete implementations don't wish
     to use one of them, just pass null in the constructor.
     */
    private String hostname;
    private String username;
    private String password;
    private String directory;

    protected SyMBADataCopier( String hostname, String username, String password, String directory ) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.directory = directory;
    }

    /**
     * The actual copy method that *must* be implemented by any concrete subclass. You should include
     * checking mechanisms to ensure that the class variables you make use of (e.g. hostname) have
     * already been filled, and return false if they haven't. Your concrete implementation does not
     * need to use the friendlyFilename option, but if it does, make sure it isn't null before using.
     *
     * @param fileToCopy       the file that needs to go to the remote location
     * @param newFilename      what to name the copied file
     * @param friendlyFilename used if an extra symbolic link (or similar) should be made to the actual file with a
     *                         nicer filename, that will be easier to read if browsing the directory.
     * @return true if successfully copied, false if there was a problem.
     */
    abstract public boolean copy( File fileToCopy, String newFilename, String friendlyFilename );

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDirectory() {
        return directory;
    }
}
