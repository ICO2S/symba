// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Common.Protocol;

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
 * @see fugeOM.Common.Protocol.GenericProtocol
 */
public class GenericProtocolDaoImpl
        extends fugeOM.Common.Protocol.GenericProtocolDaoBase {
    public List getAllLatest( final int transform ) {
        // Retrieves the latest version of all generic protocols in the database
        return super.getAllLatest(
                transform,
                "select gps from fugeOM.Common.Protocol.GenericProtocol as gps " +
                        "join gps.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Common.Protocol.GenericProtocol as internalgp " +
                        "  join internalgp.auditTrail as internalaudits " +
                        "  where internalgp.endurant.id = gps.endurant.id)" );
    }
}