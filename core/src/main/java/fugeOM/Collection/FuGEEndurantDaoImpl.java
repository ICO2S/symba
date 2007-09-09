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