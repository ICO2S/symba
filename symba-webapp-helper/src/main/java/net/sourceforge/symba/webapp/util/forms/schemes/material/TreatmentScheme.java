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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/forms/MaterialTemplateParser.java $
 */
public class TreatmentScheme extends BasicScheme {

    private int treatmentCount;

    public TreatmentScheme() {
        elementTitle = "treatment";
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
        treatmentCount = Integer.valueOf( parsedStrings[4] );
    }

    public void setSpecial( int treatmentCount ) {
        this.treatmentCount = treatmentCount;
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the value to identify the form field element
     */
    public String write() {
        // in this special case where the javascript is adding new treatment blocks, we don't
        // print out the treatmentCount, but instead leave it to the JS.
        return elementTitle + separator + materialCount + separator +
               parentOfGpaEndurant + separator + datafileNumber + separator;
    }

    public int getTreatmentCount() {
        return treatmentCount;
    }
}