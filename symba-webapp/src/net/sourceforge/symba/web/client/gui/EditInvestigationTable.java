package net.sourceforge.symba.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetails;
import org.swfupload.client.SWFUpload;

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
    private final String baseApp = GWT.getModuleBaseURL()
            .substring( 0, GWT.getModuleBaseURL().lastIndexOf( GWT.getModuleName() ) );
    private final String addChildImageUrl = baseApp + "/images/addChild30x30.png";
    private final String copyStepImageUrl = baseApp + "/images/copyStep30x15.png";

    private final SWFUpload buttonOne, buttonTwo;


    private final Button saveInfoButton;

    private Investigation investigation;
    private VerticalPanel detailsPanel;
    private FlexTable stepsTable;
    private TextBox stepTitle;
    private int editableRow, editableColumn, contentTableRowCount;
    private String editableTitle;
    private boolean defaultHandlersSet;

    private enum ActionType {
        ADD( 0 ), COPY( 1 ), SELECT( 2 ), EDIT( 3 ), IGNORE( -1 ), UNDEFINED( -2 );

        private final int value;

        ActionType( int i ) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Initialise all final and modifiable variables
     *
     * @param rpcService       the service to use to call the GWT server side
     * @param investigatePanel the panel that holds the main summaries of all investigations - used to update that panel
     * @param buttonOne        the first button to enable/disable
     * @param buttonTwo        the second button to enable/disable
     */
    public EditInvestigationTable( InvestigationsServiceAsync rpcService,
                                   SummariseInvestigationPanel investigatePanel,
                                   SWFUpload buttonOne,
                                   SWFUpload buttonTwo
    ) {

        // initialise all final variables
        this.rpcService = rpcService;
        this.investigatePanel = investigatePanel;
        this.buttonOne = buttonOne;
        this.buttonTwo = buttonTwo;

        saveInfoButton = new Button( "Save" );
        saveButton = new Button( "Save and Finish" );
        cancelButton = new Button( "Cancel" );
        addSubStepButton = new Button( "Add Top-Level Step" );

        investigationId = new TextBox();
        investigationTitle = new TextBox();
        providerId = new TextBox();
        firstName = new TextBox();
        lastName = new TextBox();
        emailAddress = new TextBox();

        // clear / set to empty all modifiable variables except defaultHandlersSet
        clearModifiable();

        // defaultHandlersSet should only be set to false in the constructor, and then true the first time
        // the handlers are loaded. Other than that, no modifications should be performed on this variable.
        // Therefore, it should not be included in clearModifiable()
        defaultHandlersSet = false;
    }

    private void clearModifiable() {
        investigation = new Investigation();
        contentTableRowCount = 0;

        detailsPanel = new VerticalPanel();
        stepsTable = new FlexTable();

        removeAllRows();

    }

    public void initEditInvestigationTable() {

        clearModifiable();

        investigationTitle.setText( "" );
        investigationId.setText( "" );
        providerId.setText( "" );
        firstName.setText( "" );
        lastName.setText( "" );
        emailAddress.setText( "" );

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
        detailsPanel.setWidth( "100%" );
        initDetailsTable();
        setWidget( contentTableRowCount++, 0, detailsPanel );

        HorizontalPanel addStepPanel = new HorizontalPanel();
        addStepPanel.setBorderWidth( 0 );
        addStepPanel.setSpacing( 0 );
        addStepPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        addStepPanel.add( addSubStepButton );
//            getCellFormatter().addStyleName( 2, 0, "contacts-ListMenu" );
        setWidget( contentTableRowCount++, 0, addStepPanel );

        stepsTable.setCellSpacing( 0 );
        stepsTable.setCellPadding( 0 );
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

//        stepsTable.setWidth( "100%" );
        stepsTable.getColumnFormatter().setWidth( 0, "15px" );
        setWidget( contentTableRowCount++, 0, stepsTable );

        addDefaultHandlers();
    }

    public void set( String id ) {

        rpcService.getInvestigation( id, new AsyncCallback<Investigation>() {
            public void onSuccess( Investigation result ) {

                initEditInvestigationTable();

                investigation = result;
                investigationId.setValue( investigation.getId() );
                investigationTitle.setValue( investigation.getInvestigationTitle() );
                providerId.setValue( investigation.getProvider().getId() );
                firstName.setValue( investigation.getProvider().getFirstName() );
                lastName.setValue( investigation.getProvider().getLastName() );
                emailAddress.setValue( investigation.getProvider().getEmailAddress() );

                fetchExperimentStepDetails();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error retrieving investigation" );
            }
        } );

    }

    private void addDefaultHandlers() {

        if ( defaultHandlersSet ) {
            return;
        } else {
            defaultHandlersSet = true;
        }

        saveButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSave();
            }
        } );

        addSubStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doAddStep( ActionType.UNDEFINED.getValue() ); // force a top-level add of an experiment step
            }
        } );

        cancelButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                buttonOne.setButtonDisabled( true );
                buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                buttonTwo.setButtonDisabled( true );
                buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );

                String cancelledTitle = investigation.getInvestigationTitle();
                clearModifiable();
                setWidget( contentTableRowCount++, 0, new Label( "Did not modify " + cancelledTitle + "." ) );
            }
        } );

        saveInfoButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSaveStepInformation( getClickedCoordinatesForSave( event ) );
            }
        } );

        investigationId.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( investigationId );
            }
        } );

        investigationTitle.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( investigationTitle );
            }
        } );

        providerId.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( providerId );
            }
        } );

        firstName.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( firstName );
            }
        } );

        lastName.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( lastName );
            }
        } );

        emailAddress.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent blurEvent ) {
                checkEmptyValue( emailAddress );
            }
        } );

    }

    private void checkEmptyValue( TextBox box ) {
        System.err.println( "inv id blur event" );
        if ( box.getValue().length() == 0 ) {
            box.addStyleName( "textbox-warning" );
        } else {
            box.addStyleName( "textbox-accepted" );
        }

    }

    private void doSaveStepInformation( int[] coordinates ) {
        // do nothing if there is no useful information in the currently editable panel, e.g. there is no such panel
        // at present.
        if ( coordinates[0] == ActionType.UNDEFINED.getValue() || coordinates[1] == ActionType.UNDEFINED.getValue() ) {
            return;
        }

        Object[] values = investigation
                .setExperimentStepTitle( coordinates[0], getActiveText() );

        // Set style based on change, then send the title to setReadOnly.
        String title = "";
        Boolean modified = false;
        if ( values != null && values[0] != null && ( ( String ) values[0] ).length() > 0 ) {
            title = ( String ) values[0];
            modified = ( Boolean ) values[1];
        }
        // todo this style is currently only be applied to one cell until the entire table is redrawn, e.g. from an added step
        if ( modified ) {
            stepsTable.getCellFormatter().addStyleName( coordinates[0], coordinates[1], "cell-modified" );
        } else {
            stepsTable.getCellFormatter().removeStyleName( coordinates[0], coordinates[1], "cell-modified" );

        }
        setReadOnly( coordinates[0], coordinates[1], title );
    }

    private void doAddStep( int selectedRow ) {
        //todo allow multiple addition of steps
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.addExperimentStep( selectedRow );
            setData( investigation.getExperiments() );
        } else if ( selectedRow == ActionType.UNDEFINED.getValue() ) {
            investigation.addExperimentStep();
            setData( investigation.getExperiments() );
        } // do nothing if ActionType.IGNORE.getValue() : we should ignore such clicks.
    }

    private void doCopyStep( int selectedRow ) {
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.deepExperimentCopy( selectedRow );
            setData( investigation.getExperiments() );
        } // do nothing if ActionType.IGNORE.getValue() or ActionType.UNDEFINED.getValue() : we should ignore such clicks.

    }

    private void doMakeEditable( int[] coordinates ) {
        if ( coordinates.length == 2 ) {
            setEditable( coordinates[0], coordinates[1] );
        }
    }

    private void doSave() {

        // there must be a nonzero value in every field: check each one, returning if any are empty
        String emptyValues = "\n";
        if ( investigationId.getValue().length() == 0 ) {
            emptyValues += "identifier, ";
        }
        if ( investigationTitle.getValue().length() == 0 ) {
            emptyValues += "title of investigation\n";
        }
        if ( providerId.getValue().length() == 0 ) {
            emptyValues += "provider identifier\n";
        }
        if ( firstName.getValue().length() == 0 ) {
            emptyValues += "provider first name\n";
        }
        if ( lastName.getValue().length() == 0 ) {
            emptyValues += "provider last name\n";
        }
        if ( emailAddress.getValue().length() == 0 ) {
            emptyValues += "provider email address\n";
        }

        if ( emptyValues.length() > 1 ) {
            Window.alert( "Error updating the following fields: " + emptyValues );
            return;
        }

        investigation.setId( investigationId.getValue() );
        investigation.setInvestigationTitle( investigationTitle.getValue() );
        investigation.getProvider().setId( providerId.getValue() );
        investigation.getProvider().setFirstName( firstName.getValue() );
        investigation.getProvider().setLastName( lastName.getValue() );
        investigation.getProvider().setEmailAddress( emailAddress.getValue() );
        investigation.setAllModified( false );

        // the experiment steps were saved as we went along, so nothing extra to do here.

        rpcService.updateInvestigation( investigation, new AsyncCallback<ArrayList<InvestigationDetails>>() {
            public void onSuccess( ArrayList<InvestigationDetails> updatedDetails ) {
                buttonOne.setButtonDisabled( true );
                buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                buttonTwo.setButtonDisabled( true );
                buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );

                String savedTitle = investigation.getInvestigationTitle();
                clearModifiable();
                investigatePanel.setInvestigationDetails( updatedDetails );
                investigatePanel.sortInvestigationDetails();
                investigatePanel.setViewData();
                setWidget( contentTableRowCount++, 0, new Label( savedTitle + " saved." ) );
            }

            public void onFailure( Throwable caught ) {
                String savedTitle = "unknown title";
                if ( investigation.getInvestigationTitle() != null &&
                        investigation.getInvestigationTitle().length() > 0 ) {
                    savedTitle = investigation.getInvestigationTitle();
                }
                Window.alert( "Error updating investigation " + savedTitle );
                caught.printStackTrace( System.err );
            }
        } );
    }

    private void initDetailsTable() {

        HorizontalPanel panel = new HorizontalPanel();
        panel.add( new Label( "Investigation ID (temp):" ) );
        panel.add( investigationId );
        detailsPanel.add( panel );

        panel = new HorizontalPanel();
        panel.add( new Label( "Investigation Title" ) );
        panel.add( investigationTitle );
        detailsPanel.add( panel );

        panel = new HorizontalPanel();
        panel.add( new Label( "Provider ID (temp)" ) );
        panel.add( providerId );
        detailsPanel.add( panel );

        panel = new HorizontalPanel();
        panel.add( new Label( "First Name" ) );
        panel.add( firstName );
        detailsPanel.add( panel );

        panel = new HorizontalPanel();
        panel.add( new Label( "Last Name" ) );
        panel.add( lastName );
        detailsPanel.add( panel );

        panel = new HorizontalPanel();
        panel.add( new Label( "Email Address" ) );
        panel.add( emailAddress );
        detailsPanel.add( panel );

        firstName.setFocus( true );
    }

    private void fetchExperimentStepDetails() {
        //todo replace with RPC call that will retrieve summary details only

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
            // here, the modification to the experiment marked with "isModified" is inaccessible, so just check
            // that the value is different from what was in the
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

            // when radio buttons are added again, none are selected, so disable the upload buttons
            buttonOne.setButtonDisabled( true );
            buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
            buttonTwo.setButtonDisabled( true );
            buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );

            addRowActions( rowValue );

            int i = 0;
            while ( i != depth ) {
                stepsTable.setText( rowValue, i + ActionType.SELECT.getValue() + 1, " " );
                i++;
            }

            stepsTable.setText( rowValue, depth + ActionType.SELECT.getValue() + 1, holder.getCurrent().getTitle() );

            // make just this cell a different style if it has been modified
            if ( holder.isModified() ) {
                stepsTable.getCellFormatter()
                        .addStyleName( rowValue, depth + ActionType.SELECT.getValue() + 1, "cell-modified" );
            } else {
                stepsTable.getCellFormatter()
                        .removeStyleName( rowValue, depth + ActionType.SELECT.getValue() + 1, "cell-modified" );
            }

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

    /**
     * Adds the actions for each row.
     *
     * @param rowValue the row to add to
     */
    private void addRowActions( int rowValue ) {

        Image addChildImage = new Image();
        addChildImage.setUrl( addChildImageUrl );
//        addChildImage.addStyleName( "size30x30" );
        addChildImage.setTitle( "Add Sub-Step" );
        stepsTable.setWidget( rowValue, ActionType.ADD.getValue(), addChildImage );

        Image copyStepImage = new Image();
        copyStepImage.setUrl( copyStepImageUrl );
//        copyStepImage.addStyleName( "size30x15" );
        copyStepImage.setTitle( "Copy Step" );
        stepsTable.setWidget( rowValue, ActionType.COPY.getValue(), copyStepImage );

        RadioButton radio = new RadioButton( "fileSelector" );

        // as long as at least one radio button is selected, then it's OK to have the upload button enabled.
        radio.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                buttonOne.setButtonDisabled( false );
                buttonOne.setButtonCursor( SWFUpload.ButtonCursor.HAND.getValue() );
                buttonTwo.setButtonDisabled( false );
                buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.HAND.getValue() );
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.SELECT.getValue(), radio );

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
            if ( cell.getCellIndex() == ActionType.ADD.getValue() ) {
                // this click is only allowed for additions
                return ActionType.ADD;
            } else if ( cell.getCellIndex() == ActionType.COPY.getValue() ) {
                // this click is only allowed for copying a step
                return ActionType.COPY;
            } else if ( cell.getCellIndex() == ActionType.SELECT.getValue() ) {
                // this click is only allowed for selecting a radio button, and we don't need to do anything.
                // return the IGNORE signal
                return ActionType.IGNORE;
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
     * @return the row number of the clicked row; ActionType.IGNORE.getValue() if outside the appropriate area for
     *         clicking (i.e. ignore); ActionType.UNDEFINED.getValue() if
     *         there is some other error meaning that no cell was actually clicked
     */
    public int getClickedRowForSubStepAddition( ClickEvent event ) {
        int selectedRow = ActionType.UNDEFINED.getValue();
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() == ActionType.ADD.getValue() ) {
                selectedRow = cell.getRowIndex();
            } else {
                // outside the appropriate area for clicking on a whole-row level
                return ActionType.IGNORE.getValue();
            }
        }

        return selectedRow;
    }

    /**
     * Returns the value of the row that has just been clicked on, as long as it is also the appropriate column for
     * copying the current step.
     *
     * @param event the even that is being caught
     * @return the row number of the clicked row; ActionType.IGNORE.getValue() if outside the appropriate area for
     *         clicking (i.e. ignore); ActionType.UNDEFINED.getValue() if
     *         there is some other error meaning that no cell was actually clicked
     */
    public int getClickedRowForStepCopying( ClickEvent event ) {
        int selectedRow = ActionType.UNDEFINED.getValue();
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() == ActionType.COPY.getValue() ) {
                selectedRow = cell.getRowIndex();
            } else {
                // outside the appropriate area for clicking on a whole-row level
                return ActionType.IGNORE.getValue();
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
     *         number of the clicked cell. Values for each will be ActionType.IGNORE.getValue() if outside the
     *         appropriate area for clicking (i.e. ignore); ActionType.UNDEFINED.getValue() if there is some other
     *         error meaning that no cell was actually clicked
     */
    public int[] getClickedCoordinatesForEdit( ClickEvent event ) {
        int selectedRow = ActionType.UNDEFINED.getValue();
        int selectedColumn = ActionType.UNDEFINED.getValue();
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() > ActionType.SELECT.getValue() ) {
                // check that it doesn't already have a text box in it, and therefore doesn't need updating
                if ( !isEditable( stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() ) ) ) {
                    // if it is a cell with some text, then it is a cell with an experiment step that should be
                    // editable. If empty, return the value for ignoring the cell.
                    if ( stepsTable.getText( cell.getRowIndex(), cell.getCellIndex() ).length() == 0 ) {
                        // outside the appropriate area for clicking
                        return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
                    }
                    selectedRow = cell.getRowIndex();
                    selectedColumn = cell.getCellIndex();
                } else {
                    // outside the appropriate area for clicking
                    return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
                }
            } else {
                // outside the appropriate area for clicking
                return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
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
     *         number of the clicked cell. Values for each will be ActionType.IGNORE.getValue() if outside the
     *         appropriate area for clicking (i.e. ignore); ActionType.UNDEFINED.getValue() if there is some other
     *         error meaning that no cell was actually clicked
     */
    public int[] getClickedCoordinatesForSave( ClickEvent event ) {
        int selectedRow = ActionType.UNDEFINED.getValue();
        int selectedColumn = ActionType.UNDEFINED.getValue();
        HTMLTable.Cell cell = stepsTable.getCellForEvent( event );

        if ( cell != null ) {
            // Suppress clicks if the user is not pressing in the correct area
            if ( cell.getCellIndex() > ActionType.SELECT.getValue() ) {
                // check that it already has a text box in it, and therefore is valid for saving
                if ( isEditable( stepsTable.getWidget( cell.getRowIndex(), cell.getCellIndex() ) ) ) {
                    // check that it is the saveInfoButton that has been clicked
                    if ( event.getSource() != saveInfoButton ) {
                        // outside the appropriate area for clicking
                        return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
                    }
                    selectedRow = cell.getRowIndex();
                    selectedColumn = cell.getCellIndex();
                } else {
                    // outside the appropriate area for clicking
                    return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
                }
            } else {
                // outside the appropriate area for clicking
                return new int[]{ ActionType.IGNORE.getValue(), ActionType.IGNORE.getValue() };
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
