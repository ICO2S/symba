package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.AuditCollection;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.common.audit.*;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.PersonBean;

import java.util.HashSet;
import java.util.Set;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/loading/LoadFuge.java $
 */
public class AuditLoader {

    public static Person setAuditor( SymbaEntityService symbaEntityService, PersonBean personBean ) {

        Person person = ( Person ) symbaEntityService.getLatestByEndurant( personBean.getEndurantLsid() );
        if ( person == null ) {
            // should never need to make a new person at this stage
            // todo put in some error handling code here for if the person isn't found in the database
            person =
                    ( Person ) DatabaseObjectHelper
                            .createEndurantAndIdentifiable( "net.sourceforge.fuge.common.audit.Person", null );
        }

        return person;
    }

    /**
     * Load the person and their affiliations into the AuditCollection of the FuGE object
     *
     * @param fuge          the object to add the information to
     * @param entityService the connection to the database
     * @param auditor       the person to assign as the creator of these objects
     * @return the modified FuGE object
     */
    public static FuGE addPersonToExperiment( FuGE fuge, EntityService entityService, Person auditor ) {

        // Create the Audit Collection (based on existing information if present)
        Set<Contact> contacts = new HashSet<Contact>();
        Set<Security> securities = new HashSet<Security>();
        Set<SecurityGroup> securityGroups = new HashSet<SecurityGroup>();
        if ( fuge.getAuditCollection() != null ) {
            if ( fuge.getAuditCollection().getAllContacts() != null ) {
                contacts = ( Set<Contact> ) fuge.getAuditCollection().getAllContacts();
            }
            if ( fuge.getAuditCollection().getSecurityCollection() != null ) {
                securities = ( Set<Security> ) fuge.getAuditCollection().getSecurityCollection();
            }
            if ( fuge.getAuditCollection().getSecurityGroups() != null ) {
                securityGroups = ( Set<SecurityGroup> ) fuge.getAuditCollection().getSecurityGroups();
            }
        }

        AuditCollection auditColl = ( AuditCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.AuditCollection" );

        // add any affiliation to the contact list
        if ( auditor.getAffiliations() != null && !auditor.getAffiliations().isEmpty() ) {
            Set<Organization> affiliations = ( Set<Organization> ) auditor.getAffiliations();
            if ( affiliations != null && !affiliations.isEmpty() ) {

                // todo - do we need to check for latest version of the affiliations?
                contacts.addAll( affiliations );
            }
        }
        // And we can now add the person to the list of contacts, if not already there
        boolean matchFound = false;
        for ( Contact contact : contacts ) {
            if ( contact.getEndurant().getIdentifier()
                    .equals( auditor.getEndurant().getIdentifier() ) ) {
                matchFound = true;
                break;
            }
        }
        if ( !matchFound ) {
            // And we can now add the person to the list of contacts
            contacts.add( auditor );
        }

        // And now that we're finished adding contacts, we can add the list of contacts
        // to the audit collection...
        auditColl.setAllContacts( contacts );
        auditColl.setSecurityCollection( securities );
        auditColl.setSecurityGroups( securityGroups );
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.AuditCollection", auditColl, auditor );

        fuge.setAuditCollection( auditColl );

        return fuge;
    }
}
