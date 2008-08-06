package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.audit.Person;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 */
public class OntologyLoader {

    /**
     * Loads a single new onotoly term to the database
     *
     * @param personBean                 the Person session bean
     * @param userEntry                  the name of the new ontology term
     * @param ontologySourceEndurantLSID the ontology source to associate with the new ontology term
     * @return String The ontologytermEndurants ID of the created DB-entry
     */

    public static String loadOnlyNewOntologytermToDB( PersonBean personBean,
                                                      String userEntry,
                                                      String ontologySourceEndurantLSID ) {

        Person performer = ( Person ) personBean.getEntityService().getIdentifiable( personBean.getLsid() );
        OntologyIndividual ontologyIndividual = ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                "net.sourceforge.fuge.common.ontology.OntologyIndividual",
                performer );

        ontologyIndividual.setName( userEntry );

        //js now find the ontologysource matching the known ontologysource endurants LSID
        OntologySource
                ontologySource =
                ( OntologySource ) personBean.getSymbaEntityService().getLatestByEndurant( ontologySourceEndurantLSID );
        ontologyIndividual.setOntologySource( ontologySource );
        ontologyIndividual.setTerm( userEntry );
        ontologyIndividual.setTermAccession( "Accession to come" );

        //js A check whether new ontology term is already in DB could be done here or earlier in metaDataValidate (with userdialog) to prevent redundant entries!
        //js There could additionally be a check for similarity to existing terms with a userdialog in metaDataValidate

        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, performer );

        return ontologyIndividual.getEndurant().getIdentifier();
    } //end loadOnlyNewOntologytermToDB()
}
