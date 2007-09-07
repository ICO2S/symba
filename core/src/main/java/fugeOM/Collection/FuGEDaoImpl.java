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
 * $LastChangedDate: 2007-08-13 13:05:06 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 547 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/core/src/main/java/fugeOM/Collection/FuGEDaoImpl.java $
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
                "select exps from fugeOM.Collection.FuGE as exps " +
                        "join exps.auditCollection.allContacts as contacts " +
                        "join exps.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Collection.FuGE as internalexps " +
                        "                     join internalexps.auditTrail as internalaudits " +
                        "                     where internalexps.endurant.id = exps.endurant.id) " +
                        "and " +
                        "contacts.endurant.identifier = :endurantId ", endurantId );
    }

    public java.util.List getAllLatestIdsWithContact( final int transform, final java.lang.String endurantId ) {
        // Retrieves all experiments whose *latest* version contains a contact with the
        // endurant identifier "endurantId".
        return super.getAllLatestWithContact(
                transform,
                "select exps.identifier from fugeOM.Collection.FuGE as exps " +
                        "join exps.auditCollection.allContacts as contacts " +
                        "join exps.auditTrail as audits " +
                        "where audits.date = (select max(internalaudits.date) from fugeOM.Collection.FuGE as internalexps " +
                        "                     join internalexps.auditTrail as internalaudits " +
                        "                     where internalexps.endurant.id = exps.endurant.id) " +
                        "and " +
                        "contacts.endurant.identifier = :endurantId ", endurantId );
    }

    public java.util.List getAllWithContact( final int transform, final java.lang.String endurantId ) {
        // Retrieves all versions of experiments containing a contact with the
        // endurant identifier "endurantId".
        return super.getAllWithContact(
                transform,
                "select exps from fugeOM.Collection.FuGE as exps " +
                        "join exps.auditCollection.allContacts as contacts " +
                        "where contacts.endurant.identifier = :endurantId", endurantId );
    }
}