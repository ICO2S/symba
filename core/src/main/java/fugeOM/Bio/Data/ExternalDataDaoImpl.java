// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Bio.Data;

import java.util.List;
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
 * @see fugeOM.Bio.Data.ExternalData
 */
public class ExternalDataDaoImpl
        extends fugeOM.Bio.Data.ExternalDataDaoBase {
    public List getAllLatest( final int transform ) {
        // Retrieves the latest version of all external data in the database
        return super.getAllLatest(
                transform,
                "select ed from fugeOM.Bio.Data.ExternalData as ed " +
                        "join ed.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Bio.Data.ExternalData as internaled " +
                        "  join internaled.auditTrail as internalaudit " +
                        "  where internaled.endurant.id = ed.endurant.id)" );
    }
    public List getAllLatestDummies( final int transform ) {
        // Retrieves the latest version of all external data in the database with the dummy string present in the name
        String dummy = "% Dummy%";
        return super.getAllLatestDummies(
                transform,
                "select ed from fugeOM.Bio.Data.ExternalData as ed " +
                        "join ed.auditTrail as audit " +
                        "where ed.name like \'" + dummy + "\' " +
                        "and audit.date = (select max(internalaudit.date) from fugeOM.Bio.Data.ExternalData as internaled " +
                        "  join internaled.auditTrail as internalaudit " +
                        "  where internaled.endurant.id = ed.endurant.id)" );
    }
}