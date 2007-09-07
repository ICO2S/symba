package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.ComplexValue;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolComplexValueType;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanComplexValueHelper.java $
 *
 */

public class CisbanComplexValueHelper {
    private final RealizableEntityService reService;

    public CisbanComplexValueHelper( RealizableEntityService reService ) {
        this.reService = reService;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ComplexValue unmarshalComplexValue( FugeOMCommonProtocolComplexValueType valueXML,
                                               ComplexValue value ) throws RealizableEntityServiceException {

        // set any ComplexValue-specific traits

        // default value as an ontology term
        if ( valueXML.getDefaultValue() != null ) {
            value.set_defaultValue( ( OntologyTerm ) reService.findIdentifiable( valueXML.getDefaultValue().getOntologyTermRef() ) );
        }

        return value;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolComplexValueType marshalComplexValue( FugeOMCommonProtocolComplexValueType valueXML,
                                                                     ComplexValue value ) {

        // set any ComplexValue-specific traits
        if ( value.get_defaultValue() != null ) {
            FugeOMCommonProtocolComplexValueType.DefaultValue defaultValueXML = new FugeOMCommonProtocolComplexValueType.DefaultValue();
            defaultValueXML.setOntologyTermRef( value.get_defaultValue().getIdentifier() );
            valueXML.setDefaultValue( defaultValueXML );
        }

        return valueXML;
    }

    public FugeOMCommonProtocolComplexValueType generateRandomXML( FugeOMCommonProtocolComplexValueType valueXML,
                                                                   FugeOMCollectionFuGEType frXML ) {
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMCommonProtocolComplexValueType.DefaultValue defaultValueXML = new FugeOMCommonProtocolComplexValueType.DefaultValue();
            defaultValueXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            valueXML.setDefaultValue( defaultValueXML );
        }
        return valueXML;
    }

    public ComplexValue getLatestVersion( ComplexValue complexValue,
                                          CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {
        if ( complexValue.get_defaultValue() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    complexValue.get_defaultValue().getEndurant().getIdentifier() );
            complexValue.set_defaultValue( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }
        return complexValue;
    }
}
