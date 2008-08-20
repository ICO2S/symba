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
 * $HeadURL$
 */
public class CharacteristicScheme extends BasicScheme {
    private String multipleElementTitle;

    // only used if storing novel characteristics
    private String descriptorOiEndurant;
    private boolean isNovel;

    public CharacteristicScheme() {
        elementTitle = "characteristic";
        multipleElementTitle = elementTitle + "Multiple";
        setNovel( false );
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
        if ( parsedStrings.length >= 5 ) {
            descriptorOiEndurant = parsedStrings[4];
            setNovel( true );
        } else {
            setNovel( false );
        }
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the value to identify the form field element
     */
    public String write() {
        return writeEither( false );
    }

    public String writeMultiple() {
        return writeEither( true );
    }

    private String writeEither( boolean isMultiple ) {
        String title = elementTitle;
        if ( isMultiple ) {
            title = multipleElementTitle;
        }
        String standardResponse = title + separator + materialCount + separator +
                                  parentOfGpaEndurant + separator + datafileNumber;
        if ( isNovel ) {
            return standardResponse + separator + descriptorOiEndurant;
        }
        return standardResponse;
    }

    public void setNovel( boolean value ) {
        isNovel = value;
        if ( isNovel ) {
            elementTitle = "novelCharacteristic";
        } else {
            elementTitle = "characteristic";
        }
        multipleElementTitle = elementTitle + "Multiple";
    }

    public String getMultipleElementTitle() {
        return multipleElementTitle;
    }

    public String getDescriptorOiEndurant() {
        return descriptorOiEndurant;
    }

    public void setDescriptorOiEndurant( String descriptorOiEndurant ) {
        this.descriptorOiEndurant = descriptorOiEndurant;
        setNovel( true );
    }

    public boolean isNovel() {
        return isNovel;
    }
}