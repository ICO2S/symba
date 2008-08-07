package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.*;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * should be removed in future versions, as it is an unhealthy reorganization.
 */
public class ReorganizeCollections {

    // BE CAREFUL with this method! It will create objects in the database, and overwrite ALL the
    // describable "*Collection"'s in your FuGE object (it will retain all existing components of those
    // objects though).
    // todo does not copy any describable information of each collection themselves to the new collection.
    public static FuGE reorganizeCollections( FuGE fuge, EntityService entityService, Person performer ) {

        if ( fuge.getAuditCollection() != null ) {
            AuditCollection collection = ( AuditCollection ) entityService
                    .createDescribable( "net.sourceforge.fuge.Collection.AuditCollection" );
            collection.setAllContacts( fuge.getAuditCollection().getAllContacts() );
            collection.setSecurityCollection( fuge.getAuditCollection().getSecurityCollection() );
            collection.setSecurityGroups( fuge.getAuditCollection().getSecurityGroups() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.AuditCollection", collection, performer );
            fuge.setAuditCollection( collection );
        }

        if ( fuge.getOntologyCollection() != null ) {
            OntologyCollection collection = ( OntologyCollection ) entityService
                    .createDescribable( "net.sourceforge.fuge.Collection.OntologyCollection" );
            collection.setSources( fuge.getOntologyCollection().getSources() );
            collection.setOntologyTerms( fuge.getOntologyCollection().getOntologyTerms() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.OntologyCollection", collection, performer );
            fuge.setOntologyCollection( collection );
        }

        if ( fuge.getReferenceableCollection() != null ) {
            ReferenceableCollection collection = ( ReferenceableCollection ) entityService.createDescribable(
                    "net.sourceforge.fuge.Collection.ReferenceableCollection" );
            collection.setAllDatabases( fuge.getReferenceableCollection().getAllDatabases() );
            collection.setAllBibliographicReferences( fuge.getReferenceableCollection().getAllBibliographicReferences() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.ReferenceableCollection", collection, performer );
            fuge.setReferenceableCollection( collection );
        }

        if ( fuge.getMaterialCollection() != null ) {
            MaterialCollection collection = ( MaterialCollection ) entityService.createDescribable(
                    "net.sourceforge.fuge.Collection.MaterialCollection" );
            collection.setMaterials( fuge.getMaterialCollection().getMaterials() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.MaterialCollection", collection, performer );
            fuge.setMaterialCollection( collection );
        }

        if ( fuge.getDataCollection() != null ) {
            DataCollection collection = ( DataCollection ) entityService.createDescribable(
                    "net.sourceforge.fuge.Collection.DataCollection" );
            collection.setAllData( fuge.getDataCollection().getAllData() );
            collection.setAllDataPartitions( fuge.getDataCollection().getAllDataPartitions() );
            collection.setAllDimensions( fuge.getDataCollection().getAllDimensions() );
            collection.setHigherLevelAnalyses( fuge.getDataCollection().getHigherLevelAnalyses() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.DataCollection", collection, performer );
            fuge.setDataCollection( collection );
        }

        if ( fuge.getProtocolCollection() != null ) {
            ProtocolCollection collection = ( ProtocolCollection ) entityService.createDescribable(
                    "net.sourceforge.fuge.Collection.ProtocolCollection" );
            collection.setAllEquipment( fuge.getProtocolCollection().getAllEquipment() );
            collection.setProtocolApplications( fuge.getProtocolCollection().getProtocolApplications() );
            collection.setAllSoftwares( fuge.getProtocolCollection().getAllSoftwares() );
            collection.setProtocols( fuge.getProtocolCollection().getProtocols() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.ProtocolCollection", collection, performer );
            fuge.setProtocolCollection( collection );
        }

        if ( fuge.getInvestigationCollection() != null ) {
            InvestigationCollection collection = ( InvestigationCollection ) entityService.createDescribable(
                    "net.sourceforge.fuge.Collection.InvestigationCollection" );
            collection.setInvestigations( fuge.getInvestigationCollection().getInvestigations() );
            collection.setFactorCollection( fuge.getInvestigationCollection().getFactorCollection() );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.Collection.InvestigationCollection", collection, performer );
            fuge.setInvestigationCollection( collection );
        }
        return fuge;
    }

}
