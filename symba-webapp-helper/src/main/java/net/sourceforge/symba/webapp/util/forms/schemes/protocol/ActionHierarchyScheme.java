package net.sourceforge.symba.webapp.util.forms.schemes.protocol;

import net.sourceforge.symba.webapp.util.forms.schemes.BasicScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * provides write and parse methods for both the name/id attribute and the value attribute
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ActionHierarchyScheme extends BasicScheme {

    // gpaIdentifier is not always filled as soon as an actionHierarchy is created.
    private String gpaIdentifier;
    private List<ActionInformation> actionHierarchy;
    private boolean isDummy;
    private boolean isAssay;
    private static final String startingElementTitle = "gpaActionHierarchy";

    public ActionHierarchyScheme() {

        this.elementTitle = startingElementTitle;
        this.actionHierarchy = new ArrayList<ActionInformation>();
        setDummy( false );
        setAssay( false );
    }

    public ActionHierarchyScheme( ActionHierarchyScheme actionHierarchyScheme ) {
        this.elementTitle = startingElementTitle;
        setBasic( actionHierarchyScheme.getParentOfGpaEndurant(), actionHierarchyScheme.getMaterialCount(),
                actionHierarchyScheme.getDatafileNumber() );
        this.gpaIdentifier = actionHierarchyScheme.getGpaIdentifier();
        this.isDummy = actionHierarchyScheme.isDummy();
        this.isAssay = actionHierarchyScheme.isAssay();
        actionHierarchy = new ArrayList<ActionInformation>( actionHierarchyScheme.getActionHierarchy() );

    }

    /**
     * Parses the "name" parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {
        String[] parsedStrings = parameterName.split( separator );

        // the datafileNumber will only be present if running through the assay protocol form
        if ( parsedStrings.length >= 2 ) {
            this.datafileNumber = Integer.parseInt( parsedStrings[1] );
            setAssay( true );
        }
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the string to put inside the id and/or name attributes of a form field element
     */
    public String write() {
        String label = elementTitle;
        if ( isAssay ) {
            label += separator + datafileNumber;
        }
        return label;
    }

    /**
     * Parses the parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parseValueAttribute( String parameterName ) {

        // the gpaIdentifier is optional. It is present at the beginning, if the first
        // splitted term does not contain separators from the ActionInformation.

        String[] parsedStrings = parameterName.split( separator );
        for ( int iii = 0; iii < parsedStrings.length; iii++ ) {
            String parsedString = parsedStrings[iii];
            ActionInformation actionInfo = new ActionInformation();
            if ( iii == 0 && !parsedString.contains( actionInfo.getSeparator() ) ) {
                this.gpaIdentifier = parsedString;
            } else {
                actionInfo.parse( parsedString );
                this.actionHierarchy.add( actionInfo );
            }
        }
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * elements of the form. Note that this is different behaviour from the rest of the
     * schemes. In this case, the id and name should be just the value of elementTitle.
     *
     * @return the string to put inside the id and/or name attributes of a form field element
     */
    public String writeValueAttribute() {
        String label = "";
        boolean labelBegun = false;
        if ( gpaIdentifier != null && gpaIdentifier.length() > 0 ) {
            label += gpaIdentifier;
            labelBegun = true;
        }
        for ( ActionInformation actionInformation : actionHierarchy ) {
            if ( labelBegun ) {
                label += separator;
            } else {
                labelBegun = true;
            }
            label += actionInformation.write();
        }
        return label;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setDummy( boolean dummy ) {
        isDummy = dummy;
        if ( isDummy ) {
            elementTitle = "gpaDummyActionHierarchy";
        }
    }

    public boolean isAssay() {
        return isAssay;
    }

    public void setAssay( boolean assay ) {
        this.isAssay = assay;
    }

    public void add( ActionInformation actionInformation ) {
        this.actionHierarchy.add( actionInformation );
    }

    public void remove( int position ) {
        this.actionHierarchy.remove( position );
    }

    public List<ActionInformation> getActionHierarchy() {
        return actionHierarchy;
    }

    public String getGpaIdentifier() {
        return gpaIdentifier;
    }

    public void setGpaIdentifier( String gpaIdentifier ) {
        this.gpaIdentifier = gpaIdentifier;
    }
}
