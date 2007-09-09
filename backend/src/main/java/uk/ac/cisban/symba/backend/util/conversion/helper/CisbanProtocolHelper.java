package uk.ac.cisban.symba.backend.util.conversion.helper;

import com.ibm.lsid.LSIDException;
import fugeOM.Common.Identifiable;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.FugeOMCollectionFuGEType;
import fugeOM.util.generatedJAXB2.FugeOMCollectionProtocolCollectionType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolGenericProtocolType;
import fugeOM.util.generatedJAXB2.FugeOMCommonProtocolProtocolType;
import uk.ac.cisban.symba.backend.util.CisbanHelper;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.*;

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

public class CisbanProtocolHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final CisbanIdentifiableHelper ci;
    private final CisbanParameterizableHelper cparam;
    private final CisbanGenericProtocolHelper cgp;
    private final CisbanHelper helper;
    private final RealizableEntityService reService;

    public CisbanProtocolHelper( RealizableEntityService reService, CisbanIdentifiableHelper ci ) {
        this.reService = reService;
        this.ci = ci;
        this.cparam = new CisbanParameterizableHelper( reService, ci.getCisbanDescribableHelper() );
        this.cgp = new CisbanGenericProtocolHelper( reService, ci );
        this.helper = CisbanHelper.create( reService );
    }

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Protocol unmarshalProtocol( FugeOMCommonProtocolProtocolType protocolXML )
            throws URISyntaxException, RealizableEntityServiceException, LSIDException {

        // determine what sort of protocol it is
        if ( protocolXML instanceof FugeOMCommonProtocolGenericProtocolType ) {

            // Retrieve latest version from the database.
            GenericProtocol genericProtocol = ( GenericProtocol ) helper.getOrCreateLatest(
                    protocolXML.getEndurant(),
                    "fugeOM.Common.Protocol.GenProtocolEndurant",
                    protocolXML.getName(),
                    "fugeOM.Common.Protocol.GenericProtocol",
                    System.err );

            // get protocol attributes
            genericProtocol = ( GenericProtocol ) ci.unmarshalIdentifiable( protocolXML, genericProtocol );
            genericProtocol = ( GenericProtocol ) cparam.unmarshalParameterizable( protocolXML, genericProtocol );

            // input types
            if ( protocolXML.getInputTypes() != null ) {
                Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
                for ( FugeOMCommonProtocolProtocolType.InputTypes inputTypesXML : protocolXML.getInputTypes() ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    ontologyTerms
                            .add( ( OntologyTerm ) reService.findIdentifiable( inputTypesXML.getOntologyTermRef() ) );
                }
                genericProtocol.setInputTypes( ontologyTerms );
            }

            // output types
            if ( protocolXML.getOutputTypes() != null ) {
                Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
                for ( FugeOMCommonProtocolProtocolType.OutputTypes outputTypesXML : protocolXML.getOutputTypes() ) {
                    // Set the object to exactly the object is that is associated
                    // with this version group.
                    ontologyTerms
                            .add( ( OntologyTerm ) reService.findIdentifiable( outputTypesXML.getOntologyTermRef() ) );
                }
                genericProtocol.setInputTypes( ontologyTerms );
            }

            // get generic protocol attributes
            genericProtocol = cgp.unmarshalGenericProtocol(
                    ( FugeOMCommonProtocolGenericProtocolType ) protocolXML, genericProtocol );

            if ( genericProtocol.getId() != null ) {
                helper.assignAndLoadIdentifiable(
                        genericProtocol, "fugeOM.Common.Protocol.GenericProtocol", System.err );
            } else {
                helper.loadIdentifiable( genericProtocol, "fugeOM.Common.Protocol.GenericProtocol", System.err );
            }

            return genericProtocol;
        }
        return null;  // shouldn't get here as there is currently only one type of Protocol allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolProtocolType marshalProtocol( Protocol protocol )
            throws URISyntaxException, RealizableEntityServiceException {

        // get any lazily loaded objects
        protocol = ( Protocol ) reService.greedyGet( protocol );

        // determine what sort of protocol it is
        if ( protocol instanceof GenericProtocol ) {

            // create fuge object
            FugeOMCommonProtocolGenericProtocolType genericProtocolXML = new FugeOMCommonProtocolGenericProtocolType();

            // sort out lazy loading
            genericProtocolXML = cgp.marshalGenericProtocol( genericProtocolXML, ( GenericProtocol ) protocol );

            // get protocol attributes
            genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) ci
                    .marshalIdentifiable( genericProtocolXML, protocol );

            genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) cparam
                    .marshalParameterizable( genericProtocolXML, protocol );

            // input types
            for ( Object obj : protocol.getInputTypes() ) {
                OntologyTerm ontologyTerm = ( OntologyTerm ) obj;

                FugeOMCommonProtocolProtocolType.InputTypes inputTypesXML = new FugeOMCommonProtocolProtocolType.InputTypes();
                inputTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
                genericProtocolXML.getInputTypes().add( inputTypesXML );
            }

            // output types
            for ( Object obj : protocol.getOutputTypes() ) {
                OntologyTerm ontologyTerm = ( OntologyTerm ) obj;

                FugeOMCommonProtocolProtocolType.OutputTypes outputTypesXML = new FugeOMCommonProtocolProtocolType.OutputTypes();
                outputTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
                genericProtocolXML.getOutputTypes().add( outputTypesXML );
            }
            return genericProtocolXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Protocol allowed.
    }

    // at this stage, frXML may not have the new equipment and software - the protocol collection may be the only one to have it
    public FugeOMCommonProtocolProtocolType generateRandomXML( FugeOMCommonProtocolProtocolType protocolXML,
                                                               FugeOMCollectionProtocolCollectionType protocolCollectionXML,
                                                               FugeOMCollectionFuGEType frXML ) {

        FugeOMCommonProtocolGenericProtocolType genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) protocolXML;

        // get protocol attributes
        genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) ci.generateRandomXML( genericProtocolXML );

        genericProtocolXML = ( FugeOMCommonProtocolGenericProtocolType ) cparam
                .generateRandomXML( genericProtocolXML, frXML );

        if ( frXML.getOntologyCollection() != null ) {
            // input types
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolProtocolType.InputTypes inputTypesXML = new FugeOMCommonProtocolProtocolType.InputTypes();
                inputTypesXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getInputTypes().add( inputTypesXML );
            }

            // output types
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonProtocolProtocolType.OutputTypes outputTypesXML = new FugeOMCommonProtocolProtocolType.OutputTypes();
                outputTypesXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getOutputTypes().add( outputTypesXML );
            }
        }

        genericProtocolXML = cgp.generateRandomXML( genericProtocolXML, protocolCollectionXML, frXML );

        return genericProtocolXML;
    }

    public Protocol getLatestVersion( Protocol protocol ) throws RealizableEntityServiceException {

        // get any lazily loaded objects
        protocol = ( Protocol ) reService.greedyGet( protocol );

        // check that the protocol isn't already the most recent version. if it is,
        // do nothing and continue to the GenericProtocol get latest version, passing
        // isLatestProtocol = true.
        String latestId = reService.findLatestIdentifierByEndurant( protocol.getEndurant().getIdentifier() );

        if ( latestId.equals( protocol.getIdentifier() ) ) {
            // go directly to GenericProtocol's getLatestVersion, with no update of Protocol necessary
            protocol = cgp.getLatestVersion( ( GenericProtocol ) protocol, true );
            return protocol;
        }

        // get the latest version of the identifiables in this object
        protocol = ( Protocol ) reService.findIdentifiable( latestId );

        protocol = ( Protocol ) ci.getLatestVersion( protocol );

        // determine what sort of protocol it is
        if ( protocol instanceof GenericProtocol ) {
            // get protocol attributes
            protocol = ( Protocol ) cparam.getLatestVersion( protocol, ci );

            // prepare updated set
            Set<OntologyTerm> set = new HashSet<OntologyTerm>();

            // load all the latest versions into the new set.
            for ( Object obj : protocol.getInputTypes() ) {
                Identifiable identifiable = ( Identifiable ) obj;
                identifiable = ( Identifiable ) reService
                        .findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
                set.add( ( OntologyTerm ) ci.getLatestVersion( identifiable ) );
            }
            protocol.setInputTypes( set );

            // prepare updated set
            Set<OntologyTerm> set1 = new HashSet<OntologyTerm>();

            // load all the latest versions into the new set.
            for ( Object obj : protocol.getOutputTypes() ) {
                Identifiable identifiable = ( Identifiable ) obj;
                identifiable = ( Identifiable ) reService
                        .findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
                set1.add( ( OntologyTerm ) ci.getLatestVersion( identifiable ) );
            }
            protocol.setOutputTypes( set1 );

            // get generic protocol attributes
            protocol = cgp.getLatestVersion( ( GenericProtocol ) protocol, false );
        }
        return protocol;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( Protocol protocol, PrintStream printStream ) {
        prettyPrint( null, protocol, printStream );
    }

    public void prettyPrint( String prepend, Protocol protocol, PrintStream printStream ) {
        String aaa = "---Protocol: ";
        if ( prepend != null ) {
            aaa = prepend + aaa;
        }

        ci.prettyPrint( aaa, protocol, printStream );
        if ( protocol instanceof GenericProtocol ) {
            cgp.prettyPrint( prepend, ( GenericProtocol ) protocol, printStream );
        }
    }

    public void prettyHtml( GenericAction parentGA,
                            Protocol protocol,
                            Collection restrictedGPAs,
                            CisbanDescribableHelper cd,
                            PrintWriter printStream ) throws RealizableEntityServiceException {

        GenericProtocol gp;
        if ( protocol instanceof GenericProtocol ) {
            gp = ( GenericProtocol ) protocol;
        } else {
            return;
        }

        // Now print out all direct children of the complete protocol, and keep drilling down
        // until you have no more actions. At that stage, search the protocol applications for
        // a match: this is where your data will be.

        // getting the latest version of the protocol gets the latest version of all its children
        // (eg GenericProtocol, GenericAction etc).
        // This will only retrieve the latest version of the protocol or the objects referencing them
        // if that object's identifier isn't the latest identifier. This speeds up the process.
        gp = ( GenericProtocol ) getLatestVersion( gp );

        // this is so the current protocol has the right height. We have to look through
        // the entire tree to get the right number
        int size = countActions( gp );
        // todo fix 3-level display. currently hard-coded for top-level microarray only
        if ( size == 21 ) {
            size = 19;
        }

        if ( size > 0 ) {
            printStream.println( "<td rowspan=\"" + size + "\">" );
            if ( parentGA != null ) {
                ci.prettyHtml( null, parentGA, printStream );
            } else {
                ci.prettyHtml( null, gp, printStream );
            }
            printStream.println( "</td>" );

            Set aSet = ( Set ) gp.getGenericActions();
            for ( int count = 1; count <= aSet.size(); count++ ) {
                for ( Object obj2 : aSet ) {
                    GenericAction ga = ( GenericAction ) obj2;
                    if ( count == ga.getActionOrdinal() ) {
                        // Find all GPAs associated with this GA from the list of restricted GPAs
                        prettyHtml( ga, ga.getGenProtocolRef(), getMatchedGPAs( ga, restrictedGPAs ), cd, printStream );
                    }
                }
            }
        } else {
            printStream.println( "<td>" );
            ci.prettyHtml( null, parentGA, printStream );
            printStream.println( "</td>" );

            CisbanProtocolApplicationHelper capp = new CisbanProtocolApplicationHelper( reService, cd, ci );
            // If there are no actions in the protocol, then its matching GPA will be the one that holds the data
            if ( restrictedGPAs != null && !restrictedGPAs.isEmpty() ) {
                // for all matching GPAs...
                for ( Object gpaObj : restrictedGPAs ) {
                    GenericProtocolApplication gpa = ( GenericProtocolApplication ) gpaObj;
                    // ...if there are no action applications....
                    if ( gpa.getActionApplications() == null || gpa.getActionApplications().isEmpty() ) {
                        // ...print the data
                        capp.prettyHtml( gpa, printStream );
                    }
                }
            }
            printStream.println( "</tr>" );
            printStream.println( "<tr>" );
        }
        printStream.flush();
    }

    private List<GenericProtocolApplication> getMatchedGPAs( GenericAction ga, Collection apps ) {
        List<GenericProtocolApplication> matchedGPAs = new ArrayList<GenericProtocolApplication>();
        for ( Object gpaObj : apps ) {
            if ( gpaObj instanceof GenericProtocolApplication ) {
                GenericProtocolApplication currentGPA = ( GenericProtocolApplication ) gpaObj;
                for ( Object aaObj : currentGPA.getActionApplications() ) {
                    ActionApplication currentAA = ( ActionApplication ) aaObj;
                    if ( currentAA.getAction().getEndurant().getIdentifier().equals(
                            ga.getEndurant().getIdentifier() ) ) {
                        matchedGPAs.add( ( GenericProtocolApplication ) currentAA.getProtAppRef() );
                    }

                }
            }
        }
        return matchedGPAs;
    }

    private int countActions( GenericProtocol gp ) {
        // this is so the current protocol has the right height. We have to look through
        // the entire tree to get the right number

        int addition = 0;
        if ( gp.getGenericActions() != null ) {
            for ( Object obj : gp.getGenericActions() ) {
                // do the same thing for the protocol reference for each generic action
                addition++; // for the current action.
                GenericAction ga = ( GenericAction ) obj;
                addition += countActions( ( GenericProtocol ) ga.getGenProtocolRef() );
            }
        }
        return addition;
    }

}
