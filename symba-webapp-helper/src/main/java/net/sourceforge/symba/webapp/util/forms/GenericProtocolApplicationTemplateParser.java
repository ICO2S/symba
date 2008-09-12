package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.SymbaFormSessionBean;

import javax.servlet.http.HttpSession;

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
public class GenericProtocolApplicationTemplateParser {

    public static StringBuffer parse( HttpSession session,
                                      SymbaFormSessionBean symbaFormSessionBean,
                                      DatafileSpecificMetadataStore info,
                                      int currentDataFile,
                                      PersonBean personBean ) {

        StringBuffer buffer = new StringBuffer();

        // sometimes, we may get GenericParameters from Dummy GPAs associated with the experiment. This is how we
        // differentiate GenericParameters that are not meant to be changed (== no Dummy GPA) from those that are
        // meant to be changed (== has dummy GPA). Search the GPAs
        // for dummies named with the addition of "XXX net.sourceforge.symba.keywords.dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        //
        // Only currently valid for AtomicValues.
        //
        // Also, GenericMaterials (inputs and outputs) will be linked from the templated dummy GPAs
        // For assays that take the output of material transformations as their inputs, only display those
        // materials which are valid choices, and don't allow modifications of them. If they wish to modify, provide
        // them with a link to the add/modify specimen pages.
        GenericProtocol chosenAssayProtocol = ( GenericProtocol ) personBean.getEntityService().getIdentifiable( info
                .getNestedActions().getActionHierarchy()
                .get( info.getNestedActions().getActionHierarchy().size() - 1 ).getProtocolOfActionIdentifier() );

        for ( Object gpaObj : personBean.getSymbaEntityService().getLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) gpaObj;
            // we're only interested in the GPA of the dummy assay (the lowest level). This will be the last element
            // in the ActionInformation ArrayList within the ActionHierarchyScheme.
            if ( genericProtocolApplication.getName().trim().contains( chosenAssayProtocol.getName() ) ) {

                // Print material portion of the form
                buffer.append(
                        MaterialTemplateParser.createMaterialForDataFormContents( session, personBean,
                                symbaFormSessionBean, info, currentDataFile,
                                genericProtocolApplication ) );

                // make a shorter version of the chosen assay protocol name
                String shortProtocolName = chosenAssayProtocol.getName();
                if ( chosenAssayProtocol.getName().contains( "(Component of" ) ) {
                    shortProtocolName =
                            chosenAssayProtocol.getName()
                                    .substring( 0, chosenAssayProtocol.getName().indexOf( "(Component of" ) );
                }

                if ( !genericProtocolApplication.getDescriptions().isEmpty() ||
                     !genericProtocolApplication.getParameterValues().isEmpty() ) {
                    buffer.append( "<fieldset>" ).append( System.getProperty( "line.separator" ) );
                    buffer.append( "<legend>Further Details of the " ).append( shortProtocolName )
                            .append( "</legend>" );
                    buffer.append( System.getProperty( "line.separator" ) ).append( "<ol>" )
                            .append( System.getProperty( "line.separator" ) );
                }

                String gpaParentEndurantId = genericProtocolApplication.getProtocol()
                        .getEndurant()
                        .getIdentifier();

                // Firstly, the template might contain descriptions beginning with "TextBox::". In this case,
                // the template is requesting that a text box be provided with the instructions given after the
                // "TextBox::" phrase. The contents of the text box will be stored in the same location as the
                // instructions in the template.
                for ( Object descriptionObj : genericProtocolApplication.getDescriptions() ) {
                    Description description = ( Description ) descriptionObj;
                    if ( description.getText() != null && description.getText().length() > 0 &&
                         description.getText().startsWith( "TextBox::ProtocolDescription::" ) ) {
                        String[] parsedStrings = description.getText().split( "::" );
                        String descriptionLabel =
                                "GPA" + parsedStrings[1] + "::" + gpaParentEndurantId + "::" + currentDataFile;
                        buffer.append( "<li>" );
                        buffer.append( "<label for=\"" ).append( descriptionLabel ).append( "\">" )
                                .append( parsedStrings[2] ).append( ": </label>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                        buffer.append( "<textarea id=\"" ).append( descriptionLabel ).append( "\" name=\"" )
                                .append( descriptionLabel ).append( "\"" );
                        buffer.append( "rows=\"5\" cols=\"40\">" );
                        buffer.append( System.getProperty( "line.separator" ) );
                        if ( info.getGenericProtocolApplicationInfo() != null &&
                             info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ) != null ) {
                            // might be empty, but won't affect the display if it is.
                            buffer.append(
                                    info.getGenericProtocolApplicationInfo()
                                            .get( gpaParentEndurantId )
                                            .getDescriptions().get( "ProtocolDescription" ) );
                        }
                        buffer.append( "</textarea><br/>" );
                        buffer.append( "</li>" );
                        buffer.append( System.getProperty( "line.separator" ) );
                    }
                }

                // Print parameter values portion of the form
                buffer.append( parseParameterValues( info, currentDataFile, genericProtocolApplication,
                        gpaParentEndurantId ) );

                if ( !genericProtocolApplication.getDescriptions().isEmpty() ||
                     !genericProtocolApplication.getParameterValues().isEmpty() ) {
                    buffer.append( "</ol>" );
                    buffer.append( "</fieldset>" );
                    buffer.append( System.getProperty( "line.separator" ) );
                }
            }
        }
        buffer.append( "<br/>" );
        buffer.append( System.getProperty( "line.separator" ) );

        return buffer;
    }

    private static StringBuffer parseParameterValues( DatafileSpecificMetadataStore info,
                                                      int currentDataFile,
                                                      GenericProtocolApplication genericProtocolApplication,
                                                      String gpaParentEndurantId ) {

        StringBuffer buffer = new StringBuffer();

        // Now provide the choices requested in the dummy GenericParameter. Currently, only an AtomicValue
        // with a fixed unit type is allowed.
        for ( Object object : genericProtocolApplication.getParameterValues() ) {
            ParameterValue parameterValue = ( ParameterValue ) object;
            if ( parameterValue.getValue() instanceof AtomicValue ) {
                Parameter parameter = parameterValue.getParameter();
                GenericParameter genericParameter = ( GenericParameter ) parameter;
                // AtomicValues just have a string value. Ask the user for it

                AtomicValue atomicValue = ( AtomicValue ) genericParameter.getDefaultValue();
                // retrieve the unit used, if present.
                String unitName = "";
                if ( atomicValue.getUnit() != null &&
                     atomicValue.getUnit().getTerm().length() > 0 ) {
                    unitName = atomicValue.getUnit().getTerm();
                }

                String nameOfField = "atomicParameterOfGPA::" +
                                     gpaParentEndurantId + "::" +
                                     genericParameter.getEndurant().getIdentifier() + "::" + currentDataFile;
                String nameOfParameter = "value"; // default
                if ( genericParameter.getParameterType() != null ) {
                    nameOfParameter = genericParameter.getParameterType().getTerm();
                } else if ( genericParameter.getName() != null && genericParameter.getName().length() > 0 ) {
                    nameOfParameter = genericParameter.getName();
                }
                String instructions =
                        "<label for=\"" + nameOfField + "\">Please fill in the " + nameOfParameter;
                if ( unitName.length() > 0 ) {
                    instructions = instructions + " ( in " + unitName + ")";
                }
                instructions += ":</label>";
                buffer.append( System.getProperty( "line.separator" ) );

                buffer.append( "<li>" );
                buffer.append( instructions );
                buffer.append( "<input id=\"" ).append( nameOfField ).append( "\" name=\"" )
                        .append( nameOfField ).append( "\"" );
                if ( info.getGenericProtocolApplicationInfo() != null &&
                     info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ) != null ) {
                    String inputValue = info.getGenericProtocolApplicationInfo()
                            .get( gpaParentEndurantId )
                            .getParameterAndAtomics()
                            .get( genericParameter.getEndurant().getIdentifier() );
                    if ( inputValue != null && inputValue.length() > 0 ) {
                        buffer.append( " value=\"" ).append( inputValue ).append( "\"" );
                    }
                }
                buffer.append( "/><br/>" );
                buffer.append( System.getProperty( "line.separator" ) );
                buffer.append( "<br/>" );
                buffer.append( "</li>" );
                buffer.append( System.getProperty( "line.separator" ) );
            }
        }

        return buffer;
    }
}
