package net.sourceforge.fuge.common;

import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.versioning.Endurant;

import java.util.*;

import org.testng.annotations.Test;

/**
 * In SyMBA, you don't add audit information except when loading metadata into the database, i.e. there will always
 * be a single audit entry that is set to "creation". The only time this might vary is when you are batch loading
 * files from other locations (in which case you should turn OFF SyMBA's default addition of a new audit object into
 * the audit trail) or when you are running a roundtrip test of the system, and have generated a random XML file
 * to perform the roundtrip. In any case, the queries tested below should work, irrespective of the number of Audit
 * items in the Audit Trail. However, if you have both pre-written Audit objects and the Audit object added to the
 * item when the database is modified, the query will use the latest audit date to determine the most recent object.
 * In essence, if you have predetermined audit dates that you wish to use over the data entry dates into the
 * database, you MUST ensure that you turn off the addition of the Audit item when loading into the database. This
 * can be done with by setting the following variable in the top-level pom.xml to false, as shown:
 * <net.sourceforge.fuge.addDbAuditTrail>false</net.sourceforge.fuge.addDbAuditTrail>
 * <p/>
 * This value is automatically set to false when running with the validation (-Denv=val) profile within Maven.
 * <p/>
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */

public class IdentifiableDaoTest {

    @Test( groups = { "hibernate" } )
    public void getForDateTestSimpleLatest() {
        // This ensures that the retrieval of an Identifiable object based on the dates in its audit trail are correct.
        // It is a fairly simple test that just loads in two Person objects with two different dates, and makes
        // sure that the newest one is returned. Each Person object only has one Audit item in the Audit Trail.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        List<Date> dates = new ArrayList<Date>();
        dates.add( gc.getTime() );
        Person oldPerson = writePerson( "getForDateTestSimpleLatestOlder", dates );

        gc.set( 2007, Calendar.MARCH, 4 );

        // make a new version of the person, using the same endurant.
        dates = new ArrayList<Date>();
        dates.add( gc.getTime() );
        Person newPerson = writePerson( "getForDateTestSimpleLatestNewer", dates, oldPerson.getEndurant() );

        SymbaEntityService symbaEntityService = ServiceLocator.instance().getSymbaEntityService();

        Person latestPersonByIdentifier = ( Person ) symbaEntityService.getLatestByIdentifier( oldPerson.getIdentifier() );

        // check that a result was returned
        assert ( latestPersonByIdentifier != null ) : printError( "Error retrieving latest person by identifier.", oldPerson, newPerson );

        // check getLatestByIdentifier method
        assert ( latestPersonByIdentifier.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByIdentifier. Instead, " + latestPersonByIdentifier.getIdentifier() +
                " was chosen!";

        Person latestPersonByEndurant = ( Person ) symbaEntityService.getLatestByEndurant( oldPerson.getEndurant().getIdentifier() );

        // check that a result was returned
        assert ( latestPersonByEndurant != null ) : printError( "Error retrieving latest person by endurant.", oldPerson, newPerson );

        // check getLatestByEndurant method
        assert ( latestPersonByEndurant.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByEndurant. Instead, " + latestPersonByEndurant.getIdentifier() +
                " was chosen!";

        Person latestPersonUsingIdentifiable = ( Person ) symbaEntityService.getLatest( oldPerson.getIdentifier() );

        // check that a result was returned
        assert ( latestPersonUsingIdentifiable != null ) : printError( "Error retrieving latest person using getLatest with an Identifiable.identifier.", oldPerson, newPerson );

        // check getLatest method using Identifiable.identifier
        assert ( latestPersonUsingIdentifiable.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingIdentifiable. Instead, " + latestPersonUsingIdentifiable.getIdentifier() +
                " was chosen!";

        Person latestPersonUsingEndurant = ( Person ) symbaEntityService.getLatest( oldPerson.getEndurant().getIdentifier() );

        // check that a result was returned
        assert ( latestPersonUsingEndurant != null ) : printError( "Error retrieving latest person using getLatest with an Endurant.identifier.", oldPerson, newPerson );

        // check getLatest method using Identifiable.identifier
        assert ( latestPersonUsingEndurant.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingEndurant. Instead, " + latestPersonUsingEndurant.getIdentifier() +
                " was chosen!";

    }

    @Test( groups = { "hibernate" } )
    public void getForDateTestSimpleMidRange() {
        // This ensures that the retrieval of an Identifiable object based on the dates in its audit trail are correct.
        // It is a fairly simple test that just loads in three Person objects with three different dates, and makes
        // sure that the middle one is returned by asking for a specific date. Each Person object only has one Audit
        // item in the Audit Trail.

        List<Date> dates = new ArrayList<Date>();

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );
        dates.add( gc.getTime() );

        Person personOne = writePerson( "getForDateTestSimpleMidRangeOne", dates );

        gc.set( 2003, Calendar.MARCH, 4 );

        // make a new version of the person, using the same endurant.
        dates = new ArrayList<Date>();
        dates.add( gc.getTime() );
        Person personTwo = writePerson( "getForDateTestSimpleMidRangeTwo", dates, personOne.getEndurant() );

        gc.set( 2007, Calendar.MARCH, 4 );

        // make a third version of the person, using the same endurant.
        dates = new ArrayList<Date>();
        dates.add( gc.getTime() );
        writePerson( "getForDateTestSimpleMidRangeThree", dates, personOne.getEndurant() );

        SymbaEntityService symbaEntityService = ServiceLocator.instance().getSymbaEntityService();

        // set search date to 2007, but in January
        gc.set( 2007, Calendar.JANUARY, 4 );

        Person midRangePersonByIdentifier = ( Person ) symbaEntityService.getLatestByIdentifier( personOne.getIdentifier(), gc.getTime() );

        // check that a result was returned
        assert ( midRangePersonByIdentifier != null ) : printError( "Error retrieving MidRange person by identifier.", personOne, personTwo );

        // check getLatestByIdentifier method
        assert ( midRangePersonByIdentifier.equals( personTwo ) ) : "MidRange Person (" + personTwo.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByIdentifier. Instead, " + midRangePersonByIdentifier.getIdentifier() +
                " was chosen!";

        Person midRangePersonByEndurant = ( Person ) symbaEntityService.getLatestByEndurant( personOne.getEndurant().getIdentifier(), gc.getTime() );

        // check that a result was returned
        assert ( midRangePersonByEndurant != null ) : printError( "Error retrieving MidRange person by endurant.", personOne, personTwo );

        // check getLatestByEndurant method
        assert ( midRangePersonByEndurant.equals( personTwo ) ) : "MidRange Person (" + personTwo.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByEndurant. Instead, " + midRangePersonByEndurant.getIdentifier() +
                " was chosen!";

        Person midRangePersonUsingIdentifiable = ( Person ) symbaEntityService.getLatest( personOne.getIdentifier(), gc.getTime() );

        // check that a result was returned
        assert ( midRangePersonUsingIdentifiable != null ) : printError( "Error retrieving MidRange person using getLatest with an Identifiable.identifier.", personOne, personTwo );

        // check getLatest method using Identifiable.identifier
        assert ( midRangePersonUsingIdentifiable.equals( personTwo ) ) : "MidRange Person (" + personTwo.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingIdentifiable. Instead, " + midRangePersonUsingIdentifiable.getIdentifier() +
                " was chosen!";

        Person midRangePersonUsingEndurant = ( Person ) symbaEntityService.getLatest( personOne.getEndurant().getIdentifier(), gc.getTime() );

        // check that a result was returned
        assert ( midRangePersonUsingEndurant != null ) : printError( "Error retrieving MidRange person using getLatest with an Endurant.identifier.", personOne, personTwo );

        // check getLatest method using Identifiable.identifier
        assert ( midRangePersonUsingEndurant.equals( personTwo ) ) : "MidRange Person (" + personTwo.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingEndurant. Instead, " + midRangePersonUsingEndurant.getIdentifier() +
                " was chosen!";

    }

    @Test( groups = { "hibernate" } )
    public void getForDateTestComplexLatest() {
        // This ensures that the retrieval of an Identifiable object based on the dates in its audit trail are correct.
        // It is a more complex test that loads in two Person objects with two different dates, and makes
        // sure that the newest one is returned. Each Person object has 3 Audit items in the Audit Trail.

        GregorianCalendar gc = new GregorianCalendar();

        gc.set( 2000, Calendar.MARCH, 4 );
        List<Date> dates = new ArrayList<Date>();
        dates.add( gc.getTime() );
        gc.set( 2003, Calendar.MARCH, 4 );
        dates.add( gc.getTime() );
        gc.set( 2006, Calendar.MARCH, 4 );
        dates.add( gc.getTime() );
        Person oldPerson = writePerson( "getForDateTestComplexLatestOlder", dates );


        // make a new version of the person, using the same endurant.
        dates = new ArrayList<Date>();
        gc.set( 2000, Calendar.FEBRUARY, 4 );
        dates.add( gc.getTime() );
        gc.set( 2003, Calendar.FEBRUARY, 4 );
        dates.add( gc.getTime() );
        gc.set( 2006, Calendar.APRIL, 4 );
        dates.add( gc.getTime() );
        Person newPerson = writePerson( "getForDateTestComplexLatestNewer", dates, oldPerson.getEndurant() );

        SymbaEntityService symbaEntityService = ServiceLocator.instance().getSymbaEntityService();

        Person latestPersonByIdentifier = ( Person ) symbaEntityService.getLatestByIdentifier( oldPerson.getIdentifier() );

        // check that a result was returned
        assert ( latestPersonByIdentifier != null ) : printError( "Error retrieving latest person by identifier.", oldPerson, newPerson );

        // check getLatestByIdentifier method
        assert ( latestPersonByIdentifier.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByIdentifier. Instead, " + latestPersonByIdentifier.getIdentifier() +
                " was chosen!";

        Person latestPersonByEndurant = ( Person ) symbaEntityService.getLatestByEndurant( oldPerson.getEndurant().getIdentifier() );

        // check that a result was returned
        assert ( latestPersonByEndurant != null ) : printError( "Error retrieving latest person by endurant.", oldPerson, newPerson );

        // check getLatestByEndurant method
        assert ( latestPersonByEndurant.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestByEndurant. Instead, " + latestPersonByEndurant.getIdentifier() +
                " was chosen!";

        Person latestPersonUsingIdentifiable = ( Person ) symbaEntityService.getLatest( oldPerson.getIdentifier() );

        // check that a result was returned
        assert ( latestPersonUsingIdentifiable != null ) : printError( "Error retrieving latest person using getLatest with an Identifiable.identifier.", oldPerson, newPerson );

        // check getLatest method using Identifiable.identifier
        assert ( latestPersonUsingIdentifiable.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingIdentifiable. Instead, " + latestPersonUsingIdentifiable.getIdentifier() +
                " was chosen!";

        Person latestPersonUsingEndurant = ( Person ) symbaEntityService.getLatest( oldPerson.getEndurant().getIdentifier() );

        // check that a result was returned
        assert ( latestPersonUsingEndurant != null ) : printError( "Error retrieving latest person using getLatest with an Endurant.identifier.", oldPerson, newPerson );

        // check getLatest method using Identifiable.identifier
        assert ( latestPersonUsingEndurant.equals( newPerson ) ) : "Newer Person (" + newPerson.getIdentifier() + ") not chosen " +
                "as latest person when running getLatestUsingEndurant. Instead, " + latestPersonUsingEndurant.getIdentifier() +
                " was chosen!";

    }

    private StringBuffer printError( String message, Person person1, Person person2 ) {

        StringBuffer result = new StringBuffer();

        result.append( message ).append( System.getProperty( "line.separator" ) );
        for ( Audit audit : person1.getAuditTrail() ) {
            result.append( person1.getName() ).append( ": audit.getDate() = " ).append( audit.getDate() )
                    .append( System.getProperty( "line.separator" ) );
        }
        for ( Audit audit : person2.getAuditTrail() ) {
            result.append( person2.getName() ).append( ": audit.getDate() = " ).append( audit.getDate() )
                    .append( System.getProperty( "line.separator" ) );
        }
        return result;
    }

    private Person writePerson( String name, List<Date> times ) {

        return writePerson( name, times, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private Person writePerson( String name, List<Date> times, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        Person person = ( Person ) es.createIdentifiable( name + ":Person:" + String.valueOf( Math.random() * 10000 ), name, "net.sourceforge.fuge.common.audit.Person" );

        // add an Endurant value (required) after saving the endurant to the database
        person.setEndurant( endurant );

        Set<Audit> audits = new HashSet<Audit>();

        for ( Date time : times ) {
            Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
            audit.setDate( new java.sql.Timestamp( time.getTime() ) );
            audit.setAction( AuditAction.creation );
            audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
            audits.add( audit );
        }

        person.setAuditTrail( audits );
        person = ( Person ) es.create( "net.sourceforge.fuge.common.audit.Person", person );

        return person;
    }
}
