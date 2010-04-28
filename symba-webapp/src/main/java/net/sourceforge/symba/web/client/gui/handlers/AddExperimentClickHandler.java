package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import net.sourceforge.symba.web.client.gui.panel.NewInvestigationPopup;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;


public class AddExperimentClickHandler extends ActivateableClickHandler {
    private final SymbaController controller;

    public AddExperimentClickHandler( SymbaController controller ) {
        super();
        this.controller = controller;
    }

    @Override
    protected boolean runClickMethod( ClickEvent event ) {
        NewInvestigationPopup panel = new NewInvestigationPopup( controller );
        panel.show();
        return true;
    }
}
