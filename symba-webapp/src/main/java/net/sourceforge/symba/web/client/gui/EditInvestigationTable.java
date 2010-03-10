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
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditInvestigationTable extends FlexTable {

    // todo disable all functions on entire page until file uploads are complete
    // todo refactor this class to make it easier to read

    private final InvestigationsServiceAsync rpcService;
    private final SymbaControllerPanel symba;
    private final String toNewStepImageUrl = "/images/toNewStep70w76h.png";
    private final String toExistingStepImageUrl = "/images/toExistingStep70w52h.png";
    private HashMap<String, Integer> fileIdToRow;
    private int radioRowSelectedOnUpload;
    private List<File> files = new ArrayList<File>();

    private final Button saveButton;
    private final Button setAsTemplateButton;
    private final Button saveCopyAsTemplateButton;
    private final Button cancelButton;
    private final Button addSubStepButton;
    private final String addChildImageUrl;
    private final String copyStepImageUrl;

    private SWFUpload buttonOne, buttonTwo;

    private FlexTable stepsTable;
    private final ReadWriteDetailsPanel readWriteDetailsPanel;
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
     * @param symba      the controller panel for the entire interface
     * @param rpcService the service to use to call the GWT server side
     * @param contacts   the contacts that are to be passed to the main panel
     */
    public EditInvestigationTable( SymbaControllerPanel symba,
                                   InvestigationsServiceAsync rpcService,
                                   HashMap<String, Contact> contacts ) {

        // initialise all final variables
        this.rpcService = rpcService;
        this.symba = symba;
        fileIdToRow = new HashMap<String, Integer>();
        radioRowSelectedOnUpload = -1;
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
        setupMultipleFileUploader();

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
//        stepsTable.getColumnFormatter().setWidth( 0, "15px" );
        setWidget( contentTableRowCount++, 0, stepsTable );

        addDefaultHandlers();

    }

    private void clearModifiable() {
        investigation = new Investigation();
        investigation.createId();
        investigation.getProvider().createId();
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
                    doSave( true );
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
                if ( GWT.isScript() ) {
                    buttonOne.setButtonDisabled( true );
                    buttonOne.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                    buttonTwo.setButtonDisabled( true );
                    buttonTwo.setButtonCursor( SWFUpload.ButtonCursor.ARROW.getValue() );
                }
                if ( investigation != null ) {
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
                }
                clearModifiable();
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
                        symba.setInvestigationDetails( results );
                        // no need to keep any file statuses at this point
                        symba.showEastWidget( "<p><strong>" + title + "</strong> has been set as a template.</p>", ""
                        );
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

    public void displayEmptyInvestigation() {
        initEditInvestigationTable();
        Investigation investigation = new Investigation();
        investigation.createId();
        investigation.getProvider().createId();

        // we need the non-template settings here if a template was previously shown, and therefore buttons were
        // previously disabled. In such cases, we need to explicitly enable them.
        saveButton.setEnabled( true );
        setAsTemplateButton.setEnabled( true );
        saveCopyAsTemplateButton.setEnabled( true );
        cancelButton.setEnabled( true );
        addSubStepButton.setEnabled( true );

        readWriteDetailsPanel.createReadableDisplay( investigation );
    }

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
                final String title = investigation.getInvestigationTitle();
                final String id = investigation.getId();
                clearModifiable();
                symba.setInvestigationDetails( updatedDetails );
                symba.showEastWidget( "<p><strong>" + title + "</strong> saved.</p>", "" );

                if ( makeTemplate ) {
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

    private void setupMultipleFileUploader() {
        String moduleBase = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();
        String baseApp = moduleBase.substring( 0, moduleBase.lastIndexOf( moduleName ) );
        String url = baseApp + "upload";

//        northHtml.setHTML( "SyMBA upload service is running on " + url );

        // Determine if the demo is being viewed in Hosted mode, if so display a
        // warning. This is because Flash to JavaScript communications does not work
        // correctly in hosted mode.
        if ( !GWT.isScript() ) {
            HTML warning = new HTML();
            warning.addStyleName( "note" );
            warning.setHTML( "NIS: Not in scripting mode. You have to deploy the app to an application server! (" +
                    url + ")" );
            warning.setTitle( "Not in scripting mode. You have to deploy the app to an application server! (" +
                    url + ")" );
            setWidget( contentTableRowCount++, 0, warning );
            return;
        }

        HorizontalPanel tempPanel = new HorizontalPanel();
        HTML bt = new HTML( "<span id=\"buttonOne-button\" />" );
        tempPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        tempPanel.setSpacing( 20 );
        tempPanel.add( bt );
        HTML bt2 = new HTML( "<span id=\"uploadToNewStep-button\" />" );
        tempPanel.add( bt2 );
        setWidget( contentTableRowCount++, 0, tempPanel );

        setupExistingStepBuilder( baseApp, url );
        setupNewStepBuilder( baseApp, url );

    }

    private void setupNewStepBuilder( String baseApp,
                                      String url ) {

        final UploadBuilder builder = new UploadBuilder();
        // builder.setDebug(true);
        builder.setHTTPSuccessCodes( 200, 201 );
        builder.setFileTypes(
                "*.wma;*.wmv;*.avi;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif;*.svg;*.txt;*.doc;*.xls;*.odt" );
        builder.setFileTypesDescription( "Images, Video & Text" );

        builder.setButtonPlaceholderID( "uploadToNewStep-button" );
        builder.setButtonImageURL( baseApp + toNewStepImageUrl );
//        builder.setButtonText( "Each file is added to a copy of the selected step" );
        builder.setButtonDisabled( true );
        builder.setButtonCursor( SWFUpload.ButtonCursor.ARROW );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 76 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                // ensure the east widget is visible using any current values
                symba.showEastWidget();
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                        .setWidget( fileIdToRow.get( e.getFile().getId() ), 0, new HTML(
                                e.getFile().getName() + ": " + ( ( e.getBytesComplete() / e.getBytesTotal() ) * 100 ) +
                                        "%" ) );
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer yellow" );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                symba.showEastWidget();
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer blue" );

            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "buttonOne failed";
                }
                ( ( HTML ) symba.getEastWidget() ).setHTML(
                        ( ( HTML ) symba.getEastWidget() ).getText() + "<br />Upload error: " + e.getFile().getId() +
                                ", " + e.getFile().getName() + " / " + message );
                removeFile( e.getFile().getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    symba.showEastWidget();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonTwo.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    symba.showEastWidget();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonTwo.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                files.add( event.getFile() );
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
//                ((FlexTable) symba.getSouthWidget()).setHTML( wrapInFileQueueStyle( "", "files = " + files.size() ) );
                if ( files.size() > 0 ) {
                    // reset table variables
                    symba.showEastWidget();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().removeAllRows();
                    fileIdToRow = new HashMap<String, Integer>();
                    int fileIdToRowCount = 0;
                    // fill in fileIdToRow
                    for ( File file : files ) {
                        fileIdToRow.put( file.getId(), fileIdToRowCount++ );
                    }
                    // next, set the value in the appropriate table row.
                    String id = files.get( 0 ).getId();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() + ": 0%" ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonTwo.startUpload( id );
                }
            }
        } );
        buttonTwo = builder.build();

    }

    private void setupExistingStepBuilder( String baseApp,
                                           String url ) {
        final UploadBuilder builder = new UploadBuilder();
        // builder.setDebug(true);
        builder.setHTTPSuccessCodes( 200, 201 );
        builder.setFileTypes(
                "*.wma;*.wmv;*.avi;*.mpg;*.mpeg;*.mp4;*.mov;*.m4v;*.aac;*.mp3;*.wav;*.png;*.jpg;*.jpeg;*.gif;*.svg;*.txt;*.doc;*.xls;*.odt" );
        builder.setFileTypesDescription( "Images, Video & Text" );

        builder.setButtonPlaceholderID( "buttonOne-button" );
        builder.setButtonImageURL( baseApp + toExistingStepImageUrl );
//        builder.setButtonText( "All files are added to the selected step" );
        builder.setButtonDisabled( true );
        builder.setButtonCursor( SWFUpload.ButtonCursor.ARROW );
        builder.setButtonWidth( 70 );
        builder.setButtonHeight( 52 );
        builder.setButtonAction( SWFUpload.ButtonAction.SELECT_FILES );

        builder.setUploadProgressHandler( new UploadProgressHandler() {

            public void onUploadProgress( UploadProgressEvent e ) {
                symba.showEastWidget();
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().setWidget( fileIdToRow.get( e.getFile().getId() ), 0, new HTML(
                        e.getFile().getName() + ": " + ( ( e.getBytesComplete() / e.getBytesTotal() ) * 100 ) + "%" ) );
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer yellow" );
            }
        } );

        builder.setUploadSuccessHandler( new UploadSuccessHandler() {
            public void onUploadSuccess( UploadSuccessEvent e ) {
                symba.showEastWidget();
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer blue" );

                // now assign this file to the appropriate experimental step
                ( ( EditInvestigationTable ) symba.getCenterWidget() )
                        .assignFileToStep( e.getFile(), radioRowSelectedOnUpload );
            }
        } );

        builder.setUploadErrorHandler( new UploadErrorHandler() {
            public void onUploadError( UploadErrorEvent e ) {
                symba.showEastWidget();
                ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                        .addStyleName( fileIdToRow.get( e.getFile().getId() ), 0, "progressContainer red" );
                String message = e.getMessage();
                if ( message == null || message.trim().length() == 0 ) {
                    message = "buttonOne failed";
                }
                ( ( HTML ) symba.getEastWidget() ).setHTML(
                        ( ( HTML ) symba.getEastWidget() ).getText() + "<br />Upload error: " + e.getFile().getId() +
                                ", " + e.getFile().getName() + " / " + message );
                removeFile( e.getFile().getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonOne.startUpload( id );
                }
            }
        } );

        builder.setUploadURL( url );

        builder.setUploadCompleteHandler( new UploadCompleteHandler() {
            public void onUploadComplete( UploadCompleteEvent e ) {
                File f = e.getFile();
                removeFile( f.getId() );
                if ( files.size() > 0 ) {
                    String id = files.get( 0 ).getId();
                    symba.showEastWidget();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonOne.startUpload( id );
                }
            }
        } );

        builder.setFileQueuedHandler( new FileQueuedHandler() {
            public void onFileQueued( FileQueuedEvent event ) {
                files.add( event.getFile() );
            }
        } );

        builder.setDialogStartHandler( new DialogStartHandler() {
            public void onDialogStart() {
                radioRowSelectedOnUpload = ( ( EditInvestigationTable ) symba.getCenterWidget() ).getSelectedRadioRow();
            }
        } );

        builder.setFileDialogCompleteHandler( new FileDialogCompleteHandler() {
            public void onFileDialogComplete( FileDialogCompleteEvent e ) {
                if ( files.size() > 0 ) {
                    symba.showEastWidget();
                    // reset table variables
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().removeAllRows();
                    fileIdToRow = new HashMap<String, Integer>();
                    int fileIdToRowCount = 0;
                    // fill in fileIdToRow
                    for ( File file : files ) {
                        fileIdToRow.put( file.getId(), fileIdToRowCount++ );
                    }
                    // next, set the value in the appropriate table row.
                    String id = files.get( 0 ).getId();
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus()
                            .setWidget( fileIdToRow.get( id ), 0, new HTML( files.get( 0 ).getName() + ": 0%" ) );
                    ( ( HelpPanel ) symba.getEastWidget() ).getFileStatus().getCellFormatter()
                            .addStyleName( fileIdToRow.get( id ), 0, "progressContainer yellow" );
                    buttonOne.startUpload( id );
                }
            }
        } );
        buttonOne = builder.build();

    }

    private void removeFile( String id ) {
        for ( File ff : files ) {
            if ( ff.getId().equals( id ) ) {
                files.remove( ff );
            }
        }
    }
}
