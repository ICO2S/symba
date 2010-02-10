package net.sourceforge.symba.web.client.gui;

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
public class EditableStepPanel extends VerticalPanel {
    private int editableRow, editableColumn;
    private TextBox stepTitle;
    private ArrayList<String> fileNames;
    private EditableStepParameterTable parameterTable;
    private ClickHandler myEditableHandler;

    public EditableStepPanel( ReadableStepPanel readablePanel,
                              final FlexTable tableToAddTo,
                              final Investigation investigationToAddTo,
                              int row,
                              int column,
                              final ClickHandler myEditableHandler ) {
        this.myEditableHandler = myEditableHandler;

        this.editableRow = row;
        this.editableColumn = column;
        stepTitle = new TextBox();
        stepTitle.setText( readablePanel.getStepTitle() );

        fileNames = readablePanel.getFileNames();
        parameterTable = new EditableStepParameterTable( readablePanel.getParameterTable() );
        Button addNewParameterButton = new Button( "Add Parameter" );
        addNewParameterButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                parameterTable.addNewParameter();
            }
        } );

        Button saveStepButton = new Button( "Save This Step" );
        saveStepButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
//                    int coordinates[] = getClickedCoordinatesForSave( clickEvent, tableToAddTo );

                // do nothing if there is no useful information in the currently editable panel, e.g. there is no such panel
                // at present.
//                    if ( coordinates[0] == ActionType.UNDEFINED.getValue() ||
//                            coordinates[1] == ActionType.UNDEFINED.getValue() ) {
//                        return;
//                    }

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

        ReadableStepPanel readable = new ReadableStepPanel( getStepTitle().getValue(), getFileNames(),
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

        private EditableStepParameterTable( ReadableStepPanel.ReadableStepParameterTable readable ) {
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
//                    subject.addBlurHandler( new BlurHandler() {
//                        public void onBlur( BlurEvent blurEvent ) {
//                            parameter.setSubject( subject.getText() );
//                        }
//                    } );
                add( subject );

                predicate = new TextBox();
                predicate.setText( parameter.getPredicate() );
//                    predicate.addBlurHandler( new BlurHandler() {
//                        public void onBlur( BlurEvent blurEvent ) {
//                            parameter.setSubject( predicate.getText() );
//                        }
//                    } );
                add( predicate );

                objectValue = new TextBox();
                objectValue.setText( parameter.getObjectValue() );
//                    objectValue.addBlurHandler( new BlurHandler() {
//                        public void onBlur( BlurEvent blurEvent ) {
//                            parameter.setSubject( objectValue.getText() );
//                        }
//                    } );
                add( objectValue );

                unit = new TextBox();
                unit.setText( parameter.getUnit() );
//                    unit.addBlurHandler( new BlurHandler() {
//                        public void onBlur( BlurEvent blurEvent ) {
//                            parameter.setSubject( unit.getText() );
//                        }
//                    } );
                add( unit );
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
