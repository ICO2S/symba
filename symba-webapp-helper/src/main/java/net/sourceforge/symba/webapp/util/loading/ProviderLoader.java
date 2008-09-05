package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.collection.Provider;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.protocol.GenericSoftware;
import net.sourceforge.fuge.common.protocol.Software;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;

import java.util.HashSet;
import java.util.Set;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class ProviderLoader {

    public static FuGE addProviderToExperiment( FuGE fuge,
                                                EntityService entityService,
                                                Person auditor,
                                                SymbaFormSessionBean formSessionBean,
                                                SymbaEntityService symbaEntityService,
                                                SoftwareMetaInformationBean softwareMeta ) {

        // todo code for allowing multiple people to edit a single experiment and have multiple providers.
        // We will assign the auditor to be the Provider. The link between the Provider
        // and the Person is called a ContactRole.

        // first, create the role
        ContactRole contactRole =
                ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" );
        contactRole.setContact( auditor );

        // the provider contains an ontology term, so we need to add that term to the experiment
        OntologyCollection ontologyCollection = OntologyLoader.prepareOntologyCollection( fuge, entityService );

        // the pre-existing information has been copied. Now add to it in any way you need to
        Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();

        // then create an ontology term to identify the role, or load the most recent version of that term
        String providerAccession = "symba.sourceforge.net#PrincipalInvestigator";
        OntologyIndividual ontologyIndividual = symbaEntityService.getOntologyIndividualByAccession(
                providerAccession );

        if ( ontologyIndividual == null ) {
            ontologyIndividual = ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.common.ontology.OntologyIndividual", auditor );
            ontologyIndividual.setName( "net.sourceforge.symba.PrincipalInvestigator" );
            ontologyIndividual.setTerm( "Principal Investigator" );
            ontologyIndividual.setTermAccession( "net.sourceforge.symba.PrincipalInvestigator" );

            // and then load that term into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, auditor );
        }

        if ( !ontologyTerms.contains( ontologyIndividual ) ) {
            // add the ontology term to the list of terms.
            ontologyTerms.add( ontologyIndividual );

            // then parse that ontology term in the role
            contactRole.setRole( ontologyIndividual );
            ontologyCollection.setOntologyTerms( ontologyTerms );

            // load the fuge object into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
            fuge.setOntologyCollection( ontologyCollection );
        }

        //  load the role into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.ContactRole", contactRole, auditor );

        Provider provider = ( Provider ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.collection.Provider", auditor );

        // parse any part of the provider that we are interested in.
        provider.setName( "Main Provider of Experiment: " + formSessionBean.getExperimentName() );
        provider.setProvider( contactRole );

        // the provider contains a reference to the producing software, so we need to add that to the experiment
        // the pre-existing information has been copied. Now add to it in any way you need to
        Set<Software> softwares;
        if ( fuge.getProtocolCollection() != null && fuge.getProtocolCollection().getAllSoftwares() != null ) {
            softwares = ( Set<Software> ) fuge.getProtocolCollection().getAllSoftwares();
        } else {
            softwares = new HashSet<Software>();
        }
        GenericSoftware producingSoftware = symbaEntityService
                .getGenericSoftwareByNameAndVersion( softwareMeta.getName(), softwareMeta.getVersion() );

        if ( producingSoftware == null ) {
            // create and save the producing software in the database
            producingSoftware = ( GenericSoftware ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.common.protocol.GenericSoftware", auditor );
            producingSoftware.setName( softwareMeta.getName() );
            producingSoftware.setVersion( softwareMeta.getVersion() );

            // and then load that term into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.common.protocol.GenericSoftware", producingSoftware, auditor );

        }

        if ( !softwares.contains( producingSoftware ) ) {

            ProtocolCollection protocolCollection = ProtocolLoader.prepareProtocolCollection( fuge, entityService );

            // add the software to the list of software.
            softwares.add( producingSoftware );
            protocolCollection.setAllSoftwares( softwares );

            // load the fuge object into the database
            protocolCollection = (ProtocolCollection) DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.ProtocolCollection", protocolCollection, auditor );
            fuge.setProtocolCollection( protocolCollection );
        }

        provider.setProducingSoftware( producingSoftware );

        // load the provider into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.Provider", provider, auditor );
        // add the role to the provider
        fuge.setProvider( provider );

        return fuge;
    }

}
