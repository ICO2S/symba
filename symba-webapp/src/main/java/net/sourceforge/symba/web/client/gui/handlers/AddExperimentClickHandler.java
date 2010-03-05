package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;


public class AddExperimentClickHandler implements ClickHandler {
    final SymbaControllerPanel dock;

    public AddExperimentClickHandler( SymbaControllerPanel dock ) {
        this.dock = dock;
    }

    public void onClick( ClickEvent event ) {
        dock.setCenterWidgetAsEditExperiment();
        ( ( EditInvestigationTable ) dock.getCenterWidget() ).displayEmptyInvestigation( );
    }
}
