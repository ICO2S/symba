package net.sourceforge.symba.util.conversion.helper;


import fugeOM.Bio.Data.ExternalData;
import fugeOM.Bio.Material.Material;
import fugeOM.Common.Description.Description;
import fugeOM.Common.Protocol.*;
import fugeOM.service.RealizableEntityServiceException;
import fugeOM.util.generatedJAXB2.*;
import net.sourceforge.symba.util.CisbanHelper;

import java.io.PrintWriter;
import java.util.Date;
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

public class CisbanProtocolApplicationHelper implements MappingHelper<ProtocolApplication, FugeOMCommonProtocolProtocolApplicationType> {
    private final CisbanIdentifiableHelper ci;
    private final CisbanParameterizableApplicationHelper cparapp;
    private final CisbanDescribableHelper cd;
    private final CisbanGenericProtocolApplicationHelper cgpa;
    private final CisbanEquipmentHelper ceq;
    private final CisbanSoftwareHelper csw;
    private final CisbanActionHelper ca;
    private final CisbanHelper helper;

    public CisbanProtocolApplicationHelper() {
        this.ci = new CisbanIdentifiableHelper();
        this.cd = ci.getCisbanDescribableHelper();
        this.cparapp = new CisbanParameterizableApplicationHelper();
        this.cgpa = new CisbanGenericProtocolApplicationHelper();
        this.ceq = new CisbanEquipmentHelper();
        this.csw = new CisbanSoftwareHelper();
        this.ca = new CisbanActionHelper();
        this.helper = CisbanHelper.create( reService );
    }

    // @todo Have not coded PartitionPair
    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public ProtocolApplication unmarshal(
            FugeOMCommonProtocolProtocolApplicationType protocolApplicationXML, ProtocolApplication protocolApplication )
            throws RealizableEntityServiceException {

        // determine what sort of protocol application it is
        if ( protocolApplicationXML instanceof FugeOMCommonProtocolGenericProtocolApplicationType ) {

            // Retrieve latest version from the database.
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) protocolApplication;

            // get protocol application attributes
            genericProtocolApplication = ( GenericProtocolApplication ) ci
                    .unmarshal( protocolApplicationXML, genericProtocolApplication );
            genericProtocolApplication = ( GenericProtocolApplication ) cparapp
                    .unmarshal( protocolApplicationXML, genericProtocolApplication );

            genericProtocolApplication.setActivityDate( protocolApplicationXML.getActivityDate() );

            Set<SoftwareApplication> set = new HashSet<SoftwareApplication>();
            for ( FugeOMCommonProtocolSoftwareApplicationType typeXML : protocolApplicationXML
                    .getSoftwareApplication() ) {
                SoftwareApplication application = ( SoftwareApplication ) helper.getOrCreateLatest(
                        typeXML.getEndurant(), "fugeOM.Common.Protocol.SoftwareAppEndurant",
                        typeXML.getName(), "fugeOM.Common.Protocol.SoftwareApplication", System.err );
                application = ( SoftwareApplication ) ci.unmarshal( typeXML, application );
                application.setAppliedSoftware( ( Software ) reService.findIdentifiable( typeXML.getSoftwareRef() ) );
                if ( application.getId() != null ) {
                    helper.assignAndLoadIdentifiable(
                            application, "fugeOM.Common.Protocol.SoftwareApplication", System.err );
                } else {
                    helper.loadIdentifiable( application, "fugeOM.Common.Protocol.SoftwareApplication", System.err );
                }
                set.add( application );
            }
            genericProtocolApplication.setSoftwareApplications( set );

            Set<EquipmentApplication> set2 = new HashSet<EquipmentApplication>();
            for ( FugeOMCommonProtocolEquipmentApplicationType typeXML : protocolApplicationXML
                    .getEquipmentApplication() ) {
                EquipmentApplication application = ( EquipmentApplication ) helper.getOrCreateLatest(
                        typeXML.getEndurant(), "fugeOM.Common.Protocol.EquipAppEndurant",
                        typeXML.getName(), "fugeOM.Common.Protocol.EquipmentApplication", System.err );
                application = ( EquipmentApplication ) ci.unmarshal( typeXML, application );
                application
                        .setAppliedEquipment( ( Equipment ) reService.findIdentifiable( typeXML.getEquipmentRef() ) );

                if ( typeXML.getParameterValue() != null && typeXML.getParameterValue().size() > 0 ) {

                    application = ( EquipmentApplication ) cparapp.unmarshal(
                            typeXML, application );
                }

                if ( application.getId() != null ) {
                    helper.assignAndLoadIdentifiable(
                            application, "fugeOM.Common.Protocol.EquipmentApplication", System.err );
                } else {
                    helper.loadIdentifiable( application, "fugeOM.Common.Protocol.EquipmentApplication", System.err );
                }
                set2.add( application );
            }
            genericProtocolApplication.setEquipmentApplications( set2 );

            Set<ActionApplication> set3 = new HashSet<ActionApplication>();
            for ( FugeOMCommonProtocolActionApplicationType typeXML : protocolApplicationXML.getActionApplication() ) {
                ActionApplication application = ( ActionApplication ) reService
                        .createDescribableOb( "fugeOM.Common.Protocol.ActionApplication" );
                application = ( ActionApplication ) cd.unmarshal( typeXML, application );
                application.setAction( ( Action ) reService.findIdentifiable( typeXML.getActionRef() ) );
                if ( typeXML.getProtocolApplicationRef() != null ) {
                    application.setProtAppRef(
                            ( ProtocolApplication ) reService.findIdentifiable( typeXML.getProtocolApplicationRef() ) );
                }

                Description description = ( Description ) reService.createDescribableOb( "fugeOM.Common.Description" );
                description = ( Description ) cd
                        .unmarshal( typeXML.getActionDeviation().getDescription(), description );
                description.setText( typeXML.getActionDeviation().getDescription().getText() );
                reService.createObInDB( "fugeOM.Common.Description", description );
                application.setActionDeviation( description );

                reService.createObInDB( "fugeOM.Common.Protocol.ActionApplication", application );
                set3.add( application );
            }
            genericProtocolApplication.setActionApplications( set3 );

            if ( protocolApplicationXML.getProtocolDeviation() != null ) {
                Description description = ( Description ) reService.createDescribableOb( "fugeOM.Common.Description" );
                description = ( Description ) cd.unmarshal(
                        protocolApplicationXML.getProtocolDeviation().getDescription(), description );
                description.setText( protocolApplicationXML.getProtocolDeviation().getDescription().getText() );
                reService.createObInDB( "fugeOM.Common.Description", description );
                genericProtocolApplication.setDeviation( description );
            }

            // get generic protocol application attributes
            genericProtocolApplication = cgpa.unmarshal(
                    ( FugeOMCommonProtocolGenericProtocolApplicationType ) protocolApplicationXML,
                    genericProtocolApplication );

            if ( genericProtocolApplication.getId() != null ) {
                helper.assignAndLoadIdentifiable(
                        genericProtocolApplication, "fugeOM.Common.Protocol.GenericProtocolApplication", System.err );
            } else {
                helper.loadIdentifiable(
                        genericProtocolApplication, "fugeOM.Common.Protocol.GenericProtocolApplication", System.err );
            }

            return genericProtocolApplication;
        }

        return null; // shouldn't get here as there is currently only one type of ProtocolApplication allowed.
    }

    // @todo Have not coded PartitionPair
    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FugeOMCommonProtocolProtocolApplicationType marshal(
            FugeOMCommonProtocolProtocolApplicationType protocolApplicationXML, ProtocolApplication protocolApplication )
            throws RealizableEntityServiceException {

        // determine what sort of protocol application it is
        if ( protocolApplication instanceof GenericProtocolApplication ) {
            FugeOMCommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML = ( FugeOMCommonProtocolGenericProtocolApplicationType ) protocolApplicationXML;

            // get generic protocol application attributes now so that lazy loading is sorted
            genericProtocolApplicationXML = cgpa.marshal(
                    genericProtocolApplicationXML, ( GenericProtocolApplication ) protocolApplication );

            // get protocol application attributes
            genericProtocolApplicationXML = ( FugeOMCommonProtocolGenericProtocolApplicationType ) ci
                    .marshal( genericProtocolApplicationXML, protocolApplication );
            genericProtocolApplicationXML = ( FugeOMCommonProtocolGenericProtocolApplicationType ) cparapp
                    .marshal(
                            genericProtocolApplicationXML, ( GenericProtocolApplication ) protocolApplication );

            if ( protocolApplication.getActivityDate() != null ) {
                genericProtocolApplicationXML.setActivityDate( protocolApplication.getActivityDate() );
            }

            for ( Object o : protocolApplication.getSoftwareApplications() ) {
                SoftwareApplication application = ( SoftwareApplication ) o;
                FugeOMCommonProtocolSoftwareApplicationType typeXML = new FugeOMCommonProtocolSoftwareApplicationType();
                typeXML = ( FugeOMCommonProtocolSoftwareApplicationType ) ci.marshal(
                        typeXML, application );
                typeXML.setSoftwareRef( application.getAppliedSoftware().getIdentifier() );
                genericProtocolApplicationXML.getSoftwareApplication().add( typeXML );
            }

            for ( Object o : protocolApplication.getEquipmentApplications() ) {
                EquipmentApplication application = ( EquipmentApplication ) o;
                FugeOMCommonProtocolEquipmentApplicationType typeXML = new FugeOMCommonProtocolEquipmentApplicationType();
                typeXML = ( FugeOMCommonProtocolEquipmentApplicationType ) ci
                        .marshal( typeXML, application );
                typeXML.setEquipmentRef( application.getAppliedEquipment().getIdentifier() );
                if ( application.getParameterValues() != null && application.getParameterValues().size() > 0 ) {

                    typeXML = ( FugeOMCommonProtocolEquipmentApplicationType ) cparapp
                            .marshal(
                                    typeXML, application );
                }
                genericProtocolApplicationXML.getEquipmentApplication().add( typeXML );
            }
            for ( Object o : protocolApplication.getActionApplications() ) {
                ActionApplication application = ( ActionApplication ) o;
                FugeOMCommonProtocolActionApplicationType typeXML = new FugeOMCommonProtocolActionApplicationType();

                typeXML = ( FugeOMCommonProtocolActionApplicationType ) cd.marshal( typeXML, application );
                typeXML.setActionRef( application.getAction().getIdentifier() );

                if ( application.getProtAppRef() != null ) {
                    typeXML.setProtocolApplicationRef( application.getProtAppRef().getIdentifier() );
                }

                if ( application.getActionDeviation() != null ) {
                    FugeOMCommonDescriptionDescriptionType descXML = new FugeOMCommonDescriptionDescriptionType();
                    descXML = ( FugeOMCommonDescriptionDescriptionType ) cd
                            .marshal( descXML, application.getActionDeviation() );
                    descXML.setText( application.getActionDeviation().getText() );
                    FugeOMCommonProtocolActionApplicationType.ActionDeviation deviation = new FugeOMCommonProtocolActionApplicationType.ActionDeviation();
                    deviation.setDescription( descXML );

                    typeXML.setActionDeviation( deviation );
                }

                genericProtocolApplicationXML.getActionApplication().add( typeXML );
            }

            if ( protocolApplication.getDeviation() != null ) {
                FugeOMCommonProtocolProtocolApplicationType.ProtocolDeviation pdXML = new FugeOMCommonProtocolProtocolApplicationType.ProtocolDeviation();
                FugeOMCommonDescriptionDescriptionType descXML = new FugeOMCommonDescriptionDescriptionType();
                descXML = ( FugeOMCommonDescriptionDescriptionType ) cd
                        .marshal( descXML, protocolApplication.getDeviation() );
                descXML.setText( protocolApplication.getDeviation().getText() );
                pdXML.setDescription( descXML );
                genericProtocolApplicationXML.setProtocolDeviation( pdXML );
            }
            return genericProtocolApplicationXML;
        }
        return null; // shouldn't get here as there is currently only one type of ProtocolApplication allowed.
    }

    // the generate code is too complex to be properly dealt with by the interface method
    // please use generateRandomXMLWithLinksOut instead
    public FugeOMCommonProtocolProtocolApplicationType generateRandomXML( FugeOMCommonProtocolProtocolApplicationType fugeOMCommonProtocolProtocolApplicationType ) {
        return null;
    }

    // at this stage, frXML may not have the new equipment and software - the protocol collection may be the only one to have it
    public FugeOMCommonProtocolProtocolApplicationType generateRandomXMLWithLinksOut(
            int ordinal,
            FugeOMCollectionProtocolCollectionType protocolCollectionXML,
            FugeOMCollectionFuGEType frXML ) {

        FugeOMCommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML = new FugeOMCommonProtocolGenericProtocolApplicationType();

        // get protocol application attributes
        genericProtocolApplicationXML = ( FugeOMCommonProtocolGenericProtocolApplicationType ) ci
                .generateRandomXML( genericProtocolApplicationXML );
        genericProtocolApplicationXML = ( FugeOMCommonProtocolGenericProtocolApplicationType ) cparapp
                .generateRandomXMLWithLinksOut( genericProtocolApplicationXML, protocolCollectionXML );

        genericProtocolApplicationXML.setActivityDate( new Date() );
        if ( !protocolCollectionXML.getSoftware().isEmpty() ) {
            FugeOMCommonProtocolSoftwareApplicationType type = new FugeOMCommonProtocolSoftwareApplicationType();
            type = ( FugeOMCommonProtocolSoftwareApplicationType ) ci.generateRandomXML( type );
            type.setSoftwareRef( protocolCollectionXML.getSoftware().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getSoftwareApplication().add( type );
        }
        if ( !protocolCollectionXML.getEquipment().isEmpty() ) {
            FugeOMCommonProtocolEquipmentApplicationType type = new FugeOMCommonProtocolEquipmentApplicationType();
            type = ( FugeOMCommonProtocolEquipmentApplicationType ) ci.generateRandomXML( type );
            type.setEquipmentRef( protocolCollectionXML.getEquipment().get( ordinal ).getValue().getIdentifier() );
            genericProtocolApplicationXML.getEquipmentApplication().add( type );
        }
        if ( !protocolCollectionXML.getProtocol().isEmpty() ) {
            FugeOMCommonProtocolActionApplicationType type = new FugeOMCommonProtocolActionApplicationType();
            type = ( FugeOMCommonProtocolActionApplicationType ) cd.generateRandomXML( type );

            FugeOMCommonProtocolGenericProtocolType gpType = ( FugeOMCommonProtocolGenericProtocolType ) protocolCollectionXML
                    .getProtocol().get( ordinal ).getValue();
            type.setActionRef( gpType.getGenericAction().get( ordinal ).getIdentifier() );
            FugeOMCommonDescriptionDescriptionType descXML = new FugeOMCommonDescriptionDescriptionType();
            descXML = ( FugeOMCommonDescriptionDescriptionType ) cd.generateRandomXML( descXML );
            descXML.setText( String.valueOf( Math.random() ) );
            FugeOMCommonProtocolActionApplicationType.ActionDeviation deviation = new FugeOMCommonProtocolActionApplicationType.ActionDeviation();
            deviation.setDescription( descXML );

            type.setActionDeviation( deviation );
            genericProtocolApplicationXML.getActionApplication().add( type );

            if ( ordinal > 0 ) {
                type.setProtocolApplicationRef(
                        protocolCollectionXML.getProtocolApplication().get( 0 ).getValue().getIdentifier() );
            }
        }

        FugeOMCommonProtocolProtocolApplicationType.ProtocolDeviation pdXML = new FugeOMCommonProtocolProtocolApplicationType.ProtocolDeviation();
        FugeOMCommonDescriptionDescriptionType descXML = new FugeOMCommonDescriptionDescriptionType();
        descXML = ( FugeOMCommonDescriptionDescriptionType ) cd.generateRandomXML( descXML );
        descXML.setText( String.valueOf( Math.random() ) );
        pdXML.setDescription( descXML );
        genericProtocolApplicationXML.setProtocolDeviation( pdXML );

        genericProtocolApplicationXML = cgpa
                .generateRandomXML( genericProtocolApplicationXML, ordinal, protocolCollectionXML, frXML );
        return genericProtocolApplicationXML;
    }

    public ProtocolApplication getLatestVersion(
            ProtocolApplication protocolApplication ) throws RealizableEntityServiceException {

        // get the latest version of the identifiables in this object
        protocolApplication = ( ProtocolApplication ) reService
                .findLatestByEndurant( protocolApplication.getEndurant().getIdentifier() );

        protocolApplication = ( ProtocolApplication ) ci.getLatestVersion( protocolApplication );

        protocolApplication = ( ProtocolApplication ) cparapp.getLatestVersion( protocolApplication );

        if ( protocolApplication.getDeviation() != null ) {
            protocolApplication.setDeviation( ( Description ) cd.getLatestVersion( protocolApplication ) );
        }

        if ( !protocolApplication.getSoftwareApplications().isEmpty() ) {
            Set<SoftwareApplication> set = new HashSet<SoftwareApplication>();
            for ( Object o : protocolApplication.getSoftwareApplications() ) {
                SoftwareApplication application = ( SoftwareApplication ) o;
                application = ( SoftwareApplication ) reService
                        .findLatestByEndurant( application.getEndurant().getIdentifier() );
                application = ( SoftwareApplication ) ci.getLatestVersion( application );
                application.setAppliedSoftware( csw.getLatestVersion( application.getAppliedSoftware() ) );
                set.add( application );
            }
            protocolApplication.setSoftwareApplications( set );
        }

        if ( !protocolApplication.getEquipmentApplications().isEmpty() ) {
            Set<EquipmentApplication> set = new HashSet<EquipmentApplication>();
            for ( Object o : protocolApplication.getEquipmentApplications() ) {
                EquipmentApplication application = ( EquipmentApplication ) o;
                application = ( EquipmentApplication ) reService
                        .findLatestByEndurant( application.getEndurant().getIdentifier() );
                application = ( EquipmentApplication ) ci.getLatestVersion( application );
                application.setAppliedEquipment( ceq.getLatestVersion( application.getAppliedEquipment() ) );
                set.add( application );
            }
            protocolApplication.setEquipmentApplications( set );
        }
        if ( !protocolApplication.getActionApplications().isEmpty() ) {
            for ( Object o : protocolApplication.getActionApplications() ) {
                ActionApplication application = ( ActionApplication ) o;
                application = ( ActionApplication ) cd.getLatestVersion( application );
                application.setAction( ca.getLatestVersion( application.getAction() ) );
                if ( application.getActionDeviation() != null ) {
                    application.setActionDeviation(
                            ( Description ) cd.getLatestVersion( application.getActionDeviation() ) );
                }
                if ( application.getProtAppRef() != null ) {
                    application.setProtAppRef( getLatestVersion( application.getProtAppRef() ) );
                }
            }
        }

        // determine what sort of protocol application it is
        if ( protocolApplication instanceof GenericProtocolApplication ) {
            protocolApplication = cgpa.getLatestVersion( ( GenericProtocolApplication ) protocolApplication );
//            if ( ( ( GenericProtocolApplication ) protocolApplication ).getGenericOutputMaterials() != null &&
//                    !( ( GenericProtocolApplication ) protocolApplication ).getGenericOutputMaterials().isEmpty() ) {
//                System.out.println( "Found Output Materials directly after get latest version for GPA" );
//            } else {
//                System.out.println( "Found No Output Materials directly after get latest version for GPA" );
//
//            }
        }

        return protocolApplication;
    }

    public void prettyHtml( ProtocolApplication pa, PrintWriter printStream ) throws RealizableEntityServiceException {

        GenericProtocolApplication gpa = ( GenericProtocolApplication ) pa;
        // get any lazily loaded objects
        gpa = ( GenericProtocolApplication ) reService.greedyGet( gpa );

        boolean tdPrinted = false;
        if ( gpa.getGenericInputCompleteMaterials() != null && gpa.getGenericInputCompleteMaterials().size() > 0 ) {
            printStream.println( "<td>" );
            tdPrinted = true;
            for ( Object obj : gpa.getGenericInputCompleteMaterials() ) {
                // will print the same info, regardless of the type of Material
                Material material = ( Material ) obj;
                material = ( Material ) reService.greedyGet( material );
                ci.prettyHtml( null, material, printStream );
                // should we print the treatments?
                // then print the ontology terms
                printStream.println( "<br>" );
                if ( material.getMaterialType() != null ) {
                    printStream.println( material.getMaterialType().getTerm() );
                    if ( material.getMaterialType().getOntologySource() != null )
                        printStream.println( "(" + material.getMaterialType().getOntologySource().getName() + ")" );
                    printStream.println( "<br>" );
                }
                if ( material.getCharacteristics() != null ) {
                    CisbanOntologyCollectionHelper helper = new CisbanOntologyCollectionHelper();
                    helper.prettyHtml( material.getCharacteristics(), printStream );
                }
            }
        }
        if ( gpa.getGenericOutputData() != null && gpa.getGenericOutputData().size() > 0 ) {
            if ( !tdPrinted ) {
                printStream.println( "<td>" );
                tdPrinted = true;
            }
            for ( Object obj : gpa.getGenericOutputData() ) {
                if ( obj instanceof ExternalData ) {
                    ExternalData data = ( ExternalData ) obj;
                    ci.prettyHtml( null, data, printStream );
                    // then print out a form button
                    printStream.println(
                            "<form action=\"downloadSingleFile.jsp\"><input type=\"hidden\" name=\"identifier\" value=\"" +
                                    data.getLocation() + "\"/>" +
                                    "<input type=\"hidden\" name=\"friendly\" value=\"" +
                                    data.getName() + "\"/>" +
                                    "<input type=\"submit\" value=\"Download This File\"/></form>" );
                }
            }
        }
        if ( tdPrinted ) {
            printStream.println( "</td>" );
        }

    }
}
