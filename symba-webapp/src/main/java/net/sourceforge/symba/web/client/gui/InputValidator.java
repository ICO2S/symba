package net.sourceforge.symba.web.client.gui;

import com.google.gwt.user.client.ui.TextBox;

public class InputValidator {

    /**
     * Use this method if you want a basic check on the validity of the text box.
     *
     * @param box the box to check
     */
    public static void nonEmptyTextBoxStyle( TextBox box ) {
        if ( box.getText().trim().length() == 0 ) {
            setWarning( box );
        } else {
            setAccepted( box );
        }
    }

    /**
     * Use this method if you are sure that your text box is wrong
     *
     * @param box the box to check
     */
    public static void setWarning( TextBox box ) {
        box.removeStyleName( "textbox-accepted" );
        box.addStyleName( "textbox-warning" );

    }

    /**
     * Use this method if you are sure that your text box is right
     *
     * @param box the box to check
     */
    public static void setAccepted( TextBox box ) {
        box.removeStyleName( "textbox-warning" );
        box.addStyleName( "textbox-accepted" );

    }
}
