package uk.ac.cisban.symba.webapp.util;

import java.io.Serializable;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate:$
 * $LastChangedRevision:$
 * $Author:$
 * $HeadURL:$
 *
 */
public class CounterBean implements Serializable {

    private int numberOfExperiments;
    private int numberOfDataFiles;

    public CounterBean() {
    }

    public int getNumberOfExperiments() {
        return numberOfExperiments;
    }

    public void setNumberOfExperiments( int numberOfExperiments ) {
        this.numberOfExperiments = numberOfExperiments;
    }

    public int getNumberOfDataFiles() {
        return numberOfDataFiles;
    }

    public void setNumberOfDataFiles( int numberOfDataFiles ) {
        this.numberOfDataFiles = numberOfDataFiles;
    }
}
