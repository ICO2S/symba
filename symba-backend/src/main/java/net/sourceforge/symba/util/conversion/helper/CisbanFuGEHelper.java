package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Bio.Investigation.Investigation;
import fugeOM.Collection.*;
import fugeOM.Common.Description.Description;
import fugeOM.Common.Protocol.Protocol;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

import net.sourceforge.symba.util.CisbanHelper;

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

public class CisbanFuGEHelper implements MappingHelper<FuGE, FugeOMCollectionFuGEType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanAuditCollectionHelper cac;
    private final CisbanOntologyCollectionHelper coc;
    private final CisbanReferenceableCollectionHelper crc;
    private final CisbanMaterialCollectionHelper cmc;
    private final CisbanProviderHelper cpr;
    private final CisbanDataCollectionHelper cdc;
    private final CisbanProtocolCollectionHelper cpc;
    private final CisbanInvestigationCollectionHelper cinv;
    private final CisbanHelper helper;

    public CisbanFuGEHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cac = new CisbanAuditCollectionHelper();
        this.coc = new CisbanOntologyCollectionHelper();
        this.crc = new CisbanReferenceableCollectionHelper();
        this.cmc = new CisbanMaterialCollectionHelper();
        this.cpr = new CisbanProviderHelper();
        this.cdc = new CisbanDataCollectionHelper();
        this.cpc = new CisbanProtocolCollectionHelper();
        this.cinv = new CisbanInvestigationCollectionHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public FuGE unmarshal( FugeOMCollectionFuGEType frXML,
                           FuGE fr ) throws RealizableEntityServiceException {

        // Must be done first: AuditCollection, OntologyCollection. After that must come ReferenceableCollection.

            // get and store in the database all AuditCollection information
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setAuditCollection( cac.unmarshal( frXML.getAuditCollection(), ( AuditCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.AuditCollection" ) ) );

            // get and store in the database all OntologyCollection information
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setOntologyCollection( coc.unmarshal( frXML.getOntologyCollection(), ( OntologyCollection ) reService.createDescribableOb(
                    "fugeOM.Collection.OntologyCollection" ) ) );

            // get and store in the database all ReferenceableCollection information
            if ( frXML.getReferenceableCollection() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setReferenceableCollection( crc.unmarshal( frXML.getReferenceableCollection(),
                        ( ReferenceableCollection ) reService.createDescribableOb( "fugeOM.Collection.ReferenceableCollection" ) ) );
            }

            // set all identifiable traits in the fuge object
            fr = ( FuGE ) ci.unmarshal( frXML, fr );

            // Get all MaterialCollection information
            if ( frXML.getMaterialCollection() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setMaterialCollection( cmc.unmarshal( frXML.getMaterialCollection(),
                        ( MaterialCollection ) reService.createDescribableOb( "fugeOM.Collection.MaterialCollection" ) ) );
            }

            // Get all DataCollection information
            if ( frXML.getDataCollection() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setDataCollection( cdc.unmarshal( frXML.getDataCollection(),
                        ( DataCollection ) reService.createDescribableOb( "fugeOM.Collection.DataCollection" ) ) );
            }

            // Get all ProtocolCollection information
            if ( frXML.getProtocolCollection() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setProtocolCollection( cpc.unmarshal( frXML.getProtocolCollection(),
                        ( ProtocolCollection ) reService.createDescribableOb( "fugeOM.Collection.ProtocolCollection" ) ) );
            }

            // Get a Provider, if present
            if ( frXML.getProvider() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setProvider( cpr.unmarshal( frXML.getProvider(),( Provider ) helper.getOrCreateLatest(
                frXML.getProvider().getEndurant(),
                "fugeOM.Collection.ProviderEndurant",
                frXML.getProvider().getName(),
                "fugeOM.Collection.Provider",
                System.err ) ) );
            }

            // Get an Investigation, if present
            if ( frXML.getInvestigationCollection() != null ) {
                // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
                fr.setInvestigationCollection(
                        cinv.unmarshal( frXML.getInvestigationCollection(),
                        ( InvestigationCollection ) reService.createDescribableOb( "fugeOM.Collection.InvestigationCollection" ) ) );
            }

        return fr;
    }

    public FugeOMCollectionFuGEType marshal( FugeOMCollectionFuGEType frXML,
                                             FuGE fr ) throws RealizableEntityServiceException {
        // get all identifiable traits from the fuge object
        frXML = ( FugeOMCollectionFuGEType ) ci.marshal( frXML, fr );

        // get from the database all AuditCollection information
        if ( fr.getAuditCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setAuditCollection( cac.marshal( new FugeOMCollectionAuditCollectionType(), fr.getAuditCollection() ) );
        }

        // get from the database all OntologyCollection information
        if ( fr.getOntologyCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setOntologyCollection( coc.marshal( new FugeOMCollectionOntologyCollectionType(), fr.getOntologyCollection() ) );
        }

        // get from the database all ReferenceableCollection information
        if ( fr.getReferenceableCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setReferenceableCollection( crc.marshal( new FugeOMCollectionReferenceableCollectionType(), fr.getReferenceableCollection() ) );
        }

        // Get all MaterialCollection information
        if ( fr.getMaterialCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setMaterialCollection( cmc.marshal( new FugeOMCollectionMaterialCollectionType(), fr.getMaterialCollection() ) );
        }

        // Get all Provider information
        if ( fr.getProvider() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProvider( cpr.marshal( new FugeOMCollectionProviderType(), fr.getProvider() ) );
        }

        // Get all data collection information
        if ( fr.getDataCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setDataCollection( cdc.marshal( new FugeOMCollectionDataCollectionType(), fr.getDataCollection() ) );
        }

        // Get all protocol collection information
        if ( fr.getProtocolCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProtocolCollection( cpc.marshal( new FugeOMCollectionProtocolCollectionType(), fr.getProtocolCollection() ) );
        }
        // Get an Investigation, if present
        if ( fr.getInvestigationCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML.setInvestigationCollection( cinv.marshal( new FugeOMCollectionInvestigationCollectionType(), fr.getInvestigationCollection() ) );
        }
        return frXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML() {
        return generateRandomXML( new FugeOMCollectionFuGEType() );
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType frXML ) {

        // generate identifiable traits
        frXML = ( FugeOMCollectionFuGEType ) ci.generateRandomXML( frXML );

        // generate AuditCollection information
        if ( frXML.getAuditCollection() == null ) {
            frXML = cac.generateRandomXMLwithLinksOut( frXML );
        }

        // generate OntologyCollection information
        if ( frXML.getOntologyCollection() == null ) {
            frXML.setOntologyCollection( coc.generateRandomXML( new FugeOMCollectionOntologyCollectionType() ) );
        }

        // generate ReferenceableCollection information
        if ( frXML.getReferenceableCollection() == null ) {
            frXML = crc.generateRandomXMLwithLinksOut( frXML );
        }

        // Get all MaterialCollection information
        if ( frXML.getMaterialCollection() == null ) {
            frXML = cmc.generateRandomXMLWithLinksOut( frXML );
        }

        // Get all data collection information - MUST BE DONE before Protocol and after Material
        if ( frXML.getDataCollection() == null ) {
            frXML = cdc.generateRandomXMLWithLinksOut( frXML );
        }

        // Get all protocol collection information
        if ( frXML.getProtocolCollection() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpc.generateRandomXML( frXML );
        }

        // Get all Provider information
        if ( frXML.getProvider() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpr.generateRandomXMLWithLinksOut( frXML );
        }

        // Get an Investigation, if present
        if ( frXML.getInvestigationCollection() == null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML = cinv.generateRandomXMLWithLinksOut( frXML );
        }

        return frXML;
    }

    // you need to remember that the latest version of an experiment may have added Equipment
    // and added Ontology Terms, just to name a few.
    public FuGE getLatestVersion( FuGE fuge ) throws RealizableEntityServiceException {

        // get the latest version of the Identifiable parts of the experiment object
        fuge = ( FuGE ) ci.getLatestVersion( fuge );

        if ( fuge.getAuditCollection() != null ) {
            // For each item in the Audit Collection, make sure you are retrieving its latest version.
            fuge.setAuditCollection( cac.getLatestVersion( fuge.getAuditCollection() ) );
        }

        if ( fuge.getOntologyCollection() != null ) {
            // For each item in the Ontology Collection, make sure you are retrieving its latest version.
            fuge.setOntologyCollection( coc.getLatestVersion( fuge.getOntologyCollection() ) );
        }

        if ( fuge.getReferenceableCollection() != null ) {
            // For each item in the Referenceable Collection, make sure you are retrieving its latest version.
            fuge.setReferenceableCollection( crc.getLatestVersion( fuge.getReferenceableCollection() ) );
        }

        if ( fuge.getMaterialCollection() != null ) {
            // todo same sort of check as done in ontologies and protocols should happen here too.
            // For each item in the Referenceable Collection, make sure you are retrieving its latest version.
            fuge.setMaterialCollection( cmc.getLatestVersion( fuge.getMaterialCollection() ) );
        }

        if ( fuge.getProvider() != null ) {
            // Make sure you are retrieving the latest version of the provider.
            fuge.setProvider( cpr.getLatestVersion( fuge.getProvider() ) );
        }

        if ( fuge.getDataCollection() != null ) {
            // For each item in the Referenceable Collection, make sure you are retrieving its latest version.
            fuge.setDataCollection( cdc.getLatestVersion( fuge.getDataCollection() ) );
        }

        if ( fuge.getProtocolCollection() != null ) {
            // first, check for new protocols in the protocol collection. These happen when the underlying protocols
            // are updated separately from the addition of data to a given experiment
            // The top-level protocol is the one that doesn't have "Component" in the name
            String protocolType = null;
            for ( Object obj : fuge.getProtocolCollection().getProtocols() ) {
                if ( !( ( Protocol ) obj ).getName().contains( "Component" ) ) {
                    protocolType = ( ( Protocol ) obj ).getEndurant().getIdentifier();
                    break;
                }
            }
            if ( protocolType != null ) {
                fuge.getProtocolCollection().setProtocols( cpc.addRelevantProtocols( fuge, protocolType ) );
            }
            fuge.getProtocolCollection().setAllEquipment(
                    cpc.addRelevantEquipment(
                            fuge, ( Set<Protocol> ) fuge.getProtocolCollection().getProtocols() ) );
            // todo should we really re-get all equipment? If so, should do the same for material, software. etc...
            // For each item in the Protocol Collection, make sure you are retrieving its latest version.
            fuge.setProtocolCollection( cpc.getLatestVersion( fuge.getProtocolCollection() ) );
            // then, check for new ontology terms in the protocols. These happen when the underlying protocols
            // are updated separately from the addition of data to a given experiment
            fuge = coc.addRelevantOntologyTerms( fuge );
        }
        return fuge;

    }

    public FuGE getLatestVersion( String endurantId ) throws RealizableEntityServiceException {

        FuGE fuge = null;

        // First, get the latest experiment (if any) associated with this endurant.
        try {
            fuge = ( FuGE ) reService.findLatestByEndurant( endurantId );
//            System.err.println( "Endurant found, object loaded: " + endurantId );
        } catch ( fugeOM.service.RealizableEntityServiceException e ) {
            // No identifiable associated with the endurant, or no such endurant. Return empty object.
            return fuge;
        }

        return getLatestVersion( fuge );
    }

    public void prettyHtml( FuGE fuge, PrintWriter printStream ) throws RealizableEntityServiceException {

        printStream.println( "<h3>" );
        ci.prettyHtml( "Experiment Name: ", fuge, printStream );
        printStream.println( "</h3>" );

        // Now print out the initial creator of the FuGE object
        if ( fuge.getProvider().getProvider().getContact().getName() != null &&
                fuge.getProvider().getProvider().getContact().getName().length() > 0 ) {
            String provider;
            provider = fuge.getProvider().getProvider().getContact().getName();

            printStream.println( "<h4>" );
            printStream.println( "Provider of the Experiment: " + provider );
            printStream.println( "</h4>" );
        }


        if ( fuge.getInvestigationCollection() != null ) {
            Collection collection = fuge.getInvestigationCollection().getInvestigations();
            if ( !collection.isEmpty() ) {
                for ( Object obj : collection ) {
                    Investigation inv = ( Investigation ) obj;
                    for ( Object obj2 : inv.getDescriptions() ) {
                        Description de = ( Description ) obj2;
                        printStream.println( "<p>" );
                        printStream.println( de.getText() );
                        printStream.println( "</p>" );
                    }
                }
            }
        }
        printStream.println( "<table border=\"1\">" );
        cpc.prettyHtml( fuge.getProtocolCollection(), printStream );
        printStream.println( "</table>" );
        printStream.flush();
    }
}
