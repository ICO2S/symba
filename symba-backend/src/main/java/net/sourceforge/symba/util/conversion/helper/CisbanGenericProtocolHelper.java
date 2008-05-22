package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import net.sourceforge.symba.util.CisbanHelper;

import java.io.PrintStream;
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

public class CisbanGenericProtocolHelper implements MappingHelper<GenericProtocol, FugeOMCommonProtocolGenericProtocolType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanActionHelper ca;
    private final CisbanParameterHelper cp;
    private final CisbanSoftwareHelper csw;
    private final CisbanHelper helper;

    public CisbanGenericProtocolHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.ca = new CisbanActionHelper();
        this.cp = new CisbanParameterHelper();
        this.csw = new CisbanSoftwareHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // @todo assumes all Collections (ReferenceableCollection, AuditCollection etc) are already extant in the database
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericProtocol unmarshal( FugeOMCommonProtocolGenericProtocolType genericProtocolXML,
                                      GenericProtocol genericProtocol ) throws RealizableEntityServiceException {

        // set any GenericProtocol-specific traits

        // can only have GenericActions here
        Set<GenericAction> genericActions = new HashSet<GenericAction>();
        for ( FugeOMCommonProtocolGenericActionType genericActionXML : genericProtocolXML.getGenericAction() ) {
            genericActions.add( ( GenericAction ) ca.unmarshal( genericActionXML, ( Action ) helper.getOrCreateLatest(
                    genericActionXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenericActionEndurant",
                    genericActionXML.getName(),
                    "fugeOM.Common.Protocol.GenericAction",
                    System.err ) ) );
        }
        genericProtocol.setGenericActions( genericActions );

        // generic parameter
        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        for ( FugeOMCommonProtocolGenericParameterType genericParameterXML : genericProtocolXML
                .getGenericParameter() ) {
            genericParameters.add( ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) helper.getOrCreateLatest(
                    genericParameterXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenParamEndurant",
                    genericParameterXML.getName(),
                    "fugeOM.Common.Protocol.GenericParameter",
                    System.err ) ) );
        }
        genericProtocol.setProtocolParameters( genericParameters );

        // genPrtcltoequip
        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
        for ( FugeOMCommonProtocolGenericProtocolType.GenPrtclToEquip genPrtclToEquipXML : genericProtocolXML
                .getGenPrtclToEquip() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            genericEquipments.add(
                    ( GenericEquipment ) reService.findIdentifiable( genPrtclToEquipXML.getGenericEquipmentRef() ) );
        }
        genericProtocol.setGenPrtclToEquip( genericEquipments );

        // generic software
        Set<GenericSoftware> genericSoftwares = new HashSet<GenericSoftware>();
        for ( FugeOMCommonProtocolGenericProtocolType.GenSoftware genSoftwareXML : genericProtocolXML
                .getGenSoftware() ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            genericSoftwares
                    .add( ( GenericSoftware ) reService.findIdentifiable( genSoftwareXML.getGenericSoftwareRef() ) );
        }
        genericProtocol.setGenSoftware( genericSoftwares );

        return genericProtocol;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericProtocolType marshal(
            FugeOMCommonProtocolGenericProtocolType genericProtocolXML,
            GenericProtocol genericProtocol ) throws RealizableEntityServiceException {

        // set any GenericProtocol-specific traits

        // can only have generic actions.
        for ( Object genericObj : genericProtocol.getGenericActions() ) {
            GenericAction genericAction = ( GenericAction ) genericObj;
            genericProtocolXML.getGenericAction()
                    .add( ( FugeOMCommonProtocolGenericActionType ) ca.marshal( new FugeOMCommonProtocolGenericActionType(), genericAction ) );
        }
        // can only have generic parameters
        for ( Object genericObj : genericProtocol.getProtocolParameters() ) {
            GenericParameter genericParameter = ( GenericParameter ) genericObj;
            genericProtocolXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.marshal( new FugeOMCommonProtocolGenericParameterType(), genericParameter ) );
        }
        // protocol to equipment
        for ( Object genericObj : genericProtocol.getGenPrtclToEquip() ) {
            GenericEquipment genericEquipment = ( GenericEquipment ) genericObj;
            FugeOMCommonProtocolGenericProtocolType.GenPrtclToEquip equip = new FugeOMCommonProtocolGenericProtocolType.GenPrtclToEquip();
            equip.setGenericEquipmentRef( genericEquipment.getIdentifier() );
            genericProtocolXML.getGenPrtclToEquip().add( equip );
        }
        // software
        for ( Object genericObj : genericProtocol.getGenSoftware() ) {
            GenericSoftware genericSoftware = ( GenericSoftware ) genericObj;
            FugeOMCommonProtocolGenericProtocolType.GenSoftware genSoftware = new FugeOMCommonProtocolGenericProtocolType.GenSoftware();
            genSoftware.setGenericSoftwareRef( genericSoftware.getIdentifier() );
            genericProtocolXML.getGenSoftware().add( genSoftware );
        }

        return genericProtocolXML;
    }

    // the only way to make random xml is via generateRandomXMLWithLinks, as all parts require input from elsewhere in the fuge document
    public FugeOMCommonProtocolGenericProtocolType generateRandomXML( FugeOMCommonProtocolGenericProtocolType genericProtocolXML ) {
        return genericProtocolXML;
    }

    public FugeOMCommonProtocolGenericProtocolType generateRandomXML(
            FugeOMCommonProtocolGenericProtocolType genericProtocolXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        genericProtocolXML = generateRandomXML( genericProtocolXML );

        // can only have generic actions.
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericProtocolXML.getGenericAction().add(
                    ( FugeOMCommonProtocolGenericActionType ) ca.generateRandomXMLWithLinksOut(
                            new FugeOMCommonProtocolGenericActionType(), i, protocolCollectionXML, frXML ) );
        }
        // can only have generic parameters
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericProtocolXML.getGenericParameter().add(
                    ( FugeOMCommonProtocolGenericParameterType ) cp
                            .generateRandomXMLWithLinksOut( new FugeOMCommonProtocolGenericParameterType(), frXML ) );
        }

        if ( protocolCollectionXML != null ) {
            // protocol to equipment
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolGenericProtocolType.GenPrtclToEquip equip = new FugeOMCommonProtocolGenericProtocolType.GenPrtclToEquip();
                equip.setGenericEquipmentRef(
                        protocolCollectionXML.getEquipment().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getGenPrtclToEquip().add( equip );
            }
            // software
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolGenericProtocolType.GenSoftware genSoftware = new FugeOMCommonProtocolGenericProtocolType.GenSoftware();
                genSoftware.setGenericSoftwareRef(
                        protocolCollectionXML.getSoftware().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getGenSoftware().add( genSoftware );
            }
        }
        return genericProtocolXML;
    }

    // it is imperative that you use the version of getLatestVersion that has the boolean value, so this method does nothing
    // todo sort out a better way of dealing with this
    public GenericProtocol getLatestVersion( GenericProtocol genericProtocol ) throws RealizableEntityServiceException {
        return null;
    }

    public GenericProtocol getLatestVersion( GenericProtocol genericProtocol,
                                             boolean isLatestProtocol ) throws RealizableEntityServiceException {

        // GenericActions and GenericParameters are wholly encompassed within GenericProtocols, and therefore
        // will always be up-to-date if the GenericProtocol is. However, Equipment and Software
        // are only passed by reference, and will have to be checked each time, irrespective of
        // the status of isLatestProtocol
        // todo does this hold true for ontology terms, or will we get the old versions of ontology terms for GAs and sucn?
        // prepare updated set
        if ( !isLatestProtocol ) {
            Set<GenericAction> set = new HashSet<GenericAction>();

            // load all the latest versions into the new set.
            for ( Object obj : genericProtocol.getGenericActions() ) {
                set.add( ( GenericAction ) ca.getLatestVersion( ( Action ) obj ) );
            }
            genericProtocol.setGenericActions( set );

            // prepare updated set
            Set<GenericParameter> set1 = new HashSet<GenericParameter>();

            // load all the latest versions into the new set.
            for ( Object obj : genericProtocol.getProtocolParameters() ) {
                set1.add( ( GenericParameter ) cp.getLatestVersion( ( Parameter ) obj ) );
            }
            genericProtocol.setProtocolParameters( set1 );

        }

        CisbanEquipmentHelper ceq = new CisbanEquipmentHelper();

        // prepare updated set
        Set<GenericEquipment> set2 = new HashSet<GenericEquipment>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocol.getGenPrtclToEquip() ) {
            set2.add( ( GenericEquipment ) ceq.getLatestVersion( ( Equipment ) obj ) );
        }
        genericProtocol.setGenPrtclToEquip( set2 );

        // prepare updated set
        Set<GenericSoftware> set3 = new HashSet<GenericSoftware>();

        // load all the latest versions into the new set.
        for ( Object obj : genericProtocol.getGenSoftware() ) {
            set3.add( ( GenericSoftware ) csw.getLatestVersion( ( Software ) obj ) );
        }
        genericProtocol.setGenSoftware( set3 );


        return genericProtocol;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( GenericProtocol genericProtocol, PrintStream printStream ) {
        prettyPrint( null, genericProtocol, printStream );
    }

    public void prettyPrint( String prepend, GenericProtocol genericProtocol, PrintStream printStream ) {
        for ( Object obj : genericProtocol.getGenericActions() ) {
            ca.prettyPrint( prepend, ( Action ) obj, printStream );
        }
    }
}
