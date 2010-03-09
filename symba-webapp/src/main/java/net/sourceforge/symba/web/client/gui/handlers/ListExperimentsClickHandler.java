package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import net.sourceforge.symba.web.client.InvestigationsService;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationView;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;

public class ListExperimentsClickHandler implements ClickHandler {
    private final SymbaControllerPanel symba;
    private final PopupPanel popup;

    public ListExperimentsClickHandler( SymbaControllerPanel symba ) {
        this.symba = symba;
        this.popup = null;
    }

    /**
     * If a popup is passed, then we should also hide the popup on completion of the click handling.
     *
     * @param symba the controller panel
     * @param popup the popup to hide
     */
    public ListExperimentsClickHandler( SymbaControllerPanel symba,
                                        PopupPanel popup ) {
        this.symba = symba;
        this.popup = popup;
    }

    public void onClick( ClickEvent event ) {
        SummariseInvestigationView investigateView = new SummariseInvestigationView( symba, symba.getRpcService(),
                false );
        investigateView.setInvestigationDetails( symba.getInvestigationDetails() );
        symba.setCenterWidget( investigateView );
        symba.hideEastWidget();
        if ( popup != null ) {
            popup.hide();
        }

    }
}
