package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.ActionApplication;
import net.sourceforge.fuge.util.generated.AuditCollection;
import net.sourceforge.fuge.util.generated.ContactRole;
import net.sourceforge.fuge.util.generated.DataCollection;
import net.sourceforge.fuge.util.generated.Description;
import net.sourceforge.fuge.util.generated.Descriptions;
import net.sourceforge.fuge.util.generated.ExternalData;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.GenericAction;
import net.sourceforge.fuge.util.generated.GenericMaterial;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.GenericProtocolApplication;
import net.sourceforge.fuge.util.generated.GenericSoftware;
import net.sourceforge.fuge.util.generated.InvestigationCollection;
import net.sourceforge.fuge.util.generated.Material;
import net.sourceforge.fuge.util.generated.MaterialCollection;
import net.sourceforge.fuge.util.generated.ObjectFactory;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.Provider;
import net.sourceforge.symba.web.client.gui.InputValidator;
import net.sourceforge.symba.web.shared.ExperimentStep;
import net.sourceforge.symba.web.shared.ExperimentStepHolder;
import net.sourceforge.symba.web.shared.Contact;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a simple class which creates a brand-new FuGE object for the given client-side Investigation object. It
 * holds no connections to databases and performs no checks of pre-existing objects in any data storage locations.
 */
public class FugeCreator {

    private final ObjectFactory factory = new ObjectFactory();

    @NotNull
    public String toFugeString( @NotNull net.sourceforge.symba.web.shared.Investigation inv ) {

        FuGE fuge = toFuge( inv );
        java.io.StringWriter sw = new StringWriter();

        try {
            // create a JAXBContext capable of handling classes generated into the net.sourceforge.fuge.util.generated
            // package
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
        // create and add the material collection to the fuge object
        MaterialCollection allMaterial = new MaterialCollection();
        fuge.setMaterialCollection( allMaterial );
        // create and add the material collection to the fuge object
        DataCollection allData = new DataCollection();
        fuge.setDataCollection( allData );
        // create and add the protocol collection to the fuge object
        ProtocolCollection allProtocol = new ProtocolCollection();
        fuge.setProtocolCollection( allProtocol );
        // create and add the ontology collection to the fuge object
        OntologyCollection allOntology = new OntologyCollection();
        fuge.setOntologyCollection( allOntology );

        // create a person who provides the investigation: the "owner"
        Person person = addPerson( allAudit, inv.getProvider() );

        // create the investigation for this fuge object
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

        // create all materials
        addMaterials( allMaterial, inv.getExperiments() );

        // create all external data items
        HashMap<String, String> dataIdentifiers = addData( allData, person, inv.getExperiments() );

        // create all protocols; also make protocol applications wherever there are materials or data
        addAllProtocols( allProtocol, allOntology, dataIdentifiers, person, inv );

        // create an audit trail associated with the object
        IdentifiableConverter.addAuditTrail( fuge, person );

        return fuge;
    }

    /**
     * We don't add the user as a person in the audit trail for the Material, because this material was
     * actually created elsewhere and is sitting in the "database" already - we're just linking to it within
     * this particular Fuge object. This is similar to the way the Contact works. Of course, this class is a
     * simplistic class that doesn't connect to any database, but this behaviour is mimicked here by
     * retaining the provided Material id (and the "add" in the method name rather than the "create"). In the
     * implementation for a database, this behaviour would need to be more complex.
     *
     * @param allMaterial    the collection to add the materials to
     * @param childrenHolder the user interface object storing all of the materials to add to the fuge object
     */
    private void addMaterials( MaterialCollection allMaterial,
                               ArrayList<ExperimentStepHolder> childrenHolder ) {
        // the materials need to be added to the collection at this stage, but do not need to be linked
        // to an experimental protocol application yet.

        for ( ExperimentStepHolder childHolder : childrenHolder ) {
            ExperimentStep child = childHolder.getCurrent();
            for ( net.sourceforge.symba.web.shared.Material inputMaterial : child.getInputMaterials() ) {
                // in the db implementation, this material would be pulled from the database rather than created here
                boolean alreadyPresent = false;
                for ( JAXBElement<? extends Material> jaxbElement : allMaterial.getMaterial() ) {
                    if ( jaxbElement.getValue().getIdentifier().equals( inputMaterial.getId() ) ) {
                        alreadyPresent = true;
                        break;
                    }
                }
                if ( !alreadyPresent ) {
                    GenericMaterial fugeMaterial = createGenericMaterial( inputMaterial );
                    allMaterial.getMaterial().add( factory.createGenericMaterial( fugeMaterial ) );
                }
            }

            for ( net.sourceforge.symba.web.shared.Material outputMaterial : child.getOutputMaterials() ) {
                // in the db implementation, this material would be pulled from the database rather than created here
                boolean alreadyPresent = false;
                for ( JAXBElement<? extends Material> jaxbElement : allMaterial.getMaterial() ) {
                    if ( jaxbElement.getValue().getIdentifier().equals( outputMaterial.getId() ) ) {
                        alreadyPresent = true;
                        break;
                    }
                }
                if ( !alreadyPresent ) {
                    GenericMaterial fugeMaterial = createGenericMaterial( outputMaterial );
                    allMaterial.getMaterial().add( factory.createGenericMaterial( fugeMaterial ) );
                }
            }

            // run this method for all children of this protocol
            if ( !child.isLeaf() ) {
                addMaterials( allMaterial, child.getChildren() );
            }
        }
    }

    /**
     * the external data files need to be added to the collection at this stage, but do not need to be linked
     * to an experimental protocol application yet.
     *
     * @param allData        the collection to add the materials to
     * @param person         the person who assigned the data items to this Fuge object
     * @param childrenHolder the user interface object storing all of the materials to add to the fuge object
     * @return a map linking the files with their new identifiers, for use when referencing these data objects
     */
    private HashMap<String, String> addData( DataCollection allData,
                                             Person person,
                                             ArrayList<ExperimentStepHolder> childrenHolder ) {

        HashMap<String, String> dataIdentifiers = new HashMap<String, String>();

        for ( ExperimentStepHolder childHolder : childrenHolder ) {
            ExperimentStep child = childHolder.getCurrent();
            for ( String inputData : child.getFileNames() ) {
                // in the db implementation, this material would be pulled from the database rather than created here
                // todo set "name" to original filename plus easy-to-remember SyMBA filename
                ExternalData fugeData = createExternalData( inputData, person );
                dataIdentifiers.put( inputData, fugeData.getIdentifier() );
                allData.getData().add( factory.createExternalData( fugeData ) );
            }

            // run this method for all children of this protocol
            if ( !child.isLeaf() ) {
                dataIdentifiers = addData( allData, person, child.getChildren() );
            }
        }

        return dataIdentifiers;
    }

    private void addAllProtocols( ProtocolCollection allProtocol,
                                  OntologyCollection allOntology,
                                  HashMap<String, String> dataIdentifiers,
                                  Person person,
                                  final Investigation inv ) {


        // create a top-level protocol for the name of the investigation, and add everything as an action
        // relating to the other protocols. In order for the top-level protocol to be recognised easily as
        // the top one, add InputValidator.TOP_PROTOCOL to the name of the protocol. This will be
        // parsed out when viewing and downloading.
        GenericProtocol topProtocol = GenericProtocolConverter.toFuge( createRandom(), createRandom(),
                InputValidator.TOP_PROTOCOL + " " + inv.getInvestigationTitle() );
        GenericProtocolApplication topGpa = GenericProtocolApplicationConverter
                .toFuge( createRandom(), createRandom(), topProtocol, person );

        addChildProtocols( allProtocol, allOntology, topProtocol, topGpa, 0, dataIdentifiers, person,
                inv.getExperiments() );

        // link the top protocol and its gpa to the collection
        allProtocol.getProtocol().add( factory.createGenericProtocol( topProtocol ) );
        allProtocol.getProtocolApplication().add( factory.createGenericProtocolApplication( topGpa ) );

    }

    private void addChildProtocols( ProtocolCollection allProtocol,
                                    OntologyCollection allOntology,
                                    GenericProtocol parentProtocol,
                                    GenericProtocolApplication parentProtocolApplication,
                                    int ordinal,
                                    HashMap<String, String> dataIdentifiers,
                                    Person person,
                                    final ArrayList<ExperimentStepHolder> childrenHolder ) {

        // now we need a Generic Protocol for each further experiment step, then add that step to the
        // top protocol as a Generic Action.
        // Also, we will need to add GenericProtocolApplication objects for each item with a file name.
        for ( ExperimentStepHolder childHolder : childrenHolder ) {
            ExperimentStep child = childHolder.getCurrent();
            // create basic protocol
            GenericProtocol childProtocol = GenericProtocolConverter
                    .toFuge( createRandom(), createRandom(), child.getTitle(), child.getParameters(), allOntology );
            // link the protocol to the collection
            allProtocol.getProtocol().add( factory.createGenericProtocol( childProtocol ) );

            // add the protocol as an action on the current parent protocol
            GenericAction action = GenericActionConverter
                    .toFuge( createRandom(), createRandom(), childProtocol.getName(), childProtocol.getIdentifier(),
                            ordinal );

            parentProtocol.getAction().add( factory.createGenericAction( action ) );

            // If there is a child step, it should definitely have a child GPA.
            GenericProtocolApplication childGpa = GenericProtocolApplicationConverter
                    .toFuge( createRandom(), createRandom(), child, dataIdentifiers, childProtocol, person );
            parentProtocolApplication.getActionApplication().add( createActionApplication( action, childGpa, person ) );
            allProtocol.getProtocolApplication().add( factory.createGenericProtocolApplication( childGpa ) );

            // run this method for all children of this protocol
            if ( !child.isLeaf() ) {
                addChildProtocols( allProtocol, allOntology, childProtocol, childGpa, 0, dataIdentifiers, person,
                        child.getChildren() );
            }

        }
    }

    /**
     * This method does not recurse through the children of the step variable. It just creates an appropriate
     * AA for this step.
     *
     * @param associatedAction the action we're referencing
     * @param childGpa         the child protocol application associated with this AA
     * @param person           the person to give credit to for running the protocol.
     * @return the new ActionApplication to add to the parent GPA
     */
    private ActionApplication createActionApplication( GenericAction associatedAction,
                                                       GenericProtocolApplication childGpa,
                                                       Person person ) {
        ActionApplication aa = new ActionApplication();
        aa.setIdentifier( createRandom() );
        aa.setEndurantRef( createRandom() );
        aa.setName( associatedAction.getName() );
        aa.setActionRef( associatedAction.getIdentifier() );
        aa.setProtocolApplicationRef( childGpa.getIdentifier() );
        IdentifiableConverter.addAuditTrail( aa, person );

        return aa;
    }

    private GenericMaterial createGenericMaterial( net.sourceforge.symba.web.shared.Material inputMaterial ) {

        GenericMaterial material = new GenericMaterial();
        material.setIdentifier( inputMaterial.getId() );
        material.setEndurantRef( createRandom() );
        material.setName( inputMaterial.getName() );

        if ( inputMaterial.getDescription().length() > 0 ) {
            Descriptions descriptions = new Descriptions();
            Description description = new Description();
            description.setText( inputMaterial.getDescription() );
            descriptions.getDescription().add( description );
            material.setDescriptions( descriptions );
        }

        return material;
    }

    private ExternalData createExternalData( String dataURI,
                                             Person person ) {

        ExternalData d = new ExternalData();
        d.setIdentifier( createRandom() );
        d.setEndurantRef( createRandom() );
        d.setName( dataURI ); // just put the URI as the name as well as the location, to have something in the name
        d.setLocation( dataURI );
        IdentifiableConverter.addAuditTrail( d, person );

        return d;
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
        IdentifiableConverter.addAuditTrail( provider, person );
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
                                                                                net.sourceforge.symba.web.shared.Investigation uiInvestigation ) {
        // Convert the main features of the investigation. We are currently only allowing a single Investigation
        // object in the FuGE object per SyMBA investigation.
        net.sourceforge.fuge.util.generated.Investigation fugeInv = new net.sourceforge.fuge.util.generated.Investigation();
        fugeInv.setName( uiInvestigation.getInvestigationTitle() );
        fugeInv.setEndurantRef( createRandom() );
        fugeInv.setIdentifier( uiInvestigation.getId() );
        // todo hypothesis and conclusion
        // create an audit trail associated with the object
        IdentifiableConverter.addAuditTrail( fugeInv, person );
        // add to the investigation collection
        allInvestigation.getInvestigation().add( fugeInv );
        return fugeInv;
    }

    /**
     * This person is actually created elsewhere and is sitting in the "database" already - we're just linking to it
     * within this particular Fuge object. This is similar to the way the Material works. Of course, this class is a
     * simplistic class that doesn't connect to any database, but this behaviour is mimicked here by
     * retaining the provided Person id (and the "add" in the method name rather than the "create"). In the
     * implementation for a database, this behaviour would need to be more complex.
     *
     * @param allAudit the collection to add the person to
     * @param uiPerson the user interface object storing the contact to add to the fuge object
     * @return the Fuge Person object
     */
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

    // todo replace with proper creation method, e.g. the connection to the LSID server

    private String createRandom() {
        return ( ( Double ) Math.random() ).toString();
    }
}
