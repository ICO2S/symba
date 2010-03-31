package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sourceforge.symba.web.client.gui.CollapsibleTable;
import net.sourceforge.symba.web.shared.ExperimentParameter;
import net.sourceforge.symba.web.shared.Material;

import java.util.ArrayList;


public class ReadableStepView extends VerticalPanel {
    private String stepTitle;
    private ArrayList<String> fileNames;
    private ReadableStepParameterTable parameterTable;
    private ReadableStepMaterialTable inputMaterialTable, outputMaterialTable;


    public ReadableStepView( String stepTitle,
                             ArrayList<String> fileNames,
                             ArrayList<ExperimentParameter> parameters,
                             ArrayList<Material> inputs,
                             ArrayList<Material> outputs ) {

        parameterTable = new ReadableStepParameterTable( parameters );
        inputMaterialTable = new ReadableStepMaterialTable( inputs, "input" );
        outputMaterialTable = new ReadableStepMaterialTable( outputs, "output" );

        this.stepTitle = stepTitle;
        this.fileNames = fileNames;
        setupView();
    }

    public ReadableStepView( String stepTitle,
                             ArrayList<String> fileNames,
                             ArrayList<ExperimentParameter> parameters,
                             ArrayList<Material> inputs,
                             ArrayList<Material> outputs,
                             ClickHandler myEditableHandler ) {

        parameterTable = new ReadableStepParameterTable( parameters, myEditableHandler );
        inputMaterialTable = new ReadableStepMaterialTable( inputs, "input", myEditableHandler );
        outputMaterialTable = new ReadableStepMaterialTable( outputs, "output", myEditableHandler );

        this.stepTitle = stepTitle;
        this.fileNames = fileNames;
        setupView( myEditableHandler );
    }

    private void setupView() {
        setupView( null );
    }

    private void setupView( ClickHandler myEditableHandler ) {

        Label label = new Label( this.stepTitle );
        add( label );

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing( 5 );
        final Label fileLabel = new Label( displayFileNameCount() );
        final Label actionLabel = new Label( "(expand)" );
        actionLabel.addStyleName( "clickable-text" );
        actionLabel.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( actionLabel.getText().equals( "(expand)" ) ) {
                    actionLabel.setText( "(collapse)" );
                    fileLabel.setText( displayFileNames() );
                } else {
                    actionLabel.setText( "(expand)" );
                    fileLabel.setText( displayFileNameCount() );
                }
            }
        } );
        if ( myEditableHandler != null ) {
            label.addClickHandler( myEditableHandler );
            label.addStyleName( "clickable-text" );
            fileLabel.addClickHandler( myEditableHandler );
            fileLabel.addStyleName( "clickable-text" );
        }

        if ( fileNames.size() > 0 ) {
            hPanel.add( fileLabel );
            hPanel.add( actionLabel );
            add( hPanel );
        }

        add( parameterTable );
        add( inputMaterialTable );
        add( outputMaterialTable );
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public ReadableStepParameterTable getParameterTable() {
        return parameterTable;
    }

    public ReadableStepMaterialTable getInputMaterialTable() {
        return inputMaterialTable;
    }

    public ReadableStepMaterialTable getOutputMaterialTable() {
        return outputMaterialTable;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public String displayFileNames() {
        String text = "";
        for ( String file : fileNames ) {
            text = text + file + "; ";
        }
        return text;
    }

    private String displayFileNameCount() {
        if ( fileNames.size() == 1 ) {
            return "1 data file ";
        } else if ( fileNames.size() > 1 ) {
            return fileNames.size() + " data files ";
        }

        return "";
    }


    public class ReadableStepParameterTable extends CollapsibleTable {
        private ReadableStepParameterTable( ArrayList list ) {
            super( list, "parameter" );
        }

        private ReadableStepParameterTable( ArrayList list,
                                            final ClickHandler myEditableHandler ) {
            super( list, "parameter", myEditableHandler );
        }

        @Override
        protected void displayList( ClickHandler myEditableHandler ) {

            for ( Object item : getList() ) {
                ExperimentParameter parameter = ( ExperimentParameter ) item;
                Label label = new Label(
                        parameter.getSubject() + " : " + parameter.getPredicate() + " : " +
                                parameter.getObjectValue() + " : " + parameter.getUnit() );
                int currentRow = getRowCount();
                setWidget( currentRow, 0, label );
                if ( myEditableHandler != null ) {
                    label.addClickHandler( myEditableHandler );
                    label.addStyleName( "clickable-text" );
                }
            }
        }
        
    }

    public class ReadableStepMaterialTable extends CollapsibleTable {
        private ReadableStepMaterialTable( ArrayList list,
                                           String listType ) {
            super( list, listType );
        }

        private ReadableStepMaterialTable( ArrayList list,
                                           String listType,
                                           final ClickHandler myEditableHandler ) {
            super( list, listType, myEditableHandler );
        }

        @Override
        protected void displayList( ClickHandler myEditableHandler ) {
            for ( Object item : getList() ) {
                Material material = ( Material ) item;
                String description = material.getDescription();
                if ( description.length() > 20 ) {
                    description = description.substring( 0, 20 ) + "...";
                }
                Label label = new Label( material.getName() + " : " + description );
                int currentRow = getRowCount();
                setWidget( currentRow, 0, label );
                if ( myEditableHandler != null ) {
                    label.addClickHandler( myEditableHandler );
                    label.addStyleName( "clickable-text" );
                }
            }
        }

    }

}
