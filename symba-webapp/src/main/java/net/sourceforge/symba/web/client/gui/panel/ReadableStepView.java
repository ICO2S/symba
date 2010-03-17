package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sourceforge.symba.web.client.stepsorter.ExperimentParameter;

import java.util.ArrayList;


public class ReadableStepView extends VerticalPanel {
    private String stepTitle;
    private ArrayList<String> fileNames;
    private ReadableStepParameterTable parameterTable;


    public ReadableStepView(
            String stepTitle,
            ArrayList<String> fileNames,
            ArrayList<ExperimentParameter> parameters ) {

        parameterTable = new ReadableStepParameterTable( parameters );

        this.stepTitle = stepTitle;
        this.fileNames = fileNames;
        setupView();
    }

    public ReadableStepView(
            String stepTitle,
            ArrayList<String> fileNames,
            ArrayList<ExperimentParameter> parameters,
            ClickHandler myEditableHandler ) {

        parameterTable = new ReadableStepParameterTable( parameters, myEditableHandler );

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
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public ReadableStepParameterTable getParameterTable() {
        return parameterTable;
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


    public class ReadableStepParameterTable extends FlexTable {
        // each row is plain text
        private ArrayList<ExperimentParameter> parameters;
        private int rowCount;

        private ReadableStepParameterTable( ArrayList<ExperimentParameter> parameters ) {
            this( parameters, null );
        }

        private ReadableStepParameterTable( ArrayList<ExperimentParameter> parameters,
                                            final ClickHandler myEditableHandler ) {
            rowCount = 1; // start at 1, as 0 is reserved for the summary of the parameters
            this.parameters = parameters;

            HorizontalPanel hPanel = new HorizontalPanel();
            hPanel.setSpacing( 5 );
            final Label parameterSummaryLabel = new Label( displayParameterCount() );
            final Label actionLabel = new Label( "(expand)" );
            actionLabel.addStyleName( "clickable-text" );
            actionLabel.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( actionLabel.getText().equals( "(expand)" ) ) {
                        actionLabel.setText( "(collapse)" );
                        displayParameters( myEditableHandler );
                    } else {
                        actionLabel.setText( "(expand)" );
                        hideParameters();
                    }
                }
            } );
            if ( myEditableHandler != null ) {
                parameterSummaryLabel.addClickHandler( myEditableHandler );
                parameterSummaryLabel.addStyleName( "clickable-text" );
            }

            if ( parameters.size() > 0 ) {
                hPanel.add( parameterSummaryLabel );
                hPanel.add( actionLabel );
                setWidget( 0, 0, hPanel );
            }

        }

        private void hideParameters() {
            if ( parameters.size() > 0 ) {
                for ( int iii = getRowCount(); iii > 1; iii-- ) {
                    remove( getWidget( iii - 1, 0 ) );
                }
            }
        }

        private void displayParameters( ClickHandler myEditableHandler ) {

            for ( ExperimentParameter parameter : this.parameters ) {
                Label label = new Label(
                        parameter.getSubject() + " : " + parameter.getPredicate() + " : " +
                                parameter.getObjectValue() + " : " + parameter.getUnit() );
                setWidget( rowCount++, 0, label );
                if ( myEditableHandler != null ) {
                    label.addClickHandler( myEditableHandler );
                    label.addStyleName( "clickable-text" );
                }
            }
        }

        private String displayParameterCount() {
            if ( parameters.size() == 1 ) {
                return "1 parameter ";
            } else if ( parameters.size() > 1 ) {
                return parameters.size() + " parameters";
            }
            return "";
        }

        public ArrayList<ExperimentParameter> getParameters() {
            return parameters;
        }
    }
}
