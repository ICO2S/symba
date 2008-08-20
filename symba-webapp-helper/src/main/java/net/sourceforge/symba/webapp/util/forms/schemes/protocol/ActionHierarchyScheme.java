package net.sourceforge.symba.webapp.util.forms.schemes.protocol;

import net.sourceforge.symba.webapp.util.forms.schemes.SkeletonScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * too different from the others to extend from the BasicScheme
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ActionHierarchyScheme extends SkeletonScheme {

    // gpaIdentifier is not always filled as soon as an actionHierarchy is created.
    private String gpaIdentifier;
    private List<ActionInformation> actionHierarchy;
    private boolean isDummy;

    public ActionHierarchyScheme() {

        this.elementTitle = "gpaActionHierarchy";
        this.actionHierarchy = new ArrayList<ActionInformation>();
        setDummy(false);
    }

    /**
     * Parses the parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {

        // the gpaIdentifier is optional. It is present if the first item in the split list does not contain any
        // separators from the ActionInformation.

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
     * @return the value to identify the form field "value" element
     */
    public String write() {
        String label = "";
        boolean labelBegun = false;
        if ( gpaIdentifier != null && gpaIdentifier.length() > 0 ) {
            label = gpaIdentifier;
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

    public void add( ActionInformation actionInformation ) {
        this.actionHierarchy.add( actionInformation );
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
