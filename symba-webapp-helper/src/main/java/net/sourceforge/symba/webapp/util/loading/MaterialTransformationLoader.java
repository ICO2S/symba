package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.protocol.GenericProtocolApplication;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;

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
 * $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp-helper/src/main/java/net/sourceforge/symba/webapp/util/loading/LoadFuge.java $
 */
public class MaterialTransformationLoader {
    private final EntityService entityService;
    private final SymbaEntityService symbaEntityService;

    private final PersonBean personBean;
    private SymbaFormSessionBean symbaFormSessionBean;
    private Person auditor;
    private SoftwareMetaInformationBean softwareMeta;

    /**
     * Creates a new instance of LoadFuge
     *
     * @param symbaFormSessionBean the bean that holds all information about the current upload
     * @param personBean           the bean that holds all information about the current user
     * @param softwareMeta         the bean that holds all information about the software used.
     */
    public MaterialTransformationLoader( SymbaFormSessionBean symbaFormSessionBean,
                                         PersonBean personBean,
                                         SoftwareMetaInformationBean softwareMeta ) {
        this.personBean = personBean;
        this.entityService = personBean.getEntityService();
        this.symbaEntityService = personBean.getSymbaEntityService();
        this.symbaFormSessionBean = symbaFormSessionBean;
        this.softwareMeta = softwareMeta;
    }

    // Returns the loaded FuGE object
    public FuGE load() {

        // first, we have to parse the auditor
        auditor = AuditLoader.setAuditor( symbaEntityService, personBean );

        FuGE fuge;
        if ( symbaFormSessionBean.getFuGE() == null ) {
            fuge = ( FuGE ) DatabaseObjectHelper
                    .createEndurantAndIdentifiable( "net.sourceforge.fuge.collection.FuGE", auditor );
            System.err.println( "New FuGE object created" );
            fuge = loadNew( fuge );
        } else {
            fuge = symbaFormSessionBean.getFuGE();
            System.err.println( "Used FuGE object loaded" );
            fuge = loadExisting( fuge );
        }

        fuge = FugeLoader.loadFugeIntoDatabase( fuge, auditor );
        return fuge;
    }

    private FuGE loadExisting( FuGE fuge ) {

        // When loading MTs, we know we are loading just one at a time. There is a chance that the user
        // has already got the exact same GPA in their FuGE experiment (for instance, if they have just pressed
        // "submit" in the specimen form without changing anything. We'll check for that. It's OK if it is already
        // present in the database, but not as their own GPA, as we would need to make a copy of that anyway.
        GenericProtocolApplication clonedGPA = ( GenericProtocolApplication ) symbaEntityService
                .getLatestByEndurant( symbaFormSessionBean.getSpecimenActionHierarchy() );
        if ( fuge.getProtocolCollection().getProtocolApplications().contains( clonedGPA ) ) {
            // this isn't a dummy, and it is already present. If its descriptor set and environmental conditions
            // still match the existing values, then we don't need to do anything - simply return without changing
            // the FuGE object at all.
            if ( ProtocolLoader.characteristicsMatch( symbaFormSessionBean.getSpecimenToBeUploaded(), clonedGPA ) ) {
                return fuge;
            }
        }

        // if we get here, we know we need to make a new GPA, and put it into the experiment.


        fuge = AuditLoader.addPersonToExperiment( fuge, entityService, auditor );
        Object[] results = MaterialLoader
                .addMaterialToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ( FuGE ) results[0];
        symbaFormSessionBean = ( SymbaFormSessionBean ) results[1];
        fuge = ProtocolLoader.loadMaterialTransformationProtocols( fuge, entityService, auditor,
                symbaFormSessionBean, symbaEntityService );

        return fuge;
    }

    private FuGE loadNew( FuGE fuge ) {

        fuge.setName( symbaFormSessionBean.getExperimentName() );
        System.err.println( "Name set" );
        fuge = AuditLoader.addPersonToExperiment( fuge, entityService, auditor );
        System.err.println( "Person added to Experiment" );
        fuge = InvestigationLoader.addInvestigationToExperiment( fuge, entityService, auditor, symbaFormSessionBean );
        System.err.println( "Investigation added to Experiment" );

        // Returns the fuge object in position 0, and the updated symbaFormSessionBean in position 1.
        Object[] results = MaterialLoader
                .addMaterialToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService );
        fuge = ( FuGE ) results[0];
        symbaFormSessionBean = ( SymbaFormSessionBean ) results[1];
        System.err.println( "Material added to Experiment" );
        fuge = ProtocolLoader.loadMaterialTransformationProtocols( fuge, entityService, auditor,
                symbaFormSessionBean, symbaEntityService );
        System.err.println( "MaterialTransformation added to Experiment" );
        fuge = ProviderLoader
                .addProviderToExperiment( fuge, entityService, auditor, symbaFormSessionBean, symbaEntityService,
                        softwareMeta );
        System.err.println( "Provider added to Experiment" );

        return fuge;
    }
}
