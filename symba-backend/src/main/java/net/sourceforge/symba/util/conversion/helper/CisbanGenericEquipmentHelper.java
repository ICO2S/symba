package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericEquipmentType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericParameterType;
import net.sourceforge.symba.util.CisbanHelper;

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

public class CisbanGenericEquipmentHelper implements MappingHelper<GenericEquipment, FugeOMCommonProtocolGenericEquipmentType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanParameterHelper cp;
    private final CisbanSoftwareHelper cs;
    private final CisbanHelper helper;

    public CisbanGenericEquipmentHelper() {
        this.cp = new CisbanParameterHelper();
        this.cs = new CisbanSoftwareHelper();
        this.helper = CisbanHelper.create( reService );

    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericEquipment unmarshal( FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
                                       GenericEquipment genericEquipment ) throws RealizableEntityServiceException {

        try {
            // set any GenericEquipment-specific traits
            Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
            for ( FugeOMCommonProtocolGenericParameterType genericParameterXML : genericEquipmentXML.getGenericParameter() ) {
                genericParameters.add( ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) helper.getOrCreateLatest(
                        genericParameterXML.getEndurant(),
                        "fugeOM.Common.Protocol.GenParamEndurant",
                        genericParameterXML.getName(),
                        "fugeOM.Common.Protocol.GenericParameter",
                        System.err ) ) );
            }
            if ( !genericParameters.isEmpty() )
                genericEquipment.setParameters( genericParameters );

            Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
            for ( FugeOMCommonProtocolGenericEquipmentType.GenericEquipmentParts referencedXML : genericEquipmentXML.getGenericEquipmentParts() ) {
                genericEquipments.add( ( GenericEquipment ) reService.findIdentifiable( referencedXML.getGenericEquipmentRef() ) );
            }
            if ( !genericEquipments.isEmpty() )
                genericEquipment.setGenEquipParts( genericEquipments );

            Set<GenericSoftware> genericSoftwares = new HashSet<GenericSoftware>();
            for ( FugeOMCommonProtocolGenericEquipmentType.Software referencedXML : genericEquipmentXML.getSoftware() ) {
                genericSoftwares.add( ( GenericSoftware ) reService.findIdentifiable( referencedXML.getGenericSoftwareRef() ) );
            }
            if ( !genericSoftwares.isEmpty() )
                genericEquipment.setSoftware( genericSoftwares );
        } catch ( RealizableEntityServiceException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error making FuGE POJO objects for Generic Equipment" );
        }


        return genericEquipment;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericEquipmentType marshal(
            FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
            GenericEquipment genericEquipment ) throws RealizableEntityServiceException {

        try {
            // get any lazily loaded objects
            genericEquipment = ( GenericEquipment ) reService.greedyGet( genericEquipment );
        } catch ( RealizableEntityServiceException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error making FuGE POJO objects for Generic Equipment" );
        }

        // set any GenericEquipment-specific traits

        for ( Object obj : genericEquipment.getParameters() ) {
            GenericParameter parameter = ( GenericParameter ) obj;
            // set fuge object
            genericEquipmentXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.marshal( new FugeOMCommonProtocolGenericParameterType(), parameter ) );
        }
        for ( Object obj : genericEquipment.getGenEquipParts() ) {
            GenericEquipment equipment = ( GenericEquipment ) obj;
            FugeOMCommonProtocolGenericEquipmentType.GenericEquipmentParts parts = new FugeOMCommonProtocolGenericEquipmentType.GenericEquipmentParts();
            parts.setGenericEquipmentRef( equipment.getIdentifier() );
            genericEquipmentXML.getGenericEquipmentParts().add( parts );
        }
        for ( Object obj : genericEquipment.getSoftware() ) {
            GenericSoftware software = ( GenericSoftware ) obj;
            FugeOMCommonProtocolGenericEquipmentType.Software softwareXML = new FugeOMCommonProtocolGenericEquipmentType.Software();
            softwareXML.setGenericSoftwareRef( software.getIdentifier() );
            genericEquipmentXML.getSoftware().add( softwareXML );
        }

        return genericEquipmentXML;
    }

    // there is currently no part of generating generic equipment that does not involve a fuge object, therefore
    // ensure you use generateRandomXMLWithLinksOut
    public FugeOMCommonProtocolGenericEquipmentType generateRandomXML( FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML ) {
        return genericEquipmentXML;
    }

    public FugeOMCommonProtocolGenericEquipmentType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
            FugeOMCommonProtocolGenericEquipmentType partXML,
            FugeOMCollectionFuGEType frXML ) {

        genericEquipmentXML = generateRandomXML( genericEquipmentXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonProtocolGenericParameterType parameterXML = new FugeOMCommonProtocolGenericParameterType();
            genericEquipmentXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.generateRandomXMLWithLinksOut( parameterXML, frXML ) );
        }

        if ( partXML != null ) {
            // parts list of one
            FugeOMCommonProtocolGenericEquipmentType.GenericEquipmentParts parts = new FugeOMCommonProtocolGenericEquipmentType.GenericEquipmentParts();
            parts.setGenericEquipmentRef( partXML.getIdentifier() );
            genericEquipmentXML.getGenericEquipmentParts().add( parts );
        }

        if ( frXML.getProtocolCollection() != null ) {
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolGenericEquipmentType.Software softwareXML = new FugeOMCommonProtocolGenericEquipmentType.Software();
                softwareXML.setGenericSoftwareRef(
                        frXML.getProtocolCollection().getSoftware().get( i ).getValue().getIdentifier() );
                genericEquipmentXML.getSoftware().add( softwareXML );
            }
        }
        return genericEquipmentXML;
    }

    public GenericEquipment getLatestVersion(
            GenericEquipment genericEquipment ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        genericEquipment = ( GenericEquipment ) reService.greedyGet( genericEquipment );

        // prepare updated set
        Set<GenericParameter> set = new HashSet<GenericParameter>();

        // load all the latest versions into the new set.
        for ( Object obj : genericEquipment.getEquipmentParameters() ) {
            set.add( ( GenericParameter ) cp.getLatestVersion( ( Parameter ) obj ) );
        }
        genericEquipment.setEquipmentParameters( set );

        // prepare updated set
        Set<GenericEquipment> set1 = new HashSet<GenericEquipment>();

        // load all the latest versions into the new set.
        for ( Object obj : genericEquipment.getGenEquipParts() ) {
            set1.add( getLatestVersion( ( GenericEquipment ) obj ) );
        }
        genericEquipment.setGenEquipParts( set1 );

        // prepare updated set
        Set<GenericSoftware> set2 = new HashSet<GenericSoftware>();

        // load all the latest versions into the new set.
        for ( Object obj : genericEquipment.getSoftware() ) {
            set2.add( ( GenericSoftware ) cs.getLatestVersion( ( Software ) obj ) );
        }
        genericEquipment.setSoftware( set2 );

        return genericEquipment;
    }
}
