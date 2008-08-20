package net.sourceforge.symba.webapp.util.forms.schemes;

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
abstract public class BasicScheme extends SkeletonScheme {
    protected String parentOfGpaEndurant;

    // May not always be used / valid : depends on the circumstances.
    protected int materialCount;

    // May not always be used / valid : depends on the circumstances.
    protected int datafileNumber;

    public void setBasic( String parentGpaEndurant, int materialCount, int datafileNumber ) {
        this.parentOfGpaEndurant = parentGpaEndurant;
        this.materialCount = materialCount;
        this.datafileNumber = datafileNumber;
    }

    public String getParentOfGpaEndurant() {
        return parentOfGpaEndurant;
    }

    public int getMaterialCount() {
        return materialCount;
    }

    public int getDatafileNumber() {
        return datafileNumber;
    }
}
