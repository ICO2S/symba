package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.panel.ContactPopupPanel;
import net.sourceforge.symba.web.client.gui.panel.NewInvestigationPopupPanel;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;


public class AddExperimentClickHandler implements ClickHandler {
    final SymbaControllerPanel dock;

    public AddExperimentClickHandler( SymbaControllerPanel dock ) {
        this.dock = dock;
    }

    public void onClick( ClickEvent event ) {
        NewInvestigationPopupPanel panel = new NewInvestigationPopupPanel( dock );
        panel.show();
    }
}
