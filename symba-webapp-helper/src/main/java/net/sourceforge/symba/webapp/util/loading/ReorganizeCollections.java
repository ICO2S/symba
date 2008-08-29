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
 */
public class ReorganizeCollections {

    // BE CAREFUL with this method! It will create objects in the database, and overwrite ALL the
    // describable "*Collection"'s that have a database id already in your FuGE object
    // (it will retain all existing components of those objects though). Should be run if loading a new version
    // of a fuge object. Setting a database id to null will cause a new line in the database to be made.
    //
    // From Andy August 2008: "Looking at the cardinalities within the model, the Collection objects are supposed to
    // be unique to a FuGE instance, but the objects within each collection can be shared. This means that if you
    // create a new FuGE object by default you have to create new collection classes."

    // deliberately does not copy any describable information of each collection themselves to the new collection.
    // todo they should all have a database id by this stage, as they will have all been saved in the database. need a better check so don't always overwrite new collections!
    public static FuGE reorganize( FuGE fuge, Person performer ) {

        if ( fuge.getAuditCollection() != null && fuge.getAuditCollection().getId() != null ) {
            AuditCollection collection = fuge.getAuditCollection();
            collection.setId( null );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.AuditCollection", collection, performer );
            fuge.setAuditCollection( collection );
        }
        if ( fuge.getConceptualMoleculeCollection() != null &&
             fuge.getConceptualMoleculeCollection().getId() != null ) {
            ConceptualMoleculeCollection collection = fuge.getConceptualMoleculeCollection();
            collection.setId( null );
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.ConceptualMoleculeCollection", collection, performer );
            fuge.setConceptualMoleculeCollection( collection );
        }
        if ( fuge.getDataCollection() != null && fuge.getDataCollection().getId() != null ) {
            DataCollection collection = fuge.getDataCollection();
            collection.setId( null );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.DataCollection", collection, performer );
            fuge.setDataCollection( collection );
        }
        if ( fuge.getInvestigationCollection() != null && fuge.getInvestigationCollection().getId() != null ) {
            InvestigationCollection collection = fuge.getInvestigationCollection();
            collection.setId( null );
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.InvestigationCollection", collection, performer );
            fuge.setInvestigationCollection( collection );
        }
        if ( fuge.getMaterialCollection() != null && fuge.getMaterialCollection().getId() != null ) {
            MaterialCollection collection = fuge.getMaterialCollection();
            collection.setId( null );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.MaterialCollection", collection, performer );
            fuge.setMaterialCollection( collection );
        }
        if ( fuge.getOntologyCollection() != null && fuge.getOntologyCollection().getId() != null ) {
            OntologyCollection collection = fuge.getOntologyCollection();
            collection.setId( null );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.OntologyCollection", collection, performer );
            fuge.setOntologyCollection( collection );
        }
        if ( fuge.getProtocolCollection() != null && fuge.getProtocolCollection().getId() != null ) {
            ProtocolCollection collection = fuge.getProtocolCollection();
            collection.setId( null );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.ProtocolCollection", collection, performer );
            fuge.setProtocolCollection( collection );
        }
        if ( fuge.getReferenceableCollection() != null && fuge.getReferenceableCollection().getId() != null ) {
            ReferenceableCollection collection = fuge.getReferenceableCollection();
            collection.setId( null );
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.ReferenceableCollection", collection, performer );
            fuge.setReferenceableCollection( collection );
        }

        return fuge;
    }

}
