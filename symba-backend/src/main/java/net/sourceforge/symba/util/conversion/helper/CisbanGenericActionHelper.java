package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.GenericAction;
import fugeOM.Common.Protocol.GenericParameter;
import fugeOM.Common.Protocol.Parameter;
import fugeOM.Common.Protocol.Protocol;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericActionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericParameterType;
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

public class CisbanGenericActionHelper implements MappingHelper<GenericAction, FugeOMCommonProtocolGenericActionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanParameterHelper cp;
    private final CisbanHelper helper;

    public CisbanGenericActionHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cp = new CisbanParameterHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericAction unmarshal( FugeOMCommonProtocolGenericActionType genericActionXML,
                                    GenericAction genericAction )
            throws RealizableEntityServiceException {

        // set any GenericAction-specific traits

        // action term
        if ( genericActionXML.getActionTerm() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            genericAction.setActionTerm(
                    ( OntologyTerm ) reService
                            .findIdentifiable( genericActionXML.getActionTerm().getOntologyTermRef() ) );
        }

        // action text
        if ( genericActionXML.getActionText() != null ) {
            genericAction.setActionText( genericActionXML.getActionText() );
        }

        // protocol ref
        if ( genericActionXML.getProtocolRef() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            genericAction
                    .setGenProtocolRef( ( Protocol ) reService.findIdentifiable( genericActionXML.getProtocolRef() ) );
        }

        // you can only have a GenericParameter here
        Set<GenericParameter> parameters = new HashSet<GenericParameter>();
        for ( FugeOMCommonProtocolGenericParameterType parameterXML : genericActionXML.getGenericParameter() ) {
            // set fuge object
            parameters.add( ( GenericParameter ) cp.unmarshal( parameterXML, ( GenericParameter ) helper.getOrCreateLatest(
                    parameterXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenParamEndurant",
                    parameterXML.getName(),
                    "fugeOM.Common.Protocol.GenericParameter",
                    System.err ) ) );
        }
        genericAction.setParameters( parameters );

        return genericAction;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolGenericActionType marshal(
            FugeOMCommonProtocolGenericActionType genericActionXML,
            GenericAction genericAction ) throws RealizableEntityServiceException {

        // set any GenericAction-specific traits

        // action term
        if ( genericAction.getActionTerm() != null ) {
            FugeOMCommonProtocolGenericActionType.ActionTerm aterm = new FugeOMCommonProtocolGenericActionType.ActionTerm();
            aterm.setOntologyTermRef( genericAction.getActionTerm().getIdentifier() );
            genericActionXML.setActionTerm( aterm );
        }

        // action text
        if ( genericAction.getActionText() != null ) {
            genericActionXML.setActionText( genericAction.getActionText() );
        }

        // protocol ref
        if ( genericAction.getGenProtocolRef() != null ) {
            genericActionXML.setProtocolRef( genericAction.getGenProtocolRef().getIdentifier() );
        }

        // you can only have a GenericParameter here
        for ( Object parameterObj : genericAction.getParameters() ) {
            GenericParameter parameter = ( GenericParameter ) parameterObj;
            // set fuge object
            genericActionXML.getGenericParameter()
                    .add( ( FugeOMCommonProtocolGenericParameterType ) cp.marshal( new FugeOMCommonProtocolGenericParameterType(), parameter ) );
        }

        return genericActionXML;
    }

    public FugeOMCommonProtocolGenericActionType generateRandomXML( FugeOMCommonProtocolGenericActionType genericActionXML ) {
        // action text
        genericActionXML.setActionText( String.valueOf( Math.random() ) );

        return genericActionXML;
    }

    public FugeOMCommonProtocolGenericActionType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolGenericActionType genericActionXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        genericActionXML = generateRandomXML( genericActionXML );

        // action term
        if ( frXML.getOntologyCollection() != null ) {
            FugeOMCommonProtocolGenericActionType.ActionTerm aterm = new FugeOMCommonProtocolGenericActionType.ActionTerm();
            aterm.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            genericActionXML.setActionTerm( aterm );
        }

        // protocol ref
        if ( protocolCollectionXML.getProtocol().size() > 0 ) {
            genericActionXML.setProtocolRef( protocolCollectionXML.getProtocol().get( 0 ).getValue().getIdentifier() );
        }

        // you can only have a GenericParameter here
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericActionXML.getGenericParameter().add(
                    ( FugeOMCommonProtocolGenericParameterType ) cp
                            .generateRandomXMLWithLinksOut( new FugeOMCommonProtocolGenericParameterType(), frXML ) );
        }

        return genericActionXML;
    }

    public GenericAction getLatestVersion( GenericAction genericAction ) throws RealizableEntityServiceException {

        CisbanProtocolHelper cpr = new CisbanProtocolHelper();

        if ( genericAction.getActionTerm() != null ) {
            OntologyTerm ot = ( OntologyTerm ) reService
                    .findLatestByEndurant( genericAction.getActionTerm().getEndurant().getIdentifier() );
            genericAction.setActionTerm( ( OntologyTerm ) ci.getLatestVersion( ot ) );
        }

        if ( genericAction.getGenProtocolRef() != null ) {
            genericAction.setGenProtocolRef( cpr.getLatestVersion( genericAction.getGenProtocolRef() ) );
        }

        // prepare updated set
        Set<GenericParameter> set = new HashSet<GenericParameter>();

        // load all the latest versions into the new set.
        for ( Object obj : genericAction.getParameters() ) {
            set.add( ( GenericParameter ) cp.getLatestVersion( ( Parameter ) obj ) );
        }
        genericAction.setParameters( set );

        return genericAction;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( GenericAction genericAction, PrintStream printStream ) {
        prettyPrint( null, genericAction, printStream );
    }

    public void prettyPrint( String prepend, GenericAction genericAction, PrintStream printStream ) {
        String bbb = "------Generic Action Text: ";
        String ccc = "    Referenced ";

        if ( prepend != null ) {
            bbb = prepend + bbb;
            ccc = prepend + ccc;
        }

        CisbanProtocolHelper cpr = new CisbanProtocolHelper();

        if ( genericAction.getGenProtocolRef() != null ) {
            cpr.prettyPrint( ccc, genericAction.getGenProtocolRef(), printStream );
        }
        if ( genericAction.getActionText() != null ) {
            printStream.println( bbb + genericAction.getActionText() );
        }
    }
}
