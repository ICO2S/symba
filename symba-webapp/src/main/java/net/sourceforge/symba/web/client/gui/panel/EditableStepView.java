package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
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
        addNewParameterButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                parameterTable.addNewParameter();
            }
        } );

        Button saveStepButton = new Button( "Save This Step" );
        saveStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {

                // save the text in the parameters text boxes
                getParameterTable().savePanelValues();

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

            }
        } );


        add( stepTitle );
        add( new HTML( getFileNamesString() ) );
        add( addNewParameterButton );
        add( saveStepButton );
        add( parameterTable );
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
            parameters.add( parameter );
            SingleParameterPanel panel = new SingleParameterPanel( parameter );
            parameterPanels.add( panel );
            setWidget( parameterRowCount++, 0, panel );
        }

        public ArrayList<ExperimentParameter> getParameters() {
            return parameters;
        }

        public void savePanelValues() {
            int counter = 0;
            for ( SingleParameterPanel panel : parameterPanels ) {
                // in order to save, at least the first three text boxes must be non-empty
                if ( panel.getSubject().getText().length() > 0 && panel.getPredicate().getText().length() > 0 &&
                        panel.getObjectValue().getText().length() > 0 ) {
                    parameters.get( counter ).setSubject( panel.getSubject().getText() );
                    parameters.get( counter ).setPredicate( panel.getPredicate().getText() );
                    parameters.get( counter ).setObjectValue( panel.getObjectValue().getText() );
                    if ( panel.getUnit().getText().length() > 0 ) {
                        parameters.get( counter ).setUnit( panel.getUnit().getText() );
                    }
                }
                counter++;
            }
        }

        public void addNewParameter() {
            ExperimentParameter parameter = new ExperimentParameter();
            addParameter( parameter );
        }

        // An internal panel contains the step stepTitle

        private class SingleParameterPanel extends HorizontalPanel {
            private TextBox subject, predicate, objectValue, unit;

            public SingleParameterPanel( final ExperimentParameter parameter ) {
                setBorderWidth( 0 );
                setSpacing( 0 );
                setHorizontalAlignment( HorizontalPanel.ALIGN_LEFT );

                subject = new TextBox();
                subject.setText( parameter.getSubject() );
                add( subject );

                predicate = new TextBox();
                predicate.setText( parameter.getPredicate() );
                add( predicate );

                objectValue = new TextBox();
                objectValue.setText( parameter.getObjectValue() );
                add( objectValue );

                unit = new TextBox();
                unit.setText( parameter.getUnit() );
                add( unit );

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
        }

    }

}
