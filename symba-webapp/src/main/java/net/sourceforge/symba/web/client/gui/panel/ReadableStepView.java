package net.sourceforge.symba.web.client.gui.panel;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.stepsorter.ExperimentParameter;

import java.util.ArrayList;


public class ReadableStepView extends VerticalPanel {
    private String stepTitle;
    private ArrayList<String> fileNames;
    private ReadableStepParameterTable parameterTable;

    public ReadableStepView(
            String stepTitle,
            ArrayList<String> fileNames,
            ArrayList<ExperimentParameter> parameters,
            ClickHandler myEditableHandler ) {

        this.stepTitle = stepTitle;

        this.fileNames = fileNames;
        parameterTable = new ReadableStepParameterTable( parameters, myEditableHandler );


        Label label = new Label( this.stepTitle );
        label.addClickHandler( myEditableHandler );
        add( label );
        label.addStyleName( "clickable-text" );

        Label label2 = new Label( displayFileNames() );
        add( label2 );
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

    public class ReadableStepParameterTable extends FlexTable {
        // each row is plain text
        private ArrayList<ExperimentParameter> parameters;
        private int rowCount;

        private ReadableStepParameterTable( ArrayList<ExperimentParameter> parameters,
                                            ClickHandler myEditableHandler ) {
            rowCount = 0;
            this.parameters = parameters;

            for ( ExperimentParameter parameter : this.parameters ) {
                Label label = new Label(
                        parameter.getSubject() + " : " + parameter.getPredicate() + " : " +
                                parameter.getObjectValue() + " : " + parameter.getUnit() );
                label.addClickHandler( myEditableHandler );
                setWidget( rowCount, 0, label );
                Label measureType = new Label(
                        InputValidator.measurementMessages.get( parameter.getMeasurementType() ) );
                measureType.addClickHandler( myEditableHandler );
                setWidget( rowCount++, 1, measureType );
            }
        }

        public ArrayList<ExperimentParameter> getParameters() {
            return parameters;
        }
    }
}
