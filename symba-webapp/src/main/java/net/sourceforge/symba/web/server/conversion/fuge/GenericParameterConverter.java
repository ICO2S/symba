package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.fuge.util.generated.AtomicValue;
import net.sourceforge.fuge.util.generated.BooleanValue;
import net.sourceforge.fuge.util.generated.ComplexValue;
import net.sourceforge.fuge.util.generated.GenericParameter;
import net.sourceforge.fuge.util.generated.ObjectFactory;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.fuge.util.generated.OntologyIndividual;
import net.sourceforge.fuge.util.generated.Unit;
import net.sourceforge.fuge.util.generated.Value;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.stepsorter.ExperimentParameter;

import javax.xml.bind.JAXBElement;

/**
 * Helper class to ensure the conversions to and from FuGE are all in one place, to reduce error and confusion.
 */
public class GenericParameterConverter {

    /**
     * A name is not passed, as it is auto-generated from the parameters themselves.
     *
     * @param identifier  the identifier for the GenericParameter
     * @param endurant    the endurant for the GenericParameter
     * @param uiParameter the parameter from the user interface
     * @param allOntology the ontology where new terms can be added.
     * @return the new FuGE parameter
     */
    public static GenericParameter toFuge( String identifier,
                                           String endurant,
                                           ExperimentParameter uiParameter,
                                           OntologyCollection allOntology ) {
        final ObjectFactory factory = new ObjectFactory();

        // todo extra validation of measurement type?
        GenericParameter parameter = new GenericParameter();
        String name = uiParameter.getSubject() + " " + InputValidator.SUBJECT_PREDICATE_DIVIDER + " " +
                uiParameter.getPredicate();
        parameter = ( GenericParameter ) IdentifiableConverter.toFuge( parameter, identifier, endurant, name );

        if ( uiParameter.getMeasurementType() == InputValidator.MeasurementType.ATOMIC ) {
            AtomicValue value = new AtomicValue();
            value.setValue( uiParameter.getObjectValue() );
            if ( uiParameter.getUnit().trim().length() > 0 ) {
                Unit unit = createUnit( allOntology, uiParameter.getUnit() );
                value.setUnit( unit );
            }
            parameter.setMeasurement( factory.createAtomicValue( value ) );
        } else if ( uiParameter.getMeasurementType() == InputValidator.MeasurementType.BOOLEAN ) {
            BooleanValue value = new BooleanValue();
            value.setValue( uiParameter.getObjectValue().equals( "true" ) );
            if ( uiParameter.getUnit().trim().length() > 0 ) {
                Unit unit = createUnit( allOntology, uiParameter.getUnit() );
                value.setUnit( unit );
            }
            parameter.setMeasurement( factory.createBooleanValue( value ) );
        } else if ( uiParameter.getMeasurementType() == InputValidator.MeasurementType.COMPLEX ) {
            ComplexValue value = new ComplexValue();
            Value ontologyValue = new Value();
            String ontoRef = OntologyIndividualConverter
                    .toFuge( IdentifiableConverter.createRandom(), IdentifiableConverter.createRandom(),
                            uiParameter.getObjectValue(), allOntology );
            ontologyValue.setOntologyTermRef( ontoRef );
            value.setValue( ontologyValue );
            if ( uiParameter.getUnit().trim().length() > 0 ) {
                Unit unit = createUnit( allOntology, uiParameter.getUnit() );
                value.setUnit( unit );
            }
            parameter.setMeasurement( factory.createComplexValue( value ) );
        }
        return parameter;
    }

    private static Unit createUnit( OntologyCollection allOntology,
                                    String unitValue ) {
        String termId = OntologyIndividualConverter
                .toFuge( IdentifiableConverter.createRandom(), IdentifiableConverter.createRandom(),
                        unitValue, allOntology );
        Unit unit = new Unit();
        unit.setOntologyTermRef( termId );
        return unit;
    }


    public static ExperimentParameter toSymba( final GenericParameter parameter,
                                               final OntologyCollection allOntology ) {
        ExperimentParameter uiParameter = new ExperimentParameter();
        Unit unit = parameter.getMeasurement().getValue().getUnit();
        if ( unit != null ) {
            for ( JAXBElement element : allOntology.getOntologyTerm() ) {
                if ( element.getValue() instanceof OntologyIndividual ) {
                    OntologyIndividual oi = ( OntologyIndividual ) element.getValue();
                    if ( unit.getOntologyTermRef().equals( oi.getIdentifier() ) ) {
                        uiParameter.setUnit( oi.getName() );
                    }
                }
            }
        }

        if ( parameter.getMeasurementValue() instanceof AtomicValue ) {
            uiParameter.setMeasurementType( InputValidator.MeasurementType.ATOMIC );
            uiParameter.setObjectValue( ( ( AtomicValue ) parameter.getMeasurementValue() ).getValue() );
        } else if ( parameter.getMeasurementValue() instanceof BooleanValue ) {
            uiParameter.setMeasurementType( InputValidator.MeasurementType.BOOLEAN );
            if ( ( ( BooleanValue ) parameter.getMeasurementValue() ).isValue() ) {
                uiParameter.setObjectValue( "true" );
            } else {
                uiParameter.setObjectValue( "false" );
            }
        } else if ( parameter.getMeasurementValue() instanceof ComplexValue ) {
            uiParameter.setMeasurementType( InputValidator.MeasurementType.COMPLEX );
            for ( JAXBElement element : allOntology.getOntologyTerm() ) {
                if ( element.getValue() instanceof OntologyIndividual ) {
                    OntologyIndividual oi = ( OntologyIndividual ) element.getValue();
                    if ( ( ( ComplexValue ) parameter.getMeasurementValue() ).getValue().getOntologyTermRef()
                            .equals( oi.getIdentifier() ) ) {
                        uiParameter.setObjectValue( oi.getName() );
                    }
                }
            }
        }

        uiParameter.setSubject( parameter.getName().substring( 0,
                parameter.getName().indexOf( InputValidator.SUBJECT_PREDICATE_DIVIDER ) ).trim() );
        uiParameter.setPredicate( parameter.getName().substring(
                parameter.getName().indexOf( InputValidator.SUBJECT_PREDICATE_DIVIDER ) +
                        InputValidator.SUBJECT_PREDICATE_DIVIDER.length() ).trim() );

        return uiParameter;
    }
}
