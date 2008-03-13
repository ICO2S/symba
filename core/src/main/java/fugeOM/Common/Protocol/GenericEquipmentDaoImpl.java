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
 * @see fugeOM.Common.Protocol.GenericEquipment
 */
public class GenericEquipmentDaoImpl
        extends fugeOM.Common.Protocol.GenericEquipmentDaoBase {
    public List getAllLatest( final int transform ) {
        // Retrieves the latest version of all external data in the database
        return super.getAllLatest(
                transform,
                "select geq from fugeOM.Common.Protocol.GenericEquipment as geq " +
                        "join geq.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Common.Protocol.GenericEquipment as internalgeq " +
                        "  join internalgeq.auditTrail as internalaudit " +
                        "  where internalgeq.endurant.id = geq.endurant.id)" );
    }
    public List getAllLatestDummies( final int transform ) {
        // Retrieves the latest version of all external data in the database with the dummy string present in the name
        String dummy = "% Dummy%";
        return super.getAllLatestDummies(
                transform,
                "select geq from fugeOM.Common.Protocol.GenericEquipment as geq " +
                        "join geq.auditTrail as audit " +
                        "where geq.name like \'" + dummy + "\' " +
                        "and audit.date = (select max(internalaudit.date) from fugeOM.Common.Protocol.GenericEquipment as internalgeq " +
                        "  join internalgeq.auditTrail as internalaudit " +
                        "  where internalgeq.endurant.id = geq.endurant.id)" );
    }
}