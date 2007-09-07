package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Data.DataPartition;
import fugeOM.Bio.Investigation.Factor;
import fugeOM.Bio.Investigation.FactorValue;
import fugeOM.Bio.Investigation.Investigation;
import fugeOM.Collection.InvestigationCollection;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

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
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanInvestigationCollectionHelper.java $
 *
 */

public class CisbanInvestigationCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanDescribableHelper cd;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanInvestigationCollectionHelper( RealizableEntityService reService, CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cd = ci.getCisbanDescribableHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    // todo investigation incomplete (only has identifiable elements)
    public InvestigationCollection unmarshalInvestigationCollection(
            FugeOMCollectionInvestigationCollectionType investigationCollectionXML ) throws RealizableEntityServiceException, URISyntaxException, LSIDException {

        InvestigationCollection investigationCollection = ( InvestigationCollection ) reService.createDescribableOb(
                "fugeOM.Collection.InvestigationCollection" );
        investigationCollection = ( InvestigationCollection ) cd.unmarshalDescribable(
                investigationCollectionXML, investigationCollection );

        Set<Factor> factors = new HashSet<Factor>();
        for ( FugeOMBioInvestigationFactorType factorXML : investigationCollectionXML.getFactor() ) {

            // Retrieve latest version from the database.
            Factor factor = ( Factor ) helper.getOrCreateLatest(
                    factorXML.getEndurant(),
                    "fugeOM.Bio.Investigation.FactorEndurant",
                    factorXML.getName(),
                    "fugeOM.Bio.Investigation.Factor",
                    System.err );

            factor = ( Factor ) ci.unmarshalIdentifiable( factorXML, factor );

            // set the non-identifiable traits

            // Set the object to exactly the object is that is associated
            // with this version group.
            factor.setFactorCategory( ( OntologyTerm ) reService.findIdentifiable( factorXML.getFactorCategory().getOntologyTermRef() ) );

            Set<FactorValue> factorValues = new HashSet<FactorValue>();
            for ( FugeOMBioInvestigationFactorValueType factorValueXML : factorXML.getFactorValue() ) {
                FactorValue factorValue = ( FactorValue ) reService.createDescribableOb(
                        "fugeOM.Bio.Investigation.FactorValue" );
                factorValue = ( FactorValue ) cd.unmarshalDescribable( factorValueXML, factorValue );
                // Set the object to exactly the object is that is associated
                // with this version group.
                factorValue.setValue( ( OntologyTerm ) reService.findIdentifiable( factorValueXML.getValue().getOntologyTermRef() ) );

                Set<DataPartition> dataPartitions = new HashSet<DataPartition>();
                for ( FugeOMBioInvestigationFactorValueType.DataPartitions dataPartitionXML : factorValueXML.getDataPartitions() ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    // todo - if not here, where does the dta partition sit?
                    DataPartition dataPartition = ( DataPartition ) reService.findIdentifiable( dataPartitionXML.getDataPartitionRef() );
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    dataPartition.setPartitionedData( ( Data ) reService.findIdentifiable( dataPartitionXML.getDataPartitionRef() ) );
                    // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
                    dataPartitions.add( dataPartition );
                }
                factorValue.setDataPartitions( dataPartitions );
                reService.createObInDB( "fugeOM.Bio.Investigation.FactorValue", factorValue );
                factorValues.add( factorValue );
            }
            factor.setFactorValues( factorValues );

            // load fuge object into database
            if ( factor.getId() != null ) {
                helper.assignAndLoadIdentifiable( factor, "fugeOM.Bio.Investigation.Factor", System.err );
            } else {
                helper.loadIdentifiable( factor, "fugeOM.Bio.Investigation.Factor", System.err );
            }
            factors.add( factor );
        }
        investigationCollection.setFactorCollection( factors );

        Set<Investigation> investigations = new HashSet<Investigation>();
        for ( FugeOMBioInvestigationInvestigationType investigationXML : investigationCollectionXML.getInvestigation() ) {
            Investigation investigation = ( Investigation ) helper.getOrCreateLatest(
                    investigationXML.getEndurant(),
                    "fugeOM.Bio.Investigation.InvestigationEndurant",
                    investigationXML.getName(),
                    "fugeOM.Bio.Investigation.Investigation",
                    System.err );
            investigation = ( Investigation ) ci.unmarshalIdentifiable( investigationXML, investigation );
            // load fuge object into database
            if ( investigation.getId() != null ) {
                helper.assignAndLoadIdentifiable( investigation, "fugeOM.Bio.Investigation.Investigation", System.err );
            } else {
                helper.loadIdentifiable( investigation, "fugeOM.Bio.Investigation.Investigation", System.err );
            }
            investigations.add( investigation );
        }
        investigationCollection.setInvestigations( investigations );

        reService.createObInDB( "fugeOM.Collection.InvestigationCollection", investigationCollection );
        return investigationCollection;
    }

    // todo investigation incomplete (only has identifiable elements)
    public FugeOMCollectionInvestigationCollectionType marshalInvestigationCollection(
            InvestigationCollection investigationCollection ) throws RealizableEntityServiceException, URISyntaxException {

        FugeOMCollectionInvestigationCollectionType investigationCollectionXML = new FugeOMCollectionInvestigationCollectionType();

        // set describable information
        investigationCollectionXML = ( FugeOMCollectionInvestigationCollectionType ) cd.marshalDescribable(
                investigationCollectionXML, investigationCollection );

        for ( Object obj : investigationCollection.getFactorCollection() ) {
            Factor factor = ( Factor ) obj;
            FugeOMBioInvestigationFactorType factorXML = new FugeOMBioInvestigationFactorType();

            factorXML = ( FugeOMBioInvestigationFactorType ) ci.marshalIdentifiable( factorXML, factor );

            // set the non-identifiable traits

            FugeOMBioInvestigationFactorType.FactorCategory categoryValueXML = new FugeOMBioInvestigationFactorType.FactorCategory();
            categoryValueXML.setOntologyTermRef( factor.getFactorCategory().getIdentifier() );
            factorXML.setFactorCategory( categoryValueXML );

            for ( Object obj2 : factor.getFactorValues() ) {
                FugeOMBioInvestigationFactorValueType factorValueXML = new FugeOMBioInvestigationFactorValueType();
                FactorValue factorValue = ( FactorValue ) obj2;

                // get any lazily loaded objects
                factorValue = ( FactorValue ) reService.greedyGet( factorValue );

                factorValueXML = ( FugeOMBioInvestigationFactorValueType ) cd.marshalDescribable(
                        factorValueXML, factorValue );
                FugeOMBioInvestigationFactorValueType.Value valueXML = new FugeOMBioInvestigationFactorValueType.Value();
                valueXML.setOntologyTermRef( factorValue.getValue().getIdentifier() );
                factorValueXML.setValue( valueXML );

                for ( Object obj3 : factorValue.getDataPartitions() ) {
                    FugeOMBioInvestigationFactorValueType.DataPartitions dataPartitionXML = new FugeOMBioInvestigationFactorValueType.DataPartitions();
                    DataPartition dataPartition = ( DataPartition ) obj3;
                    dataPartitionXML.setDataPartitionRef( dataPartition.getPartitionedData().getIdentifier() );
                    // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
                    factorValueXML.getDataPartitions().add( dataPartitionXML );
                }
                factorXML.getFactorValue().add( factorValueXML );
            }

            investigationCollectionXML.getFactor().add( factorXML );
        }

        for ( Object obj : investigationCollection.getInvestigations() ) {
            Investigation investigation = ( Investigation ) obj;
            FugeOMBioInvestigationInvestigationType investigationXML = new FugeOMBioInvestigationInvestigationType();

            investigationXML = ( FugeOMBioInvestigationInvestigationType ) ci.marshalIdentifiable(
                    investigationXML, investigation );
            investigationCollectionXML.getInvestigation().add( investigationXML );
        }

        return investigationCollectionXML;
    }

    // todo investigation incomplete (only has identifiable elements)
    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType fuGEType ) {
        FugeOMCollectionInvestigationCollectionType investigationCollectionXML = new FugeOMCollectionInvestigationCollectionType();
        investigationCollectionXML = ( FugeOMCollectionInvestigationCollectionType ) cd.generateRandomXML(
                investigationCollectionXML, ci );
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMBioInvestigationFactorType factorXML = new FugeOMBioInvestigationFactorType();

            factorXML = ( FugeOMBioInvestigationFactorType ) ci.generateRandomXML( factorXML );

            // set the non-identifiable traits

            if ( fuGEType.getOntologyCollection() != null ) {
                FugeOMBioInvestigationFactorType.FactorCategory categoryValueXML = new FugeOMBioInvestigationFactorType.FactorCategory();
                categoryValueXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                factorXML.setFactorCategory( categoryValueXML );
            }

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FugeOMBioInvestigationFactorValueType factorValueXML = new FugeOMBioInvestigationFactorValueType();
                factorValueXML = ( FugeOMBioInvestigationFactorValueType ) cd.generateRandomXML( factorValueXML, ci );
                if ( fuGEType.getOntologyCollection() != null ) {
                    FugeOMBioInvestigationFactorValueType.Value valueXML = new FugeOMBioInvestigationFactorValueType.Value();
                    valueXML.setOntologyTermRef(
                            fuGEType.getOntologyCollection().getOntologyTerm().get( ii ).getValue().getIdentifier() );
                    factorValueXML.setValue( valueXML );
                }

// todo still not sure where datapartitions fit in, so won't make them for now.
//                for ( int iii = 0; iii < NUMBER_ELEMENTS; iii++ ) {
//                    FugeOMBioInvestigationFactorValueType.DataPartitions dataPartitionXML = new FugeOMBioInvestigationFactorValueType.DataPartitions();
//                    if ( fuGEType.getDataCollection() != null ) {
//                        dataPartitionXML.setDataPartitionRef( fuGEType.getDataCollection().getData().get( iii ).getValue().getIdentifier() );
//                        // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
//                        factorValueXML.getDataPartitions().add( dataPartitionXML );
//                    }
//                }
                factorXML.getFactorValue().add( factorValueXML );
            }
            investigationCollectionXML.getFactor().add( factorXML );
        }
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMBioInvestigationInvestigationType investigationXML = new FugeOMBioInvestigationInvestigationType();

            investigationXML = ( FugeOMBioInvestigationInvestigationType ) ci.generateRandomXML( investigationXML );
            investigationCollectionXML.getInvestigation().add( investigationXML );
        }

        fuGEType.setInvestigationCollection( investigationCollectionXML );
        return fuGEType;
    }

    // Get the latest version of any identifiable object(s) in this Collection
    // todo investigation incomplete (only has identifiable elements)
    public InvestigationCollection getLatestVersion(
            InvestigationCollection investigationCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        investigationCollection = ( InvestigationCollection ) cd.getLatestVersion( investigationCollection, ci );

        // load all the latest versions into the new set.
        Set<Factor> set = new HashSet<Factor>();
        for ( Object obj : investigationCollection.getFactorCollection() ) {
            Factor factor = ( Factor ) obj;
            factor = ( Factor ) reService.findLatestByEndurant( factor.getEndurant().getIdentifier() );
            factor = ( Factor ) ci.getLatestVersion( factor );

            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    factor.getFactorCategory().getEndurant().getIdentifier() );
            factor.setFactorCategory( ( OntologyTerm ) ci.getLatestVersion( ot ) );

            Set<FactorValue> fvs = new HashSet<FactorValue>();
            for ( Object obj2 : factor.getFactorValues() ) {
                FactorValue factorValue = ( FactorValue ) obj2;
                factorValue = ( FactorValue ) cd.getLatestVersion( factorValue, ci );
                factorValue = ( FactorValue ) reService.greedyGet( factorValue );
                OntologyTerm ot2 = ( OntologyTerm ) reService.findLatestByEndurant(
                        factorValue.getValue().getEndurant().getIdentifier() );
                factorValue.setValue( ( OntologyTerm ) ci.getLatestVersion( ot2 ) );

                CisbanDataHelper cdat = new CisbanDataHelper( reService, ci );
                Set<DataPartition> dps = new HashSet<DataPartition>();
                for ( Object obj3 : factorValue.getDataPartitions() ) {
                    DataPartition dataPartition = ( DataPartition ) obj3;
                    Data data = ( Data ) reService.findLatestByEndurant(
                            dataPartition.getPartitionedData().getEndurant().getIdentifier() );
                    dataPartition.setPartitionedData( cdat.getLatestVersion( data ) );
                    // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
                    dps.add( dataPartition );
                }
                factorValue.setDataPartitions( dps );
                fvs.add( factorValue );
            }
            factor.setFactorValues( fvs );
            set.add( factor );
        }
        investigationCollection.setFactorCollection( set );

        Set<Investigation> investigations = new HashSet<Investigation>();
        for ( Object obj : investigationCollection.getFactorCollection() ) {
            Investigation investigation = ( Investigation ) obj;
            investigation = ( Investigation ) reService.findLatestByEndurant( investigation.getEndurant().getIdentifier() );
            investigation = ( Investigation ) ci.getLatestVersion( investigation );
            investigations.add( investigation );
        }
        investigationCollection.setInvestigations( investigations );

        return investigationCollection;
    }
}
