package net.sourceforge.symba.web.client.gui;

import com.google.gwt.user.client.ui.TextBox;

import java.io.Serializable;
import java.util.HashMap;

public class InputValidator implements Serializable {
    // todo implement range

    public static enum MeasurementType {
        ATOMIC, BOOLEAN, COMPLEX, RANGE, UNKNOWN
    }

    public static final HashMap<MeasurementType, String> measurementMessages = new HashMap<MeasurementType, String>();

    static {
        measurementMessages.put( MeasurementType.ATOMIC, "Your value is a number" );
        measurementMessages.put( MeasurementType.BOOLEAN, "Your value is true/false" );
        measurementMessages.put( MeasurementType.COMPLEX, "Your value is a word or phrase" );
        measurementMessages.put( MeasurementType.UNKNOWN, "Your value is unknown" );
    }

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

    public static MeasurementType measurementTypeChecker( String input ) {

        input = input.trim();

        if ( input.compareToIgnoreCase( "true" ) == 0 || input.compareToIgnoreCase( "false" ) == 0 ) {
            return MeasurementType.BOOLEAN;
        }

        try {
            Double.parseDouble( input );
            return MeasurementType.ATOMIC;
        } catch ( NumberFormatException e ) {
            // fall through
        }

        // todo check for RANGE

        // return what the type will become if we cannot figure it out
        return MeasurementType.COMPLEX;
    }

}
