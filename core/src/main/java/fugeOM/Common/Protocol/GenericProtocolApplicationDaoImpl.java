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
 * @see fugeOM.Common.Protocol.GenericProtocolApplication
 */
public class GenericProtocolApplicationDaoImpl
        extends fugeOM.Common.Protocol.GenericProtocolApplicationDaoBase {
    public List getAllLatest( final int transform ) {
        // Retrieves the latest version of all generic PAs in the database
        return super.getAllLatest(
                transform,
                "select gpas from fugeOM.Common.Protocol.GenericProtocolApplication as gpas " +
                        "join gpas.auditTrail as audits " +
                        "where " +
                        "audits.date = (select max(internalaudits.date) from fugeOM.Common.Protocol.GenericProtocolApplication as internalgpa " +
                        "  join internalgpa.auditTrail as internalaudits " +
                        "  where internalgpa.endurant.id = gpas.endurant.id)");
    }

    public List getAllLatestDummies( final int transform ) {
        // Retrieves the latest version of all generic PAs in the database with the dummy string present in the name
        String dummy = "% Dummy%";
        return super.getAllLatestDummies(
                transform,
                "select gpas from fugeOM.Common.Protocol.GenericProtocolApplication as gpas " +
                        "join gpas.auditTrail as audits " +
                        "where gpas.name like \'" + dummy + "\' " +
                        "and " +
                        "audits.date = (select max(internalaudits.date) from fugeOM.Common.Protocol.GenericProtocolApplication as internalgpa " +
                        "  join internalgpa.auditTrail as internalaudits " +
                        "  where internalgpa.endurant.id = gpas.endurant.id)" );
    }
}