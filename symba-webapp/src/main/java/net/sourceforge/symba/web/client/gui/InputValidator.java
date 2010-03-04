package net.sourceforge.symba.web.client.gui;

import com.google.gwt.user.client.ui.TextBox;

public class InputValidator {

    public static void nonEmptyTextBoxStyle( TextBox box ) {
        if ( box.getText().trim().length() == 0 ) {
            box.removeStyleName( "textbox-accepted" );
            box.addStyleName( "textbox-warning" );
        } else {
            box.removeStyleName( "textbox-warning" );
            box.addStyleName( "textbox-accepted" );
        }
    }

}
