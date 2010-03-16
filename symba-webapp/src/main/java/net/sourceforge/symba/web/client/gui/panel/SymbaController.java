package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.EditInvestigationView;
import net.sourceforge.symba.web.client.gui.SummariseInvestigationView;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbaController extends DockPanel {

    private static final String DEFAULT_EAST_WIDTH = "20em";
    private final InvestigationsServiceAsync rpcService;

    // the type of widget in the non-center panels will not change, though they may not always be visible.
    private final HelpPanel eastWidget;
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
    public SymbaController( InvestigationsServiceAsync rpcService ) {

        this.rpcService = rpcService;

        setWidth( "100%" );
        setSpacing( 10 );

        // the center widget starts out as a HomePanel, but will change
        HomePanel home = new HomePanel();
        centerWidget = home;
        SymbaHeader northWidget = new SymbaHeader( this, home );
        SymbaFooter southWidget = new SymbaFooter();

        eastWidget = new HelpPanel( this );
        eastWidget.getFileStatus().addStyleName( "fieldset flash" );
        eastSet = false;

        add( northWidget, DockPanel.NORTH );
        add( southWidget, DockPanel.SOUTH );
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
        remove( centerWidget );
        centerWidget = widget;
        add( widget, DockPanel.CENTER );
    }

    public void setCenterWidgetAsEditExperiment() {
        Investigation investigation = new Investigation();
        investigation.createId();
        investigation.getProvider().createId();
        EditInvestigationView view = new EditInvestigationView( this, investigation, rpcService, contacts );
        setCenterWidget( view );
        showEastWidget( "", "<em>You can only upload files once you have selected an experimental step." +
                "Do not upload more files until the files you have selected have completed.</em>" );
    }

    public void setCenterWidgetAsEditExperiment( final String id ) {
        rpcService.getInvestigation( id, new AsyncCallback<Investigation>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Error retrieving investigation " + id );
            }

            public void onSuccess( Investigation result ) {
                EditInvestigationView view = new EditInvestigationView( SymbaController.this, result, rpcService,
                        contacts );
                setCenterWidget( view );
                showEastWidget( "", "<em>You can only upload files once you have selected an experimental step." +
                        "Do not upload more files until the files you have selected have completed.</em>" );
            }
        } );
    }

    public void hideEastWidget() {
        if ( eastSet ) {
            remove( eastWidget );
            eastSet = false;
        }
    }

    /**
     * Unlike the version of showEastWidget() which accepts arguments, this method will assume all values
     * for the components of the east widget are set, and this method simply ensures that the widget is visible.
     */
    public void showEastWidget() {
        if ( !eastSet ) {
            add( eastWidget, DockPanel.EAST );
            // set default east panel width
            eastWidget.setWidth( DEFAULT_EAST_WIDTH );
            eastSet = true;
        }
    }

    /**
     * Adds or updates the east widget. First, you change the values within HelpPanel as appropriate.
     * Then, if the east widget is not yet visible, make it visible.
     * <p/>
     * Please note that this is nothing more than a convenience method for the simple parts of the east
     * widget. The fileStatus part of the current east widget is a flex table, and therefore, this method
     * ignores that part. Access the FlexTable's methods directly via getEastWidget().getFileStatus()
     *
     * @param htmlStatus     the html status to display in the East panel.
     * @param htmlDirections the directions for symba at this point in time.
     */
    public void showEastWidget( String htmlStatus,
                                String htmlDirections ) {
        eastWidget.setStatus( htmlStatus );
        eastWidget.setDirections( htmlDirections );
        showEastWidget();
    }

    /**
     * This helper method allows you to access the current directions without having to know what class type
     * is sitting in the East panel.
     *
     * @return the current directions (may be an empty string).
     */
    public String getEastWidgetDirections() {
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

    public void setCenterWidgetAsListExperiments() {
        SummariseInvestigationView investigateView = new SummariseInvestigationView( this,
                SummariseInvestigationView.ViewType.EXTENDED );
        investigateView.setInvestigationDetails( getInvestigationDetails() );
        setCenterWidget( investigateView );

    }
}
