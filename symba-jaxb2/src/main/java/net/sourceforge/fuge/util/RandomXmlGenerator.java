package net.sourceforge.fuge.util;

import net.sourceforge.fuge.util.generated.*;
import net.sourceforge.fuge.util.generated.AccessRight;
import net.sourceforge.fuge.util.generated.Action;
import net.sourceforge.fuge.util.generated.ActionApplication;
import net.sourceforge.fuge.util.generated.ActionDeviation;
import net.sourceforge.fuge.util.generated.ActionTerm;
import net.sourceforge.fuge.util.generated.Affiliations;
import net.sourceforge.fuge.util.generated.Annotations;
import net.sourceforge.fuge.util.generated.AtomicValue;
import net.sourceforge.fuge.util.generated.Audit;
import net.sourceforge.fuge.util.generated.AuditCollection;
import net.sourceforge.fuge.util.generated.AuditTrail;
import net.sourceforge.fuge.util.generated.BibliographicReference;
import net.sourceforge.fuge.util.generated.BibliographicReferences;
import net.sourceforge.fuge.util.generated.BooleanValue;
import net.sourceforge.fuge.util.generated.Characteristics;
import net.sourceforge.fuge.util.generated.ComplexValue;
import net.sourceforge.fuge.util.generated.Components;
import net.sourceforge.fuge.util.generated.Conclusion;
import net.sourceforge.fuge.util.generated.Contact;
import net.sourceforge.fuge.util.generated.ContactRole;
import net.sourceforge.fuge.util.generated.Data;
import net.sourceforge.fuge.util.generated.DataCollection;
import net.sourceforge.fuge.util.generated.DataProperty;
import net.sourceforge.fuge.util.generated.DataType;
import net.sourceforge.fuge.util.generated.Database;
import net.sourceforge.fuge.util.generated.DatabaseReference;
import net.sourceforge.fuge.util.generated.Describable;
import net.sourceforge.fuge.util.generated.Description;
import net.sourceforge.fuge.util.generated.Descriptions;
import net.sourceforge.fuge.util.generated.EquipmentApplication;
import net.sourceforge.fuge.util.generated.EquipmentParts;
import net.sourceforge.fuge.util.generated.ExternalData;
import net.sourceforge.fuge.util.generated.ExternalFormatDocumentation;
import net.sourceforge.fuge.util.generated.Factor;
import net.sourceforge.fuge.util.generated.FactorType;
import net.sourceforge.fuge.util.generated.FactorValue;
import net.sourceforge.fuge.util.generated.FileFormat;
import net.sourceforge.fuge.util.generated.FuGE;
import net.sourceforge.fuge.util.generated.GenericAction;
import net.sourceforge.fuge.util.generated.GenericEquipment;
import net.sourceforge.fuge.util.generated.GenericEquipmentSoftwareRef;
import net.sourceforge.fuge.util.generated.GenericMaterial;
import net.sourceforge.fuge.util.generated.GenericMaterialMeasurement;
import net.sourceforge.fuge.util.generated.GenericParameter;
import net.sourceforge.fuge.util.generated.GenericProtocol;
import net.sourceforge.fuge.util.generated.GenericProtocolApplication;
import net.sourceforge.fuge.util.generated.GenericProtocolEquipmentRef;
import net.sourceforge.fuge.util.generated.GenericProtocolSoftwareRef;
import net.sourceforge.fuge.util.generated.GenericSoftware;
import net.sourceforge.fuge.util.generated.GenericSoftwareEquipmentRef;
import net.sourceforge.fuge.util.generated.Hypothesis;
import net.sourceforge.fuge.util.generated.Identifiable;
import net.sourceforge.fuge.util.generated.InputCompleteMaterials;
import net.sourceforge.fuge.util.generated.InputData;
import net.sourceforge.fuge.util.generated.InputTypes;
import net.sourceforge.fuge.util.generated.Investigation;
import net.sourceforge.fuge.util.generated.InvestigationCollection;
import net.sourceforge.fuge.util.generated.Make;
import net.sourceforge.fuge.util.generated.Material;
import net.sourceforge.fuge.util.generated.MaterialCollection;
import net.sourceforge.fuge.util.generated.MaterialType;
import net.sourceforge.fuge.util.generated.Measurement;
import net.sourceforge.fuge.util.generated.Members;
import net.sourceforge.fuge.util.generated.Model;
import net.sourceforge.fuge.util.generated.NameValueType;
import net.sourceforge.fuge.util.generated.ObjectFactory;
import net.sourceforge.fuge.util.generated.ObjectProperty;
import net.sourceforge.fuge.util.generated.OntologyCollection;
import net.sourceforge.fuge.util.generated.OntologyIndividual;
import net.sourceforge.fuge.util.generated.OntologySource;
import net.sourceforge.fuge.util.generated.OntologyTerm;
import net.sourceforge.fuge.util.generated.Organization;
import net.sourceforge.fuge.util.generated.OutputData;
import net.sourceforge.fuge.util.generated.OutputMaterials;
import net.sourceforge.fuge.util.generated.OutputTypes;
import net.sourceforge.fuge.util.generated.Owners;
import net.sourceforge.fuge.util.generated.Parameter;
import net.sourceforge.fuge.util.generated.ParameterType;
import net.sourceforge.fuge.util.generated.ParameterValue;
import net.sourceforge.fuge.util.generated.Parameterizable;
import net.sourceforge.fuge.util.generated.ParameterizableApplication;
import net.sourceforge.fuge.util.generated.ParameterizableParamType;
import net.sourceforge.fuge.util.generated.Parent;
import net.sourceforge.fuge.util.generated.Person;
import net.sourceforge.fuge.util.generated.PropertySets;
import net.sourceforge.fuge.util.generated.Protocol;
import net.sourceforge.fuge.util.generated.ProtocolApplication;
import net.sourceforge.fuge.util.generated.ProtocolCollection;
import net.sourceforge.fuge.util.generated.ProtocolDeviation;
import net.sourceforge.fuge.util.generated.Provider;
import net.sourceforge.fuge.util.generated.QualityControlStatistics;
import net.sourceforge.fuge.util.generated.Range;
import net.sourceforge.fuge.util.generated.ReferenceableCollection;
import net.sourceforge.fuge.util.generated.Role;
import net.sourceforge.fuge.util.generated.Security;
import net.sourceforge.fuge.util.generated.SecurityAccess;
import net.sourceforge.fuge.util.generated.SecurityGroup;
import net.sourceforge.fuge.util.generated.Software;
import net.sourceforge.fuge.util.generated.SoftwareApplication;
import net.sourceforge.fuge.util.generated.Unit;
import net.sourceforge.fuge.util.generated.Uri;
import net.sourceforge.fuge.util.generated.UriType;
import net.sourceforge.fuge.util.generated.Value;
import net.sourceforge.fuge.util.identification.FuGEIdentifier;
import net.sourceforge.fuge.util.identification.FuGEIdentifierFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

/**
 * GenerateRandomXML is an class that will generate 3 basic XML files that fit with the auto-generated FuGE XSD.
 * You will need to modify this class - or create a new one - if you wish to use it for a community extension you
 * built yourself.
 * <p/>
 * Copyright Notice
 * <p/>
 * The MIT License
 * <p/>
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * Acknowledgements
 * The authors wish to thank the Proteomics Standards Initiative for
 * the provision of infrastructure and expertise in the form of the PSI
 * Document Process that has been used to formalise this document.
 * This file is based on code originally part of SyMBA (http://symba.sourceforge.net).
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class RandomXmlGenerator {

    private static int ELEMENT_DEPTH = 1;

    /**
     * Generates the random XML files with the default element depth of 1, and without validating against a schema
     *
     * @param xmlFilename the xml file to write out to
     * @return the newly-created JAXB object
     * @throws JAXBException         if there is a problem creating the xml document
     * @throws SAXException          if there is an error adding the schema to use for validating
     * @throws FileNotFoundException @param xmlFilename the name of the file to write to
     */
    public static FuGE generate( String xmlFilename ) throws JAXBException, SAXException,
            FileNotFoundException {

        return generate( "", xmlFilename );

    }

    /**
     * Generates the random XML files with the provided element depth, and without validating against a schema
     *
     * @param xmlFilename the xml file to write out to
     * @param depth       the number of elements to make for each aspect of the XML file (i.e. how deep to go).
     * @return the newly-created JAXB object
     * @throws JAXBException         if there is a problem creating the xml document
     * @throws SAXException          if there is an error adding the schema to use for validating
     * @throws FileNotFoundException @param xmlFilename the name of the file to write to
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public static FuGE generate( String xmlFilename,
                                                   int depth ) throws
            JAXBException,
            SAXException,
            FileNotFoundException {

        ELEMENT_DEPTH = depth;
        return generate( "", xmlFilename );

    }

    /**
     * Generates the random XML files with the provided element depth.
     *
     * @param xmlFilename    the xml file to write out to
     * @param depth          the number of elements to make for each aspect of the XML file (i.e. how deep to go).
     * @param schemaFilename the schema to validate against
     * @return the newly-created JAXB object
     * @throws JAXBException         if there is a problem creating the xml document
     * @throws SAXException          if there is an error adding the schema to use for validating
     * @throws FileNotFoundException @param xmlFilename the name of the file to write to
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public static FuGE generate( String schemaFilename,
                                                   String xmlFilename,
                                                   int depth ) throws
            JAXBException,
            SAXException,
            FileNotFoundException {

        ELEMENT_DEPTH = depth;
        return generate( schemaFilename, xmlFilename );

    }


    /**
     * Generates the random XML files. Defaults to a element depth of 1.
     *
     * @param xmlFilename    the xml file to write out to
     * @param schemaFilename the schema to validate against
     * @return the newly-created JAXB object
     * @throws JAXBException         if there is a problem creating the xml document
     * @throws SAXException          if there is an error adding the schema to use for validating
     * @throws FileNotFoundException @param xmlFilename the name of the file to write to
     */
    public static FuGE generate( String schemaFilename,
                                                   String xmlFilename ) throws
            JAXBException,
            SAXException,
            FileNotFoundException {

        FuGE rootXML;

        OutputStream os;

        System.err.println( "Schema file is: " + schemaFilename );
        System.err.println( "File for XML output is: " + xmlFilename );

        // create a JAXBContext capable of handling classes generated into the net.sourceforge.fuge.util.generatedJAXB2
        // package
        JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generated" );

        System.err.println( "JAXB Context created." );

        // create a Marshaller
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.setEventHandler(
                new ValidationEventHandler() {
                    public boolean handleEvent( ValidationEvent e ) {
                        System.out.println( "Validation Error: " + e.getMessage() );
                        return false;
                    }
                } );

        if ( schemaFilename.length() > 0 ) {
            // set the correct schema
            SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
            Schema schema = sf.newSchema( new File( schemaFilename ) );
            m.setSchema( schema );
        }

        System.err.println( "Marshaller initialized." );

        // create a jaxb root object
        System.err.println( "Starting generation..." );

        rootXML = generateRandomFuGEXML();

        os = new FileOutputStream( xmlFilename );
        @SuppressWarnings( "unchecked" )
        JAXBElement element = new JAXBElement(
                new QName( "http://fuge.sourceforge.net/fuge/1.0", "FuGE" ),
                FuGE.class,
                rootXML );
        m.marshal( element, os );

        System.err.println( "Generation complete." );

        return rootXML;

    }

    /**
     * Public container class for all of the random generation methods
     *
     * @return the fuge jaxb2 object containing the randomly-generated information
     */
    private static FuGE generateRandomFuGEXML() {

        FuGE fugeXML = new FuGE();

        // generate identifiable traits
        fugeXML = ( FuGE ) generateRandomIdentifiableXML( fugeXML );

        // generate AuditCollection information
        if ( fugeXML.getAuditCollection() == null ) {
            fugeXML = generateRandomAuditCollectionXML( fugeXML );
        }

        // generate OntologyCollection information
        if ( fugeXML.getOntologyCollection() == null ) {
            fugeXML = generateRandomOntologyCollectionXML( fugeXML );
        }

        // generate ReferenceableCollection information
        if ( fugeXML.getReferenceableCollection() == null ) {
            fugeXML = generateRandomReferenceableCollectionXML( fugeXML );
        }

        // Get all MaterialCollection information
        if ( fugeXML.getMaterialCollection() == null ) {
            fugeXML = generateRandomMaterialCollectionXML( fugeXML );
        }

        // Get all data collection information - MUST BE DONE before Protocol and after Material
        if ( fugeXML.getDataCollection() == null ) {
            fugeXML = generateRandomDataCollectionXML( fugeXML );
        }

        // Get all protocol collection information
        if ( fugeXML.getProtocolCollection() == null ) {
            // marshall the fuge object into a jaxb object
            fugeXML = generateRandomProtocolCollectionXML( fugeXML );
        }

        // Get all Provider information
        if ( fugeXML.getProvider() == null ) {
            // marshall the fuge object into a jaxb object
            fugeXML = generateRandomProviderXML( fugeXML );
        }

        // Get an Investigation, if present
        if ( fugeXML.getInvestigationCollection() == null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fugeXML = generateRandomInvestigationCollectionXML( fugeXML );
        }

        return fugeXML;
    }

    /**
     * Generates a random JAXB2 InvestigationCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to add the investigation collection to
     * @return the modified fuge object, having the newly-created investigation collection within it
     */
    private static FuGE generateRandomInvestigationCollectionXML( FuGE fugeXML ) {

        InvestigationCollection investigationCollectionXML =
                new InvestigationCollection();
        investigationCollectionXML = ( InvestigationCollection ) generateRandomDescribableXML(
                investigationCollectionXML );

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            Investigation investigationXML = new Investigation();

            investigationXML =
                    ( Investigation ) generateRandomIdentifiableXML( investigationXML );

            // top-level hypothesis
            Hypothesis hypothesisXML =
                    new Hypothesis();
            Description descriptionXML = new Description();
            descriptionXML.setText( "Some Hypothesis " + String.valueOf( Math.random() ) );
            hypothesisXML.setDescription( descriptionXML );
            investigationXML.setHypothesis( hypothesisXML );

            // top-level conclusion
            Conclusion conclusionXML =
                    new Conclusion();
            descriptionXML = new Description();
            descriptionXML.setText( "Some Conclusion " + String.valueOf( Math.random() ) );
            conclusionXML.setDescription( descriptionXML );
            investigationXML.setConclusion( conclusionXML );

            investigationCollectionXML.getInvestigation().add( investigationXML );
            Factor factorXML = new Factor();

            factorXML = ( Factor ) generateRandomIdentifiableXML( factorXML );

            // set the non-identifiable traits

            if ( fugeXML.getOntologyCollection() != null ) {
                FactorType categoryValueXML =
                        new FactorType();
                categoryValueXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                factorXML.setFactorType( categoryValueXML );
            }

            ObjectFactory factory = new ObjectFactory();
            for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                FactorValue factorValueXML = new FactorValue();
                factorValueXML = ( FactorValue ) generateRandomDescribableXML( factorValueXML );
                if ( fugeXML.getOntologyCollection() != null ) {
                    BooleanValue valueXML = ( BooleanValue )
                            generateRandomMeasurementXML( new BooleanValue(), fugeXML );
                    factorValueXML.setMeasurement( factory.createBooleanValue( valueXML ) );
                }

// todo still not sure where datapartitions fit in, so won't make them for now.
//                for ( int iii = 0; iii < ELEMENT_DEPTH; iii++ ) {
//                    FactorValue.DataPartitions dataPartitionXML = new FactorValue.DataPartitions();
//                    if ( fugeXML.getDataCollection() != null ) {
//                        dataPartitionXML.setDataPartitionRef( fugeXML.getDataCollection().getData().get( iii ).getValue().getIdentifier() );
//                        // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
//                        factorValueXML.getDataPartitions().add( dataPartitionXML );
//                    }
//                }
                factorXML.getFactorValue().add( factorValueXML );
            }
            investigationCollectionXML.getFactor().add( factorXML );
        }

        fugeXML.setInvestigationCollection( investigationCollectionXML );
        return fugeXML;

    }

    /**
     * Generates a random JAXB2 Provider element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to get the software reference from, and add the provider to
     * @return the modified fuge object, having the newly-created provider within it
     */
    private static FuGE generateRandomProviderXML( FuGE fugeXML ) {
        // create fuge object
        Provider providerXML = new Provider();

        providerXML = ( Provider ) generateRandomIdentifiableXML( providerXML );

        providerXML.setContactRole( generateRandomContactRoleXML( fugeXML ) );

        if ( fugeXML.getProtocolCollection() != null ) {
            providerXML.setSoftwareRef(
                    fugeXML.getProtocolCollection().getSoftware().get( 0 ).getValue().getIdentifier() );
        }
        fugeXML.setProvider( providerXML );
        return fugeXML;
    }

    /**
     * Generates a random JAXB2 ProtocolCollection and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to add the protocol collection to
     * @return the modified fuge object, having the newly-created protocol collection within it
     */
    private static FuGE generateRandomProtocolCollectionXML( FuGE fugeXML ) {
        ProtocolCollection protocolCollectionXML = new ProtocolCollection();

        protocolCollectionXML = ( ProtocolCollection ) generateRandomDescribableXML(
                protocolCollectionXML );

        if ( protocolCollectionXML.getEquipment().isEmpty() ) {
            // equipment
            protocolCollectionXML = generateRandomEquipmentXML( protocolCollectionXML, fugeXML );
        }

        if ( protocolCollectionXML.getSoftware().isEmpty() ) {
            // software
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                GenericSoftware genericSoftwareXML = new GenericSoftware();
                genericSoftwareXML = ( GenericSoftware ) generateRandomSoftwareXML(
                        genericSoftwareXML, protocolCollectionXML, fugeXML );
                JAXBElement<? extends GenericSoftware> element = ( new ObjectFactory() )
                        .createGenericSoftware( genericSoftwareXML );
                protocolCollectionXML.getSoftware().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocol().isEmpty() ) {
            // protocol
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                GenericProtocol genericProtocolXML = new GenericProtocol();
                genericProtocolXML = ( GenericProtocol ) generateRandomProtocolXML(
                        genericProtocolXML, protocolCollectionXML, fugeXML );
                JAXBElement<? extends GenericProtocol> element = ( new ObjectFactory() )
                        .createGenericProtocol( genericProtocolXML );
                protocolCollectionXML.getProtocol().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocolApplication().isEmpty() ) {
            // protocol application
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                JAXBElement<? extends GenericProtocolApplication> element =
                        ( new ObjectFactory() )
                                .createGenericProtocolApplication(
                                        ( GenericProtocolApplication ) generateRandomProtocolApplicationXML(
                                                i, protocolCollectionXML, fugeXML ) );
                protocolCollectionXML.getProtocolApplication().add( element );
            }
        }

        fugeXML.setProtocolCollection( protocolCollectionXML );
        return fugeXML;
    }

    /**
     * At this stage, rootXML may not have the new equipment and software - the protocol collection may be the only one to have it
     *
     * @param protocolXML           the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML required to check values from
     * @param fugeXML               the object to get various bits of information to populate the protocol with
     * @return a random JAXB2 Protocol
     */
    private static Protocol generateRandomProtocolXML( Protocol protocolXML,
                                                                             ProtocolCollection protocolCollectionXML,
                                                                             FuGE fugeXML ) {
        GenericProtocol genericProtocolXML =
                ( GenericProtocol ) protocolXML;

        // get protocol attributes
        genericProtocolXML =
                ( GenericProtocol ) generateRandomIdentifiableXML( genericProtocolXML );

        genericProtocolXML = ( GenericProtocol ) generateRandomParameterizableXML(
                genericProtocolXML, fugeXML );

        if ( fugeXML.getOntologyCollection() != null ) {
            // input types
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                InputTypes inputTypesXML =
                        new InputTypes();
                inputTypesXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getInputTypes().add( inputTypesXML );
            }

            // output types
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                OutputTypes outputTypesXML =
                        new OutputTypes();
                outputTypesXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getOutputTypes().add( outputTypesXML );
            }
        }

        genericProtocolXML = generateRandomGenericProtocolXML( genericProtocolXML, protocolCollectionXML, fugeXML );
        return genericProtocolXML;
    }

    /**
     * @param genericProtocolXML    the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get information from
     * @return a random JAXB2 GenericProtocol
     */
    private static GenericProtocol generateRandomGenericProtocolXML(
            GenericProtocol genericProtocolXML,
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        ObjectFactory factory = new ObjectFactory();

        // can only have generic actions.
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            genericProtocolXML.getAction().add(
                    factory.createGenericAction( generateRandomActionXML(
                            new GenericAction(), i, protocolCollectionXML, fugeXML ) ) );
        }
        // can only have generic parameters
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            genericProtocolXML.getGenericParameter().add(
                    ( GenericParameter )
                            generateRandomParameterXML( new GenericParameter(), fugeXML ) );
        }

        if ( protocolCollectionXML != null ) {
            // protocol to equipment
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                GenericProtocolEquipmentRef equip =
                        new GenericProtocolEquipmentRef();
                equip.setGenericEquipmentRef(
                        protocolCollectionXML.getEquipment().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getEquipment().add( equip );
            }
            // software
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                GenericProtocolSoftwareRef genSoftware =
                        new GenericProtocolSoftwareRef();
                genSoftware.setGenericSoftwareRef(
                        protocolCollectionXML.getSoftware().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getSoftware().add( genSoftware );
            }
        }
        return genericProtocolXML;
    }

    /**
     * @param actionXML             the JAXB2 object that is returned with attributes filled
     * @param ordinal               the position in an exterior loop controlling how many elements are generated
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get some information from
     * @return a random JAXB2 Action
     */
    private static GenericAction generateRandomActionXML( Action actionXML,
                                                                                int ordinal,
                                                                                ProtocolCollection protocolCollectionXML,
                                                                                FuGE fugeXML ) {

        GenericAction genericActionXML = ( GenericAction ) actionXML;

        // get action attributes
        genericActionXML = ( GenericAction ) generateRandomIdentifiableXML( genericActionXML );

        // action ordinal
        genericActionXML.setActionOrdinal( ordinal );

        // get generic action attributes
        genericActionXML = generateRandomGenericActionXML( genericActionXML, protocolCollectionXML, fugeXML );

        return genericActionXML;

    }

    /**
     * @param genericActionXML      the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get some information from
     * @return a random JAXB2 GenericAction
     */
    private static GenericAction generateRandomGenericActionXML(
            GenericAction genericActionXML,
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        genericActionXML.setActionText( String.valueOf( Math.random() ) );

        // action term
        if ( fugeXML.getOntologyCollection() != null ) {
            ActionTerm aterm = new ActionTerm();
            aterm.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            genericActionXML.setActionTerm( aterm );
        }

        // protocol ref
        if ( protocolCollectionXML.getProtocol().size() > 0 ) {
            genericActionXML.setProtocolRef( protocolCollectionXML.getProtocol().get( 0 ).getValue().getIdentifier() );
        }

        // you can only have a GenericParameter here
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            genericActionXML.getGenericParameter().add(
                    ( GenericParameter ) generateRandomParameterXML(
                            new GenericParameter(), fugeXML ) );
        }

        return genericActionXML;
    }

    /**
     * at this stage, rootXML may not have the new equipment and software - the protocol collection may be the only one to have it
     *
     * @param ordinal               the position in an exterior loop controlling how many elements are generated
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get some information from
     * @return a random JAXB2 ProtocolApplication
     */
    private static ProtocolApplication generateRandomProtocolApplicationXML( int ordinal,
                                                                                                   ProtocolCollection protocolCollectionXML,
                                                                                                   FuGE fugeXML ) {
        GenericProtocolApplication genericProtocolApplicationXML =
                new GenericProtocolApplication();

        // get protocol application attributes
        genericProtocolApplicationXML =
                ( GenericProtocolApplication ) generateRandomIdentifiableXML(
                        genericProtocolApplicationXML );
        genericProtocolApplicationXML =
                ( GenericProtocolApplication ) generateRandomParameterizableApplicationXML(
                        genericProtocolApplicationXML, protocolCollectionXML, fugeXML );

        try {
            genericProtocolApplicationXML.setActivityDate(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar() ) );
        } catch ( DatatypeConfigurationException e ) {
            throw new RuntimeException( "Error creating XMLGregorianCalendar from current date", e );
        }
        if ( !protocolCollectionXML.getSoftware().isEmpty() ) {
            SoftwareApplication type = new SoftwareApplication();
            type = ( SoftwareApplication ) generateRandomIdentifiableXML( type );
            type.setSoftwareRef( protocolCollectionXML.getSoftware().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getSoftwareApplication().add( type );
        }
        if ( !protocolCollectionXML.getEquipment().isEmpty() ) {
            EquipmentApplication type = new EquipmentApplication();
            type = ( EquipmentApplication ) generateRandomIdentifiableXML( type );
            type.setEquipmentRef( protocolCollectionXML.getEquipment().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getEquipmentApplication().add( type );
        }
        if ( !protocolCollectionXML.getProtocol().isEmpty() ) {
            ActionApplication type = new ActionApplication();
            type = ( ActionApplication ) generateRandomIdentifiableXML( type );

            GenericProtocol gpType =
                    ( GenericProtocol ) protocolCollectionXML
                            .getProtocol().get( ordinal ).getValue();
            type.setActionRef( gpType.getAction().get( ordinal ).getValue().getIdentifier() );
            Description descXML = new Description();
            descXML = ( Description ) generateRandomDescribableXML( descXML );
            descXML.setText( String.valueOf( Math.random() ) );
            ActionDeviation deviation =
                    new ActionDeviation();
            deviation.setDescription( descXML );

            type.setActionDeviation( deviation );
            genericProtocolApplicationXML.getActionApplication().add( type );

            // ensure we only add one of these, by only adding when the ordinal is exactly 1. This isn't perfect,
            // but it's hard to ensure that we only ever have 0 or 1 links between action application and its
            // child protocol application
            if ( ordinal == 1 ) {
                type.setProtocolApplicationRef(
                        protocolCollectionXML.getProtocolApplication().get( 0 ).getValue().getIdentifier() );
            }
        }

        ProtocolDeviation pdXML =
                new ProtocolDeviation();
        Description descXML = new Description();
        descXML = ( Description ) generateRandomDescribableXML( descXML );
        descXML.setText( String.valueOf( Math.random() ) );
        pdXML.setDescription( descXML );
        genericProtocolApplicationXML.setProtocolDeviation( pdXML );

        genericProtocolApplicationXML = generateRandomGenericProtocolApplicationXML( genericProtocolApplicationXML,
                ordinal, protocolCollectionXML, fugeXML );
        return genericProtocolApplicationXML;
    }

    /**
     * @param genericProtocolApplicationXML the JAXB2 object that is returned with attributes filled
     * @param ordinal                       the position in an exterior loop controlling how many elements are generated
     * @param protocolCollectionXML         needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML                       the object to get some information from for populating the GPA
     * @return a random JAXB2 GenericProtocolApplication
     */
    private static GenericProtocolApplication generateRandomGenericProtocolApplicationXML(
            GenericProtocolApplication genericProtocolApplicationXML,
            int ordinal,
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        if ( !protocolCollectionXML.getProtocol().isEmpty() ) {
            genericProtocolApplicationXML.setProtocolRef(
                    protocolCollectionXML.getProtocol().get( ordinal ).getValue().getIdentifier() );
        }

        int output = ordinal + 1;
        if ( ordinal == ELEMENT_DEPTH - 1 ) {
            output = 0;
        }

        if ( fugeXML.getDataCollection() != null ) {
            // input data
            InputData gidXML =
                    new InputData();
            gidXML.setDataRef( fugeXML.getDataCollection().getData().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getInputData().add( gidXML );

            // output data
            OutputData godXML =
                    new OutputData();
            godXML.setDataRef( fugeXML.getDataCollection().getData().get( output ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getOutputData().add( godXML );
        }

        if ( fugeXML.getMaterialCollection() != null ) {
            // input complete material
            InputCompleteMaterials icmXML =
                    new InputCompleteMaterials();
            icmXML.setMaterialRef(
                    fugeXML.getMaterialCollection().getMaterial().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getInputCompleteMaterials().add( icmXML );

            // input material
            GenericMaterialMeasurement gmmXML = new GenericMaterialMeasurement();
            gmmXML.setMaterialRef(
                    fugeXML.getMaterialCollection().getMaterial().get( ordinal ).getValue().getIdentifier() );
            ObjectFactory factory = new ObjectFactory();
            Range valueXML = ( Range )
                    generateRandomMeasurementXML( new Range(), fugeXML );
            gmmXML.setMeasurement( factory.createRange( valueXML ) );
            genericProtocolApplicationXML.getGenericMaterialMeasurement().add( gmmXML );

            // output material
            OutputMaterials gomXML =
                    new OutputMaterials();
            gomXML.setMaterialRef(
                    fugeXML.getMaterialCollection().getMaterial().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getOutputMaterials().add( gomXML );
        }

        return genericProtocolApplicationXML;
    }

    /**
     * @param parameterizableApplicationXML the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML         needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML                       the object to get some information from for populating the parameterizable application
     * @return a random JAXB2 ParameterizableApplication
     */
    private static ParameterizableApplication generateRandomParameterizableApplicationXML(
            ParameterizableApplication parameterizableApplicationXML,
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        parameterizableApplicationXML =
                ( ParameterizableApplication ) generateRandomIdentifiableXML(
                        parameterizableApplicationXML );

        if ( !protocolCollectionXML.getEquipment().isEmpty() ) {
            GenericEquipment eqXML =
                    ( GenericEquipment ) protocolCollectionXML
                            .getEquipment()
                            .get( 0 )
                            .getValue();
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                ParameterValue pvalueXML = new ParameterValue();
                pvalueXML = ( ParameterValue ) generateRandomDescribableXML( pvalueXML );
                pvalueXML.setParameterRef( eqXML.getGenericParameter().get( 0 ).getIdentifier() );
                pvalueXML.setMeasurement( ( new ObjectFactory() ).createAtomicValue(
                        ( AtomicValue ) generateRandomMeasurementXML(
                                new AtomicValue(), fugeXML ) ) );
                parameterizableApplicationXML.getParameterValue().add( pvalueXML );
            }
        }
        return parameterizableApplicationXML;
    }

    /**
     * this method is different from the others in that it will generate ALL equipment
     * in one go, rather than just one piece of equipment. This is because software may not
     * have been made yet, and so this method needs protocolCollection changeable so that it can add
     * software if necessary.
     *
     * @param protocolCollectionXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML               the object to get some information from for populating the equipment
     * @return a random JAXB2 ProtocolCollection
     */
    private static ProtocolCollection generateRandomEquipmentXML(
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            GenericEquipment genEquipmentXML = new GenericEquipment();

            genEquipmentXML =
                    ( GenericEquipment ) generateRandomIdentifiableXML( genEquipmentXML );


            genEquipmentXML = ( GenericEquipment ) generateRandomParameterizableXML(
                    genEquipmentXML, fugeXML );

            if ( fugeXML.getOntologyCollection() != null ) {
                Make make = new Make();
                make.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setMake( make );

                Model model = new Model();
                model.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setModel( model );
            }

            // software required for generic equipment attributes
            if ( protocolCollectionXML.getSoftware() == null ) {
                for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                    GenericSoftware genericSoftwareXML =
                            new GenericSoftware();
                    genericSoftwareXML = ( GenericSoftware ) generateRandomSoftwareXML(
                            genericSoftwareXML, protocolCollectionXML, fugeXML );
                    JAXBElement<? extends GenericSoftware> element =
                            ( new ObjectFactory() ).createGenericSoftware(
                                    genericSoftwareXML );
                    protocolCollectionXML.getSoftware().add( element );
                }
            }
            // get generic equipment attributes
            if ( i > 0 ) {
                genEquipmentXML = generateRandomGenericEquipmentXML(
                        genEquipmentXML,
                        ( GenericEquipment ) protocolCollectionXML.getEquipment()
                                .get( 0 )
                                .getValue(),
                        fugeXML );
            } else {
                genEquipmentXML = generateRandomGenericEquipmentXML( genEquipmentXML, null, fugeXML );
            }

            JAXBElement<? extends GenericEquipment> element =
                    ( new ObjectFactory() ).createGenericEquipment(
                            genEquipmentXML );
            protocolCollectionXML.getEquipment().add( element );
        }
        return protocolCollectionXML;

    }

    /**
     * @param genericEquipmentXML the JAXB2 object that is returned with attributes filled
     * @param partXML             the parts list for this equipment
     * @param fugeXML             the object to get some information from for populating the generic equipment
     * @return a random JAXB2 GenericEquipment
     */
    private static GenericEquipment generateRandomGenericEquipmentXML(
            GenericEquipment genericEquipmentXML,
            GenericEquipment partXML,
            FuGE fugeXML ) {

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            GenericParameter parameterXML = new GenericParameter();
            genericEquipmentXML.getGenericParameter()
                    .add( ( GenericParameter ) generateRandomParameterXML( parameterXML,
                            fugeXML ) );
        }

        if ( partXML != null ) {
            // parts list of one
            EquipmentParts parts =
                    new EquipmentParts();
            parts.setGenericEquipmentRef( partXML.getIdentifier() );
            genericEquipmentXML.getEquipmentParts().add( parts );
        }

        if ( fugeXML.getProtocolCollection() != null ) {
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                GenericEquipmentSoftwareRef softwareXML =
                        new GenericEquipmentSoftwareRef();
                softwareXML.setGenericSoftwareRef(
                        fugeXML.getProtocolCollection().getSoftware().get( i ).getValue().getIdentifier() );
                genericEquipmentXML.getSoftware().add( softwareXML );
            }
        }
        return genericEquipmentXML;

    }

    /**
     * at this stage, rootXML may not have the new equipment and software - the protocol collection may be the only one to have it
     *
     * @param softwareXML           the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get some information from for populating the software
     * @return a random JAXB2 Software Object
     */
    private static Software generateRandomSoftwareXML( Software softwareXML,
                                                                             ProtocolCollection protocolCollectionXML,
                                                                             FuGE fugeXML ) {
        // make software attributes

        if ( softwareXML instanceof GenericSoftware ) {
            GenericSoftware genericSoftwareXML =
                    ( GenericSoftware ) softwareXML;
            genericSoftwareXML = ( GenericSoftware ) generateRandomIdentifiableXML(
                    genericSoftwareXML );
            genericSoftwareXML = ( GenericSoftware ) generateRandomParameterizableXML(
                    genericSoftwareXML, fugeXML );

            genericSoftwareXML.setVersion( String.valueOf( Math.random() ) );

            // get generic software attributes
            genericSoftwareXML = generateRandomGenericSoftwareXML( genericSoftwareXML, protocolCollectionXML, fugeXML );

            return genericSoftwareXML;
        }

        return softwareXML;

    }

    /**
     * @param genericSoftwareXML    the JAXB2 object that is returned with attributes filled
     * @param protocolCollectionXML needed for the information contained within it to make the JAXB2 object correctly
     * @param fugeXML               the object to get some information from for populating the software
     * @return a random JAXB2 GenericSoftware
     */
    private static GenericSoftware generateRandomGenericSoftwareXML(
            GenericSoftware genericSoftwareXML,
            ProtocolCollection protocolCollectionXML,
            FuGE fugeXML ) {

        // you can only have a GenericParameter here
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            genericSoftwareXML.getGenericParameter().add(
                    ( GenericParameter ) generateRandomParameterXML(
                            new GenericParameter(), fugeXML ) );
        }

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
             GenericSoftwareEquipmentRef parts =
                    new GenericSoftwareEquipmentRef();
            if ( !protocolCollectionXML.getEquipment().isEmpty() && protocolCollectionXML.getEquipment().size() > i ) {
                parts.setGenericEquipmentRef(
                        protocolCollectionXML.getEquipment().get( i ).getValue().getIdentifier() );
                genericSoftwareXML.getEquipment().add( parts );
            }
        }
        return genericSoftwareXML;

    }

    /**
     * @param parameterXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML      the object to get some information from for populating the parameter
     * @return a random JAXB2 Parameter
     */
    private static Parameter generateRandomParameterXML( Parameter parameterXML,
                                                                               FuGE fugeXML ) {

        // get parameter attributes
        parameterXML = ( Parameter ) generateRandomIdentifiableXML( parameterXML );

        parameterXML.setIsInputParam( true );

        // measurement
        ComplexValue measurementXML =
                ( ComplexValue ) generateRandomMeasurementXML(
                        new ComplexValue(), fugeXML );
        parameterXML.setMeasurement( ( new ObjectFactory() ).createComplexValue( measurementXML ) );

        // get generic parameter attributes
        parameterXML =
                generateRandomGenericParameterXML( ( GenericParameter ) parameterXML, fugeXML );
        return parameterXML;

    }

    /**
     * @param genericParameterXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML             the object to get some information from for populating the parameter
     * @return a random JAXB2 GenericParameter
     */
    private static GenericParameter generateRandomGenericParameterXML(
            GenericParameter genericParameterXML,
            FuGE fugeXML ) {

        if ( fugeXML.getOntologyCollection() != null ) {
            ParameterType ptXML =
                    new ParameterType();
            ptXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            genericParameterXML.setParameterType( ptXML );
        }
        return genericParameterXML;
    }

    /**
     * todo deal with propertySets
     *
     * @param defaultXML the measurement object to populate with random information
     * @param fugeXML    the object to get some information from for populating the measurement
     * @return an AtomicValue Measurement wrapped in a JAXB2 element
     */
    private static Measurement generateRandomMeasurementXML(
            Measurement defaultXML,
            FuGE fugeXML ) {

        defaultXML = ( Measurement ) generateRandomDescribableXML( defaultXML );

        // add a unit and a data type
        if ( fugeXML.getOntologyCollection().getOntologyTerm().size() >= 2 ) {
            Unit unitXML = new Unit();
            unitXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            defaultXML.setUnit( unitXML );

            DataType dataTypeXML =
                    new DataType();
            dataTypeXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 1 ).getValue().getIdentifier() );
            defaultXML.setDataType( dataTypeXML );
        }

        if ( defaultXML instanceof AtomicValue ) {
            // get atomic value attributes
            defaultXML = generateRandomAtomicValueXML( ( AtomicValue ) defaultXML );
            return ( defaultXML );
        } else if ( defaultXML instanceof BooleanValue ) {
            // get boolean value attributes
            defaultXML = generateRandomBooleanValueXML( ( BooleanValue ) defaultXML );
            return ( defaultXML );
        }
        if ( defaultXML instanceof ComplexValue ) {
            // get complex value attributes
            defaultXML = generateRandomComplexValueXML( ( ComplexValue ) defaultXML, fugeXML );
            return ( defaultXML );
        } else if ( defaultXML instanceof Range ) {
            // get Range attributes
            // todo:implement with the proper random xml, with ranges and ontology references.
            defaultXML = generateRandomRangeXML( ( Range ) defaultXML );
            return ( defaultXML );
        }

        return defaultXML;

    }

    private static Range generateRandomRangeXML( Range valueXML ) {
        valueXML.setLowerLimit( "some lower limit" );
        valueXML.setUpperLimit( "some upper limit" );
        return valueXML;
    }

    private static ComplexValue generateRandomComplexValueXML(
            ComplexValue valueXML,
            FuGE fugeXML ) {
        if ( fugeXML.getOntologyCollection() != null ) {
            Value defaultValueXML =
                    new Value();
            defaultValueXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            valueXML.setValue( defaultValueXML );
        }
        return valueXML;
    }

    private static BooleanValue generateRandomBooleanValueXML(
            BooleanValue valueXML ) {
        valueXML.setValue( true );
        return valueXML;
    }

    private static AtomicValue generateRandomAtomicValueXML(
            AtomicValue valueXML ) {
        valueXML.setValue( "5" );
        return valueXML;
    }

    /**
     * @param parameterizableXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML            the object to get some information from for populating the parameterizable object
     * @return a random JAXB2 Parameterizable
     */
    private static Parameterizable generateRandomParameterizableXML(
            Parameterizable parameterizableXML,
            FuGE fugeXML ) {

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            parameterizableXML.getContactRole().add( generateRandomContactRoleXML( fugeXML ) );
        }

        if ( fugeXML.getOntologyCollection() != null ) {
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                ParameterizableParamType parameterizableTypesXML =
                        new ParameterizableParamType();
                parameterizableTypesXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                parameterizableXML.getTypes().add( parameterizableTypesXML );
            }
        }
        return parameterizableXML;
    }

    /**
     * todo deal with InternalData
     * <p/>
     * Generates a random JAXB2 DataCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to add the data collection to
     * @return the modified fuge object
     */
    private static FuGE generateRandomDataCollectionXML( FuGE fugeXML ) {

        // create the jaxb Data collection object
        DataCollection datCollXML = new DataCollection();

        // set describable information
        datCollXML = ( DataCollection ) generateRandomDescribableXML( datCollXML );

        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {

            // As RawData objects do not appear in the XML, there is no need to code it here.
            datCollXML.getData().add(
                    factory.createExternalData(
                            ( ExternalData ) generateRandomDataXML(
                                    new ExternalData(), fugeXML ) ) );
        }

        fugeXML.setDataCollection( datCollXML );
        return fugeXML;
    }

    /**
     * @param dataXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML the object to get some information from for populating the data
     * @return a random JAXB2 Data object
     */
    private static Data generateRandomDataXML( Data dataXML,
                                                              FuGE fugeXML ) {

        ExternalData externalDataXML = ( ExternalData ) dataXML;

        // set the data attributes
        externalDataXML = ( ExternalData ) generateRandomIdentifiableXML( externalDataXML );

        // set the externaldata attributes
        externalDataXML = generateRandomExternalDataXML( externalDataXML, fugeXML );

        return externalDataXML;

    }

    /**
     * Creates a random JAXB2 ExternalData object
     *
     * @param externalDataXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML         the object to get some information from for populating the external data
     * @return a random JAXB2 ExternalData object
     */
    private static ExternalData generateRandomExternalDataXML( ExternalData externalDataXML,
                                                                              FuGE fugeXML ) {

        // Location
        externalDataXML.setLocation( "http://some.random.url/" + String.valueOf( Math.random() ) );

        // external format documentation
        ExternalFormatDocumentation efdXML =
                new ExternalFormatDocumentation();
        UriType uriXML = new UriType();

        // set jaxb object
        uriXML = ( UriType ) generateRandomDescribableXML( uriXML );
        uriXML.setLocation( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        // load jaxb object into describableXML
        efdXML.setUri( uriXML );
        externalDataXML.setExternalFormatDocumentation( efdXML );

        // FileFormat
        if ( fugeXML.getOntologyCollection() != null ) {
            FileFormat fileformatXML = new FileFormat();
            fileformatXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }
        return externalDataXML;
    }

    /**
     * Generates a random JAXB2 MaterialCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to fill with random materials
     * @return the modified fuge object with the material collection added
     */
    private static FuGE generateRandomMaterialCollectionXML( FuGE fugeXML ) {

        // create the jaxb material collection object
        MaterialCollection matCollXML = new MaterialCollection();

        // set describable information
        matCollXML = ( MaterialCollection ) generateRandomDescribableXML( matCollXML );

        // set up the converter and the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            // set jaxb object
            if ( i > 0 ) {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( GenericMaterial ) generateRandomMaterialXML(
                                        fugeXML, ( GenericMaterial ) matCollXML.getMaterial()
                                                .get( 0 )
                                                .getValue() ) ) );
            } else {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( GenericMaterial ) generateRandomMaterialXML(
                                        fugeXML, null ) ) );
            }
        }

        fugeXML.setMaterialCollection( matCollXML );
        return fugeXML;

    }

    /**
     * Creates a random JAXB2 Material
     *
     * @param fugeXML the object to get some information from for populating the material
     * @param genXML  the JAXB2 object that is returned with attributes filled @return a random JAXB2 Material
     * @return the randomly-generated material type
     */
    private static Material generateRandomMaterialXML( FuGE fugeXML,
                                                                          GenericMaterial genXML ) {
        // create fuge object
        GenericMaterial genericMaterialXML = new GenericMaterial();

        // set the material attributes
        genericMaterialXML = ( GenericMaterial ) generateRandomSpecificXML(
                genericMaterialXML, fugeXML );

        // set the generic material attributes
        genericMaterialXML = generateRandomGenericMaterialXML( genericMaterialXML, genXML );

        return genericMaterialXML;
    }

    /**
     * Creates a random JAXB2 GenericMaterial
     *
     * @param genericMaterialXML the JAXB2 object that is returned with attributes filled
     * @param componentXML       the Material component that is a component of genericMaterialXML
     * @return a random JAXB2 GenericMaterial
     */
    private static GenericMaterial generateRandomGenericMaterialXML(
            GenericMaterial genericMaterialXML,
            GenericMaterial componentXML ) {
        // Components. These elements are references to GenericMaterial. Only generate one reference.
        if ( componentXML != null ) {
            Components componentsXML =
                    new Components();
            componentsXML.setGenericMaterialRef( componentXML.getIdentifier() );
            genericMaterialXML.getComponents().add( componentsXML );
        }
        return genericMaterialXML;
    }


    /**
     * This should be run at a time where the ontology collection and audit collection have already been run.
     *
     * @param materialXML the JAXB2 object that is returned with attributes filled
     * @param fugeXML     the object to get some information from for populating the material
     * @return the passed Material JAXB2 object with the attributes filled that are specific to the abstract Material class
     */
    private static Material generateRandomSpecificXML( Material materialXML,
                                                                          FuGE fugeXML ) {
        materialXML = ( Material ) generateRandomIdentifiableXML( materialXML );

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            materialXML.getContactRole().add( generateRandomContactRoleXML( fugeXML ) );
        }

        if ( fugeXML.getOntologyCollection() != null ) {
            MaterialType materialTypeXML = new MaterialType();
            materialTypeXML.setOntologyTermRef(
                    fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            materialXML.setMaterialType( materialTypeXML );

            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                Characteristics characteristicXML =
                        new Characteristics();
                characteristicXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getCharacteristics().add( characteristicXML );
            }

            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                QualityControlStatistics qcsXML =
                        new QualityControlStatistics();
                qcsXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getQualityControlStatistics().add( qcsXML );
            }
        }
        return materialXML;
    }

    /**
     * @param identifiableXML the JAXB2 object that is returned with attributes filled
     * @return a randomly-filled Identifiable JAXB2 object
     */
    private static Identifiable generateRandomIdentifiableXML( Identifiable identifiableXML ) {

        FuGEIdentifier identifierMaker = FuGEIdentifierFactory.createFuGEIdentifier( null, null );

        identifiableXML = ( Identifiable ) generateRandomDescribableXML( identifiableXML );
        identifiableXML.setIdentifier( identifierMaker.create( "random.class.name" ) );
        identifiableXML.setEndurantRef( identifierMaker.create( "random.class.name.Endurant" ) );
        identifiableXML.setName( String.valueOf( Math.random() ) );

        // this ensures that if smaller objects (like DatabaseReference) are being created, there is no unneccessary attempt
        //  to create sub-objects, and additionally there will be no infinite recursion
        if ( identifiableXML instanceof FuGE ) {
            FuGE fugeXML = ( FuGE ) identifiableXML;
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                DatabaseReference DatabaseReferenceXML =
                        new DatabaseReference();
                DatabaseReferenceXML = ( DatabaseReference )
                        generateRandomDescribableXML( DatabaseReferenceXML );
                DatabaseReferenceXML.setAccession( String.valueOf( Math.random() ) );
                DatabaseReferenceXML.setAccessionVersion( String.valueOf( Math.random() ) );

                // This is a reference to another object, so create that object before setting the reference
                if ( fugeXML.getReferenceableCollection() == null ) {
                    fugeXML = generateRandomReferenceableCollectionXML( fugeXML );
                }
                // get the first object and make it what is referred.
                DatabaseReferenceXML
                        .setDatabaseRef( fugeXML.getReferenceableCollection().getDatabase().get( i ).getIdentifier() );
                fugeXML.getDatabaseReference().add( DatabaseReferenceXML );
            }

            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                BibliographicReferences brRefXML =
                        new BibliographicReferences();
                // This is a reference to another object, so create that object before setting the reference
                if ( fugeXML.getReferenceableCollection() == null ) {
                    fugeXML = generateRandomReferenceableCollectionXML( fugeXML );
                }
                // get the first object and make it what is referred.
                brRefXML.setBibliographicReferenceRef(
                        fugeXML.getReferenceableCollection().getBibliographicReference().get( i ).getIdentifier() );
                fugeXML.getBibliographicReferences().add( brRefXML );
            }
            return fugeXML;
        }
        return identifiableXML;
    }

    /**
     * Generates a random JAXB2 ReferenceableCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to which the referenceable collection is added
     * @return the modified fuge object
     */
    private static FuGE generateRandomReferenceableCollectionXML( FuGE fugeXML ) {

        ReferenceableCollection refCollXML = new ReferenceableCollection();

        refCollXML = ( ReferenceableCollection ) generateRandomDescribableXML( refCollXML );

        refCollXML = ( ReferenceableCollection ) generateRandomDescribableXML( refCollXML );

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            BibliographicReference bibRefXML =
                    new BibliographicReference();
            bibRefXML = ( BibliographicReference ) generateRandomIdentifiableXML( bibRefXML );

            bibRefXML.setAuthors( String.valueOf( Math.random() ) );
            bibRefXML.setEditor( String.valueOf( Math.random() ) );
            bibRefXML.setIssue( String.valueOf( Math.random() ) );
            bibRefXML.setPages( String.valueOf( Math.random() ) );
            bibRefXML.setPublication( String.valueOf( Math.random() ) );
            bibRefXML.setPublisher( String.valueOf( Math.random() ) );
            bibRefXML.setTitle( String.valueOf( Math.random() ) );
            bibRefXML.setVolume( String.valueOf( Math.random() ) );
            bibRefXML.setYear(
                    ( int ) ( Math.random() *
                            1000 ) ); // int will not be null

            refCollXML.getBibliographicReference().add( bibRefXML );
        }
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            Database dbXML = new Database();

            dbXML = ( Database ) generateRandomIdentifiableXML( dbXML );
            dbXML.setURI( String.valueOf( Math.random() ) );
            dbXML.setVersion( String.valueOf( Math.random() ) );
            for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                if ( fugeXML.getAuditCollection() == null ) {
                    fugeXML = generateRandomAuditCollectionXML( fugeXML );
                }
                if ( fugeXML.getOntologyCollection() == null ) {
                    fugeXML = generateRandomOntologyCollectionXML( fugeXML );
                }
                dbXML.getContactRole().add( generateRandomContactRoleXML( fugeXML ) );
            }
            refCollXML.getDatabase().add( dbXML );
        }
        fugeXML.setReferenceableCollection( refCollXML );

        return fugeXML;
    }

    /**
     * this method is a litte different from the other generateRandomXML, in that it needs to return
     * a contactRole type, so all creation of audit and ontology terms must have already happened outside
     * this method.
     *
     * @param fugeXML the fuge object to get an example audit person from
     * @return a randomly-generated ContactRole JAXB2 object
     */
    private static ContactRole generateRandomContactRoleXML( FuGE fugeXML ) {

        ContactRole contactRoleXML = new ContactRole();
        contactRoleXML = ( ContactRole ) generateRandomDescribableXML( contactRoleXML );

        contactRoleXML.setContactRef( fugeXML.getAuditCollection().getContact().get( 0 ).getValue().getIdentifier() );

        Role roleXML = new Role();
        roleXML.setOntologyTermRef(
                fugeXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }

    /**
     * specifically for generating random values for use in testing. Only FuGE objects will get the full
     * generated XML, as this prevents infinite recursion.
     *
     * @param describableXML the JAXB2 object that is returned with attributes filled
     * @return a randomly-generated Describable JAXB2 object
     */
    private static Describable generateRandomDescribableXML( Describable describableXML ) {

        // at the moment there is nothing outside the class check if-statement.

        // this ensures that if smaller objects (like DatabaseEntry) are being created, there is no unneccessary attempt
        //  to create sub-objects, and additionally there will be no infinite recursion

        // create jaxb object
        AuditTrail auditsXML = new AuditTrail();

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            // create jaxb object
            Audit auditXML = new Audit();

            // set jaxb object
            if ( describableXML instanceof FuGE ) {
                auditXML = ( Audit ) generateRandomDescribableXML( auditXML );
            }

            // in addition to the standard describables, it also has date, action and contact ref, of which
            // the first two are required.
            try {
                auditXML.setDate( DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar() ) );
            } catch ( DatatypeConfigurationException e ) {
                throw new RuntimeException( "Error creating new date for random xml generation", e );
            }

            // @todo options are hardcoded: is this really the only/best way?
            auditXML.setAction( "creation" );
            if ( describableXML instanceof FuGE ) {
                FuGE fugeXML = ( FuGE ) describableXML;
                if ( fugeXML.getAuditCollection() == null ) {
                    fugeXML = generateRandomAuditCollectionXML( fugeXML );
                }
                auditXML.setContactRef(
                        fugeXML.getAuditCollection().getContact().get( i ).getValue().getIdentifier() );
                describableXML = fugeXML;
            }

            // add to collection
            auditsXML.getAudit().add( auditXML );
        }

        // load jaxb object into fugeXML
        describableXML.setAuditTrail( auditsXML );

        // create fuge object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.

        // create jaxb objects
        Descriptions descriptionsXML = new Descriptions();

        // set jaxb object
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {

            // create singular jaxb object
            Description descriptionXML = new Description();

            // set jaxb object
            if ( describableXML instanceof FuGE ) {
                descriptionXML =
                        ( Description ) generateRandomDescribableXML( descriptionXML );
            }
            descriptionXML.setText( String.valueOf( Math.random() ) );

            // add to collection of objects
            descriptionsXML.getDescription().add( descriptionXML );
        }
        // load jaxb object into fugeXML
        describableXML.setDescriptions( descriptionsXML );

        // create fuge object for any number of annotations (optional), which contains one required OntologyTerm_ref
        if ( describableXML instanceof FuGE ) {
            FuGE fugeXML = ( FuGE ) describableXML;
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
                Annotations annotationXML = new Annotations();
                if ( fugeXML.getOntologyCollection() == null ) {
                    fugeXML = generateRandomOntologyCollectionXML( fugeXML );
                }
                annotationXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                fugeXML.getAnnotations().add( annotationXML );
                describableXML = fugeXML;
            }
        }

        Uri uriElementXML = new Uri();
        UriType uriXML = new UriType();

        // add describable information to the URI only if it is a fuge object to prevent lots of recursion
        // (URIs have URIs have URIs...)
        if ( describableXML instanceof FuGE ) {
            uriXML = ( UriType ) generateRandomDescribableXML( uriXML );
        }
        uriXML.setLocation( "http://some.random.url/" + String.valueOf( Math.random() ) );

        // load jaxb object into fugeXML
        uriElementXML.setUri( uriXML );
        describableXML.setUri( uriElementXML );

        // create fuge object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        // create jaxb objects
        PropertySets propertySetsXML = new PropertySets();

        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            // create singular jaxb object
            NameValueType nameValueTypeXML = new NameValueType();

            // set jaxb object
            if ( describableXML instanceof FuGE ) {
                nameValueTypeXML =
                        ( NameValueType ) generateRandomDescribableXML( nameValueTypeXML );
            }
            nameValueTypeXML.setName( String.valueOf( Math.random() ) );
            nameValueTypeXML.setType( String.valueOf( Math.random() ) );
            nameValueTypeXML.setValue( String.valueOf( Math.random() ) );

            // load jaxb object into collection
            propertySetsXML.getNameValueType().add( nameValueTypeXML );
        }

        // load jaxb object into describable
        describableXML.setPropertySets( propertySetsXML );

        // load jaxb security object reference into fugeXML
        if ( describableXML instanceof FuGE ) {
            FuGE fugeXML = ( FuGE ) describableXML;
            if ( fugeXML.getAuditCollection() == null ) {
                fugeXML = generateRandomAuditCollectionXML( fugeXML );
            }
            fugeXML.setSecurityRef( fugeXML.getAuditCollection().getSecurity().get( 0 ).getIdentifier() );
            describableXML = fugeXML;
        }
        return describableXML;
    }

    /**
     * Generates a random JAXB2 AuditCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the FuGE object to add the audit collection to
     * @return the modified version of fugeXML
     */
    private static FuGE generateRandomAuditCollectionXML( FuGE fugeXML ) {
        // create jaxb object
        AuditCollection auditCollXML = new AuditCollection();

        // set describable information
        auditCollXML = ( AuditCollection ) generateRandomDescribableXML( auditCollXML );

        // set describable information
        auditCollXML = ( AuditCollection ) generateRandomDescribableXML( auditCollXML );

        // Contacts
        String firstOrg = null;
        ObjectFactory factory = new ObjectFactory();
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            // create jaxb object
            Organization organizationXML = new Organization();

            // set jaxb object
            organizationXML = ( Organization ) generateRandomContactXML( organizationXML );

            // set organization traits - only set a parent if i > 0.

            if ( i > 0 ) {

                Parent parentOrganizationXML =
                        new Parent();
                parentOrganizationXML.setOrganizationRef( firstOrg );
                organizationXML.setParent( parentOrganizationXML );
            } else {
                firstOrg = organizationXML.getIdentifier();
            }

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createOrganization( organizationXML ) );

            // create jaxb object
            Person personXML = new Person();

            personXML = ( Person ) generateRandomContactXML( personXML );

            // set jaxb object
            personXML = generateRandomPersonXML( personXML, organizationXML );

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createPerson( personXML ) );
        }

        // set the security and security group
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            SecurityGroup sgXML = new SecurityGroup();
            sgXML = ( SecurityGroup ) generateRandomIdentifiableXML( sgXML );

            for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                Members memXML = new Members();
                memXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                sgXML.getMembers().add( memXML );
            }
            auditCollXML.getSecurityGroup().add( sgXML );
        }

        // fixme should owner really be a collection??
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            Security securityXML = new Security();
            securityXML = ( Security ) generateRandomIdentifiableXML( securityXML );

            for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                Owners ownerXML = new Owners();
                ownerXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                securityXML.getOwners().add( ownerXML );
            }

            for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                SecurityAccess accessXML = new SecurityAccess();
                accessXML = ( SecurityAccess ) generateRandomDescribableXML( accessXML );

                AccessRight accessRightXML =
                        new AccessRight();
                if ( fugeXML.getOntologyCollection() == null ) {
                    fugeXML = generateRandomOntologyCollectionXML( fugeXML );
                }
                accessRightXML.setOntologyTermRef(
                        fugeXML.getOntologyCollection().getOntologyTerm().get( ii ).getValue().getIdentifier() );
                accessXML.setAccessRight( accessRightXML );

                accessXML.setSecurityGroupRef( auditCollXML.getSecurityGroup().get( ii ).getIdentifier() );
                securityXML.getSecurityAccess().add( accessXML );
            }
            auditCollXML.getSecurity().add( securityXML );
        }

        fugeXML.setAuditCollection( auditCollXML );

        return fugeXML;
    }

    /**
     * Generates a random JAXB2 OntologyCollection element and adds it to the rootXML JAXB2 object
     *
     * @param fugeXML the object to fill with ontology collection information
     * @return the filled fuge object
     */
    private static FuGE generateRandomOntologyCollectionXML( FuGE fugeXML ) {
        OntologyCollection ontoCollXML = new OntologyCollection();

        ontoCollXML = ( OntologyCollection ) generateRandomDescribableXML( ontoCollXML );

        // set ontology sources
        OntologySource ontoSourceXML = null;
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            ontoSourceXML = new OntologySource();
            ontoSourceXML = ( OntologySource ) generateRandomIdentifiableXML( ontoSourceXML );
            ontoSourceXML.setOntologyURI( String.valueOf( Math.random() ) );
            ontoCollXML.getOntologySource().add( ontoSourceXML );
        }

        // set ontology terms
        ObjectFactory factory = new ObjectFactory();
        for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {
            ontoCollXML.getOntologyTerm()
                    .add( factory.createOntologyIndividual( generateRandomOntologyIndividualXML( ontoSourceXML ) ) );
        }

        fugeXML.setOntologyCollection( ontoCollXML );

        return fugeXML;
    }

    /**
     * Calls the other generateRandomOntologyIndividualXML method with a default boolean value of false, to
     * say that it isn't being generated inside the for-loop that's counting the number of repeated XML elements to make
     *
     * @param ontoSourceXML the OntologySource JAXB2 object to associate with this OntologyIndividual
     * @return a randomly-generated OntologyIndividual JAXB2 object
     */
    private static OntologyIndividual generateRandomOntologyIndividualXML(
            OntologySource ontoSourceXML ) {
        return generateRandomOntologyIndividualXML( ontoSourceXML, false );
    }

    /**
     * @param ontoSourceXML the OntologySource JAXB2 object to associate with this OntologyIndividual
     * @param inner         whether or not this is being called from within this method
     * @return a randomly-generated OntologyIndividual JAXB2 object
     */
    private static OntologyIndividual generateRandomOntologyIndividualXML(
            OntologySource ontoSourceXML,
            boolean inner ) {

        OntologyIndividual ontologyIndividualXML = new OntologyIndividual();
        ontologyIndividualXML = ( OntologyIndividual ) generateRandomOntologyTermXML(
                ontologyIndividualXML, ontoSourceXML );

        if ( !inner ) {
            ObjectFactory factory = new ObjectFactory();
            for ( int i = 0; i < ELEMENT_DEPTH; i++ ) {

                ObjectProperty objectPropertyXML = new ObjectProperty();
                objectPropertyXML = ( ObjectProperty ) generateRandomIdentifiableXML(
                        objectPropertyXML );
                objectPropertyXML = ( ObjectProperty ) generateRandomOntologyTermXML(
                        objectPropertyXML, ontoSourceXML );
                for ( int ii = 0; ii < ELEMENT_DEPTH; ii++ ) {
                    objectPropertyXML.getOntologyIndividual()
                            .add( generateRandomOntologyIndividualXML( ontoSourceXML, true ) );
                }
                ontologyIndividualXML.getOntologyProperty().add( factory.createObjectProperty( objectPropertyXML ) );

                DataProperty dataPropertyXML = new DataProperty();
                dataPropertyXML =
                        ( DataProperty ) generateRandomIdentifiableXML( dataPropertyXML );
                dataPropertyXML = ( DataProperty ) generateRandomOntologyTermXML(
                        dataPropertyXML, ontoSourceXML );
                dataPropertyXML.setDataType( String.valueOf( Math.random() ) );
                ontologyIndividualXML.getOntologyProperty().add( factory.createDataProperty( dataPropertyXML ) );
            }
        }
        return ontologyIndividualXML;
    }

    /**
     * @param ontologyTermXML the JAXB2 object that is returned with attributes filled
     * @param ontoSourceXML   the OntologySource JAXB2 object to associate with this OntologyTerm
     * @return a randomly-generated OntologyTerm JAXB2 object
     */
    private static OntologyTerm generateRandomOntologyTermXML(
            OntologyTerm ontologyTermXML,
            OntologySource ontoSourceXML ) {

        ontologyTermXML = ( OntologyTerm ) generateRandomIdentifiableXML( ontologyTermXML );

        ontologyTermXML.setTerm( String.valueOf( Math.random() ) );
        ontologyTermXML.setTermAccession( String.valueOf( Math.random() ) );
        ontologyTermXML.setOntologySourceRef( ontoSourceXML.getIdentifier() );

        return ontologyTermXML;
    }

    /**
     * @param contactXML the JAXB2 object that is returned with attributes filled
     * @return a randomly-generated Contact JAXB2 object
     */
    private static Contact generateRandomContactXML( Contact contactXML ) {
        // set all identifiable traits in the jaxb object
        contactXML = ( Contact ) generateRandomIdentifiableXML( contactXML );

        // set all non-identifiable contact traits
        contactXML.setAddress( String.valueOf( Math.random() ) );
        contactXML.setEmail( String.valueOf( Math.random() ) );
        contactXML.setFax( String.valueOf( Math.random() ) );
        contactXML.setPhone( String.valueOf( Math.random() ) );
        contactXML.setTollFreePhone( String.valueOf( Math.random() ) );

        return contactXML;
    }

    /**
     * @param personXML       the JAXB2 object that is returned with attributes filled
     * @param organizationXML the Organization JAXB2 object to associate with this Persn
     * @return a randomly-generated Person JAXB2 object
     */
    private static Person generateRandomPersonXML(
            Person personXML,
            Organization organizationXML ) {

        // set person traits
        personXML.setFirstName( String.valueOf( Math.random() ) );
        personXML.setLastName( String.valueOf( Math.random() ) );
        personXML.setMidInitials( String.valueOf( Math.random() ) );

        Affiliations affiliationXML = new Affiliations();

        affiliationXML.setOrganizationRef( organizationXML.getIdentifier() );
        personXML.getAffiliations().add( affiliationXML );

        return personXML;
    }

}
