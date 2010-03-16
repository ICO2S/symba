package net.sourceforge.symba.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.InvestigationsServiceAsync;
import net.sourceforge.symba.web.client.gui.panel.*;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;
import org.swfupload.client.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditInvestigationView extends VerticalPanel {

    // todo disable all functions on entire page until file uploads are complete
    // todo refactor this class to make it easier to read

    private static enum SaveType {
        SAVE_ONLY, SET_AS_TEMPLATE, SET_COPY_AS_TEMPLATE
    }

    private final InvestigationsServiceAsync rpcService;
    private final SymbaController symba;

    private final Button saveButton;
    private final Button setAsTemplateButton;
    private final Button saveCopyAsTemplateButton;
    private final Button cancelButton;
    private final Button addSubStepButton;
    private final String addChildImageUrl;
    private final String copyStepImageUrl;

    private FlexTable stepsTable;
    private final ReadWriteDetailsPanel readWriteDetailsPanel;

    private final Investigation investigation;
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
     * @param symba         the controller panel for the entire interface
     * @param investigation the investigation assigned to this view.
     * @param rpcService    the service to use to call the GWT server side
     * @param contacts      the contacts that are to be passed to the main panel
     */
    public EditInvestigationView( SymbaController symba,
                                  Investigation investigation,
                                  InvestigationsServiceAsync rpcService,
                                  HashMap<String, Contact> contacts ) {

        // initialise all final variables
        this.rpcService = rpcService;
        this.symba = symba;
        if ( investigation != null ) {
            this.investigation = investigation;
        } else {
            this.investigation = new Investigation();
            this.investigation.createId();
            this.investigation.getProvider().createId();
        }
        readWriteDetailsPanel = new ReadWriteDetailsPanel( contacts, rpcService );

        if ( GWT.isScript() ) {
            String baseApp = GWT.getModuleBaseURL()
                    .substring( 0, GWT.getModuleBaseURL().lastIndexOf( GWT.getModuleName() ) );
            addChildImageUrl = baseApp + "/images/addChild30x30.png";
            copyStepImageUrl = baseApp + "/images/copyStep30x15.png";
        } else {
            // you need slightly different URLs when in development mode.
            addChildImageUrl = "/images/addChild30x30.png";
            copyStepImageUrl = "/images/copyStep30x15.png";
        }

        saveButton = new Button( "Save and Finish" );
        setAsTemplateButton = new Button( "Set As Template" );
        saveCopyAsTemplateButton = new Button( "Save a Copy As Template" );
        cancelButton = new Button( "Cancel" );
        addSubStepButton = new Button( "Add Top-Level Step" );

        // defaultHandlersSet should only be set to false in the constructor, and then true the first time
        // the handlers are loaded. Other than that, no modifications should be performed on this variable.
        // Therefore, it should not be included in clearModifiable()
        defaultHandlersSet = false;

        displayInvestigation();
    }

    // todo
    // upload URIs (must be on cisbclust) - once done, SyMBA checks the file is there and then sets it to read only.
    //

    //
    // initialising/clearing/resetting methods
    //

    public void initEditInvestigationView() {

        clearModifiable();
        setWidth( "100%" );

        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setBorderWidth( 0 );
        menuPanel.setSpacing( 0 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        menuPanel.add( saveButton );
        menuPanel.add( setAsTemplateButton );
        menuPanel.add( saveCopyAsTemplateButton );
        menuPanel.add( cancelButton );

        HorizontalPanel addStepPanel = new HorizontalPanel();
        addStepPanel.setBorderWidth( 0 );
        addStepPanel.setSpacing( 0 );
        addStepPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        addStepPanel.add( addSubStepButton );

        // wrap the stepsTable within a CaptionPanel
        CaptionPanel protocolWrapper = new CaptionPanel( "Experimental Steps" );
        VerticalPanel protocolPanel = new VerticalPanel();
        protocolPanel.add( addStepPanel );
        protocolWrapper.setStyleName( "captionpanel-border" );
        protocolPanel.add( stepsTable );
        protocolWrapper.add( protocolPanel );

        addDefaultHandlers();

        add( menuPanel );
        add( readWriteDetailsPanel );
        add( protocolWrapper );

    }

    private void clearModifiable() {
        selectedRadioRow = -1;

        // cannot initialise a ReadableStepView until we have rows and columns

        stepsTable = new FlexTable();
        stepsTable.setCellSpacing( 0 );
        stepsTable.setCellPadding( 0 );

        for ( int iii = getWidgetCount() - 1; iii >= 0; iii-- ) {
            remove( iii );
        }
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
                    doSave( SaveType.SET_AS_TEMPLATE );
                } else {
                    // no need to keep any file statuses at this point
                    symba.showEastWidget(
                            "<p>Saving as template cancelled.</p>", symba.getEastWidgetDirections() );
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
                    doSave( SaveType.SET_COPY_AS_TEMPLATE );
                } else {
                    // no need to keep any file statuses at this point
                    symba.showEastWidget( "<p>Saving a copy as a template cancelled.</p>",
                            symba.getEastWidgetDirections() );
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
                if ( investigation.getInvestigationTitle().length() > 0 ) {
                    // no need to keep any file statuses at this point
                    symba.showEastWidget(
                            "<p>Modifications to <strong>" + investigation.getInvestigationTitle() +
                                    "</strong> cancelled.</p>", symba.getEastWidgetDirections() );
                } else {
                    // no need to keep any file statuses at this point
                    symba.showEastWidget(
                            "<p>Creation of new investigation cancelled.</p>", symba.getEastWidgetDirections()
                    );
                }
                symba.setCenterWidgetAsListExperiments();
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
                        symba.setInvestigationDetails( results );
                        // no need to keep any file statuses at this point
                        symba.showEastWidget( "<p><strong>" + title + "</strong> has been set as a template.</p>", "" );
                        symba.setCenterWidgetAsListExperiments();
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

    private void displayInvestigation() {
        initEditInvestigationView();

        // we need the non-template settings here if a template was previously shown, and therefore buttons were
        // previously disabled. In such cases, we need to explicitly enable them.
        saveButton.setEnabled( true );
        setAsTemplateButton.setEnabled( true );
        saveCopyAsTemplateButton.setEnabled( true );
        cancelButton.setEnabled( true );
        addSubStepButton.setEnabled( true );

        readWriteDetailsPanel.createReadableDisplay( investigation );

        if ( investigation.isTemplate() ) {
            // disable buttons that allow modifications
            saveButton.setEnabled( false );
            setAsTemplateButton.setEnabled( false );
            saveCopyAsTemplateButton.setEnabled( false );
            addSubStepButton.setEnabled( false );
        } else {
            // we need the "else" here if a template was previously shown, and therefore buttons were
            // previously disabled. We need to explicitly enable them.
            saveButton.setEnabled( true );
            setAsTemplateButton.setEnabled( true );
            saveCopyAsTemplateButton.setEnabled( true );
            addSubStepButton.setEnabled( true );
        }
        cancelButton.setEnabled( true );

//                symba.showEastWidget( "Running Display Data", symba.getEastWidgetDirections() );

        displayData();
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

        for ( ExperimentStepHolder holder : data ) {
//            System.err
//                    .println( "Printing Row " + rowValue + " (" + depth + " " + holder.getCurrent().getTitle() + ")" );
            holder.setStepId( rowValue );

            addRowActions( rowValue );

            int i = 0;
            while ( i != depth ) {
                stepsTable.setText( rowValue, i + ActionType.SELECT.getValue() + 1, " " );
                i++;
            }

            ReadableStepView readableStepView;
            if ( !investigation.isTemplate() ) {
                ClickHandler myHandler = new makeEditableHandler( rowValue,
                        depth + ActionType.SELECT.getValue() + 1 );
                readableStepView = new ReadableStepView(
                        holder.getCurrent().getTitle(),
                        holder.getCurrent().getFileNames(), holder.getCurrent().getParameters(), myHandler );
            } else {
                readableStepView = new ReadableStepView(
                        holder.getCurrent().getTitle(),
                        holder.getCurrent().getFileNames(), holder.getCurrent().getParameters() );
            }
            stepsTable.setWidget( rowValue, depth + ActionType.SELECT.getValue() + 1, readableStepView );

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
     */
    public void displayData() {
        stepsTable.removeAllRows();
        displayData( investigation.getExperiments(), 0, 0 );
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
        addChildImage.setTitle( "Add Sub-Step" );
        addChildImage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                doAddStep( rowValue );
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.ADD.getValue(), addChildImage );
        stepsTable.getCellFormatter().setHeight( rowValue, ActionType.ADD.getValue(), "30px" );
        stepsTable.getCellFormatter().setWidth( rowValue, ActionType.ADD.getValue(), "30px" );

        Image copyStepImage = new Image();
        copyStepImage.setUrl( copyStepImageUrl );
        copyStepImage.setTitle( "Copy Step" );
        copyStepImage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                doCopyStep( rowValue );
            }
        } );
        stepsTable.setWidget( rowValue, ActionType.COPY.getValue(), copyStepImage );
        stepsTable.getCellFormatter().setHeight( rowValue, ActionType.ADD.getValue(), "30px" );
        stepsTable.getCellFormatter().setWidth( rowValue, ActionType.ADD.getValue(), "15px" );

        RadioButton radio = new RadioButton( "fileSelector" );

        // as long as at least one radio button is selected, then it's OK to have the upload button enabled.
        radio.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                HTMLTable.Cell cell = stepsTable.getCellForEvent( clickEvent );
                selectedRadioRow = cell.getRowIndex();

            }
        } );
        stepsTable.setWidget( rowValue, ActionType.SELECT.getValue(), radio );

    }

    private void doAddStep( int selectedRow ) {
        //todo allow multiple addition of steps
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.addExperimentStep( selectedRow );
            displayData();
        } else if ( selectedRow == ActionType.UNDEFINED.getValue() ) {
            investigation.addExperimentStep();
            displayData();
        } // do nothing if ActionType.IGNORE.getValue() : we should ignore such clicks.
    }

    private void doCopyStep( int selectedRow ) {
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.deepExperimentCopy( selectedRow );
            displayData();
        } // do nothing if ActionType.IGNORE.getValue() or ActionType.UNDEFINED.getValue() : we should ignore such clicks.

    }

    private void doSave() {
        doSave( SaveType.SAVE_ONLY );
    }

    private void doSave( final SaveType saveType ) {

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

        // todo ensure Identifier has changed before this step, if necessary
        rpcService.updateInvestigation( investigation, new AsyncCallback<ArrayList<InvestigationDetail>>() {
            public void onSuccess( ArrayList<InvestigationDetail> updatedDetails ) {
                final String title = investigation.getInvestigationTitle();
                final String id = investigation.getId();
                symba.setInvestigationDetails( updatedDetails );
                symba.showEastWidget( "<p><strong>" + title + "</strong> saved.</p>", "" );
                symba.setCenterWidgetAsListExperiments();

                if ( saveType == SaveType.SET_COPY_AS_TEMPLATE ) {
                    rpcService.copyInvestigation( id,
                            new AsyncCallback<InvestigationDetail>() {
                                public void onSuccess( InvestigationDetail result ) {
                                    setAsTemplate( result.getId(), result.getInvestigationTitle() );
                                }

                                public void onFailure( Throwable caught ) {
                                    Window.alert(
                                            "Error copying Investigation " + title + ": no template created." +
                                                    Arrays.toString( caught.getStackTrace() ) );
                                }
                            } );

                } else if ( saveType == SaveType.SET_AS_TEMPLATE ) {
                    setAsTemplate( id, title );
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

            final PopupPanel popup = new PopupPanel( true );
            popup.setPopupPositionAndShow( new PopupPanel.PositionCallback() {
                public void setPosition( int offsetWidth,
                                         int offsetHeight ) {
                    int top = ( Window.getClientHeight() - offsetHeight ) / 2;
                    popup.setPopupPosition( 0, top );
                }
            } );

            popup.show();
            EditableStepView editableStepView = new EditableStepView(
                    ( ReadableStepView ) stepsTable.getWidget( row, column ), popup, stepsTable, investigation, row,
                    column, this );
            popup.add( editableStepView );
            editableStepView.getStepTitle().setFocus( true );

        }
    }

}
