package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationView;
import net.sourceforge.symba.web.client.gui.handlers.ListExperimentsClickHandler;

public class NewInvestigationPopup extends PopupPanel {

    public NewInvestigationPopup( final SymbaController controller ) {
        super( true ); // set auto-hide property
        setWidget( new ChoicesPanel( controller, this ) );

        // set the position to the center of the window
        setPopupPositionAndShow( new PopupPanel.PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                int left = ( Window.getClientWidth() - offsetWidth ) / 2;
                int top = ( Window.getClientHeight() - offsetHeight ) / 2;
                setPopupPosition( left, top );
            }
        } );
    }

    private class ChoicesPanel extends VerticalPanel {
        public ChoicesPanel( final SymbaController symba,
                             NewInvestigationPopup parentPanel ) {
            Label label = new Label( "Add New" );
            label.addStyleName( "clickable-text" );
            label.addStyleName( "pointer-select" );
            label.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // They've requested a completely new experiment
                    symba.setCenterWidgetAsEditExperiment();
                    hide();
                }
            } );

            add( label );

            SummariseInvestigationView view = new SummariseInvestigationView( symba,
                    SummariseInvestigationView.ViewType.COPY_CHOSEN, parentPanel );
            view.setInvestigationDetails( symba.getStoredInvestigationDetails() );

            add( view );

            Label moreInfo = new Label(
                    "View Investigations" );
            moreInfo.addStyleName( "clickable-text" );
            moreInfo.addClickHandler( new ListExperimentsClickHandler( symba, parentPanel ) );

            add( moreInfo );
        }
    }
}
