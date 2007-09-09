// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Common.Audit;
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

/**
 * @see fugeOM.Common.Audit.Person
 */
public class PersonDaoImpl
        extends fugeOM.Common.Audit.PersonDaoBase {
    public java.util.List getAllLatest( final int transform ) {
        // Retrieves the latest version of all people in the database, irrespective of experimental affiliation.
        return super.getAllLatest(
                transform,
                "select contacts from fugeOM.Common.Audit.Person as contacts " +
                        "join contacts.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Common.Audit.Person as internalc " +
                        "  join internalc.auditTrail as internalaudits " +
                        "  where internalc.endurant.id = contacts.endurant.id)" );
    }
}