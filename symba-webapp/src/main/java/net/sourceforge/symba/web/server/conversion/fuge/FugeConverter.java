package net.sourceforge.symba.web.server.conversion.fuge;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.fuge.util.generated.FuGEBioInvestigationInvestigationType;
import net.sourceforge.fuge.util.generated.FuGECollectionAuditCollectionType;
import net.sourceforge.fuge.util.generated.FuGECollectionFuGEType;
import net.sourceforge.fuge.util.generated.FuGECollectionInvestigationCollectionType;
import net.sourceforge.fuge.util.generated.FuGECollectionProtocolCollectionType;
import net.sourceforge.fuge.util.generated.FuGECollectionProviderType;
import net.sourceforge.fuge.util.generated.FuGECommonAuditAuditType;
import net.sourceforge.fuge.util.generated.FuGECommonAuditContactRoleType;
import net.sourceforge.fuge.util.generated.FuGECommonAuditPersonType;
import net.sourceforge.fuge.util.generated.FuGECommonDescribableType;
import net.sourceforge.fuge.util.generated.FuGECommonIdentifiableType;
import net.sourceforge.fuge.util.generated.FuGECommonProtocolGenericSoftwareType;
import net.sourceforge.symba.web.shared.Investigation;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

/**
 * This is a simple class which creates a brand-new FuGE object for the given client-side Investigation object. It
 * holds no connections to databases and performs no checks of pre-existing objects in any data storage locations.
 */
public class FugeConverter {

    @NotNull
    public String toFugeString( @NotNull Investigation inv ) {

        FuGECollectionFuGEType fuge = toFuge( inv );
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
                    FuGECollectionFuGEType.class,
                    fuge );
            m.marshal( element, sw );
        } catch ( JAXBException e ) {
            e.printStackTrace();
            return "";
        }

        return sw.toString();

    }

    @NotNull
    public FuGECollectionFuGEType toFuge( @NotNull Investigation inv ) {

        // todo provider information may not match the login details
        // will need to pass the login details and set the audit trails to the person logged in, and the provider
        // to the person set within the Investigation as the provider.

        FuGECollectionFuGEType fuge = new FuGECollectionFuGEType();

        // create and add the audit collection to the fuge object
        FuGECollectionAuditCollectionType allAudit = new FuGECollectionAuditCollectionType();
        fuge.setAuditCollection( allAudit );
        // create and add the investigation collection to the fuge object
        FuGECollectionInvestigationCollectionType allInvestigation = new FuGECollectionInvestigationCollectionType();
        fuge.setInvestigationCollection( allInvestigation );
        // create and add the protocol collection to the fuge object
        FuGECollectionProtocolCollectionType allProtocol = new FuGECollectionProtocolCollectionType();
        fuge.setProtocolCollection( allProtocol );

        // create a fuge person
        FuGECommonAuditPersonType person = new FuGECommonAuditPersonType();
        person.setIdentifier( inv.getProvider().getId() );
        person.setEndurantRef( createRandom() );
        person.setFirstName( inv.getProvider().getFirstName() );
        person.setLastName( inv.getProvider().getLastName() );
        person.setEmail( inv.getProvider().getEmailAddress() );
        // todo organisation
        // add to the Audit collection
        FuGECollectionAuditCollectionType.FuGECollectionAuditCollectionTypeContactItem item = new FuGECollectionAuditCollectionType.FuGECollectionAuditCollectionTypeContactItem();
        item.setItemValue( person );
        allAudit.getContactItems().add( item );
        // we don't add an audit trail to the contact, as it could be the added contact which is performing the addition

        // Convert the main features of the investigation. We are currently only allowing a single Investigation
        // object in the FuGE object per SyMBA investigation.
        FuGEBioInvestigationInvestigationType fugeInv = new FuGEBioInvestigationInvestigationType();
        fugeInv.setName( inv.getInvestigationTitle() );
        fugeInv.setEndurantRef( createRandom() );
        fugeInv.setIdentifier( inv.getId() );
        // todo hypothesis and conclusion
        // create an audit trail associated with the object
        createAuditTrail( fugeInv, person );
        // add to the investigation collection
        allInvestigation.getInvestigation().add( fugeInv );

        // create the main features of the Fuge object itself
        fuge.setName( "FuGE structure for investigation: " + inv.getInvestigationTitle() );
        fuge.setEndurantRef( createRandom() );
        fuge.setIdentifier( createRandom() );

        // create a provider object
        FuGECollectionProviderType provider = new FuGECollectionProviderType();
        // create a fuge Software object describing SyMBA
        FuGECommonProtocolGenericSoftwareType symbaSoftware = new FuGECommonProtocolGenericSoftwareType();
        symbaSoftware.setIdentifier( createRandom() );
        symbaSoftware.setEndurantRef( createRandom() );
        // todo import the version of SyMBA directly into the code via Spring
        symbaSoftware.setVersion( "8.09" );
        symbaSoftware.setName( "SyMBA (http://symba.sourceforge.net) by CISBAN (http://www.cisban.ac.uk)" );
        // add the software object to the protocol collection
        FuGECollectionProtocolCollectionType.FuGECollectionProtocolCollectionTypeSoftwareItem sItem = new FuGECollectionProtocolCollectionType.FuGECollectionProtocolCollectionTypeSoftwareItem();
        sItem.setItemValue( symbaSoftware );
        allProtocol.getSoftwareItems().add( sItem );
        // link the fuge contact to the provider of the new Fuge metadata
        FuGECommonAuditContactRoleType roleType = new FuGECommonAuditContactRoleType();
        roleType.setContactRef( person.getIdentifier() );
        // todo add an appropriate ontology term for the role
//        FuGECommonAuditContactRoleType.Role value = new FuGECommonAuditContactRoleType.Role();
//        roleType.setRole( value );
        provider.setContactRole( roleType );
        // link the SyMBA software object to the provider of the new Fuge metadata to show that the provider used
        // SyMBA to process their metadata
        provider.setSoftwareRef( symbaSoftware.getIdentifier() );
        // create an audit trail for the provider
        createAuditTrail( provider, person );
        // link the provider to the fuge object
        fuge.setProvider( provider );
        // create an audit trail associated with the object
        createAuditTrail( fuge, person );

        return fuge;
    }

    private void createAuditTrail( FuGECommonIdentifiableType type,
                                   FuGECommonAuditPersonType person ) {
        FuGECommonDescribableType.AuditTrail trail = new FuGECommonDescribableType.AuditTrail();
        FuGECommonAuditAuditType item = new FuGECommonAuditAuditType();
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
