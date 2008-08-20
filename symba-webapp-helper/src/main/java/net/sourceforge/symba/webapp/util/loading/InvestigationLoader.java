package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.bio.investigation.Investigation;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.InvestigationCollection;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
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
public class InvestigationLoader {

    // this method only applicable to new experiments
    public static FuGE addInvestigationToExperiment( FuGE fuge,
                                                     EntityService entityService,
                                                     Person auditor, SymbaFormSessionBean formSessionBean ) {
        InvestigationCollection investigationCollection = ( InvestigationCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.InvestigationCollection" );

        Set<Investigation> investigations = new HashSet<Investigation>();

        Set<Description> descriptions = new HashSet<Description>();
        Description description1 = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        Description description2 = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        if ( formSessionBean.getHypothesis() != null ) {
            description1.setText( "Hypothesis: " + formSessionBean.getHypothesis() );
        } else {
            description1.setText( "Hypothesis: None" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description1, auditor );

        descriptions.add( description1 );
        if ( formSessionBean.getConclusion() != null ) {
            description2.setText( "Conclusions: " + formSessionBean.getConclusion() );
        } else {
            description2.setText( "Conclusions: None" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description2, auditor );
        descriptions.add( description2 );

        // Create a new investigation in the database.
        Investigation investigation = ( Investigation ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.bio.investigation.Investigation", auditor );

        investigation.setName( "Investigation Details for " + formSessionBean.getExperimentName() );
        investigation.setDescriptions( descriptions );

        DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.investigation.Investigation", investigation, auditor );

        investigations.add( investigation );

        investigationCollection.setInvestigations( investigations );

        // load the fuge object into the database
        DatabaseObjectHelper
                .save( "net.sourceforge.fuge.collection.InvestigationCollection", investigationCollection, auditor );
        fuge.setInvestigationCollection( investigationCollection );

        return fuge;
    }

}