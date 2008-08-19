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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/forms/MaterialTemplateParser.java $
 */
abstract public class SkeletonScheme {
    protected final String separator = "::";

    protected String elementTitle;

    /**
     * Parses the parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    abstract public void parse( String parameterName );

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the value to identify the form field element
     */
    abstract public String write();

    public String getElementTitle() {
        return elementTitle;
    }
}
