package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.common.Identifiable;

import java.util.Set;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * For static helper methods that can be used in a generic fashion by more than one Loader class.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/loading/LoadFuge.java $
 */
public class LoaderHelper {

    // toSearch must be identifiable
    public static boolean findMatchingEndurant( String endurant, Set toSearch ) {
        if ( toSearch == null ) {
            return false;
        }
        for ( Object obj : toSearch ) {
            Identifiable iden = ( Identifiable ) obj;
            if ( iden.getEndurant().getIdentifier().equals( endurant ) )
                return true;
        }
        return false;
    }

}
