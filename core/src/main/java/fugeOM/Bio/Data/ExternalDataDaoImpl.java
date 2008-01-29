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
        // Retrieves the latest version of all generic materials in the database
        return super.getAllLatest(
                transform,
                "select eds from fugeOM.Bio.Data.ExternalData as eds " +
                        "join eds.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Bio.Data.ExternalData as internaled " +
                        "  join internaled.auditTrail as internalaudits " +
                        "  where internaled.endurant.id = eds.endurant.id)" );
    }
}