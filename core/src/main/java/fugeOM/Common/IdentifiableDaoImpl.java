// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package fugeOM.Common;

import java.util.Date;
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
 * @see fugeOM.Common.Identifiable
 */
public class IdentifiableDaoImpl
        extends fugeOM.Common.IdentifiableDaoBase {
    /**
     * @see fugeOM.Common.IdentifiableDao#findLatestIdByIdentifier(java.lang.String)
     */
    protected String handleFindLatestIdByIdentifier( java.lang.String identifiableIdentifier ) {
        // SHOULD only return one, unless there are two latest objects for the endurant with the same timestamp.
        String queryString = "select i.identifier from fugeOM.Common.Identifiable as i " +
                "join i.auditTrail as audits " +
                "where i.endurant.identifier = (select iden.endurant.identifier " +
                "                                 from fugeOM.Common.Identifiable as iden " +
                "                                where iden.identifier = :identifier) " +
                "and " +
                "audits.date = (select max(internalaudits.date) " +
                "                 from fugeOM.Common.Identifiable as iden " +
                "                 join iden.auditTrail as internalaudits " +
                "                where iden.endurant.id = i.endurant.id)";
        try {
            // should only return one object
//            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString ).uniqueResult();
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "identifier", identifiableIdentifier );
            java.util.Set results = new java.util.LinkedHashSet( queryObject.list() );
            java.lang.String result = null;
            if ( results != null && results.size() > 0 ) {
                if ( results.size() != 1 ) {
                    System.err.println(
                            "More than one object retrieved for latest version of identifier " +
                                    identifiableIdentifier );
                    return null;
                }
                result = ( String ) results.iterator().next();
            }
            return result;
        }
        catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /**
     * @see fugeOM.Common.IdentifiableDao#findLatestIdByEndurant(java.lang.String)
     */
    protected java.lang.String handleFindLatestIdByEndurant( java.lang.String endurantIdentifier ) {
        // SHOULD only return one, unless there are two latest objects for the endurant with the same timestamp.
        String queryString = "select i.identifier from fugeOM.Common.Identifiable as i " +
                "join i.auditTrail as audits " +
                "where i.endurant.identifier = :identifier " +
                "and " +
                "audits.date = (select max(internalaudits.date) from fugeOM.Common.Identifiable as iden " +
                "               join iden.auditTrail as internalaudits " +
                "               where iden.endurant.id = i.endurant.id)";
        try {
            // should only return one object
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "identifier", endurantIdentifier );
            java.util.Set results = new java.util.LinkedHashSet( queryObject.list() );
            java.lang.String result = null;
            if ( results != null && results.size() > 0 ) {
                result = ( String ) results.iterator().next();
            }
            return result;
        }
        catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /**
     * @see fugeOM.Common.IdentifiableDao#FindByIdentifierAndDate(java.lang.String,java.util.Date)
     */
    protected java.lang.Object handleFindByIdentifierAndDate( java.lang.String identifier, java.util.Date date ) {
        // SHOULD only return one, unless there are two latest objects for the endurant with the same timestamp.
        String queryString = "select i from fugeOM.Common.Identifiable as i " +
                "join i.auditTrail as audit " +
                "where i.endurant.identifier =  (select iden.endurant.identifier " +
                "                                 from fugeOM.Common.Identifiable as iden " +
                "                                where iden.identifier = :identifier) " +
                "and " +
                "audit.date = (select max(internalaudits.date) " +
                "                from fugeOM.Common.Identifiable as iden " +
                "                join iden.auditTrail as internalaudits " +
                "               where iden.endurant.id = i.endurant.id " +
                "                 and internalaudits.date <= :date) ";
        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "identifier", identifier );
            queryObject.setParameter( "date", date );
            java.util.Set results = new java.util.LinkedHashSet( queryObject.list() );
            java.lang.String result = null;
            if ( results != null && results.size() > 0 ) {
                if ( results.size() != 1 ) {
                    System.err
                            .println( "More than one object retrieved for latest version of identifier " + identifier );
                    return null;
                }
                result = ( String ) results.iterator().next();
            }
            return result;
        }
        catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    public Object findByEndurantAndDate( final int transform, final String endurantId, final Date date ) {
        String queryString = "select i from fugeOM.Common.Identifiable as i " +
                "join i.auditTrail as audit " +
                "where i.endurant.identifier = :endurantId " +
                "and " +
                "audit.date <= :date " +
                "order by audit.date " +
                "desc";
        try {
            org.hibernate.Query queryObject = super.getSession( false ).createQuery( queryString );
            queryObject.setParameter( "endurantId", endurantId );
            queryObject.setParameter( "date", date );
            java.util.Set results = new java.util.LinkedHashSet( queryObject.list() );
            java.lang.Object result = null;
            // todo possible removal of null check, as it should never be null
            if ( results != null && results.size() > 0 ) {
                result = results.iterator().next();
            }
            result = transformEntity( transform, ( fugeOM.Common.Identifiable ) result );
            return result;
        }
        catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }
}