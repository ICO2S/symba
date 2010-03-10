package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HelpPanel extends VerticalPanel {

    private HTML status;
    private HTML directions;
    private FlexTable fileStatus;

    public HelpPanel() {
        status = new HTML();
        directions = new HTML();
        fileStatus = new FlexTable();

        add( status );
        add( directions );
        add( fileStatus );
    }

    public String getDirections() {
        return directions.getHTML();
    }

    public FlexTable getFileStatus() {
        return fileStatus;
    }

    public void setStatus( String html ) {
        status.setHTML( html );
    }

    public void setDirections( String html ) {
        directions.setHTML( html );
    }
}
