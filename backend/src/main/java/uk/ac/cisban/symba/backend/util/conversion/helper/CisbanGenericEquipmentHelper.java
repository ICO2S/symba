package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericEquipmentType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericParameterType;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanGenericEquipmentHelper.java $
 *
 */

public class CisbanGenericEquipmentHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanParameterHelper cp;
    private final CisbanSoftwareHelper cs;
    private final RealizableEntityService reService;

    public CisbanGenericEquipmentHelper( RealizableEntityService reService, CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.cp = new CisbanParameterHelper( reService, ci );
        this.cs = new CisbanSoftwareHelper( reService, ci );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericEquipment unmarshalGenericEquipment( FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
                                                       GenericEquipment genericEquipment ) throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // set any GenericEquipment-specific traits
        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        for ( FugeOMCommonProtocolGenericParameterType genericParameterXML : genericEquipmentXML.getGenericParameter() ) {
            genericParameters.add( ( GenericParameter ) cp.unmarshalParameter( genericParameterXML ) );
        }
        if ( !genericParameters.isEmpty() )
            genericEquipment.setEquipmentParameters( genericParameters );

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

        return genericEquipment;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericEquipmentType marshalGenericEquipment(
            FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
            GenericEquipment genericEquipment ) throws URISyntaxException, RealizableEntityServiceException {

        // get any lazily loaded objects
        genericEquipment = ( GenericEquipment ) reService.greedyGet( genericEquipment );

        // set any GenericEquipment-specific traits

        for ( Object obj : genericEquipment.getEquipmentParameters() ) {
            GenericParameter parameter = ( GenericParameter ) obj;
            // set fuge object
            genericEquipmentXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.marshalParameter( parameter ) );
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

    public FugeOMCommonProtocolGenericEquipmentType generateRandomXML(
            FugeOMCommonProtocolGenericEquipmentType genericEquipmentXML,
            FugeOMCommonProtocolGenericEquipmentType partXML,
            FugeOMCollectionFuGEType frXML ) {

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonProtocolGenericParameterType parameterXML = new FugeOMCommonProtocolGenericParameterType();
            genericEquipmentXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.generateRandomXML( parameterXML, frXML ) );
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
            GenericEquipment genericEquipment, boolean isLatestEquipment ) throws RealizableEntityServiceException {

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
            set1.add( getLatestVersion( ( GenericEquipment ) obj, false ) );
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
