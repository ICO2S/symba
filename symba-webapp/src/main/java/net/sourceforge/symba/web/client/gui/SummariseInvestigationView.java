package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.panel.SymbaControllerPanel;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;
import java.util.List;

public class SummariseInvestigationView extends FlexTable {
    private final InvestigationsServiceAsync rpcService;
    private final SymbaControllerPanel symba;
    private PopupPanel popup = null;

    private final boolean copyOnly;

    private final Button copyButton;
    private final Button deleteButton;

    private FlexTable investigationsTable;
    private ListBox investigationsListBox;
    private List<InvestigationDetail> investigationDetails;
    // todo only retrieve contacts less often

    /**
     * If a popup is passed, then we should also hide the popup on completion of the click handling.
     *
     * @param symba      the controller panel
     * @param rpcService the service to connect to the data storage medium
     * @param copyOnly   whether or not to present the copy-only view
     * @param popup      the popup to hide
     */
    public SummariseInvestigationView( SymbaControllerPanel symba,
                                       InvestigationsServiceAsync rpcService,
                                       boolean copyOnly,
                                       PopupPanel popup ) {
        this( symba, rpcService, copyOnly );
        this.popup = popup;
    }

    public SummariseInvestigationView( SymbaControllerPanel symba,
                                       InvestigationsServiceAsync rpcService,
                                       boolean copyOnly ) {
        this.rpcService = rpcService;
        this.symba = symba;
        this.copyOnly = copyOnly;
        copyButton = new Button( "Copy" );
        deleteButton = new Button( "Delete" );

        investigationsTable = new FlexTable();
        investigationsListBox = new ListBox();

        if ( copyOnly ) {
            makeListBox();
        } else {
            makeExpandedTable();
        }
    }

    private void makeListBox() {

        setWidget( 0, 0, investigationsListBox );

        // force the user to click the copy button in order to display the new investigation. This will reduce
        // the number of unnecessary copies being made.
        Button chooseButton = new Button( "Copy Chosen Investigation" );
        setWidget( 0, 1, chooseButton );
        chooseButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                int selected = investigationsListBox.getSelectedIndex();
                if ( selected == -1 ) {
                    selected = 0; // choose the first in the list if none have been selected yet
                }
                copyInvestigation( investigationsListBox.getValue( selected ) );
            }
        } );
    }

    private void makeExpandedTable() {

        // Display the menu
        //
        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setBorderWidth( 0 );
        menuPanel.setSpacing( 0 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        menuPanel.add( copyButton );
        menuPanel.add( deleteButton );
        setWidget( 0, 0, menuPanel );

        // Create the investigation list
        //
        investigationsTable.setCellSpacing( 0 );
        investigationsTable.setCellPadding( 0 );
        investigationsTable.setWidth( "100%" );
        setWidget( 1, 0, investigationsTable );

        addExpandedHandlers();

    }

    public void addExpandedHandlers() {

        // todo only enabled and visible for Admins (once login enabled)
        deleteButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                deleteSelectedInvestigation();
            }
        } );

        copyButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                int selectedRow = getSelectedRow();

                if ( selectedRow == -1 ) {
                    Window.alert( "You must select exactly one investigation to copy." );
                    return;
                }

                copyInvestigation( investigationDetails.get( selectedRow ).getId() );
            }
        } );

        investigationsTable.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                int selectedRow = getClickedRow( event );

                // why copy here?
                if ( selectedRow >= 0 ) {
                    String id = investigationDetails.get( selectedRow ).getId();
                    symba.setCenterWidgetAsEditExperiment();
                    ( ( EditInvestigationTable ) symba.getCenterWidget() ).displayInvestigation( id );
                }
            }
        } );

    }

    private void copyInvestigation( final String id ) {

        // duplicate the single selected investigation
        rpcService.copyInvestigation( id,
                new AsyncCallback<InvestigationDetail>() {
                    public void onSuccess( InvestigationDetail result ) {
                        investigationDetails.add( result );
                        // refresh the view of the list of details
                        sortInvestigationDetails();
                        setViewData();
                        // change the main display to the newly-copied investigation if we are in the
                        // minimal copy-only view
                        if ( copyOnly ) {
                            symba.setCenterWidgetAsEditExperiment();
                            ( ( EditInvestigationTable ) symba.getCenterWidget() )
                                    .displayInvestigation( result.getId() );
                        }
                        // if a popup was passed, then hide that popup upon successful completion.
                        if ( popup != null ) {
                            popup.hide();
                        }
                    }

                    public void onFailure( Throwable caught ) {
                        Window.alert( "Error copying investigation" );
                    }
                } );
    }

    private void deleteSelectedInvestigation() {
        int selectedRow = getSelectedRow();
        String id = investigationDetails.get( selectedRow ).getId();

        rpcService.deleteInvestigation( id, new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> result ) {
                investigationDetails = result;
                sortInvestigationDetails();
                setViewData();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error deleting selected investigations" );
            }
        } );
    }

    /**
     * Non-templates should be shown first, and then templates.
     * Within each grouping, sort alphabetically by contact surname.
     * Within each contact surname, sort alphabetically on investigation title.
     */
    public void sortInvestigationDetails() {

        // sort on templates
        for ( int i = 0; i < investigationDetails.size(); ++i ) {
            for ( int j = 0; j < investigationDetails.size() - 1; ++j ) {
                if ( !investigationDetails.get( j ).isTemplate() ) {
                    InvestigationDetail tmp = investigationDetails.get( j );
                    investigationDetails.set( j, investigationDetails.get( j + 1 ) );
                    investigationDetails.set( j + 1, tmp );
                }
            }
        }
        // sort by surname
        for ( int i = 0; i < investigationDetails.size(); ++i ) {
            for ( int j = 0; j < investigationDetails.size() - 1; ++j ) {
                if ( investigationDetails.get( j ).getProvider().getLastName()
                        .compareToIgnoreCase( investigationDetails.get( j + 1 ).getProvider().getLastName() ) >= 0 &&
                        investigationDetails.get( j ).isTemplate() == investigationDetails.get( j + 1 ).isTemplate() ) {
                    InvestigationDetail tmp = investigationDetails.get( j );
                    investigationDetails.set( j, investigationDetails.get( j + 1 ) );
                    investigationDetails.set( j + 1, tmp );
                }
            }
        }
        // sort by investigation title
        for ( int i = 0; i < investigationDetails.size(); ++i ) {
            for ( int j = 0; j < investigationDetails.size() - 1; ++j ) {
                if ( investigationDetails.get( j ).getInvestigationTitle()
                        .compareToIgnoreCase( investigationDetails.get( j + 1 ).getInvestigationTitle() ) >= 0 &&
                        investigationDetails.get( j ).getProvider().getLastName()
                                .equals( investigationDetails.get( j + 1 ).getProvider().getLastName() ) ) {
                    InvestigationDetail tmp = investigationDetails.get( j );
                    investigationDetails.set( j, investigationDetails.get( j + 1 ) );
                    investigationDetails.set( j + 1, tmp );
                }
            }
        }
    }

    public void setViewData() {

        if ( copyOnly ) {
            // remove existing entries
            for ( int iii = 0; iii < investigationsListBox.getItemCount(); iii++ ) {
                investigationsListBox.removeItem( iii );
            }
            // add new entries
            for ( InvestigationDetail detail : investigationDetails ) {
                investigationsListBox.addItem( detail.summarise().getHTML(), detail.getId() );
            }

        } else {
            investigationsTable.removeAllRows();

            for ( int i = 0; i < investigationDetails.size(); ++i ) {
                investigationsTable.setWidget( i, 0, new RadioButton( "investigationListing" ) );
                investigationsTable.setWidget( i, 1, investigationDetails.get( i ).summarise() );
            }
        }
    }

    public int getClickedRow( ClickEvent event ) {
        int selectedRow = -1;
        HTMLTable.Cell cell = investigationsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is actually selecting the
            //  check box
            //
            if ( cell.getCellIndex() > 0 ) {
                selectedRow = cell.getRowIndex();
            }
        }

        return selectedRow;
    }

    public int getSelectedRow() {

        for ( int i = 0; i < investigationsTable.getRowCount(); ++i ) {
            RadioButton radioButton = ( RadioButton ) investigationsTable.getWidget( i, 0 );
            if ( radioButton.getValue() ) {
                return i;
            }
        }

        return -1;
    }

    public void setInvestigationDetails( ArrayList<InvestigationDetail> investigationDetails ) {
        this.investigationDetails = investigationDetails;
        symba.setInvestigationDetails( investigationDetails );
        sortInvestigationDetails();
        setViewData();
    }
}
