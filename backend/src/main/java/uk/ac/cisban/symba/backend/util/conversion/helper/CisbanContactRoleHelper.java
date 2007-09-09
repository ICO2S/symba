package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Audit.Contact;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Ontology.OntologyIndividual;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditContactRoleType;

import java.net.URISyntaxException;

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

public class CisbanContactRoleHelper {

    private final CisbanDescribableHelper cd;
    private final RealizableEntityService reService;

    public CisbanContactRoleHelper( RealizableEntityService reService, CisbanDescribableHelper cd ) {
        this.reService = reService;
        this.cd = cd;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ContactRole unmarshalContactRole(
            FugeOMCommonAuditContactRoleType contactRoleXML ) throws URISyntaxException, RealizableEntityServiceException {

        ContactRole contactRole = ( ContactRole ) reService.createDescribableOb( "fugeOM.Common.Audit.ContactRole" );

        contactRole = ( ContactRole ) cd.unmarshalDescribable( contactRoleXML, contactRole );

        // Set the object to exactly the object is that is associated
        // with this version group.
        contactRole.setContact( ( Contact ) reService.findIdentifiable( contactRoleXML.getContactRef() ) );

        // Set the object to exactly the object is that is associated
        // with this version group.
        contactRole.setRole( ( OntologyIndividual ) reService.findIdentifiable( contactRoleXML.getRole().getOntologyTermRef() ) );

        reService.createObInDB( "fugeOM.Common.Audit.ContactRole", contactRole );
        return contactRole;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonAuditContactRoleType marshalContactRole( ContactRole contactRole ) throws URISyntaxException {

        FugeOMCommonAuditContactRoleType contactRoleXML = new FugeOMCommonAuditContactRoleType();
        contactRoleXML = ( FugeOMCommonAuditContactRoleType ) cd.marshalDescribable( contactRoleXML, contactRole );

        contactRoleXML.setContactRef( contactRole.getContact().getIdentifier() );

        FugeOMCommonAuditContactRoleType.Role roleXML = new FugeOMCommonAuditContactRoleType.Role();
        roleXML.setOntologyTermRef( contactRole.getRole().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }

    // this method is a litte different from the other generateRandomXML, in that it needs to return
    // a contactRole type, so all creation of audit and ontology terms must have already happened outside
    // this method.
    public FugeOMCommonAuditContactRoleType generateRandomXML( FugeOMCollectionFuGEType fuGEType,
                                                               CisbanIdentifiableHelper ci ) {
        FugeOMCommonAuditContactRoleType contactRoleXML = new FugeOMCommonAuditContactRoleType();
        contactRoleXML = ( FugeOMCommonAuditContactRoleType ) cd.generateRandomXML( contactRoleXML, ci );

        contactRoleXML.setContactRef( fuGEType.getAuditCollection().getContact().get( 0 ).getValue().getIdentifier() );

        FugeOMCommonAuditContactRoleType.Role roleXML = new FugeOMCommonAuditContactRoleType.Role();
        roleXML.setOntologyTermRef(
                fuGEType.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }

    public ContactRole getLatestVersion( ContactRole contactRole,
                                         CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper( reService, ci );
        CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper( reService, ci );

        contactRole = ( ContactRole ) cd.getLatestVersion( contactRole, ci );

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
