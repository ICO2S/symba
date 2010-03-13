package net.sourceforge.symba.web.client.stepsorter;

import net.sourceforge.symba.web.client.gui.InputValidator;

import java.io.Serializable;

/**
 * Stores the triples used to describe parameterisation, equipment and software of an ExperimentStep.
 */
public class ExperimentParameter implements Serializable {

    String subject;  // e.g. "time point", "fluorescent dye"
    String predicate; // e.g. "hasValue", "hasConcentration"
    String objectValue; // e.g. "30", "10"
    String unit; // e.g. "hours", "mM"
    InputValidator.MeasurementType measurementType;

    boolean fullyWriteable;

    public ExperimentParameter() {
        subject = "";
        predicate = "";
        objectValue = "";
        unit = "";
        measurementType = InputValidator.MeasurementType.UNKNOWN;

        fullyWriteable = true;
    }

    public ExperimentParameter( String subject,
                                String predicate,
                                String objectValue,
                                String unit,
                                InputValidator.MeasurementType measurementType ) {
        this.subject = subject;
        this.predicate = predicate;
        this.objectValue = objectValue;
        this.unit = unit;
        this.measurementType = measurementType;

        fullyWriteable = true;

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( String subject ) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate( String predicate ) {
        this.predicate = predicate;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue( String objectValue ) {
        this.objectValue = objectValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit( String unit ) {
        this.unit = unit;
    }

    public InputValidator.MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType( InputValidator.MeasurementType measurementType ) {
        this.measurementType = measurementType;
    }

    public boolean isFullyWriteable() {
        return fullyWriteable;
    }

    public void setFullyWriteable( boolean fullyWriteable ) {
        this.fullyWriteable = fullyWriteable;
    }
}
