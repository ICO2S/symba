package net.sourceforge.symba.webapp.util;

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

public class ActionSummary {

    private String chosenActionEndurant; // the endurant of the action selected
    private String chosenActionName; // the name of the action selected
    private String chosenChildProtocolEndurant; // the endurant of the protocol associated with the chosenActionEndurant
    private String chosenChildProtocolName; // the name of the protocol associated with the chosenActionEndurant

    public String getChosenActionEndurant() {
        return chosenActionEndurant;
    }

    public void setChosenActionEndurant( String chosenActionEndurant ) {
        this.chosenActionEndurant = chosenActionEndurant;
    }

    public String getChosenActionName() {
        return chosenActionName;
    }

    public void setChosenActionName( String chosenActionName ) {
        this.chosenActionName = chosenActionName;
    }

    public String getChosenChildProtocolEndurant() {
        return chosenChildProtocolEndurant;
    }

    public void setChosenChildProtocolEndurant( String chosenChildProtocolEndurant ) {
        this.chosenChildProtocolEndurant = chosenChildProtocolEndurant;
    }

    public String getChosenChildProtocolName() {
        return chosenChildProtocolName;
    }

    public void setChosenChildProtocolName( String chosenChildProtocolName ) {
        this.chosenChildProtocolName = chosenChildProtocolName;
    }
}
