// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Common.Ontology;

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
 * @see fugeOM.Common.Ontology.OntologyTerm
 */
public class OntologyTermDaoImpl
        extends fugeOM.Common.Ontology.OntologyTermDaoBase {
    public List getAllLatestWithSource( final int transform, String sourceEndurant ) {
        // Retrieves the latest version of all generic materials in the database
        return super.getAllLatestWithSource(
                transform,
                "select ontologyTerms from fugeOM.Common.Ontology.OntologyTerm as ontologyTerms " +
                        "join ontologyTerms.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Common.Ontology.OntologyTerm as internalgm " +
                        "  join internalgm.auditTrail as internalaudits " +
                        "  where internalgm.endurant.id = ontologyTerms.endurant.id)" +
                        "and ontologyTerms.ontologySource.endurant.identifier = :sourceEndurant", sourceEndurant );
    }
}