// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Common.Ontology;
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
 * @see fugeOM.Common.Ontology.OntologySource
 */
public class OntologySourceDaoImpl
        extends fugeOM.Common.Ontology.OntologySourceDaoBase {
    public java.util.List getAllLatest( final int transform ) {
        // Retrieves all latest versions of the Ontology Sources
        return super.getAllLatest(
                transform,
                "select sources from fugeOM.Common.Ontology.OntologySource as sources " +
                        "join sources.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Common.Ontology.OntologySource as internalsources " +
                        "                     join internalsources.auditTrail as internalaudits " +
                        "                     where internalsources.endurant.id = sources.endurant.id)");
    }
}