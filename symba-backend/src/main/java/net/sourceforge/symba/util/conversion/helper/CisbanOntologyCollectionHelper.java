package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Collection.FuGE;
import fugeOM.Collection.OntologyCollection;
import fugeOM.Collection.ProtocolCollection;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.*;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import net.sourceforge.symba.util.CisbanHelper;

import javax.xml.bind.JAXBElement;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */

public class CisbanOntologyCollectionHelper implements MappingHelper<OntologyCollection, FugeOMCollectionOntologyCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanDescribableHelper cd;
    private final CisbanIdentifiableHelper ci;
    private final CisbanHelper helper;

    public CisbanOntologyCollectionHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cd = ci.getCisbanDescribableHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public OntologyCollection unmarshal( FugeOMCollectionOntologyCollectionType ontoCollXML, OntologyCollection ontoColl )
            throws RealizableEntityServiceException {

        ontoColl = ( OntologyCollection ) cd.unmarshal( ontoCollXML, ontoColl );

        ontoColl = unmarshalCollectionContents( ontoCollXML, ontoColl );

        // set collection in database
        reService.createObInDB( "fugeOM.Collection.OntologyCollection", ontoColl );

        return ontoColl;
    }

    public OntologyCollection unmarshalCollectionContents( FugeOMCollectionOntologyCollectionType ontoCollXML,
                                                           OntologyCollection ontoColl )
            throws RealizableEntityServiceException {
        // Ontology Sources
        Set<OntologySource> ontologySources = new HashSet<OntologySource>();
        for ( FugeOMCommonOntologyOntologySourceType ontoSourceXML : ontoCollXML.getOntologySource() ) {

            // Retrieve latest version from the database.
            OntologySource ontologySource = ( OntologySource ) helper.getOrCreateLatest(
                    ontoSourceXML.getEndurant(),
                    "fugeOM.Common.Ontology.OntoSourceEndurant",
                    ontoSourceXML.getName(),
                    "fugeOM.Common.Ontology.OntologySource",
                    System.err );

            ontologySource = ( OntologySource ) ci.unmarshal( ontoSourceXML, ontologySource );
            ontologySource.setOntologyURI( ontoSourceXML.getOntologyURI() );
            if ( ontologySource.getId() != null ) {
                helper.assignAndLoadIdentifiable( ontologySource, "fugeOM.Common.Ontology.OntologySource", System.err );
            } else {
                helper.loadIdentifiable( ontologySource, "fugeOM.Common.Ontology.OntologySource", System.err );
            }
            ontologySources.add( ontologySource );
        }
        ontoColl.setOntologySources( ontologySources );

        // Ontology Terms
        Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
        for ( JAXBElement<? extends FugeOMCommonOntologyOntologyTermType> ontologyTermElementXML : ontoCollXML.getOntologyTerm() ) {
            FugeOMCommonOntologyOntologyTermType ontologyTermXML = ontologyTermElementXML.getValue();
            if ( ontologyTermXML instanceof FugeOMCommonOntologyOntologyIndividualType ) {
                FugeOMCommonOntologyOntologyIndividualType ontologyIndividualXML = ( FugeOMCommonOntologyOntologyIndividualType ) ontologyTermXML;
                ontologyTerms.add( unmarshalOntologyIndividual( ontologyIndividualXML ) );
            }
        }
        ontoColl.setOntologyTerms( ontologyTerms );
        return ontoColl;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCollectionOntologyCollectionType marshal( FugeOMCollectionOntologyCollectionType ontoCollXML, OntologyCollection ontoColl )
            throws RealizableEntityServiceException {

        ontoCollXML = ( FugeOMCollectionOntologyCollectionType ) cd.marshal( ontoCollXML, ontoColl );

        // set ontology terms
        ObjectFactory factory = new ObjectFactory();
        for ( Object ontologyTermObj : ontoColl.getOntologyTerms() ) {
            OntologyTerm ontologyTerm = ( OntologyTerm ) ontologyTermObj;

            if ( ontologyTerm instanceof OntologyIndividual )
                ontoCollXML.getOntologyTerm()
                        .add( factory.createOntologyIndividual( marshalOntologyIndividual( ( OntologyIndividual ) ontologyTerm ) ) );
        }

        // set ontology source
        for ( Object ontoSourceObj : ontoColl.getOntologySources() ) {
            OntologySource ontoSource = ( OntologySource ) ontoSourceObj;

            FugeOMCommonOntologyOntologySourceType ontoSourceXML = new FugeOMCommonOntologyOntologySourceType();
            ontoSourceXML = ( FugeOMCommonOntologyOntologySourceType ) ci.marshal(
                    ontoSourceXML, ontoSource );
            ontoSourceXML.setOntologyURI( ontoSource.getOntologyURI() );
            ontoCollXML.getOntologySource().add( ontoSourceXML );
        }

        return ontoCollXML;
    }

    public FugeOMCollectionOntologyCollectionType generateRandomXML( FugeOMCollectionOntologyCollectionType ontoCollXML ) {

        ontoCollXML = ( FugeOMCollectionOntologyCollectionType ) cd.generateRandomXML( ontoCollXML );

        // set ontology sources
        FugeOMCommonOntologyOntologySourceType ontoSourceXML = null;
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            ontoSourceXML = new FugeOMCommonOntologyOntologySourceType();
            ontoSourceXML = ( FugeOMCommonOntologyOntologySourceType ) ci.generateRandomXML( ontoSourceXML );
            ontoSourceXML.setOntologyURI( String.valueOf( Math.random() ) );
            ontoCollXML.getOntologySource().add( ontoSourceXML );
        }

        // set ontology terms
        ObjectFactory factory = new ObjectFactory();
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            ontoCollXML.getOntologyTerm()
                    .add( factory.createOntologyIndividual( generateRandomOntologyIndividualXML( ontoSourceXML ) ) );
        }

        return ontoCollXML;
    }

    private FugeOMCommonOntologyOntologyIndividualType generateRandomOntologyIndividualXML(
            FugeOMCommonOntologyOntologySourceType ontoSourceXML ) {
        return generateRandomOntologyIndividualXML( ontoSourceXML, false );
    }

    private FugeOMCommonOntologyOntologyIndividualType generateRandomOntologyIndividualXML(
            FugeOMCommonOntologyOntologySourceType ontoSourceXML, boolean inner ) {

        FugeOMCommonOntologyOntologyIndividualType ontologyIndividualXML = new FugeOMCommonOntologyOntologyIndividualType();
        ontologyIndividualXML = ( FugeOMCommonOntologyOntologyIndividualType ) generateRandomOntologyTermXML(
                ontologyIndividualXML, ontoSourceXML );

        if ( !inner ) {
            ObjectFactory factory = new ObjectFactory();
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

                FugeOMCommonOntologyObjectPropertyType objectPropertyXML = new FugeOMCommonOntologyObjectPropertyType();
                objectPropertyXML = ( FugeOMCommonOntologyObjectPropertyType ) ci.generateRandomXML( objectPropertyXML );
                objectPropertyXML = ( FugeOMCommonOntologyObjectPropertyType ) generateRandomOntologyTermXML(
                        objectPropertyXML, ontoSourceXML );
                for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                    objectPropertyXML.getOntologyIndividual()
                            .add( generateRandomOntologyIndividualXML( ontoSourceXML, true ) );
                }
                ontologyIndividualXML.getOntologyProperty().add( factory.createObjectProperty( objectPropertyXML ) );

                FugeOMCommonOntologyDataPropertyType dataPropertyXML = new FugeOMCommonOntologyDataPropertyType();
                dataPropertyXML = ( FugeOMCommonOntologyDataPropertyType ) ci.generateRandomXML( dataPropertyXML );
                dataPropertyXML = ( FugeOMCommonOntologyDataPropertyType ) generateRandomOntologyTermXML(
                        dataPropertyXML, ontoSourceXML );
                dataPropertyXML.setDataType( String.valueOf( Math.random() ) );
                ontologyIndividualXML.getOntologyProperty().add( factory.createDataProperty( dataPropertyXML ) );
            }
        }
        return ontologyIndividualXML;
    }

    private FugeOMCommonOntologyOntologyTermType generateRandomOntologyTermXML(
            FugeOMCommonOntologyOntologyTermType ontologyTermXML,
            FugeOMCommonOntologyOntologySourceType ontoSourceXML ) {

        ontologyTermXML = ( FugeOMCommonOntologyOntologyTermType ) ci.generateRandomXML( ontologyTermXML );

        ontologyTermXML.setTerm( String.valueOf( Math.random() ) );
        ontologyTermXML.setTermAccession( String.valueOf( Math.random() ) );
        ontologyTermXML.setOntologySourceRef( ontoSourceXML.getIdentifier() );

        return ontologyTermXML;
    }

    private OntologyIndividual unmarshalOntologyIndividual(
            FugeOMCommonOntologyOntologyIndividualType ontologyIndividualXML )
            throws RealizableEntityServiceException {

        // Retrieve latest version from the database.
        OntologyIndividual ontologyIndividual = ( OntologyIndividual ) helper.getOrCreateLatest(
                ontologyIndividualXML.getEndurant(),
                "fugeOM.Common.Ontology.OntoIndvEndurant",
                ontologyIndividualXML.getName(),
                "fugeOM.Common.Ontology.OntologyIndividual",
                System.err );

        // set the ontology term traits
        ontologyIndividual = ( OntologyIndividual ) unmarshalOntologyTerm( ontologyIndividualXML, ontologyIndividual );

        // set the OntologyIndividual-specific traits
        Set<OntologyProperty> ontologyProperties = new HashSet<OntologyProperty>();
        for ( JAXBElement<? extends FugeOMCommonOntologyOntologyPropertyType> ontologyPropertyElementXML : ontologyIndividualXML
                .getOntologyProperty() ) {

            FugeOMCommonOntologyOntologyPropertyType ontologyPropertyXML = ontologyPropertyElementXML.getValue();

            if ( ontologyPropertyXML instanceof FugeOMCommonOntologyObjectPropertyType ) {

                FugeOMCommonOntologyObjectPropertyType objectPropertyXML = ( FugeOMCommonOntologyObjectPropertyType ) ontologyPropertyXML;

                // set object property

                // Retrieve latest version from the database.
                ObjectProperty objectProperty = ( ObjectProperty ) helper.getOrCreateLatest(
                        objectPropertyXML.getEndurant(),
                        "fugeOM.Common.Ontology.ObjPropertyEndurant",
                        objectPropertyXML.getName(),
                        "fugeOM.Common.Ontology.ObjectProperty",
                        System.err );

                objectProperty = ( ObjectProperty ) unmarshalOntologyTerm( objectPropertyXML, objectProperty );

                Set<OntologyIndividual> smallOis = new HashSet<OntologyIndividual>();
                for ( FugeOMCommonOntologyOntologyIndividualType smallOiXML : objectPropertyXML.getOntologyIndividual() )
                    smallOis.add( unmarshalOntologyIndividual( smallOiXML ) );

                objectProperty.setContent( smallOis );
                if ( objectProperty.getId() != null ) {
                    objectProperty.setName( objectPropertyXML.getName() ); // won't be set if retrieved from db
                    helper.assignAndLoadIdentifiable(
                            objectProperty, "fugeOM.Common.Ontology.ObjectProperty", System.err );
                } else {
                    helper.loadIdentifiable( objectProperty, "fugeOM.Common.Ontology.ObjectProperty", System.err );
                }

                ontologyProperties.add( objectProperty );

            } else if ( ontologyPropertyXML instanceof FugeOMCommonOntologyDataPropertyType ) {

                FugeOMCommonOntologyDataPropertyType dataPropertyXML = ( FugeOMCommonOntologyDataPropertyType ) ontologyPropertyXML;

                // set data property

                // Retrieve latest version from the database.
                DataProperty dataProperty = ( DataProperty ) helper.getOrCreateLatest(
                        dataPropertyXML.getEndurant(),
                        "fugeOM.Common.Ontology.DataPropertyEndurant",
                        dataPropertyXML.getName(),
                        "fugeOM.Common.Ontology.DataProperty",
                        System.err );

                dataProperty = ( DataProperty ) unmarshalOntologyTerm( dataPropertyXML, dataProperty );
                dataProperty.setDataType( dataPropertyXML.getDataType() );
                if ( dataProperty.getId() != null ) {
                    dataProperty.setName( dataPropertyXML.getName() ); // won't be set if retrieved from db
                    helper.assignAndLoadIdentifiable( dataProperty, "fugeOM.Common.Ontology.DataProperty", System.err );
                } else {
                    helper.loadIdentifiable( dataProperty, "fugeOM.Common.Ontology.DataProperty", System.err );
                }
                ontologyProperties.add( dataProperty );
            }
        }
        ontologyIndividual.setProperties( ontologyProperties );
        if ( ontologyIndividual.getId() != null ) {
            ontologyIndividual.setName( ontologyIndividualXML.getName() ); // won't be set if retrieved from db
            helper.assignAndLoadIdentifiable(
                    ontologyIndividual, "fugeOM.Common.Ontology.OntologyIndividual", System.err );
        } else {
            helper.loadIdentifiable( ontologyIndividual, "fugeOM.Common.Ontology.OntologyIndividual", System.err );
        }
        return ontologyIndividual;
    }

    private FugeOMCommonOntologyOntologyIndividualType marshalOntologyIndividual(
            OntologyIndividual ontologyIndividual ) {

        FugeOMCommonOntologyOntologyIndividualType ontologyIndividualXML = new FugeOMCommonOntologyOntologyIndividualType();
        ontologyIndividualXML = ( FugeOMCommonOntologyOntologyIndividualType ) marshalOntologyTerm(
                ontologyIndividualXML, ontologyIndividual );

        ObjectFactory factory = new ObjectFactory();

        for ( Object ontologyPropertyObj : ontologyIndividual.getProperties() ) {
            OntologyProperty ontologyProperty = ( OntologyProperty ) ontologyPropertyObj;

            if ( ontologyProperty instanceof ObjectProperty ) {
                FugeOMCommonOntologyObjectPropertyType objectPropertyXML = new FugeOMCommonOntologyObjectPropertyType();
                ObjectProperty objectProperty = ( ObjectProperty ) ontologyProperty;
                objectPropertyXML = ( FugeOMCommonOntologyObjectPropertyType ) marshalOntologyTerm(
                        objectPropertyXML, objectProperty );
                for ( Object smallOiObj : objectProperty.getContent() ) {
                    OntologyIndividual smallOi = ( OntologyIndividual ) smallOiObj;
                    objectPropertyXML.getOntologyIndividual().add( marshalOntologyIndividual( smallOi ) );
                }

                ontologyIndividualXML.getOntologyProperty().add( factory.createObjectProperty( objectPropertyXML ) );
            } else if ( ontologyProperty instanceof DataProperty ) {
                FugeOMCommonOntologyDataPropertyType dataPropertyXML = new FugeOMCommonOntologyDataPropertyType();
                DataProperty dataProperty = ( DataProperty ) ontologyProperty;
                dataPropertyXML = ( FugeOMCommonOntologyDataPropertyType ) marshalOntologyTerm(
                        dataPropertyXML, dataProperty );
                dataPropertyXML.setDataType( dataProperty.getDataType() );
                ontologyIndividualXML.getOntologyProperty().add( factory.createDataProperty( dataPropertyXML ) );
            }
        }
        return ontologyIndividualXML;
    }


    private OntologyTerm unmarshalOntologyTerm( FugeOMCommonOntologyOntologyTermType ontologyTermXML,
                                                OntologyTerm ontologyTerm )
            throws RealizableEntityServiceException {
        ontologyTerm = ( OntologyTerm ) ci.unmarshal( ontologyTermXML, ontologyTerm );
        ontologyTerm.setTerm( ontologyTermXML.getTerm() );
        ontologyTerm.setTermAccession( ontologyTermXML.getTermAccession() );
        if ( ontologyTermXML.getOntologySourceRef() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            ontologyTerm.setOntologySource( ( OntologySource ) reService.findIdentifiable( ontologyTermXML.getOntologySourceRef() ) );
        }
        return ontologyTerm;
    }

    private FugeOMCommonOntologyOntologyTermType marshalOntologyTerm(
            FugeOMCommonOntologyOntologyTermType ontologyTermXML, OntologyTerm ontologyTerm ) {

        ontologyTermXML = ( FugeOMCommonOntologyOntologyTermType ) ci.marshal(
                ontologyTermXML, ontologyTerm );

        if ( ontologyTerm.getTerm() != null ) ontologyTermXML.setTerm( ontologyTerm.getTerm() );
        if ( ontologyTerm.getTermAccession() != null )
            ontologyTermXML.setTermAccession( ontologyTerm.getTermAccession() );
        if ( ontologyTerm.getOntologySource() != null )
            ontologyTermXML.setOntologySourceRef( ontologyTerm.getOntologySource().getIdentifier() );

        return ontologyTermXML;
    }

    public OntologyCollection getLatestVersion(
            OntologyCollection ontologyCollection ) throws RealizableEntityServiceException {

        // get the latest version of the identifiable objects in the describable section
        ontologyCollection = ( OntologyCollection ) cd.getLatestVersion( ontologyCollection );

        // prepare updated set
        Set<OntologySource> set = new HashSet<OntologySource>();

        // load all the latest versions into the new set.
        for ( Object obj : ontologyCollection.getOntologySources() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set.add( ( OntologySource ) ci.getLatestVersion( identifiable ) );
        }
        ontologyCollection.setOntologySources( set );

        // prepare updated set
        Set<OntologyTerm> set2 = new HashSet<OntologyTerm>();

        // load all the latest versions into the new set.
        for ( Object obj : ontologyCollection.getOntologyTerms() ) {
            if ( obj instanceof OntologyIndividual ) {
                OntologyIndividual oi = ( OntologyIndividual ) obj;
                oi = getLatestOntologyIndividualVersion( oi );
                set2.add( oi );
            }
        }
        ontologyCollection.setOntologyTerms( set2 );

        return ontologyCollection;
    }

    public OntologyIndividual getLatestOntologyIndividualVersion(
            OntologyIndividual ontologyIndividual ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        ontologyIndividual = ( OntologyIndividual ) reService.findLatestByEndurant( ontologyIndividual.getEndurant().getIdentifier() );
        ontologyIndividual = ( OntologyIndividual ) ci.getLatestVersion( ontologyIndividual );

        if ( ontologyIndividual.getOntologySource() != null ) {
            // get the latest version of the ontology source referenced by the ontology term.
            OntologySource os = ( OntologySource ) reService.findLatestByEndurant(
                    ontologyIndividual.getOntologySource().getEndurant().getIdentifier() );
            ontologyIndividual.setOntologySource( ( OntologySource ) ci.getLatestVersion( os ) );
        }

        // prepare updated set
        Set<OntologyProperty> set = new HashSet<OntologyProperty>();

        // load all the latest versions into the new set.
        for ( Object obj : ontologyIndividual.getProperties() ) {
            if ( obj instanceof ObjectProperty ) {
                ObjectProperty op = ( ObjectProperty ) obj;
                op = ( ObjectProperty ) reService.findLatestByEndurant( op.getEndurant().getIdentifier() );
                op = ( ObjectProperty ) ci.getLatestVersion( op );
                Set<OntologyIndividual> set2 = new HashSet<OntologyIndividual>();
                for ( Object obj2 : op.getContent() ) {
                    set2.add( getLatestOntologyIndividualVersion( ( OntologyIndividual ) obj2 ) );
                }
                op.setContent( set2 );
                set.add( op );
            } else if ( obj instanceof DataProperty ) {
                DataProperty dp = ( DataProperty ) obj;
                dp = ( DataProperty ) reService.findLatestByEndurant( dp.getEndurant().getIdentifier() );
                set.add( ( DataProperty ) ci.getLatestVersion( dp ) );
            }
        }
        ontologyIndividual.setProperties( set );
        return ontologyIndividual;
    }

    public FuGE addRelevantOntologyTerms( FuGE fuge ) throws RealizableEntityServiceException {
        OntologyCollection ontologyCollection = ( OntologyCollection ) reService.createDescribableOb(
                "fugeOM.Collection.OntologyCollection" );

        // many protocols contain ontology terms, so we need to add any mentioned terms to the OntologyCollection

        // go through each of the protocols in the experiment. Currently, it is only in the Parameters of the
        // Actions of the protocols, and in the Parameters of the Protocols themselves where we look for ontology terms
        // start by making sure we won't lose any already-included ontology terms from the collection
        Set<OntologyTerm> ontologyTerms;
        if ( fuge.getOntologyCollection() != null ) {
            ontologyTerms = ( Set<OntologyTerm> ) fuge.getOntologyCollection().getOntologyTerms();
        } else {
            ontologyTerms = new HashSet<OntologyTerm>();
        }

        ontologyTerms.addAll( addOntologyTermsFromProtocols( fuge.getProtocolCollection(), ontologyTerms ) );
        ontologyTerms.addAll(
                addOntologyTermsFromEquipment(
                        ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment(), ontologyTerms ) );

        ontologyCollection.setOntologyTerms( ontologyTerms );
        // don't do any checking with ontology sources - just assume that those already present are correct.
        // todo check for new ontology sources associated with the new ontology terms#
        if ( fuge.getOntologyCollection() != null && fuge.getOntologyCollection().getOntologySources() != null ) {
            ontologyCollection.setOntologySources( fuge.getOntologyCollection().getOntologySources() );
        }
        // load the fuge object into the database
        reService.createObInDB( "fugeOM.Collection.OntologyCollection", ontologyCollection );
        fuge.setOntologyCollection( ontologyCollection );

        return fuge;
    }

    private Set<OntologyTerm> addOntologyTermsFromEquipment( Set<Equipment> allEquipment,
                                                             Set<OntologyTerm> ontologyTerms )
            throws RealizableEntityServiceException {
        for ( Equipment equipment : allEquipment ) {
            if ( equipment instanceof GenericEquipment ) {
                // checking for generic parameters inside the protocol
                for ( Object gpObj : equipment.getParameters() ) {
                    if ( gpObj instanceof GenericParameter ) {
                        ontologyTerms.addAll(
                                addOntologyTermsFromParameter(
                                        ( GenericParameter ) gpObj, ontologyTerms ) );
                    }
                }
                // checking for ontology terms elsewhere in the GenericEquipment
                if ( equipment.getMake() != null ) {
                    ontologyTerms = addOntologyTermIfFound(
                            ontologyTerms, equipment.getMake().getEndurant().getIdentifier() );
                }
                if ( equipment.getModel() != null ) {
                    ontologyTerms = addOntologyTermIfFound(
                            ontologyTerms, equipment.getModel().getEndurant().getIdentifier() );
                }
                if ( equipment.getAnnotations() != null && !equipment.getAnnotations().isEmpty() ) {
                    for ( Object obj : equipment.getAnnotations() ) {
                        OntologyTerm ot = ( OntologyTerm ) obj;
                        ontologyTerms = addOntologyTermIfFound(
                                ontologyTerms, ot.getEndurant().getIdentifier() );
                    }
                }
            }
        }
        return ontologyTerms;
    }


    private Set<OntologyTerm> addOntologyTermsFromProtocols( ProtocolCollection protocolCollection,
                                                             Set<OntologyTerm> ontologyTerms )
            throws RealizableEntityServiceException {

        for ( Object obj : protocolCollection.getProtocols() ) {
            if ( obj instanceof GenericProtocol ) {
                // checking for generic parameters inside the protocol
                for ( Object gpObj : ( ( GenericProtocol ) obj ).getProtocolParameters() ) {
                    if ( gpObj instanceof GenericParameter ) {
                        ontologyTerms.addAll(
                                addOntologyTermsFromParameter(
                                        ( GenericParameter ) gpObj, ontologyTerms ) );
                    }
                }
                // checking for generic parameters inside the protocol's generic action
                for ( Object gaObj : ( ( GenericProtocol ) obj ).getGenericActions() ) {
                    for ( Object gpObj : ( ( GenericAction ) gaObj ).getParameters() ) {
                        if ( gpObj instanceof GenericParameter ) {
                            ontologyTerms.addAll(
                                    addOntologyTermsFromParameter(
                                            ( GenericParameter ) gpObj, ontologyTerms ) );
                        }
                    }
                }
            }
        }
        return ontologyTerms;
    }

    private Set<OntologyTerm> addOntologyTermsFromParameter( GenericParameter genericParameter,
                                                             Set<OntologyTerm> ontologyTerms )
            throws RealizableEntityServiceException {
        if ( genericParameter.getUnit() != null ) {
            boolean found = false;
            for ( OntologyTerm ot : ontologyTerms ) {
                if ( ot.getEndurant()
                        .getIdentifier()
                        .equals(
                                genericParameter.getUnit()
                                        .getEndurant().getIdentifier() ) ) {
                    // this is already present - don't add
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                ontologyTerms.add(
                        ( OntologyTerm ) reService.findLatestByEndurant(
                                genericParameter.getUnit()
                                        .getEndurant().getIdentifier() ) );
            }
        }
        if ( genericParameter.getDefaultValue() instanceof ComplexValue ) {
            // complex values can have ontology terms as their value
            DefaultValue dv = genericParameter.getDefaultValue();
            if ( dv instanceof ComplexValue ) {
                ComplexValue complexValue = ( ComplexValue ) dv;
                ontologyTerms = addOntologyTermIfFound(
                        ontologyTerms, complexValue.get_defaultValue().getEndurant().getIdentifier() );
            }
        }
        return ontologyTerms;
    }

    private Set<OntologyTerm> addOntologyTermIfFound( Set<OntologyTerm> ontologyTerms,
                                                      String endurantId ) throws RealizableEntityServiceException {
        // todo faster search algorithm
        boolean found = false;
        for ( OntologyTerm ot : ontologyTerms ) {
            if ( ot.getEndurant().getIdentifier().equals( endurantId ) ) {
                // this is already present - don't add
                found = true;
                break;
            }
        }
        if ( !found ) {
            System.err.println( "ADDING: " + endurantId );
            ontologyTerms.add( ( OntologyTerm ) reService.findLatestByEndurant( endurantId ) );
        }
        return ontologyTerms;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( OntologyCollection ontologyCollection, PrintStream printStream ) {

        String aaa = "Ontology Source             : ";
        String bbb = "Ontology Source Reference   : ";
        String ccc = "Ontology Individual         : ";
        String ddd = "Ontology Individual's Source: ";
        String eee = "Ontology Individual's Term  : ";

        // print ontology sources.
        for ( Object obj : ontologyCollection.getOntologySources() ) {
            OntologySource source = ( OntologySource ) obj;
            ci.prettyPrint( aaa, source, printStream );
            if ( source.getOntologyURI() != null ) {
                printStream.println( bbb + source.getOntologyURI() );
            }
        }

        // print ontology terms.
        for ( Object obj : ontologyCollection.getOntologyTerms() ) {
            if ( obj instanceof OntologyIndividual ) {
                OntologyIndividual oi = ( OntologyIndividual ) obj;
                ci.prettyPrint( ccc, oi, printStream );
                printStream.println( eee + oi.getTerm() );
                if ( oi.getOntologySource() != null ) {
                    ci.prettyPrint( ddd, oi.getOntologySource(), printStream );
                    printStream.println( bbb + oi.getOntologySource().getOntologyURI() );
                }
            }
        }

        // todo ObjectProperty and DataProperty
    }

    public void prettyHtml( Collection ontologyTerms, PrintWriter printStream ) {
        for ( Object obj : ontologyTerms ) {
            if ( obj instanceof OntologyTerm ) {
                OntologyTerm ontologyTerm = ( OntologyTerm ) obj;
                printStream.println( ontologyTerm.getTerm() );
                if ( ontologyTerm.getOntologySource() != null ) {
                    printStream.println( " (" + ontologyTerm.getOntologySource().getName() + ")" );
                }
                printStream.println( "<br>" );
            }
        }

    }
}