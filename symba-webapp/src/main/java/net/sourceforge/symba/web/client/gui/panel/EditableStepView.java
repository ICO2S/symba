package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.gui.handlers.ToolTip;
import net.sourceforge.symba.web.shared.ExperimentParameter;
import net.sourceforge.symba.web.shared.Investigation;
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class holds all information for the current placement of the editable step, and for the values and
 * display structures present therein.
 */
public class EditableStepView extends PopupPanel {
    private int editableRow, editableColumn;
    private TextBox stepTitle;
    private EditableStepParameterTable parameterTable;
    private ClickHandler myEditableHandler;
    private MaterialListPanel inputMaterialPanel, outputMaterialPanel;
//    private FileForm fileForm;
//    private GwtUploadFile fileForm;
    private Investigation investigation;

    public EditableStepView( final SymbaController controller,
                             ReadableStepView readableView,
                             final FlexTable tableToAddTo,
                             final Investigation investigation,
                             int row,
                             int column,
                             final ClickHandler myEditableHandler,
                             final boolean completed ) {
        super( false ); // turn auto-hide off
        setPopupPositionAndShow( new PopupPanel.PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                int top = ( Window.getClientHeight() - offsetHeight ) / 2;
                setPopupPosition( 0, top );
            }
        } );


        this.investigation = investigation;

        VerticalPanel contents = new VerticalPanel();
        contents.setSpacing( 5 );

        this.myEditableHandler = myEditableHandler;
        this.editableRow = row;
        this.editableColumn = column;

        //
        // step title
        //
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing( 5 );
        stepTitle = new TextBox();
        stepTitle.setText( readableView.getStepTitle() );
        final Label legend = new Label( "Title of Step: " );
        legend.addStyleName( "textbox-legend" );
        hPanel.add( legend );
        hPanel.add( stepTitle );

        //
        // files
        //
        // we do not want to manipulate the calling set of files directly - those should
        // only be changed upon saving of the step.
        CaptionPanel outputDataCaptionPanel = new CaptionPanel( "Output Data" );
        outputDataCaptionPanel.setStyleName( "captionpanel-border" );
        final GwtUploadFile fileForm = new GwtUploadFile( readableView.getFileInfo() );
        fileForm.setTitle( "Output Data" );
        outputDataCaptionPanel.add( fileForm );
//        fileForm = new FileForm( readableView.getFileInfo() );

        //
        // parameters
        //
        parameterTable = new EditableStepParameterTable( readableView.getParameterTable().getList() );
        Label addNewParameterLabel = new Label( "Add Parameter" );
        addNewParameterLabel.addStyleName( "clickable-text" );
        // a panel to separate out the parameter addition steps
        CaptionPanel parameterCaptionPanel = new CaptionPanel( "Parameters" );
        parameterCaptionPanel.setStyleName( "captionpanel-border" );
        VerticalPanel parameterContentPanel = new VerticalPanel();
        parameterCaptionPanel.add( parameterContentPanel );
        parameterContentPanel.add( parameterTable );
        parameterContentPanel.add( addNewParameterLabel );

        //
        // materials
        //
        inputMaterialPanel = new MaterialListPanel( controller, "input",
                readableView.getInputMaterialTable().getList(), MaterialListPanel.ViewType.ASSIGN_TO_EXPERIMENT );
        outputMaterialPanel = new MaterialListPanel( controller, "output",
                readableView.getOutputMaterialTable().getList(), MaterialListPanel.ViewType.ASSIGN_TO_EXPERIMENT );

        CaptionPanel inputMaterialCaptionPanel = new CaptionPanel( "Input Materials" );
        inputMaterialCaptionPanel.setStyleName( "captionpanel-border" );
        inputMaterialCaptionPanel.add( inputMaterialPanel );

        CaptionPanel outputMaterialCaptionPanel = new CaptionPanel( "Output Materials" );
        outputMaterialCaptionPanel.setStyleName( "captionpanel-border" );
        outputMaterialCaptionPanel.add( outputMaterialPanel );

        //
        // Buttons
        //
        Button saveStepButton = new Button( "Accept Changes" );
        Button cancelStepButton = new Button( "Cancel Changes" );
        HorizontalPanel actionButtonsPanel = new HorizontalPanel();
        actionButtonsPanel.add( saveStepButton );
        actionButtonsPanel.add( cancelStepButton );

        //
        // All handlers
        //
        addNewParameterLabel.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                parameterTable.addNewParameter();
            }
        } );

        saveStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                saveStep( completed, controller.getStoredMaterials(), fileForm, tableToAddTo );
            }
        } );

        cancelStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                hide();
            }
        } );

        stepTitle.addKeyPressHandler( new KeyPressHandler() {
            public void onKeyPress( KeyPressEvent event ) {
                if ( event.getCharCode() == KeyCodes.KEY_ENTER ) {
                    saveStep( completed, controller.getStoredMaterials(), fileForm, tableToAddTo );
                }
            }
        } );

        //
        // positioning
        //
        contents.add( hPanel );
        contents.add( outputDataCaptionPanel );
        contents.add( parameterCaptionPanel );
        contents.add( inputMaterialCaptionPanel );
        contents.add( outputMaterialCaptionPanel );
        contents.add( actionButtonsPanel );
        add( contents );
        show();
    }

    private void saveStep( boolean completed,
                           final HashMap<String, Material> storedMaterials,
                           GwtUploadFile fileForm,
                           FlexTable tableToAddTo ) {
        // save the text in the parameters text boxes
        if ( !getParameterTable().savePanelValues( completed ) ) {
            return;
        }

        // If the materials have changed at all, save the selected materials: input and output
        ArrayList<Material> inputs = inputMaterialPanel.getOriginallySelectedMaterials();
        ArrayList<Material> outputs = outputMaterialPanel.getOriginallySelectedMaterials();

        if ( inputMaterialPanel.hasVisibleList() ) {
            inputs = new ArrayList<Material>();
            for ( String id : inputMaterialPanel.getSelectedMaterialIds() ) {
                inputs.add( storedMaterials.get( id ) );
            }
        }
        if ( outputMaterialPanel.hasVisibleList() ) {
            outputs = new ArrayList<Material>();
            for ( String id : outputMaterialPanel.getSelectedMaterialIds() ) {
                outputs.add( storedMaterials.get( id ) );
            }
        }

        Object[] values = investigation
                .setExperimentStepInfo( editableRow, stepTitle.getText(),
                        getParameterTable().getParameters(), inputs, outputs, fileForm.getFileInfo() );

        // Set style based on change, then send the stepTitle to setReadOnly.
        Boolean modified = false;
        if ( values.length == 2 && values[0] != null && values[1] != null &&
                ( ( String ) values[0] ).length() > 0 ) {
            modified = ( Boolean ) values[1];
        }
        // todo this style is currently only be applied to one cell until the entire table is redrawn, e.g. from an added step
        if ( modified ) {
            tableToAddTo.getCellFormatter().addStyleName( editableRow, editableColumn, "cell-modified" );
        } else {
            tableToAddTo.getCellFormatter()
                    .removeStyleName( editableRow, editableColumn, "cell-modified" );

        }
        setReadOnly( tableToAddTo, inputs, outputs, fileForm );
        hide();

    }


    /**
     * We are not doing any application logic - we're simply taking the value from our data as provided
     * by the presenter (that's what currently saved) and putting it into a read-only text cell.
     * <p/>
     *
     * @param tableToAddTo the table to add the widget to at the stored positions
     * @param inputs       the recently-added input materials
     * @param outputs      the recently-added output materials
     * @param fileForm     the form containing the new files for this step
     */
    public void setReadOnly( FlexTable tableToAddTo,
                             ArrayList<Material> inputs,
                             ArrayList<Material> outputs,
                             GwtUploadFile fileForm ) {

        // here, the modification to the experiment marked with "isModified" is inaccessible, so just check
        // that the value is different from what was in the

        ReadableStepView readable = new ReadableStepView( getStepTitle().getValue(), fileForm.getFileInfo(),
                getParameterTable().getParameters(), inputs, outputs, myEditableHandler );
        tableToAddTo.setWidget( getEditableRow(), getEditableColumn(), readable );
    }

    public int getEditableRow() {
        return editableRow;
    }

    public int getEditableColumn() {
        return editableColumn;
    }

    public TextBox getStepTitle() {
        return stepTitle;
    }

    public EditableStepParameterTable getParameterTable() {
        return parameterTable;
    }

    private class EditableStepParameterTable extends FlexTable {
        ArrayList<ExperimentParameter> parameters;
        ArrayList<SingleParameterPanel> parameterPanels;
        int parameterRowCount;

        private EditableStepParameterTable( ArrayList<ExperimentParameter> parameters ) {
            this.parameters = parameters;
            parameterPanels = new ArrayList<SingleParameterPanel>();
            parameterRowCount = 0;

            boolean setFocusOnFirst = true;
            for ( ExperimentParameter parameter : parameters ) {
                addSingleParameterRow( parameter, setFocusOnFirst );
                if ( setFocusOnFirst ) {
                    setFocusOnFirst = false;
                }
            }
        }

        private void addSingleParameterRow( ExperimentParameter parameter,
                                            boolean setFocus ) {
            final SingleParameterPanel panel = new SingleParameterPanel( parameterRowCount, parameter, setFocus );
            parameterPanels.add( panel );
            setWidget( parameterRowCount, 0, panel );

            final Button removeButton = new Button( "X" );
            removeButton.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    // for some reason removeRow(parameterRowCount) doesn't remove the row, so widgets need to be
                    // removed separately
                    remove( panel );
                    remove( removeButton );
                    parameterPanels.remove( panel );
                }
            } );

            setWidget( parameterRowCount, 1, removeButton );

            panel.getSubject().setFocus( true );

            parameterRowCount++;

        }

        public ArrayList<ExperimentParameter> getParameters() {
            return parameters;
        }

        /**
         * Delete all current values of parameters, and fill it with what is in the parameterPanels.
         * Let the user know what is missing if partially filled in.
         *
         * @param completed true if the user is trying to set the current Investigation as completed.
         * @return true if at least one parameter value was saved, false otherwise (e.g. due to validation error)
         */
        public boolean savePanelValues( boolean completed ) {

            String emptyValues = makeErrorMessages( parameterPanels, completed );
            if ( emptyValues.length() > 0 ) {
                Window.alert( "At least one parameter is missing a value for the following fields:\n" + emptyValues );
                return false;
            }

            parameters.clear();
            for ( SingleParameterPanel panel : parameterPanels ) {
                // We've already validated all rows. Just add.
                ExperimentParameter p = new ExperimentParameter();
                p.setSubject( panel.getSubject().getText() );
                p.setPredicate( panel.getPredicate().getText() );
                p.setObjectValue( panel.getObjectValue().getText() );
                if ( panel.getUnit().getText().length() > 0 ) {
                    p.setUnit( panel.getUnit().getText() );
                }
                p.setMeasurementType( panel.getMeasure() );
                parameters.add( p );
            }

            return true;
        }

        /**
         * Creates an error message if any required fields are missing.
         *
         * @param parameterPanels the panels to check
         * @param completed       true if the user is trying to set the current Investigation as completed. In this case,
         *                        every parameter must have an objectValue, whereas in other cases it is not required.
         * @return the error message, or "" if no errors.
         */
        private String makeErrorMessages( ArrayList<SingleParameterPanel> parameterPanels,
                                          boolean completed ) {
            // we can stop as soon as the first two fields are missing in at least one parameter, and as soon as
            // at least one parameter does not have its measurement type chosen.
            boolean subjectMissing = false, predicateValueMissing = false, radioMissing = false, objectMissing = false;
            String emptyValues = "";
            for ( SingleParameterPanel panel : parameterPanels ) {
                // there must be a nonzero value in every non-unit field (the unit is optional).
                if ( !subjectMissing && panel.getSubject().getText().trim().length() == 0 ) {
                    subjectMissing = true;
                    emptyValues += "Parameter Name\n";
                }
                if ( !predicateValueMissing && panel.getPredicate().getText().trim().length() == 0 ) {
                    predicateValueMissing = true;
                    emptyValues += "Relationship\n";
                }
                if ( !radioMissing && panel.getObjectValue().getText().trim().length() > 0 &&
                        panel.getMeasure() == InputValidator.MeasurementType.UNKNOWN ) {
                    radioMissing = true;
                    emptyValues += "Measurement Type (number, true/false, or word/phrase for each parameter)\n";
                }
                if ( completed ) {
                    if ( !objectMissing && panel.getObjectValue().getText().trim().length() == 0 ) {
                        objectMissing = true;
                        emptyValues += "Parameter Value\n";
                    }
                }
                if ( !completed && subjectMissing && predicateValueMissing && radioMissing ) {
                    break;
                } else if ( completed && subjectMissing && predicateValueMissing && radioMissing && objectMissing ) {
                    break;
                }
            }
            if ( emptyValues.length() > 0 ) {
                emptyValues = "\n" + emptyValues;
            }
            return emptyValues;
        }

        public void addNewParameter() {
            addSingleParameterRow( new ExperimentParameter(), true );
        }

        // An internal panel contains the step stepTitle

        private class SingleParameterPanel extends HorizontalPanel {
            private TextBox subject, predicate, objectValue, unit;
            private InputValidator.MeasurementType measure;
            private RadioButton number, trueOrFalse, phrase;
            private ToolTip measurementTip;
            private VerticalPanel radioPanel;

            public SingleParameterPanel( final int counter,
                                         final ExperimentParameter parameter,
                                         boolean focused ) {
                setBorderWidth( 0 );
                setSpacing( 0 );
                setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );

                radioPanel = new VerticalPanel();

                measure = InputValidator.MeasurementType.UNKNOWN;
                number = new RadioButton( "measurementGroup" + counter, "number" );
                addRadioParameterHandlers( InputValidator.MeasurementType.ATOMIC, number );


                trueOrFalse = new RadioButton( "measurementGroup" + counter, "true/false" );
                addRadioParameterHandlers( InputValidator.MeasurementType.BOOLEAN, trueOrFalse );

                phrase = new RadioButton( "measurementGroup" + counter, "word" );
                addRadioParameterHandlers( InputValidator.MeasurementType.COMPLEX, phrase );

                radioPanel.add( number );
                radioPanel.add( trueOrFalse );
                radioPanel.add( phrase );
                radioPanel.setVisible( false );

                subject = new TextBox();
                ParameterCaptionBox sPanel = new ParameterCaptionBox( "Parameter Name, e.g. Camera", subject,
                        parameter.getSubject(), true );
                subject.setFocus( focused );

                predicate = new TextBox();
                ParameterCaptionBox pPanel = new ParameterCaptionBox( "Relationship, e.g. has brand", predicate,
                        parameter.getPredicate(), true );

                objectValue = new TextBox();
                ParameterCaptionBox oPanel = new ParameterCaptionBox( "Value, e.g. Canon 5D", objectValue,
                        parameter.getObjectValue(), false );
                if ( parameter.getObjectValue().length() > 0 ) {
                    setMeasureAndRadio( objectValue.getText() );
                }
                // in addition to the standard functionality provided by the ParameterCaptionBox, also check
                // if we can tell the type of parameter. If we cannot, then it becomes a complex value
                objectValue.addBlurHandler( new BlurHandler() {
                    public void onBlur( BlurEvent event ) {
                        if ( objectValue.getText().trim().length() > 0 ) {
                            setMeasureAndRadio( objectValue.getText() );
                        } else {
                            radioPanel.setVisible( false );
                            number.setValue( false );
                            trueOrFalse.setValue( false );
                            phrase.setValue( false );
                        }
                    }
                } );

                unit = new TextBox();
                ParameterCaptionBox uPanel = new ParameterCaptionBox( "Units (optional), e.g. centimetres", unit,
                        parameter.getUnit(), false );

                // do not allow modifications to anything other than the objectValue and the unit if this
                // parameter has been copied from a template.
                if ( !parameter.isFullyWriteable() ) {
                    subject.setEnabled( false );
                    predicate.setEnabled( false );
                }

                add( sPanel );
                add( pPanel );
                add( oPanel );
                add( uPanel );
                add( radioPanel );
            }

            private void addRadioParameterHandlers( final InputValidator.MeasurementType type,
                                                    final RadioButton currentButton ) {
                currentButton.addClickHandler( new ClickHandler() {
                    public void onClick( ClickEvent event ) {
                        measure = type;
                    }
                } );
                currentButton.addMouseOverHandler( new MouseOverHandler() {
                    public void onMouseOver( MouseOverEvent event ) {
                        if ( measurementTip != null ) {
                            measurementTip.hide(); // ensure any old value is not present
                        }
                        if ( currentButton.getValue() ) {
                            measurementTip = new ToolTip( currentButton,
                                    InputValidator.measurementMessages.get( measure ) );
                            measurementTip.show();
                        }
                    }
                } );
                currentButton.addMouseOutHandler( new MouseOutHandler() {
                    public void onMouseOut( MouseOutEvent event ) {
                        measurementTip.hide();
                    }
                } );
            }

            private void setMeasureAndRadio( String text ) {
                radioPanel.setVisible( true );
                measure = InputValidator.measurementTypeChecker( text );
                if ( measure == InputValidator.MeasurementType.ATOMIC ) {
                    number.setValue( true );
                } else if ( measure == InputValidator.MeasurementType.BOOLEAN ) {
                    trueOrFalse.setValue( true );
                } else if ( measure == InputValidator.MeasurementType.COMPLEX ) {
                    phrase.setValue( true );
                }
            }

            public TextBox getSubject() {
                return subject;
            }

            public TextBox getPredicate() {
                return predicate;
            }

            public TextBox getObjectValue() {
                return objectValue;
            }

            public TextBox getUnit() {
                return unit;
            }

            public InputValidator.MeasurementType getMeasure() {
                return measure;
            }

            private class ParameterCaptionBox extends CaptionPanel {
                private ParameterCaptionBox( String captionText,
                                             final TextBox box,
                                             String boxText,
                                             boolean nonEmpty ) {
                    super( captionText );
                    addStyleName( "parameter-title" );
                    addStyleName( "captionpanel-border" );
                    box.setText( boxText );
                    add( box );
                    if ( nonEmpty ) {
                        box.addBlurHandler( new BlurHandler() {
                            public void onBlur( BlurEvent event ) {
                                InputValidator.nonEmptyTextBoxStyle( box );
                            }
                        } );
                    }
                }
            }
        }
    }
}
