package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Material.GenericMaterial;
import fugeOM.Bio.Material.Material;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioMaterialGenericMaterialType;
import fugeOM.util.generatedJAXB2.FugeOMBioMaterialMaterialType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditContactRoleType;
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
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */

public class CisbanMaterialHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanGenericMaterialHelper cgm;
    private final CisbanContactRoleHelper ccr;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanMaterialHelper( RealizableEntityService reService,
                                 CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cgm = new CisbanGenericMaterialHelper( reService );
        this.ccr = new CisbanContactRoleHelper( reService, ci.getCisbanDescribableHelper() );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Material unmarshalMaterial( FugeOMBioMaterialMaterialType materialXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // determine what sort of material it is
        if ( materialXML instanceof FugeOMBioMaterialGenericMaterialType ) {

            // Retrieve latest version from the database.
            GenericMaterial gmaterial = ( GenericMaterial ) helper.getOrCreateLatest(
                    materialXML.getEndurant(),
                    "fugeOM.Bio.Material.GenericMaterialEndurant",
                    materialXML.getName(),
                    "fugeOM.Bio.Material.GenericMaterial",
                    System.err );

//            System.err.println( "gmaterial: " + gmaterial.getIdentifier() );

            // set the material attributes.
            gmaterial = ( GenericMaterial ) unmarshalMaterialSpecific( materialXML, gmaterial );

            // set the generic material attributes
            gmaterial = cgm.unmarshalGenericMaterial( ( FugeOMBioMaterialGenericMaterialType ) materialXML, gmaterial );

            if ( gmaterial.getId() != null ) {
                helper.assignAndLoadIdentifiable( gmaterial, "fugeOM.Bio.Material.GenericMaterial", System.err );
            } else {
                helper.loadIdentifiable( gmaterial, "fugeOM.Bio.Material.GenericMaterial", System.err );
            }
            return gmaterial;
        }
        return null; // shouldn't get here as there is currently only one type of Material allowed.
    }

    private Material unmarshalMaterialSpecific( FugeOMBioMaterialMaterialType materialXML,
                                                Material material ) throws RealizableEntityServiceException, URISyntaxException {

        material = ( Material ) ci.unmarshalIdentifiable( materialXML, material );

        // contacts
        Set<ContactRole> contactRoles = new HashSet<ContactRole>();
        for ( FugeOMCommonAuditContactRoleType contactRoleXML : materialXML.getContactRole() ) {
            contactRoles.add( ccr.unmarshalContactRole( contactRoleXML ) );
        }
        material.setContacts( contactRoles );

        // material type
        if ( materialXML.getMaterialType() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            material.setMaterialType( ( OntologyTerm ) reService.findIdentifiable( materialXML.getMaterialType().getOntologyTermRef() ) );
        }

        // characteristics
        Set<OntologyTerm> characteristics = new HashSet<OntologyTerm>();
        for ( FugeOMBioMaterialMaterialType.Characteristics characteristicXML : materialXML.getCharacteristics() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            characteristics.add( ( OntologyTerm ) reService.findIdentifiable( characteristicXML.getOntologyTermRef() ) );
        }
        material.setCharacteristics( characteristics );

        // quality control statistics
        Set<OntologyTerm> qcs = new HashSet<OntologyTerm>();
        for ( fugeOM.util.generatedJAXB2.FugeOMBioMaterialMaterialType.QualityControlStatistics qcXML : materialXML.getQualityControlStatistics() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            qcs.add( ( OntologyTerm ) reService.findIdentifiable( qcXML.getOntologyTermRef() ) );
        }
        material.setQualityControlStatistics( qcs );

        return material;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMBioMaterialMaterialType marshalMaterial( Material material )
            throws URISyntaxException {

        // determine what sort of material it is
        if ( material instanceof GenericMaterial ) {

            // create fuge object
            FugeOMBioMaterialGenericMaterialType genericMaterialXML = new FugeOMBioMaterialGenericMaterialType();

            // set the material attributes
            genericMaterialXML = ( FugeOMBioMaterialGenericMaterialType ) marshalMaterialSpecific(
                    genericMaterialXML, material );

            // set the generic material attributes
            genericMaterialXML = cgm.marshalGenericMaterial( genericMaterialXML, ( GenericMaterial ) material );

            return genericMaterialXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Material allowed.
    }

    public FugeOMBioMaterialMaterialType generateRandomXML( FugeOMCollectionFuGEType frXML,
                                                            FugeOMBioMaterialGenericMaterialType genXML ) {

        // create fuge object
        FugeOMBioMaterialGenericMaterialType genericMaterialXML = new FugeOMBioMaterialGenericMaterialType();

        // set the material attributes
        genericMaterialXML = ( FugeOMBioMaterialGenericMaterialType ) generateRandomSpecificXML(
                genericMaterialXML, frXML );

        // set the generic material attributes
        genericMaterialXML = cgm.generateRandomXML( genericMaterialXML, genXML );

        return genericMaterialXML;
    }

    // This should be run at a time where the ontology collection and audit collection have already been run.
    private FugeOMBioMaterialMaterialType generateRandomSpecificXML( FugeOMBioMaterialMaterialType materialXML,
                                                                     FugeOMCollectionFuGEType frXML ) {
        materialXML = ( FugeOMBioMaterialMaterialType ) ci.generateRandomXML( materialXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            materialXML.getContactRole().add( ccr.generateRandomXML( frXML, ci ) );
        }

        if ( frXML.getOntologyCollection() != null ) {
            FugeOMBioMaterialMaterialType.MaterialType materialTypeXML = new FugeOMBioMaterialMaterialType.MaterialType();
            materialTypeXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            materialXML.setMaterialType( materialTypeXML );

            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMBioMaterialMaterialType.Characteristics characteristicXML = new FugeOMBioMaterialMaterialType.Characteristics();
                characteristicXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getCharacteristics().add( characteristicXML );
            }

            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMBioMaterialMaterialType.QualityControlStatistics qcsXML = new FugeOMBioMaterialMaterialType.QualityControlStatistics();
                qcsXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getQualityControlStatistics().add( qcsXML );
            }
        }
        return materialXML;
    }

    private FugeOMBioMaterialMaterialType marshalMaterialSpecific( FugeOMBioMaterialMaterialType materialXML,
                                                                   Material material ) throws URISyntaxException {

        materialXML = ( FugeOMBioMaterialMaterialType ) ci.marshalIdentifiable( materialXML, material );

        for ( Object contactRoleObj : material.getContacts() ) {
            materialXML.getContactRole().add( ccr.marshalContactRole( ( ContactRole ) contactRoleObj ) );
        }

        if ( material.getMaterialType() != null ) {
            FugeOMBioMaterialMaterialType.MaterialType materialTypeXML = new FugeOMBioMaterialMaterialType.MaterialType();
            materialTypeXML.setOntologyTermRef( material.getMaterialType().getIdentifier() );
            materialXML.setMaterialType( materialTypeXML );
        }

//        System.out.println( "Number of characteristics: " + material.getCharacteristics().size() );
        for ( Object characteristicObj : material.getCharacteristics() ) {
            OntologyTerm characteristic = ( OntologyTerm ) characteristicObj;

            FugeOMBioMaterialMaterialType.Characteristics characteristicXML = new FugeOMBioMaterialMaterialType.Characteristics();
            characteristicXML.setOntologyTermRef( characteristic.getIdentifier() );
            materialXML.getCharacteristics().add( characteristicXML );
        }

        for ( Object qcsObj : material.getQualityControlStatistics() ) {
            OntologyTerm qcs = ( OntologyTerm ) qcsObj;

            FugeOMBioMaterialMaterialType.QualityControlStatistics qcsXML = new FugeOMBioMaterialMaterialType.QualityControlStatistics();
            qcsXML.setOntologyTermRef( qcs.getIdentifier() );
            materialXML.getQualityControlStatistics().add( qcsXML );
        }
        return materialXML;
    }

    public Material getLatestVersion( Material material ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        material = ( Material ) reService.findLatestByEndurant( material.getEndurant().getIdentifier() );
        material = ( Material ) ci.getLatestVersion( material );

        // prepare updated set
        Set<ContactRole> set = new HashSet<ContactRole>();

        // load all the latest versions into the new set.
        for ( Object obj : material.getContacts() ) {
            set.add( ccr.getLatestVersion( ( ContactRole ) obj, ci ) );
        }
        material.setContacts( set );

        if ( material.getMaterialType() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    material.getMaterialType().getEndurant().getIdentifier() );
            material.setMaterialType( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        // prepare updated set
        Set<OntologyTerm> set2 = new HashSet<OntologyTerm>();

        // load all the latest versions into the new set.
        for ( Object obj : material.getCharacteristics() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set2.add( ( OntologyTerm ) ci.getLatestVersion( identifiable ) );
        }
        material.setCharacteristics( set2 );

        // todo ARGH - ask Matt
        Set<OntologyTerm> set3 = new HashSet<OntologyTerm>();

        // load all the latest versions into the new set.
        for ( Object obj : material.getQualityControlStatistics() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set3.add( ( OntologyTerm ) ci.getLatestVersion( identifiable ) );
        }
        material.setQualityControlStatistics( set3 );

        // update the generic material identifiables, if present
        if ( material instanceof GenericMaterial ) {
            material = cgm.getLatestVersion( ( GenericMaterial ) material, ci );
        }

        return material;
    }
}
