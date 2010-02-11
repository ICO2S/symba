package net.sourceforge.symba.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import org.swfupload.client.File;
import org.swfupload.client.SWFUpload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditInvestigationTable extends FlexTable {

    private final InvestigationsServiceAsync rpcService;
    private final SummariseInvestigationPanel investigatePanel;

    private final Button saveButton;
    private final Button setAsTemplateButton;
    private final Button saveCopyAsTemplateButton;
    private final Button cancelButton;
    private final Button addSubStepButton;
    private final String baseApp = GWT.getModuleBaseURL()
            .substring( 0, GWT.getModuleBaseURL().lastIndexOf( GWT.getModuleName() ) );
    private final String addChildImageUrl = baseApp + "/images/addChild30x30.png";
    private final String copyStepImageUrl = baseApp + "/images/copyStep30x15.png";

    private final SWFUpload buttonOne, buttonTwo;

    private FlexTable stepsTable;
    private ReadWriteDetailsPanel readWriteDetailsPanel;
    private EditableStepPanel editableStepPanel;

    private int contentTableRowCount;
    private Investigation investigation;
    private boolean defaultHandlersSet;

    // This variable will change *whenever* an onClick for the radio Button occurs. As it may change while files
    // are being uploaded, the upload mechanism stores the value of this variable when the upload button is pressed.
    // It is then sent back to this class when each file is ready to be associated with a step.
    private int selectedRadioRow;

    private enum ActionType {
        ADD( 0 ), COPY( 1 ), SELECT( 2 ), UNDEFINED( -2 );

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
                                   SWFUpload buttonTwo ) {

        // initialise all final variables
        this.rpcService = rpcService;
        this.investigatePanel = investigatePanel;
        this.buttonOne = buttonOne;
        this.buttonTwo = buttonTwo;

        saveButton = new Button( "Save and Finish" );
        setAsTemplateButton = new Button( "Set As Template" );
        saveCopyAsTemplateButton = new Button( "Save a Copy As Template" );
        cancelButton = new Button( "Cancel" );
        addSubStepButton = new Button( "Add Top-Level Step" );

        // clear / set to empty all modifiable variables except defaultHandlersSet
        clearModifiable();

        // defaultHandlersSet should only be set to false in the constructor, and then true the first time
        // the handlers are loaded. Other than that, no modifications should be performed on this variable.
        // Therefore, it should not be included in clearModifiable()
        defaultHandlersSet = false;
    }

    // todo
    // upload URIs (must be on cisbclust) - once done, SyMBA checks the file is there and then sets it to read only.
    //

    //
    // initialising/clearing/resetting methods
    //

    public void initEditInvestigationTable() {

        clearModifiable();

        setWidth( "100%" );
//            getCellFormatter().addStyleName( 0, 0, "investigation-ListContainer" );
        getCellFormatter().setWidth( 0, 0, "100%" );
        getFlexCellFormatter().setVerticalAlignment( 0, 0, DockPanel.ALIGN_TOP );

        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setBorderWidth( 0 );
        menuPanel.setSpacing( 0 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        menuPanel.add( saveButton );
        menuPanel.add( setAsTemplateButton );
        menuPanel.add( saveCopyAsTemplateButton );
        menuPanel.add( cancelButton );
//            getCellFormatter().addStyleName( 0, 0, "contacts-ListMenu" );
        setWidget( contentTableRowCount++, 0, menuPanel );

        // Create the investigation summary view
        readWriteDetailsPanel = new ReadWriteDetailsPanel();
        setWidget( contentTableRowCount++, 0, readWriteDetailsPanel );

        HorizontalPanel addStepPanel = new HorizontalPanel();
        addStepPanel.setBorderWidth( 0 );
        addStepPanel.setSpacing( 0 );
        addStepPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        addStepPanel.add( addSubStepButton );
//            getCellFormatter().addStyleName( 2, 0, "contacts-ListMenu" );
        setWidget( contentTableRowCount++, 0, addStepPanel );

        stepsTable.setCellSpacing( 0 );
        stepsTable.setCellPadding( 0 );

//        stepsTable.setWidth( "100%" );
        stepsTable.getColumnFormatter().setWidth( 0, "15px" );
        setWidget( contentTableRowCount++, 0, stepsTable );

        addDefaultHandlers();
    }

    private void clearModifiable() {
        investigation = new Investigation();
        contentTableRowCount = 0;
        selectedRadioRow = -1;

        // cannot initialise an EditableStepPanel or ReadableStepPanel until we have rows and columns
        editableStepPanel = null;

        stepsTable = new FlexTable();

        removeAllRows();
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

        setAsTemplateButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                // remove any files associated with this investigation, as templates don't have files.
                // however, before doing this, check if the user really wants to make this a template, with all of
                // its consequences.
                // todo allow read-only investigations to be removed or modified by their owners only.
                boolean response = Window.confirm(
                        "Setting this Investigation as a template will first save its current state, and then remove " +
                                "any links to files you may have " +
                                "made. It will set this Investigation as read-only. The purpose of a template is for " +
                                "it to be copied by you and other users, thus sharing common aspects of " +
                                "Investigations. If, instead, you want a copy of this Investigation to be made a " +
                                "template, cancel this request and choose \"Save A Copy As Template\". Are you sure " +
                                "you wish to continue?" );
                if ( response ) {
                    String id = investigation.getId(); // unchangeable by the user
                    String title = investigation
                            .getInvestigationTitle(); // todo take the version from the page, not the stored investigation
                    doSave();
                    setAsTemplate( id, title );
                } else {
                    Window.alert( "Saving as template cancelled." );
                }
            }
        } );

        saveCopyAsTemplateButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                // remove any files associated with this investigation, as templates don't have files.
                // however, before doing this, check if the user really wants to make a copy of this a template
                // todo allow read-only investigations to be removed or modified by their owners only.
                boolean response = Window.confirm(
                        "This will first save this Investigation, then make a copy of it, storing the copy as a" +
                                "template. The purpose of a template is for " +
                                "it to be copied by you and other users, thus sharing common aspects of " +
                                "Investigations. Are you sure you wish to continue?" );
                if ( response ) {
                    doSave( true );
                } else {
                    Window.alert( "Saving a copy as a template cancelled." );
                }

            }
        } );

        addSubStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doAddStep( ActionType.UNDEFINED.getValue() ); // force a top-level add of an experiment step
            }
        } );

        cancelButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( GWT.isScript() ) {
                    buttonOne.setButtonDisabled( true );
                    buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                    buttonTwo.setButtonDisabled( true );
                    buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                }
                String cancelledTitle = investigation.getInvestigationTitle();
                clearModifiable();
                setWidget( contentTableRowCount++, 0, new Label( "Did not modify " + cancelledTitle + "." ) );
            }
        } );

    }

    private void setAsTemplate( String id,
                                final String title ) {
        rpcService.setInvestigationAsTemplate( id,
                new AsyncCallback<ArrayList<InvestigationDetail>>() {
                    public void onFailure( Throwable throwable ) {
                        Window.alert( "Saving of Investigation " + title +
                                " as a template failed: " + Arrays.toString( throwable.getStackTrace() ) );
                    }

                    public void onSuccess( ArrayList<InvestigationDetail> results ) {
                        if ( GWT.isScript() ) {
                            buttonOne.setButtonDisabled( true );
                            buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                            buttonTwo.setButtonDisabled( true );
                            buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                        }
                        clearModifiable();
                        investigatePanel.setInvestigationDetails( results );
                        investigatePanel.sortInvestigationDetails();
                        investigatePanel.setViewData();
                        setWidget( contentTableRowCount++, 0, new Label( title + " has been set as a template." ) );
                    }
                } );
    }

    //
    // un-modifying (e.g. getting) methods
    //

    public int getSelectedRadioRow() {
        return selectedRadioRow;
    }

    //
    // Methods which change the class variables or run RPC calls which modify server variables
    //

    public void displayInvestigation( String id ) {

        rpcService.getInvestigation( id, new AsyncCallback<Investigation>() {
            public void onSuccess( Investigation result ) {

                initEditInvestigationTable();

                investigation = result;
                readWriteDetailsPanel.createReadableDisplay( investigation );

                if ( investigation.isTemplate() ) {
                    // disable buttons that allow modifications
                    saveButton.setEnabled( false );
                    setAsTemplateButton.setEnabled( false );
                    saveCopyAsTemplateButton.setEnabled( false );
                    cancelButton.setEnabled( false );
                    addSubStepButton.setEnabled( false );
                } else {
                    // we need the "else" here if a template was previously shown, and therefore buttons were
                    // previously disabled. We need to explicitly enable them.
                    saveButton.setEnabled( true );
                    setAsTemplateButton.setEnabled( true );
                    saveCopyAsTemplateButton.setEnabled( true );
                    cancelButton.setEnabled( true );
                    addSubStepButton.setEnabled( true );
                }

                displaySteps();
            }

            public void onFailure( Throwable caught ) {
                Window.alert( "Error retrieving investigation" );
            }
        } );

    }

    private void displaySteps() {
        //todo replace with RPC call that will retrieve summary details only

        displayData( investigation.getExperiments() );
    }

    /**
     * Should only be called from the [void displayData()] method which implements the Display interface
     *
     * @param data     the thing to display in the table
     * @param rowValue the value being passed through the steps hierarchy
     * @param depth    the hierarchical depth of the current step
     * @return the new value of the row count
     */
    public int displayData( List<ExperimentStepHolder> data,
                            Integer rowValue,
                            Integer depth ) {
        if ( GWT.isScript() ) {
            // when radio buttons are added again, none are selected, so disable the upload buttons: only add file upload
            // features if in scripting mode (i.e. not development mode).
            buttonOne.setButtonDisabled( true );
            buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
            buttonTwo.setButtonDisabled( true );
            buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
        }

        for ( ExperimentStepHolder holder : data ) {
//            System.err
//                    .println( "Printing Row " + rowValue + " (" + depth + holder.getCurrent().getTitle() + ")" );
            holder.setStepId( rowValue );

            addRowActions( rowValue );

            int i = 0;
            while ( i != depth ) {
                stepsTable.setText( rowValue, i + ActionType.SELECT.getValue() + 1, " " );
                i++;
            }

            ClickHandler myEditableHandler = new makeEditableHandler( rowValue,
                    depth + ActionType.SELECT.getValue() + 1 );
            ReadableStepPanel readableStepPanel = new ReadableStepPanel(
                    holder.getCurrent().getTitle(),
                    holder.getCurrent().getFileNames(), holder.getCurrent().getParameters(), myEditableHandler );
            stepsTable.setWidget( rowValue, depth + ActionType.SELECT.getValue() + 1, readableStepPanel );

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
                rowValue = displayData( holder.getCurrent().getChildren(), rowValue, depth + 1 );
            }
        }
        return rowValue;
    }

    /**
     * clears the existing display of experiment steps and starts re-writing from the beginning.
     *
     * @param data the data to display
     */
    public void displayData( List<ExperimentStepHolder> data ) {
        stepsTable.removeAllRows();
        displayData( data, 0, 0 );
    }

    public void assignFileToStep( File file,
                                  int radioRowSelectedOnUpload ) {
        int depth = investigation.addExperimentFile( radioRowSelectedOnUpload, file );
        int column = depth + ActionType.SELECT.getValue() + 1;
        // add the action columns to the depth value
        stepsTable.getCellFormatter()
                .addStyleName( radioRowSelectedOnUpload, column, "cell-modified" );
        stepsTable.setHTML( radioRowSelectedOnUpload, column,
                stepsTable.getHTML( radioRowSelectedOnUpload, column ) + "<br/>" + file.getName() );
    }

    /**
     * Adds the actions for each row.
     *
     * @param rowValue the row to add to
     */
    private void addRowActions( final int rowValue ) {

        Image addChildImage = new Image();
        addChildImage.setUrl( addChildImageUrl );
//        addChildImage.addStyleName( "size30x30" );
        addChildImage.setTitle( "Add Sub-Step" );
        addChildImage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                doAddStep( rowValue );
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.ADD.getValue(), addChildImage );

        Image copyStepImage = new Image();
        copyStepImage.setUrl( copyStepImageUrl );
//        copyStepImage.addStyleName( "size30x15" );
        copyStepImage.setTitle( "Copy Step" );
        copyStepImage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                doCopyStep( rowValue );
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.COPY.getValue(), copyStepImage );

        RadioButton radio = new RadioButton( "fileSelector" );

        // as long as at least one radio button is selected, then it's OK to have the upload button enabled.
        radio.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                HTMLTable.Cell cell = stepsTable.getCellForEvent( clickEvent );
                selectedRadioRow = cell.getRowIndex();

                if ( GWT.isScript() ) {
                    // don't add file upload functionality if not in scripting mode (i.e. in development mode)
                    buttonOne.setButtonDisabled( false );
                    buttonOne.setButtonCursor( SWFUpload.ButtonCursor.HAND.getValue() );
                    buttonTwo.setButtonDisabled( false );
                    buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.HAND.getValue() );
                }
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.SELECT.getValue(), radio );

    }

    private void doAddStep( int selectedRow ) {
        //todo allow multiple addition of steps
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.addExperimentStep( selectedRow );
            displayData( investigation.getExperiments() );
        } else if ( selectedRow == ActionType.UNDEFINED.getValue() ) {
            investigation.addExperimentStep();
            displayData( investigation.getExperiments() );
        } // do nothing if ActionType.IGNORE.getValue() : we should ignore such clicks.
    }

    private void doCopyStep( int selectedRow ) {
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.deepExperimentCopy( selectedRow );
            displayData( investigation.getExperiments() );
        } // do nothing if ActionType.IGNORE.getValue() or ActionType.UNDEFINED.getValue() : we should ignore such clicks.

    }

    private void doSave() {
        doSave( false );
    }

    private void doSave( final boolean makeTemplate ) {

        String emptyValues = readWriteDetailsPanel.makeErrorMessages();
        if ( emptyValues.length() > 1 ) {
            Window.alert( "Error updating the following fields: " + emptyValues );
            return;
        }

        // only change those values that have been modified, which in this simple case will just be those
        // with text boxes rather than simple labels.
        readWriteDetailsPanel.updateModifiedDetails( investigation );
        investigation.setAllModified( false );

        // the experiment steps were saved as we went along, so nothing extra to do here.

        rpcService.updateInvestigation( investigation, new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> updatedDetails ) {
                if ( GWT.isScript() ) {
                    buttonOne.setButtonDisabled( true );
                    buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                    buttonTwo.setButtonDisabled( true );
                    buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                }
                String title = investigation.getInvestigationTitle();
                clearModifiable();
                investigatePanel.setInvestigationDetails( updatedDetails );
                investigatePanel.sortInvestigationDetails();
                investigatePanel.setViewData();
                setWidget( contentTableRowCount++, 0, new Label( title + " saved." ) );

                if ( makeTemplate ) {
                    rpcService.copyInvestigation( investigation.getId(),
                            new AsyncCallback<InvestigationDetail>() {
                                public void onSuccess( InvestigationDetail result ) {
                                    setAsTemplate( result.getId(), result.getInvestigationTitle() );
                                }

                                public void onFailure( Throwable caught ) {
                                    Window.alert(
                                            "Error copying Investigation " + investigation.getInvestigationTitle() +
                                                    ": no template created." +
                                                    Arrays.toString( caught.getStackTrace() ) );
                                }
                            } );

                }
            }

            public void onFailure( Throwable caught ) {
                String savedTitle = "unknown title";
                if ( investigation.getInvestigationTitle() != null &&
                        investigation.getInvestigationTitle().length() > 0 ) {
                    savedTitle = investigation.getInvestigationTitle();
                }
                Window.alert( "Error updating investigation " + savedTitle + " stack trace: " + caught.toString() );
                caught.printStackTrace( System.err );
            }
        } );
    }

    private class makeEditableHandler implements ClickHandler {
        private int row, column;

        public makeEditableHandler( int row,
                                    int column ) {
            this.row = row;
            this.column = column;
        }

        public void onClick( ClickEvent clickEvent ) {
            displayEditable( row, column );

        }

        public void displayEditable( final int row,
                                     final int column ) {

            // we come to this method if a user has clicked on an experiment step. Prior to this, either there are
            // no previously-editable fields, or there is an editable field that has not yet been reset to read only.
            // use the previously-stored coordinates and set them to read-only.

            if ( editableStepPanel != null ) {
                editableStepPanel.setReadOnly( stepsTable );
                editableStepPanel = null;
            }

            ReadableStepPanel readable = ( ReadableStepPanel ) stepsTable.getWidget( row, column );
            editableStepPanel = new EditableStepPanel( readable, stepsTable, investigation, row, column,
                    new makeEditableHandler( row, column ) );
            stepsTable.setWidget( row, column, editableStepPanel );

        }
    }
}
