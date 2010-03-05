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

public class SummariseInvestigationTable extends FlexTable {
    private final InvestigationsServiceAsync rpcService;
    private final SymbaControllerPanel symba;

    private final Button copyButton;
    private final Button deleteButton;

    private FlexTable investigationsTable;
    private List<InvestigationDetail> investigationDetails;
    // todo only retrieve contacts less often

    public SummariseInvestigationTable( SymbaControllerPanel symba,
                                        InvestigationsServiceAsync rpcService ) {

        this.rpcService = rpcService;
        this.symba = symba;

        // Create the menu
        //
        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setBorderWidth( 0 );
        menuPanel.setSpacing( 0 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        copyButton = new Button( "Copy" );
        deleteButton = new Button( "Delete" );
        menuPanel.add( copyButton );
        menuPanel.add( deleteButton );
        setWidget( 0, 0, menuPanel );

        // Create the investigation list
        //
        investigationsTable = new FlexTable();
        investigationsTable.setCellSpacing( 0 );
        investigationsTable.setCellPadding( 0 );
        investigationsTable.setWidth( "100%" );
        setWidget( 1, 0, investigationsTable );

        addDefaultHandlers();
    }

    public void addDefaultHandlers() {

        // todo only enabled and visible for Admins (once login enabled)
        deleteButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                deleteSelectedInvestigation();
            }
        } );

        copyButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                copySelectedInvestigation();
            }
        } );

        investigationsTable.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                int selectedRow = getClickedRow( event );

                if ( selectedRow >= 0 ) {
                    String id = investigationDetails.get( selectedRow ).getId();
                    symba.setCenterWidgetAsEditExperiment();
                    ( ( EditInvestigationTable ) symba.getCenterWidget() ).displayInvestigation( id );
                }
            }
        } );

    }

    public void fetchInvestigationDetails() {
        rpcService.getInvestigationDetails( new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> result ) {
                investigationDetails = result;
                sortInvestigationDetails();
                setViewData();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error fetching investigation list: " + caught.getMessage() );
            }
        } );
    }

    private void copySelectedInvestigation() {

        // duplicate the single selected investigation, then go to the edit view.
        int selectedRow = getSelectedRow();

        if ( selectedRow == -1 ) {
            Window.alert( "You must select exactly one investigation to copy." );
            return;
        }

        rpcService.copyInvestigation( investigationDetails.get( selectedRow ).getId(),
                new AsyncCallback<InvestigationDetail>() {
                    public void onSuccess( InvestigationDetail result ) {
                        investigationDetails.add( result );
                        sortInvestigationDetails();
                        setViewData();
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

    public void sortInvestigationDetails() {

        for ( int i = 0; i < investigationDetails.size(); ++i ) {
            for ( int j = 0; j < investigationDetails.size() - 1; ++j ) {
                if ( investigationDetails.get( j ).getInvestigationTitle()
                        .compareToIgnoreCase( investigationDetails.get( j + 1 ).getInvestigationTitle() ) >= 0 ) {
                    InvestigationDetail tmp = investigationDetails.get( j );
                    investigationDetails.set( j, investigationDetails.get( j + 1 ) );
                    investigationDetails.set( j + 1, tmp );
                }
            }
        }
    }

    public void setViewData() {
        investigationsTable.removeAllRows();

        for ( int i = 0; i < investigationDetails.size(); ++i ) {
            investigationsTable.setWidget( i, 0, new RadioButton( "investigationListing" ) );
            investigationsTable.setWidget( i, 1, investigationDetails.get( i ).summarise() );
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

    public void setInvestigationDetails( List<InvestigationDetail> investigationDetails ) {
        this.investigationDetails = investigationDetails;
    }
}
