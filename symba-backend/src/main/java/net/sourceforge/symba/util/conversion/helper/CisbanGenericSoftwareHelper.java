package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericParameterType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericSoftwareType;
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

public class CisbanGenericSoftwareHelper implements MappingHelper<GenericSoftware, FugeOMCommonProtocolGenericSoftwareType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanParameterHelper cp;
    private final CisbanIdentifiableHelper ci;
    private final CisbanHelper helper;

    public CisbanGenericSoftwareHelper() {
        this.cp = new CisbanParameterHelper();
        this.ci = new CisbanIdentifiableHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericSoftware unmarshal( FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML,
                                      GenericSoftware genericSoftware ) throws RealizableEntityServiceException {

        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();

        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        // set any GenericSoftware-specific traits
        for ( FugeOMCommonProtocolGenericParameterType genericParameterXML : genericSoftwareXML.getGenericParameter() ) {
            genericParameters.add( ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) helper.getOrCreateLatest(
                    genericParameterXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenParamEndurant",
                    genericParameterXML.getName(),
                    "fugeOM.Common.Protocol.GenericParameter",
                    System.err ) ) );
        }

        if ( !genericParameters.isEmpty() )
            genericSoftware.setSoftwareParameters( genericParameters );

        for ( FugeOMCommonProtocolGenericSoftwareType.GenEquipment referencedXML : genericSoftwareXML.getGenEquipment() ) {
            genericEquipments.add( ( GenericEquipment ) reService.findIdentifiable( referencedXML.getGenericEquipmentRef() ) );
        }
        if ( !genericEquipments.isEmpty() )
            genericSoftware.setGenEquipment( genericEquipments );

        return genericSoftware;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericSoftwareType marshal(
            FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML,
            GenericSoftware genericSoftware ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        genericSoftware = ( GenericSoftware ) reService.greedyGet( genericSoftware );

        // set any GenericSoftware-specific traits

        // you can only have a GenericParameter here
        for ( Object obj : genericSoftware.getSoftwareParameters() ) {
            GenericParameter parameter = ( GenericParameter ) obj;
            // set fuge object
            genericSoftwareXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.marshal( new FugeOMCommonProtocolGenericParameterType(), parameter ) );
        }

        for ( Object obj : genericSoftware.getGenEquipment() ) {
            GenericEquipment equipment = ( GenericEquipment ) obj;
            FugeOMCommonProtocolGenericSoftwareType.GenEquipment parts = new FugeOMCommonProtocolGenericSoftwareType.GenEquipment();
            parts.setGenericEquipmentRef( equipment.getIdentifier() );
            genericSoftwareXML.getGenEquipment().add( parts );
        }
        return genericSoftwareXML;
    }

    // there is no part of generating random xml that does not include passing fuge and protocol objects.
    public FugeOMCommonProtocolGenericSoftwareType generateRandomXML( FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML ) {
        return genericSoftwareXML;
    }

    public FugeOMCommonProtocolGenericSoftwareType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {
        // todo - do I need to add a generate identifiable parts here?
        genericSoftwareXML = generateRandomXML( genericSoftwareXML );

        // you can only have a GenericParameter here
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericSoftwareXML.getGenericParameter().add(
                    ( FugeOMCommonProtocolGenericParameterType ) cp.generateRandomXMLWithLinksOut(
                            new FugeOMCommonProtocolGenericParameterType(), frXML ) );
        }

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonProtocolGenericSoftwareType.GenEquipment parts = new FugeOMCommonProtocolGenericSoftwareType.GenEquipment();
            if ( !protocolCollectionXML.getEquipment().isEmpty() && protocolCollectionXML.getEquipment().size() > i ) {
                parts.setGenericEquipmentRef(
                        protocolCollectionXML.getEquipment().get( i ).getValue().getIdentifier() );
                genericSoftwareXML.getGenEquipment().add( parts );
            }
        }
        return genericSoftwareXML;
    }

    public GenericSoftware getLatestVersion( GenericSoftware genericSoftware ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        genericSoftware = ( GenericSoftware ) reService.greedyGet( genericSoftware );

        CisbanEquipmentHelper ce = new CisbanEquipmentHelper();

        // prepare updated set
        Set<GenericParameter> set = new HashSet<GenericParameter>();

        // load all the latest versions into the new set.
        for ( Object obj : genericSoftware.getSoftwareParameters() ) {
            set.add( ( GenericParameter ) cp.getLatestVersion( ( Parameter ) obj ) );
        }
        genericSoftware.setSoftwareParameters( set );

        // prepare updated set
        Set<GenericEquipment> set1 = new HashSet<GenericEquipment>();

        // load all the latest versions into the new set.
        for ( Object obj : genericSoftware.getGenEquipment() ) {
            set1.add( ( GenericEquipment ) ce.getLatestVersion( ( Equipment ) obj ) );
        }
        genericSoftware.setGenEquipment( set1 );

        return genericSoftware;
    }
}
