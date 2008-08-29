package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.collection.DataCollection;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.*;
import net.sourceforge.symba.webapp.util.storage.SyMBADataCopier;
import net.sourceforge.symba.webapp.util.storage.SyMBADataCopierFactory;

import java.io.IOException;
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
public class AssayLoader {
    private final EntityService entityService;
    private final SymbaEntityService symbaEntityService;

    private final ScpBean scpBean;
    private final PersonBean personBean;
    private SymbaFormSessionBean symbaFormSessionBean;
    private Person auditor;
    private SoftwareMetaInformationBean softwareMeta;

    /**
     * Creates a new instance of LoadFuge
     *
     * @param symbaFormSessionBean the bean that holds all information about the current upload
     * @param personBean           the bean that holds all information about the current user
     * @param scpBean              the bean that holds all information about where and how to secure copy the data files
     * @param softwareMeta         the bean that holds all software information about SyMBA itself
     */
    public AssayLoader( SymbaFormSessionBean symbaFormSessionBean,
                        PersonBean personBean,
                        ScpBean scpBean,
                        SoftwareMetaInformationBean softwareMeta ) {
        this.personBean = personBean;
        this.entityService = personBean.getEntityService();
        this.symbaEntityService = personBean.getSymbaEntityService();

        this.symbaFormSessionBean = symbaFormSessionBean;
        this.scpBean = scpBean;
        this.softwareMeta = softwareMeta;
    }

    public void load() throws IOException {

        // first, we have to parse the auditor
        auditor = AuditLoader.setAuditor( symbaEntityService, personBean );

        if ( symbaFormSessionBean.getFuGE() == null ) {
            loadNew();
        } else {
            loadExisting();
        }
    }

    private void loadExisting() throws IOException {

        FuGE fuge = symbaFormSessionBean.getFuGE();
        fuge = AuditLoader.addPersonToExperiment( fuge, entityService, auditor );
        Object[] results = MaterialLoader
                .addMaterialToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ( FuGE ) results[0];
        symbaFormSessionBean = ( SymbaFormSessionBean ) results[1];
        fuge = loadData( fuge );
        fuge = ProtocolLoader.loadAssayProtocols( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ReorganizeCollections.reorganize( fuge, auditor );
        FugeLoader.loadFugeIntoDatabase( fuge, auditor );
    }

    private void loadNew() throws IOException {

        FuGE fuge = ( FuGE ) DatabaseObjectHelper
                .createEndurantAndIdentifiable( "net.sourceforge.fuge.collection.FuGE", auditor );
        fuge.setName( symbaFormSessionBean.getExperimentName() );

        fuge = AuditLoader.addPersonToExperiment( fuge, entityService, auditor );
        fuge = InvestigationLoader.addInvestigationToExperiment( fuge, entityService, auditor, symbaFormSessionBean );
        Object[] results = MaterialLoader
                .addMaterialToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ( FuGE ) results[0];
        symbaFormSessionBean = ( SymbaFormSessionBean ) results[1];
        fuge = loadData( fuge );
        fuge = ProtocolLoader.loadAssayProtocols( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ProviderLoader
                .addProviderToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService,
                        softwareMeta );
        FugeLoader.loadFugeIntoDatabase( fuge, auditor );
    }

    private FuGE loadData( FuGE fuge ) throws IOException {

        DataCollection dataCollection = ( DataCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.DataCollection" );

        Set<Data> datas;
        // this line ensures that, if there is any data in the collection, that it is retained.
        if ( fuge.getDataCollection() != null && !fuge.getDataCollection().getAllData().isEmpty() ) {
            datas = ( Set<Data> ) fuge.getDataCollection().getAllData();
        } else {
            datas = new HashSet<Data>();
        }

        // we store data in a file store and use scpBean to get the information on where to put it
        SyMBADataCopier copier = SyMBADataCopierFactory.createSyMBADataCopier( "scp", scpBean.getHostname(),
                scpBean.getUsername(), scpBean.getPassword(), scpBean.getDirectory(), scpBean.getRemoteDataStoreOs() );

        int iii = 0;
        for ( DatafileSpecificMetadataStore rdib : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {

            // "cisban." just there to get removed.
            String fileLSID = DatabaseObjectHelper.getLsid( "cisban.RawData" );

            // Change the file LSID *just* for outputting, and just if it is DOS
            if ( scpBean.getRemoteDataStoreOs().equals( "dos" ) ) {
                fileLSID = LsidFilenameConverter.convert( fileLSID, scpBean.getLsidColonReplacement() );
            }

            if ( !copier.copy( rdib.getDataFile(), fileLSID, rdib.getFriendlyId() ) ) {
                // error making the copy
                throw new IOException();
            }

            // Create a new external data object.
            ExternalData externalData = ( ExternalData ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                    "net.sourceforge.fuge.bio.data.ExternalData", auditor );
            externalData.setName( rdib.getFriendlyId() );
            externalData.setLocation( fileLSID );

            // parse the description of the file, if present
            if ( rdib.getDataFileDescription() != null && rdib.getDataFileDescription().length() > 0 ) {
                // add any descriptions. You can see above we have just created a new externaldata object, so
                // no need to check for old descriptions.
                Set<Description> descriptions = new HashSet<Description>();
                Description description = ( Description ) entityService.createDescribable(
                        "net.sourceforge.fuge.common.description.Description" );
                description.setText( rdib.getDataFileDescription() );
                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.description.Description", description, auditor );
                descriptions.add( description );
                externalData.setDescriptions( descriptions );
            }

            // parse the file format, if it has one.
            if ( rdib.getFileFormat() != null ) {

                // we will be updating the ontology collection, therefore ensure that we start with everything
                // that was in it to begin with.
                OntologyCollection ontologyCollection = OntologyLoader.prepareOntologyCollection( fuge, entityService );

                // the pre-existing information has been copied. Now add to it in any way you need to
                Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();
                Set<OntologySource> ontologySources = ( Set<OntologySource> ) ontologyCollection.getSources();

                // todo proper algorithm
                boolean matchFound = LoaderHelper.findMatchingEndurant( rdib.getFileFormat(), ontologyTerms );
                // irrespective of whether or not we found a match, we still need to add the term to the new material
                OntologyTerm termToAdd =
                        ( OntologyTerm ) symbaEntityService.getLatestByEndurant( rdib.getFileFormat() );

                externalData.setFileFormat( termToAdd );

                // todo this won't catch cases where the ontology source was added at a later date
                if ( !matchFound ) {
                    // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                    // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                    ontologyTerms.add( termToAdd );
                    if ( termToAdd.getOntologySource() != null ) {
                        ontologySources.add(
                                ( OntologySource ) symbaEntityService.getLatestByEndurant(
                                        termToAdd.getOntologySource().getEndurant().getIdentifier() ) );
                    }
                    ontologyCollection.setOntologyTerms( ontologyTerms );
                    ontologyCollection.setSources( ontologySources );

                    // load the fuge object into the database
                    DatabaseObjectHelper
                            .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
                    fuge.setOntologyCollection( ontologyCollection );
                }
            }

            // load the external data into the database
            DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.data.ExternalData", externalData, auditor );

            rdib.setEndurantLsid( externalData.getEndurant().getIdentifier() );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( rdib, iii );

            datas.add( externalData );
            iii++;
        }

        dataCollection.setAllData( datas );

        if ( fuge.getDataCollection() != null ) {
            dataCollection.setAllDataPartitions( fuge.getDataCollection().getAllDataPartitions() );
            dataCollection.setAllDimensions( fuge.getDataCollection().getAllDimensions() );
            dataCollection.setHigherLevelAnalyses( fuge.getDataCollection().getHigherLevelAnalyses() );
        }

        // load the fuge object into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.DataCollection", dataCollection, auditor );
        fuge.setDataCollection( dataCollection );

        return fuge;
    }
}
