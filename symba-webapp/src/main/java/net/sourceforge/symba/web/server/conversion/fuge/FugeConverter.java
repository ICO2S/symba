package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.fuge.util.generated.Audit;
import net.sourceforge.fuge.util.generated.AuditCollection;
import net.sourceforge.fuge.util.generated.AuditCollectionContactItem;
import net.sourceforge.fuge.util.generated.AuditTrail;
import net.sourceforge.fuge.util.generated.ContactRole;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.GenericSoftware;
import net.sourceforge.fuge.util.generated.Identifiable;
import net.sourceforge.fuge.util.generated.Investigation;
import net.sourceforge.fuge.util.generated.InvestigationCollection;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.ProtocolCollectionSoftwareItem;
import net.sourceforge.fuge.util.generated.Provider;
import net.sourceforge.symba.web.shared.Contact;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Date;

/**
 * This is a simple class which creates a brand-new FuGE object for the given client-side Investigation object. It
 * holds no connections to databases and performs no checks of pre-existing objects in any data storage locations.
 */
public class FugeConverter {

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
    public FuGE toFuge( @NotNull net.sourceforge.symba.web.shared.Investigation inv ) {

        // todo provider information may not match the login details
        // will need to pass the login details and set the audit trails to the person logged in, and the provider
        // to the person set within the Investigation as the provider.

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

        Person person = addPerson( allAudit, inv.getProvider() );
        addInvestigation( allInvestigation, person, inv );

        // create the main features of the Fuge object itself
        fuge.setName( "FuGE structure for investigation: " + inv.getInvestigationTitle() );
        fuge.setEndurantRef( createRandom() );
        fuge.setIdentifier( createRandom() );

        GenericSoftware symbaSoftware = addSymbaSoftware( allProtocol );
        // create a provider object
        Provider provider = createProvider( person, symbaSoftware );
        // link the provider to the fuge object
        fuge.setProvider( provider );
        // create an audit trail associated with the object
        addAuditTrail( fuge, person );

        return fuge;
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
        ProtocolCollectionSoftwareItem sItem = new ProtocolCollectionSoftwareItem();
        sItem.setItemValue( symbaSoftware );
        allProtocol.getSoftwareItems().add( sItem );
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
        AuditCollectionContactItem item = new AuditCollectionContactItem();
        item.setItemValue( person );
        item.setItemName( "Person" );
        allAudit.getContactItems().add( item );
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
