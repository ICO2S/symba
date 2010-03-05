package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.EditInvestigationTable;
import net.sourceforge.symba.web.client.gui.SymbaHeader;
import net.sourceforge.symba.web.shared.Contact;

import java.util.HashMap;

public class SymbaControllerPanel extends DockPanel {

    private final InvestigationsServiceAsync rpcService;

    // the type of widget in the non-center panels will not change, though they may not always be visible.
    private final FlexTable southWidget;
    private final HTML eastWidget;
    private final SymbaHeader northWidget;
    private boolean eastSet;

    // Store all current contacts in a central location so all other panels have access to it.
    private HashMap<String, Contact> contacts;

    // The type of the Widget in the center panel might change
    private Widget centerWidget;

    /**
     * Creates an dock panel with the default layout for the SyMBA pages.
     *
     * @param rpcService           the service to use to connect to the data storage
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

        eastWidget = new HTML( "<em>You can only upload files once you have selected an experimental step." +
                "Do not upload more files until the files you have selected have completed.</em>" );

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
    }

    public void hideEastWidget() {
        if ( eastSet ) {
            remove( eastWidget );
            eastSet = false;
        }
    }

    public void showEastWidget() {
        if ( !eastSet ) {
            add( eastWidget, DockPanel.EAST );
            // set default east panel width
            eastWidget.setWidth( "20em" );
        }
        eastSet = true;
    }
}
