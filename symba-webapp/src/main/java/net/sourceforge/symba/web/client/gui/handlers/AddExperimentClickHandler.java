package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.NewInvestigationPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;


public class AddExperimentClickHandler implements ClickHandler {
    private final SymbaController controller;
    private boolean isActive;

    public AddExperimentClickHandler( SymbaController controller ) {
        this.controller = controller;
        isActive = true;
    }

    public void onClick( ClickEvent event ) {
        if ( isActive ) {
            NewInvestigationPopup panel = new NewInvestigationPopup( controller );
            panel.show();
        }
    }

    public void disable() {
        isActive = false;
    }

    public void enable() {
        isActive = true;
    }
}
