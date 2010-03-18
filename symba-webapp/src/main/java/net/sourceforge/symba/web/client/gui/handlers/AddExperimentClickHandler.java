package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.NewInvestigationPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;


public class AddExperimentClickHandler implements ClickHandler {
    final SymbaController controller;

    public AddExperimentClickHandler( SymbaController controller ) {
        this.controller = controller;
    }

    public void onClick( ClickEvent event ) {
        NewInvestigationPopup panel = new NewInvestigationPopup( controller );
        panel.show();
    }
}
