// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Collection;
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
 * @see fugeOM.Collection.FuGE
 */
public class FuGEDaoImpl
        extends fugeOM.Collection.FuGEDaoBase {
    public java.util.List getAllLatestWithContact( final int transform, final java.lang.String endurantId ) {
        // Retrieves all experiments whose *latest* version contains a contact with the
        // endurant identifier "endurantId".
        return super.getAllLatestWithContact(
                transform,
                "select exp from fugeOM.Collection.FuGE as exp " +
                        "join exp.auditCollection.allContacts as contacts " +
                        "join exp.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Collection.FuGE as internalexp " +
                        "                     join internalexp.auditTrail as internalaudit " +
                        "                     where internalexp.endurant.id = exp.endurant.id) " +
                        "and " +
                        "contacts.endurant.identifier = :endurantId ", endurantId );
    }

    public java.util.List getAllLatestIdsWithContact( final int transform, final java.lang.String endurantId ) {
        // Retrieves all experiments whose *latest* version contains a contact with the
        // endurant identifier "endurantId".
        return super.getAllLatestWithContact(
                transform,
                "select exp.identifier from fugeOM.Collection.FuGE as exp " +
                        "join exp.auditCollection.allContacts as contacts " +
                        "join exp.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Collection.FuGE as internalexp " +
                        "                     join internalexp.auditTrail as internalaudit " +
                        "                     where internalexp.endurant.id = exp.endurant.id) " +
                        "and " +
                        "contacts.endurant.identifier = :endurantId ", endurantId );
    }

    public java.util.List getAllLatestIds( final int transform ) {
        // Retrieves all *latest* versions of every experiment in the database
        return super.getAllLatestIds(
                transform,
                "select exp.identifier from fugeOM.Collection.FuGE as exp " +
                        "join exp.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Collection.FuGE as internalexp " +
                        "                     join internalexp.auditTrail as internalaudit " +
                        "                     where internalexp.endurant.id = exp.endurant.id) " );
    }

    public java.util.List getAllWithContact( final int transform, final java.lang.String endurantId ) {
        // Retrieves all versions of experiments containing a contact with the
        // endurant identifier "endurantId".
        return super.getAllWithContact(
                transform,
                "select exp from fugeOM.Collection.FuGE as exp " +
                        "join exp.auditCollection.allContacts as contacts " +
                        "where contacts.endurant.identifier = :endurantId", endurantId );
    }

    public java.util.List getAllLatestIdsWithName( final int transform, final java.lang.String investigationName ) {
        // Retrieves all versions of experiments containing a the investigation name investigationName
        // Searches all experiments: NOT restricted by person
        return super.getAllLatestIdsWithName(
                transform,
                "select exp.identifier from fugeOM.Collection.FuGE as exp " +
                        "join exp.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Collection.FuGE as internalexp " +
                        "                     join internalexp.auditTrail as internalaudit " +
                        "                     where internalexp.endurant.id = exp.endurant.id) " +
                        "and " +
                        "exp.name like :investigationName", "%" + investigationName + "%");
    }

    public java.util.List getAllLatestIdsWithOntologyTerm( final int transform, final java.lang.String endurantId ) {
        // Retrieves all experiments whose *latest* version contains a the OntologyTerm endurantId provided
        // Searches all experiments: NOT restricted by person
        System.err.println( "endurantId is " + endurantId);
        return super.getAllLatestIdsWithOntologyTerm(
                transform,
                "select exp.identifier from fugeOM.Collection.FuGE as exp " +
                        "join exp.ontologyCollection.ontologyTerms as term " +
                        "join exp.auditTrail as audit " +
                        "where audit.date = (select max(internalaudit.date) from fugeOM.Collection.FuGE as internalexp " +
                        "                     join internalexp.auditTrail as internalaudit " +
                        "                     where internalexp.endurant.id = exp.endurant.id) " +
                        "and " +
                        "term.endurant.identifier = :endurantId", endurantId );
    }

}