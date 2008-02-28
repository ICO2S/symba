package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.FuGE;
import fugeOM.Collection.ProtocolCollection;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

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

public class CisbanProtocolCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanProtocolHelper cpr;
    private final CisbanEquipmentHelper ceq;
    private final CisbanSoftwareHelper csw;
    private final CisbanProtocolApplicationHelper capp;
    private final RealizableEntityService reService;

    public CisbanProtocolCollectionHelper( RealizableEntityService reService,
                                           CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.cd = ci.getCisbanDescribableHelper();
        this.ci = ci;
        this.cpr = new CisbanProtocolHelper( reService, ci );
        this.ceq = new CisbanEquipmentHelper( reService, ci );
        this.csw = new CisbanSoftwareHelper( reService, ci );
        this.capp = new CisbanProtocolApplicationHelper( reService, this.cd, ci );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ProtocolCollection unmarshalProtocolCollection(
            FugeOMCollectionProtocolCollectionType protocolCollectionXML ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // create the fuge data collection object
        ProtocolCollection protocolCollection = ( ProtocolCollection ) reService
                .createDescribableOb( "fugeOM.Collection.ProtocolCollection" );

        // set describable information
        protocolCollection = ( ProtocolCollection ) cd
                .unmarshalDescribable( protocolCollectionXML, protocolCollection );

        protocolCollection = unmarshalCollectionContents( protocolCollectionXML, protocolCollection );

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.ProtocolCollection", protocolCollection );

        return protocolCollection;
    }

    public ProtocolCollection unmarshalCollectionContents( FugeOMCollectionProtocolCollectionType protocolCollectionXML,
                                                           ProtocolCollection protocolCollection ) throws LSIDException, RealizableEntityServiceException, URISyntaxException {
        // There are references to equipment and software within Protocol, so those should be loaded first

        // equipment
        Set<Equipment> equipments = new HashSet<Equipment>();
        for ( JAXBElement<? extends FugeOMCommonProtocolEquipmentType> elementXML : protocolCollectionXML
                .getEquipment() ) {
            // set fuge object
            equipments.add( ceq.unmarshalEquipment( elementXML.getValue() ) );
        }
        protocolCollection.setAllEquipment( equipments );

        // software
        Set<Software> softwares = new HashSet<Software>();
        for ( JAXBElement<? extends FugeOMCommonProtocolSoftwareType> elementXML : protocolCollectionXML
                .getSoftware() ) {
            FugeOMCommonProtocolSoftwareType protocolSoftwareXML = elementXML.getValue();
            softwares.add( csw.unmarshalSoftware( protocolSoftwareXML ) );
        }
        protocolCollection.setAllSoftwares( softwares );

        // protocol
        Set<Protocol> protocols = new HashSet<Protocol>();
        for ( JAXBElement<? extends FugeOMCommonProtocolProtocolType> protocolElementXML : protocolCollectionXML
                .getProtocol() ) {
            FugeOMCommonProtocolProtocolType protocolXML = protocolElementXML.getValue();
            protocols.add( cpr.unmarshalProtocol( protocolXML ) );
        }
        protocolCollection.setProtocols( protocols );

        // protocol application
        Set<ProtocolApplication> protocolApplications = new HashSet<ProtocolApplication>();
        for ( JAXBElement<? extends FugeOMCommonProtocolProtocolApplicationType> elementXML : protocolCollectionXML
                .getProtocolApplication() ) {
            FugeOMCommonProtocolProtocolApplicationType protocolApplicationXML = elementXML.getValue();
            protocolApplications.add( capp.unmarshalProtocolApplication( protocolApplicationXML ) );
        }
        protocolCollection.setAllProtocolApps( protocolApplications );

        return protocolCollection;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionProtocolCollectionType marshalProtocolCollection(
            ProtocolCollection protocolCollection ) throws URISyntaxException, RealizableEntityServiceException {
        FugeOMCollectionProtocolCollectionType protocolCollectionXML = new FugeOMCollectionProtocolCollectionType();

        protocolCollectionXML = ( FugeOMCollectionProtocolCollectionType ) cd
                .marshalDescribable( protocolCollectionXML, protocolCollection );

        // equipment
        for ( Object obj : protocolCollection.getAllEquipment() ) {
            FugeOMCommonProtocolEquipmentType equipmentXML = ceq.marshalEquipment( ( Equipment ) obj );
            if ( obj instanceof GenericEquipment ) {
                JAXBElement<? extends FugeOMCommonProtocolGenericEquipmentType> element = ( new ObjectFactory() )
                        .createGenericEquipment( ( FugeOMCommonProtocolGenericEquipmentType ) equipmentXML );
                protocolCollectionXML.getEquipment().add( element );
            }
        }

        // software
        for ( Object obj : protocolCollection.getAllSoftwares() ) {
            Software software = ( Software ) obj;
            FugeOMCommonProtocolSoftwareType softwareXML = csw.marshalSoftware( software );
            if ( obj instanceof GenericSoftware ) {
                JAXBElement<? extends FugeOMCommonProtocolGenericSoftwareType> element = ( new ObjectFactory() )
                        .createGenericSoftware( ( FugeOMCommonProtocolGenericSoftwareType ) softwareXML );
                protocolCollectionXML.getSoftware().add( element );
            }
        }

        // protocol
        for ( Object obj : protocolCollection.getProtocols() ) {
            Protocol protocol = ( Protocol ) obj;
            FugeOMCommonProtocolProtocolType protocolXML = cpr.marshalProtocol( protocol );
            if ( protocol instanceof GenericProtocol ) {
                JAXBElement<? extends FugeOMCommonProtocolGenericProtocolType> element = ( new ObjectFactory() )
                        .createGenericProtocol( ( FugeOMCommonProtocolGenericProtocolType ) protocolXML );
                protocolCollectionXML.getProtocol().add( element );
            }
        }

        // protocol application
        for ( Object obj : protocolCollection.getAllProtocolApps() ) {
            ProtocolApplication protocolApplication = ( ProtocolApplication ) obj;
            FugeOMCommonProtocolProtocolApplicationType paXML = capp.marshalProtocolApplication( protocolApplication );
            if ( protocolApplication instanceof GenericProtocolApplication ) {
                JAXBElement<? extends FugeOMCommonProtocolGenericProtocolApplicationType> element = ( new ObjectFactory() )
                        .createGenericProtocolApplication(
                                ( FugeOMCommonProtocolGenericProtocolApplicationType ) paXML );
                protocolCollectionXML.getProtocolApplication().add( element );
            }
        }

        return protocolCollectionXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType frXML ) {
        FugeOMCollectionProtocolCollectionType protocolCollectionXML = new FugeOMCollectionProtocolCollectionType();

        protocolCollectionXML = ( FugeOMCollectionProtocolCollectionType ) cd
                .generateRandomXML( protocolCollectionXML, ci );

        if ( protocolCollectionXML.getEquipment().isEmpty() ) {
            // equipment
            protocolCollectionXML = ceq.generateRandomXML( protocolCollectionXML, frXML );
        }

        if ( protocolCollectionXML.getSoftware().isEmpty() ) {
            // software
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML = new FugeOMCommonProtocolGenericSoftwareType();
                genericSoftwareXML = csw.generateRandomXML( genericSoftwareXML, protocolCollectionXML, frXML );
                JAXBElement<? extends FugeOMCommonProtocolGenericSoftwareType> element = ( new ObjectFactory() )
                        .createGenericSoftware( genericSoftwareXML );
                protocolCollectionXML.getSoftware().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocol().isEmpty() ) {
            // protocol
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolGenericProtocolType genericProtocolXML = new FugeOMCommonProtocolGenericProtocolType();
                genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) cpr
                        .generateRandomXML( genericProtocolXML, protocolCollectionXML, frXML );
                JAXBElement<? extends FugeOMCommonProtocolGenericProtocolType> element = ( new ObjectFactory() )
                        .createGenericProtocol( genericProtocolXML );
                protocolCollectionXML.getProtocol().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocolApplication().isEmpty() ) {
            // protocol application
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                JAXBElement<? extends FugeOMCommonProtocolGenericProtocolApplicationType> element = ( new ObjectFactory() )
                        .createGenericProtocolApplication(
                                ( FugeOMCommonProtocolGenericProtocolApplicationType ) capp
                                        .generateRandomXML( i, protocolCollectionXML, frXML ) );
                protocolCollectionXML.getProtocolApplication().add( element );
            }
        }

        frXML.setProtocolCollection( protocolCollectionXML );
        return frXML;
    }

    // We go through all equipment referenced in the protocols in protocolSet, retrieving all present.
    // These will get added to the experiment.
    public Set<Equipment> addRelevantEquipment( FuGE fuge,
                                                Set<Protocol> protocolSet ) throws RealizableEntityServiceException {
        Set<Equipment> equipmentSet;
        if ( fuge.getProtocolCollection() != null && fuge.getProtocolCollection().getAllEquipment() != null ) {
            equipmentSet = ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment();
        } else {
            equipmentSet = new HashSet<Equipment>();
        }

        for ( Protocol obj : protocolSet ) {
            if ( obj instanceof GenericProtocol ) {
                GenericProtocol gp = ( GenericProtocol ) obj;
                gp = ( GenericProtocol ) reService.greedyGet( gp );
                if ( gp.getGenPrtclToEquip() != null ) {
                    for ( Object equipObj : gp.getGenPrtclToEquip() ) {
                        if ( equipObj instanceof GenericEquipment ) {
                            if ( !equipmentSet.contains( equipObj ) ) {
                                // the current equipment is not yet in the experiment. Add it.
                                equipmentSet.add( ( GenericEquipment ) equipObj );
                            }
                        }
                    }
                }
            }
        }

        return equipmentSet;
    }

    // We go through all protocols in the database, retrieving all that are directly associated (via GenericAction)
    // with the top-level investigation id'ed in protocolEndurantIdentifier. These will get added to the experiment.
    // In SyMBA, the protocolEndurantIdentifier is the endurant identifier.
    // only works for generic protocols
    public Set<Protocol> addRelevantProtocols( FuGE fuge,
                                               String protocolEndurantIdentifier ) throws RealizableEntityServiceException {

        Protocol abstractProtocol = ( Protocol ) reService.findLatestByEndurant( protocolEndurantIdentifier );

        Set<Protocol> protocolSet;
        if ( fuge.getProtocolCollection() != null ) {
            protocolSet = ( Set<Protocol> ) fuge.getProtocolCollection().getProtocols();
        } else {
            protocolSet = new HashSet<Protocol>();
        }

        if ( !( abstractProtocol instanceof GenericProtocol ) ) {
            return protocolSet;
        }

        GenericProtocol topLevelProtocol = ( GenericProtocol ) abstractProtocol;

        // Assume that, if there is already something in the protocol collection, it must at a minimum
        // already contain the top-level protocol
        if ( protocolSet.isEmpty() ) {
            protocolSet.add( topLevelProtocol );
        }

        protocolSet.addAll( getChildProtocols( topLevelProtocol, protocolSet ) );

        return protocolSet;
    }

    // Works similarly to addRelevantProtocols, but does not initialize the Set<Protocol> with values from the
    // top-level investigation.
    public Set<Protocol> getChildProtocols( GenericProtocol parentProtocol, Set<Protocol> alreadyAddedProtocols ) {

        if ( alreadyAddedProtocols == null ) {
            alreadyAddedProtocols = new HashSet<Protocol>();
        }

        for ( Object obj : parentProtocol.getGenericActions() ) {
            GenericAction genericAction = ( GenericAction ) obj;
            System.out.println( "Investigating genericAction.getName() = " + genericAction.getName() );
            // add the generic protocol referenced by the generic action.
            GenericProtocol genericProtocol = ( GenericProtocol ) genericAction.getGenProtocolRef();
            boolean matchFound = false;
            for ( Protocol protocol : alreadyAddedProtocols ) {
                if ( protocol instanceof GenericProtocol ) {
                    GenericProtocol gpSearch = ( GenericProtocol ) protocol;
                    if ( gpSearch.getEndurant()
                            .getIdentifier().trim()
                            .equals( genericProtocol.getEndurant().getIdentifier().trim() ) ) {
                        matchFound = true;
                        System.err.println( "Match Found" );
                        break;
                    }
                }
            }
            if ( !matchFound ) {
                // the current protocol is not yet in the experiment. Add it, and any actions that may be present
                // within this protocol.
                alreadyAddedProtocols.add( genericProtocol );
                alreadyAddedProtocols.addAll( getChildProtocols( genericProtocol, alreadyAddedProtocols ) );
            }
        }
        return alreadyAddedProtocols;
    }

    public ProtocolCollection getLatestVersion(
            ProtocolCollection protocolCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        protocolCollection = ( ProtocolCollection ) cd.getLatestVersion( protocolCollection, ci );

        // equipment : prepare updated set
        Set<Equipment> set = new HashSet<Equipment>();

        // load all the latest versions into the new set.
        for ( Object obj : protocolCollection.getAllEquipment() ) {
            set.add( ceq.getLatestVersion( ( Equipment ) obj ) );
        }
        protocolCollection.setAllEquipment( set );

        // software : prepare updated set
        Set<Software> set1 = new HashSet<Software>();

        // load all the latest versions into the new set.
        for ( Object obj : protocolCollection.getAllSoftwares() ) {
            set1.add( csw.getLatestVersion( ( Software ) obj ) );
        }
        protocolCollection.setAllSoftwares( set1 );

        // protocol : prepare updated set
        Set<Protocol> set2 = new HashSet<Protocol>();

        // load all the latest versions into the new set.
        for ( Object obj : protocolCollection.getProtocols() ) {
            set2.add( cpr.getLatestVersion( ( Protocol ) obj ) );
        }
        protocolCollection.setProtocols( set2 );

        // protocol application: prepare updated set
        Set<ProtocolApplication> set3 = new HashSet<ProtocolApplication>();

        // load all the latest versions into the new set.
        for ( Object obj : protocolCollection.getAllProtocolApps() ) {
            set3.add( capp.getLatestVersion( ( ProtocolApplication ) obj ) );
        }
        protocolCollection.setAllProtocolApps( set3 );

        return protocolCollection;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( ProtocolCollection protocolCollection, PrintStream printStream ) {
        for ( Object obj : protocolCollection.getProtocols() ) {
            cpr.prettyPrint( ( Protocol ) obj, printStream );
        }
    }

    public void prettyHtml( ProtocolCollection protocolCollection,
                            PrintWriter printStream ) throws RealizableEntityServiceException {

        if ( protocolCollection == null || protocolCollection.getProtocols() == null ) {
            printStream.println(
                    "Error: this experiment has no protocols. Please contact " +
                            "helpdesk@cisban.ac.uk" );
            return;
        }
        boolean afterFirst = false;
        for ( Object obj : protocolCollection.getProtocols() ) {
            if ( obj instanceof GenericProtocol ) {
                GenericProtocol temp = ( GenericProtocol ) obj;
                // Drill down from each top-level protocol all the way into the data files
                if ( !temp.getName().contains( "Component" ) ) {
                    // print out any data files associated with the GPAs associated with this protocol.
                    if ( !afterFirst ) {
                        afterFirst = true;
                        printStream.println( "<tr>" );
                    }
                    cpr.prettyHtml( null, temp, protocolCollection.getAllProtocolApps(), cd, printStream );
                }
            }
        }
        printStream.flush();
    }
}
