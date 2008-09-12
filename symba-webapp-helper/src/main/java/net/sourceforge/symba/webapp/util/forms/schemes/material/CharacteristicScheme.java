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
public class CharacteristicScheme extends BasicMaterialScheme {
    private String multipleElementTitle;
    private String sourceEndurant;
    private boolean isNovel;

    // only used if storing novel characteristics
    private String descriptorOiEndurant;

    private static final String completeMaterialNovelElementTitle = "completeMaterialNovelCharacteristic";
    private static final String measuredMaterialNovelElementTitle = "measuredMaterialNovelCharacteristic";


    public CharacteristicScheme() {
        completeMaterialElementTitle = "completeMaterialCharacteristic";
        measuredMaterialElementTitle = "measuredMaterialCharacteristic";
        elementTitle = completeMaterialElementTitle;
        setMeasuredMaterial( false );        

        multipleElementTitle = elementTitle + "Multiple";
        setNovel( false );
    }

    /**
     * Parses the "name" parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {
        String[] parsedStrings = parameterName.split( separator );
        if ( parsedStrings[0].startsWith( completeMaterialElementTitle ) ||
             parsedStrings[0].startsWith( completeMaterialNovelElementTitle ) ) {
            setMeasuredMaterial( false );
        } else if ( parsedStrings[0].startsWith( measuredMaterialElementTitle ) ||
                    parsedStrings[0].startsWith( measuredMaterialNovelElementTitle ) ) {
            setMeasuredMaterial( true );
        }
        multipleElementTitle = elementTitle + "Multiple";
        materialCount = Integer.valueOf( parsedStrings[1] );
        parentOfGpaEndurant = parsedStrings[2];
        datafileNumber = Integer.valueOf( parsedStrings[3] );
        sourceEndurant = parsedStrings[4];
        if ( parsedStrings.length >= 6 ) {
            descriptorOiEndurant = parsedStrings[5];
            setNovel( true );
        } else {
            setNovel( false );
        }
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the string to put inside the id and/or name attributes of a form field element
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
                                  parentOfGpaEndurant + separator + datafileNumber + separator + sourceEndurant;
        if ( isNovel ) {
            return standardResponse + separator + descriptorOiEndurant;
        }
        return standardResponse;
    }

    public void setNovel( boolean value ) {
        isNovel = value;
        if ( isNovel ) {
            if ( isMeasuredMaterial ) {
                elementTitle = measuredMaterialNovelElementTitle;
            } else {
                elementTitle = completeMaterialNovelElementTitle;
            }
        } else {
            setElementTitle( isMeasuredMaterial );
            descriptorOiEndurant = "";
        }
        multipleElementTitle = elementTitle + "Multiple";
    }

    public String getMultipleElementTitle() {
        return multipleElementTitle;
    }

    public String getSourceEndurant() {
        return sourceEndurant;
    }

    public void setSourceEndurant( String sourceEndurant ) {
        this.sourceEndurant = sourceEndurant;
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