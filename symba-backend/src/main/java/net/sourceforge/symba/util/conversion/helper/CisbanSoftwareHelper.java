package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Common.Protocol.GenericSoftware;
import fugeOM.Common.Protocol.Software;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericSoftwareType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolSoftwareType;
import net.sourceforge.symba.util.CisbanHelper;

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

public class CisbanSoftwareHelper implements MappingHelper<Software, FugeOMCommonProtocolSoftwareType> {
    private final CisbanParameterizableHelper cparam;
    private final CisbanGenericSoftwareHelper cgs;
    private final CisbanIdentifiableHelper ci;
    private final CisbanHelper helper;

    public CisbanSoftwareHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cparam = new CisbanParameterizableHelper();
        this.cgs = new CisbanGenericSoftwareHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Software unmarshal( FugeOMCommonProtocolSoftwareType softwareXML, Software software )
            throws RealizableEntityServiceException {

        // determine what sort of software it is
        if ( softwareXML instanceof FugeOMCommonProtocolGenericSoftwareType ) {

            // Retrieve latest version from the database.
            GenericSoftware genericSoftware = ( GenericSoftware ) software;

            // get software attributes
            ci.unmarshal( softwareXML, genericSoftware );
            genericSoftware = ( GenericSoftware ) cparam.unmarshal( softwareXML, genericSoftware );

            if ( softwareXML.getVersion() != null ) {
                genericSoftware.setVersion( softwareXML.getVersion() );
            }

            // get generic software attributes
            genericSoftware = cgs.unmarshal(
                    ( FugeOMCommonProtocolGenericSoftwareType ) softwareXML, genericSoftware );

                if ( genericSoftware.getId() != null ) {
                    helper.assignAndLoadIdentifiable(
                            genericSoftware, "fugeOM.Common.Protocol.GenericSoftware", System.err );
                } else {
                    helper.loadIdentifiable( genericSoftware, "fugeOM.Common.Protocol.GenericSoftware", System.err );
                }

            return genericSoftware;
        }
        return null; // shouldn't get here as there is currently only one type of Software allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolSoftwareType marshal( FugeOMCommonProtocolSoftwareType softwareXML, Software software )
            throws RealizableEntityServiceException {

        // determine what sort of software it is
        if ( software instanceof GenericSoftware ) {

            // create fuge object
            FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML = new FugeOMCommonProtocolGenericSoftwareType();

            // get generic software attributes - doing it first will greedy get all appropriate objects before continuing.
            genericSoftwareXML = cgs.marshal( genericSoftwareXML, ( GenericSoftware ) software );

            // get software attributes
            genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) ci.marshal(
                    genericSoftwareXML, software );
            genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) cparam.marshal(
                    genericSoftwareXML, software );

            if ( software.getVersion() != null ) {
                genericSoftwareXML.setVersion( software.getVersion() );
            }

            return genericSoftwareXML;
        }
        return null; // shouldn't get here as there is currently only one type of Software allowed.
    }

    public FugeOMCommonProtocolSoftwareType generateRandomXML( FugeOMCommonProtocolSoftwareType softwareXML ) {

        softwareXML = ( FugeOMCommonProtocolSoftwareType ) ci.generateRandomXML( softwareXML );
        softwareXML.setVersion( String.valueOf( Math.random() ) );

        return softwareXML;
    }

    // at this stage, frXML may not have the new equipment and software - the protocol collection may be the only one to have it
    public FugeOMCommonProtocolSoftwareType generateRandomXMLWithLinksOut(
            FugeOMCommonProtocolSoftwareType softwareXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        softwareXML = generateRandomXML( softwareXML );

        // get software attributes
        softwareXML = ( FugeOMCommonProtocolSoftwareType ) cparam.generateRandomXMLWithLinksOut(
                softwareXML, frXML );

        // get generic software attributes
        softwareXML = cgs.generateRandomXMLWithLinksOut( ( FugeOMCommonProtocolGenericSoftwareType ) softwareXML,
                protocolCollectionXML, frXML );

        return softwareXML;
    }

    public Software getLatestVersion( Software software ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        software = ( Software ) reService.findLatestByEndurant( software.getEndurant().getIdentifier() );
        software = ( Software ) ci.getLatestVersion( software );

        // get software attributes
        software = ( Software ) cparam.getLatestVersion( software );

        // determine what sort of software it is
        if ( software instanceof GenericSoftware ) {
            // get generic software attributes
            software = cgs.getLatestVersion( ( GenericSoftware ) software );
        }

        return software;
    }
}
