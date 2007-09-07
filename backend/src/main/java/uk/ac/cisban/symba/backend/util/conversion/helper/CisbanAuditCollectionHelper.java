package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.AuditCollection;
import fugeOM.Common.Audit.*;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.OntologyIndividual;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import javax.xml.bind.JAXBElement;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanAuditCollectionHelper.java $
 *
 */

public class CisbanAuditCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanAuditCollectionHelper( RealizableEntityService reService,
                                        CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.cd = ci.getCisbanDescribableHelper();
        this.ci = ci;
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public AuditCollection unmarshalAuditCollection(
            FugeOMCollectionAuditCollectionType auditCollXML ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // create fuge object
        AuditCollection auditColl = ( AuditCollection ) reService.createDescribableOb(
                "fugeOM.Collection.AuditCollection" );

        // set describable information for the AuditCollection
        auditColl = ( AuditCollection ) cd.unmarshalDescribable( auditCollXML, auditColl );
        auditColl = unmarshalCollectionContents( auditCollXML, auditColl );

        // Once all of the AuditCollection is full, add it to the database.
        reService.createObInDB( "fugeOM.Collection.AuditCollection", auditColl );

        return auditColl;
    }

    public AuditCollection unmarshalCollectionContents( FugeOMCollectionAuditCollectionType auditCollXML,
                                                        AuditCollection auditColl ) throws LSIDException, RealizableEntityServiceException, URISyntaxException {
        // Get contacts from AuditCollection
        List<JAXBElement<? extends FugeOMCommonAuditContactType>> contactsXML = auditCollXML.getContact();
        Set<Contact> contacts = new HashSet<Contact>();

        // An inherent limitation is that all Organizations MUST be loaded before Persons,
        // as Person can reference an organization. This means, unfortunately, two for-loops to
        // guarantee the order.
        for ( JAXBElement<? extends FugeOMCommonAuditContactType> contactElementXML : contactsXML ) {
            // create jaxb object
            FugeOMCommonAuditContactType contactXML = contactElementXML.getValue();

            // Discover if Person or Organization
            if ( contactXML instanceof FugeOMCommonAuditOrganizationType ) {
                // create jaxb object
                FugeOMCommonAuditOrganizationType organizationXML = ( FugeOMCommonAuditOrganizationType ) contactXML;

                // Retrieve latest version from the database.
                Organization organization = ( Organization ) helper.getOrCreateLatest(
                        organizationXML.getEndurant(),
                        "fugeOM.Common.Audit.OrganizationEndurant",
                        organizationXML.getName(),
                        "fugeOM.Common.Audit.Organization",
                        System.err );

                // set fuge object
                organization = ( Organization ) unmarshallContact( organizationXML, organization );

                // set organization traits, if present
                if ( organizationXML.getParent() != null ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    organization.setParent( ( Organization ) reService.findIdentifiable( organizationXML.getParent().getOrganizationRef() ) );
                }

                // load fuge object into database
                if ( organization.getId() != null ) {
                    helper.assignAndLoadIdentifiable( organization, "fugeOM.Common.Audit.Organization", System.err );
                } else {
                    helper.loadIdentifiable( organization, "fugeOM.Common.Audit.Organization", System.err );
                }

                // add fuge object into collection of objects
                contacts.add( organization );

            }
        }
        for ( JAXBElement<? extends FugeOMCommonAuditContactType> contactElementXML : contactsXML ) {
            // create jaxb object
            FugeOMCommonAuditContactType contactXML = contactElementXML.getValue();
            if ( contactXML instanceof FugeOMCommonAuditPersonType ) {
                // create jaxb object
                FugeOMCommonAuditPersonType personXML = ( FugeOMCommonAuditPersonType ) contactXML;

                // Retrieve latest version from the database.
                Person person = ( Person ) helper.getOrCreateLatest(
                        personXML.getEndurant(),
                        "fugeOM.Common.Audit.PersonEndurant",
                        personXML.getName(),
                        "fugeOM.Common.Audit.Person",
                        System.err );

                // set fuge object
                person = ( Person ) unmarshallContact( personXML, person );

                // set person traits
                String temp;
                if ( ( temp = personXML.getFirstName() ) != null )
                    person.setFirstName( temp );
                if ( ( temp = personXML.getLastName() ) != null )
                    person.setLastName( temp );
                if ( ( temp = personXML.getMidInitials() ) != null )
                    person.setMidInitials( temp );

                Set<Organization> affiliations = new HashSet<Organization>();
                for ( FugeOMCommonAuditPersonType.Affiliations affiliationXML : personXML.getAffiliations() ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    affiliations.add( ( Organization ) reService.findIdentifiable( affiliationXML.getOrganizationRef() ) );
                }
                person.setAffiliations( affiliations );

                // load fuge object into database
                if ( person.getId() != null ) {
                    helper.assignAndLoadIdentifiable( person, "fugeOM.Common.Audit.Person", System.err );
                } else {
                    helper.loadIdentifiable( person, "fugeOM.Common.Audit.Person", System.err );
                }

                // add fuge object into collection of objects
                contacts.add( person );
            }
        }
        // set collection of contacts as the contacts for this AuditCollection.
        auditColl.setAllContacts( contacts );

        // set the security and security group
        Set<SecurityGroup> sgs = new HashSet<SecurityGroup>();
        for ( FugeOMCommonAuditSecurityGroupType sgXML : auditCollXML.getSecurityGroup() ) {
            SecurityGroup sg = ( SecurityGroup ) helper.getOrCreateLatest(
                    sgXML.getEndurant(),
                    "fugeOM.Common.Audit.SecGroupEndurant",
                    sgXML.getName(),
                    "fugeOM.Common.Audit.SecurityGroup",
                    System.err );
            sg = ( SecurityGroup ) ci.unmarshalIdentifiable( sgXML, sg );

            Set<Contact> cs = new HashSet<Contact>();
            for ( FugeOMCommonAuditSecurityGroupType.Members memXML : sgXML.getMembers() ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                for ( Contact c : contacts ) {
                    if ( c.getIdentifier().equals( memXML.getContactRef() ) ) {
                        cs.add( c );
                        break;
                    }
                }
                cs.add( ( Contact ) reService.findIdentifiable( memXML.getContactRef() ) );
            }
            sg.setMembers( cs );

            // load fuge object into database
            if ( sg.getId() != null ) {
                helper.assignAndLoadIdentifiable( sg, "fugeOM.Common.Audit.SecurityGroup", System.err );
            } else {
                helper.loadIdentifiable( sg, "fugeOM.Common.Audit.SecurityGroup", System.err );
            }
            sgs.add( sg );
        }
        auditColl.setSecurityGroups( sgs );

        // fixme should owner really be a collection??
        Set<Security> securities = new HashSet<Security>();
        for ( FugeOMCommonAuditSecurityType securityXML : auditCollXML.getSecurity() ) {
            // Retrieve latest version from the database.
            Security security = ( Security ) helper.getOrCreateLatest(
                    securityXML.getEndurant(),
                    "fugeOM.Common.Audit.SecurityEndurant",
                    securityXML.getName(),
                    "fugeOM.Common.Audit.Security",
                    System.err );
            security = ( Security ) ci.unmarshalIdentifiable( securityXML, security );

            Set<Contact> cs = new HashSet<Contact>();
            for ( FugeOMCommonAuditSecurityType.Owner owner : securityXML.getOwner() ) {
                cs.add( ( Contact ) reService.findIdentifiable( owner.getContactRef() ) );
            }
            security.setOwner( cs );

            Set<SecurityAccess> accesses = new HashSet<SecurityAccess>();
            for ( FugeOMCommonAuditSecurityAccessType accessXML : securityXML.getSecurityAccess() ) {
                SecurityAccess access = ( SecurityAccess ) reService.createDescribableOb(
                        "fugeOM.Common.Audit.SecurityAccess" );
                access = ( SecurityAccess ) cd.unmarshalDescribable( accessXML, access );

                if ( accessXML.getAccessRight() != null ) {
                    FugeOMCommonAuditSecurityAccessType.AccessRight accessRightXML = accessXML.getAccessRight();
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    // we will have a problem here if the ontologycollection hasn't been loaded yet.
                    // fixme: temp solution is to simply not load these access rights if there is no ontology collection.
                    try {
                        access.setAccessRight( ( OntologyIndividual ) reService.findIdentifiable( accessRightXML.getOntologyTermRef() ) );
                    } catch ( fugeOM.service.RealizableEntityServiceException e ) {
                        System.err.println(
                                "No Ontology Individual found: NOT LOADING THIS SECURITY ACCESS RIGHT! = " +
                                        accessXML.getAccessRight().getOntologyTermRef() );
                    }
                }
                if ( accessXML.getSecurityGroupRef() != null ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    access.setAccessGroup( ( SecurityGroup ) reService.findIdentifiable( accessXML.getSecurityGroupRef() ) );
                }
                reService.createObInDB( "fugeOM.Common.Audit.SecurityAccess", access );
                accesses.add( access );
            }
            security.setSecurityRights( accesses );

            // load fuge object into database
            if ( security.getId() != null ) {
                helper.assignAndLoadIdentifiable( security, "fugeOM.Common.Audit.Security", System.err );
            } else {
                helper.loadIdentifiable( security, "fugeOM.Common.Audit.Security", System.err );
            }
            securities.add( security );
        }

        auditColl.setSecurityCollection( securities );

        return auditColl;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionAuditCollectionType marshalAuditCollection(
            AuditCollection auditColl ) throws URISyntaxException, RealizableEntityServiceException {

        // create jaxb object
        FugeOMCollectionAuditCollectionType auditCollXML = new FugeOMCollectionAuditCollectionType();

        // set describable information
        auditCollXML = ( FugeOMCollectionAuditCollectionType ) cd.marshalDescribable( auditCollXML, auditColl );

        // Contacts

        ObjectFactory factory = new ObjectFactory();
        for ( Object contactObj : auditColl.getAllContacts() ) {
            Contact contact = ( Contact ) contactObj;

            // Discover if Person or Organization
            if ( contact instanceof Organization ) {
                Organization organization = ( Organization ) contact;

                // create jaxb object
                FugeOMCommonAuditOrganizationType organizationXML = new FugeOMCommonAuditOrganizationType();

                // set jaxb object
                organizationXML = ( FugeOMCommonAuditOrganizationType ) marshalContact( organizationXML, organization );

                // set organization traits
                organizationXML = marshalOrganization( organizationXML, organization );

                // add jaxb object into collection of objects
                auditCollXML.getContact().add( factory.createOrganization( organizationXML ) );

            } else if ( contact instanceof Person ) {
                Person person = ( Person ) contact;

                // create jaxb object
                FugeOMCommonAuditPersonType personXML = new FugeOMCommonAuditPersonType();

                personXML = ( FugeOMCommonAuditPersonType ) marshalContact( personXML, person );

                // set jaxb object
                personXML = marshalPerson( personXML, person );

                // add jaxb object into collection of objects
                auditCollXML.getContact().add( factory.createPerson( personXML ) );
            }
        }
        // set the security and security group
        for ( Object obj : auditColl.getSecurityGroups() ) {
            SecurityGroup sg = ( SecurityGroup ) obj;
            FugeOMCommonAuditSecurityGroupType sgXML = new FugeOMCommonAuditSecurityGroupType();
            sgXML = ( FugeOMCommonAuditSecurityGroupType ) ci.marshalIdentifiable( sgXML, sg );

            sg = ( SecurityGroup ) reService.greedyGet( sg );
            for ( Object obj2 : sg.getMembers() ) {
                FugeOMCommonAuditSecurityGroupType.Members memXML = new FugeOMCommonAuditSecurityGroupType.Members();
                memXML.setContactRef( ( ( Contact ) obj2 ).getIdentifier() );
                sgXML.getMembers().add( memXML );
            }
            auditCollXML.getSecurityGroup().add( sgXML );
        }

        // fixme should owner really be a collection??
        for ( Object obj : auditColl.getSecurityCollection() ) {
            Security security = ( Security ) obj;
            FugeOMCommonAuditSecurityType securityXML = new FugeOMCommonAuditSecurityType();
            securityXML = ( FugeOMCommonAuditSecurityType ) ci.marshalIdentifiable( securityXML, security );

            security = ( Security ) reService.greedyGet( security );
            for ( Object obj2 : security.getOwner() ) {
                Contact c = ( Contact ) obj2;
                FugeOMCommonAuditSecurityType.Owner ownerXML = new FugeOMCommonAuditSecurityType.Owner();
                ownerXML.setContactRef( c.getIdentifier() );
                securityXML.getOwner().add( ownerXML );
            }

            for ( Object obj3 : security.getSecurityRights() ) {
                SecurityAccess sa = ( SecurityAccess ) obj3;
                FugeOMCommonAuditSecurityAccessType accessXML = new FugeOMCommonAuditSecurityAccessType();
                accessXML = ( FugeOMCommonAuditSecurityAccessType ) cd.marshalDescribable( accessXML, sa );

                if ( sa.getAccessRight() != null ) {
                    FugeOMCommonAuditSecurityAccessType.AccessRight accessRightXML = new FugeOMCommonAuditSecurityAccessType.AccessRight();
                    accessRightXML.setOntologyTermRef( sa.getAccessRight().getIdentifier() );
                    accessXML.setAccessRight( accessRightXML );
                }
                if ( sa.getAccessGroup() != null ) {
                    sa = ( SecurityAccess ) reService.greedyGet( sa );
                    accessXML.setSecurityGroupRef( sa.getAccessGroup().getIdentifier() );
                }
                securityXML.getSecurityAccess().add( accessXML );
            }
            auditCollXML.getSecurity().add( securityXML );
        }

        return auditCollXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType fuGEType ) {
        // create jaxb object
        FugeOMCollectionAuditCollectionType auditCollXML = new FugeOMCollectionAuditCollectionType();

        // set describable information
        auditCollXML = ( FugeOMCollectionAuditCollectionType ) cd.generateRandomXML( auditCollXML, ci );

        // Contacts
        String firstOrg = null;
        ObjectFactory factory = new ObjectFactory();
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create jaxb object
            FugeOMCommonAuditOrganizationType organizationXML = new FugeOMCommonAuditOrganizationType();

            // set jaxb object
            organizationXML = ( FugeOMCommonAuditOrganizationType ) generateRandomContactXML( organizationXML );

            // set organization traits - only set a parent if i > 0.

            if ( i > 0 ) {
                FugeOMCommonAuditOrganizationType.Parent parentOrganizationXML = new FugeOMCommonAuditOrganizationType.Parent();
                parentOrganizationXML.setOrganizationRef( firstOrg );
                organizationXML.setParent( parentOrganizationXML );
            } else {
                firstOrg = organizationXML.getIdentifier();
            }

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createOrganization( organizationXML ) );

            // create jaxb object
            FugeOMCommonAuditPersonType personXML = new FugeOMCommonAuditPersonType();

            personXML = ( FugeOMCommonAuditPersonType ) generateRandomContactXML( personXML );

            // set jaxb object
            personXML = generateRandomPersonXML( personXML, organizationXML );

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createPerson( personXML ) );
        }

        // set the security and security group
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonAuditSecurityGroupType sgXML = new FugeOMCommonAuditSecurityGroupType();
            sgXML = ( FugeOMCommonAuditSecurityGroupType ) ci.generateRandomXML( sgXML );

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FugeOMCommonAuditSecurityGroupType.Members memXML = new FugeOMCommonAuditSecurityGroupType.Members();
                memXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                sgXML.getMembers().add( memXML );
            }
            auditCollXML.getSecurityGroup().add( sgXML );
        }

        // fixme should owner really be a collection??
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonAuditSecurityType securityXML = new FugeOMCommonAuditSecurityType();
            securityXML = ( FugeOMCommonAuditSecurityType ) ci.generateRandomXML( securityXML );

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FugeOMCommonAuditSecurityType.Owner ownerXML = new FugeOMCommonAuditSecurityType.Owner();
                ownerXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                securityXML.getOwner().add( ownerXML );
            }

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FugeOMCommonAuditSecurityAccessType accessXML = new FugeOMCommonAuditSecurityAccessType();
                accessXML = ( FugeOMCommonAuditSecurityAccessType ) cd.generateRandomXML( accessXML, ci );

                FugeOMCommonAuditSecurityAccessType.AccessRight accessRightXML = new FugeOMCommonAuditSecurityAccessType.AccessRight();
                if ( fuGEType.getOntologyCollection() == null ) {
                    CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper( reService, ci );
                    fuGEType = coc.generateRandomXML( fuGEType );
                }
                accessRightXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( ii ).getValue().getIdentifier() );
                accessXML.setAccessRight( accessRightXML );

                accessXML.setSecurityGroupRef( auditCollXML.getSecurityGroup().get( ii ).getIdentifier() );
                securityXML.getSecurityAccess().add( accessXML );
            }
            auditCollXML.getSecurity().add( securityXML );
        }

        fuGEType.setAuditCollection( auditCollXML );
        return fuGEType;
    }

    private FugeOMCommonAuditContactType generateRandomContactXML( FugeOMCommonAuditContactType contactXML ) {
        // set all identifiable traits in the jaxb object
        contactXML = ( FugeOMCommonAuditContactType ) ci.generateRandomXML( contactXML );

        // set all non-identifiable contact traits
        contactXML.setAddress( String.valueOf( Math.random() ) );
        contactXML.setEmail( String.valueOf( Math.random() ) );
        contactXML.setFax( String.valueOf( Math.random() ) );
        contactXML.setPhone( String.valueOf( Math.random() ) );
        contactXML.setTollFreePhone( String.valueOf( Math.random() ) );

        return contactXML;
    }

    private FugeOMCommonAuditPersonType generateRandomPersonXML(
            FugeOMCommonAuditPersonType personXML,
            FugeOMCommonAuditOrganizationType organizationXML ) {

        // set person traits
        personXML.setFirstName( String.valueOf( Math.random() ) );
        personXML.setLastName( String.valueOf( Math.random() ) );
        personXML.setMidInitials( String.valueOf( Math.random() ) );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonAuditPersonType.Affiliations affiliationXML = new FugeOMCommonAuditPersonType.Affiliations();

            affiliationXML.setOrganizationRef( organizationXML.getIdentifier() );
            personXML.getAffiliations().add( affiliationXML );
        }
        return personXML;
    }

    public FugeOMCommonAuditPersonType marshalPerson( FugeOMCommonAuditPersonType personXML,
                                                      Person person ) {

        // set person traits
        String temp;
        if ( ( temp = person.getFirstName() ) != null )
            personXML.setFirstName( temp );
        if ( ( temp = person.getLastName() ) != null )
            personXML.setLastName( temp );
        if ( ( temp = person.getMidInitials() ) != null )
            personXML.setMidInitials( temp );

        Collection affiliations;
        if ( !( affiliations = person.getAffiliations() ).isEmpty() ) {
            for ( Object affiliationObj : affiliations ) {
                Organization affiliation = ( Organization ) affiliationObj;
                FugeOMCommonAuditPersonType.Affiliations affiliationXML = new FugeOMCommonAuditPersonType.Affiliations();

                affiliationXML.setOrganizationRef( affiliation.getIdentifier() );
                personXML.getAffiliations().add( affiliationXML );
            }
        }
        return personXML;
    }

    public FugeOMCommonAuditOrganizationType marshalOrganization( FugeOMCommonAuditOrganizationType organizationXML,
                                                                  Organization organization ) {
        if ( organization.getParent() != null ) {
            FugeOMCommonAuditOrganizationType.Parent parentOrganizationXML = new FugeOMCommonAuditOrganizationType.Parent();
            parentOrganizationXML.setOrganizationRef( organization.getParent().getIdentifier() );
            organizationXML.setParent( parentOrganizationXML );
        }
        return organizationXML;
    }

    private Contact unmarshallContact( FugeOMCommonAuditContactType contactXML,
                                       Contact contact ) throws URISyntaxException, RealizableEntityServiceException {
        // set all identifiable traits in the fuge object
        contact = ( Contact ) ci.unmarshalIdentifiable( contactXML, contact );

        String temp;
        // set all non-identifiable contact traits
        if ( ( temp = contactXML.getAddress() ) != null )
            contact.setAddress( temp );
        if ( ( temp = contactXML.getEmail() ) != null )
            contact.setEmail( temp );
        if ( ( temp = contactXML.getFax() ) != null )
            contact.setFax( temp );
        if ( ( temp = contactXML.getPhone() ) != null )
            contact.setPhone( temp );
        if ( ( temp = contactXML.getTollFreePhone() ) != null )
            contact.setTollFreePhone( temp );

        return contact;
    }

    public FugeOMCommonAuditContactType marshalContact( FugeOMCommonAuditContactType contactXML,
                                                        Contact contact ) throws URISyntaxException {
        // set all identifiable traits in the jaxb object
        contactXML = ( FugeOMCommonAuditContactType ) ci.marshalIdentifiable( contactXML, contact );

        String temp;
        // set all non-identifiable contact traits
        if ( ( temp = contact.getAddress() ) != null )
            contactXML.setAddress( temp );
        if ( ( temp = contact.getEmail() ) != null )
            contactXML.setEmail( temp );
        if ( ( temp = contact.getFax() ) != null )
            contactXML.setFax( temp );
        if ( ( temp = contact.getPhone() ) != null )
            contactXML.setPhone( temp );
        if ( ( temp = contact.getTollFreePhone() ) != null )
            contactXML.setTollFreePhone( temp );

        return contactXML;
    }

    // Get the latest version of any identifiable object(s) in this Collection
    public AuditCollection getLatestVersion( AuditCollection auditColl ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        auditColl = ( AuditCollection ) cd.getLatestVersion( auditColl, ci );

        // prepare updated set
        Set<Contact> contacts = new HashSet<Contact>();

        // load all the latest versions into the new set.
        for ( Object obj : auditColl.getAllContacts() ) {
            Contact contact = ( Contact ) obj;
            contact = getLatestContactVersion( contact );
            contacts.add( contact );
        }

        // set collection of contacts as the contacts for this AuditCollection.
        auditColl.setAllContacts( contacts );

        // prepare updated set
        Set<SecurityGroup> sgs = new HashSet<SecurityGroup>();
        // load all the latest versions into the new set.
        for ( Object obj : auditColl.getSecurityGroups() ) {
            SecurityGroup sg = ( SecurityGroup ) obj;
            sg = ( SecurityGroup ) ci.getLatestVersion( sg );
            sg = ( SecurityGroup ) reService.greedyGet( sg );

            contacts.clear();
            for ( Object obj2 : sg.getMembers() ) {
                Contact contact = ( Contact ) obj2;
                contact = getLatestContactVersion( contact );
                contacts.add( contact );
            }
            sg.setMembers( contacts );
            sgs.add( sg );
        }
        auditColl.setSecurityGroups( sgs );

        // prepare updated set
        Set<Security> securities = new HashSet<Security>();
        // load all the latest versions into the new set.
        // fixme should owner really be a collection??
        for ( Object obj : auditColl.getSecurityCollection() ) {
            Security security = ( Security ) obj;
            security = ( Security ) ci.getLatestVersion( security );
            security = ( Security ) reService.greedyGet( security );

            contacts.clear();
            for ( Object obj2 : security.getOwner() ) {
                Contact c = ( Contact ) obj2;
                c = getLatestContactVersion( c );
                contacts.add( c );
            }
            security.setOwner( contacts );

            // prepare updated set
            Set<SecurityAccess> accesses = new HashSet<SecurityAccess>();
            // load all the latest versions into the new set.
            for ( Object obj3 : security.getSecurityRights() ) {
                SecurityAccess sa = ( SecurityAccess ) obj3;
                sa = ( SecurityAccess ) cd.getLatestVersion( sa, ci );
                sa = ( SecurityAccess ) reService.greedyGet( sa );

                if ( sa.getAccessRight() != null ) {
                    OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                            sa.getAccessRight().getEndurant().getIdentifier() );
                    sa.setAccessRight( ( OntologyTerm ) ci.getLatestVersion( ot ) );
                }
                if ( sa.getAccessGroup() != null ) {
                    SecurityGroup sg = ( SecurityGroup ) reService.findLatestByEndurant(
                            sa.getAccessGroup().getEndurant().getIdentifier() );
                    sa.setAccessGroup( ( SecurityGroup ) ci.getLatestVersion( sg ) );
                }
                accesses.add( sa );
            }
            security.setSecurityRights( accesses );
            securities.add( security );
        }
        auditColl.setSecurityCollection( securities );

        return auditColl;
    }

    public Contact getLatestContactVersion( Contact contact ) throws RealizableEntityServiceException {
        // get the latest version of the identifiables in this object
        contact = ( Contact ) reService.findLatestByEndurant( contact.getEndurant().getIdentifier() );
        contact = ( Contact ) ci.getLatestVersion( contact );

        // It matters if its a Person or Organization, as they have further fuge objects in them
        if ( contact instanceof Person ) {
            Person person = ( Person ) contact;
            person = getLatestPersonVersion( person );
            return person;
        } else if ( contact instanceof Organization ) {
            Organization organization = ( Organization ) contact;
            organization = getLatestOrganizationVersion( organization );
            return organization;
        }
        return contact; // should never reach this point unless another child of Contact has been created without updating the code.
    }

    private Organization getLatestOrganizationVersion(
            Organization organization ) throws RealizableEntityServiceException {

        // ensure the reference is completely up to date.
        if ( organization.getParent() != null ) {
            Identifiable identifiable = organization.getParent();
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            organization.setParent( ( Organization ) ci.getLatestVersion( identifiable ) );
        }

        return organization;
    }

    // Get the latest version of any identifiable object(s) in this object
    private Person getLatestPersonVersion( Person person ) throws RealizableEntityServiceException {

        // prepare updated set
        Set<Organization> set = new HashSet<Organization>();

        // load all the latest versions into the new set.
        for ( Object obj : person.getAffiliations() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set.add( ( Organization ) ci.getLatestVersion( identifiable ) );
        }
        person.setAffiliations( set );

        return person;
    }
}
