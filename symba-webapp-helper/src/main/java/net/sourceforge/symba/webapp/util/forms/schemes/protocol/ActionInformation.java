package net.sourceforge.symba.webapp.util.forms.schemes.protocol;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Not a complete scheme - instead, used in the ActionHierarchyScheme class.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ActionInformation {
    private final String separator = "---";

    private String parentProtocolIdentifier;
    private String parentProtocolName;
    private String actionEndurant;
    private String actionName;
    private String protocolOfActionIdentifier;

    public ActionInformation() {
    }

    public ActionInformation( String parentProtocolIdentifier,
                              String parentProtocolName,
                              String actionEndurant,
                              String actionName, String protocolOfActionIdentifier ) {
        this.parentProtocolIdentifier = parentProtocolIdentifier;
        this.parentProtocolName = parentProtocolName;
        this.actionEndurant = actionEndurant;
        this.actionName = actionName;
        this.protocolOfActionIdentifier = protocolOfActionIdentifier;
    }

    /**
     * Parses the parameter from a form field, filling the class members for later access
     *
     * @param parameterName the raw name of the parameter from the form.
     */
    public void parse( String parameterName ) {
        String[] parsedStrings = parameterName.split( separator );
        parentProtocolIdentifier = parsedStrings[0];
        parentProtocolName = parsedStrings[1];
        actionEndurant = parsedStrings[2];
        actionName = parsedStrings[3];
        protocolOfActionIdentifier = parsedStrings[4];
    }

    /**
     * Assuming the values have been set, the write method allows the creation of the value
     * of the "id" and "name" attributes within a form field element.
     *
     * @return the value to identify the form field element
     */
    public String write() {
        return parentProtocolIdentifier + separator + parentProtocolName + separator + actionEndurant + separator +
               actionName + separator + protocolOfActionIdentifier;
    }

    public String getSeparator() {
        return separator;
    }

    public String getParentProtocolIdentifier() {
        return parentProtocolIdentifier;
    }

    public String getParentProtocolName() {
        return parentProtocolName;
    }

    public String getActionEndurant() {
        return actionEndurant;
    }

    public String getActionName() {
        return actionName;
    }

    public String getProtocolOfActionIdentifier() {
        return protocolOfActionIdentifier;
    }
}
