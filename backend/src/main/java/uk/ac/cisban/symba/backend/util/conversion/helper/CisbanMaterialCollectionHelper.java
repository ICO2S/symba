package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Bio.Material.GenericMaterial;
import fugeOM.Bio.Material.Material;
import fugeOM.Collection.MaterialCollection;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;
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

public class CisbanMaterialCollectionHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanMaterialHelper cm;
    private final RealizableEntityService reService;

    public CisbanMaterialCollectionHelper( RealizableEntityService reService,
                                           CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cd = ci.getCisbanDescribableHelper();
        this.cm = new CisbanMaterialHelper( reService, ci );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public MaterialCollection unmarshalMaterialCollection(
            FugeOMCollectionMaterialCollectionType matCollXML ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // create the fuge material collection object
        MaterialCollection matColl = ( MaterialCollection ) reService.createDescribableOb(
                "fugeOM.Collection.MaterialCollection" );

        // set describable information
        matColl = ( MaterialCollection ) cd.unmarshalDescribable( matCollXML, matColl );

        matColl = unmarshalCollectionContents( matCollXML, matColl );

        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.MaterialCollection", matColl );

        return matColl;
    }

    public MaterialCollection unmarshalCollectionContents( FugeOMCollectionMaterialCollectionType matCollXML,
                                                           MaterialCollection matColl ) throws LSIDException, RealizableEntityServiceException, URISyntaxException {
        // Create collection of material for addition to the material collection
        Set<Material> materials = new HashSet<Material>();

        // set up the converter

        for ( JAXBElement<? extends FugeOMBioMaterialMaterialType> materialElementXML : matCollXML.getMaterial() ) {
            FugeOMBioMaterialMaterialType materialXML = materialElementXML.getValue();
            // set fuge object
            materials.add( cm.unmarshalMaterial( materialXML ) );
        }

        // Add the set of generic materials to the material collection
        matColl.setMaterials( materials );

        return matColl;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionMaterialCollectionType marshalMaterialCollection(
            MaterialCollection matColl ) throws URISyntaxException {

        // create the jaxb material collection object
        FugeOMCollectionMaterialCollectionType matCollXML = new FugeOMCollectionMaterialCollectionType();

        // set describable information
        matCollXML = ( FugeOMCollectionMaterialCollectionType ) cd.marshalDescribable( matCollXML, matColl );

        // set up the converter and the factory
        ObjectFactory factory = new ObjectFactory();

        for ( Object materialObj : matColl.getMaterials() ) {
            Material material = ( Material ) materialObj;
//            System.out.println(
//                    "Number of characteristics before marshaling of material: " +
//                            material.getCharacteristics().size() );

            if ( material instanceof GenericMaterial ) {
                // set jaxb object
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FugeOMBioMaterialGenericMaterialType ) cm.marshalMaterial( material ) ) );
            }
        }
        return matCollXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType frXML ) {
        // create the jaxb material collection object
        FugeOMCollectionMaterialCollectionType matCollXML = new FugeOMCollectionMaterialCollectionType();

        // set describable information
        matCollXML = ( FugeOMCollectionMaterialCollectionType ) cd.generateRandomXML( matCollXML, ci );

        // set up the converter and the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // set jaxb object
            if ( i > 0 ) {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FugeOMBioMaterialGenericMaterialType ) cm.generateRandomXML(
                                        frXML, ( FugeOMBioMaterialGenericMaterialType ) matCollXML.getMaterial()
                                        .get( 0 )
                                        .getValue() ) ) );
            } else {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FugeOMBioMaterialGenericMaterialType ) cm.generateRandomXML(
                                        frXML, null ) ) );
            }
        }

        frXML.setMaterialCollection( matCollXML );
        return frXML;
    }

    public MaterialCollection getLatestVersion(
            MaterialCollection materialCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        materialCollection = ( MaterialCollection ) cd.getLatestVersion( materialCollection, ci );

        // prepare updated set
        Set<Material> set = new HashSet<Material>();

        // load all the latest versions into the new set.
        for ( Object obj : materialCollection.getMaterials() ) {
            Material m = cm.getLatestVersion( ( Material ) obj );
            set.add( m );
        }
        materialCollection.setMaterials( set );

        return materialCollection;
    }

}
