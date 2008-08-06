package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityService;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/LoadPerson.java $
 *
 */

public class LoadPerson {

    private EntityService entityService;

    /**
     * Creates a new instance of LoadPerson
     * @param entityService the link to the database
     */
    public LoadPerson( EntityService entityService ) {
        this.entityService = entityService;
    }

    public PersonBean loadInDB( PersonBean personBean ) {

        // assume we have the latest version of the person already.
        Person person = ( Person ) entityService.getIdentifiable( personBean.getLsid() );
        
        person.setFirstName( personBean.getFirstName() );
        person.setLastName( personBean.getLastName() );
        person.setEmail( personBean.getEmail() );

        // If we're finished messing around with person, then we can now create it in the database
        // todo cannot add an auditor object as we are in the process of changing the person!
        DatabaseObjectHelper.save("net.sourceforge.fuge.common.audit.Person", person, null);

        personBean.setEndurantLsid( person.getEndurant().getIdentifier() );
        personBean.setLsid( person.getIdentifier() );

        return personBean;
    }
}