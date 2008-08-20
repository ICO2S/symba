package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.ontology.ObjectProperty;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.webapp.util.MaterialFactorsStore;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;
import net.sourceforge.symba.webapp.util.forms.schemes.material.CharacteristicScheme;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
public class OntologyLoader {

    /**
     * Loads a single new onotoly term to the database
     *
     * @param personBean                 the Person session bean
     * @param userEntry                  the name of the new ontology term
     * @param ontologySourceEndurantLSID the ontology source to associate with the new ontology term
     * @return String The ontologytermEndurants ID of the created DB-entry
     */

    public static OntologyIndividual loadOnlyNewOntologytermToDB( PersonBean personBean,
                                                                  String userEntry,
                                                                  String ontologySourceEndurantLSID ) {

        Person performer = ( Person ) personBean.getEntityService().getIdentifiable( personBean.getLsid() );
        OntologyIndividual ontologyIndividual =
                ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                        "net.sourceforge.fuge.common.ontology.OntologyIndividual",
                        performer );

        //js now find the ontologysource matching the known ontologysource endurants LSID
        OntologySource
                ontologySource =
                ( OntologySource ) personBean.getSymbaEntityService().getLatestByEndurant( ontologySourceEndurantLSID );

        ontologyIndividual.setName( userEntry );
        ontologyIndividual.setOntologySource( ontologySource );
        ontologyIndividual.setTerm( userEntry );
        ontologyIndividual.setTermAccession( DatabaseObjectHelper.getLsid( "RU_Accession" ) );

        //js A check whether new ontology term is already in DB could be done here or earlier in metaDataValidate (with userdialog) to prevent redundant entries!
        //js There could additionally be a check for similarity to existing terms with a userdialog in metaDataValidate

        DatabaseObjectHelper
                .save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, performer );

        return ontologyIndividual;
    }

    public static SymbaFormSessionBean validateLoadRequest( HttpServletRequest request,
                                                            PersonBean validUser,
                                                            SymbaFormSessionBean symbaFormSessionBean,
                                                            boolean isDuringAssayProcessing ) {

        String hiddennewterminfo = request.getParameter( "hiddennewterminfofield" );
        if ( hiddennewterminfo != null && hiddennewterminfo.length() > 0 ) {
            String[] tmpArr = hiddennewterminfo
                    .split( ":::" );//splits userentry and selection(=parametername) from second String, which then is to be split further by "::"
            String mixedTermInfo = tmpArr[0];
            String userEntry = tmpArr[1];
            // this equals the corresponding parametername in actual request (which has to be be ignored, only this one time)
//            String selectionName = tmpArr[2];

            if ( mixedTermInfo.startsWith( "ontologyTextfield::" ) ) {
                // after ontologyTextfield::characteristicScheme::ontologySourceEndurant::ontoCount
                CharacteristicScheme chs = new CharacteristicScheme();
                chs.parse( mixedTermInfo.substring( 19 ) );
                String[] parsed = mixedTermInfo.split( "::" );
                // second-to-last section
                String otseLSID = parsed[parsed.length - 2];

                //Now we have to insert the single new Term into the DB (method now returns some info rg created oterm):
                OntologyIndividual addedOI =
                        OntologyLoader.loadOnlyNewOntologytermToDB( validUser, userEntry, otseLSID );
                //symbaFormSessionBean, validUser , scp).loadOnlyNewOntologytermToDB(userEntry, otseLSID);

                //finally we parse a flag for a redirection back to metadata.jsp

                // Now remove preexisting user selections for this entry from just this data files' MaterialFactorStores...
                MaterialFactorsStore mf;
                if ( isDuringAssayProcessing ) {
                    mf = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( chs.getDatafileNumber() )
                            .getGenericProtocolApplicationInfo().get( chs.getParentOfGpaEndurant() )
                            .getInputCompleteMaterialFactors().get( chs.getMaterialCount() );
                } else {
                    mf = symbaFormSessionBean.getSpecimenToBeUploaded();
                }

                //note: selections of multiple selections will not be removed but kept and presented in addition to the new entry
                //...and fill Materialfactorstore instead with the newly created Ontology-Entry (which then will be marked as selected option instantly by the metadata.jsp):
                OntologySource ontologySource =
                        ( OntologySource ) validUser.getSymbaEntityService().getLatestByEndurant( otseLSID );
                boolean multipleSel = false;
                for ( Object o : ontologySource.getDescriptions() ) {
                    Description desc = ( Description ) o;
                    if ( desc.getText().contains( "please choose all that apply" ) ) { //then its a multiple selection
                        multipleSel = true;
                    }
                }
                if ( multipleSel ) { //adding to preexisting entries in respective HashSet:
                    LinkedHashSet<String> multipleCharacteristics;
                    if ( mf.getMultipleCharacteristics().get( otseLSID ) != null ) {
                        multipleCharacteristics = mf.getMultipleCharacteristics().get( otseLSID );
                    } else if ( mf.getNovelMultipleCharacteristics().get( otseLSID ) != null ) {
                        multipleCharacteristics = mf.getNovelMultipleCharacteristics().get( otseLSID );
                    } else {
                        multipleCharacteristics = new LinkedHashSet<String>();
                    }
                    // if it is a material transformation, only the term and termaccession are stored, so in
                    // this value, store the term rather than the ontologytermendurant.
                    if ( !chs.isNovel() ) {
                        multipleCharacteristics.add( addedOI.getEndurant().getIdentifier() );
                        mf.addMultipleCharacteristics( otseLSID, multipleCharacteristics );
                    } else {
                        multipleCharacteristics.add( addedOI.getTerm() + "::" + addedOI.getTermAccession() );
                        mf.addNovelMultipleCharacteristics( otseLSID, multipleCharacteristics );
                    }
                } else { // if single selection field:
                    // if it is a material transformation, only the term and termaccession are stored, so in
                    // this value, store the term rather than the ontologytermendurant.
                    if ( !chs.isNovel() ) {
                        mf.addCharacteristic( otseLSID, addedOI.getEndurant().getIdentifier() );
                    } else {
                        mf.addNovelCharacteristic( otseLSID, addedOI.getTerm() + "::" + addedOI.getTermAccession() );
                    }
                }

                if ( isDuringAssayProcessing ) {
                    symbaFormSessionBean.getDatafileSpecificMetadataStores()
                            .get( chs.getDatafileNumber() ).getGenericProtocolApplicationInfo()
                            .get( chs.getParentOfGpaEndurant() )
                            .setInputCompleteMaterialFactor( mf, chs.getMaterialCount() );
                } else {
                    symbaFormSessionBean.setSpecimenToBeUploaded( mf );
                }
            }
        }

        return symbaFormSessionBean;
    }

    public static OntologyCollection prepareOntologyCollection( FuGE fuge, EntityService entityService ) {

        OntologyCollection ontologyCollection = ( OntologyCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.OntologyCollection" );

        Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
        Set<OntologySource> ontologySources = new HashSet<OntologySource>();

        if ( fuge.getOntologyCollection() != null ) {
            if ( fuge.getOntologyCollection().getOntologyTerms() != null ) {
                ontologyTerms = ( Set<OntologyTerm> ) fuge.getOntologyCollection().getOntologyTerms();
            }
            if ( fuge.getOntologyCollection().getSources() != null ) {
                ontologySources = ( Set<OntologySource> ) fuge.getOntologyCollection().getSources();
            }
        }

        ontologyCollection.setOntologyTerms( ontologyTerms );
        ontologyCollection.setSources( ontologySources );

        return ontologyCollection;
    }

    /**
     * Checks using source, term and accession to see if the specified object property has the individual described.
     * All three must be found in order for it to be a real match
     *
     * @param objectProperty the thing to search inside
     * @param sourceEndurant the source endurant identifier to search for
     * @param term           the term to search for
     * @param accession      the term accession to search for
     * @return true if the individual is present within the ObjectProperty, false otherwise
     */
    public static boolean objectPropertyhasIndividual( ObjectProperty objectProperty,
                                                       String sourceEndurant,
                                                       String term,
                                                       String accession ) {

        for ( OntologyIndividual ontologyIndividual : objectProperty.getContent() ) {
            if ( ontologyIndividual.getTerm().equals( term ) &&
                 ontologyIndividual.getTermAccession().equals( accession ) &&
                 ontologyIndividual.getOntologySource().getEndurant().getIdentifier().equals( sourceEndurant ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks using term and accession to see if the specified object property has the individual described.
     * Both must be found in order for it to be a real match. If you know the source, you should use the other
     * version of this method that also checks the source.
     *
     * @param objectProperty the thing to search inside
     * @param term           the term to search for
     * @param accession      the term accession to search for
     * @return true if the individual is present within the ObjectProperty, false otherwise
     */
    public static boolean objectPropertyhasIndividual( ObjectProperty objectProperty, String term, String accession ) {
        for ( OntologyIndividual ontologyIndividual : objectProperty.getContent() ) {
            if ( ontologyIndividual.getTerm().equals( term ) &&
                 ontologyIndividual.getTermAccession().equals( accession ) ) {
                return true;
            }
        }
        return false;
    }
}
