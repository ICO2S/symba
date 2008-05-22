package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Collection.Provider;
import fugeOM.Common.Audit.ContactRole;
import fugeOM.Common.Protocol.Software;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProviderType;
import fugeOM.util.generatedJAXB2.FugeOMCommonAuditContactRoleType;
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

public class CisbanProviderHelper implements MappingHelper<Provider, FugeOMCollectionProviderType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanContactRoleHelper ccr;
    private final CisbanHelper helper;

    public CisbanProviderHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.ccr = new CisbanContactRoleHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Provider unmarshal( FugeOMCollectionProviderType providerXML, Provider provider )
            throws RealizableEntityServiceException {

        provider = ( Provider ) ci.unmarshal( providerXML, provider );

        // contact
        provider.setProvider( ccr.unmarshal( providerXML.getContactRole(), ( ContactRole ) reService.createDescribableOb( "fugeOM.Common.Audit.ContactRole" ) ) );

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
    public FugeOMCollectionProviderType marshal( FugeOMCollectionProviderType providerXML, Provider provider )
            throws RealizableEntityServiceException {

        providerXML = ( FugeOMCollectionProviderType ) ci.marshal( providerXML, provider );

        providerXML.setContactRole( ccr.marshal( new FugeOMCommonAuditContactRoleType(), provider.getProvider() ) );

        if ( provider.getProducingSoftware() != null ) {
            providerXML.setSoftwareRef( provider.getProducingSoftware().getIdentifier() );
        }

        return providerXML;
    }

    public FugeOMCollectionProviderType generateRandomXML( FugeOMCollectionProviderType providerXML ) {

        providerXML = ( FugeOMCollectionProviderType ) ci.generateRandomXML( providerXML );

        return providerXML;
    }

    public FugeOMCollectionFuGEType generateRandomXMLWithLinksOut( FugeOMCollectionFuGEType frXML ) {
        // create fuge object
        FugeOMCollectionProviderType providerXML = generateRandomXML( new FugeOMCollectionProviderType() );

        providerXML.setContactRole( ccr.generateRandomXMLwithLinksOut( frXML ) );

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
