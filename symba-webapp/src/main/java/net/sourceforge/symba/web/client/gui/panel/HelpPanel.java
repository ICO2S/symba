package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.handlers.ListExperimentsClickHandler;

public class HelpPanel extends VerticalPanel {

    private final HelpStackPanel helpStackPanel;

    public HelpPanel( final SymbaController controller ) {
        super();

        helpStackPanel = new HelpStackPanel( controller );

        setSpacing( 10 );
        setWidth( "100%" );

        Label hideMe = new Label( "[Hide this panel]" );
        hideMe.addStyleName( "clickable-text" );
        hideMe.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                controller.hideEastWidget();
            }
        } );

        add( hideMe );
        add( helpStackPanel );
    }

    public String getDirections() {
        return helpStackPanel.getDirections().getHTML();
    }

    public void setUserStatus( String html ) {
        helpStackPanel.getUserActionStatus().setHTML( html );
        helpStackPanel.showUserActionStatus();
    }

    public void setDirections( String html ) {
        helpStackPanel.getDirections().setHTML( html );
        helpStackPanel.showDirections();
    }

    public void refreshApplicationStatus() {
        helpStackPanel.getApplicationStatus().refreshStatus();
        helpStackPanel.showApplicationStatus();
    }


    /**
     * This class provides a collapsible summary of all of the current help types for SyMBA.
     * userActionStatus: what action the user has recently performed (save/update/cancel etc.).
     * directions: general directions for context-sensitive help.
     * applicationStatus: basic information on the current status of the application.
     */
    private class HelpStackPanel extends StackPanel {
        private HTML userActionStatus;
        private HTML directions;
        private ApplicationStatusPanel applicationStatus;

        private HelpStackPanel( SymbaController controller ) {
            super();

            setStyleName( "stackPanelItem" );
            setWidth( "100%" );

            userActionStatus = new HTML("<p>This is where status messages about your data will be displayed.</p>");
            directions = new HTML( "<p>This is where context-sensitive help messages will be displayed.</p>" );
            applicationStatus = new ApplicationStatusPanel( controller );

            add( userActionStatus, "<font size=\"+1\">Your Status</font>", true ); // index 0
            add( directions, "<font size=\"+1\">Help</font>", true ); // index 1
            add( applicationStatus, "<font size=\"+1\">SyMBA Status</font>", true ); // index 2
            showStack( 2 );
        }

        public HTML getDirections() {
            return directions;
        }

        public void showDirections() {
            showStack( 1 );
        }

        public HTML getUserActionStatus() {
            return userActionStatus;
        }

        public void showUserActionStatus() {
            showStack( 0 );
        }

        public ApplicationStatusPanel getApplicationStatus() {
            return applicationStatus;
        }

        public void showApplicationStatus() {
            showStack( 2 );
        }
    }

    private class ApplicationStatusPanel extends VerticalPanel {

        private final Label numberOfInvestigations;
        private SymbaController controller;

        private ApplicationStatusPanel( SymbaController controller ) {
            super();
            setSpacing( 5 );
            
            this.controller = controller;

            // components
            numberOfInvestigations = new Label();
            Label refreshLabel = new Label("[Refresh SyMBA Status]");

            // styles
            numberOfInvestigations.addStyleName( "clickable-text" );
            refreshLabel.addStyleName( "clickable-text" );

            // handlers
            numberOfInvestigations.addClickHandler( new ListExperimentsClickHandler( controller ) );
            refreshLabel.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    refreshStatus();
                }
            } );

            // positioning
            add( numberOfInvestigations );
            add( refreshLabel );

            refreshStatus();
        }

        private void refreshStatus() {
            // this may be null if the data has not yet been retrieved from the storage medium yet.
            if ( controller.getStoredInvestigationDetails() != null ) {
                if ( controller.getStoredInvestigationDetails().size() == 1 ) {
                    numberOfInvestigations
                            .setText( controller.getStoredInvestigationDetails().size() + " Investigation" );
                } else {
                    numberOfInvestigations
                            .setText( controller.getStoredInvestigationDetails().size() + " Investigations" );
                }
            } else {
                numberOfInvestigations.setText( "Loading Investigation information..." );
            }
        }
    }
}
