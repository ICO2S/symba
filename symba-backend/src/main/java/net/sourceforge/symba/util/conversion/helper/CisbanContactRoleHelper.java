package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Audit.Contact;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Ontology.OntologyIndividual;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditContactRoleType;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */

public class CisbanContactRoleHelper implements MappingHelper<ContactRole, FugeOMCommonAuditContactRoleType> {

    private final CisbanDescribableHelper cd;

    public CisbanContactRoleHelper() {
        this.cd = new CisbanDescribableHelper();
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ContactRole unmarshal(
            FugeOMCommonAuditContactRoleType contactRoleXML, ContactRole contactRole )
            throws RealizableEntityServiceException {

        contactRole = ( ContactRole ) cd.unmarshal( contactRoleXML, contactRole );

        try {
            // Set the object to exactly the object is that is associated
            // with this version group.
            contactRole.setContact( ( Contact ) reService.findIdentifiable( contactRoleXML.getContactRef() ) );

            // Set the object to exactly the object is that is associated
            // with this version group.
            contactRole.setRole( ( OntologyIndividual ) reService.findIdentifiable( contactRoleXML.getRole().getOntologyTermRef() ) );

            reService.createObInDB( "fugeOM.Common.Audit.ContactRole", contactRole );
        } catch ( RealizableEntityServiceException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error unmarshaling ContactRole" );
        }
        return contactRole;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonAuditContactRoleType marshal( FugeOMCommonAuditContactRoleType contactRoleXML, ContactRole contactRole )
            throws RealizableEntityServiceException {

        contactRoleXML = ( FugeOMCommonAuditContactRoleType ) cd.marshal( contactRoleXML, contactRole );

        contactRoleXML.setContactRef( contactRole.getContact().getIdentifier() );

        FugeOMCommonAuditContactRoleType.Role roleXML = new FugeOMCommonAuditContactRoleType.Role();
        roleXML.setOntologyTermRef( contactRole.getRole().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }

    public FugeOMCommonAuditContactRoleType generateRandomXML( FugeOMCommonAuditContactRoleType contactRoleXML ) {

        contactRoleXML = ( FugeOMCommonAuditContactRoleType ) cd.generateRandomXML( contactRoleXML );

        return contactRoleXML;
    }

    // this method is a litte different from the other generateRandomXMLwithLinksOut, in that it needs to return
    // a contactRole type, so all creation of audit and ontology terms must have already happened outside
    // this method.
    public FugeOMCommonAuditContactRoleType generateRandomXMLwithLinksOut( FugeOMCollectionFuGEType fuGEType ) {

        FugeOMCommonAuditContactRoleType contactRoleXML = generateRandomXML( new FugeOMCommonAuditContactRoleType() );

        contactRoleXML.setContactRef( fuGEType.getAuditCollection().getContact().get( 0 ).getValue().getIdentifier() );

        FugeOMCommonAuditContactRoleType.Role roleXML = new FugeOMCommonAuditContactRoleType.Role();
        roleXML.setOntologyTermRef(
                fuGEType.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }

    public ContactRole getLatestVersion( ContactRole contactRole ) throws RealizableEntityServiceException {

        CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper();
        CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper();

        contactRole = ( ContactRole ) cd.getLatestVersion( contactRole );

        Contact c = contactRole.getContact();
        c = cac.getLatestContactVersion( c );
        contactRole.setContact( c );

        OntologyTerm ot = contactRole.getRole();
        if ( ot instanceof OntologyIndividual ) {
            ot = coc.getLatestOntologyIndividualVersion( ( OntologyIndividual ) ot );
        }
        contactRole.setRole( ot );

        return contactRole;
    }
}
