package net.sourceforge.symba.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.panel.*;
import net.sourceforge.symba.web.shared.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.InvestigationDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditInvestigationView extends VerticalPanel {

    // todo disable all functions on entire page until file uploads are complete

    public static enum ViewType {
        NEW_INVESTIGATION, EXISTING_INVESTIGATION
    }

    public static final String INVESTIGATION_EXPLANATION = "An <strong>investigation</strong> consists of one or " +
            "more experiments. Investigations are containers for all experiments that relate to a " +
            "<strong>single</strong> hypothesis and conclusion.";

    private static enum SaveType {
        SAVE_ONLY, SET_AS_TEMPLATE
    }

    private final SymbaController controller;

    private final CheckBox completedCheckBox;
    private final CheckBox setAsTemplateCheckBox;
    private final Button saveButton;
    private final Button saveAndFinishButton;
    private final Button cancelButton;
    private final Button addSubStepButton;
    private final String addChildImageUrl;
    private final String copyStepImageUrl;

    private FlexTable stepsTable;
    private final InvestigationDetailsPanel investigationDetailsPanel;

    private final Investigation investigation;

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
     * @param symbaController       the symbaController panel for the entire interface
     * @param selectedInvestigation the investigation assigned to this view.
     * @param viewType              whether the view is of a new or a pre-existing investigation
     */
    public EditInvestigationView( SymbaController symbaController,
                                  Investigation selectedInvestigation,
                                  ViewType viewType ) {

        setWidth( "100%" );
        setSpacing( 5 );

        this.controller = symbaController;

        if ( selectedInvestigation != null ) {
            investigation = selectedInvestigation;
        } else {
            investigation = new Investigation();
            investigation.createId();
            investigation.getProvider().createId();
            viewType = ViewType.NEW_INVESTIGATION;
        }

        //
        // prepare all buttons, labels and check boxes.
        //
        Label overallTitle = new Label();
        HTML explanation = new HTML( INVESTIGATION_EXPLANATION );
        overallTitle.addStyleName( "header-title" );
        if ( viewType == ViewType.NEW_INVESTIGATION ) {
            overallTitle.setText( "Create New Investigation" );
        } else {
            overallTitle.setText( "View Existing Investigation" );
        }

        completedCheckBox = new CheckBox( "Freeze" );
        // todo add help message to this
        // "Check this box if your investigation is completely described. Checking this box will disallow any
        // further modifications to the investigation."
        completedCheckBox.addStyleName( "note" );
        if ( this.investigation.isCompleted() ) {
            completedCheckBox.setValue( true );
            completedCheckBox.setEnabled( false ); // todo allow them to un-set the completed flag
        }
        setAsTemplateCheckBox = new CheckBox( "Set As Template" );
        setAsTemplateCheckBox.addStyleName( "note" );
        if ( this.investigation.isTemplate() ) {
            setAsTemplateCheckBox.setValue( true );
        }

        HorizontalPanel savePanel = new HorizontalPanel();
        saveButton = new Button( "Save" );
        saveAndFinishButton = new Button( "Save and Finish" );
        savePanel.add( saveButton );
        savePanel.add( saveAndFinishButton );

        cancelButton = new Button( "Cancel" );
        addSubStepButton = new Button( "Add Top-Level Step" );
        if ( investigation.isReadOnly() ) {
            // disable buttons and boxes that allow modifications
            saveButton.setEnabled( false );
            saveAndFinishButton.setEnabled( false );
            addSubStepButton.setEnabled( false );
            setAsTemplateCheckBox.setEnabled( false );
            completedCheckBox.setEnabled( false );
        } else {
            // we need the "else" here if a template was previously shown, and therefore buttons were
            // previously disabled. We need to explicitly enable them.
            saveButton.setEnabled( true );
            saveAndFinishButton.setEnabled( true );
            addSubStepButton.setEnabled( true );
            setAsTemplateCheckBox.setEnabled( true );
            completedCheckBox.setEnabled( true );
        }
        cancelButton.setEnabled( true );

        //
        // prepare the top part of the page, where the details of the investigation are displayed
        //
        investigationDetailsPanel = new InvestigationDetailsPanel( controller );
        investigationDetailsPanel.createReadableDisplay( investigation );

        //
        // sort out image paths: must be done before displayStepData() is called to ensure paths are correct for the
        // display itself.
        //
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

        //
        // prepare the bottom part of the page, where the experimental steps are displayed.
        //
        stepsTable = new FlexTable();
        stepsTable.setCellSpacing( 5 );
        stepsTable.setCellPadding( 0 );
        displayStepData();

        //
        // Prepare all container panels
        //
        HorizontalPanel menuPanel = new HorizontalPanel();
        menuPanel.setSpacing( 10 );
        menuPanel.setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );
        menuPanel.add( savePanel );
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

        addHandlers();

        //
        // Put everything together in one view.
        //
        add( overallTitle );
        add( explanation );
        add( investigationDetailsPanel );
        add( protocolWrapper );
        if ( !investigation.isTemplate() ) {
            add( completedCheckBox );
        }
        add( setAsTemplateCheckBox );
        add( menuPanel );
    }

    // todo
    // upload URIs (must be on cisbclust) - once done, SyMBA checks the file is there and then sets it to read only.
    //

    //
    // initialising/clearing/resetting methods
    //

    private void addHandlers() {

        completedCheckBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            public void onValueChange( ValueChangeEvent<Boolean> booleanValueChangeEvent ) {
                investigation.setCompleted( completedCheckBox.getValue() );
                if ( completedCheckBox.getValue() ) {
                    // if the completed box is checked, this cannot also be made into a template.
                    // (The user must make a copy of the investigation first, which happens elsewhere.)
                    investigation.setTemplate( false );
                    setAsTemplateCheckBox.setValue( false );
                    setAsTemplateCheckBox.setEnabled( false );
                } else {
                    // if the completed box is switched to false, then we can re-enable the template box
                    setAsTemplateCheckBox.setEnabled( true );
                }
            }
        } );

        setAsTemplateCheckBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            public void onValueChange( ValueChangeEvent<Boolean> booleanValueChangeEvent ) {
                investigation.setTemplate( setAsTemplateCheckBox.getValue() );
                // if the template box is checked, this cannot also be marked as completed, as it makes no
                // logical sense.
                if ( setAsTemplateCheckBox.getValue() ) {
                    investigation.setCompleted( false );
                    completedCheckBox.setValue( false );
                    completedCheckBox.setEnabled( false );
                } else {
                    // if the template box is switched to false, then we can re-enable the completed box
                    completedCheckBox.setEnabled( true );
                }
            }
        } );

        saveAndFinishButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSave( true );
            }
        } );

        saveButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                doSave( false );
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
                    controller.setEastWidgetUserStatus( "<p>Modifications to <strong>" + investigation.getInvestigationTitle() +
                            "</strong> cancelled.</p>" );
                } else {
                    // no need to keep any file statuses at this point
                    controller.setEastWidgetUserStatus( "<p>Creation of new investigation cancelled.</p>" );
                }
                controller.setCenterWidgetAsListExperiments();
            }
        } );

    }

    private void setAsTemplate( final String id,
                                final String title,
                                final boolean finish ) {
        controller.getRpcService().setInvestigationAsTemplate( id,
                new AsyncCallback<ArrayList<InvestigationDetail>>() {
                    public void onFailure( Throwable throwable ) {
                        Window.alert( "Saving of Investigation " + title +
                                " as a template failed: " + Arrays.toString( throwable.getStackTrace() ) );
                        controller
                                .setEastWidgetUserStatus( "<p><strong>Saving of Investigation " + title +
                                "as a template failed.</p>" );
                    }

                    public void onSuccess( ArrayList<InvestigationDetail> results ) {
                        controller.setStoredInvestigationDetails( results );
                        // no need to keep any directions at this point
                        if ( finish ) {
                            controller.setCenterWidgetAsListExperiments();
                        } else {
                            controller.setCenterWidgetAsEditExperiment( id );
                        }
                        controller
                                .setEastWidgetUserStatus( "<p><strong>" + title + "</strong> has been set as a template.</p>" );
                    }
                } );
    }

    //
    // un-modifying (e.g. getting) methods
    //

    //
    // Methods which change the class variables or run RPC calls which modify server variables
    //

    /**
     * Should only be called from the [void displayStepData()] method which implements the Display interface
     *
     * @param data     the thing to display in the table
     * @param rowValue the value being passed through the steps hierarchy
     * @param depth    the hierarchical depth of the current step
     * @return the new value of the row count
     */
    public int displayStepData( List<ExperimentStepHolder> data,
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
            if ( !investigation.isReadOnly() ) {
                ClickHandler myHandler = new MakeEditableHandler( rowValue, depth + ActionType.SELECT.getValue() + 1,
                        investigation.isCompleted() );
                readableStepView = new ReadableStepView( holder.getCurrent().getTitle(),
                        holder.getCurrent().getFileInfo(), holder.getCurrent().getParameters(),
                        holder.getCurrent().getInputMaterials(), holder.getCurrent().getOutputMaterials(), myHandler );
            } else {
                readableStepView = new ReadableStepView( holder.getCurrent().getTitle(),
                        holder.getCurrent().getFileInfo(), holder.getCurrent().getParameters(),
                        holder.getCurrent().getInputMaterials(), holder.getCurrent().getOutputMaterials() );
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
                rowValue = displayStepData( holder.getCurrent().getChildren(), rowValue, depth + 1 );
            }
        }

        return rowValue;
    }

    /**
     * clears the existing display of experiment steps and starts re-writing from the beginning.
     */
    public void displayStepData() {
        stepsTable.removeAllRows();
        displayStepData( investigation.getExperiments(), 0, 0 );
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
        stepsTable.getCellFormatter().setHeight( rowValue, ActionType.COPY.getValue(), "30px" );
        stepsTable.getCellFormatter().setWidth( rowValue, ActionType.COPY.getValue(), "15px" );

//        RadioButton radio = new RadioButton( "fileSelector" );

        // as long as at least one radio button is selected, then it's OK to have the upload button enabled.
//        radio.addClickHandler( new ClickHandler() {
//            public void onClick( ClickEvent clickEvent ) {
//                HTMLTable.Cell cell = stepsTable.getCellForEvent( clickEvent );
//                selectedRadioRow = cell.getRowIndex();
//
//            }
//        } );
//        stepsTable.setWidget( rowValue, ActionType.SELECT.getValue(), radio );

    }

    private void doAddStep( int selectedRow ) {
        //todo allow multiple addition of steps
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.addExperimentStep( selectedRow );
            displayStepData();
        } else if ( selectedRow == ActionType.UNDEFINED.getValue() ) {
            investigation.addExperimentStep();
            displayStepData();
        } // do nothing if ActionType.IGNORE.getValue() : we should ignore such clicks.
    }

    private void doCopyStep( int selectedRow ) {
        if ( selectedRow >= ActionType.ADD.getValue() ) {
            investigation.deepExperimentCopy( selectedRow );
            displayStepData();
        } // do nothing if ActionType.IGNORE.getValue() or ActionType.UNDEFINED.getValue() : we should ignore such clicks.

    }

    private void doSave( boolean finish ) {

        if ( setAsTemplateCheckBox.getValue() ) {
            // remove any files associated with this investigation, as templates don't have files.
            // however, before doing this, check if the user really wants to make this a template, with all of
            // its consequences.
            // todo allow read-only investigations to be removed or modified by their owners only.
            boolean response = Window.confirm(
                    "Setting this Investigation as a template will first save its current state, REMOVE " +
                            "any links to files, and mark it as read-only. A template can then be copied " +
                            "by you and other users, thus sharing common aspects of " +
                            "Investigations. Are you sure you wish to continue?" );
            if ( response ) {
                doSave( SaveType.SET_AS_TEMPLATE, finish );
            } else {
                // no need to keep any directions at this point
                controller.setEastWidgetUserStatus( "<p>Saving as template cancelled.</p>" );
            }
        } else {
            doSave( SaveType.SAVE_ONLY, finish );
        }
    }

    private void doSave( final SaveType saveType,
                         final boolean finish ) {

        String emptyValues = investigationDetailsPanel.makeErrorMessages();
        if ( emptyValues.length() > 1 ) {
            Window.alert( "Error updating the following fields: " + emptyValues );
            return;
        }

        investigationDetailsPanel.updateModifiedDetails( investigation );
        investigation.setAllModified( false );

        // the experiment steps were saved as we went along, so nothing extra to do here.
        // the "completed" flag was saved as we went along, so nothing extra to do here.
        // the "template" flag was saved as we went along, so nothing extra to do here.

        // todo ensure Identifier has changed before this step, if necessary
        controller.getRpcService()
                .updateInvestigation( investigation, new AsyncCallback<ArrayList<InvestigationDetail>>() {
                    public void onSuccess( ArrayList<InvestigationDetail> updatedDetails ) {
                        final String title = investigation.getInvestigationTitle();
                        final String id = investigation.getId();
                        controller.setStoredInvestigationDetails( updatedDetails );
                        if ( finish ) {
                            controller.setCenterWidgetAsListExperiments();
                            controller.setEastWidgetUserStatus( "<p><strong>" + title + "</strong> saved.</p>" );
                        } else {
                            controller.setCenterWidgetAsEditExperiment( id );
                            controller.setEastWidgetUserStatus( "<p>Saving <strong>" + title + "</strong>...</p>" );
                        }

                        if ( saveType == SaveType.SET_AS_TEMPLATE ) {
                            setAsTemplate( id, title, finish );
                        }
                    }

                    public void onFailure( Throwable caught ) {
                        String savedTitle = "unknown title";
                        if ( investigation.getInvestigationTitle() != null &&
                                investigation.getInvestigationTitle().length() > 0 ) {
                            savedTitle = investigation.getInvestigationTitle();
                        }
                        Window.alert(
                                "Error updating investigation " + savedTitle + " stack trace: " + caught.toString() );
                        caught.printStackTrace( System.err );
                    }
                } );
    }

    private class MakeEditableHandler implements ClickHandler {
        private int row, column;
        private boolean completed;

        public MakeEditableHandler( int row,
                                    int column,
                                    boolean completed ) {
            this.row = row;
            this.column = column;
            this.completed = completed;
        }

        public void onClick( ClickEvent clickEvent ) {
            EditableStepView view = new EditableStepView( controller,
                    ( ReadableStepView ) stepsTable.getWidget( row, column ), stepsTable,
                    investigation, row, column, this, completed );
            view.getStepTitle().setFocus( true );

        }
    }

}
