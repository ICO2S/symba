// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Bio.Material;

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
 * @see fugeOM.Bio.Material.GenericMaterial
 */
public class GenericMaterialDaoImpl
        extends fugeOM.Bio.Material.GenericMaterialDaoBase {
    public List getAllLatest( final int transform ) {
        // Retrieves the latest version of all generic materials in the database
        return super.getAllLatest(
                transform,
                "select gms from fugeOM.Bio.Material.GenericMaterial as gms " +
                        "join gms.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Bio.Material.GenericMaterial as internalgm " +
                        "  join internalgm.auditTrail as internalaudits " +
                        "  where internalgm.endurant.id = gms.endurant.id)" );
    }

    public List getAllLatestDummies( final int transform ) {
        // Retrieves the latest version of all generic materials in the database with the dummy string present in the name
        String dummy = "% Dummy%";
        return super.getAllLatestDummies(
                transform,
                "select gms from fugeOM.Bio.Material.GenericMaterial as gms " +
                        "join gms.auditTrail as audits " +
                        "where gms.name like \'" + dummy + "\' " +
                        "and " +
                        "audits.date = (select max(internalaudits.date) from fugeOM.Bio.Material.GenericMaterial as internalgm " +
                        "  join internalgm.auditTrail as internalaudits " +
                        "  where internalgm.endurant.id = gms.endurant.id)" );
    }
}