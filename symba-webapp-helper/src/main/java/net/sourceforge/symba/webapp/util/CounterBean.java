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
public class CounterBean implements Serializable {

    private long numberOfExperiments;
    private long numberOfDataFiles;

    public CounterBean() {
    }

    public long getNumberOfExperiments() {
        return numberOfExperiments;
    }

    public void setNumberOfExperiments( long numberOfExperiments ) {
        this.numberOfExperiments = numberOfExperiments;
    }

    public long getNumberOfDataFiles() {
        return numberOfDataFiles;
    }

    public void setNumberOfDataFiles( long numberOfDataFiles ) {
        this.numberOfDataFiles = numberOfDataFiles;
    }
}
