package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationView;
import net.sourceforge.symba.web.client.gui.panel.SymbaController;

public class ListExperimentsClickHandler extends ActivateableClickHandler {
    private final SymbaController controller;
    private final PopupPanel popup;

    public ListExperimentsClickHandler( SymbaController controller ) {
        super();
        this.controller = controller;
        this.popup = null;
    }

    /**
     * If a popup is passed, then we should also hide the popup on completion of the click handling.
     *
     * @param controller the controller panel
     * @param popup      the popup to hide
     */
    public ListExperimentsClickHandler( SymbaController controller,
                                        PopupPanel popup ) {
        super();
        this.controller = controller;
        this.popup = popup;
    }

    @Override
    protected boolean runClickMethod( ClickEvent event ) {
        SummariseInvestigationView investigateView = new SummariseInvestigationView( controller,
                SummariseInvestigationView.ViewType.EXTENDED );
        investigateView.setInvestigationDetails( controller.getStoredInvestigationDetails() );
        controller.setCenterWidget( investigateView );
        if ( popup != null ) {
            popup.hide();
        }
        return true;
    }
}
