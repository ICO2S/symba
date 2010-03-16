package net.sourceforge.symba.web.client.gui.handlers;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolTip extends PopupPanel {

    public ToolTip( Widget callingWidget,
                    String text ) {

        super( true );
        int left = callingWidget.getAbsoluteLeft() + 10;
        int top = callingWidget.getAbsoluteTop() + 30;
        setPopupPosition( left, top );
        setStyleName( "tooltip-style" );
        add( new HTML( text ) );
    }
}