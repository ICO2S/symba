package net.sourceforge.symba.webapp.util.forms.schemes.material;

import net.sourceforge.symba.webapp.util.forms.schemes.BasicScheme;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/forms/schemes/BasicScheme.java $
 */
abstract public class BasicMaterialScheme extends BasicScheme {

    protected boolean isMeasuredMaterial;
    protected String measuredMaterialElementTitle;
    protected String completeMaterialElementTitle;

    public boolean isMeasuredMaterial() {
        return isMeasuredMaterial;
    }

    public void setMeasuredMaterial( boolean measuredMaterial ) {
        isMeasuredMaterial = measuredMaterial;
        setElementTitle( isMeasuredMaterial );
    }

    public void setElementTitle( boolean measuredMaterial ) {
        if ( measuredMaterial ) {
            elementTitle = measuredMaterialElementTitle;
        } else {
            elementTitle = completeMaterialElementTitle;
        }

    }
}
