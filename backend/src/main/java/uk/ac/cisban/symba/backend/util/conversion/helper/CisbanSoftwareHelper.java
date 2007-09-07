package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Protocol.GenericSoftware;
import fugeOM.Common.Protocol.Software;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericSoftwareType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolSoftwareType;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.net.URISyntaxException;

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
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanSoftwareHelper.java $
 *
 */

public class CisbanSoftwareHelper {
    private final CisbanParameterizableHelper cparam;
    private final CisbanGenericSoftwareHelper cgs;
    private final CisbanIdentifiableHelper ci;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanSoftwareHelper( RealizableEntityService reService, CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cparam = new CisbanParameterizableHelper( reService, ci.getCisbanDescribableHelper() );
        this.cgs = new CisbanGenericSoftwareHelper( reService, ci );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Software unmarshalSoftware( FugeOMCommonProtocolSoftwareType softwareXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // determine what sort of software it is
        if ( softwareXML instanceof FugeOMCommonProtocolGenericSoftwareType ) {

            // Retrieve latest version from the database.
            GenericSoftware genericSoftware = ( GenericSoftware ) helper.getOrCreateLatest(
                    softwareXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenSoftwareEndurant",
                    softwareXML.getName(),
                    "fugeOM.Common.Protocol.GenericSoftware",
                    System.err );

            // get software attributes
            ci.unmarshalIdentifiable( softwareXML, genericSoftware );
            genericSoftware = ( GenericSoftware ) cparam.unmarshalParameterizable( softwareXML, genericSoftware );

            if ( softwareXML.getVersion() != null ) {
                genericSoftware.setVersion( softwareXML.getVersion() );
            }

            // get generic software attributes
            genericSoftware = cgs.unmarshalGenericSoftware(
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
    public FugeOMCommonProtocolSoftwareType marshalSoftware( Software software )
            throws URISyntaxException, RealizableEntityServiceException {

        // determine what sort of software it is
        if ( software instanceof GenericSoftware ) {

            // create fuge object
            FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML = new FugeOMCommonProtocolGenericSoftwareType();

            // get generic software attributes - doing it first will greedy get all appropriate objects before continuing.
            genericSoftwareXML = cgs.marshalGenericSoftware( genericSoftwareXML, ( GenericSoftware ) software );

            // get software attributes
            genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) ci.marshalIdentifiable(
                    genericSoftwareXML, software );
            genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) cparam.marshalParameterizable(
                    genericSoftwareXML, software );

            if ( software.getVersion() != null ) {
                genericSoftwareXML.setVersion( software.getVersion() );
            }

            return genericSoftwareXML;
        }
        return null; // shouldn't get here as there is currently only one type of Software allowed.
    }

    // at this stage, frXML may not have the new equipment and software - the protocol collection may be the only one to have it
    public FugeOMCommonProtocolGenericSoftwareType generateRandomXML(
            FugeOMCommonProtocolGenericSoftwareType genericSoftwareXML,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        // get software attributes
        genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) ci.generateRandomXML( genericSoftwareXML );
        genericSoftwareXML = ( FugeOMCommonProtocolGenericSoftwareType ) cparam.generateRandomXML(
                genericSoftwareXML, frXML );

        genericSoftwareXML.setVersion( String.valueOf( Math.random() ) );

        // get generic software attributes
        genericSoftwareXML = cgs.generateRandomXML( genericSoftwareXML, protocolCollectionXML, frXML );

        return genericSoftwareXML;
    }

    public Software getLatestVersion( Software software ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        software = ( Software ) reService.findLatestByEndurant( software.getEndurant().getIdentifier() );
        software = ( Software ) ci.getLatestVersion( software );

        // get software attributes
        software = ( Software ) cparam.getLatestVersion( software, ci );

        // determine what sort of software it is
        if ( software instanceof GenericSoftware ) {
            // get generic software attributes
            software = cgs.getLatestVersion( ( GenericSoftware ) software );
        }

        return software;
    }
}
