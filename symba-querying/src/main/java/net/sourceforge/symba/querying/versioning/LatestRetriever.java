package net.sourceforge.symba.querying.versioning;

import net.sourceforge.fuge.common.protocol.GenericProtocol;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * These methods do more than just retrieve the latest version of the top-level object. In fact, these methods assume
 * that you already have passed the most up-to-date version of the object. What they do is check for
 * new sub-objects that may have been added since the top-level object was created. This is because updates
 *
 * only things which could be templated (and not Dummies) need this sort of checking: OntologyCollection,
 * Protocols. 
 *
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-core/src/main/java/fugeOM/Common/IdentifiableDaoImpl.java $
 */
public class LatestRetriever {

    public static GenericProtocol latest(GenericProtocol genericProtocol) {

        return genericProtocol;
    }
}
