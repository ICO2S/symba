package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Collection.Provider;
import fugeOM.Common.Protocol.Software;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProviderType;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.net.URISyntaxException;

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

public class CisbanProviderHelper {
    private final CisbanIdentifiableHelper ci;
    private final CisbanContactRoleHelper ccr;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanProviderHelper( RealizableEntityService reService,
                                 CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.ccr = new CisbanContactRoleHelper( reService, ci.getCisbanDescribableHelper() );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Provider unmarshalProvider( FugeOMCollectionProviderType providerXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // Retrieve latest version from the database.
        Provider provider = ( Provider ) helper.getOrCreateLatest(
                providerXML.getEndurant(),
                "fugeOM.Collection.ProviderEndurant",
                providerXML.getName(),
                "fugeOM.Collection.Provider",
                System.err );

        provider = ( Provider ) ci.unmarshalIdentifiable( providerXML, provider );

        // contact
        provider.setProvider( ccr.unmarshalContactRole( providerXML.getContactRole() ) );

        // provider type
        if ( providerXML.getSoftwareRef() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            provider.setProducingSoftware( ( Software ) reService.findIdentifiable( providerXML.getSoftwareRef() ) );
        }

        if ( provider.getId() != null ) {
            helper.assignAndLoadIdentifiable( provider, "fugeOM.Collection.Provider", System.err );
        } else {
            helper.loadIdentifiable( provider, "fugeOM.Collection.Provider", System.err );
        }

        return provider;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionProviderType marshalProvider( Provider provider )
            throws URISyntaxException {

        // create fuge object
        FugeOMCollectionProviderType providerXML = new FugeOMCollectionProviderType();

        providerXML = ( FugeOMCollectionProviderType ) ci.marshalIdentifiable( providerXML, provider );

        providerXML.setContactRole( ccr.marshalContactRole( provider.getProvider() ) );

        if ( provider.getProducingSoftware() != null ) {
            providerXML.setSoftwareRef( provider.getProducingSoftware().getIdentifier() );
        }

        return providerXML;
    }

    public FugeOMCollectionFuGEType generateRandomXML( FugeOMCollectionFuGEType frXML ) {
        // create fuge object
        FugeOMCollectionProviderType providerXML = new FugeOMCollectionProviderType();

        providerXML = ( FugeOMCollectionProviderType ) ci.generateRandomXML( providerXML );

        providerXML.setContactRole( ccr.generateRandomXML( frXML, ci ) );

        if ( frXML.getProtocolCollection() != null ) {
            providerXML.setSoftwareRef(
                    frXML.getProtocolCollection().getSoftware().get( 0 ).getValue().getIdentifier() );
        }
        frXML.setProvider( providerXML );

        return frXML;
    }

    public Provider getLatestVersion( Provider provider ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        provider = ( Provider ) reService.findLatestByEndurant( provider.getEndurant().getIdentifier() );
        provider = ( Provider ) ci.getLatestVersion( provider );

        if ( provider.getProducingSoftware() != null ) {
            Software s = ( Software ) reService.findLatestByEndurant(
                    provider.getProducingSoftware().getEndurant().getIdentifier() );
            provider.setProducingSoftware( ( Software ) ci.getLatestVersion( s ) );
        }

        return provider;
    }
}
