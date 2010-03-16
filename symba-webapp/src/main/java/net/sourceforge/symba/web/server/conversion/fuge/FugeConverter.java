package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.fuge.util.generated.AtomicValue;
import net.sourceforge.fuge.util.generated.Audit;
import net.sourceforge.fuge.util.generated.AuditCollection;
import net.sourceforge.fuge.util.generated.AuditTrail;
import net.sourceforge.fuge.util.generated.BooleanValue;
import net.sourceforge.fuge.util.generated.ComplexValue;
import net.sourceforge.fuge.util.generated.ContactRole;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.GenericAction;
import net.sourceforge.fuge.util.generated.GenericParameter;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.GenericSoftware;
import net.sourceforge.fuge.util.generated.Identifiable;
import net.sourceforge.fuge.util.generated.InvestigationCollection;
import net.sourceforge.fuge.util.generated.ObjectFactory;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.fuge.util.generated.OntologyIndividual;
import net.sourceforge.fuge.util.generated.OntologyTerm;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.Provider;
import net.sourceforge.fuge.util.generated.Unit;
import net.sourceforge.fuge.util.generated.Value;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.client.stepsorter.ExperimentParameter;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStep;
import net.sourceforge.symba.web.client.stepsorter.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a simple class which creates a brand-new FuGE object for the given client-side Investigation object. It
 * holds no connections to databases and performs no checks of pre-existing objects in any data storage locations.
 */
public class FugeConverter {

    private final ObjectFactory factory = new ObjectFactory();

    @NotNull
    public String toFugeString( @NotNull net.sourceforge.symba.web.shared.Investigation inv ) {

        FuGE fuge = toFuge( inv );
        java.io.StringWriter sw = new StringWriter();

        // create a JAXBContext capable of handling classes generated into the net.sourceforge.fuge.util.generated
        // package
        try {
            JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generated" );

            // create a Marshaller
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            // todo do we want a validation step here that would notify, but not stop things, if there were a problem?

            @SuppressWarnings( "unchecked" )
            JAXBElement element = new JAXBElement(
                    new QName( "http://fuge.sourceforge.net/fuge/1.0", "FuGE" ),
                    FuGE.class,
                    fuge );
            m.marshal( element, sw );
        } catch ( JAXBException e ) {
            e.printStackTrace();
            return "";
        }

        return sw.toString();

    }

    @NotNull
    public FuGE toFuge( @NotNull final net.sourceforge.symba.web.shared.Investigation inv ) {

        // todo provider information may not match the login details
        // will need to pass the login details and set the audit trails to the person logged in, and the provider
        // to the person set within the Investigation as the provider.

        // add template and completed information to the name used for JUST the FuGE entry.
        String name = inv.getInvestigationTitle();
        if ( inv.isCompleted() ) {
            name = InputValidator.COMPLETED_INVESTIGATION + " " + name;
        }
        if ( inv.isTemplate() ) {
            name = InputValidator.TEMPLATE + " " + name;
        }

        FuGE fuge = new FuGE();

        // create and add the audit collection to the fuge object
        AuditCollection allAudit = new AuditCollection();
        fuge.setAuditCollection( allAudit );
        // create and add the investigation collection to the fuge object
        InvestigationCollection allInvestigation = new InvestigationCollection();
        fuge.setInvestigationCollection( allInvestigation );
        // create and add the protocol collection to the fuge object
        ProtocolCollection allProtocol = new ProtocolCollection();
        fuge.setProtocolCollection( allProtocol );
        // create and add the ontology collection to the fuge object
        OntologyCollection allOntology = new OntologyCollection();
        fuge.setOntologyCollection( allOntology );

        Person person = addPerson( allAudit, inv.getProvider() );
        addInvestigation( allInvestigation, person, inv );

        // create the main features of the Fuge object itself
        fuge.setName( name );
        fuge.setEndurantRef( createRandom() );
        fuge.setIdentifier( createRandom() );

        // create a software instance for SyMBA
        GenericSoftware symbaSoftware = addSymbaSoftware( allProtocol );
        // create a provider object linked to the person creating it and the SyMBA software
        Provider provider = createProvider( person, symbaSoftware );
        // link the provider to the fuge object
        fuge.setProvider( provider );

        // create all protocols
        addAllProtocols( allProtocol, allOntology, person, inv );

        // create an audit trail associated with the object
        addAuditTrail( fuge, person );

        return fuge;
    }

    private void addAllProtocols( ProtocolCollection allProtocol,
                                  OntologyCollection allOntology,
                                  Person person,
                                  final net.sourceforge.symba.web.shared.Investigation inv ) {


        // create a top-level protocol for the name of the investigation, and add everything as an action
        // relating to the other protocols. In order for the top-level protocol to be recognised easily as
        // the top one, add InputValidator.TOP_PROTOCOL to the name of the protocol. This will be
        // parsed out when viewing and downloading.
        GenericProtocol topProtocol = createGenericProtocol(
                InputValidator.TOP_PROTOCOL + " " + inv.getInvestigationTitle() );

        addProtocols( allProtocol, allOntology, topProtocol, 0, person, inv.getExperiments() );

    }

    private void addProtocols( ProtocolCollection allProtocol,
                               OntologyCollection allOntology,
                               GenericProtocol currentProtocol,
                               int ordinal,
                               Person person,
                               final ArrayList<ExperimentStepHolder> holders ) {

        // now we need a Generic Protocol for each further experiment step, then add that step to the
        // top protocol as a Generic Action.
        // Also, we will need to add GenericProtocolApplication objects for each item with a file name.
        for ( ExperimentStepHolder holder : holders ) {
            ExperimentStep child = holder.getCurrent();
            System.err.println( "child.getTitle(): " + child.getTitle() );
            // create basic protocol
            GenericProtocol childProtocol = createGenericProtocol( child.getTitle() );
            // add any parameters
            for ( ExperimentParameter parameter : child.getParameters() ) {
                createAndAddGenericParameter( allOntology, childProtocol, parameter );
            }
            // link the protocol to the collection
            allProtocol.getProtocol().add( factory.createGenericProtocol( childProtocol ) );

            // run this method for all children of this protocol
            if ( !child.isLeaf() ) {
                addProtocols( allProtocol, allOntology, childProtocol, 0, person, child.getChildren() );
            }

            // add the protocol as an action on the top protocol
            createGenericAction( currentProtocol, childProtocol, ordinal++ );
        }

        // link the top protocol to the collection
        allProtocol.getProtocol().add( factory.createGenericProtocol( currentProtocol ) );
    }

    private void createGenericAction( GenericProtocol topProtocol,
                                      GenericProtocol childProtocol,
                                      int ordinal ) {
        GenericAction action = new GenericAction();
        action.setActionOrdinal( ordinal );
        action.setProtocolRef( childProtocol.getIdentifier() );
        action.setName( childProtocol.getName() );
        action.setIdentifier( createRandom() );
        action.setEndurantRef( createRandom() );
        topProtocol.getAction().add( factory.createGenericAction( action ) );
    }

    private void createAndAddGenericParameter( OntologyCollection allOntology,
                                               GenericProtocol protocol,
                                               ExperimentParameter parameter ) {
        // todo extra validation of measurement type?
        GenericParameter p = new GenericParameter();
        p.setIdentifier( createRandom() );
        p.setEndurantRef( createRandom() );
        p.setName( parameter.getSubject() + " " + InputValidator.SUBJECT_PREDICATE_DIVIDER + " " +
                parameter.getPredicate() );
        if ( parameter.getMeasurementType() == InputValidator.MeasurementType.ATOMIC ) {
            AtomicValue value = new AtomicValue();
            value.setValue( parameter.getObjectValue() );
            Unit unit = createUnit( allOntology, parameter.getUnit() );
            value.setUnit( unit );
            p.setMeasurement( factory.createAtomicValue( value ) );
        } else if ( parameter.getMeasurementType() == InputValidator.MeasurementType.BOOLEAN ) {
            BooleanValue value = new BooleanValue();
            value.setValue( parameter.getObjectValue().equals( "true" ) );
            Unit unit = createUnit( allOntology, parameter.getUnit() );
            value.setUnit( unit );
            p.setMeasurement( factory.createBooleanValue( value ) );
        } else if ( parameter.getMeasurementType() == InputValidator.MeasurementType.COMPLEX ) {
            ComplexValue value = new ComplexValue();
            Value ontologyValue = new Value();
            String ontoRef = createOrRetrieveOntologyTerm( allOntology, parameter.getObjectValue() );
            ontologyValue.setOntologyTermRef( ontoRef );
            value.setValue( ontologyValue );
            Unit unit = createUnit( allOntology, parameter.getUnit() );
            value.setUnit( unit );
            p.setMeasurement( factory.createComplexValue( value ) );
        }
        protocol.getGenericParameter().add( p );
    }

    private Unit createUnit( OntologyCollection allOntology,
                             String unitValue ) {
        String termId = createOrRetrieveOntologyTerm( allOntology, unitValue );

        Unit unit = new Unit();
        unit.setOntologyTermRef( termId );
        return unit;
    }

    private String createOrRetrieveOntologyTerm( OntologyCollection allOntology,
                                                 String objectValue ) {
        // find a matching term, if present, within this fuge object's ontology collection. If found,
        // use that rather than creating a new ontology term
        for ( JAXBElement<? extends OntologyTerm> jaxbElement : allOntology.getOntologyTerm() ) {
            OntologyTerm term = jaxbElement.getValue();
            if ( term instanceof OntologyIndividual && term.getTerm().equals( objectValue ) ) {
                return term.getIdentifier();
            }
        }

        OntologyIndividual term = new OntologyIndividual();
        term.setIdentifier( createRandom() );
        term.setEndurantRef( createRandom() );
        term.setName( objectValue );
        // todo allow real ontology terms and have their term accession specified
        term.setTerm( objectValue );
        allOntology.getOntologyTerm().add( factory.createOntologyIndividual( term ) );

        return term.getIdentifier();
    }

    private GenericProtocol createGenericProtocol( String name ) {
        GenericProtocol p = new GenericProtocol();
        p.setIdentifier( createRandom() );
        p.setEndurantRef( createRandom() );
        p.setName( name );

        return p;
    }

    /**
     * This is a "create" rather than an "add" because it will create a provider based on the person argument,
     * but will not add it to any fuge collection or fuge object.
     *
     * @param person        the person to assign as a provider
     * @param symbaSoftware the software to associate with the provider
     * @return the new provider for the given person and software
     */
    private Provider createProvider( Person person,
                                     GenericSoftware symbaSoftware ) {
        Provider provider = new Provider();
        provider.setIdentifier( createRandom() );
        provider.setEndurantRef( createRandom() );

        // link the fuge contact to the provider of the new Fuge metadata
        ContactRole roleType = new ContactRole();
        roleType.setContactRef( person.getIdentifier() );
        // todo add an appropriate ontology term for the role
//        ContactRole.Role value = new ContactRole.Role();
//        roleType.setRole( value );
        provider.setContactRole( roleType );
        // link the SyMBA software object to the provider of the new Fuge metadata to show that the provider used
        // SyMBA to process their metadata
        provider.setSoftwareRef( symbaSoftware.getIdentifier() );
        // create an audit trail for the provider
        addAuditTrail( provider, person );
        return provider;
    }

    private GenericSoftware addSymbaSoftware( ProtocolCollection allProtocol ) {
        // create a fuge Software object describing SyMBA
        GenericSoftware symbaSoftware = new GenericSoftware();
        symbaSoftware.setIdentifier( createRandom() );
        symbaSoftware.setEndurantRef( createRandom() );
        // todo import the version of SyMBA directly into the code via Spring
        symbaSoftware.setVersion( "8.09" );
        symbaSoftware.setName( "SyMBA (http://symba.sourceforge.net) by CISBAN (http://www.cisban.ac.uk)" );
        // add the software object to the protocol collection
        allProtocol.getSoftware().add( factory.createGenericSoftware( symbaSoftware ) );
        return symbaSoftware;
    }

    private net.sourceforge.fuge.util.generated.Investigation addInvestigation( InvestigationCollection allInvestigation,
                                                                                Person person,
                                                                                Investigation uiInvestigation ) {
        // Convert the main features of the investigation. We are currently only allowing a single Investigation
        // object in the FuGE object per SyMBA investigation.
        net.sourceforge.fuge.util.generated.Investigation fugeInv = new net.sourceforge.fuge.util.generated.Investigation();
        fugeInv.setName( uiInvestigation.getInvestigationTitle() );
        fugeInv.setEndurantRef( createRandom() );
        fugeInv.setIdentifier( uiInvestigation.getId() );
        // todo hypothesis and conclusion
        // create an audit trail associated with the object
        addAuditTrail( fugeInv, person );
        // add to the investigation collection
        allInvestigation.getInvestigation().add( fugeInv );
        return fugeInv;
    }

    private Person addPerson( AuditCollection allAudit,
                              Contact uiPerson ) {
        // create a fuge person
        Person person = new Person();
        person.setIdentifier( uiPerson.getId() );
        person.setEndurantRef( createRandom() );
        person.setFirstName( uiPerson.getFirstName() );
        person.setLastName( uiPerson.getLastName() );
        person.setEmail( uiPerson.getEmailAddress() );
        // todo organisation
        // add to the Audit collection
        allAudit.getContact().add( factory.createPerson( person ) );
        // we don't add an audit trail to the contact, as it could be the added contact which is performing the addition

        return person;
    }

    private void addAuditTrail( Identifiable type,
                                Person person ) {
        AuditTrail trail = new AuditTrail();
        Audit item = new Audit();
        item.setContactRef( person.getIdentifier() );
        item.setDateItem( new Date() );
        item.setAction( "CREATE" );
        trail.getAudit().add( item );

        type.setAuditTrail( trail );
    }

    // todo replace with proper creation method, e.g. the connection to the LSID server

    private String createRandom() {

        return ( ( Double ) Math.random() ).toString();

    }
}
