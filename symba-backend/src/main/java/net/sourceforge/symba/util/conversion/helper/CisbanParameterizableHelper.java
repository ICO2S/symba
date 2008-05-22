package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.Parameterizable;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditContactRoleType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolParameterizableType;

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

public class CisbanParameterizableHelper implements MappingHelper<Parameterizable, FugeOMCommonProtocolParameterizableType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanContactRoleHelper ccr;

    public CisbanParameterizableHelper() {
        this.ccr = new CisbanContactRoleHelper();
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Parameterizable unmarshal( FugeOMCommonProtocolParameterizableType parameterizableXML,
                                      Parameterizable parameterizable )
            throws RealizableEntityServiceException {

        // contacts
        Set<ContactRole> contactRoles = new HashSet<ContactRole>();

        for ( FugeOMCommonAuditContactRoleType contactRoleXML : parameterizableXML.getContactRole() ) {
            contactRoles.add( ccr.unmarshal( contactRoleXML, ( ContactRole ) reService.createDescribableOb( "fugeOM.Common.Audit.ContactRole" ) ) );
        }
        parameterizable.setProvider( contactRoles );

        // parameterizable types
        if ( parameterizableXML.getParameterizableTypes() != null ) {
            Set<OntologyTerm> pts = new HashSet<OntologyTerm>();
            for ( FugeOMCommonProtocolParameterizableType.ParameterizableTypes parameterizableTypesXML : parameterizableXML
                    .getParameterizableTypes() ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                pts.add( ( OntologyTerm ) reService.findIdentifiable( parameterizableTypesXML.getOntologyTermRef() ) );
            }
            parameterizable.setParameterizableTypes( pts );
        }

        return parameterizable;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolParameterizableType marshal(
            FugeOMCommonProtocolParameterizableType parameterizableXML, Parameterizable parameterizable )
            throws RealizableEntityServiceException {

        // get any lazily loaded objects
        parameterizable = ( Parameterizable ) reService.greedyGet( parameterizable );

        for ( Object contactRoleObj : parameterizable.getProvider() ) {
            parameterizableXML.getContactRole().add( ccr.marshal( new FugeOMCommonAuditContactRoleType(), ( ContactRole ) contactRoleObj ) );
        }

        for ( Object obj : parameterizable.getParameterizableTypes() ) {
            OntologyTerm ontologyTerm = ( OntologyTerm ) obj;

            FugeOMCommonProtocolParameterizableType.ParameterizableTypes parameterizableTypesXML = new FugeOMCommonProtocolParameterizableType.ParameterizableTypes();
            parameterizableTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
            parameterizableXML.getParameterizableTypes().add( parameterizableTypesXML );
        }
        return parameterizableXML;
    }

    public FugeOMCommonProtocolParameterizableType generateRandomXML( FugeOMCommonProtocolParameterizableType parameterizableXML ) {

        CisbanIdentifiableHelper ci = new CisbanIdentifiableHelper();
        parameterizableXML = ( FugeOMCommonProtocolParameterizableType ) ci.generateRandomXML( parameterizableXML );

        return parameterizableXML;
    }

    public FugeOMCommonProtocolParameterizableType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolParameterizableType parameterizableXML, FugeOMCollectionFuGEType frXML ) {

        parameterizableXML = generateRandomXML( parameterizableXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            parameterizableXML.getContactRole().add( ccr.generateRandomXMLwithLinksOut( frXML ) );
        }

        if ( frXML.getOntologyCollection() != null ) {
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolParameterizableType.ParameterizableTypes parameterizableTypesXML = new FugeOMCommonProtocolParameterizableType.ParameterizableTypes();
                parameterizableTypesXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                parameterizableXML.getParameterizableTypes().add( parameterizableTypesXML );
            }
        }
        return parameterizableXML;
    }

    public Parameterizable getLatestVersion( Parameterizable parameterizable ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        parameterizable = ( Parameterizable ) reService.greedyGet( parameterizable );

        // prepare updated set
        Set<ContactRole> set = new HashSet<ContactRole>();

        // load all the latest versions into the new set.
        for ( Object obj : parameterizable.getProvider() ) {
            set.add( ccr.getLatestVersion( ( ContactRole ) obj ) );
        }
        parameterizable.setProvider( set );

        // prepare updated set
        Set<OntologyTerm> set2 = new HashSet<OntologyTerm>();

        // load all the latest versions into the new set.
        for ( Object obj : parameterizable.getParameterizableTypes() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set2.add( ( OntologyTerm ) ( new CisbanIdentifiableHelper() ).getLatestVersion( identifiable ) );
        }
        parameterizable.setParameterizableTypes( set2 );

        return parameterizable;
    }
}