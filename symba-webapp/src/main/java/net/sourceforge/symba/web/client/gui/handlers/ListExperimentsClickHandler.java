package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import net.sourceforge.symba.web.client.InvestigationsService;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationTable;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;

public class ListExperimentsClickHandler implements ClickHandler {
    final SymbaControllerPanel dock;

    public ListExperimentsClickHandler( SymbaControllerPanel dock ) {
        this.dock = dock;
    }

    public void onClick( ClickEvent event ) {
        InvestigationsServiceAsync rpcService = GWT.create( InvestigationsService.class );
        SummariseInvestigationTable investigateTable = new SummariseInvestigationTable( dock, rpcService );

        investigateTable.fetchInvestigationDetails();
        dock.setCenterWidget( investigateTable );
        dock.showEastWidget();
    }
}
