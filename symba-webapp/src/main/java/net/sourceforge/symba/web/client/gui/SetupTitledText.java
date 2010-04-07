package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;

public class SetupTitledText {
    public static void set( final HorizontalPanel panel,
                            final TextBoxBase box,
                            String legend,
                            String value,
                            boolean readOnly ) {

        // clear the panel.
        for ( int iii = panel.getWidgetCount(); iii > 0; iii-- ) {
            panel.remove( iii - 1 );
        }

        panel.setSpacing( 5 );
        Label legendLabel = new Label( legend );
        legendLabel.addStyleName( "textbox-legend" );
        panel.add( legendLabel );

        if ( value != null && value.length() > 0 ) {
            final Label label = new Label();
            label.setText( value );
            if ( !readOnly ) {
                label.addStyleName( "clickable-text" );
                label.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent clickEvent ) {
                        panel.remove( 1 ); // remove existing label widget
                        box.setText( label.getText() );
                        panel.add( box ); // add the write widget
                        box.setFocus( true );
                    }
                } );
            }
            panel.add( label );
        } else {
            // there is no value at all yet for the investigation detail. Put in empty box
            box.setText( "" );
            panel.add( box ); // add the write widget
        }
    }
}
