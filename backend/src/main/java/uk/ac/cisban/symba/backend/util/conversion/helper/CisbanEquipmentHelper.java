package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.Equipment;
import fugeOM.Common.Protocol.GenericEquipment;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import javax.xml.bind.JAXBElement;
import java.net.URISyntaxException;

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

public class CisbanEquipmentHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanParameterizableHelper cparam;
    private final CisbanGenericEquipmentHelper cgeq;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanEquipmentHelper( RealizableEntityService reService, CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cparam = new CisbanParameterizableHelper( reService, ci.getCisbanDescribableHelper() );
        this.cgeq = new CisbanGenericEquipmentHelper( reService, ci );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Equipment unmarshalEquipment( FugeOMCommonProtocolEquipmentType equipmentXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // determine what sort of equipment it is
        if ( equipmentXML instanceof FugeOMCommonProtocolGenericEquipmentType ) {
            // create or retrieve fuge object

            // Retrieve latest version from the database.
            GenericEquipment genericEquipment = ( GenericEquipment ) helper.getOrCreateLatest(
                    equipmentXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenEquipEndurant",
                    equipmentXML.getName(),
                    "fugeOM.Common.Protocol.GenericEquipment",
                    System.err );

            // get generic equipment attributes - doing this first here runs the lazy loading before getting to equipment attributes
            genericEquipment = cgeq.unmarshalGenericEquipment(
                    ( FugeOMCommonProtocolGenericEquipmentType ) equipmentXML, genericEquipment );

            genericEquipment = ( GenericEquipment ) ci.unmarshalIdentifiable( equipmentXML, genericEquipment );

            // get equipment attributes
            genericEquipment = ( GenericEquipment ) cparam.unmarshalParameterizable( equipmentXML, genericEquipment );

            if ( equipmentXML.getMake() != null ) {
                genericEquipment.setMake( ( OntologyTerm ) reService.findIdentifiable( equipmentXML.getMake().getOntologyTermRef() ) );
            }
            if ( equipmentXML.getModel() != null ) {
                genericEquipment.setModel( ( OntologyTerm ) reService.findIdentifiable( equipmentXML.getModel().getOntologyTermRef() ) );
            }

            if ( genericEquipment.getId() != null ) {
                helper.assignAndLoadIdentifiable(
                        genericEquipment, "fugeOM.Common.Protocol.GenericEquipment", System.err );
            } else {
                helper.loadIdentifiable( genericEquipment, "fugeOM.Common.Protocol.GenericEquipment", System.err );
            }

            return genericEquipment;
        }
        System.err.println( "Error processing XML Equipment class: Should be of type \"FugeOMCommonProtocolGenericEquipmentType\" and isn't" );
        return null;  // shouldn't get here as there is currently only one type of Equipment allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolEquipmentType marshalEquipment( Equipment equipment )
            throws URISyntaxException, RealizableEntityServiceException {

        if ( equipment instanceof GenericEquipment ) {

            // create fuge object
            FugeOMCommonProtocolGenericEquipmentType equipmentXML = new FugeOMCommonProtocolGenericEquipmentType();

            // get equipment attributes
            equipmentXML = ( FugeOMCommonProtocolGenericEquipmentType ) ci.marshalIdentifiable(
                    equipmentXML, equipment );

            equipmentXML = ( FugeOMCommonProtocolGenericEquipmentType ) cparam.marshalParameterizable(
                    equipmentXML, equipment );

            if ( equipment.getMake() != null ) {
                FugeOMCommonProtocolEquipmentType.Make make = new FugeOMCommonProtocolEquipmentType.Make();
                make.setOntologyTermRef( equipment.getMake().getIdentifier() );
                equipmentXML.setMake( make );
            }
            if ( equipment.getModel() != null ) {
                FugeOMCommonProtocolEquipmentType.Model model = new FugeOMCommonProtocolEquipmentType.Model();
                model.setOntologyTermRef( equipment.getModel().getIdentifier() );
                equipmentXML.setModel( model );
            }

            // get generic equipment attributes
            equipmentXML = cgeq.marshalGenericEquipment( equipmentXML, ( GenericEquipment ) equipment );

            return equipmentXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Equipment allowed.
    }

    // this method is different from the others in that it will generate ALL equipment
    // in one go, rather than just one piece of equipment. This is because software may not
    // have been made yet, and so this method needs protocolCollection changeable so that it can add
    // software if necessary.
    public FugeOMCollectionProtocolCollectionType generateRandomXML(
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FugeOMCommonProtocolGenericEquipmentType genEquipmentXML = new FugeOMCommonProtocolGenericEquipmentType();
            // get equipment attributes
            genEquipmentXML = ( FugeOMCommonProtocolGenericEquipmentType ) ci.generateRandomXML( genEquipmentXML );

            genEquipmentXML = ( FugeOMCommonProtocolGenericEquipmentType ) cparam.generateRandomXML(
                    genEquipmentXML, frXML );

            if ( frXML.getOntologyCollection() != null ) {
                FugeOMCommonProtocolEquipmentType.Make make = new FugeOMCommonProtocolEquipmentType.Make();
                make.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setMake( make );

                FugeOMCommonProtocolEquipmentType.Model model = new FugeOMCommonProtocolEquipmentType.Model();
                model.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setModel( model );
            }

            // software required for generic equipment attributes
            if ( protocolCollectionXML.getSoftware() == null ) {
                CisbanSoftwareHelper csw = new CisbanSoftwareHelper( reService, ci );
                for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                    FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML = new FugeOMCommonProtocolGenericSoftwareType();
                    genericSoftwareXML = csw.generateRandomXML( genericSoftwareXML, protocolCollectionXML, frXML );
                    JAXBElement<? extends FugeOMCommonProtocolGenericSoftwareType> element = ( new ObjectFactory() ).createGenericSoftware(
                            genericSoftwareXML );
                    protocolCollectionXML.getSoftware().add( element );
                }
            }
            // get generic equipment attributes
            if ( i > 0 ) {
                genEquipmentXML = cgeq.generateRandomXML(
                        genEquipmentXML,
                        ( FugeOMCommonProtocolGenericEquipmentType ) protocolCollectionXML.getEquipment()
                                .get( 0 )
                                .getValue(),
                        frXML );
            } else {
                genEquipmentXML = cgeq.generateRandomXML( genEquipmentXML, null, frXML );
            }

            JAXBElement<? extends FugeOMCommonProtocolGenericEquipmentType> element = ( new ObjectFactory() ).createGenericEquipment(
                    genEquipmentXML );
            protocolCollectionXML.getEquipment().add( element );
        }
        return protocolCollectionXML;

    }

    public Equipment getLatestVersion( Equipment equipment ) throws RealizableEntityServiceException {

        // check that the equipment isn't already the most recent version. if it is,
        // do nothing and continue to the GenericEquipment get latest version, passing
        // isLatestEquipment = true.
        // todo quetsion of latest version of ontology terms, as these are also passed by reference
        String latestId = reService.findLatestIdentifierByEndurant( equipment.getEndurant().getIdentifier() );

        if ( latestId.equals( equipment.getIdentifier() ) ) {
            // go directly to GenericEquipment's getLatestVersion, with no update of Equipment necessary
            equipment = cgeq.getLatestVersion( ( GenericEquipment ) equipment, true );
            return equipment;
        }

        equipment = ( Equipment ) reService.findIdentifiable( latestId );
        equipment = ( Equipment ) ci.getLatestVersion( equipment );

        // get equipment attributes
        equipment = ( Equipment ) cparam.getLatestVersion( equipment, ci );

        if ( equipment.getMake() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    equipment.getMake().getEndurant().getIdentifier() );
            equipment.setMake( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        if ( equipment.getModel() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    equipment.getModel().getEndurant().getIdentifier() );
            equipment.setModel( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        // get generic equipment attributes
        if ( equipment instanceof GenericEquipment ) {
            equipment = cgeq.getLatestVersion( ( GenericEquipment ) equipment, false );
        }

        return equipment;
    }
}
