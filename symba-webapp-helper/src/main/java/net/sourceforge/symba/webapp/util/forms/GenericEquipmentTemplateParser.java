package net.sourceforge.symba.webapp.util.forms;

import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.protocol.GenericProtocol;
import net.sourceforge.fuge.common.protocol.GenericEquipment;
import net.sourceforge.fuge.common.protocol.GenericParameter;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.measurement.AtomicValue;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.ontology.OntologySource;
import net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore;
import net.sourceforge.symba.webapp.util.PersonBean;
import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionInformation;

import java.util.List;

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
public class GenericEquipmentTemplateParser {

    public static StringBuffer parse( DatafileSpecificMetadataStore info,
                                      int currentDataFile,
                                      PersonBean personBean ) {

        StringBuffer buffer = new StringBuffer();

        // we also allow developers choose various settings for their GenericEquipment associated with the experiment.
        // Any value stored in a GenericParameter is changeable. Any value in other references to ontology terms
        // (e.g. make, model, annotations) is considered constant and unchangeable.
        // Search the GenericEquipment for GenericParameters, and print out their options.
        GenericProtocol chosenProtocol = ( GenericProtocol ) personBean.getEntityService()
                .getIdentifiable( info.getNestedActions().getActionHierarchy().get(
                        info.getNestedActions().getActionHierarchy().size() - 1 ).getProtocolOfActionIdentifier() );

        if ( chosenProtocol.getEquipment() != null ) {
            for ( GenericEquipment genericEquipment : chosenProtocol.getEquipment() ) {

                // we're only interested in making more fields in the form if there are any GenericParameters here.
                if ( genericEquipment.getParameters() != null && !genericEquipment.getParameters().isEmpty() ) {
                    buffer.append( "<fieldset>" );
                    buffer.append( "<legend>" ).append( genericEquipment.getName() ).append( "</legend>" );
                    buffer.append( "<ol>" );

                    // Provide the name of the equipment as a hidden variable
                    buffer.append( "<input type=\"hidden\" name=\"equipmentName::" )
                            .append( genericEquipment.getEndurant().getIdentifier() ).append( "::" )
                            .append( currentDataFile ).append( "\" value=\"" ).append( genericEquipment.getName() )
                            .append( "\"/><br/>" );

                    // Now allow a free-text description of the Equipment, to be added in the final stages to the
                    // EquipmentApplication element of this protocol's GPA.
                    String nameOfDescriptionField =
                            "equipmentDescription::" + genericEquipment.getEndurant().getIdentifier() + "::" +
                            currentDataFile;
                    buffer.append( "<li>" );
                    buffer.append( "<label for=\"" ).append( nameOfDescriptionField ).append( "\">Description of the " )
                            .append( genericEquipment.getName() ).append( ":</label>" );
                    buffer.append( "<textarea id=\"" ).append( nameOfDescriptionField ).append( "\" name=\"" )
                            .append( nameOfDescriptionField ).append( "\" rows=\"5\" cols=\"40\">" );

                    if ( info.getGenericEquipmentInfo().get( genericEquipment.getEndurant().getIdentifier() ) !=
                         null ) {
                        String inputValue = info.getGenericEquipmentInfo()
                                .get( genericEquipment.getEndurant().getIdentifier() ).getFreeTextDescription();
                        if ( inputValue != null && inputValue.length() > 0 ) {
                            buffer.append( inputValue );
                        }
                    }
                    buffer.append( "</textarea><br/>" );
                    buffer.append( "</li>" );

                    // Now, retrieve all parameters (currently only valid for ComplexValue and AtomicValue).
                    for ( Object paramObj : genericEquipment.getParameters() ) {
                        if ( paramObj instanceof GenericParameter ) {
                            GenericParameter genericParameter = ( GenericParameter ) paramObj;
                            if ( genericParameter.getDefaultValue() instanceof ComplexValue ) {
                                // ComplexValue objects reference an OntologyTerm,
                                //  which in turn is associated with an OntologySource. Instead of displaying just the referenced
                                // OntologyTerm, a pull-down menu should be displayed of ALL of the OntologyTerms in the database
                                // associated with that OntologySource.
                                ComplexValue complexValue =
                                        ( ComplexValue ) ( ( GenericParameter ) paramObj ).getDefaultValue();
                                buffer.append( "<br/>" );

                                // unchecked cast warning provided by javac when using generics in Lists/Sets and
                                // casting from Object, even though runtime can handle this.
                                // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                                @SuppressWarnings( "unchecked" )
                                List<OntologyTerm> ontologyTerms = personBean.getSymbaEntityService()
                                        .getLatestTermsWithSource(
                                                complexValue.getValue()
                                                        .getOntologySource()
                                                        .getEndurant().getIdentifier() );
                                if ( !ontologyTerms.isEmpty() ) {

                                    // the ontology source for this complex value may have a description which tells the user
                                    // how to select the correct ontology term. Check for that.
                                    OntologySource ontologySource = complexValue.getValue()
                                            .getOntologySource();
                                    ontologySource = ( OntologySource ) personBean.getSymbaEntityService()
                                            .getLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                                    String nameOfField = "parameterOfEquipment::" +
                                                         genericEquipment.getEndurant().getIdentifier() + "::" +
                                                         genericParameter.getEndurant().getIdentifier() + "::" +
                                                         currentDataFile;
                                    String nameOfParameter = "term"; // default
                                    if ( genericParameter.getParameterType() != null ) {
                                        nameOfParameter = genericParameter.getParameterType().getTerm();
                                    } else if ( genericParameter.getName() != null &&
                                                genericParameter.getName().length() > 0 ) {
                                        nameOfParameter = genericParameter.getName();
                                    }

                                    String instructions =
                                            "<label for=\"" + nameOfField + "\">Please select your " +
                                            nameOfParameter +
                                            ":</label>";
                                    for ( Object descObj : ontologySource.getDescriptions() ) {
                                        Description desc = ( Description ) descObj;
                                        if ( desc.getText().startsWith( "Instructions: " ) ) {
                                            instructions =
                                                    "<label for=\"" + nameOfField + "\">" +
                                                    desc.getText().substring( 14 ) +
                                                    "</label>";
                                        }
                                    }

                                    buffer.append( "<li>" );
                                    buffer.append( instructions );
                                    buffer.append( "<select id=\"" ).append( nameOfField ).append( "\" name=\"" )
                                            .append( nameOfField ).append( "\">" );
                                    buffer.append(
                                            "<option value=\"\" ></option>" );//js now allways an empty option on pos 0
                                    for ( OntologyTerm ot : ontologyTerms ) {
                                        String inputStartValue =
                                                "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                                        if ( info.getGenericEquipmentInfo() != null &&
                                             info.getGenericEquipmentInfo()
                                                     .get( genericEquipment.getEndurant().getIdentifier() ) !=
                                                                                                            null ) {
                                            String inputValue = info.getGenericEquipmentInfo()
                                                    .get( genericEquipment.getEndurant().getIdentifier() )
                                                    .
                                                            getParameterAndTerms()
                                                    .get( genericParameter.getEndurant().getIdentifier() );
                                            if ( inputValue != null &&
                                                 inputValue.equals( ot.getEndurant().getIdentifier() ) ) {
                                                inputStartValue += " selected=\"selected\"";
                                            }
                                        }
                                        buffer.append( inputStartValue ).append( ">" ).append( ot.getTerm() )
                                                .append( "</option>" );
                                    }
                                    buffer.append( "</select>" );
                                    buffer.append( "<br/>" );
                                    buffer.append( "</li>" );
                                }
                            }
                            if ( genericParameter.getDefaultValue() instanceof AtomicValue ) {
                                // AtomicValues just have a string value. Ask the user for it
                                buffer.append( "<br/>" );

                                // retrieve the unit used, if present.
                                String unitName = "";
                                if ( genericParameter.getDefaultValue().getUnit() != null &&
                                     genericParameter.getDefaultValue().getUnit().getTerm().length() > 0 ) {
                                    unitName = genericParameter.getDefaultValue().getUnit().getTerm();
                                }

                                String nameOfField = "atomicParameterOfEquipment::" +
                                                     genericEquipment.getEndurant().getIdentifier() + "::" +
                                                     genericParameter.getEndurant().getIdentifier() + "::" +
                                                     currentDataFile;
                                String nameOfParameter = "value"; // default
                                if ( genericParameter.getParameterType() != null ) {
                                    nameOfParameter = genericParameter.getParameterType().getTerm();
                                } else if ( genericParameter.getName() != null &&
                                            genericParameter.getName().length() > 0 ) {
                                    nameOfParameter = genericParameter.getName();
                                }
                                String instructions =
                                        "<label for=\"" + nameOfField + "\">Please fill in the " + nameOfParameter;
                                if ( unitName.length() > 0 ) {
                                    instructions = instructions + " ( in " + unitName + ")";
                                }
                                instructions += ":</label>";

                                buffer.append( "<li>" );
                                buffer.append( instructions );
                                buffer.append( "<input id=\"" ).append( nameOfField ).append( "\" name=\"" )
                                        .append( nameOfField ).append( "\"" );
                                if ( info.getGenericEquipmentInfo() != null &&
                                     info.getGenericEquipmentInfo()
                                             .get( genericEquipment.getEndurant().getIdentifier() ) != null ) {
                                    String inputValue = info.getGenericEquipmentInfo()
                                            .get( genericEquipment.getEndurant().getIdentifier() )
                                            .getParameterAndAtomics()
                                            .get( genericParameter.getEndurant().getIdentifier() );
                                    if ( inputValue != null ) {
                                        buffer.append( " value=\"" ).append( inputValue ).append( "\"" );
                                    }
                                }
                                buffer.append( "/><br/>" );
                                buffer.append( "<br/>" );
                                buffer.append( "</li>" );
                            }
                        }
                    }
                    buffer.append( "</ol>" );
                    buffer.append( "</fieldset>" );
                }
            }
        }
        return buffer;
    }
}
