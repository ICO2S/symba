package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.stepsorter.ExperimentParameter;
import net.sourceforge.symba.web.shared.Investigation;

import java.util.ArrayList;

/**
 * This class holds all information for the current placement of the editable step, and for the values and
 * display structures present therein.
 */
public class EditableStepView extends VerticalPanel {
    private int editableRow, editableColumn;
    private TextBox stepTitle;
    private ArrayList<String> fileNames;
    private EditableStepParameterTable parameterTable;
    private ClickHandler myEditableHandler;

    public EditableStepView( ReadableStepView readableView,
                             final PopupPanel container,
                             final FlexTable tableToAddTo,
                             final Investigation investigationToAddTo,
                             int row,
                             int column,
                             final ClickHandler myEditableHandler ) {
        this.myEditableHandler = myEditableHandler;

        this.editableRow = row;
        this.editableColumn = column;
        stepTitle = new TextBox();
        stepTitle.setText( readableView.getStepTitle() );

        fileNames = readableView.getFileNames();
        parameterTable = new EditableStepParameterTable( readableView.getParameterTable() );
        Button addNewParameterButton = new Button( "Add Parameter" );
        Button saveStepButton = new Button( "Save This Step" );

        // a panel to separate out the parameter addition steps
        CaptionPanel parameterCaptionPanel = new CaptionPanel( "Parameters" );
        parameterCaptionPanel.setStyleName( "gwt-Label" );
        VerticalPanel parameterContentPanel = new VerticalPanel();
        parameterCaptionPanel.add( parameterContentPanel );
        parameterContentPanel.add( parameterTable );
        parameterContentPanel.add( addNewParameterButton );


        // add all handlers
        addNewParameterButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                parameterTable.addNewParameter();
            }
        } );

        saveStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {

                // save the text in the parameters text boxes
                if ( !getParameterTable().savePanelValues() ) {
                    return;
                }

                Object[] values = investigationToAddTo
                        .setExperimentStepInfo( editableRow, stepTitle.getText(),
                                getParameterTable().getParameters() );


                // Set style based on change, then send the stepTitle to setReadOnly.
                Boolean modified = false;
                if ( values != null && values[0] != null && ( ( String ) values[0] ).length() > 0 ) {
                    modified = ( Boolean ) values[1];
                }
                // todo this style is currently only be applied to one cell until the entire table is redrawn, e.g. from an added step
                if ( modified ) {
                    tableToAddTo.getCellFormatter().addStyleName( editableRow, editableColumn, "cell-modified" );
                } else {
                    tableToAddTo.getCellFormatter()
                            .removeStyleName( editableRow, editableColumn, "cell-modified" );

                }
                setReadOnly( tableToAddTo );
                container.hide();

            }
        } );


        add( stepTitle );
        add( new HTML( getFileNamesString() ) );
        add( parameterCaptionPanel );
        add( saveStepButton );
    }


    /**
     * We are not doing any application logic - we're simply taking the value from our data as provided
     * by the presenter (that's what currently saved) and putting it into a read-only text cell.
     * <p/>
     *
     * @param tableToAddTo the table to add the widget to at the stored positions
     */
    public void setReadOnly( FlexTable tableToAddTo ) {

        // here, the modification to the experiment marked with "isModified" is inaccessible, so just check
        // that the value is different from what was in the

        ReadableStepView readable = new ReadableStepView( getStepTitle().getValue(), getFileNames(),
                getParameterTable().getParameters(), myEditableHandler );
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

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    private String getFileNamesString() {
        String value = "";
        for ( String file : fileNames ) {
            value = value + file + "; ";
        }
        return value;
    }

    public EditableStepParameterTable getParameterTable() {
        return parameterTable;
    }

    private class EditableStepParameterTable extends FlexTable {
        ArrayList<ExperimentParameter> parameters;
        ArrayList<SingleParameterPanel> parameterPanels;
        int parameterRowCount;

        private EditableStepParameterTable( ReadableStepView.ReadableStepParameterTable readable ) {
            parameters = new ArrayList<ExperimentParameter>();
            parameterPanels = new ArrayList<SingleParameterPanel>();
            parameterRowCount = 0;

            for ( ExperimentParameter parameter : readable.getParameters() ) {
                addParameter( parameter );
            }
        }

        private void addParameter( ExperimentParameter parameter ) {
            SingleParameterPanel panel = new SingleParameterPanel( parameter );
            parameterPanels.add( panel );
            setWidget( parameterRowCount++, 0, panel );
        }

        public ArrayList<ExperimentParameter> getParameters() {
            return parameters;
        }

        /**
         * Delete all current values of parameters, and fill it with what is in the parameterPanels.
         * Let the user know what is missing if partially filled in.
         */
        public boolean savePanelValues() {

            String emptyValues = makeErrorMessages( parameterPanels );
            if ( emptyValues.length() > 1 ) {
                Window.alert( "You must provide a value for all fields except the Unit, which is optional." +
                        "\nThe following fields are missing for at least one parameter:\n" + emptyValues );
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
                parameters.add( p );
            }

            return true;
        }

        private String makeErrorMessages( ArrayList<SingleParameterPanel> parameterPanels ) {
            // we can stop as soon as all three fields have are missing in at least one parameter
            boolean subjectMissing = false, objectValueMissing = false, predicateValueMissing = false;
            String emptyValues = "\n";
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
                if ( !objectValueMissing && panel.getObjectValue().getText().trim().length() == 0 ) {
                    objectValueMissing = true;
                    emptyValues += "Value\n";
                }
                if ( subjectMissing && objectValueMissing && predicateValueMissing ) {
                    break;
                }
            }
            return emptyValues;

        }

        public void addNewParameter() {
            addParameter( new ExperimentParameter() );
        }

        // An internal panel contains the step stepTitle

        private class SingleParameterPanel extends HorizontalPanel {
            private TextBox subject, predicate, objectValue, unit;

            public SingleParameterPanel( final ExperimentParameter parameter ) {
                setBorderWidth( 0 );
                setSpacing( 0 );
                setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );

                subject = new TextBox();
                ParameterCaptionBox sPanel = new ParameterCaptionBox( "Parameter Name, e.g. Camera", subject,
                        parameter.getSubject() );
                add( sPanel );

                predicate = new TextBox();
                ParameterCaptionBox pPanel = new ParameterCaptionBox( "Relationship, e.g. has brand", predicate,
                        parameter.getPredicate() );
                add( pPanel );

                objectValue = new TextBox();
                ParameterCaptionBox oPanel = new ParameterCaptionBox( "Value, e.g. Canon 5D", objectValue,
                        parameter.getSubject() );
                add( oPanel );

                unit = new TextBox();
                ParameterCaptionBox uPanel = new ParameterCaptionBox( "Units (optional), e.g. centimetres", unit,
                        parameter.getSubject() );
                add( uPanel );

                // do not allow modifications to anything other than the objectValue and the unit if this
                // parameter has been copied from a template.
                if ( !parameter.isFullyWriteable() ) {
                    subject.setEnabled( false );
                    predicate.setEnabled( false );
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

            private class ParameterCaptionBox extends CaptionPanel {
                private ParameterCaptionBox( String captionText,
                                             final TextBox box,
                                             String boxText ) {
                    super( captionText );
                    addStyleName( "parameter-title" );
                    box.setText( boxText );
                    add( box );
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
