package net.sourceforge.fuge.common.audit;

import org.testng.annotations.Test;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

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
 * $HeadURL$
 */
public class AuditTest {

    // tests whether using a GregorianCalendar is precise enough
    @Test( groups = { "audit", "hibernate" } )
    public void GregorianCalendarTest() {
        EntityService es = ServiceLocator.instance().getEntityService();

        Timestamp timestamp = new Timestamp( new GregorianCalendar().getTimeInMillis() );

        Audit audit = writeAudit( timestamp );

        // retrieve audit from the database
        Audit retrievedAudit = ( Audit ) es.getDescribable( audit.getId() );

        // check to see if the timestamps match (i.e. hasn't lost any information in the storage.
        assert ( retrievedAudit.getDate().equals( audit.getDate() ) ) : "Retrieved Audit date (" + retrievedAudit.getDate()
                + ") does not match loaded Audit date (" + audit.getDate() + ")";
    }

    // tests whether using a java.util.Date is precise enough
    @Test( groups = { "audit", "hibernate" } )
    public void DateTest() {

        EntityService es = ServiceLocator.instance().getEntityService();

        Timestamp timestamp = new Timestamp( new Date().getTime() );
        Audit audit = writeAudit( timestamp );

        // retrieve audit from the database
        Audit retrievedAudit = ( Audit ) es.getDescribable( audit.getId() );

        // check to see if the timestamps match (i.e. hasn't lost any information in the storage.
        assert ( retrievedAudit.getDate().equals( audit.getDate() ) ) : "Retrieved Audit date (" + retrievedAudit.getDate()
                + ") does not match loaded Audit date (" + audit.getDate() + ")";
    }

    private Audit writeAudit( Timestamp timestamp ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setAction( AuditAction.creation );

        audit.setDate( timestamp );

        // load in database
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );

        return audit;
    }

}
