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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-core/src/main/java/fugeOM/Common/IdentifiableDaoImpl.java $
 */
public class OntologyIndividualDaoTest {

    @Test( groups = { "ontology", "hibernate" } )
    public void getByAccessionSimpleTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        OntologyIndividual one = writeOntologyIndividual( "getByAccessionSimpleTest1", gc.getTime() );

        OntologySource osOne = writeOntologySource( "getByAccessionSimpleTest2", gc.getTime() );
        OntologyIndividual three = writeOntologyIndividual( "getByAccessionSimpleTest3", gc.getTime(), osOne );

        OntologyIndividual retrievedOne = ses.getOntologyIndividualByAccession( one.getTermAccession() );
        OntologyIndividual retrievedSeven = ses.getOntologyIndividualByAccession( three.getTermAccession() );

        assert ( retrievedOne != null ) :
                "The OntologyIndividual " + one.getTermAccession() + " has not been retrieved by the query";

        assert ( retrievedOne.getTermAccession().equals( one.getTermAccession() ) ) :
                "The OntologyIndividual " + one.getTermAccession() + " term accession does not match the retrieved" +
                "term accession of " + retrievedOne.getTermAccession();

        assert ( retrievedSeven != null ) :
                "The OntologyIndividual " + three.getTermAccession() + " has not been retrieved by the query";

        assert ( retrievedSeven.getTermAccession().equals( three.getTermAccession() ) ) :
                "The OntologyIndividual " + three.getTermAccession() + " term accession does not match the retrieved" +
                "term accession of " + retrievedSeven.getTermAccession();
    }

    @Test( groups = { "ontology", "hibernate" } )
    public void getByAccessionComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        OntologyIndividual one = writeOntologyIndividual( "getByAccessionComplexTest1", gc.getTime() );
        gc.set( 2004, Calendar.MARCH, 4 );
        OntologyIndividual two =
                writeOntologyIndividual( "getByAccessionComplexTest2", gc.getTime(), one.getEndurant() );

        OntologyIndividual retrievedOne = ses.getOntologyIndividualByAccession( one.getTermAccession() );
        OntologyIndividual retrievedTwo = ses.getOntologyIndividualByAccession( two.getTermAccession() );

        assert ( retrievedOne == null ) :
                "The OntologyIndividual " + retrievedOne.getTermAccession() + " (name = " + retrievedOne.getName() +
                ") should not be retrieved by the query as it is an older version";

        assert ( retrievedTwo != null ) :
                "The OntologyIndividual " + two.getTermAccession() + " has not been retrieved by the query";

        assert ( retrievedTwo.getTermAccession().equals( two.getTermAccession() ) ) :
                "The OntologyIndividual " + two.getTermAccession() + " term accession does not match the retrieved" +
                "term accession of " + retrievedTwo.getTermAccession();
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
