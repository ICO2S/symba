package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Describable;
import fugeOM.Common.Protocol.Parameter;
import fugeOM.Common.Protocol.ParameterValue;
import fugeOM.Common.Protocol.ParameterizableApplication;
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
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanParameterizableApplicationHelper.java $
 *
 */

public class CisbanParameterizableApplicationHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final RealizableEntityService reService;

    public CisbanParameterizableApplicationHelper( RealizableEntityService reService, CisbanDescribableHelper cd ) {
        this.reService = reService;
        this.cd = cd;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ParameterizableApplication unmarshalParameterizableApplication(
            FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            ParameterizableApplication parameterizableApplication )
            throws URISyntaxException, RealizableEntityServiceException {

        Set<ParameterValue> parameterValues = new HashSet<ParameterValue>();
        for ( JAXBElement<? extends FugeOMCommonProtocolParameterValueType> elementXML : parameterizableApplicationXML.getParameterValue() ) {
            FugeOMCommonProtocolParameterValueType pvalueXML = elementXML.getValue();
            ParameterValue pvalue = ( ParameterValue ) reService.createDescribableOb(
                    "fugeOM.Common.Protocol.ParameterValue" );
            pvalue = ( ParameterValue ) cd.unmarshalDescribable( pvalueXML, pvalue );
            // Set the object to exactly the object is that is associated
            // with this version group.
            pvalue.setParameter( ( Parameter ) reService.findIdentifiable( pvalueXML.getParameterRef() ) );
            reService.createObInDB( "fugeOM.Common.Protocol.ParameterValue", pvalue );
            parameterValues.add( pvalue );
        }
        parameterizableApplication.setParameterValues( parameterValues );

        return parameterizableApplication;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolParameterizableApplicationType marshalParameterizableApplication(
            FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            ParameterizableApplication parameterizableApplication ) {

        for ( Object obj : parameterizableApplication.getParameterValues() ) {
            ParameterValue pvalue = ( ParameterValue ) obj;
            FugeOMCommonProtocolParameterValueType pvalueXML = new FugeOMCommonProtocolParameterValueType();
            pvalueXML.setParameterRef( pvalue.getParameter().getIdentifier() );
            JAXBElement<? extends FugeOMCommonProtocolParameterValueType> element = ( new ObjectFactory() ).createParameterValue(
                    pvalueXML );
            parameterizableApplicationXML.getParameterValue().add( element );
        }
        return parameterizableApplicationXML;
    }

    public FugeOMCommonProtocolParameterizableApplicationType generateRandomXML(
            FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML ) {

        if ( !protocolCollectionXML.getEquipment().isEmpty() ) {
            FugeOMCommonProtocolGenericEquipmentType eqXML = ( FugeOMCommonProtocolGenericEquipmentType ) protocolCollectionXML
                    .getEquipment()
                    .get( 0 )
                    .getValue();
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolParameterValueType pvalueXML = new FugeOMCommonProtocolParameterValueType();
                pvalueXML.setParameterRef( eqXML.getGenericParameter().get( 0 ).getIdentifier() );
                JAXBElement<? extends FugeOMCommonProtocolParameterValueType> element = ( new ObjectFactory() ).createParameterValue(
                        pvalueXML );
                parameterizableApplicationXML.getParameterValue().add( element );
            }
        }
        return parameterizableApplicationXML;
    }

    public ParameterizableApplication getLatestVersion(
            ParameterizableApplication parameterizableApplication,
            CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        // prepare updated set
        Set<ParameterValue> set = new HashSet<ParameterValue>();

        // load all the latest versions into the new set.
        for ( Object obj : parameterizableApplication.getParameterValues() ) {
            set.add( ( ParameterValue ) cd.getLatestVersion( ( Describable ) obj, ci ) );
        }
        parameterizableApplication.setParameterValues( set );

        return parameterizableApplication;
    }
}
