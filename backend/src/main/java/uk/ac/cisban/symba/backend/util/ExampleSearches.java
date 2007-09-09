package uk.ac.cisban.symba.backend.util;

import fugeOM.Collection.FuGE;
import fugeOM.Common.Audit.Audit;
import fugeOM.Common.Audit.Person;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Protocol.Equipment;
import fugeOM.Common.Protocol.Protocol;
import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/*
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

public class ExampleSearches {
    private final RealizableEntityService reService;

    public ExampleSearches() {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.reService = serviceLocator.getRealizableEntityService();
    }

    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.out.println( "USAGE: fugeOM.util.ExampleSearches log-file" );
            return;
        }
        ExampleSearches searches = new ExampleSearches();
        searches.runAll( args[0] );
    }

    private void runAll( String arg ) {
        try {
            FileOutputStream fout = new FileOutputStream( arg );
            PrintStream fos = new PrintStream( fout );

            listAllExperiments( fos );
//            String identifier = "urn:lsid:cisban.cisbs.org:Person:0:1";
            String identifier = "urn:lsid:cisban.cisbs.org:Person:dbee3d36-e819-44be-b238-8717b2090c88";
            String endurant = "urn:lsid:cisban.cisbs.org:PersonEndurant:fe8abb3e-e7af-4a46-b06c-9ee6ec677519";
            Person person = ( Person ) reService.findIdentifiable( identifier );
            listAllLatestExperimentsWithPerson( person.getEndurant().getIdentifier(), fos );
            listAllExperimentsWithPerson( person.getEndurant().getIdentifier(), fos );
            listAllPeople( fos );
            listAllLatestPeople( fos );
            listAllProtocols( fos );
            listAllEquipment( fos );

            Identifiable i = ( Identifiable ) reService.findLatestByEndurant( endurant );
            fos.println( "Latest version by endurant " + endurant + " is: " + i.getIdentifier() );
            fos.println(
                    "Latest version of identifier by endurant " + endurant + " is: " +
                            reService.findLatestIdentifierByEndurant( endurant ) );
            fos.println(
                    "Latest version of identifier by identifier " + identifier + " is: " +
                            reService.findLatestIdentifierByIdentifiable( identifier ) );
            fos.println(
                    "Latest version of identifier by unknown " + identifier + " is: " +
                            reService.findLatestIdentifierByUnknown( identifier ) );

            endurant = "urn:lsid:cisban.cisbs.org:FuGEEndurant:ae0fe6ad-293f-4395-9c09-4ebe384caa2b";
            identifier = "urn:lsid:cisban.cisbs.org:FuGE:defb8c3a-9a4d-42cd-9d64-46480aa75557";
            Identifiable i2 = ( Identifiable ) reService.findLatestByEndurant( endurant );
            fos.println( "Latest version by endurant " + endurant + " is: " + i2.getIdentifier() );
            fos.println(
                    "Latest version of identifier by endurant " + endurant + " is: " +
                            reService.findLatestIdentifierByEndurant( endurant ) );
            fos.println(
                    "Latest version of identifier by identifier " + identifier + " is: " +
                            reService.findLatestIdentifierByIdentifiable( identifier ) );
            fos.println(
                    "Latest version of identifier by unknown " + identifier + " is: " +
                            reService.findLatestIdentifierByUnknown( identifier ) );

            fos.println( "Count of Number of Latest Experiments: " + reService.countLatestExperiments() );
            fos.println( "Count of Number of Data Sets: " + reService.countData() );
            fos.close();

        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( RealizableEntityServiceException e ) {
            e.printStackTrace();
        }
    }

    private void listAllPeople( PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllPeople) Will get ALL versions of all people..." );
        List people = reService.getAllPeople();
        for ( Object obj : people ) {
            Person person = ( Person ) obj;
            fos.println( "(listAllPeople) identifier: " + person.getIdentifier() );
            fos.println( "(listAllPeople) endurant  : " + person.getEndurant().getIdentifier() );
            // Print the date of each
            for ( Object o : person.getAuditTrail() ) {
                Audit audit = ( Audit ) o;
                fos.print( "(listAllPeople) audit date: " + audit.getDate() );
                fos.println( "; audit action: " + audit.getAction() );
            }
        }
    }

    private void listAllLatestPeople( PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllLatestPeople) Will get current versions of all people..." );
        List people = reService.getAllLatestPeople();
        for ( Object obj : people ) {
            Person person = ( Person ) obj;
            fos.println( "(listAllLatestPeople) identifier: " + person.getIdentifier() );
            fos.println( "(listAllLatestPeople) endurant  : " + person.getEndurant().getIdentifier() );
            // Print the date of each
            for ( Object o : person.getAuditTrail() ) {
                Audit audit = ( Audit ) o;
                fos.print( "(listAllLatestPeople) audit date: " + audit.getDate() );
                fos.println( "; audit action: " + audit.getAction() );
            }
        }
    }

    private void listAllLatestExperimentsWithPerson( String endurantId,
                                                     PrintStream fos ) throws RealizableEntityServiceException {
        fos.println(
                "(listAllLatestExperimentsWithPerson) Will get current versions of experiments for all contact versions..." );
        List exps = reService.getAllLatestExperimentsWithContact( endurantId );
        for ( Object obj : exps ) {
            FuGE fuge = ( FuGE ) obj;
            fos.println( "(listAllLatestExperimentsWithPerson): " + fuge.getIdentifier() );
            // Print the date of each experiment
            for ( Object o : fuge.getAuditTrail() ) {
                Audit audit = ( Audit ) o;
                fos.print( "(listAllLatestExperimentsWithPerson) audit date: " + audit.getDate() );
                fos.println( "; audit action: " + audit.getAction() );
            }
        }
    }

    private void listAllEquipment( PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllEquipment) Running.." );
        List equipment = reService.getAllEquipment();
        for ( Object obj : equipment ) {
            Equipment equip = ( Equipment ) obj;
            fos.println( "(listAllEquipment) Equipment in database: " + equip.getIdentifier() );
        }
    }

    private void listAllProtocols( PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllProtocols) Running.." );
        List protocols = reService.getAllProtocols();
        for ( Object obj : protocols ) {
            Protocol protocol = ( Protocol ) obj;
            fos.println( "(listAllProtocols) Protocol in database: " + protocol.getIdentifier() );
        }
    }

    private void listAllExperimentsWithPerson( String endurantId,
                                               PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllExperimentsWithPerson) Will get all versions of experiments for all contact versions..." );
        List exps = reService.getAllExperimentsWithContact( endurantId );
        for ( Object obj : exps ) {
            FuGE fuge = ( FuGE ) obj;
            fos.println( "(listAllExperimentsWithPerson): " + fuge.getIdentifier() );
            // Print the date of each experiment
            for ( Object o : fuge.getAuditTrail() ) {
                Audit audit = ( Audit ) o;
                fos.print( "(listAllExperimentsWithPerson) audit date: " + audit.getDate() );
                fos.println( "; audit action: " + audit.getAction() );
            }

        }
    }

    private void listAllExperiments( PrintStream fos ) throws RealizableEntityServiceException {
        fos.println( "(listAllExperiments) Running.." );
        List exps = reService.getAllExperiments();
        for ( Object obj : exps ) {
            FuGE fuge = ( FuGE ) obj;
            fos.println( "(listAllExperiments) Experiment in database: " + fuge.getIdentifier() );
        }
    }
}
