package net.sourceforge.symba.obiloader.conversion;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.util.SimpleURIMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;
import java.io.PrintStream;

import fugeOM.util.generatedJAXB2.*;
import net.sourceforge.symba.util.CisbanHelper;
import net.sourceforge.symba.ServiceLocator;

/**
 * This file is part of the sbml2owl package.
 * <p/>
 * Copyright (C) 2007-8, Allyson Lister and her employers
 * <p/>
 * Modifications to the initial code base are copyright of their respective authors, or their employers as appropriate.
 * Authorship of the modifications may be determined from the Subversion history.
 * <p/>
 * To view the full licensing information for this software and ALL other packages contained in this distribution,
 * please see LICENSE.txt
 * <p/>
 * $LastChangedDate: $ $LastChangedRevision: $ $Author:  $ $HeadURL: $
 */
public class ObiManipulator {

    private String obiLogicalURI;
    private String obiPhysicalDirectory;

    private OWLOntologyManager manager;

    private OWLOntology obi;

    public ObiManipulator( String obiLogicalURI,
                           String obiPhysicalDirectory ) throws OWLOntologyCreationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, OWLReasonerException, InstantiationException, IllegalAccessException, URISyntaxException {

        this.obiLogicalURI = obiLogicalURI;
        this.obiPhysicalDirectory = obiPhysicalDirectory;

        // create a manager
        this.manager = OWLManager.createOWLOntologyManager();

//        addAllObiMappings();

        // add a trailing slash if there isn't one
        if ( this.obiPhysicalDirectory.indexOf( this.obiPhysicalDirectory.length() - 1 ) != '/' ) {
            this.obiPhysicalDirectory += "/";
        }

        this.obi = this.manager.loadOntologyFromPhysicalURI( URI.create( obiPhysicalDirectory + "OBI.owl" ) );
        // The data factory facilitates the creation of ontology objects.
//        OWLDataFactory factory = this.manager.getOWLDataFactory();

        Set<OWLOntology> importsClosure = manager.getImportsClosure( this.obi );
        OWLReasoner reasoner = new org.mindswap.pellet.owlapi.Reasoner( this.manager );
        reasoner.loadOntologies( importsClosure );

        // You could use reflection to create an instance of pellet is so that there is no compile time
        // dependency (since the pellet libraries aren't contained in the OWL API repository), as shown
        // below:
        // String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
        // Class reasonerClass = Class.forName( reasonerClassName );
        // Constructor<OWLReasoner> con = reasonerClass.getConstructor( OWLOntologyManager.class );
        // this.reasoner = con.newInstance( this.manager );
        reasoner = new org.mindswap.pellet.owlapi.Reasoner( this.manager );

        // as we do not plan to change obi, we can check consistency once only, ahead of time, and throw an exception
        // immediately if it isn't consistent.
        System.out.println( "Checking consistency of OBI..." );
        long start = System.currentTimeMillis();
        Set<OWLClass> inconsistentClasses = reasoner.getInconsistentClasses();
        float elapsed = System.currentTimeMillis() - start;
        System.out.println( "Time to check consistency (seconds): " + elapsed / 1000 );
        if ( !inconsistentClasses.isEmpty() ) {
            System.err.println( "The following classes are inconsistent: " );
            for ( OWLClass cls : inconsistentClasses ) {
                System.err.println( "    " + cls );
            }
            throw new RuntimeException( "Cannot progress when OBI is inconsistent" );
        }

        System.out.println( "OBI is consistent." );
        System.out.println( "obi.getReferencedClasses().size() = " + obi.getReferencedClasses().size() );
        System.out.println( "obi.getURI() = " + obi.getURI() );
        System.out.println( "obi.getImports() = " + obi.getImports( manager ).size() );
        for ( OWLOntology owlOntology : obi.getImports( manager ) ) {
            System.out.println( owlOntology.getURI() + " = " + owlOntology.getReferencedClasses().size() );
        }
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getObi() {
        return obi;
    }

    /**
     * This method returns direct children only, and not the entire hierarchy below the provided class.
     *
     * @param obiClassId the name of the class (plus branch information, see above) you want the children from.
     * @return a flattened set of child classes
     * @throws org.semanticweb.owl.inference.OWLReasonerException
     *          if there is an error getting the child classes
     */
    public Set<OWLDescription> getChildren( String obiClassId ) throws OWLReasonerException {

        System.out.println( "obiLogicalURI + obiClassId = " + obiLogicalURI + obiClassId );
        OWLClass parentClass = manager.getOWLDataFactory().getOWLClass( URI.create( obiLogicalURI + obiClassId ) );
        return parentClass.getSubClasses( obi );

        // for some reason, this doesn't retrieve any classes
//        Set<Set<OWLClass>> hierarchicalChildClasses = reasoner.getDescendantClasses( parentClass );
//        return OWLReasonerAdapter.flattenSetOfSets( hierarchicalChildClasses );
    }

    /**
     * This method returns all children, and not the entire hierarchy below the provided class.
     *
     * @param obiClassId the name of the class (plus branch information, see above) you want the children from.
     * @return a flattened set of child classes
     * @throws org.semanticweb.owl.inference.OWLReasonerException
     *          if there is an error getting the child classes
     */
    public Set<OWLDescription> getAllSubclasses( String obiClassId ) throws OWLReasonerException {

        System.out.println( "obiLogicalURI + obiClassId = " + obiLogicalURI + obiClassId );
        OWLClass parentClass = manager.getOWLDataFactory().getOWLClass( URI.create( obiLogicalURI + obiClassId ) );
        return getAllSubclasses( parentClass );

    }

    private Set<OWLDescription> getAllSubclasses( OWLClass parentClass ) {
        Set<OWLDescription> descs = new HashSet<OWLDescription>();
        descs.addAll( parentClass.getSubClasses( obi ) );
        for ( OWLDescription desc : parentClass.getSubClasses( obi ) ) {
            OWLClass currentClass = manager.getOWLDataFactory().getOWLClass( URI.create( obiLogicalURI + desc.toString() ) );
            descs.addAll( getAllSubclasses( currentClass ) );
        }
        return descs;
    }

    private void addMappings( URI logical, URI physical ) {

        SimpleURIMapper mapper = new SimpleURIMapper( logical, physical );

        // Set up the mappings, which map the logical URI to the physical URI
        manager.addURIMapper( mapper );
    }

    /**
     * The information about what needs to be loaded can be found in obi.repository, and this method is drawn
     * from that file.
     *
     * @throws java.net.URISyntaxException if there is an error building the URIs from the logical and physical
     *                                     locations of the various imported ontologies.
     */
    private void addAllObiMappings() throws URISyntaxException {
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/externalDerived.owl" ), new java.net.URI( obiPhysicalDirectory + "externalDerived.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/external.owl" ), new java.net.URI( obiPhysicalDirectory + "external.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/obi-owl-full.owl" ), new java.net.URI( obiPhysicalDirectory + "obi-owl-full.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/Biomaterial.owl" ), new java.net.URI( obiPhysicalDirectory + "Biomaterial.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/DataTransformation.owl" ), new java.net.URI( obiPhysicalDirectory + "DataTransformation.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/DigitalEntityPlus.owl" ), new java.net.URI( obiPhysicalDirectory + "DigitalEntityPlus.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/DataFormatSpecification.owl" ), new java.net.URI( obiPhysicalDirectory + "DataFormatSpecification.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/InstrumentAndPart.owl" ), new java.net.URI( obiPhysicalDirectory + "InstrumentAndPart.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/Obsolete.owl" ), new java.net.URI( obiPhysicalDirectory + "Obsolete.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/PlanAndPlannedProcess.owl" ), new java.net.URI( obiPhysicalDirectory + "PlanAndPlannedProcess.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/Relations.owl" ), new java.net.URI( obiPhysicalDirectory + "Relations.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/Role.owl" ), new java.net.URI( obiPhysicalDirectory + "Role.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/OBI-Function.owl" ), new java.net.URI( obiPhysicalDirectory + "OBI-Function.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/Quality.owl" ), new java.net.URI( obiPhysicalDirectory + "Quality.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/TheRest.owl" ), new java.net.URI( obiPhysicalDirectory + "TheRest.owl" ) );
        addMappings( new java.net.URI( obiLogicalURI + "obi" + "/AnnotationProperty.owl" ), new java.net.URI( obiPhysicalDirectory + "AnnotationProperty.owl" ) );
        addMappings( new java.net.URI( "http://www.ifomis.org/bfo/1.1" ), new java.net.URI( obiPhysicalDirectory + "../external/bfo11.owl" ) );
        addMappings( new java.net.URI( "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl" ), new java.net.URI( obiPhysicalDirectory + "../external/protege-dc.owl" ) );
        addMappings( new java.net.URI( "http://protege.stanford.edu/plugins/owl/protege" ), new java.net.URI( obiPhysicalDirectory + "../external/protege.owl" ) );
        addMappings( new java.net.URI( "http://www.obofoundry.org/ro/ro.owl" ), new java.net.URI( obiPhysicalDirectory + "../external/ro.owl" ) );
        addMappings( new java.net.URI( "http://purl.org/obo/owl/ro_bfo_bridge/1.1" ), new java.net.URI( obiPhysicalDirectory + "../external/ro_bfo1-1_bridge.owl" ) );

    }

    public void printSummaryOWLDescriptions( PrintStream ps, Set<OWLDescription> owlDescriptions ) {
        for ( OWLDescription owlDescription : owlDescriptions ) {
            ps.print( "owl class id: " + owlDescription );
            for ( OWLAnnotation annotation : owlDescription.asOWLClass().getAnnotations( obi ) ) {
                if ( annotation.getAnnotationURI().toString().equals( "http://www.w3.org/2000/01/rdf-schema#label" ) ) {
                    ps.print( " (owl label: " );
                    // remove the "@en" from the end that is present in the label by default
                    if ( annotation.getAnnotationValue().toString().contains( "@" ) ) {
                        ps.print( annotation.getAnnotationValue().toString().substring( 0, annotation.getAnnotationValue().toString().indexOf( "@" ) ) );
                    } else {
                        ps.print( annotation.getAnnotationValue() );
                    }
                    ps.println( ")" );
                }
            }
        }
    }

    public FugeOMCollectionOntologyCollectionType createFuGEOntologyCollection( FugeOMCollectionOntologyCollectionType finalOntologyCollection, String sourceClassString, Set<OWLDescription> owlDescriptions ) {

        CisbanHelper ch = CisbanHelper.create( ServiceLocator.instance().getRealizableEntityService() );
        ObjectFactory factory = new ObjectFactory();

        // add ontology sources only if they have not been added before
        FugeOMCommonOntologyOntologySourceType ontologySource = classAlreadyPresent( finalOntologyCollection, obiLogicalURI + sourceClassString );
        if ( ontologySource == null ) {
            OWLClass sourceClass = manager.getOWLDataFactory().getOWLClass( URI.create( obiLogicalURI + sourceClassString ) );
            ontologySource = new FugeOMCommonOntologyOntologySourceType();
            ontologySource.setIdentifier( ch.getLSID( "fugeOM.Common.Ontology.OntologySource" ) );
            ontologySource.setEndurant( ch.getLSID( "fugeOM.Common.Ontology.OntoSourceEndurant" ) );
            ontologySource.setOntologyURI( obiLogicalURI + sourceClassString );
            for ( OWLAnnotation annotation : sourceClass.getAnnotations( obi ) ) {
                if ( annotation.getAnnotationURI().toString().equals( "http://www.w3.org/2000/01/rdf-schema#label" ) ) {
                    // remove the "@en" from the end that is present in the label by default
                    if ( annotation.getAnnotationValue().toString().contains( "@" ) ) {
                        ontologySource.setName( annotation.getAnnotationValue().toString().substring( 0, annotation.getAnnotationValue().toString().indexOf( "@" ) ) );
                    } else {
                        ontologySource.setName( annotation.getAnnotationValue().toString() );
                    }
                }
            }
            finalOntologyCollection.getOntologySource().add( ontologySource );
        }

        // add ontology individuals only if they have not been added before
        for ( OWLDescription owlDescription : owlDescriptions ) {
            FugeOMCommonOntologyOntologyIndividualType ontologyIndividual = ( FugeOMCommonOntologyOntologyIndividualType ) classAlreadyPresent( finalOntologyCollection, owlDescription.toString(), ontologySource.getIdentifier() );
            if ( ontologyIndividual == null ) {
                ontologyIndividual = new FugeOMCommonOntologyOntologyIndividualType();
                ontologyIndividual.setTermAccession( owlDescription.toString() );
                ontologyIndividual.setIdentifier( ch.getLSID( "fugeOM.Common.Ontology.OntologyIndividual" ) );
                ontologyIndividual.setEndurant( ch.getLSID( "fugeOM.Common.Ontology.OntoIndvEndurant" ) );
                ontologyIndividual.setOntologySourceRef( ontologySource.getIdentifier() );
                for ( OWLAnnotation annotation : owlDescription.asOWLClass().getAnnotations( obi ) ) {
                    if ( annotation.getAnnotationURI().toString().equals( "http://www.w3.org/2000/01/rdf-schema#label" ) ) {
                        // remove the "@en" from the end that is present in the label by default
                        if ( annotation.getAnnotationValue().toString().contains( "@" ) ) {
                            String cleaned = annotation.getAnnotationValue().toString().substring( 0, annotation.getAnnotationValue().toString().indexOf( "@" ) );
                            ontologyIndividual.setTerm( cleaned );
                            // name is simply set to the same value as the term
                            ontologyIndividual.setName( cleaned );
                        } else {
                            ontologyIndividual.setTerm( annotation.getAnnotationValue().toString() );
                            // name is simply set to the same value as the term
                            ontologyIndividual.setName( annotation.getAnnotationValue().toString() );
                        }
                    }
                }
                finalOntologyCollection.getOntologyTerm().add( factory.createOntologyIndividual( ontologyIndividual ) );
            }
        }

        return finalOntologyCollection;
    }

    /**
     * A class is already present If and Only If the ontology uri provided is already present in one of the
     * Ontology Sources provided. This allows the same term to be used in the context of multiple ontology sources,
     * and therefore at different levels of granularity within the SyMBA form interface.
     *
     * @param finalOntologyCollection the ontology collection to check for pre-existing terms and to add new terms to
     * @param termAccession the accession number of the new term - used as the comparison with the pre-existing terms
     * @param ontologySourceIdentifier the ontology source that this new term belongs to: used as an extra comparison
     * @return the pre-exising object, or null if not already present
     */
    private FugeOMCommonOntologyOntologyTermType classAlreadyPresent( FugeOMCollectionOntologyCollectionType finalOntologyCollection, String termAccession, String ontologySourceIdentifier ) {


        for ( javax.xml.bind.JAXBElement<? extends FugeOMCommonOntologyOntologyTermType> ontologyTerm : finalOntologyCollection.getOntologyTerm() ) {
            if ( ontologyTerm.getValue().getTermAccession().equals( termAccession )
                    && ontologyTerm.getValue().getOntologySourceRef().equals( ontologySourceIdentifier ) ) {
                return ontologyTerm.getValue();
            }
        }

        return null;

    }

    /**
     * A class is already present If and Only If it fulfils the following:
     * 1. its term accession provided is already present in one of the Ontology Individuals provided in the
     * OntologyCollection
     * - AND -
     * 2. if the match found in number 1 is in the list of ontology terms, it must belong to the same ontology source
     * as the one provided
     *
     * @param finalOntologyCollection the ontology collection to check for pre-existing sources and to add new sources to
     * @param ontologySourceURI the uri of the new source - used as the comparison with the pre-existing sources
     * @return the pre-exising object, or null if not already present
     */
    private FugeOMCommonOntologyOntologySourceType classAlreadyPresent( FugeOMCollectionOntologyCollectionType finalOntologyCollection, String ontologySourceURI ) {

        for ( FugeOMCommonOntologyOntologySourceType ontologySource : finalOntologyCollection.getOntologySource() ) {
            if ( ontologySource.getOntologyURI().equals( ontologySourceURI ) ) {
                return ontologySource;
            }
        }

        return null;
    }
}
