package net.sourceforge.fuge.collection;

import org.testng.annotations.Test;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.versioning.Endurant;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.querying.ExperimentSummary;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.audit.Contact;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.service.EntityService;

import java.util.*;

/**
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
public class FuGEDaoTest {

    @Test( groups = { "counters", "fuge", "hibernate" } )
    public void countExperimentsSimpleTest() {

        // we don't know how many experiments are already in the database, so instead we will compare the
        // number of experiments before and after this test has been run.

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        long startNumber = ses.countExperiments();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 experiments, each of which has its own Endurant.
        writeFuGE( "countExperimentsSimpleTest1", gc.getTime() );
        writeFuGE( "countExperimentsSimpleTest2", gc.getTime() );
        writeFuGE( "countExperimentsSimpleTest3", gc.getTime() );

        long endNumber = ses.countExperiments();

        assert ( endNumber - startNumber == 3 ) :
                "3 Experiments, each with their own Endurant, have been loaded. However, " +
                "the starting number of experiments was " + startNumber + " and the " +
                "ending number of experiments was " + endNumber + ". The difference in these values should have been 3";

    }

    @Test( groups = { "counters", "fuge", "hibernate" } )
    public void countExperimentsComplexTest() {

        // we don't know how many experiments are already in the database, so instead we will compare the
        // number of experiments before and after this test has been run.

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        long startNumber = ses.countExperiments();

        // We're not testing the Audit Trail here, so just use the same date except for the new version of the existing
        // object.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 experiments, one of which is a new version of another. This will lead to a total of 2 new
        // experiments based on the criteria within countExperiments().
        writeFuGE( "countExperimentsComplexTest1", gc.getTime() );
        FuGE two = writeFuGE( "countExperimentsComplexTest2", gc.getTime() );
        gc.set( 2001, Calendar.MARCH, 4 );
        writeFuGE( "countExperimentsComplexTest2a", gc.getTime(), two.getEndurant() );

        long endNumber = ses.countExperiments();

        assert ( endNumber - startNumber == 2 ) :
                "3 Experiments, two with their own Endurant and one re-using an Endurant, have been loaded. However, " +
                "the starting number of experiments was " + startNumber + " and the " +
                "ending number of experiments was " + endNumber + ". The difference in these values should have been 2";
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesSimpleTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        FuGE one = writeFuGE( "getSummariesSimpleTest1", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesSimpleTest2", gc.getTime() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummaries();

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( experimentSummaries.contains( acutalOne ) ) :
                "Summaries do not contain experiment " + one.getIdentifier();
        assert ( experimentSummaries.contains( acutalTwo ) ) :
                "Summaries do not contain experiment " + two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        FuGE one = writeFuGE( "getSummariesComplexTest1", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        FuGE two = writeFuGE( "getSummariesComplexTest2", gc.getTime(), one.getEndurant() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );

        // retrieve all summaries = should only have the "one" in the returned list.
        List fullResults = ses.getSummaries();

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( !experimentSummaries.contains( acutalOne ) ) :
                "Summaries contain experiment that is an older version: " + one.getIdentifier();
        assert ( experimentSummaries.contains( acutalTwo ) ) :
                "Summaries do not contain experiment " + two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithPartialNameSimpleTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        FuGE one = writeFuGE( "getSummariesWithPartialNameSimpleTest-match-string", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithPartialNameSimpleTest-some-other-string", gc.getTime() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithPartialName( "match" );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( experimentSummaries.contains( acutalOne ) ) :
                "Summaries do not contain experiment " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does not match the query string " + two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithPartialNameComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        FuGE one = writeFuGE( "getSummariesWithPartialNameComplexTest-some-other-string", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithPartialNameComplexTest-match-string", gc.getTime() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );
        gc.set( 2008, Calendar.MARCH, 4 );
        FuGE three =
                writeFuGE( "getSummariesWithPartialNameComplexTest-match-string", gc.getTime(), two.getEndurant() );
        ExperimentSummary acutalThree = new ExperimentSummary( three.getIdentifier(), three.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithPartialName( "match" );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( !experimentSummaries.contains( acutalOne ) ) :
                "Summaries contain experiment that does not match the query string: " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does match the query string but is an older version: " +
                two.getIdentifier();
        assert ( experimentSummaries.contains( acutalThree ) ) :
                "Summaries do not contain the experiment that matches the query term: " + two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithContactSimpleTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        Person person = writePerson( "getSummariesWithContactSimpleTest-Person", gc.getTime() );

        FuGE one = writeFuGE( "getSummariesWithContactSimpleTest1", gc.getTime(), person );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithContactSimpleTest2", gc.getTime() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithContact( person.getEndurant().getIdentifier() );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( experimentSummaries.contains( acutalOne ) ) :
                "Summaries do not contain experiment " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does not match the person provided " + two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithContactComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        Person person = writePerson( "getSummariesWithContactComplexTest-Person", gc.getTime() );

        FuGE one = writeFuGE( "getSummariesWithContactComplexTest1", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithContactComplexTest2", gc.getTime(), person );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );
        gc.set( 2008, Calendar.MARCH, 4 );
        FuGE three =
                writeFuGE( "getSummariesWithContactComplexTest3", gc.getTime(), two.getEndurant(), person );
        ExperimentSummary acutalThree = new ExperimentSummary( three.getIdentifier(), three.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithContact( person.getEndurant().getIdentifier() );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( !experimentSummaries.contains( acutalOne ) ) :
                "Summaries contain experiment that does not match the query person: " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does match the query person but is an older version: " +
                two.getIdentifier();
        assert ( experimentSummaries.contains( acutalThree ) ) :
                "Summaries do not contain the experiment that matches the query person: " + three.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithOntologyTermSimpleTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        OntologyIndividual ontologyIndividual =
                writeOntologyIndividual( "getSummariesWithOntologyTermSimpleTest-OntologyTerm", gc.getTime() );

        FuGE one = writeFuGE( "getSummariesWithOntologyTermSimpleTest1", gc.getTime(), ontologyIndividual );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithOntologyTermSimpleTest2", gc.getTime() );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithOntologyTerm( ontologyIndividual.getEndurant().getIdentifier() );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( experimentSummaries.contains( acutalOne ) ) :
                "Summaries do not contain experiment " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does not match the ontologyIndividual provided " +
                two.getIdentifier();
    }

    @Test( groups = { "fuge", "hibernate" } )
    public void getSummariesWithOntologyTermComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        OntologyIndividual ontologyIndividual =
                writeOntologyIndividual( "getSummariesWithOntologyTermComplexTest-Person", gc.getTime() );

        FuGE one = writeFuGE( "getSummariesWithOntologyTermComplexTest1", gc.getTime() );
        ExperimentSummary acutalOne = new ExperimentSummary( one.getIdentifier(), one.getName(), gc.getTime() );
        FuGE two = writeFuGE( "getSummariesWithOntologyTermComplexTest2", gc.getTime(), ontologyIndividual );
        ExperimentSummary acutalTwo = new ExperimentSummary( two.getIdentifier(), two.getName(), gc.getTime() );
        gc.set( 2008, Calendar.MARCH, 4 );
        FuGE three =
                writeFuGE( "getSummariesWithOntologyTermComplexTest3", gc.getTime(), two.getEndurant(),
                        ontologyIndividual );
        ExperimentSummary acutalThree = new ExperimentSummary( three.getIdentifier(), three.getName(), gc.getTime() );

        // retrieve all summaries.
        List fullResults = ses.getSummariesWithOntologyTerm( ontologyIndividual.getEndurant().getIdentifier() );

        List<ExperimentSummary> experimentSummaries = new ArrayList<ExperimentSummary>();

        for ( Object currentResult : fullResults ) {
            ExperimentSummary es = new ExperimentSummary( ( List ) currentResult );
            experimentSummaries.add( es );
        }

        assert ( !experimentSummaries.contains( acutalOne ) ) :
                "Summaries contain experiment that does not match the query ontologyIndividual: " + one.getIdentifier();
        assert ( !experimentSummaries.contains( acutalTwo ) ) :
                "Summaries contain experiment that does match the query ontologyIndividual but is an older version: " +
                two.getIdentifier();
        assert ( experimentSummaries.contains( acutalThree ) ) :
                "Summaries do not contain the experiment that matches the query ontologyIndividual: " +
                three.getIdentifier();
    }

    private FuGE writeFuGE( String name, Date time ) {

        return writeFuGE( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private FuGE writeFuGE( String name, Date time, Person person ) {

        return writeFuGE( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ), person, null );
    }

    private FuGE writeFuGE( String name, Date time, OntologyIndividual ontologyIndividual ) {

        return writeFuGE( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ), null, ontologyIndividual );
    }

    private FuGE writeFuGE( String name, Date time, Endurant endurant ) {

        return writeFuGE( name, time, endurant, null, null );
    }

    private FuGE writeFuGE( String name, Date time, Endurant endurant, Person person ) {

        return writeFuGE( name, time, endurant, person, null );
    }

    private FuGE writeFuGE( String name, Date time, Endurant endurant, OntologyIndividual ontologyIndividual ) {

        return writeFuGE( name, time, endurant, null, ontologyIndividual );
    }

    private FuGE writeFuGE( String name,
                            Date time,
                            Endurant endurant,
                            Person person,
                            OntologyIndividual ontologyIndividual ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        FuGE fuGE = ( FuGE ) es.createIdentifiable( name + ":FuGE:" + String.valueOf( Math.random() * 10000 ), name,
                "net.sourceforge.fuge.collection.FuGE" );

        // add an Endurant value (required) after saving the endurant to the database
        fuGE.setEndurant( endurant );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        if ( person != null ) {
            audit.setPerformer( person );
            AuditCollection auditCollection =
                    ( AuditCollection ) es.createDescribable( "net.sourceforge.fuge.collection.AuditCollection" );
            Set<Contact> contacts = new HashSet<Contact>();
            contacts.add( person );
            auditCollection.setAllContacts( contacts );
            es.create( "net.sourceforge.fuge.collection.AuditCollection", auditCollection );
            fuGE.setAuditCollection( auditCollection );
        }
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        // add to ontologycollection and to FuGE if ontologyIndividual is not null
        if ( ontologyIndividual != null ) {
            OntologyCollection ontologyCollection =
                    ( OntologyCollection ) es.createDescribable( "net.sourceforge.fuge.collection.OntologyCollection" );
            Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
            ontologyTerms.add( ontologyIndividual );
            fuGE.setAnnotations( ontologyTerms );
            ontologyCollection.setOntologyTerms( ontologyTerms );
            es.create( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection );
            fuGE.setOntologyCollection( ontologyCollection );
        }

        fuGE.setAuditTrail( audits );
        fuGE = ( FuGE ) es.create( "net.sourceforge.fuge.collection.FuGE", fuGE );

        return fuGE;

    }

    private Person writePerson( String name, Date time ) {

        return writePerson( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private Person writePerson( String name, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        Person person = ( Person ) es.createIdentifiable( name + ":Person:" + String.valueOf( Math.random() * 10000 ),
                name, "net.sourceforge.fuge.common.audit.Person" );

        // add an Endurant value (required) after saving the endurant to the database
        person.setEndurant( endurant );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        person.setAuditTrail( audits );
        person = ( Person ) es.create( "net.sourceforge.fuge.common.audit.Person", person );

        return person;
    }

    private OntologyIndividual writeOntologyIndividual( String name, Date time ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        OntologyIndividual individual = ( OntologyIndividual ) es
                .createIdentifiable( name + ":OntologyIndividual:" + String.valueOf( Math.random() * 10000 ), name,
                        "net.sourceforge.fuge.common.ontology.OntologyIndividual" );

        // add an Endurant value (required) after saving the endurant to the database
        individual.setEndurant( DatabaseObjectHelper.getOrLoadEndurant( null, null ) );

        // OntologyIndividuals must have a term
        individual.setTerm( "some term " + String.valueOf( Math.random() * 100 ) );
        individual.setTermAccession( "some term AC" + String.valueOf( Math.random() * 100 ) );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        individual.setAuditTrail( audits );
        individual =
                ( OntologyIndividual ) es
                        .create( "net.sourceforge.fuge.common.ontology.OntologyIndividual", individual );

        return individual;

    }

}
