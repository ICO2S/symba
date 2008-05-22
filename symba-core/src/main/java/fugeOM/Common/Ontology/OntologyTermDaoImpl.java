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
                        "join ontologyTerms.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Common.Ontology.OntologyTerm as internalterms " +
                        "  join internalterms.auditTrail as internalaudit " +
                        "  where internalterms.endurant.id = ontologyTerms.endurant.id)" +
                        "and ontologyTerms.ontologySource.endurant.identifier = :sourceEndurant", sourceEndurant );
    }

    public java.util.List getAllLatestUnsourced( final int transform ) {
        // Retrieves all latest versions of those Ontology Terms that do not have an OntologySource
        return super.getAllLatestUnsourced(
                transform,
                "select ontologyTerms from fugeOM.Common.Ontology.OntologyTerm as ontologyTerms " +
                        "join ontologyTerms.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Common.Ontology.OntologyTerm as internalterms " +
                        "                     join internalterms.auditTrail as internalaudit " +
                        "                     where internalterms.endurant.id = ontologyTerms.endurant.id) " +
                        "and ontologyTerms.ontologySource is null");
    }
}