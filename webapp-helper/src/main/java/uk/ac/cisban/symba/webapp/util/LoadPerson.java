package uk.ac.cisban.symba.webapp.util;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Audit.Person;
import fugeOM.service.RealizableEntityService;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.io.FileNotFoundException;

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
 * $HeadURL$
 *
 */

public class LoadPerson {
    RealizableEntityService reService;

    private final CisbanHelper helper;
    private Person person;


    /**
     * Creates a new instance of LoadPerson
     * @param reService the link to the database
     */
    public LoadPerson( fugeOM.service.RealizableEntityService reService ) {
        this.reService = reService;
        this.helper = CisbanHelper.create( reService );
    }

    public PersonBean loadInDB( PersonBean pb ) throws fugeOM.service.RealizableEntityServiceException, FileNotFoundException, LSIDException {

        String personIdentifier = helper.getLSID( "fugeOM.Common.Audit.Person" );

        // Retrieve latest person from the database.
        Person person = ( Person ) helper.getOrCreateLatest(
                pb.getEndurantLsid(), "fugeOM.Common.Audit.PersonEndurant", null, "fugeOM.Common.Audit.Person", null );
        //person.setName( String.valueOf( Math.random() ) );
        person.setFirstName( pb.getFirstName() );
        person.setLastName( pb.getLastName() );
        person.setEmail( pb.getEmail() );

        // If we're finished messing around with person, then we can now create it in the database
        if ( person.getId() != null ) {
            // Assume this object has changed, assign a new LSID and auditTrail, and load into the database
            helper.assignAndLoadIdentifiable( person, "fugeOM.Common.Audit.Person", null );
            System.out.print(
                    "!!!!!!!!!!!!!Person " + personIdentifier + " has been added as a new revision in the database." );
        } else {
            helper.loadIdentifiable( person, "fugeOM.Common.Audit.Person", null );
        }

        System.out.println( "!!!!!!!!!!!!NEW ID  " + person.getIdentifier() );

        pb.setEndurantLsid( person.getEndurant().getIdentifier() );
        pb.setLsid( person.getIdentifier() );

        return pb;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson( Person person ) {
        this.person = person;
    }
}