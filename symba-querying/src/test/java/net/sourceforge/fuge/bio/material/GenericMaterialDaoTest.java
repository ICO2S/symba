package net.sourceforge.fuge.bio.material;

import org.testng.annotations.Test;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.versioning.Endurant;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

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
public class GenericMaterialDaoTest {

    @Test( groups = { "material", "hibernate" } )
    public void getLatestGenericMaterialsSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericMaterial one = writeGenericMaterial( "getLatestGenericMaterialsSimpleTest1", false, gc.getTime() );
        GenericMaterial two = writeGenericMaterial( "getLatestGenericMaterialsSimpleTest2", false, gc.getTime() );
        GenericMaterial three = writeGenericMaterial( "getLatestGenericMaterialsSimpleTest3", true, gc.getTime() );

        List fullResults = ses.getLatestGenericMaterials( false );

        assert ( fullResults.contains( one ) ) :
                "GenericMaterials do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericMaterials do not contain experiment " + two.getIdentifier();
        assert ( fullResults.contains( three ) ) :
                "GenericMaterials do not contain experiment " + three.getIdentifier();
    }


    @Test( groups = { "material", "hibernate" } )
    public void getLatestGenericMaterialsComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericMaterial one = writeGenericMaterial( "getLatestGenericMaterialsComplexTest1", false, gc.getTime() );
        GenericMaterial three =
                writeGenericMaterial( "getLatestGenericMaterialsComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        GenericMaterial two =
                writeGenericMaterial( "getLatestGenericMaterialsComplexTest2", false, gc.getTime(), one.getEndurant() );
        GenericMaterial four =
                writeGenericMaterial( "getLatestGenericMaterialsComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestGenericMaterials( false );

        assert ( !fullResults.contains( one ) ) :
                "GenericMaterials contain experiment that is an older version: " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericMaterials do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericMaterials contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "GenericMaterials do not contain experiment " + four.getIdentifier();
    }

    @Test( groups = { "material", "hibernate" } )
    public void getLatestGenericMaterialsDummiesSimpleTest() {
        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments. When we run getSummaries, we will probably get more than those two. However, we
        // will just check that those two are returned. By using contains(), we know they have the right information,
        // as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericMaterial one = writeGenericMaterial( "getLatestGenericMaterialsDummiesSimpleTest1", true, gc.getTime() );
        GenericMaterial two = writeGenericMaterial( "getLatestGenericMaterialsDummiesSimpleTest2", true, gc.getTime() );
        GenericMaterial three = writeGenericMaterial( "getLatestGenericMaterialsDummiesSimpleTest3", false, gc.getTime() );

        List fullResults = ses.getLatestGenericMaterials( true );

        assert ( fullResults.contains( one ) ) :
                "GenericMaterials do not contain experiment " + one.getIdentifier();
        assert ( fullResults.contains( two ) ) :
                "GenericMaterials do not contain experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericMaterials contains experiment " + three.getIdentifier();
    }


    @Test( groups = { "material", "hibernate" } )
    public void getLatestGenericMaterialsDummiesComplexTest() {

        SymbaEntityService ses = ServiceLocator.instance().getSymbaEntityService();

        // load in two experiments with the same endurant. When we run getSummaries, we will probably get more than
        // those two. However, we will just check that those two are returned. By using contains(), we know they have
        // the right information, as the equals() method tests all of it.

        GregorianCalendar gc = new GregorianCalendar();
        gc.set( 2000, Calendar.MARCH, 4 );

        GenericMaterial one = writeGenericMaterial( "getLatestGenericMaterialsDummiesComplexTest1", false, gc.getTime() );
        GenericMaterial three =
                writeGenericMaterial( "getLatestGenericMaterialsDummiesComplexTest3", true, gc.getTime() );
        gc.set( 2002, Calendar.MARCH, 4 );
        GenericMaterial two =
                writeGenericMaterial( "getLatestGenericMaterialsDummiesComplexTest2", false, gc.getTime(), one.getEndurant() );
        GenericMaterial four =
                writeGenericMaterial( "getLatestGenericMaterialsDummiesComplexTest4", true, gc.getTime(),
                        three.getEndurant() );

        List fullResults = ses.getLatestGenericMaterials( true );

        assert ( !fullResults.contains( one ) ) :
                "GenericMaterials contain experiment that is an older version: " + one.getIdentifier();
        assert ( !fullResults.contains( two ) ) :
                "GenericMaterials contains experiment " + two.getIdentifier();
        assert ( !fullResults.contains( three ) ) :
                "GenericMaterials contain experiment that is an older version: " + three.getIdentifier();
        assert ( fullResults.contains( four ) ) :
                "GenericMaterials do not contain experiment " + four.getIdentifier();
    }

    private GenericMaterial writeGenericMaterial( String name, boolean setupDummies, Date time ) {
        return writeGenericMaterial( name, setupDummies, time, DatabaseObjectHelper.getOrLoadEndurant( null, null ) );
    }

    private GenericMaterial writeGenericMaterial( String name, boolean setupDummies, Date time, Endurant endurant ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        GenericMaterial genericMaterial = ( GenericMaterial ) es.createIdentifiable( name + ":GenericMaterial:" +
                                                                                     String.valueOf(
                                                                                             Math.random() * 10000 ),
                name, "net.sourceforge.fuge.bio.material.GenericMaterial" );

        if ( setupDummies ) {
            genericMaterial.setName( genericMaterial.getName() + " Dummy" );
        }

        // add an Endurant value (required) after saving the endurant to the database
        genericMaterial.setEndurant( endurant );

        Set<Audit> audits = new HashSet<Audit>();

        Audit audit = ( Audit ) es.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
        audit.setDate( new java.sql.Timestamp( time.getTime() ) );
        audit.setAction( AuditAction.creation );
        audit = ( Audit ) es.create( "net.sourceforge.fuge.common.audit.Audit", audit );
        audits.add( audit );

        genericMaterial.setAuditTrail( audits );
        genericMaterial =
                ( GenericMaterial ) es.create( "net.sourceforge.fuge.bio.material.GenericMaterial", genericMaterial );

        return genericMaterial;

    }
}
