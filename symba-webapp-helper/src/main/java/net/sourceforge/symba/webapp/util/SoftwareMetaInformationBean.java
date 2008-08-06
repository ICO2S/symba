package net.sourceforge.symba.webapp.util;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Stores all of the information needed for a meta information about this software application. Will be stored
 * as an application variable within the web interface
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/SymbaFormSessionBean.java $
 */
public class SoftwareMetaInformationBean {

    private String name;
    private String version;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }
}
