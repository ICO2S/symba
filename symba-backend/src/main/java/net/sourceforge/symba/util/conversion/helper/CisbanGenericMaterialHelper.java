package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Bio.Material.GenericMaterial;
import fugeOM.Bio.Material.Material;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMBioMaterialGenericMaterialType;

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

public class CisbanGenericMaterialHelper implements MappingHelper<GenericMaterial, FugeOMBioMaterialGenericMaterialType> {

    // @todo assumes all Collections (ReferenceableCollection, AuditCollection etc) are already extant in the database
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericMaterial unmarshal( FugeOMBioMaterialGenericMaterialType genericMaterialXML,
                                      GenericMaterial genericMaterial ) throws RealizableEntityServiceException {

        // set only GenericMaterial-specific traits

        if ( !genericMaterialXML.getComponents().isEmpty() ) {
            // Components. These elements are references to GenericMaterial.
            Set<GenericMaterial> components = new HashSet<GenericMaterial>();
            for ( FugeOMBioMaterialGenericMaterialType.Components componentXML : genericMaterialXML.getComponents() ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                components.add( ( GenericMaterial ) reService.findIdentifiable( componentXML.getGenericMaterialRef() ) );
            }

            genericMaterial.setComponents( components );
        }

        return genericMaterial;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMBioMaterialGenericMaterialType marshal(
            FugeOMBioMaterialGenericMaterialType genericMaterialXML, GenericMaterial genericMaterial )
             {

        // set only GenericMaterial-specific traits

        // Components. These elements are references to GenericMaterial.
        for ( Object componentObj : genericMaterial.getComponents() ) {
            GenericMaterial component = ( GenericMaterial ) componentObj;

            FugeOMBioMaterialGenericMaterialType.Components componentsXML = new FugeOMBioMaterialGenericMaterialType.Components();
            componentsXML.setGenericMaterialRef( component.getIdentifier() );
            genericMaterialXML.getComponents().add( componentsXML );
        }

        return genericMaterialXML;
    }

    // currently does nothing in this section, as the difference between GenericMaterial and Material is all in the components 
    public FugeOMBioMaterialGenericMaterialType generateRandomXML( FugeOMBioMaterialGenericMaterialType genericMaterialXML ) {

        return genericMaterialXML;
    }

    public FugeOMBioMaterialGenericMaterialType generateRandomXMLWithComponents(
            FugeOMBioMaterialGenericMaterialType genericMaterialXML,
            FugeOMBioMaterialGenericMaterialType componentXML ) {

        genericMaterialXML = generateRandomXML( genericMaterialXML );

        // Components. These elements are references to GenericMaterial. Only generate one reference.
        if ( componentXML != null ) {
            FugeOMBioMaterialGenericMaterialType.Components componentsXML = new FugeOMBioMaterialGenericMaterialType.Components();
            componentsXML.setGenericMaterialRef( componentXML.getIdentifier() );
            genericMaterialXML.getComponents().add( componentsXML );
        }
        return genericMaterialXML;
    }

    public GenericMaterial getLatestVersion( GenericMaterial genericMaterial ) throws RealizableEntityServiceException {
        CisbanMaterialHelper cm = new CisbanMaterialHelper();

        // prepare updated set
        Set<GenericMaterial> set = new HashSet<GenericMaterial>();

        // load all the latest versions into the new set.
        for ( Object obj : genericMaterial.getComponents() ) {
            set.add( ( GenericMaterial ) cm.getLatestVersion( ( Material ) obj ) );
        }
        genericMaterial.setComponents( set );

        return genericMaterial;
    }

}
