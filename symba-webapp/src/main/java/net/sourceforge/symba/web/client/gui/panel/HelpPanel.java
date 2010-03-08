package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HelpPanel extends VerticalPanel {

    private HTML htmlStatus;
    private HTML htmlDirections;

    public HelpPanel() {
        htmlStatus = new HTML();
        htmlDirections = new HTML();

        add( htmlStatus );
        add( htmlDirections );
    }

    public void setStatus( String html ) {

        htmlStatus.setHTML( html );

    }

    public void setDirections( String html ) {

        htmlDirections.setHTML( html );
    }

    public String getStatusMessage() {
        return htmlStatus.getHTML();
    }

    public String getDirections() {
        return htmlDirections.getHTML();
    }
}
