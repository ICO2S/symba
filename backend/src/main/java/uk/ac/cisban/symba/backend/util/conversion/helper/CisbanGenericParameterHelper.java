package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.GenericParameter;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericParameterType;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanGenericParameterHelper.java $
 *
 */

public class CisbanGenericParameterHelper {
    private final RealizableEntityService reService;

    public CisbanGenericParameterHelper( RealizableEntityService reService ) {
        this.reService = reService;
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericParameter unmarshalGenericParameter( FugeOMCommonProtocolGenericParameterType genericParameterXML,
                                                       GenericParameter genericParameter ) throws RealizableEntityServiceException {

        // set any GenericParameter-specific traits
        if ( genericParameterXML.getParameterType() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            genericParameter.setParameterType( ( OntologyTerm ) reService.findIdentifiable( genericParameterXML.getParameterType().getOntologyTermRef() ) );
        }

        return genericParameter;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericParameterType marshalGenericParameter(
            FugeOMCommonProtocolGenericParameterType genericParameterXML,
            GenericParameter genericParameter ) {

        // set any GenericParameter-specific traits
        if ( genericParameter.getParameterType() != null ) {
            FugeOMCommonProtocolGenericParameterType.ParameterType ptXML = new FugeOMCommonProtocolGenericParameterType.ParameterType();
            ptXML.setOntologyTermRef( genericParameter.getParameterType().getIdentifier() );
            genericParameterXML.setParameterType( ptXML );
        }

        return genericParameterXML;
    }

    public FugeOMCommonProtocolGenericParameterType generateRandomXML(
            FugeOMCommonProtocolGenericParameterType genericParameterXML, FugeOMCollectionFuGEType frXML ) {

        if ( frXML.getOntologyCollection() != null ) {
            FugeOMCommonProtocolGenericParameterType.ParameterType ptXML = new FugeOMCommonProtocolGenericParameterType.ParameterType();
            ptXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            genericParameterXML.setParameterType( ptXML );
        }
        return genericParameterXML;
    }

    public GenericParameter getLatestVersion( GenericParameter genericParameter,
                                              CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        if ( genericParameter.getParameterType() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService.findLatestByEndurant(
                    genericParameter.getParameterType().getEndurant().getIdentifier() );
            genericParameter.setParameterType( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        return genericParameter;
    }
}
