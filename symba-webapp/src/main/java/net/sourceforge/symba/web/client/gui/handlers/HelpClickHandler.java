package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class HelpClickHandler implements ClickHandler {
    private final SymbaController controller;

    public HelpClickHandler( SymbaController controller ) {

        this.controller = controller;
    }

    public void onClick( ClickEvent event ) {
        // do nothing at the moment - haven't decided how to deal with the Help.
    }
}
