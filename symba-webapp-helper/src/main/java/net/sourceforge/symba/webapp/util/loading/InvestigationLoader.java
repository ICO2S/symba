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

        Description hypothesis = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        Description conclusion = ( Description ) entityService.createDescribable(
                "net.sourceforge.fuge.common.description.Description" );
        if ( formSessionBean.getHypothesis() != null ) {
            hypothesis.setText( formSessionBean.getHypothesis() );
        } else {
            hypothesis.setText( "None supplied" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", hypothesis, auditor );

        if ( formSessionBean.getConclusion() != null ) {
            conclusion.setText( formSessionBean.getConclusion() );
        } else {
            conclusion.setText( "None supplied" );
        }
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", conclusion, auditor );

        // Create a new investigation in the database.
        Investigation investigation = ( Investigation ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.bio.investigation.Investigation", auditor );

        investigation.setName( "Investigation Details for " + formSessionBean.getExperimentName() );
        investigation.setHypothesis( hypothesis );
        investigation.setConclusion( conclusion );

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
