package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Describable;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;
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

public class CisbanParameterizableApplicationHelper implements MappingHelper<ParameterizableApplication, FugeOMCommonProtocolParameterizableApplicationType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;

    public CisbanParameterizableApplicationHelper() {
        this.cd = new CisbanDescribableHelper();
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ParameterizableApplication unmarshal(
            FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            ParameterizableApplication parameterizableApplication )
            throws RealizableEntityServiceException {

        Set<ParameterValue> parameterValues = new HashSet<ParameterValue>();
        for ( JAXBElement<? extends FugeOMCommonProtocolParameterValueType> elementXML : parameterizableApplicationXML.getParameterValue() ) {
            FugeOMCommonProtocolParameterValueType pvalueXML = elementXML.getValue();

            if ( pvalueXML instanceof FugeOMCommonProtocolAtomicParameterValueType ) {
                AtomicParameterValue pvalue = ( AtomicParameterValue ) reService.createDescribableOb(
                        "fugeOM.Common.Protocol.AtomicParameterValue" );
                pvalue = ( AtomicParameterValue ) cd.unmarshal( pvalueXML, pvalue );
                // Set the object to exactly the object is that is associated
                // with this version group.
                pvalue.setParameter( ( Parameter ) reService.findIdentifiable( pvalueXML.getParameterRef() ) );
                pvalue.setValue( ( ( FugeOMCommonProtocolAtomicParameterValueType ) pvalueXML ).getValue() );
                reService.createObInDB( "fugeOM.Common.Protocol.AtomicParameterValue", pvalue );
                parameterValues.add( pvalue );
            } else if ( pvalueXML instanceof FugeOMCommonProtocolComplexParameterValueType ) {
                ComplexParameterValue pvalue = ( ComplexParameterValue ) reService.createDescribableOb(
                        "fugeOM.Common.Protocol.ComplexParameterValue" );
                pvalue = ( ComplexParameterValue ) cd.unmarshal( pvalueXML, pvalue );
                // Set the object to exactly the object is that is associated
                // with this version group.
                pvalue.setParameter( ( Parameter ) reService.findIdentifiable( pvalueXML.getParameterRef() ) );
                pvalue.setParameterValue(
                        ( OntologyTerm ) reService.findIdentifiable(
                                ( ( FugeOMCommonProtocolComplexParameterValueType ) pvalueXML ).getParameterValue().getOntologyTermRef() ) );
                reService.createObInDB( "fugeOM.Common.Protocol.ComplexParameterValue", pvalue );
                parameterValues.add( pvalue );
            } else if ( pvalueXML instanceof FugeOMCommonProtocolBooleanParameterValueType ) {
                BooleanParameterValue pvalue = ( BooleanParameterValue ) reService.createDescribableOb(
                        "fugeOM.Common.Protocol.BooleanParameterValue" );
                pvalue = ( BooleanParameterValue ) cd.unmarshal( pvalueXML, pvalue );
                // Set the object to exactly the object is that is associated
                // with this version group.
                pvalue.setParameter( ( Parameter ) reService.findIdentifiable( pvalueXML.getParameterRef() ) );
                pvalue.setValue( ( ( FugeOMCommonProtocolBooleanParameterValueType ) pvalueXML ).isValue() );
                reService.createObInDB( "fugeOM.Common.Protocol.BooleanParameterValue", pvalue );
                parameterValues.add( pvalue );
            } else if ( pvalueXML instanceof FugeOMCommonProtocolRangeParameterValueType ) {
                RangeParameterValue pvalue = ( RangeParameterValue ) reService.createDescribableOb(
                        "fugeOM.Common.Protocol.RangeParameterValue" );
                pvalue = ( RangeParameterValue ) cd.unmarshal( pvalueXML, pvalue );
                // Set the object to exactly the object is that is associated
                // with this version group.
                pvalue.setParameter( ( Parameter ) reService.findIdentifiable( pvalueXML.getParameterRef() ) );
                pvalue.setLowValue( ( ( FugeOMCommonProtocolRangeParameterValueType ) pvalueXML ).getLowValue() );
                pvalue.setHighValue( ( ( FugeOMCommonProtocolRangeParameterValueType ) pvalueXML ).getHighValue() );
                reService.createObInDB( "fugeOM.Common.Protocol.RangeParameterValue", pvalue );
                parameterValues.add( pvalue );
            }

        }

        parameterizableApplication.setParameterValues( parameterValues );

        return parameterizableApplication;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolParameterizableApplicationType marshal(
            FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            ParameterizableApplication parameterizableApplication ) {

        for ( Object obj : parameterizableApplication.getParameterValues() ) {
            ParameterValue pvalue = ( ParameterValue ) obj;

            if ( pvalue instanceof AtomicParameterValue ) {
                FugeOMCommonProtocolAtomicParameterValueType pvalueXML = new FugeOMCommonProtocolAtomicParameterValueType();
                pvalueXML = ( FugeOMCommonProtocolAtomicParameterValueType ) cd.marshal( pvalueXML, pvalue );

                pvalueXML.setParameterRef( pvalue.getParameter().getIdentifier() );
                pvalueXML.setValue( ( ( AtomicParameterValue ) pvalue ).getValue() );
                JAXBElement<? extends FugeOMCommonProtocolAtomicParameterValueType> element = ( new ObjectFactory() ).createAtomicParameterValue(
                        pvalueXML );
                parameterizableApplicationXML.getParameterValue().add( element );
            } else if ( pvalue instanceof ComplexParameterValue ) {
                FugeOMCommonProtocolComplexParameterValueType pvalueXML = new FugeOMCommonProtocolComplexParameterValueType();

                pvalueXML = ( FugeOMCommonProtocolComplexParameterValueType ) cd.marshal(
                        pvalueXML, pvalue );
                pvalueXML.setParameterRef( pvalue.getParameter().getIdentifier() );
                FugeOMCommonProtocolComplexParameterValueType.ParameterValue temp = new FugeOMCommonProtocolComplexParameterValueType.ParameterValue();
                temp.setOntologyTermRef( ( ( ComplexParameterValue ) pvalue ).getParameterValue().getIdentifier() );
                pvalueXML.setParameterValue( temp );
                JAXBElement<? extends FugeOMCommonProtocolComplexParameterValueType> element = ( new ObjectFactory() ).createComplexParameterValue(
                        pvalueXML );
                parameterizableApplicationXML.getParameterValue().add( element );
            } else if ( pvalue instanceof BooleanParameterValue ) {
                FugeOMCommonProtocolBooleanParameterValueType pvalueXML = new FugeOMCommonProtocolBooleanParameterValueType();

                pvalueXML = ( FugeOMCommonProtocolBooleanParameterValueType ) cd.marshal(
                        pvalueXML, pvalue );
                pvalueXML.setParameterRef( pvalue.getParameter().getIdentifier() );
                pvalueXML.setValue( ( ( BooleanParameterValue ) pvalue ).getValue() );
                JAXBElement<? extends FugeOMCommonProtocolBooleanParameterValueType> element = ( new ObjectFactory() ).createBooleanParameterValue(
                        pvalueXML );
                parameterizableApplicationXML.getParameterValue().add( element );
            } else if ( pvalue instanceof RangeParameterValue ) {
                FugeOMCommonProtocolRangeParameterValueType pvalueXML = new FugeOMCommonProtocolRangeParameterValueType();

                pvalueXML = ( FugeOMCommonProtocolRangeParameterValueType ) cd.marshal( pvalueXML, pvalue );
                pvalueXML.setParameterRef( pvalue.getParameter().getIdentifier() );
                pvalueXML.setLowValue( ( ( RangeParameterValue ) pvalue ).getLowValue() );
                pvalueXML.setHighValue( ( ( RangeParameterValue ) pvalue ).getHighValue() );
                JAXBElement<? extends FugeOMCommonProtocolRangeParameterValueType> element = ( new ObjectFactory() ).createRangeParameterValue(
                        pvalueXML );
                parameterizableApplicationXML.getParameterValue().add( element );
            }

        }
        return parameterizableApplicationXML;
    }

    // currently all the code required to make the randomXML needs the protocolCollection passed as well.
    public FugeOMCommonProtocolParameterizableApplicationType generateRandomXML( FugeOMCommonProtocolParameterizableApplicationType parameterizableApplicationXML ) {
        return parameterizableApplicationXML;
    }

    public FugeOMCommonProtocolParameterizableApplicationType generateRandomXMLWithLinksOut(
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
            ParameterizableApplication parameterizableApplication ) throws RealizableEntityServiceException {

        // prepare updated set
        Set<ParameterValue> set = new HashSet<ParameterValue>();

        // load all the latest versions into the new set.
        for ( Object obj : parameterizableApplication.getParameterValues() ) {
            set.add( ( ParameterValue ) cd.getLatestVersion( ( Describable ) obj ) );
        }
        parameterizableApplication.setParameterValues( set );

        return parameterizableApplication;
    }
}
