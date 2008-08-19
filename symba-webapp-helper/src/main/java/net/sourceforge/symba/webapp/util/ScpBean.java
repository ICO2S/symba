package net.sourceforge.symba.webapp.util;

import java.io.Serializable;

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

public class ScpBean implements Serializable {

    private String hostname;
    private String username;
    private String password;
    private String directory;
    private String remoteDataStoreOs;  /// may only be "unix" or "dos"
    private String lsidColonReplacement; /// only used if remoteDataStoreOs is parse to "dos"

    public ScpBean() {
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname( String hostname ) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory( String directory ) {
        this.directory = directory;
    }

    public String getRemoteDataStoreOs() {
        return remoteDataStoreOs;
    }

    public void setRemoteDataStoreOs( String remoteDataStoreOs ) {
        this.remoteDataStoreOs = remoteDataStoreOs;
    }

    public String getLsidColonReplacement() {
        return lsidColonReplacement;
    }

    public void setLsidColonReplacement( String lsidColonReplacement ) {
        this.lsidColonReplacement = lsidColonReplacement;
    }
}
