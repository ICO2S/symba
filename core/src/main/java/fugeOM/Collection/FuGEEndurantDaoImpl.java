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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/core/src/main/java/fugeOM/Collection/FuGEEndurantDaoImpl.java $
 *
 */

/**
 * @see fugeOM.Collection.FuGEEndurant
 */
public class FuGEEndurantDaoImpl
        extends fugeOM.Collection.FuGEEndurantDaoBase {
    /**
     * @see fugeOM.Collection.FuGEEndurantDao#countAll()
     */
    protected int handleCountAll() {
        String queryString = "select count(identifier) from fugeOM.Collection.FuGEEndurant";
        try {
            return ( ( Integer )
                    super.getSession( false ).createQuery( queryString ).uniqueResult() ).intValue();
        }
        catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

}