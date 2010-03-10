package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationView;
import net.sourceforge.symba.web.client.gui.handlers.ListExperimentsClickHandler;

public class InitialDownloadPopup extends PopupPanel {
    public InitialDownloadPopup( SymbaController dock ) {
        super( true ); // set auto-hide property
        setWidget( new ChoicesPanel( dock, this ) );

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
                             InitialDownloadPopup parentPanel ) {
            add( new HTML( "<h2>Would you like to...</h2>" ) );

            Label label = new Label( "...download one or more data files?" );
            label.addStyleName( "clickable-text" );
            label.addStyleName( "pointer-select" );
            label.addClickHandler( new ListExperimentsClickHandler( symba, parentPanel ) );

            add( label );

            HorizontalPanel existing = new HorizontalPanel();
            Label existingLabel = new Label( "...download Investigation metadata?" );
            existing.add( existingLabel );
            SummariseInvestigationView view = new SummariseInvestigationView( symba,
                    SummariseInvestigationView.ViewType.DISPLAY_CHOSEN_METADATA, parentPanel );
            view.setInvestigationDetails( symba.getInvestigationDetails() );
            existing.add( view );

            add( existing );

            add( new Label( "(Click outside the box to cancel and close.)" ) );
        }
    }

}
