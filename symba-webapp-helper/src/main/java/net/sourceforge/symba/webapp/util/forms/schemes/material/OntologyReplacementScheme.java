package net.sourceforge.symba.webapp.util.forms.schemes.material;

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
public class OntologyReplacementScheme extends BasicMaterialScheme {
    
    private String titleOfReplacement;

    public OntologyReplacementScheme() {
        completeMaterialElementTitle = "completeMaterialOntologyReplacement";
        measuredMaterialElementTitle = "measuredMaterialOntologyReplacement";
        elementTitle = completeMaterialElementTitle;
        isMeasuredMaterial = false;
    }

    /**
     * Parses the "name" parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {
        String[] parsedStrings = parameterName.split( separator );
        if ( parsedStrings[0].equals( completeMaterialElementTitle ) ) {
            setMeasuredMaterial( false );
        } else if ( parsedStrings[0].equals( measuredMaterialElementTitle ) ) {
            setMeasuredMaterial( true );
        }
        titleOfReplacement = parsedStrings[1];
        materialCount = Integer.valueOf( parsedStrings[2] );
        parentOfGpaEndurant = parsedStrings[3];
        datafileNumber = Integer.valueOf( parsedStrings[4] );
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the string to put inside the id and/or name attributes of a form field element
     */
    public String write() {
        return elementTitle + separator + titleOfReplacement + separator + materialCount + separator +
               parentOfGpaEndurant + separator + datafileNumber;
    }

    public void setSpecial( String titleOfReplacement ) {
        this.titleOfReplacement = titleOfReplacement;
    }

    public String getTitleOfReplacement() {
        return titleOfReplacement;
    }
}
