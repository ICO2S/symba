package net.sourceforge.symba.database;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.symba.database.controller.FugeDatabaseController;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

public class SimpleFugeEntriesDatabaseFiller {

    private FugeDatabaseController controller;

    // test contact data below originally from the GWT 2.0 tutorial.
    private static final String[] contactsFirstNameData = new String[]{
            "Alpha", "Bravo", "Charlie" };

    private static final String[] contactsLastNameData = new String[]{
            "Jones", "Smith", "Taylor" };

    private static final String[] contactsEmailData = new String[]{
            "a@example.com", "b@example.com", "c@example.com" };

    private static final String[] investigationIdData = new String[]{ "1", "2", "3" };
    private static final String[] investigationEndurantData = new String[]{ "E1", "E2", "E3" };
    private static final String[] investigationTitleData = new String[]{ "Investigation 1", "Investigation 2", "Investigation 3" };

    public SimpleFugeEntriesDatabaseFiller() {
        ApplicationContext ctxt = new ClassPathXmlApplicationContext( "spring-config.xml" );

        controller = ctxt
                .getBean( "fugeDatabaseController", FugeDatabaseController.class );

    }

    public void create() throws JAXBException, SAXException, FileNotFoundException {

        for ( int i = 0, contactsFirstNameDataLength = contactsFirstNameData.length;
              i < contactsFirstNameDataLength; i++ ) {

            String xmlFilename = "/tmp/fugeExample" + ( ( Double ) ( Math.random() * 100 ) ).intValue() + ".xml";

            String schemaFilename = null;

            System.err.println( "Filename for this run: " + xmlFilename );

            FuGECollectionFuGEType fuge = generateSimpleExample( xmlFilename, schemaFilename, i );

            controller.createOrAddFugeVersion( fuge );
        }
    }

    @NotNull
    private FuGECollectionFuGEType generateSimpleExample( String xmlFilename,
                                                          String schemaFilename,
                                                          int position ) throws JAXBException,
            SAXException,
            FileNotFoundException {

        System.err.println( "Schema file is: " + schemaFilename );
        System.err.println( "File for XML output is: " + xmlFilename );

        // First, create FuGE-ML objects
        ObjectFactory factory = new ObjectFactory();

        // create the root object
        FuGECollectionFuGEType root = new FuGECollectionFuGEType();

        // create a creator based on the stored details
        FuGECommonAuditPersonType creator = new FuGECommonAuditPersonType();
        creator.setEmail( contactsEmailData[position] );
        creator.setFirstName( contactsFirstNameData[position] );
        creator.setLastName( contactsLastNameData[position] );
        creator.setIdentifier( ( ( Double ) Math.random() ).toString() );
        creator.setEndurantRef( ( ( Double ) Math.random() ).toString() );

        // add the creator to the AuditCollection
        FuGECollectionAuditCollectionType auditCollection = new FuGECollectionAuditCollectionType();
        auditCollection.getContact().add( factory.createPerson( creator ) );

        // Add the audit trail and the created audit collection to the root object
        root.setAuditCollection( auditCollection );
        createAuditInformation( root, creator.getIdentifier() );

        root.setIdentifier( investigationIdData[position] );
        root.setEndurantRef( investigationEndurantData[position] );
        root.setName( investigationTitleData[position] );
        FuGECollectionProviderType provider = new FuGECollectionProviderType();
        FuGECommonAuditContactRoleType contactRole = new FuGECommonAuditContactRoleType();
        contactRole.setContactRef( creator.getIdentifier() );
//        FuGECommonAuditContactRoleType.Role role = factory.createFuGECommonAuditContactRoleTypeRole();
//        contactRole.setRole(  );
        provider.setContactRole( contactRole );
        root.setProvider( provider );


        // create a JAXBContext capable of handling classes generated into the net.sourceforge.fuge.util.generated
        // package
        JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generated" );

        // create a Marshaller
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.setEventHandler(
                new ValidationEventHandler() {
                    public boolean handleEvent( ValidationEvent e ) {
                        System.out.println( "Validation Error: " + e.getMessage() );
                        return false;
                    }
                } );

        if ( schemaFilename != null ) {
            // set the correct schema
            SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
            Schema schema = sf.newSchema( new File( schemaFilename ) );
            m.setSchema( schema );
        }

        OutputStream os = new FileOutputStream( xmlFilename );
        @SuppressWarnings( "unchecked" )
        JAXBElement element = new JAXBElement(
                new QName( "http://fuge.sourceforge.net/fuge/1.0", "FuGE" ),
                FuGECollectionFuGEType.class,
                root );
        m.marshal( element, os );

        System.err.println( "Generation complete." );

        return root;
    }

    private void createAuditInformation( FuGECommonDescribableType describableXML,
                                         String auditContactIdentifier ) {

        // create jaxb object to hold all audit information
        FuGECommonDescribableType.AuditTrail auditsXML = new FuGECommonDescribableType.AuditTrail();

        // create jaxb object to hold a single audit item
        FuGECommonAuditAuditType auditXML = new FuGECommonAuditAuditType();

        // When adding basic audit information, add the current date, the action and the contact ref, of which
        // the first two are required.
        try {
            auditXML.setDate( DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar() ) );
        } catch ( DatatypeConfigurationException e ) {
            throw new RuntimeException( "Error creating current date", e );
        }

        auditXML.setAction( "creation" );
        auditXML.setContactRef( auditContactIdentifier );

        // add to collection
        auditsXML.getAudit().add( auditXML );

        // load jaxb object into fugeXML
        describableXML.setAuditTrail( auditsXML );


    }
}
