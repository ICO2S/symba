package net.sourceforge.symba.web.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetails;

import java.util.ArrayList;
import java.util.List;

public class EditInvestigationTable extends FlexTable {

    private final InvestigationsServiceAsync rpcService;
    private final SummariseInvestigationPanel investigatePanel;

    private final TextBox investigationId;
    private final TextBox investigationTitle;
    private final TextBox providerId;
    private final TextBox firstName;
    private final TextBox lastName;
    private final TextBox emailAddress;
    private final Button saveButton;
    private final Button cancelButton;
    private final Button addSubStepButton;
    private final String addChildImageUrl = "/images/addChild30x30.png";
    private final String copyStepImageUrl = "/images/copyStep30x15.png";

    private final Button saveInfoButton;

    private Investigation investigation;
    private FlexTable detailsTable;
    private FlexTable stepsTable;
    private TextBox stepTitle;
    private int editableRow, editableColumn, contentTableRowCount;
    private String editableTitle;

    private enum ActionType {
        ADD, COPY, EDIT, IGNORE
    }

    public EditInvestigationTable( InvestigationsServiceAsync rpcService,
                                   SummariseInvestigationPanel investigatePanel ) {
        this.rpcService = rpcService;
        this.investigatePanel = investigatePanel;

        saveInfoButton = new Button( "Save" );
        saveButton = new Button( "Save" );
        cancelButton = new Button( "Cancel" );
        addSubStepButton = new Button( "Add Top-Level Step" );

        investigationTitle = new TextBox();
        investigationId = new TextBox();
        providerId = new TextBox();
        firstName = new TextBox();
        lastName = new TextBox();
        emailAddress = new TextBox();

        initModifiable();
    }

    private void initModifiable() {
        investigation = new Investigation();
        contentTableRowCount = 0;

        detailsTable = new FlexTable();
        stepsTable = new FlexTable();
        removeAllRows();

    }

    public void initEditInvestigationTable() {
        investigation = new Investigation();

        setWidth( "100%" );
//            getCellFormatter().addStyleName( 0, 0, "investigation-ListContainer" );
        getCellFormatter().setWidth( 0, 0, "100%" );
        getFlexCellFormatter().setVerticalAlignment( 0, 0, DockPanel.ALIGN_TOP );

        // the saveInfoButton is not always present, and neither is its associated TextBox.
        stepTitle = null;
        editableRow = -1;
        editableColumn = -1;
        editableTitle = "";

        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setBorderWidth( 0 );
        menuPanel.setSpacing( 0 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        menuPanel.add( saveButton );
        menuPanel.add( cancelButton );
//            getCellFormatter().addStyleName( 0, 0, "contacts-ListMenu" );
        setWidget( contentTableRowCount++, 0, menuPanel );

        // Create the investigation summary view
        detailsTable.setCellSpacing( 0 );
        detailsTable.setCellPadding( 0 );
        detailsTable.setWidth( "100%" );
        detailsTable.getColumnFormatter().addStyleName( 1, "add-contact-input" );
        initDetailsTable();
        detailsTable.getColumnFormatter().setWidth( 0, "15px" );
        setWidget( contentTableRowCount++, 0, detailsTable );

        HorizontalPanel addStepPanel = new HorizontalPanel();
        addStepPanel.setBorderWidth( 0 );
        addStepPanel.setSpacing( 0 );
        addStepPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        addStepPanel.add( addSubStepButton );
//            getCellFormatter().addStyleName( 2, 0, "contacts-ListMenu" );
        setWidget( contentTableRowCount++, 0, addStepPanel );

        stepsTable.setCellSpacing( 10 );
        stepsTable.setCellPadding( 0 );
//        stepsTable.setWidth( "100%" );
        stepsTable.getColumnFormatter().setWidth( 0, "15px" );
        setWidget( contentTableRowCount++, 0, stepsTable );

        addDefaultHandlers();
    }

    public void setInvestigationTable( String id ) {

        rpcService.getInvestigation( id, new AsyncCallback<Investigation>() {
            public void onSuccess( Investigation result ) {
                investigation = result;
                investigationId.setValue( investigation.getId() );
                investigationTitle.setValue( investigation.getInvestigationTitle() );
                providerId.setValue( investigation.getProvider().getId() );
                firstName.setValue( investigation.getProvider().getFirstName() );
                lastName.setValue( investigation.getProvider().getLastName() );
                emailAddress.setValue( investigation.getProvider().getEmailAddress() );

                fetchExperimentStepDetails();

                initEditInvestigationTable();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error retrieving investigation" );
            }
        } );

    }

    private void addDefaultHandlers() {
        saveButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSave();
            }
        } );

        addSubStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doAddStep( -2 ); // force a top-level add of an experiment step
            }
        } );

        cancelButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                initModifiable();
            }
        } );

        saveInfoButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSaveStepInformation( getClickedCoordinatesForSave( event ) );
            }
        } );

        stepsTable.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                ActionType type = getClickActionType( event );
                if ( type == ActionType.ADD ) {
                    doAddStep( getClickedRowForSubStepAddition( event ) );
                } else if ( type == ActionType.COPY ) {
                    doCopyStep( getClickedRowForStepCopying( event ) );
                } else if ( type == ActionType.EDIT ) {
                    doMakeEditable( getClickedCoordinatesForEdit( event ) );
                }
            }
        } );

    }

    private void doSaveStepInformation( int[] coordinates ) {
        // do nothing if there is no useful information in the currently editable panel, e.g. there is no such panel
        // at present.
        if ( coordinates[0] == -2 || coordinates[1] == -2 ) {
            return;
        }

        String value = investigation
                .setExperimentStepTitle( coordinates[0], getActiveText() );
        setReadOnly( coordinates[0], coordinates[1], value );
    }

    private void doAddStep( int selectedRow ) {
        //todo allow multiple addition of steps
        if ( selectedRow >= 0 ) {
            investigation.addExperimentStep( selectedRow );
            setData( investigation.getExperiments() );
        } else if ( selectedRow == -2 ) {
            investigation.addExperimentStep();
            setData( investigation.getExperiments() );
        } // do nothing if -1 : we should ignore such clicks.
    }

    private void doCopyStep( int selectedRow ) {
        if ( selectedRow >= 0 ) {
            investigation.deepExperimentCopy( selectedRow );
            setData( investigation.getExperiments() );
        } // do nothing if -1 or -2 : we should ignore such clicks.

    }

    private void doMakeEditable( int[] coordinates ) {
        if ( coordinates.length == 2 ) {
            setEditable( coordinates[0], coordinates[1] );
        }
    }

    private void doSave() {
        investigation.setId( investigationId.getValue() );
        investigation.setInvestigationTitle( investigationTitle.getValue() );
        investigation.getProvider().setId( providerId.getValue() );
        investigation.getProvider().setFirstName( firstName.getValue() );
        investigation.getProvider().setLastName( lastName.getValue() );
        investigation.getProvider().setEmailAddress( emailAddress.getValue() );

        // the experiment steps were saved as we went along, so nothing extra to do here.

        rpcService.updateInvestigation( investigation, new AsyncCallback<ArrayList<InvestigationDetails>>() {
            public void onSuccess( ArrayList<InvestigationDetails> updatedDetails ) {
                initModifiable();
                investigatePanel.setInvestigationDetails( updatedDetails );
                investigatePanel.sortInvestigationDetails();
                investigatePanel.setViewData();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error updating investigation" );
                caught.printStackTrace( System.err );
            }
        } );
    }

    private void initDetailsTable() {
        detailsTable.setWidget( 0, 0, new Label( "Investigation ID (temp):" ) );
        detailsTable.setWidget( 0, 1, investigationId );
        detailsTable.setWidget( 1, 0, new Label( "Investigation Title" ) );
        detailsTable.setWidget( 1, 1, investigationTitle );
        detailsTable.setWidget( 2, 0, new Label( "Provider ID (temp)" ) );
        detailsTable.setWidget( 2, 1, providerId );
        detailsTable.setWidget( 3, 0, new Label( "First Name" ) );
        detailsTable.setWidget( 3, 1, firstName );
        detailsTable.setWidget( 4, 0, new Label( "Last Name" ) );
        detailsTable.setWidget( 4, 1, lastName );
        detailsTable.setWidget( 5, 0, new Label( "Email Address" ) );
        detailsTable.setWidget( 5, 1, emailAddress );
        firstName.setFocus( true );
    }

    private void fetchExperimentStepDetails() {
        //todo replace with RPC call that will retrieve details. For now, all details start as empty.

        setData( investigation.getExperiments() );
    }

    public void setEditable( final int row,
                             final int column ) {

        // we come to this method if a user has clicked on an experiment step. Prior to this, either there are
        // no previously-editable fields, or there is an editable field that has not yet been reset to read only.
        // use the previously-stored coordinates and set them to read-only.
        if ( hasEditableMetadata() ) {
            setReadOnly( editableRow, editableColumn, editableTitle );
        }

        HorizontalPanel experimentDetailPanel = new HorizontalPanel();
        experimentDetailPanel.setBorderWidth( 0 );
        experimentDetailPanel.setSpacing( 0 );
        experimentDetailPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );

        stepTitle = new TextBox();
        stepTitle.setText( stepsTable.getText( row, column ) );
        setEditableMetadata( row, column, stepsTable.getText( row, column ) );
        experimentDetailPanel.add( stepTitle );

        experimentDetailPanel.add( saveInfoButton );

        stepsTable.setWidget( row, column, experimentDetailPanel );

    }

    /**
     * We are not doing any application logic - we're simply taking the value from our data as provided
     * by the presenter (that's what currently saved) and putting it into a read-only text cell.
     * <p/>
     * This will only be updated if there was a FocusPanel containing the correct Widget to begin with.
     *
     * @param row    the row of the cell being set to read only
     * @param column the row of the cell being set to read only
     * @param title  the string containing the current title.
     */
    public void setReadOnly( int row,
                             int column,
                             String title ) {

        Widget widget = stepsTable.getWidget( row, column );
        if ( isEditable( widget ) ) {
            stepsTable.setText( row, column, title );
            stepTitle = null;
            resetEditableMetadata();
        }

    }

    private boolean isEditable( Widget widget ) {
        return widget instanceof HorizontalPanel && ( ( HorizontalPanel ) widget ).getWidget( 0 ) instanceof TextBox;
    }

    /**
     * Should only be called from the [void setData()] method which implements the Display interface
     *
     * @param data     the thing to display in the table
     * @param rowValue the value being passed through the steps hierarchy
     * @param depth    the hierarchical depth of the current step
     * @return the new value of the row count
     */
    public int setData( List<ExperimentStepHolder> data,
                        Integer rowValue,
                        Integer depth ) {
        for ( ExperimentStepHolder holder : data ) {
//            System.err
//                    .println( "Printing Row " + rowValue + " (" + depth + holder.getCurrent().getTitle() + ")" );
            holder.setStepId( rowValue );
//            stepsTable.setWidget( rowValue, 0, new RadioButton( "stepListing" ) );

            addRowActions( rowValue );

            int i = 0;
            while ( i != depth ) {
                stepsTable.setText( rowValue, i + 2, " " );
                i++;
            }
            stepsTable.setText( rowValue, depth + 2, holder.getCurrent().getTitle() );
            if ( depth == 0 || ( depth % 2 == 0 ) ) {
                stepsTable.getRowFormatter().setStyleName( rowValue, "experiment-EvenStep" );
            } else {
                stepsTable.getRowFormatter().setStyleName( rowValue, "experiment-OddStep" );
            }

            rowValue++;
            if ( !holder.getCurrent().isLeaf() ) {
                rowValue = setData( holder.getCurrent().getChildren(), rowValue, depth + 1 );
            }
        }
        return rowValue;
    }

    private void addRowActions( int rowValue ) {

        Image addChildImage = new Image();
        addChildImage.setUrl( addChildImageUrl );
//        addChildImage.addStyleName( "size30x30" );
        addChildImage.setTitle( "Add Sub-Step" );
        stepsTable.setWidget( rowValue, 0, addChildImage );

        Image copyStepImage = new Image();
        copyStepImage.setUrl( copyStepImageUrl );
//        copyStepImage.addStyleName( "size30x15" );
        copyStepImage.setTitle( "Copy Step" );
        stepsTable.setWidget( rowValue, 1, copyStepImage );
    }

    /**
     * clears the existing display of experiment steps and starts re-writing from the beginning.
     *
     * @param data the data to display
     */
    public void setData( List<ExperimentStepHolder> data ) {
        stepsTable.removeAllRows();
        setData( data, 0, 0 );
    }

    public ActionType getClickActionType( ClickEvent event ) {
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            if ( cell.getCellIndex() == 0 ) {
                // this click is only allowed for additions
                return ActionType.ADD;
            } else if ( cell.getCellIndex() == 1 ) {
                // this click is only allowed for copying a step
                return ActionType.COPY;
            } else {
                // in other positions, the click is for editing or saving a cell.
                if ( !isEditable( stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() ) ) ) {
                    return ActionType.EDIT;
//                } else {
//                    // it is editable - if the click is on the save button, return the save value
//                    Widget widget = stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() );
//                    return ActionType.SAVE;
                }
            }
        }

        return ActionType.IGNORE;
    }

    /**
     * Returns the value of the row that has just been clicked on, as long as it is also the appropriate column for
     * adding a child step.
     *
     * @param event the even that is being caught
     * @return the row number of the clicked row; -1 if outside the appropriate area for clicking (i.e. ignore); -2 if
     *         there is some other error meaning that no cell was actually clicked
     */
    public int getClickedRowForSubStepAddition( ClickEvent event ) {
        int selectedRow = -2;
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() == 0 ) {
                selectedRow = cell.getRowIndex();
            } else {
                // outside the appropriate area for clicking on a whole-row level
                return -1;
            }
        }

        return selectedRow;
    }

    /**
     * Returns the value of the row that has just been clicked on, as long as it is also the appropriate column for
     * copying the current step.
     *
     * @param event the even that is being caught
     * @return the row number of the clicked row; -1 if outside the appropriate area for clicking (i.e. ignore); -2 if
     *         there is some other error meaning that no cell was actually clicked
     */
    public int getClickedRowForStepCopying( ClickEvent event ) {
        int selectedRow = -2;
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() == 1 ) {
                selectedRow = cell.getRowIndex();
            } else {
                // outside the appropriate area for clicking on a whole-row level
                return -1;
            }
        }

        return selectedRow;
    }

    /**
     * Returns the values of the row and column that have just been clicked on, as long as the thing clicked on
     * was the plain text and not an already-editable cell.
     *
     * @param event the even that is being caught
     * @return an int[] of size 2. In position [0], the row number of the clicked cell; in position[1] the column
     *         number of the clicked cell. Values for each will be -1 if outside the appropriate area for clicking
     *         (i.e. ignore); -2 if there is some other error meaning that no cell was actually clicked
     */
    public int[] getClickedCoordinatesForEdit( ClickEvent event ) {
        int selectedRow = -2;
        int selectedColumn = -2;
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() > 1 ) {
                // check that it doesn't already have a text box in it, and therefore doesn't need updating
                if ( !isEditable( stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() ) ) ) {
                    // if it is a cell with some text, then it is a cell with an experiment step that should be
                    // editable. If empty, return the value for ignoring the cell.
                    if ( stepsTable.getText( cell.getRowIndex(), cell.getCellIndex() ).length() == 0 ) {
                        // outside the appropriate area for clicking
                        return new int[]{ -1, -1 };
                    }
                    selectedRow = cell.getRowIndex();
                    selectedColumn = cell.getCellIndex();
                } else {
                    // outside the appropriate area for clicking
                    return new int[]{ -1, -1 };
                }
            } else {
                // outside the appropriate area for clicking
                return new int[]{ -1, -1 };
            }
        }

        return new int[]{ selectedRow, selectedColumn };
    }

    /**
     * Returns the values of the row and column that have just been clicked on, as long as the thing that was
     * clicked on was the saveInfoButton.
     *
     * @param event the even that is being caught
     * @return an int[] of size 2. In position [0], the row number of the clicked cell; in position[1] the column
     *         number of the clicked cell. Values for each will be -1 if outside the appropriate area for clicking
     *         (i.e. ignore); -2 if there is some other error meaning that no cell was actually clicked
     */
    public int[] getClickedCoordinatesForSave( ClickEvent event ) {
        int selectedRow = -2;
        int selectedColumn = -2;
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() > 1 ) {
                // check that it already has a text box in it, and therefore is valid for saving
                if ( isEditable( stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() ) ) ) {
                    // check that it is the saveInfoButton that has been clicked
                    if ( event.getSource() != saveInfoButton ) {
                        // outside the appropriate area for clicking
                        return new int[]{ -1, -1 };
                    }
                    selectedRow = cell.getRowIndex();
                    selectedColumn = cell.getCellIndex();
                } else {
                    // outside the appropriate area for clicking
                    return new int[]{ -1, -1 };
                }
            } else {
                // outside the appropriate area for clicking
                return new int[]{ -1, -1 };
            }
        }

        return new int[]{ selectedRow, selectedColumn };
    }

    public String getActiveText() {
        if ( stepTitle != null ) {
            return stepTitle.getText();
        }

        return "";
    }

    void setEditableMetadata( int row,
                              int column,
                              String title ) {
        editableRow = row;
        editableColumn = column;
        editableTitle = title;
    }

    void resetEditableMetadata() {
        setEditableMetadata( -1, -1, "" );
    }

    boolean hasEditableMetadata() {
        return editableRow != -1 && editableColumn != -1 && editableTitle.length() > 0;
    }

}
