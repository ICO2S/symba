package net.sourceforge.symba.webapp.util.loading;

import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.GenericMaterialMeasurement;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.MaterialCollection;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.ontology.*;
import net.sourceforge.fuge.common.protocol.GenericProtocolApplication;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.symba.service.SymbaEntityService;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.MaterialFactorsStore;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;

import java.util.*;

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
public class MaterialLoader {

    /**
     * @param fuge                 the object to add experimental info to
     * @param entityService        connection to database
     * @param auditor              person to assign to all the changes in the db
     * @param symbaFormSessionBean session info, also updated in this method
     * @param symbaEntityService   connection to database
     * @return An object array of FuGE and symbaFormSessionBean. This is really silly, but want to
     *         abstract this code out of the main method, and it is changing two things. Cannot figure out another way
     *         to do this properly.
     */
    public static Object[] addMaterialToExperiment( FuGE fuge,
                                                    EntityService entityService,
                                                    Person auditor,
                                                    SymbaFormSessionBean symbaFormSessionBean,
                                                    SymbaEntityService symbaEntityService ) {

        OntologyCollection ontologyCollection = OntologyLoader.prepareOntologyCollection( fuge, entityService );

        Set<OntologyTerm> ontologyTerms = ( Set<OntologyTerm> ) ontologyCollection.getOntologyTerms();
        Set<OntologySource> ontologySources = ( Set<OntologySource> ) ontologyCollection.getSources();

        MaterialCollection materialCollection = ( MaterialCollection ) entityService
                .createDescribable( "net.sourceforge.fuge.collection.MaterialCollection" );

        Set<Material> materials;
        // this line ensures that, if there is any material in the collection, that it is retained.
        if ( fuge.getMaterialCollection() != null && !fuge.getMaterialCollection().getMaterials().isEmpty() ) {
            materials = ( Set<Material> ) fuge.getMaterialCollection().getMaterials();
        } else {
            materials = new HashSet<Material>();
        }

        // each rdib instance has its own parse of input materials.
        boolean hasMaterial = false;

        if ( symbaFormSessionBean.getDatafileSpecificMetadataStores() != null &&
             !symbaFormSessionBean.getDatafileSpecificMetadataStores().isEmpty() ) {
            int dataFileNumber = 0;
            int materialNumber = 0;
            for ( DatafileSpecificMetadataStore rdib : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
                if ( rdib.getGenericProtocolApplicationInfo() != null ) {
                    for ( String currentKey : rdib.getGenericProtocolApplicationInfo().keySet() ) {
                        for ( MaterialFactorsStore mfs : rdib.getGenericProtocolApplicationInfo().get( currentKey )
                                .getInputCompleteMaterialFactors() ) {

                            // only load the material if it hasn't already been created, e.g. via a pre-existing
                            // material transformation
                            if ( mfs.getCreatedMaterialEndurant() != null &&
                                 mfs.getCreatedMaterialEndurant().length() > 0 ) {
                                hasMaterial = true;

                                Object[] objects = loadSingleMaterial( mfs, ontologyTerms, ontologySources,
                                        auditor, entityService, symbaEntityService, false );
                                GenericMaterial genericMaterial = ( GenericMaterial ) objects[0];
                                ontologyTerms = ( Set<OntologyTerm> ) objects[1];
                                ontologySources = ( Set<OntologySource> ) objects[2];
                                genericMaterial = ( GenericMaterial ) DatabaseObjectHelper.save(
                                        "net.sourceforge.fuge.bio.material.GenericMaterial", genericMaterial, auditor );

                                // a final step for this rdib genericMaterial info: add the genericMaterial to the list
                                // of materials after storing
                                materials.add( genericMaterial );
                                mfs.setCreatedMaterialEndurant( genericMaterial.getEndurant().getIdentifier() );
                                rdib.getGenericProtocolApplicationInfo().get( currentKey )
                                        .setInputCompleteMaterialFactor( mfs, materialNumber );
                                symbaFormSessionBean.setDatafileSpecificMetadataStore( rdib, dataFileNumber );
                            }
                            materialNumber++;
                        }
                    }
                    dataFileNumber++;
                }
            }
        } else if ( symbaFormSessionBean.getSpecimenToBeUploaded() != null ) {
            // We could be loading materials from the specimen form
            hasMaterial = true;

            // There will not be an exact match of the entire MT, or we wouldn't be loading the FuGE object at all.
            // However, we know that the descriptor set of the input and output materials will be identical.
            // Start out by checking to see if the input material (with a matching descriptor set) is already
            // in the database. If it is, then we take the corresponding output material as a base, modify it
            // to match the actual experimental conditions, and make just a new output material. Otherwise, we
            // make both an input and output material.

            // Get all currently-existing pairs. Allow the user to choose any of these pairs to copy
            // and modify. These pairs are identified by retrieving all non-dummy material transformation GPAs.
            @SuppressWarnings( "unchecked" )
            List<GenericProtocolApplication> materialTransformations =
                    ( List<GenericProtocolApplication> ) symbaEntityService.getLatestMaterialTransformations();

            Material input = getMatchingInputMaterial( materialTransformations,
                    symbaFormSessionBean.getSpecimenToBeUploaded().getNovelCharacteristics(),
                    symbaFormSessionBean.getSpecimenToBeUploaded().getNovelMultipleCharacteristics(),
                    symbaEntityService );

            if ( input != null ) {
                // re-use the contents of the existing input specimen except for the name, which the user has supplied.
                // if there is no user-supplied name, provide a basic one.
                String name = "Specimen Prior to Measuring of Experimental Conditions";
                if ( symbaFormSessionBean.getSpecimenToBeUploaded().getMaterialName() != null &&
                     symbaFormSessionBean.getSpecimenToBeUploaded().getMaterialName().length() > 0 ) {
                    name = symbaFormSessionBean.getSpecimenToBeUploaded().getMaterialName();
                }
                input.setName( name );
                // now give it a new endurant
                input.setEndurant( DatabaseObjectHelper.getOrLoadEndurant( null, auditor ) );
                // and a new identifier
                input = ( Material ) DatabaseObjectHelper
                        .assignAndSave( "net.sourceforge.fuge.bio.material.GenericMaterial", input, auditor );
                materials.add( input );
            } else {
                // make new input material
                Object[] objects = loadSingleMaterial( symbaFormSessionBean.getSpecimenToBeUploaded(), ontologyTerms,
                        ontologySources, auditor, entityService, symbaEntityService, true );
                input = ( GenericMaterial ) objects[0];
                input = ( GenericMaterial ) DatabaseObjectHelper.save(
                        "net.sourceforge.fuge.bio.material.GenericMaterial", input, auditor );
                ontologyTerms = ( Set<OntologyTerm> ) objects[1];
                ontologySources = ( Set<OntologySource> ) objects[2];
                materials.add( input );
            }
            // always make new output material
            Object[] objects = loadSingleMaterial( symbaFormSessionBean.getSpecimenToBeUploaded(), ontologyTerms,
                    ontologySources, auditor, entityService, symbaEntityService, false );
            GenericMaterial outputMaterial = ( GenericMaterial ) objects[0];
            // everything except the descriptor set will have been added. Copy that directly from the input material
            OntologyIndividual descriptorSet = ( OntologyIndividual ) input.getCharacteristics().iterator().next();
            outputMaterial.getCharacteristics().add( descriptorSet );
            outputMaterial = ( GenericMaterial ) DatabaseObjectHelper.save(
                    "net.sourceforge.fuge.bio.material.GenericMaterial", outputMaterial, auditor );
            symbaFormSessionBean.getSpecimenToBeUploaded().setCreatedMaterialEndurant(
                    input.getEndurant().getIdentifier() + "::" + outputMaterial.getEndurant().getIdentifier() );
            ontologyTerms = ( Set<OntologyTerm> ) objects[1];
            ontologySources = ( Set<OntologySource> ) objects[2];
            materials.add( outputMaterial );
        }

        // if there has been at least one material, then we need to load the updated ontology terms into the ontology
        // collection
        if ( hasMaterial ) {
            ontologyCollection.setOntologyTerms( ontologyTerms );
            ontologyCollection.setSources( ontologySources );

            // load the fuge object into the database
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, auditor );
            fuge.setOntologyCollection( ontologyCollection );

            // load the collection object into the database if we've added materials
            materialCollection.setMaterials( materials );
            DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.collection.MaterialCollection", materialCollection, auditor );
            fuge.setMaterialCollection( materialCollection );
        }

        return new Object[]{ fuge, symbaFormSessionBean };
    }

    // input materials only have novel charactreristics within a single descriptor set currently.
    private static Material getMatchingInputMaterial( List<GenericProtocolApplication> materialTransformations,
                                                      HashMap<String, String> novelCharacteristics,
                                                      HashMap<String, LinkedHashSet<String>> novelMultipleCharacteristics,
                                                      SymbaEntityService symbaEntityService ) {

        // all characteristics must be present in the single descriptor set of the input materials from the database.
        List<OntologyIndividual> novels = new ArrayList<OntologyIndividual>();
        for ( String key : novelCharacteristics.keySet() ) {
            novels.add(
                    ( OntologyIndividual ) symbaEntityService.getLatestByEndurant( novelCharacteristics.get( key ) ) );
        }
        // also add all the muliple characteristics
        for ( String key : novelMultipleCharacteristics.keySet() ) {
            for ( String single : novelMultipleCharacteristics.get( key ) ) {
                novels.add( ( OntologyIndividual ) symbaEntityService.getLatestByEndurant( single ) );
            }
        }

        // there MUST be a match for each novel individual in both term and term accession
        for ( GenericProtocolApplication materialTransformation : materialTransformations ) {
            for ( GenericMaterialMeasurement measurement : materialTransformation.getInputMaterials() ) {
                Material input = measurement.getMeasuredMaterial();
                if ( input.getCharacteristics().size() == 1 ) {
                    // only interested if there is a single descriptor set
                    OntologyTerm ot = input.getCharacteristics().iterator().next();
                    if ( ot instanceof OntologyIndividual ) {
                        OntologyIndividual oi = ( OntologyIndividual ) ot;
                        // the number of properties must match the number of novel characteristics
                        if ( oi.getProperties().size() == novelCharacteristics.size() ) {
                            boolean partialMatchFound = false;
                            for ( OntologyProperty ontProp : oi.getProperties() ) {
                                partialMatchFound = false;
                                // a descriptor set must have properties which are ObjectProperties, and which each
                                // contain one or more OntologyIndividuals
                                if ( ontProp instanceof ObjectProperty ) {
                                    ObjectProperty op = ( ObjectProperty ) ontProp;
                                    for ( OntologyIndividual inner : op.getContent() ) {
                                        partialMatchFound = false;
                                        for ( OntologyIndividual novel : novels ) {
                                            if ( inner.getOntologySource().getEndurant().getIdentifier().equals(
                                                    novel.getOntologySource().getEndurant().getIdentifier() ) && inner
                                                    .getTerm().equals( novel.getTerm() ) && inner.getTermAccession()
                                                    .equals( novel.getTermAccession() ) ) {
                                                partialMatchFound = true;
                                                break;
                                            }
                                        }
                                        // each instance of the op.getContent() loop must have partialMatchFound = true
                                        if ( !partialMatchFound ) {
                                            // otherwise, one of the properties in the input material doesn't match, and
                                            // we need to try the next material via the next if-statement
                                            break;
                                        }
                                    }
                                }
                                // each instance of the oi.getProperties() loop must have partialMatchFound = true
                                if ( !partialMatchFound ) {
                                    // otherwise, one of the properties in the input material doesn't match, and
                                    // we need to try the next material
                                    break;
                                }
                            }
                            // we're out of the loop. If partialMatchFound remains true, it means there is a complete
                            // match. return the winning material. Otherwise, try the next material
                            if ( partialMatchFound ) {
                                return input;
                            }
                        }
                    }
                }
            }
        }
        // no match found - return null
        return null;
    }

    /**
     * @param mfs                holds the session information to use to create the material
     * @param ontologyTerms      holds the list of terms to add to
     * @param ontologySources    holds the list of sources to add to
     * @param auditor            the person to assign the creation of the objects to in the database
     * @param entityService      the fuge connection to the database
     * @param symbaEntityService the symba connection to the database
     * @param isInputToMaterialTransformation
     *                           if true, only uses the novel*Characteristics values, and not the regular ones
     * @return An object array of GenericMaterial, ontologyTerms, ontologySource. This is really silly, but want to
     *         abstract this code out of the main method, and it is changing three things. Cannot figure out another way
     *         to do this properly.
     */
    private static Object[] loadSingleMaterial( MaterialFactorsStore mfs,
                                                Set<OntologyTerm> ontologyTerms,
                                                Set<OntologySource> ontologySources,
                                                Person auditor,
                                                EntityService entityService,
                                                SymbaEntityService symbaEntityService,
                                                boolean isInputToMaterialTransformation ) {

        // the material needs to be made, and each ontology term needs to be added if it hasn't already been added
        String nameToUse = "";
        if ( mfs.getMaterialName() != null &&
             mfs.getMaterialName().length() > 0 ) {
            nameToUse = mfs.getMaterialName();
        }

        GenericMaterial genericMaterial =
                ( GenericMaterial ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                        "net.sourceforge.fuge.bio.material.GenericMaterial", auditor );
        genericMaterial.setName( nameToUse );

        if ( !isInputToMaterialTransformation ) {

            // add any OntologyReplacement descriptions.
            if ( mfs.getOntologyReplacements() != null &&
                 mfs.getOntologyReplacements().size() > 0 ) {
                Set<Description> descriptions;
                if ( genericMaterial.getDescriptions() == null ) {
                    descriptions = new HashSet<Description>();
                } else {
                    descriptions = ( Set<Description> ) genericMaterial.getDescriptions();
                }
                for ( String key : mfs.getOntologyReplacements().keySet() ) {
                    Description description = ( Description ) entityService.createDescribable(
                            "net.sourceforge.fuge.common.description.Description" );
                    description.setText(
                            key + " = " + mfs.getOntologyReplacements().get( key ) );
                    DatabaseObjectHelper
                            .save( "net.sourceforge.fuge.common.description.Description", description,
                                    auditor );
                    descriptions.add( description );
                }
                genericMaterial.setDescriptions( descriptions );
            }

            if ( mfs.getMaterialType() != null ) {
                // The first ontology term is the materialType

                // todo proper algorithm
                boolean matchFound = LoaderHelper.findMatchingEndurant(
                        mfs.getMaterialType(), ontologyTerms );
                // irrespective of whether or not we found a match, we still need to add the term to the new material
                OntologyTerm termToAdd = ( OntologyTerm ) symbaEntityService
                        .getLatestByEndurant( mfs.getMaterialType() );
                genericMaterial.setMaterialType( termToAdd );
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
                }
            }

            if ( mfs.getCharacteristics() != null ) {
                // Now we have the characteristics to load.
                Set<OntologyTerm> characteristics;
                if ( genericMaterial.getCharacteristics() == null ) {
                    characteristics = new HashSet<OntologyTerm>();
                } else {
                    characteristics = ( Set<OntologyTerm> ) genericMaterial.getCharacteristics();
                }
                for ( String ontologySourceEndurant : mfs.getCharacteristics()
                        .keySet() ) {
                    String ontologyTermEndurant =
                            mfs.getCharacteristics().get( ontologySourceEndurant );
                    // todo proper algorithm
                    boolean matchFound = LoaderHelper.findMatchingEndurant( ontologyTermEndurant, ontologyTerms );
                    // irrespective of whether or not we found a match, we still need to add the term to the new material
                    OntologyTerm termToAdd =
                            ( OntologyTerm ) symbaEntityService
                                    .getLatestByEndurant( ontologyTermEndurant );
                    characteristics.add( termToAdd );
                    // todo this won't catch cases where the ontology source was added at a later date

                    if ( !matchFound ) {
                        // if we didn't find a match, both the OntologyTerm and its Source (if present) need to be added
                        // to the fuge entry, which means added to the ontologyTerms and ontologySources.
                        ontologyTerms.add( termToAdd );
                        ontologySources.add( ( OntologySource ) symbaEntityService.getLatestByEndurant(
                                ontologySourceEndurant ) );
                    }
                }
                genericMaterial.setCharacteristics( characteristics );
            }

            if ( mfs.getTreatmentInfo() != null ) {
                // Next are the treatments, which get loaded as individual descriptions on the GenericMaterial.
                // These are always added, without checks.
                Set<Description> descriptions;
                if ( genericMaterial.getDescriptions() == null ) {
                    descriptions = new HashSet<Description>();
                } else {
                    descriptions = ( Set<Description> ) genericMaterial.getDescriptions();
                }
                for ( String treatmentDesc : mfs.getTreatmentInfo() ) {
                    Description description = ( Description ) entityService.createDescribable(
                            "net.sourceforge.fuge.common.description.Description" );
                    description.setText( "Treatment: " + treatmentDesc );
                    DatabaseObjectHelper.save(
                            "net.sourceforge.fuge.common.description.Description", description,
                            auditor );
                    descriptions.add( description );
                }
                genericMaterial.setDescriptions( descriptions );
            }
        } else {

            // we only deal with novel (and novel multiple) characteristics if it is the input to a MT protocol application
            // otherwise, not interested.
            if ( mfs.getNovelCharacteristics() != null || mfs.getNovelMultipleCharacteristics() != null ) {
                // Now we have the characteristics to load.
                Set<OntologyTerm> characteristics;
                if ( genericMaterial.getCharacteristics() == null ) {
                    characteristics = new HashSet<OntologyTerm>();
                } else {
                    characteristics = ( Set<OntologyTerm> ) genericMaterial.getCharacteristics();
                }
                OntologyIndividual clonedDescriptorSet =
                        ( OntologyIndividual ) symbaEntityService.getLatestByEndurant( mfs.getDescriptorOiEndurant() );
                OntologyIndividual newDescriptorSet =
                        ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                "net.sourceforge.fuge.common.ontology.OntologyIndividual", auditor );

                // set the name and term to something appropriate
                String name = "Specimen Descriptor";
                if ( mfs.getMaterialName() != null && mfs.getMaterialName().length() > 0 ) {
                    name += " " + mfs.getMaterialName();
                }
                newDescriptorSet.setName( name );
                newDescriptorSet.setTerm( name );
                newDescriptorSet.setTermAccession( "not applicable" );
                if ( clonedDescriptorSet.getOntologySource() != null ) {
                    newDescriptorSet.setOntologySource( clonedDescriptorSet.getOntologySource() );
                }

                Set<OntologyProperty> newProperties = new HashSet<OntologyProperty>();
                for ( String ontologySourceEndurant : mfs.getNovelCharacteristics().keySet() ) {
                    ObjectProperty opForCurrentSource =
                            fillObjectProperty( symbaEntityService, clonedDescriptorSet, ontologySourceEndurant,
                                    mfs.getNovelCharacteristics().get( ontologySourceEndurant ),
                                    "Object Property for " + name, auditor );
                    if ( opForCurrentSource != null ) {
                        newProperties.add( opForCurrentSource );
                        if ( !LoaderHelper.findMatchingEndurant( ontologySourceEndurant, ontologySources ) ) {
                            ontologySources.add( ( OntologySource ) symbaEntityService
                                    .getLatestByEndurant( ontologySourceEndurant ) );
                        }
                    }
                }
                for ( String ontologySourceEndurant : mfs.getNovelMultipleCharacteristics().keySet() ) {
                    ObjectProperty opForCurrentSource =
                            fillObjectProperty( symbaEntityService, clonedDescriptorSet, ontologySourceEndurant,
                                    mfs.getNovelMultipleCharacteristics().get( ontologySourceEndurant ),
                                    "Object Property for " + name, auditor );
                    if ( opForCurrentSource != null ) {
                        newProperties.add( opForCurrentSource );
                        if ( !LoaderHelper.findMatchingEndurant( ontologySourceEndurant, ontologySources ) ) {
                            ontologySources.add( ( OntologySource ) symbaEntityService
                                    .getLatestByEndurant( ontologySourceEndurant ) );
                        }
                    }
                }

                newDescriptorSet.setProperties( newProperties );
                newDescriptorSet = ( OntologyIndividual ) DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", newDescriptorSet,
                                auditor );
                ontologyTerms.add( newDescriptorSet );
                if ( newDescriptorSet.getOntologySource() != null ) {
                    ontologySources.add( newDescriptorSet.getOntologySource() );
                }
                characteristics.add( newDescriptorSet );
                genericMaterial.setCharacteristics( characteristics );
            }
        }

        return new Object[]{ genericMaterial, ontologyTerms, ontologySources };
    }

    private static ObjectProperty fillObjectProperty( SymbaEntityService symbaEntityService,
                                                      OntologyIndividual descriptorSet,
                                                      String ontologySourceEndurant,
                                                      String termAndAccessionPair,
                                                      String defaultName,
                                                      Person auditor ) {
        LinkedHashSet<String> list = new LinkedHashSet<String>();
        list.add( termAndAccessionPair );
        return fillObjectProperty( symbaEntityService, descriptorSet, ontologySourceEndurant, list, defaultName,
                auditor );

    }

    private static ObjectProperty fillObjectProperty( SymbaEntityService symbaEntityService,
                                                      OntologyIndividual clonedDescriptorSet,
                                                      String ontologySourceEndurant,
                                                      LinkedHashSet<String> termAndAccessionPairs,
                                                      String defaultName,
                                                      Person auditor ) {
        // find the matching object property.
        ObjectProperty clonedOP = null;
        for ( OntologyProperty ontologyProperty : clonedDescriptorSet.getProperties() ) {
            if ( ontologyProperty instanceof ObjectProperty ) {
                for ( OntologyIndividual oi : ( ( ObjectProperty ) ontologyProperty ).getContent() ) {
                    // find at least one that shares the ontology source you've got - that means we've
                    // found the correct object property.
                    if ( oi.getOntologySource().getEndurant().getIdentifier()
                            .equals( ontologySourceEndurant ) ) {
                        clonedOP = ( ObjectProperty ) ontologyProperty;
                        break;
                    }
                }
            }
            if ( clonedOP != null ) {
                break;
            }
        }

        if ( clonedOP != null ) {
            // create new OP based on cloned OP
            ObjectProperty op =
                    ( ObjectProperty ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                            "net.sourceforge.fuge.common.ontology.ObjectProperty", auditor );
            String opName = clonedOP.getName();
            if ( opName.contains( "net.sourceforge.symba.keywords.dummy" ) ) {
                opName = defaultName;
            }
            op.setName( opName );
            op.setTerm( clonedOP.getTerm() );
            op.setTermAccession( clonedOP.getTermAccession() );
            if ( clonedOP.getOntologySource() != null ) {
                op.setOntologySource( clonedOP.getOntologySource() );
            }
            // every child of the current OP will have the same ontology Source
            OntologySource childOs = clonedOP.getContent().iterator().next().getOntologySource();

            Set<OntologyIndividual> newContents = new HashSet<OntologyIndividual>();

            for ( String termAndAccessionPair : termAndAccessionPairs ) {

                OntologyIndividual ontologyIndividual =
                        ( OntologyIndividual ) DatabaseObjectHelper.createEndurantAndIdentifiable(
                                "net.sourceforge.fuge.common.ontology.OntologyIndividual", auditor );
                String[] parsedPair = termAndAccessionPair.split( "::" );
                ontologyIndividual.setName( parsedPair[0] );
                ontologyIndividual.setTerm( parsedPair[0] );
                ontologyIndividual.setTermAccession( parsedPair[1] );
                ontologyIndividual.setOntologySource( childOs );
                ontologyIndividual = ( OntologyIndividual ) DatabaseObjectHelper.save(
                        "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, auditor );
                newContents.add( ontologyIndividual );
            }

            op.setContent( newContents );
            op = ( ObjectProperty ) DatabaseObjectHelper
                    .save( "net.sourceforge.fuge.common.ontology.ObjectProperty", op, auditor );

            return op;
        }

        return null;

    }
}
