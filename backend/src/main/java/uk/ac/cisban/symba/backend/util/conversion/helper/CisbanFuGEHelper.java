package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Investigation.Investigation;
import fugeOM.Collection.FuGE;
import fugeOM.Common.Description.Description;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;

import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Collection;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanFuGEHelper.java $
 *
 */

public class CisbanFuGEHelper {
    private final RealizableEntityService reService;
    private final CisbanIdentifiableHelper ci;
    private final CisbanAuditCollectionHelper cac;
    private final CisbanOntologyCollectionHelper coc;
    private final CisbanReferenceableCollectionHelper crc;
    private final CisbanMaterialCollectionHelper cmc;
    private final CisbanProviderHelper cpr;
    private final CisbanDataCollectionHelper cdc;
    private final CisbanProtocolCollectionHelper cpc;
    private final CisbanInvestigationCollectionHelper cinv;

    public CisbanFuGEHelper( RealizableEntityService reService ) {
        this.reService = reService;
        this.ci = new CisbanIdentifiableHelper( reService, new CisbanDescribableHelper( reService ) );
        this.cac = new CisbanAuditCollectionHelper( reService, ci );
        this.coc = new CisbanOntologyCollectionHelper( reService, ci );
        this.crc = new CisbanReferenceableCollectionHelper( reService, ci );
        this.cmc = new CisbanMaterialCollectionHelper( reService, ci );
        this.cpr = new CisbanProviderHelper( reService, ci );
        this.cdc = new CisbanDataCollectionHelper( reService, ci );
        this.cpc = new CisbanProtocolCollectionHelper( reService, ci );
        this.cinv = new CisbanInvestigationCollectionHelper( reService, ci );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public FuGE unmarshalFuGE( FugeOMCollectionFuGEType frXML,
                               FuGE fr ) throws RealizableEntityServiceException, URISyntaxException, LSIDException {

        // Must be done first: AuditCollection, OntologyCollection. After that must come ReferenceableCollection.

        // get and store in the database all AuditCollection information
        // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
        fr.setAuditCollection( cac.unmarshalAuditCollection( frXML.getAuditCollection() ) );

        // get and store in the database all OntologyCollection information
        // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
        fr.setOntologyCollection( coc.unmarshalOntologyCollection( frXML.getOntologyCollection() ) );

        // get and store in the database all ReferenceableCollection information
        if ( frXML.getReferenceableCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setReferenceableCollection( crc.unmarshalReferenceableCollection( frXML.getReferenceableCollection() ) );
        }

        // set all identifiable traits in the fuge object
        fr = ( FuGE ) ci.unmarshalIdentifiable( frXML, fr );

        // Get all MaterialCollection information
        if ( frXML.getMaterialCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setMaterialCollection( cmc.unmarshalMaterialCollection( frXML.getMaterialCollection() ) );
        }

        // Get all DataCollection information
        if ( frXML.getDataCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setDataCollection( cdc.unmarshalDataCollection( frXML.getDataCollection() ) );
        }

        // Get all ProtocolCollection information
        if ( frXML.getProtocolCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setProtocolCollection( cpc.unmarshalProtocolCollection( frXML.getProtocolCollection() ) );
        }

        // Get a Provider, if present
        if ( frXML.getProvider() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setProvider( cpr.unmarshalProvider( frXML.getProvider() ) );
        }

        // Get an Investigation, if present
        if ( frXML.getInvestigationCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setInvestigationCollection(
                    cinv.unmarshalInvestigationCollection( frXML.getInvestigationCollection() ) );
        }

        return fr;
    }

    public FugeOMCollectionFuGEType marshalFuGE( FugeOMCollectionFuGEType frXML,
                                                 FuGE fr ) throws URISyntaxException, RealizableEntityServiceException {
        // get all identifiable traits from the fuge object
        frXML = ( FugeOMCollectionFuGEType ) ci.marshalIdentifiable( frXML, fr );

        // get from the database all AuditCollection information
        if ( fr.getAuditCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setAuditCollection( cac.marshalAuditCollection( fr.getAuditCollection() ) );
        }

        // get from the database all OntologyCollection information
        if ( fr.getOntologyCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setOntologyCollection( coc.marshalOntologyCollection( fr.getOntologyCollection() ) );
        }

        // get from the database all ReferenceableCollection information
        if ( fr.getReferenceableCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setReferenceableCollection( crc.marshalReferenceableCollection( fr.getReferenceableCollection() ) );
        }

        // Get all MaterialCollection information
        if ( fr.getMaterialCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setMaterialCollection( cmc.marshalMaterialCollection( fr.getMaterialCollection() ) );
        }

        // Get all Provider information
        if ( fr.getProvider() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProvider( cpr.marshalProvider( fr.getProvider() ) );
        }

        // Get all data collection information
        if ( fr.getDataCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setDataCollection( cdc.marshalDataCollection( fr.getDataCollection() ) );
        }

        // Get all protocol collection information
        if ( fr.getProtocolCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProtocolCollection( cpc.marshalProtocolCollection( fr.getProtocolCollection() ) );
        }
        // Get an Investigation, if present
        if ( fr.getInvestigationCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML.setInvestigationCollection( cinv.marshalInvestigationCollection( fr.getInvestigationCollection() ) );
        }
        return frXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML() {
        // create a jaxb root object
        FugeOMCollectionFuGEType frXML = new FugeOMCollectionFuGEType();

        // generate identifiable traits
        frXML = ( FugeOMCollectionFuGEType ) ci.generateRandomXML( frXML );

        // generate AuditCollection information
        if ( frXML.getAuditCollection() == null ) {
            frXML = cac.generateRandomXML( frXML );
        }

        // generate OntologyCollection information
        if ( frXML.getOntologyCollection() == null ) {
            frXML = coc.generateRandomXML( frXML );
        }

        // generate ReferenceableCollection information
        if ( frXML.getReferenceableCollection() == null ) {
            frXML = crc.generateRandomXML( frXML );
        }

        // Get all MaterialCollection information
        if ( frXML.getMaterialCollection() == null ) {
            frXML = cmc.generateRandomXML( frXML );
        }

        // Get all data collection information - MUST BE DONE before Protocol and after Material
        if ( frXML.getDataCollection() == null ) {
            frXML = cdc.generateRandomXML( frXML );
        }

        // Get all protocol collection information
        if ( frXML.getProtocolCollection() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpc.generateRandomXML( frXML );
        }

        // Get all Provider information
        if ( frXML.getProvider() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpr.generateRandomXML( frXML );
        }

        // Get an Investigation, if present
        if ( frXML.getInvestigationCollection() == null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML = cinv.generateRandomXML( frXML );
        }

        return frXML;
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
            // For each item in the Protocol Collection, make sure you are retrieving its latest version.
            fuge.setProtocolCollection( cpc.getLatestVersion( fuge.getProtocolCollection() ) );
        }
        return fuge;
    }

    public void prettyHtml( FuGE fuge, PrintWriter printStream ) throws RealizableEntityServiceException {

        printStream.println( "<h3>" );
        ci.prettyHtml( "Experiment Name: ", fuge, printStream );
        printStream.println( "</h3>" );
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
