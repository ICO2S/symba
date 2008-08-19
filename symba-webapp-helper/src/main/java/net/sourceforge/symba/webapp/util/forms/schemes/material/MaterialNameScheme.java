package net.sourceforge.symba.webapp.util.forms.schemes.material;

import net.sourceforge.symba.webapp.util.forms.schemes.BasicScheme;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate: 2008-08-06 10:43:17 +0100 (Wed, 06 Aug 2008) $
 * $LastChangedRevision: 194 $
 * $Author: allysonlister $
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/forms/MaterialTemplateParser.java $
 */
public class MaterialNameScheme extends BasicScheme {

    public MaterialNameScheme() {
        elementTitle = "materialName";        
    }

    /**
     * Parses the parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {
        String[] parsedStrings = parameterName.split( separator );
        materialCount = Integer.valueOf( parsedStrings[1] );
        parentOfGpaEndurant = parsedStrings[2];
        datafileNumber = Integer.valueOf( parsedStrings[3] );
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the value to identify the form field element
     */
    public String write() {
        return elementTitle + separator + materialCount + separator +
               parentOfGpaEndurant + separator + datafileNumber;
    }
}
