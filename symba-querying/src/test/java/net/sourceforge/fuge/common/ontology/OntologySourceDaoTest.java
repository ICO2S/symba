package net.sourceforge.fuge.common.ontology;

import org.testng.annotations.Test;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.versioning.Endurant;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;
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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-core/src/main/java/fugeOM/Common/IdentifiableDaoImpl.java $
 */
public class OntologySourceDaoTest {

    @Test( groups = { "ontology", "hibernate" } )
    public void getLatestOntologySourcesSimpleTest() {

        // First, count the number of latest ontology sources already in the database. Then load in 3 more, each
        // with their own endurant. Then, check to see that the number has incremented by 3.
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        int startNumber = ses.getLatestOntologySources().size();

        // We're not testing the Audit Trail here, so just put in all with the same date.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 OntologySources, each of which has its own Endurant.
        OntologySource one = writeOntologySource( "countOntologySourcesSimpleTest1", gc.getTime() );
        OntologySource two = writeOntologySource( "countOntologySourcesSimpleTest2", gc.getTime() );
        OntologySource three = writeOntologySource( "countOntologySourcesSimpleTest3", gc.getTime() );

        List<OntologySource> ontologySources = ses.getLatestOntologySources();

        int endNumber = ontologySources.size();

        assert ( endNumber - startNumber == 3 ) :
                "3 OntologySources, each with their own Endurant, have been loaded. However, " +
                "the starting number of OntologySources was " + startNumber + " and the " +
                "ending number of OntologySources was " + endNumber + ". The difference in these values should " +
                "have been 3";

        // All three must be in the list.
        assert ( ontologySources.contains( one ) ) : "OntologySource one must be in the list, but it isn't.";
        assert ( ontologySources.contains( two ) ) : "OntologySource two must be in the list, but it isn't.";
        assert ( ontologySources.contains( three ) ) : "OntologySource three must be in the list, but it isn't.";

    }

    @Test( groups = { "ontology", "hibernate" } )
    public void getLatestOntologySourcesComplexTest() {

        // First, count the number of latest ontology sources already in the database. Then load in 3 more, 2
        // with their own endurant, and the third having the second's endurant. Then, check to see that the number
        // has incremented by 2.
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        int startNumber = ses.getLatestOntologySources().size();

        // We're not testing the Audit Trail here, so just use the same date except for the new version of the existing
        // object.
        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        // Now load 3 experiments, one of which is a new version of another. This will lead to a total of 2 new
        // experiments based on the criteria within countExperiments().
        OntologySource one = writeOntologySource( "countOntologySourcesComplexTest1", gc.getTime() );
        OntologySource two = writeOntologySource( "countOntologySourcesComplexTest2", gc.getTime() );
        gc.set( 2001, Calendar.MARCH, 4 );
        OntologySource twoA =
                writeOntologySource( "countOntologySourcesComplexTest2a", gc.getTime(), two.getEndurant() );

        List<OntologySource> ontologySources = ( List<OntologySource> ) ses.getLatestOntologySources();

        int endNumber = ontologySources.size();

        assert ( endNumber - startNumber == 2 ) :
                "3 OntologySources, two with their own Endurant and one re-using an Endurant, have been loaded. However, " +
                "the starting number of OntologySources was " + startNumber + " and the " +
                "ending number of OntologySources was " + endNumber + ". The difference in these values should " +
                "have been 2";

        // at least numbers one and twoA should be in the list, and definitely number two should not be
        assert ( ontologySources.contains( one ) ) : "OntologySource one must be in the list, but it isn't.";
        assert ( ontologySources.contains( twoA ) ) : "OntologySource twoA must be in the list, but it isn't.";
        assert ( !ontologySources.contains( two ) ) : "OntologySource two must NOT be in the list, but it is.";
    }

    private OntologySource writeOntologySource( String name, Date time ) {

        return writeOntologySource( name, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private OntologySource writeOntologySource( String name, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        OntologySource ontologySource = ( OntologySource ) es
                .createIdentifiable( name + ":OntologySource:" + String.valueOf( Math.random() * 10000 ), name,
                        "net.sourceforge.fuge.common.ontology.OntologySource" );

        // add an Endurant value (required) after saving the endurant to the database
        ontologySource.setEndurant( endurant );

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
