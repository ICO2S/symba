package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.SymbaHeader;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbaControllerPanel extends DockPanel {

    private final InvestigationsServiceAsync rpcService;

    // the type of widget in the non-center panels will not change, though they may not always be visible.
    private final FlexTable southWidget;
    private final HelpPanel eastWidget;
    private final SymbaHeader northWidget;
    private boolean eastSet;

    // Store all current contacts in a central location so all other panels have access to it.
    private HashMap<String, Contact> contacts;

    // Store all current investigations in a central location so all other panels have access to it.
    private ArrayList<InvestigationDetail> investigationDetails;

    // The type of the Widget in the center panel might change
    private Widget centerWidget;

    /**
     * Creates an dock panel with the default layout for the SyMBA pages.
     *
     * @param rpcService the service to use to connect to the data storage
     */
    public SymbaControllerPanel( InvestigationsServiceAsync rpcService ) {

        this.rpcService = rpcService;

        setWidth( "80%" );

        // the center widget starts out as a HomePanel, but will change
        HomePanel home = new HomePanel();
        centerWidget = home;
        northWidget = new SymbaHeader( this, home );

        southWidget = new FlexTable();
        southWidget.addStyleName( "fieldset flash" );

        eastWidget = new HelpPanel();

        add( northWidget, DockPanel.NORTH );
        add( southWidget, DockPanel.SOUTH );
        eastSet = false;

        add( centerWidget, DockPanel.CENTER );

        contacts = new HashMap<String, Contact>();
        rpcService.getAllContacts( new AsyncCallback<HashMap<String, Contact>>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Failed to retrieve up-to-date contacts." );
            }

            public void onSuccess( HashMap<String, Contact> result ) {
                contacts = result;
            }
        } );

        rpcService.getInvestigationDetails( new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> result ) {
                investigationDetails = result;
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error fetching investigation list: " + caught.getMessage() );
            }
        } );

    }

    public Widget getNorthWidget() {
        return northWidget;
    }

    public Widget getSouthWidget() {
        return southWidget;
    }

    public Widget getEastWidget() {
        return eastWidget;
    }

    public Widget getCenterWidget() {
        return centerWidget;
    }

    /**
     * The center part of the dock does not have a remove() method directly accessible. Therefore we can be sure
     * that there will always be something in the center panel. If the widget coming in doesn't match the
     * current widget in the center panel, remove the current center and add the new one.
     *
     * @param widget the widget to add
     */
    public void setCenterWidget( Widget widget ) {
        if ( centerWidget != widget ) {
            remove( centerWidget );
            centerWidget = widget;
            add( widget, DockPanel.CENTER );
        }
    }

    public void setCenterWidgetAsEditExperiment() {
        EditInvestigationTable table = new EditInvestigationTable( this, rpcService, contacts );
        setCenterWidget( table );
        showEastWidget( "", "<em>You can only upload files once you have selected an experimental step." +
                "Do not upload more files until the files you have selected have completed.</em>" );
    }

    public void hideEastWidget() {
        if ( eastSet ) {
            remove( eastWidget );
            eastSet = false;
        }
    }

    /**
     * Adds or updates the east widget. First, you change the status and the directions as appropriate.
     * Then, if the east widget is not yet visible, make it visible.
     *
     * @param htmlStatus the html status to display in the East panel.
     * @param htmlDirections the directions for symba at this point in time.
     */
    public void showEastWidget( String htmlStatus, String htmlDirections ) {
        eastWidget.setStatus( htmlStatus );
        eastWidget.setDirections( htmlDirections );
        if ( !eastSet ) {
            add( eastWidget, DockPanel.EAST );
            // set default east panel width
            eastWidget.setWidth( "20em" );
            eastSet = true;
        }
    }

    /**
     * This helper method allows you to access the status message without having to know what class type
     * is sitting in the East panel.
     * @return the current status message (may be an empty string).
     */
    public String getEastWidgetStatusMessage( ) {
        return eastWidget.getStatusMessage();
    }

    /**
     * This helper method allows you to access the current directions without having to know what class type
     * is sitting in the East panel.
     * @return the current directions (may be an empty string).
     */
    public String getEastWidgetDirections( ) {
        return eastWidget.getDirections();
    }

    public ArrayList<InvestigationDetail> getInvestigationDetails() {
        return investigationDetails;
    }

    public void setInvestigationDetails( ArrayList<InvestigationDetail> investigationDetails ) {
        this.investigationDetails = investigationDetails;
    }

    public InvestigationsServiceAsync getRpcService() {
        return rpcService;
    }
}
