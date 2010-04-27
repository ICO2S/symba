package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class LogoutClickHandler implements ClickHandler {
    private SymbaController controller;

    public LogoutClickHandler( SymbaController controller ) {
        this.controller = controller;
    }

    public void onClick( ClickEvent event ) {
        controller.unsetUser();
        controller.cancelAll();
    }
}
