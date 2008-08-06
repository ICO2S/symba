package net.sourceforge.fuge.common.ontology;

import org.testng.annotations.Test;

import java.util.*;

import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.versioning.Endurant;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;

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
public class OntologyTermDaoTest {

    @Test( groups = { "ontology", "hibernate" } )
    public void getLatestUnsourcedAndSourcedTermsSimpleTest() {

        // load unsourced and sourced terms, and see if they are counted properly.
        // First, count the number of latest unsourced terms already in the database. Then load in 4 more
        // of unsourced and 3 of sourced, each with their own endurant. Then, check to see that the number has
        // incremented by 4 for unsourced.
        // Finally, as all the new sourced terms linked to a single source, check that the single source
        // contains exactly 3 OntologyIndividuals.

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        int startNumber = ses.getLatestUnsourcedTerms().size();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // create and load 3 OntologyIndividuals without OntologySources
        OntologyTerm one = writeOntologyIndividual( "getLatestUnsourcedAndSourcedTermsSimpleTest1", gc.getTime() );
        OntologyTerm two = writeOntologyIndividual( "getLatestUnsourcedAndSourcedTermsSimpleTest2", gc.getTime() );
        OntologyTerm three = writeOntologyIndividual( "getLatestUnsourcedAndSourcedTermsSimpleTest3", gc.getTime() );
        OntologyTerm four = writeOntologyIndividual( "getLatestUnsourcedAndSourcedTermsSimpleTest4", gc.getTime() );

        // create and load 3 OntologyIndividuals a single OntologySource
        OntologySource osOne = writeOntologySource( "getLatestTermsWithSourceSimpleTest5", gc.getTime() );
        OntologyTerm six = writeOntologyIndividual( "getLatestTermsWithSourceSimpleTest6", gc.getTime(), osOne );
        OntologyTerm seven = writeOntologyIndividual( "getLatestTermsWithSourceSimpleTest7", gc.getTime(), osOne );
        OntologyTerm eight = writeOntologyIndividual( "getLatestTermsWithSourceSimpleTest8", gc.getTime(), osOne );

        @SuppressWarnings( "unchecked" )
        List<OntologyTerm> ontologyTerms = ses.getLatestUnsourcedTerms();

        int endNumber = ontologyTerms.size();

        assert ( endNumber - startNumber == 4 ) :
                "3 OntologyIndividuals without Sources and each with different Endurants, have been loaded. However, " +
                "the starting number of OntologyIndividuals was " + startNumber + " and the " +
                "ending number of OntologyIndividuals was " + endNumber + ". The difference in these values should " +
                "have been 4";

        // all of the unsourced terms must be present in the list, and none of the sourced terms.
        assert ( ontologyTerms.contains( one ) ) :
                "OntologyTerm one is unsourced, but isn't in the list and should be.";
        assert ( ontologyTerms.contains( two ) ) :
                "OntologyTerm two is unsourced, but isn't in the list and should be.";
        assert ( ontologyTerms.contains( three ) ) :
                "OntologyTerm three is unsourced, but isn't in the list and should be.";
        assert ( ontologyTerms.contains( four ) ) :
                "OntologyTerm four is unsourced, but isn't in the list and should be.";
        assert ( !ontologyTerms.contains( six ) ) :
                "OntologyTerm six is sourced, and therefore must NOT be in the list, but it is.";
        assert ( !ontologyTerms.contains( seven ) ) :
                "OntologyTerm seven is sourced, and therefore must NOT be in the list, but it is.";
        assert ( !ontologyTerms.contains( eight ) ) :
                "OntologyTerm eight is sourced, and therefore must NOT be in the list, but it is.";


        @SuppressWarnings( "unchecked" )
        List<OntologyTerm> sourcedTerms = ses.getLatestTermsWithSource( osOne.getEndurant().getIdentifier() );

        int numberWithSource = sourcedTerms.size();

        assert ( numberWithSource == 3 ) :
                "3 OntologyIndividuals with one Source and each with different Endurants, have been loaded. However, " +
                numberWithSource + " have been counted. This value should have been 3";

        // all of the unsourced terms not must be present in the list, and all of the sourced terms should be.
        assert ( !sourcedTerms.contains( one ) ) :
                "OntologyTerm one is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( two ) ) :
                "OntologyTerm two is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( three ) ) :
                "OntologyTerm three is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( four ) ) :
                "OntologyTerm four is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( sourcedTerms.contains( six ) ) :
                "OntologyTerm six is sourced, and therefore must be in the list, but isn't.";
        assert ( sourcedTerms.contains( seven ) ) :
                "OntologyTerm seven is sourced, and therefore must be in the list, but isn't.";
        assert ( sourcedTerms.contains( eight ) ) :
                "OntologyTerm eight is sourced, and therefore must be in the list, but isn't.";
    }

    @Test( groups = { "ontology", "hibernate" } )
    public void getLatestUnsourcedAndSourcedTermsComplexTest() {

        // load unsourced and sourced terms, and see if they are counted properly.
        // First, count the number of latest unsourced terms already in the database. Then load in 4 more
        // of unsourced and 3 of sourced, 2 of each type sharing an endurant. Then, check to see that the number has
        // incremented by 3 for unsourced.
        // Finally, as all the new sourced terms linked to a single source, check that the single source
        // contains exactly 2 OntologyIndividuals (there are 3, but one of the 3 is an older version).

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        int startNumber = ses.getLatestUnsourcedTerms().size();

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // create and load 3 OntologyIndividuals without OntologySources, and two with a shared Endurant
        OntologyTerm one = writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest1", gc.getTime() );
        OntologyTerm two = writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest2", gc.getTime() );
        OntologyIndividual three =
                writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest3", gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        OntologyTerm four =
                writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest4", gc.getTime(), three.getEndurant() );

        // create and load 4 OntologyIndividuals a single OntologySource, and two with a shared Endurant
        OntologySource osOne = writeOntologySource( "getLatestUnsourcedTermsComplexTest5", gc.getTime() );
        OntologyTerm six = writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest6", gc.getTime(), osOne );
        OntologyIndividual seven =
                writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest7", gc.getTime(), osOne );
        gc.set( 2004, Calendar.MARCH, 4 );
        OntologyTerm eight = writeOntologyIndividual( "getLatestUnsourcedTermsComplexTest8", gc.getTime(), osOne,
                seven.getEndurant() );

        @SuppressWarnings( "unchecked" )
        List<OntologyTerm> ontologyTerms = ses.getLatestUnsourcedTerms();

        int endNumber = ontologyTerms.size();

        assert ( endNumber - startNumber == 3 ) :
                "4 OntologyIndividuals without Sources and 2 sharing an Endurant, have been loaded. However, " +
                "the starting number of OntologyIndividuals was " + startNumber + " and the " +
                "ending number of OntologyIndividuals was " + endNumber + ". The difference in these values should " +
                "have been 3";

        // all of the unsourced terms must be present in the list except the older version, and none of the sourced terms.
        assert ( ontologyTerms.contains( one ) ) :
                "OntologyTerm one is unsourced, but isn't in the list and should be.";
        assert ( ontologyTerms.contains( two ) ) :
                "OntologyTerm two is unsourced, but isn't in the list and should be.";
        assert ( !ontologyTerms.contains( three ) ) :
                "OntologyTerm three is unsourced and an older version, so shouldn't be in the list, but is.";
        assert ( ontologyTerms.contains( four ) ) :
                "OntologyTerm four is unsourced, but isn't in the list and should be.";
        assert ( !ontologyTerms.contains( six ) ) :
                "OntologyTerm six is sourced, and therefore must NOT be in the list, but it is.";
        assert ( !ontologyTerms.contains( seven ) ) :
                "OntologyTerm seven is sourced, and therefore must NOT be in the list, but it is.";
        assert ( !ontologyTerms.contains( eight ) ) :
                "OntologyTerm eight is sourced, and therefore must NOT be in the list, but it is.";


        @SuppressWarnings( "unchecked" )
        List<OntologyTerm> sourcedTerms = ses.getLatestTermsWithSource( osOne.getEndurant().getIdentifier() );

        int numberWithSource = sourcedTerms.size();

        assert ( numberWithSource == 2 ) :
                "3 OntologyIndividuals with one Source and 2 sharing an Endurant, have been loaded. However, " +
                +numberWithSource + " have been counted. This value should have been 2";

        // all of the unsourced terms not must be present in the list, and all of the sourced terms should be, except
        // for the one with the older version.
        assert ( !sourcedTerms.contains( one ) ) :
                "OntologyTerm one is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( two ) ) :
                "OntologyTerm two is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( three ) ) :
                "OntologyTerm three is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( !sourcedTerms.contains( four ) ) :
                "OntologyTerm four is unsourced, and therefore shouldn't be in the list, but is.";
        assert ( sourcedTerms.contains( six ) ) :
                "OntologyTerm six is sourced, and therefore must be in the list, but isn't.";
        assert ( !sourcedTerms.contains( seven ) ) :
                "OntologyTerm seven is sourced but an older version, and therefore must NOT be in the list, but is.";
        assert ( sourcedTerms.contains( eight ) ) :
                "OntologyTerm eight is sourced, and therefore must be in the list, but isn't.";

    }

    private OntologyIndividual writeOntologyIndividual( String name, Date time ) {

        // cast the null value to ensure it goes to the correct method.
        return writeOntologyIndividual( name, time, ( OntologySource ) null );

    }

    private OntologyIndividual writeOntologyIndividual( String name, Date time, Endurant endurant ) {

        return writeOntologyIndividual( name, time, null, endurant );
    }

    private OntologyIndividual writeOntologyIndividual( String name, Date time, OntologySource referencedSource ) {

        return writeOntologyIndividual( name, time, referencedSource,
                DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private OntologyIndividual writeOntologyIndividual( String name,
                                                        Date time,
                                                        OntologySource referencedSource,
                                                        Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        OntologyIndividual individual = ( OntologyIndividual ) es
                .createIdentifiable( name + ":OntologyIndividual:" + String.valueOf( Math.random() * 10000 ), name,
                        "net.sourceforge.fuge.common.ontology.OntologyIndividual" );

        // add an Endurant value (required) after saving the endurant to the database
        individual.setEndurant( endurant );

        // OntologyIndividuals must have a term
        individual.setTerm( "some term " + String.valueOf( Math.random() * 100 ) );
        individual.setTermAccession( "some term AC" + String.valueOf( Math.random() * 100 ) );

        if ( referencedSource != null ) {
            // Now link to the OntologySource, if requested
            individual.setOntologySource( referencedSource );
        }

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

    private OntologySource writeOntologySource( String name, Date time ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        OntologySource ontologySource = ( OntologySource ) es
                .createIdentifiable( name + ":OntologySource:" + String.valueOf( Math.random() * 10000 ), name,
                        "net.sourceforge.fuge.common.ontology.OntologySource" );

        // add an Endurant value (required) after saving the endurant to the database
        ontologySource.setEndurant( DatabaseObjectHelper.getOrLoadEndurant( null, null ) );

        // OntologySource objects must have an ontologyURI.
        ontologySource.setOntologyURI( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        ontologySource.setAuditTrail( audits );
        ontologySource =
                ( OntologySource ) es.create( "net.sourceforge.fuge.common.ontology.OntologySource", ontologySource );

        return ontologySource;

    }
}
