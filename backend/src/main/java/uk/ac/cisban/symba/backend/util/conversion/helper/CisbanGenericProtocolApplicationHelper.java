package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Bio.Data.Data;
import fugeOM.Bio.Material.Material;
import fugeOM.Common.Protocol.GenericProtocolApplication;
import fugeOM.Common.Protocol.Protocol;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericProtocolApplicationType;

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

public class CisbanGenericProtocolApplicationHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final RealizableEntityService reService;

    public CisbanGenericProtocolApplicationHelper( RealizableEntityService reService ) {
        this.reService = reService;
    }

    // @todo GenMatMeas has not been implemented, and there seems to be a problem somewhere in uml OR xsd such that the inputmaterials aren't properly working
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericProtocolApplication unmarshalGenericProtocolApplication(
            FugeOMCommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML,
            GenericProtocolApplication genericProtocolApplication ) throws RealizableEntityServiceException {

        // set any GenericProtocolApplication-specific traits

        // Set the object to exactly the object is that is associated
        // with this version group.
        genericProtocolApplication.setGenericProtocol(
                ( Protocol ) reService.findIdentifiable(
                        genericProtocolApplicationXML.getProtocolRef() ) );

        // input data
        Set<Data> datas = new HashSet<Data>();
        for ( FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputData gidXML : genericProtocolApplicationXML
                .getGenericInputData() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            datas.add( ( Data ) reService.findIdentifiable( gidXML.getDataRef() ) );
        }
        genericProtocolApplication.setGenericInputData( datas );

        // output data
        Set<Data> outdatas = new HashSet<Data>();
        for ( FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputData godXML : genericProtocolApplicationXML
                .getGenericOutputData() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            outdatas.add( ( Data ) reService.findIdentifiable( godXML.getDataRef() ) );
        }
        genericProtocolApplication.setGenericOutputData( outdatas );

        // input material
        Set<Material> icmaterials = new HashSet<Material>();
        for ( FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputCompleteMaterials gicmXML : genericProtocolApplicationXML
                .getGenericInputCompleteMaterials() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            icmaterials.add( ( Material ) reService.findIdentifiable( gicmXML.getMaterialRef() ) );
        }
        genericProtocolApplication.setGenericInputCompleteMaterials( icmaterials );

        // output material
        Set<Material> materials = new HashSet<Material>();
        for ( FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputMaterials gomXML : genericProtocolApplicationXML
                .getGenericOutputMaterials() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            materials.add( ( Material ) reService.findIdentifiable( gomXML.getMaterialRef() ) );
        }
        genericProtocolApplication.setGenericOutputMaterials( materials );

        return genericProtocolApplication;
    }

    // @todo GenMatMeas has not been implemented
    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericProtocolApplicationType marshalGenericProtocolApplication(
            FugeOMCommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML,
            GenericProtocolApplication genericProtocolApplication ) throws RealizableEntityServiceException {

        if ( genericProtocolApplication.getGenericOutputMaterials() != null &&
                !genericProtocolApplication.getGenericOutputMaterials().isEmpty() ) {
            System.out.println( "Found Output Materials directly before greedy get for GPA " + genericProtocolApplication.getIdentifier());
        } else {
            System.out.println( "Found No Output Materials directly before greedy get for GPA " + genericProtocolApplication.getIdentifier() );
        }

        // get any lazily loaded objects
        genericProtocolApplication = ( GenericProtocolApplication ) reService.greedyGet( genericProtocolApplication );

        // protocol ref
        genericProtocolApplicationXML.setProtocolRef( genericProtocolApplication.getGenericProtocol().getIdentifier() );

        // input data
        for ( Object obj : genericProtocolApplication.getGenericInputData() ) {
            Data data = ( Data ) obj;
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputData godXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputData();
            godXML.setDataRef( data.getIdentifier() );
            genericProtocolApplicationXML.getGenericInputData().add( godXML );
        }

        // output data
        for ( Object obj : genericProtocolApplication.getGenericOutputData() ) {
            Data data = ( Data ) obj;
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputData godXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputData();
            godXML.setDataRef( data.getIdentifier() );
            genericProtocolApplicationXML.getGenericOutputData().add( godXML );
        }

        if ( genericProtocolApplication.getGenericOutputMaterials() != null &&
                !genericProtocolApplication.getGenericOutputMaterials().isEmpty() ) {
            System.out.println( "Found Output Materials directly before marshaling " + genericProtocolApplication.getIdentifier() );
        } else {
            System.out.println( "Found No Output Materials directly before marshaling " + genericProtocolApplication.getIdentifier() );

        }

        // input complete material
        for ( Object obj : genericProtocolApplication.getGenericInputCompleteMaterials() ) {
            Material material = ( Material ) obj;
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputCompleteMaterials gicmXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputCompleteMaterials();
            gicmXML.setMaterialRef( material.getIdentifier() );
            genericProtocolApplicationXML.getGenericInputCompleteMaterials().add( gicmXML );
        }
        if ( genericProtocolApplication.getGenericOutputMaterials() != null &&
                !genericProtocolApplication.getGenericOutputMaterials().isEmpty() ) {
            System.out.println( "Found Output Materials directly before marshaling outputmaterials " + genericProtocolApplication.getIdentifier() );
        } else {
            System.out.println( "Found No Output Materials directly before marshaling outputmaterials " + genericProtocolApplication.getIdentifier() );

        }

        // output material
        for ( Object obj : genericProtocolApplication.getGenericOutputMaterials() ) {
            Material material = ( Material ) obj;
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputMaterials godXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputMaterials();
            godXML.setMaterialRef( material.getIdentifier() );
            genericProtocolApplicationXML.getGenericOutputMaterials().add( godXML );
        }

        if ( genericProtocolApplication.getGenericOutputMaterials() != null &&
                !genericProtocolApplication.getGenericOutputMaterials().isEmpty() ) {
            System.out.println( "Found Output Materials directly after marshaling" );
        } else {
            System.out.println( "Found No Output Materials directly after marshaling" );

        }

        return genericProtocolApplicationXML;
    }

    public FugeOMCommonProtocolGenericProtocolApplicationType generateRandomXML(
            FugeOMCommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML,
            int ordinal,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        if ( !protocolCollectionXML.getProtocol().isEmpty() ) {
            genericProtocolApplicationXML.setProtocolRef(
                    protocolCollectionXML.getProtocol().get( ordinal ).getValue().getIdentifier() );
        }

        int output = ordinal + 1;
        if ( ordinal == NUMBER_ELEMENTS - 1 )
            output = 0;

        if ( frXML.getDataCollection() != null ) {
            // input data
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputData gidXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericInputData();
            gidXML.setDataRef( frXML.getDataCollection().getData().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getGenericInputData().add( gidXML );

            // output data
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputData godXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputData();
            godXML.setDataRef( frXML.getDataCollection().getData().get( output ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getGenericOutputData().add( godXML );
        }

        if ( frXML.getMaterialCollection() != null ) {
            // output material
            FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputMaterials gomXML = new FugeOMCommonProtocolGenericProtocolApplicationType.GenericOutputMaterials();
            gomXML.setMaterialRef(
                    frXML.getMaterialCollection().getMaterial().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getGenericOutputMaterials().add( gomXML );
        }

        return genericProtocolApplicationXML;
    }

    public GenericProtocolApplication getLatestVersion(
            GenericProtocolApplication genericProtocolApplication,
            CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        genericProtocolApplication = ( GenericProtocolApplication ) reService.greedyGet( genericProtocolApplication );

//        if ( genericProtocolApplication.getGenericOutputMaterials() != null &&
//                !genericProtocolApplication.getGenericOutputMaterials().isEmpty() ) {
//            System.out.println( "Found Output Materials directly before get latest version for GPA" );
//        } else {
//            System.out.println( "Found No Output Materials directly before get latest version for GPA" );
//
//        }

        CisbanProtocolHelper cpr = new CisbanProtocolHelper( reService, ci );
        CisbanDataHelper cdat = new CisbanDataHelper( reService, ci );
        CisbanMaterialHelper cm = new CisbanMaterialHelper( reService, ci );

        genericProtocolApplication.setGenericProtocol( cpr.getLatestVersion( genericProtocolApplication.getGenericProtocol() ) );

        // prepare updated set
        Set<Data> set = new HashSet<Data>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocolApplication.getGenericInputData() ) {
            set.add( cdat.getLatestVersion( ( Data ) obj ) );
        }
        genericProtocolApplication.setGenericInputData( set );

        // prepare updated set
        Set<Data> setOD = new HashSet<Data>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocolApplication.getGenericOutputData() ) {
            setOD.add( cdat.getLatestVersion( ( Data ) obj ) );
        }
        genericProtocolApplication.setGenericOutputData( setOD );

        // prepare updated set
        Set<Material> set1 = new HashSet<Material>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocolApplication.getGenericOutputMaterials() ) {
            set1.add( cm.getLatestVersion( ( Material ) obj ) );
        }
        genericProtocolApplication.setGenericOutputMaterials( set1 );

        // prepare updated set
        Set<Material> setGICM = new HashSet<Material>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocolApplication.getGenericInputCompleteMaterials() ) {
            setGICM.add( cm.getLatestVersion( ( Material ) obj ) );
        }
        genericProtocolApplication.setGenericInputCompleteMaterials( setGICM );

        // todo GenericInputMaterials, which takes GenericMatMeas's

        return genericProtocolApplication;
    }
}
