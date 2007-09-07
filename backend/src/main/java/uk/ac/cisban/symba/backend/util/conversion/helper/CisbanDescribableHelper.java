package uk.ac.cisban.symba.backend.util.conversion.helper;

import fugeOM.Common.Audit.Audit;
import fugeOM.Common.Audit.AuditAction;
import fugeOM.Common.Audit.Contact;
import fugeOM.Common.Audit.Security;
import fugeOM.Common.Describable;
import fugeOM.Common.Description.Description;
import fugeOM.Common.Description.Uri;
import fugeOM.Common.Identifiable;
import fugeOM.Common.NameValueType;
import fugeOM.Common.Ontology.OntologyTerm;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;

import java.net.URISyntaxException;
import java.util.*;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate: 2007-08-13 12:19:48 +0100 (Mon, 13 Aug 2007) $
 * $LastChangedRevision: 546 $
 * $Author: ally $
 * $HeadURL: https://metagenome.ncl.ac.uk/subversion/cisban/DataPortal/trunk/cisbandpi/dpi/src/main/java/uk/ac/cisban/dpi/util/conversion/helper/CisbanDescribableHelper.java $
 *
 */

public class CisbanDescribableHelper {
    private final int NUMBER_ELEMENTS = 2;
    private final RealizableEntityService reService;

    public CisbanDescribableHelper( RealizableEntityService reService ) {
        this.reService = reService;
    }

    // Please note that this Describable object itself is NEITHER created NOR loaded into the database here,
    // as that would require a knowledge of what subclass of Describable this was.
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public Describable unmarshalDescribable( FugeOMCommonDescribableType describableXML,
                                             Describable describable ) throws URISyntaxException, RealizableEntityServiceException {

        // In jaxb, auditTrail is an object in its own right, while in fuge, it is simply
        // a collection of Audits. It is an optional element

        // get jaxb object of the auditTrail
        FugeOMCommonDescribableType.AuditTrail auditTrailXML = describableXML.getAuditTrail();
        if ( auditTrailXML != null ) {
            // create fuge object of audit trail if it is present in the xml
            Set<Audit> auditTrail = new HashSet<Audit>();

            // set fuge object by getting all audits

            // create jaxb object
            List<FugeOMCommonAuditAuditType> auditsXML = auditTrailXML.getAudit();
            for ( FugeOMCommonAuditAuditType auditXML : auditsXML ) {
                // create fuge object
                Audit audit = ( Audit ) reService.createDescribableOb( "fugeOM.Common.Audit.Audit" );

                // set fuge object
                audit = ( Audit ) unmarshalDescribable( auditXML, audit );
                // in addition to the standard describables, it also has date, action and contact ref, of which
                // the first two are required.
                audit.setDate( auditXML.getDate() );
                // @todo options are hardcoded: is this really the only/best way?
                if ( auditXML.getAction().equals( "creation" ) )
                    audit.setAction( AuditAction.CREATION );
                if ( auditXML.getAction().equals( "deletion" ) )
                    audit.setAction( AuditAction.DELETION );
                if ( auditXML.getAction().equals( "modification" ) )
                    audit.setAction( AuditAction.MODIFICATION );

                // Set the object to exactly the object is that is associated
                // with this version group.
                if ( auditXML.getContactRef() != null )
                    audit.setPerformer( ( Contact ) reService.findIdentifiable( auditXML.getContactRef() ) );

                // load fuge object in database
                reService.createObInDB( "fugeOM.Common.Audit.Audit", audit );

                // add to collection
                auditTrail.add( audit );
            }
            // load fuge object into describable
            describable.setAuditTrail( auditTrail );
        }

        // create jaxb object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.
        FugeOMCommonDescribableType.Descriptions descriptionsElementXML = describableXML.getDescriptions();

        if ( descriptionsElementXML != null ) {
            // create jaxb object
            List<FugeOMCommonDescriptionDescriptionType> descriptionsXML = descriptionsElementXML.getDescription();

            // create fuge object
            Set<Description> descriptions = new HashSet<Description>();

            // set fuge object
            for ( FugeOMCommonDescriptionDescriptionType descriptionXML : descriptionsXML ) {
                Description description = ( Description ) reService.createDescribableOb(
                        "fugeOM.Common.Description.Description" );
                description = ( Description ) unmarshalDescribable( descriptionXML, description );
                description.setText( descriptionXML.getText() );
                reService.createObInDB( "fugeOM.Common.Description.Description", description );
                descriptions.add( description );
            }
            // load fuge object into describable
            describable.setDescriptions( descriptions );
        }

        // create jaxb object for any number of annotations (optional), which contains one required OntologyTerm_ref
        List<FugeOMCommonDescribableType.Annotations> annotationsXML = describableXML.getAnnotations();

        if ( !annotationsXML.isEmpty() ) {
            // create fuge object
            Set<OntologyTerm> annotations = new HashSet<OntologyTerm>();

            // set fuge object
            for ( FugeOMCommonDescribableType.Annotations annotationXML : annotationsXML ) {
                // Set the object to exactly the object is that is associated
                // with this version group.
                annotations.add( ( OntologyTerm ) reService.findIdentifiable( annotationXML.getOntologyTermRef() ) );
            }
            // load fuge object into describable
            describable.setAnnotations( annotations );
        }

        // create jaxb object for 0 or 1 exturi (optional), which contains exactly 1 ExternalURI
        FugeOMCommonDescribableType.Exturi exturiElementXML = describableXML.getExturi();

        // todo Uri class really should contain a URI and not a String. Postgres currently has problems when it is a Uri.
        if ( exturiElementXML != null ) {
            // create jaxb object
            FugeOMCommonDescriptionExternalURIType exturiXML = exturiElementXML.getExternalURI();

            // create fuge object
            Uri exturi = ( Uri ) reService.createDescribableOb( "fugeOM.Common.Description.Uri" );

            // set fuge object
            exturi = ( Uri ) unmarshalDescribable( exturiXML, exturi );
//            exturi.setUri( new java.net.URI( exturiXML.getUri() ) );
            exturi.setUri( exturiXML.getUri() );

            // load fuge object into database
            reService.createObInDB( "fugeOM.Common.Description.Uri", exturi );

            // load fuge object into describable
            describable.setExturi( exturi );
        }

        // create jaxb object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        FugeOMCommonDescribableType.PropertySets propertySetsXML = describableXML.getPropertySets();

        if ( propertySetsXML != null ) {
            // create jaxb object
            List<FugeOMCommonNameValueTypeType> nameValueTypesXML = propertySetsXML.getNameValueType();

            // create fuge collection object
            Set<NameValueType> propertySets = new HashSet<NameValueType>();

            // set fuge collection object
            for ( FugeOMCommonNameValueTypeType nameValueTypeXML : nameValueTypesXML ) {
                // create fuge object
                NameValueType nameValueType = ( NameValueType ) reService.createDescribableOb(
                        "fugeOM.Common.NameValueType" );

                // set fuge object
                nameValueType = ( NameValueType ) unmarshalDescribable( nameValueTypeXML, nameValueType );
                nameValueType.setName( nameValueTypeXML.getName() );
                nameValueType.setType( nameValueTypeXML.getType() );
                nameValueType.setValue( nameValueTypeXML.getValue() );

                // load fuge object into database
                reService.createObInDB( "fugeOM.Common.NameValueType", nameValueType );

                // load fuge object into collection
                propertySets.add( nameValueType );
            }

            // load fuge object into describable
            describable.setPropertySets( propertySets );
        }

        // load fuge object reference into describable
        if ( describableXML.getSecurityRef() != null ) {
            // Set the object to exactly the object is that is associated
            // with this version group.
            describable.setSecurity( ( Security ) reService.findIdentifiable( describableXML.getSecurityRef() ) );
        }
        return describable;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonDescribableType marshalDescribable( FugeOMCommonDescribableType describableXML,
                                                           Describable describable ) throws URISyntaxException {
        // In jaxb, auditTrail is an object in its own right, while in fuge, it is simply
        // a collection of Audits. It is an optional element

        if ( describable.getAuditTrail() != null && !describable.getAuditTrail().isEmpty() ) {

            // create jaxb object
            FugeOMCommonDescribableType.AuditTrail auditsXML = new FugeOMCommonDescribableType.AuditTrail();

            for ( Object auditObj : describable.getAuditTrail() ) {
                Audit audit = ( Audit ) auditObj;

                // create jaxb object
                FugeOMCommonAuditAuditType auditXML = new FugeOMCommonAuditAuditType();

                // set jaxb object
                auditXML = ( FugeOMCommonAuditAuditType ) marshalDescribable( auditXML, audit );

                // in addition to the standard describables, it also has date, action and contact ref, of which
                // the first two are required.
                auditXML.setDate( audit.getDate() );

                // @todo options are hardcoded: is this really the only/best way?
                if ( audit.getAction() == AuditAction.CREATION )
                    auditXML.setAction( "creation" );
                if ( audit.getAction() == AuditAction.DELETION )
                    auditXML.setAction( "deletion" );
                if ( audit.getAction() == AuditAction.MODIFICATION )
                    auditXML.setAction( "modification" );
                if ( audit.getPerformer() != null )
                    auditXML.setContactRef( audit.getPerformer().getIdentifier() );

                // add to collection
                auditsXML.getAudit().add( auditXML );
            }
            // load jaxb object into describableXML
            describableXML.setAuditTrail( auditsXML );
        }

        // create fuge object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.

        if ( !describable.getDescriptions().isEmpty() ) {
            // create jaxb objects
            FugeOMCommonDescribableType.Descriptions descriptionsXML = new FugeOMCommonDescribableType.Descriptions();

            Collection descriptions = describable.getDescriptions();
            // set jaxb object
            for ( Object descriptionObj : descriptions ) {
                Description description = ( Description ) descriptionObj;

                // create singular jaxb object
                FugeOMCommonDescriptionDescriptionType descriptionXML = new FugeOMCommonDescriptionDescriptionType();

                // set jaxb object
                descriptionXML = ( FugeOMCommonDescriptionDescriptionType ) marshalDescribable(
                        descriptionXML, description );
                descriptionXML.setText( description.getText() );

                // add to collection of objects
                descriptionsXML.getDescription().add( descriptionXML );
            }
            // load jaxb object into describableXML
            describableXML.setDescriptions( descriptionsXML );
        }

        // create fuge object for any number of annotations (optional), which contains one required OntologyTerm_ref
        Collection annotations;
        if ( !( annotations = describable.getAnnotations() ).isEmpty() ) {

            // set jaxb object
            for ( Object annotationObj : annotations ) {
                OntologyTerm annotation = ( OntologyTerm ) annotationObj;

                FugeOMCommonDescribableType.Annotations annotationXML = new FugeOMCommonDescribableType.Annotations();
                annotationXML.setOntologyTermRef( annotation.getIdentifier() );
                describableXML.getAnnotations().add( annotationXML );
            }
        }

        // create fuge object for 0 or 1 exturi (optional), which contains exactly 1 ExternalURI
        Uri exturi = describable.getExturi();

        if ( exturi != null ) {
            // create jaxb object
            FugeOMCommonDescribableType.Exturi exturiElementXML = new FugeOMCommonDescribableType.Exturi();
            FugeOMCommonDescriptionExternalURIType exturiXML = new FugeOMCommonDescriptionExternalURIType();

            // set jaxb object
            exturiXML = ( FugeOMCommonDescriptionExternalURIType ) marshalDescribable( exturiXML, exturi );
            exturiXML.setUri( exturi.getUri() );

            // load jaxb object into describableXML
            exturiElementXML.setExternalURI( exturiXML );
            describableXML.setExturi( exturiElementXML );
        }

        // create fuge object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        // create jaxb objects
        FugeOMCommonDescribableType.PropertySets propertySetsXML = new FugeOMCommonDescribableType.PropertySets();

        Collection nameValueTypes;
        if ( !( nameValueTypes = describable.getPropertySets() ).isEmpty() ) {
            // set jaxb collection object
            for ( Object nameValueTypeObj : nameValueTypes ) {
                NameValueType nameValueType = ( NameValueType ) nameValueTypeObj;

                // create singular jaxb object
                FugeOMCommonNameValueTypeType nameValueTypeXML = new FugeOMCommonNameValueTypeType();

                // set jaxb object
                nameValueTypeXML = ( FugeOMCommonNameValueTypeType ) marshalDescribable(
                        nameValueTypeXML, nameValueType );
                nameValueTypeXML.setName( nameValueType.getName() );
                nameValueTypeXML.setType( nameValueType.getType() );
                nameValueTypeXML.setValue( nameValueType.getValue() );

                // load jaxb object into collection
                propertySetsXML.getNameValueType().add( nameValueTypeXML );
            }

            // load jaxb object into describable
            describableXML.setPropertySets( propertySetsXML );
        }

        // load jaxb security object reference into describableXML
        if ( describable.getSecurity() != null ) {
            describableXML.setSecurityRef( describable.getSecurity().getIdentifier() );
        }
        return describableXML;
    }

    // specifically for generating random values for use in testing. Only FuGE objects will get the full
    // generated XML, as this prevents infinite recursion.
    public FugeOMCommonDescribableType generateRandomXML( FugeOMCommonDescribableType type,
                                                          CisbanIdentifiableHelper ci ) {

        // at the moment there is nothing outside the class check if-statement.

        // this ensures that if smaller objects (like DatabaseEntry) are being created, there is no unneccessary attempt
        //  to create sub-objects, and additionally there will be no infinite recursion

        // create jaxb object
        FugeOMCommonDescribableType.AuditTrail auditsXML = new FugeOMCommonDescribableType.AuditTrail();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create jaxb object
            FugeOMCommonAuditAuditType auditXML = new FugeOMCommonAuditAuditType();

            // set jaxb object
            if ( type instanceof FugeOMCollectionFuGEType )
                auditXML = ( FugeOMCommonAuditAuditType ) generateRandomXML( auditXML, ci );

            // in addition to the standard describables, it also has date, action and contact ref, of which
            // the first two are required.
            auditXML.setDate( new Date() );

            // @todo options are hardcoded: is this really the only/best way?
            auditXML.setAction( "creation" );
            if ( type instanceof FugeOMCollectionFuGEType ) {
                FugeOMCollectionFuGEType fuGEType = ( FugeOMCollectionFuGEType ) type;
                if ( fuGEType.getAuditCollection() == null ) {
                    CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper( reService, ci );
                    fuGEType = cac.generateRandomXML( fuGEType );
                }
                auditXML.setContactRef(
                        fuGEType.getAuditCollection().getContact().get( i ).getValue().getIdentifier() );
                type = fuGEType;
            }

            // add to collection
            auditsXML.getAudit().add( auditXML );
        }
        // load jaxb object into fuGEType
        type.setAuditTrail( auditsXML );

        // create fuge object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.

        // create jaxb objects
        FugeOMCommonDescribableType.Descriptions descriptionsXML = new FugeOMCommonDescribableType.Descriptions();

        // set jaxb object
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

            // create singular jaxb object
            FugeOMCommonDescriptionDescriptionType descriptionXML = new FugeOMCommonDescriptionDescriptionType();

            // set jaxb object
            if ( type instanceof FugeOMCollectionFuGEType )
                descriptionXML = ( FugeOMCommonDescriptionDescriptionType ) generateRandomXML( descriptionXML, ci );
            descriptionXML.setText( String.valueOf( Math.random() ) );

            // add to collection of objects
            descriptionsXML.getDescription().add( descriptionXML );
        }
        // load jaxb object into fuGEType
        type.setDescriptions( descriptionsXML );

        // create fuge object for any number of annotations (optional), which contains one required OntologyTerm_ref
        if ( type instanceof FugeOMCollectionFuGEType ) {
            FugeOMCollectionFuGEType fuGEType = ( FugeOMCollectionFuGEType ) type;
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FugeOMCommonDescribableType.Annotations annotationXML = new FugeOMCommonDescribableType.Annotations();
                if ( fuGEType.getOntologyCollection() == null ) {
                    CisbanOntologyCollectionHelper coc = new CisbanOntologyCollectionHelper( reService, ci );
                    fuGEType = coc.generateRandomXML( fuGEType );
                }
                annotationXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                fuGEType.getAnnotations().add( annotationXML );
                type = fuGEType;
            }
        }

        FugeOMCommonDescribableType.Exturi exturiElementXML = new FugeOMCommonDescribableType.Exturi();
        FugeOMCommonDescriptionExternalURIType exturiXML = new FugeOMCommonDescriptionExternalURIType();

        // set jaxb object
        if ( type instanceof FugeOMCollectionFuGEType )
            exturiXML = ( FugeOMCommonDescriptionExternalURIType ) generateRandomXML( exturiXML, ci );
        exturiXML.setUri( "http://some.random.url/" + String.valueOf( Math.random() ) );

        // load jaxb object into fuGEType
        exturiElementXML.setExternalURI( exturiXML );
        type.setExturi( exturiElementXML );

        // create fuge object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        // create jaxb objects
        FugeOMCommonDescribableType.PropertySets propertySetsXML = new FugeOMCommonDescribableType.PropertySets();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create singular jaxb object
            FugeOMCommonNameValueTypeType nameValueTypeXML = new FugeOMCommonNameValueTypeType();

            // set jaxb object
            if ( type instanceof FugeOMCollectionFuGEType )
                nameValueTypeXML = ( FugeOMCommonNameValueTypeType ) generateRandomXML( nameValueTypeXML, ci );
            nameValueTypeXML.setName( String.valueOf( Math.random() ) );
            nameValueTypeXML.setType( String.valueOf( Math.random() ) );
            nameValueTypeXML.setValue( String.valueOf( Math.random() ) );

            // load jaxb object into collection
            propertySetsXML.getNameValueType().add( nameValueTypeXML );
        }

        // load jaxb object into describable
        type.setPropertySets( propertySetsXML );

        // load jaxb security object reference into fuGEType
        if ( type instanceof FugeOMCollectionFuGEType ) {
            FugeOMCollectionFuGEType fuGEType = ( FugeOMCollectionFuGEType ) type;
            if ( fuGEType.getAuditCollection() == null ) {
                CisbanAuditCollectionHelper cac = new CisbanAuditCollectionHelper( reService, ci );
                fuGEType = cac.generateRandomXML( fuGEType );
            }
            fuGEType.setSecurityRef( fuGEType.getAuditCollection().getSecurity().get( 0 ).getIdentifier() );
            type = fuGEType;
        }
        return type;
    }

    // Get the latest version of any identifiable object present within this object
    public Describable getLatestVersion( Describable describable,
                                         CisbanIdentifiableHelper ci ) throws RealizableEntityServiceException {

        // prepare updated set
        Set<Audit> set = new HashSet<Audit>();
        for ( Object obj : describable.getAuditTrail() ) {
            Audit audit = ( Audit ) obj;
            audit = ( Audit ) getLatestVersion( audit, ci );
            Identifiable identifiable = audit.getPerformer();
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            audit.setPerformer( ( Contact ) ci.getLatestVersion( identifiable ) );
            set.add( audit );
        }
        describable.setAnnotations( set );

        // prepare updated set
        Set<Description> set2 = new HashSet<Description>();

        // load all the latest versions into the new set.
        for ( Object obj : describable.getDescriptions() ) {
            set2.add( ( Description ) getLatestVersion( ( Describable ) obj, ci ) );
        }
        describable.setDescriptions( set2 );

        // prepare updated set
        Set<OntologyTerm> set3 = new HashSet<OntologyTerm>();

        // load all the latest versions into the new set.
        for ( Object obj : describable.getAnnotations() ) {
            Identifiable identifiable = ( Identifiable ) obj;
            identifiable = ( Identifiable ) reService.findLatestByEndurant( identifiable.getEndurant().getIdentifier() );
            set3.add( ( OntologyTerm ) ci.getLatestVersion( identifiable ) );
        }
        describable.setAnnotations( set3 );

        // ensure the external uri is also completely up to date.
        if ( describable.getExturi() != null ) {
            describable.setExturi( ( Uri ) getLatestVersion( describable.getExturi(), ci ) );
        }

        // ensure the security reference is also completely up to date.
        if ( describable.getSecurity() != null ) {
            Security s = describable.getSecurity();
            s = ( Security ) reService.findLatestByEndurant( s.getEndurant().getIdentifier() );
            describable.setSecurity( ( Security ) ci.getLatestVersion( s ) );
        }

        // prepare updated set
        Set<NameValueType> set4 = new HashSet<NameValueType>();
        // load all the latest versions into the new set.
        for ( Object obj : describable.getPropertySets() ) {
            set4.add( ( NameValueType ) getLatestVersion( ( Describable ) obj, ci ) );
        }
        describable.setPropertySets( set4 );

        return describable;
    }
}
