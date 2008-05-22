package net.sourceforge.symba.util.conversion.helper;

import fugeOM.Common.Protocol.Action;
import fugeOM.Common.Protocol.GenericAction;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolActionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericActionType;
import net.sourceforge.symba.util.CisbanHelper;

import java.io.PrintStream;

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

public class CisbanActionHelper implements MappingHelper<Action, FugeOMCommonProtocolActionType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanGenericActionHelper cga;
    private final CisbanHelper helper;

    public CisbanActionHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cga = new CisbanGenericActionHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Action unmarshal( FugeOMCommonProtocolActionType actionXML, Action action )
            throws RealizableEntityServiceException {

        // determine what sort of action it is
        if ( actionXML instanceof FugeOMCommonProtocolGenericActionType ) {

            // Retrieve latest version from the database.
            GenericAction genericAction = ( GenericAction ) action;

            // get action attributes
            genericAction = ( GenericAction ) ci.unmarshal( actionXML, genericAction );

            // action ordinal
            if ( actionXML.getActionOrdinal() != null )
                genericAction.setActionOrdinal( actionXML.getActionOrdinal() );

            // get generic action attributes
            genericAction = cga
                    .unmarshal( ( FugeOMCommonProtocolGenericActionType ) actionXML, genericAction );

            if ( genericAction.getId() != null ) {
                helper.assignAndLoadIdentifiable( genericAction, "fugeOM.Common.Protocol.GenericAction", System.err );
            } else {
                helper.loadIdentifiable( genericAction, "fugeOM.Common.Protocol.GenericAction", System.err );
            }

            return genericAction;
        }
        return null; // shouldn't get here as there is currently only one type of Action allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolActionType marshal( FugeOMCommonProtocolActionType actionXML, Action action )
            throws RealizableEntityServiceException {

        // determine what sort of action it is
        if ( action instanceof GenericAction ) {
            // create fuge object
            FugeOMCommonProtocolGenericActionType genericActionXML = ( FugeOMCommonProtocolGenericActionType ) actionXML;

            // get action attributes
            genericActionXML = ( FugeOMCommonProtocolGenericActionType ) ci
                    .marshal( genericActionXML, action );

            // action ordinal
            genericActionXML.setActionOrdinal( action.getActionOrdinal() );

            // get generic action attributes
            genericActionXML = cga.marshal( genericActionXML, ( GenericAction ) action );

            return genericActionXML;
        }
        return null; // shouldn't get here as there is currently only one type of Action allowed.
    }

    // you should use generateRandomXMLWithLinksOut, as it is to complicated to perform without more arguments.
    public FugeOMCommonProtocolActionType generateRandomXML( FugeOMCommonProtocolActionType actionXML ) {
        return actionXML;
    }

    // at this stage, frXML may not have the new equipment and software - the protocol collection may be the only one to have it
    public FugeOMCommonProtocolActionType generateRandomXMLWithLinksOut( FugeOMCommonProtocolActionType actionXML,
                                                                         int ordinal,
                                                                         FugeOMCollectionProtocolCollectionType protocolCollectionXML,
                                                                         FugeOMCollectionFuGEType frXML ) {

        FugeOMCommonProtocolGenericActionType genericActionXML = ( FugeOMCommonProtocolGenericActionType ) actionXML;

        // get action attributes
        genericActionXML = ( FugeOMCommonProtocolGenericActionType ) ci.generateRandomXML( genericActionXML );

        // action ordinal
        genericActionXML.setActionOrdinal( ordinal );

        // get generic action attributes
        genericActionXML = cga.generateRandomXMLWithLinksOut( genericActionXML, protocolCollectionXML, frXML );

        return genericActionXML;
    }

    public Action getLatestVersion( Action action ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        action = ( Action ) reService.findLatestByEndurant( action.getEndurant().getIdentifier() );
        action = ( Action ) ci.getLatestVersion( action );

        // determine what sort of action it is
        if ( action instanceof GenericAction ) {
            action = cga.getLatestVersion( ( GenericAction ) action );
        }
        return action;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( Action action, PrintStream printStream ) {
        prettyPrint( null, action, printStream );
    }

    public void prettyPrint( String prepend, Action action, PrintStream printStream ) {
        String aaa = "-----Action: ";
        if ( prepend != null ) {
            aaa = prepend + aaa;
        }
        ci.prettyPrint( aaa, action, printStream );
        cga.prettyPrint( prepend, ( GenericAction ) action, printStream );
    }
}
