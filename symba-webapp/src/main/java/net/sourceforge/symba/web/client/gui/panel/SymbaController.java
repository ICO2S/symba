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
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class SymbaController extends DockPanel {

    private static final String DEFAULT_EAST_WIDTH = "33%";
    private final InvestigationsServiceAsync rpcService;

    // the type of widget in the non-center panels will not change, though they may not always be visible.
    private final HelpPanel eastWidget;
    private boolean eastSet;
    
    private final SymbaHeader northWidget;
    private final HomePanel homePanel;

    // Who the user is logged in as
    private Contact user;

    // Store all current storedContacts in a central location so all panels have access to it.
    private HashMap<String, Contact> storedContacts;

    // Store all current storedContacts in a central location so all panels have access to it.
    private HashMap<String, Material> storedMaterials;

    // Store all current investigations in a central location so all panels have access to it.
    private ArrayList<InvestigationDetail> storedInvestigationDetails;

    // Store all current parameter subjects in a central location so all panels have access to it.
    private HashSet<String> storedParameterSubjects;

    // The type of the Widget in the center panel might change
    private Widget centerWidget;
    private static final String BASIC_EDITING_HELP = "<p>Upload files by clicking on the experimental step you wish" +
            " to associate with those files. You can upload multiple files in sequence by clicking on the file " +
            "\"Browse\" button multiple times.</p>";

    /**
     * Creates an dock panel with the default layout for the SyMBA pages.
     *
     * @param rpcService the service to use to connect to the data storage
     */
    public SymbaController( InvestigationsServiceAsync rpcService ) {

        this.rpcService = rpcService;

        updateStoredContacts();
        updateStoredMaterials();
        updateStoredInvestigationDetails();
        updateStoredParameterSubjects();

        setWidth( "100%" );
        setSpacing( 10 );

        user = new Contact();

        // the center widget starts out as a HomePanel, but will change
        homePanel = new HomePanel(this);
        centerWidget = homePanel;
        northWidget = new SymbaHeader( this, homePanel );
        SymbaFooter southWidget = new SymbaFooter();

        eastWidget = new HelpPanel( this );
        eastSet = false;

        add( northWidget, DockPanel.NORTH );
        add( southWidget, DockPanel.SOUTH );
        add( centerWidget, DockPanel.CENTER );
        centerWidget.addStyleName( "center-style" );
        showEastWidget();
    }

    public Contact getUser() {
        return user;
    }

    /**
     * Set the user for this session to be the one provided as an argument
     * @param user the user logged into this session
     */
    public void setUser( Contact user ) {
        this.user = user;
        northWidget.enableActions();
        eastWidget.enableActions();
    }

    /**
     * Removes any current user assigned to this session
     */
    public void unsetUser() {
        this.user = new Contact();
        northWidget.disableActions();
        eastWidget.disableActions();
        homePanel.getLogin().populateNameListBox();
        homePanel.getLogin().setupNameDetailPanel( "", "", false );
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
        EditInvestigationView view = new EditInvestigationView( this, investigation,
                EditInvestigationView.ViewType.NEW_INVESTIGATION );
        setCenterWidget( view );
        showEastWidget( "", BASIC_EDITING_HELP );
    }

    public void setCenterWidgetAsEditExperiment( final String id ) {
        rpcService.getInvestigation( id, new AsyncCallback<Investigation>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Error retrieving investigation " + id );
            }

            public void onSuccess( Investigation result ) {
                EditInvestigationView view = new EditInvestigationView( SymbaController.this, result,
                        EditInvestigationView.ViewType.EXISTING_INVESTIGATION );
                setCenterWidget( view );
                showEastWidget( "<p>Display of Investigation <strong>" + result.getInvestigationTitle() +
                        "</strong> complete.</p>", BASIC_EDITING_HELP );
            }
        } );
    }

    public void showSymbaStatus() {
        eastWidget.refreshApplicationStatus();
    }

    public void hideEastWidget() {
        if ( eastSet ) {
            remove( eastWidget );
            eastSet = false;
        }
    }

    /**
     * Unlike the version of showEastWidget() which accepts arguments, this method will assume all values
     * for the components of the east widget are set, and simply ensures that the widget is visible.
     */
    public void showEastWidget() {
        if ( !eastSet ) {
            add( eastWidget, DockPanel.EAST );
            // set default east panel width
            setCellWidth( eastWidget, DEFAULT_EAST_WIDTH );
            eastWidget.addStyleName( "east-style" );
            eastSet = true;
        }
    }

    /**
     * Only changes the setUserStatus() value in the east widget, leaving the directions unchanged.
     *
     * @param htmlStatus the html status to display in the East panel.
     */
    public void setEastWidgetUserStatus( String htmlStatus ) {
        eastWidget.setUserStatus( htmlStatus );
        showEastWidget();
    }

    /**
     * Only changes the setDirections() value in the east widget, leaving the user status unchanged.
     *
     * @param htmlDirections the directions to display in the East panel.
     */
    public void setEastWidgetDirections( String htmlDirections ) {
        eastWidget.setDirections( htmlDirections );
        showEastWidget();
    }

    /**
     * Adds or updates the east widget. First, you change the values within HelpPanel as appropriate.
     * Then, if the east widget is not yet visible, make it visible.
     * <p/>
     * Please note that this is nothing more than a convenience method for the simple parts of the east
     * widget, but not for all parts of that widget.
     *
     * @param htmlStatus     the html status to display in the East panel.
     * @param htmlDirections the directions for symba at this point in time.
     */
    public void showEastWidget( String htmlStatus,
                                String htmlDirections ) {
        // as each set method shows its panel, whichever set method is called last will have its panel opened.
        // Therefore, if either string is empty, set that one first. By default, display the directions last so that
        // they show.
        if ( htmlDirections.length() == 0 ) {
            eastWidget.setDirections( htmlDirections );
            eastWidget.setUserStatus( htmlStatus );
        } else {
            eastWidget.setUserStatus( htmlStatus );
            eastWidget.setDirections( htmlDirections );
        }
        showEastWidget();
    }

    public InvestigationsServiceAsync getRpcService() {
        return rpcService;
    }

    public void setCenterWidgetAsListExperiments() {
        SummariseInvestigationView investigateView = new SummariseInvestigationView( this,
                SummariseInvestigationView.ViewType.EXTENDED );
        investigateView.setInvestigationDetails( getStoredInvestigationDetails() );
        setCenterWidget( investigateView );

    }

    public ArrayList<InvestigationDetail> getStoredInvestigationDetails() {
        return storedInvestigationDetails;
    }

    public HashMap<String, Contact> getStoredContacts() {
        return storedContacts;
    }

    public HashMap<String, Material> getStoredMaterials() {
        return storedMaterials;
    }

    public HashSet<String> getStoredParameterSubjects() {
        return storedParameterSubjects;
    }

    private void updateStoredContacts() {

        storedContacts = new HashMap<String, Contact>();
        rpcService.getAllContacts( new AsyncCallback<HashMap<String, Contact>>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Failed to retrieve up-to-date contacts." );
            }

            public void onSuccess( HashMap<String, Contact> result ) {
                storedContacts = result;
                homePanel.getLogin().populateNameListBox();
            }
        } );
    }

    private void updateStoredMaterials() {

        storedMaterials = new HashMap<String, Material>();
        rpcService.getAllMaterials( new AsyncCallback<HashMap<String, Material>>() {
            public void onFailure( Throwable caught ) {
                Window.alert( "Failed to retrieve up-to-date materials." );
            }

            public void onSuccess( HashMap<String, Material> result ) {
                storedMaterials = result;
            }
        } );
    }

    private void updateStoredInvestigationDetails() {
        rpcService.getInvestigationDetails( new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> result ) {
                storedInvestigationDetails = result;
                eastWidget.refreshApplicationStatus();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error fetching investigation list: " + caught.getMessage() +
                        Arrays.toString( caught.getStackTrace() ) );
            }
        } );
    }

    private void updateStoredParameterSubjects() {
        rpcService.getParameterSubjects( new AsyncCallback<HashSet<String>>() {
            public void onSuccess( HashSet<String> result ) {
                storedParameterSubjects = result;
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error fetching parameter subjects: " + caught.getMessage() +
                        Arrays.toString( caught.getStackTrace() ) );
            }
        } );
    }

    public void setStoredMaterials( HashMap<String, Material> materials ) {
        storedMaterials = new HashMap<String, Material>( materials );
    }

    public void setStoredContacts( HashMap<String, Contact> contacts ) {
        storedContacts = new HashMap<String, Contact>( contacts );
    }

    public void setStoredInvestigationDetails( ArrayList<InvestigationDetail> details ) {
        storedInvestigationDetails = new ArrayList<InvestigationDetail>( details );
    }

    public void cancelAll() {
        setCenterWidget( homePanel );
        showEastWidget("", "");
        showSymbaStatus();
    }
}
