package net.sourceforge.symba.web.client.gui;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.io.Serializable;
import java.util.HashMap;

public class InputValidator implements Serializable {
    // todo implement range

    public static enum MeasurementType {
        ATOMIC, BOOLEAN, COMPLEX, RANGE, UNKNOWN
    }

    public static final String TOP_PROTOCOL = "net.sourceforge.symba.topProtocol";
    public static final String SUBJECT_PREDICATE_DIVIDER = "net.sourceforge.symba.subjectPredicateDivider";
    public static final String COMPLETED_INVESTIGATION = "net.sourceforge.symba.completedInvestigation";
    public static final String TEMPLATE = "net.sourceforge.symba.template";
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

    public static void nonEmptySuggestBoxStyle( SuggestBox box ) {
        if ( box.getText().trim().length() == 0 ) {
            setWarning( box );
        } else {
            setAccepted( box );
        }
    }

    /**
     * Use this method if you are sure that your widget is wrong
     *
     * @param widget the widget that has been checked
     */
    public static void setWarning( Widget widget ) {
        widget.removeStyleName( "textbox-accepted" );
        widget.addStyleName( "textbox-warning" );

    }

    /**
     * Use this method if you are sure that your widget is right
     *
     * @param widget the widget that has been checked
     */
    public static void setAccepted( Widget widget ) {
        widget.removeStyleName( "textbox-warning" );
        widget.addStyleName( "textbox-accepted" );

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
