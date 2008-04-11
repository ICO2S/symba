<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="fugeOM.Bio.Data.ExternalData" %>
<%@ page import="fugeOM.Bio.Material.GenericMaterial" %>
<%@ page import="fugeOM.Common.Description.Description" %>
<%@ page import="fugeOM.Common.Ontology.OntologySource" %>
<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <script type="text/javascript">var treatment_number = 1;</script>

    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>

</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
<p>
    (1) Introduction -> (2) Attach to an Experiment -> (3) Upload Data ->
    <font class="blueText">(4) Select Protocol</font> -> (5) Confirm Your Submission -> (6) Completion and Download
</p>

<c:if test="${param.errorMsg != null}">
    <font color="red">
        <c:out value="${param.errorMsg}"/>
    </font>
    <br/>
    <br/>
</c:if>

<h3>Please select the factors appropriate to your data file: <a
        href="help.jsp#protocol"
        onClick="return popup(this, 'notes')">[ Help ]</a></h3>

<%--
Ensure you do not have a multipart/form-data for the form's ENCTYPE if you wish to use
POST, and then a request.getParameter() in the following page. It will cause all request.getParameter()
to return null. See this page for more details:
http://www.programmersheaven.com/mb/jsp/363016/363016/ReadMessage.aspx
--%>
<form name="selectMetadata" action="metaDataValidate.jsp" method="post">
<%
    boolean inputAlreadyPresent = false;

    //        todo this entire page needs generification
    for ( int iii = 0; iii < investigationBean.getAllDataBeans().size(); iii++ ) {
        RawDataInfoBean info = investigationBean.getAllDataBeans().get( iii );

        // ensure that we choose the 2nd-level "chosen child" information, if it is present
        String chosenChildProtocolIdentifier, chosenChildProtocolName;
        if ( info.getChosenSecondLevelChildProtocolIdentifier() != null &&
                info.getChosenSecondLevelChildProtocolIdentifier().length() > 0 ) {
            chosenChildProtocolIdentifier = info.getChosenSecondLevelChildProtocolIdentifier();
            chosenChildProtocolName = info.getChosenSecondLevelChildProtocolName();
        } else {
            chosenChildProtocolIdentifier = info.getChosenChildProtocolIdentifier();
            chosenChildProtocolName = info.getChosenChildProtocolName();
            chosenChildProtocolName = info.getChosenChildProtocolName();
        }

        String selectDescName = "actionListDescription::" + iii;
        out.println( "<br/>" );
        out.println( "<hr/>" );
        out.println( "<h3>Information for " + info.getFriendlyId() + "</h3>" );
        out.println( "<p>Original name: " + info.getOldFilename() + "</p>" );

        // First, print out the form fields for the Materials.
        // sometimes, we may get factors from Materials associated with the experiment. Search the materials
        // for dummies named with the addition of "XXX Dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        for ( Object gmObj : validUser.getReService().getAllLatestGenericMaterials( true ) ) {
            GenericMaterial genericMaterial = ( GenericMaterial ) validUser.getReService().greedyGet( gmObj );
            boolean requestName = false, requestTreatment = false;
            if ( !genericMaterial.getName().contains( " Noname" ) ) {
                requestName = true;
            }
            if ( !genericMaterial.getName().contains( " Notreatment" ) ) {
                requestTreatment = true;
            }

            if ( genericMaterial.getName().trim().contains( chosenChildProtocolName ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Material Characteristics".
//                out.println( "Match Found<br/>" );
                String displayName = genericMaterial.getName()
                        .substring( 0, genericMaterial.getName().indexOf( " Dummy" ) )
                        .trim();

                out.println( "<fieldset>" );
                out.println( "<legend>" + displayName + "</legend>" );
                out.println( "<ol>" );

                // Print out any descriptions that are OntologyReplacements as a normal text field.
                for ( Object descriptionObj : genericMaterial.getDescriptions() ) {
                    Description description = ( Description ) descriptionObj;
                    if ( description.getText() != null && description.getText().length() > 0 &&
                            description.getText().startsWith( "OntologyReplacement::" ) ) {
                        // this is a field that does not have enough information yet to be promoted to
                        // a material characteristic, so it is currently a free-text field.
                        String[] parsedStrings = description.getText().split( "::" );
                        String descriptionLabel = parsedStrings[0] + "::" + parsedStrings[1] + "::" + iii;
                        out.println( "<li>" );
                        if ( parsedStrings.length == 4 && parsedStrings[2].equals( "Help" ) ) {
                            out.println(
                                    "<label for=\"" + descriptionLabel + "\">Enter the <SPAN title=\" " +
                                            parsedStrings[3] + " \" class=\"symba.simplepopup\">" + parsedStrings[1] +
                                            "</SPAN>: </label>" );
                        } else {
                            out.println(
                                    "<label for=\"" + descriptionLabel + "\">Enter the " + parsedStrings[1] +
                                            ": </label>" );
                        }
                        String inputStartValue = "<input id=\"" + descriptionLabel + "\" name=\"" + descriptionLabel + "\"";
                        if ( info.getMaterialFactorsBean() != null &&
                                info.getMaterialFactorsBean().getOntologyReplacements().get( parsedStrings[1] ) != null ) {
                            inputAlreadyPresent = true;
                            inputStartValue += " value=\"" + info.getMaterialFactorsBean().getOntologyReplacements().get( parsedStrings[1] ) + "\"";
                        }
                        out.println( inputStartValue + "><br/>" );
                        out.println( "</li>" );
                    }
                }

                // Will show the user-inputted material name if already present
                if ( requestName ) {
                    String matName = "materialName" + iii;
                    out.println( "<li>" );
                    out.println( "<label for=\"" + matName + "\">Name/ID of this Material (optional): </label>" );
                    if ( info.getMaterialFactorsBean() != null && info.getMaterialFactorsBean().getMaterialName() != null ) {
                        inputAlreadyPresent = true;
                        out.println(
                                "<input id=\"" + matName + "\" name=\"" + matName + "\" value=\"" +
                                        info.getMaterialFactorsBean().getMaterialName() +
                                        "\"><br/>" );
                    } else {
                        out.println( "<input id=\"" + matName + "\" name=\"" + matName + "\"><br/>" );
                    }
                    out.println( "<br/>" );
                    out.println( "</li>" );
                }

                // will provide a treatment box, if requested
                if ( requestTreatment ) {
                    out.println( "<li>" );
                    out.println(
                            "<p class=\"bigger\">Please enter some information about the " + displayName + ", starting " +
                                    "with treatment information. There should be a separate box for each " +
                                    "treatment performed (optional)." );

                    if ( info.getMaterialFactorsBean() != null &&
                            info.getMaterialFactorsBean().getTreatmentInfo() != null &&
                            !info.getMaterialFactorsBean().getTreatmentInfo().isEmpty() ) {
                        out.println( "<em>NOTE: Treatments already entered will remain in the system (see list below).</em>" );
                        out.println( "<ol>" );
                        inputAlreadyPresent = true;
                        for ( String singleTreatment : info.getMaterialFactorsBean().getTreatmentInfo() ) {
                            out.println( "<li>Treatment already recorded: " + singleTreatment + "</li>" );
                        }
                        out.println( "</ol>" );
                        out.println( "You may add additional treatments by entering them below." );
                    }

                    out.println( "</p>" );

                    // Each material may have had more than one treatment. Currently this is NOT stored as a controlled
                    // vocabulary, but as free text.
                    String moreTreatments = "moreTreatments" + iii;
                    String treatmentLabel = "treatment" + iii + "-";
                    String treatmentLabelFirst = treatmentLabel + "0";
                    out.println(
                            "<label for=\"" + treatmentLabelFirst + "\">Type, Dose and Length of Treatment: </label>" );
                    out.println(
                            "<textarea id=\"" + treatmentLabelFirst + "\" name=\"" + treatmentLabelFirst +
                                    "\" rows=\"5\" cols=\"40\"></textarea> " );
                    out.println( "<br/>" );

                    out.println( "<div id=\"" + moreTreatments + "\"></div>" );
                    out.println( "<br/> " );

                    out.println( "<div id=\"moreTreatmentsLink\">" );
                    out.println(
                            "<a href=\"javascript:addTreatmentInput('" + treatmentLabel +
                                    "', '" + moreTreatments + "');\">Add Another Treatment</a>" );
                    out.println( "</div>" );
                    out.println( "<br/>" );
                    out.println( "</li>" );
                }

                // Now, retrieve the materialType (singular) and all characteristics of this material.
                // Each references an OntologyTerm, which in turn is associated with an OntologySource.
                // Instead of displaying just the referenced OntologyTerm, a pull-down menu should be displayed
                // of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( genericMaterial.getMaterialType() != null ) {
//                    out.println( "<p class=\"bigger\">Next, please choose the correct Material Type.</p>" );
                    List genericList = validUser.getReService().getAllLatestTermsWithSource(
                            genericMaterial.getMaterialType()
                                    .getOntologySource()
                                    .getEndurant().getIdentifier() );
                    List<OntologyTerm> ontologyTerms = genericList;
                    if ( !ontologyTerms.isEmpty() ) {

                        // the ontology source for the material type may have a description which tells the user
                        // how to select the correct ontology term. Check for that.
                        OntologySource ontologySource = genericMaterial.getMaterialType().getOntologySource();
                        ontologySource = ( OntologySource ) validUser.getReService()
                                .findLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                        // Never allow multiple choices for material types.
                        boolean foundInstructions = false;
                        String matType = "materialType" + iii;
                        String instructions =
                                "<label for=\"" + matType + "\">Please select your material type:</label>";
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                instructions =
                                        "<label for=\"" + matType + "\">" + desc.getText().substring( 14 ) +
                                                "</label>";
                                foundInstructions = true;
                            }
                        }

                        out.println( "<li>" );
                        out.println( instructions );
                        out.println( "<select name=\"" + matType + "\">" );
                        for ( OntologyTerm ot : ontologyTerms ) {
                            String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                            if ( info.getMaterialFactorsBean() != null &&
                                    info.getMaterialFactorsBean().getMaterialType() != null &&
                                    info.getMaterialFactorsBean().getMaterialType().equals( ot.getEndurant().getIdentifier() ) ) {
                                inputAlreadyPresent = true;
                                inputStartValue += " selected=\"selected\"";
                            }
                            out.println( inputStartValue + ">" + ot.getTerm() + "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br/>" );
                        out.println( "</li>" );
                    }
                }

                int ontoCount = 0;
                for ( Object characteristicObj : genericMaterial.getCharacteristics() ) {
                    // each ontology source for a characteristic may have a description which tells the user
                    // how to select the correct ontology term. Check for that.
                    OntologySource ontologySource = ( ( OntologyTerm ) characteristicObj ).getOntologySource();
                    List genericList = validUser.getReService()
                            .getAllLatestTermsWithSource( ontologySource.getEndurant().getIdentifier() );
                    List<OntologyTerm> ontologyTerms = genericList;
                    if ( !ontologyTerms.isEmpty() ) {

                        ontologySource = ( OntologySource ) validUser.getReService()
                                .findLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                        boolean multipleAllowed = false;
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                out.println( "<p class=\"bigger\">" + desc.getText().substring( 14 ) + "</p>" );
                                // check if multiple selections are allowed by searching for the phrase
                                // "please choose all that apply."
                                if ( desc.getText().endsWith( "please choose all that apply." ) ) {
                                    multipleAllowed = true;
                                }
                            }
                        }

                        String characteristicLabel = "characteristic" + iii + "-" + ontoCount;
                        out.println( "<li>" );
                        out.println(
                                "<label for=\"" + characteristicLabel +
                                        "\">Please select your " + ontologySource.getName() + "</label>" );
                        if ( multipleAllowed ) {
                            out.println( "<select multiple name=\"" + characteristicLabel + "\">" );
                        } else {
                            out.println( "<select name=\"" + characteristicLabel + "\">" );
                        }
                        for ( OntologyTerm ot : ontologyTerms ) {
                            String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                            if ( info.getMaterialFactorsBean() != null &&
                                    info.getMaterialFactorsBean().getCharacteristics() != null ) {
                                for ( String endurantIdentifier : info.getMaterialFactorsBean().getCharacteristics() ) {
                                    if ( endurantIdentifier.equals( ot.getEndurant().getIdentifier() ) ) {
                                        inputAlreadyPresent = true;
                                        inputStartValue += " selected=\"selected\"";
                                        break; // found a match.
                                    }
                                }
                            }
                            out.println( inputStartValue + ">" + ot.getTerm() + "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br/>" );
                        out.println( "</li>" );
                    }
                    ontoCount++;
                }
                out.println( "</ol>" );
                out.println( "</fieldset>" );

                // only allow one dummy generic material per experiment, for now only!
                break;
            }
        }

        // Next, print information that is directly associated with the data file: this includes a text description
        // of the file itself, and any file format information.
        out.println( "<fieldset>" );
        out.println( "<legend>Data</legend>" );
        out.println( "<ol>" );

        // Text Box for the description of the file
        out.println( "<li>" );
        out.println(
                "<label for=\"" + selectDescName + "\">Description of File <a href=\"help.jsp#cisbanIdentifiers\"" );
        out.println( "onClick=\"return popup(this, 'notes')\">[ ? ]</a>:</label>" );
        out.println( "<textarea id=\"" + selectDescName + "\" name=\"" + selectDescName + "\" rows=\"5\" cols=\"40\">" );
        if ( info.getDataName() != null ) {
            inputAlreadyPresent = true;
            out.println( info.getDataName() );
        }
        out.println( "</textarea>" );
        out.println( "<br/>" );
        out.println( "</li>" );

        // The file format for the ExternalData associated with the experiment.
        // Search the ExternalData for dummies named with the name of the current experiment.
        for ( Object obj : validUser.getReService().getAllLatestExternalData( true ) ) {
            ExternalData externalData = ( ExternalData ) validUser.getReService().greedyGet( obj );
            if ( externalData.getName().trim().contains( investigationBean.getTopLevelProtocolName().trim() ) ) {

                // Now, retrieve the file format (singular). It references an OntologyTerm, which in turn is associated
                // with an OntologySource. Instead of displaying just the referenced OntologyTerm, a pull-down menu
                // should be displayed of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( externalData.getFileFormat() != null ) {
                    List genericList = validUser.getReService()
                            .getAllLatestTermsWithSource(
                                    externalData.getFileFormat()
                                            .getOntologySource()
                                            .getEndurant().getIdentifier() );
                    List<OntologyTerm> ontologyTerms = genericList;
                    if ( !ontologyTerms.isEmpty() ) {

                        // the ontology source for the file format may have a description which tells the user
                        // how to select the correct ontology term. Check for that.
                        OntologySource ontologySource = externalData.getFileFormat().getOntologySource();
                        ontologySource = ( OntologySource ) validUser.getReService()
                                .findLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                        String fileFormat = "fileFormat" + iii;
                        String instructions =
                                "<label for=\"" + fileFormat + "\">Please select your file format:</label>";
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                instructions = "<label for=\"" + fileFormat + "\">" + desc.getText().substring( 14 ) +
                                        "</label>";
                            }
                        }

                        out.println( instructions );
                        out.println( "<li>" );
                        out.println( "<select name=\"" + fileFormat + "\" id=\"" + fileFormat + "\">" );
                        for ( OntologyTerm ot : ontologyTerms ) {
                            String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                            if ( info.getFileFormat() != null &&
                                    info.getFileFormat().equals( ot.getEndurant().getIdentifier() ) ) {
                                inputAlreadyPresent = true;
                                inputStartValue += " selected=\"selected\"";
                            }
                            out.println( inputStartValue + ">" + ot.getTerm() + "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br/>" );
                        out.println( "</li>" );
                    }
                }

                // only allow one dummy external data per experiment, for now only!
                break;
            }

        }

        out.println( "</ol>" );
        out.println( "</fieldset>" );

        // sometimes, we may get GenericParameters from Dummy GPAs associated with the experiment. This is how we
        // differentiate GenericParameters that are not meant to be changed (== no Dummy GPA) from those that are
        // meant to be changed (== has dummy GPA). Search the GPAs
        // for dummies named with the addition of "XXX Dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        //
        // Only currently valid for AtomicValues.
        for ( Object gpaObj : validUser.getReService().getAllLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) validUser.getReService()
                    .greedyGet( gpaObj );
//            out.println(
//                    "Comparing current GenericProtocolApplication name (" +
//                            genericProtocolApplication.getName().trim() +
//                            ") with info.getChosenChildProtocolName() (" + info.getChosenChildProtocolName() +
//                            ")<br/>" );
            if ( genericProtocolApplication.getName().trim().contains( chosenChildProtocolName ) ) {
                out.println( "<fieldset>" );
                // make a shorter version of the chosen child protocol name
                String shortProtocolName = chosenChildProtocolName;
                if ( chosenChildProtocolName.contains( "(Component of" ) ) {
                    shortProtocolName = chosenChildProtocolName.substring( 0, chosenChildProtocolName.indexOf( "(Component of" ) );
                }
                out.println( "<legend>Further Details of the " + shortProtocolName + "</legend>" );
                out.println( "<ol>" );
                String gpaParentEndurantId = genericProtocolApplication.getGenericProtocol().getEndurant().getIdentifier();

                // Firstly, the template might contain descriptions beginning with "TextBox::". In this case,
                // the template is requesting that a text box be provided with the instructions given after the
                // "TextBox::" phrase. The contents of the text box will be stored in the same location as the
                // instructions in the template.
                // todo deal with the alreadypresent multiple TextBox instances (currently only deals with one)
                for ( Object descriptionObj : genericProtocolApplication.getDescriptions() ) {
                    Description description = ( Description ) descriptionObj;
                    if ( description.getText() != null && description.getText().length() > 0 &&
                            description.getText().startsWith( "TextBox::" ) ) {
                        String[] parsedStrings = description.getText().split( "::" );
                        String descriptionLabel = "GPA" + parsedStrings[0] + "::" + gpaParentEndurantId + "::" + iii;
                        out.println( "<li>" );
                        out.println(
                                "<label for=\"" + descriptionLabel + "\">" + parsedStrings[1] + ": </label>" );
                        out.println( "<textarea id=\"" + descriptionLabel + "\" name=\"" + descriptionLabel + "\"" );
                        out.println( "rows=\"5\" cols=\"40\">" );
                        if ( info.getGenericProtocolApplicationInfo() != null &&
                                info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ) != null ) {
                            inputAlreadyPresent = true;
                            out.println( info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ).getDescriptions().get( 0 ) );
                        }
                        out.println( "</textarea><br/>" );
                        out.println( "</li>" );
                    }
                }

                // Now provide the choices requested in the dummy GenericParameter. Currently, only an AtomicValue
                // with a fixed unit type is allowed.
                for ( Object obj2 : genericProtocolApplication.getParameterValues() ) {
                    if ( obj2 instanceof AtomicParameterValue ) {
                        GenericParameter genericParameter = ( GenericParameter ) ( ( AtomicParameterValue ) obj2 ).getParameter();
                        if ( genericParameter.getDefaultValue() instanceof AtomicValue ) {
                            // AtomicValues just have a string value. Ask the user for it

                            // retrieve the unit used, if present.
                            String unitName = "";
                            if ( genericParameter.getUnit() != null &&
                                    genericParameter.getUnit().getTerm().length() > 0 ) {
                                unitName = genericParameter.getUnit().getTerm();
                            }

                            String nameOfField = "atomicParameterOfGPA::" +
                                    gpaParentEndurantId + "::" +
                                    genericParameter.getEndurant().getIdentifier() + "::" + iii;
                            String nameOfParameter = "value"; // default
                            if ( genericParameter.getParameterType() != null ) {
                                nameOfParameter = genericParameter.getParameterType().getTerm();
                            } else
                            if ( genericParameter.getName() != null && genericParameter.getName().length() > 0 ) {
                                nameOfParameter = genericParameter.getName();
                            }
                            String instructions =
                                    "<label for=\"" + nameOfField + "\">Please fill in the " + nameOfParameter;
                            if ( unitName.length() > 0 ) {
                                instructions = instructions + " ( in " + unitName + ")";
                            }
                            instructions += ":</label>";

                            out.println( "<li>" );
                            out.println( instructions );
                            out.print( "<input id=\"" + nameOfField + "\" name=\"" + nameOfField + "\"" );
                            if ( info.getGenericProtocolApplicationInfo() != null &&
                                    info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId ) != null ) {
                                String inputValue = info.getGenericProtocolApplicationInfo().get( gpaParentEndurantId )
                                        .getParameterAndAtomics().get( genericParameter.getEndurant().getIdentifier() );
                                if ( inputValue != null && inputValue.length() > 0 ) {
                                    inputAlreadyPresent = true;
                                    out.print( " value=\"" + inputValue + "\"" );
                                }
                            }
                            out.println( "/><br/>" );
                            out.println( "<br/>" );
                            out.println( "</li>" );
                        }
                    }
                }
                out.println( "</ol>" );
                out.println( "</fieldset>" );
            }
        }
        out.println( "<br/>" );

        // we also allow developers choose various settings for their GenericEquipment associated with the experiment.
        // Any value stored in a GenericParameter is changeable. Any value in other references to ontology terms
        // (e.g. make, model, annotations) is considered constant and unchangeable.
        // Search the GenericEquipment for GenericParameters, and print out their options.
        GenericProtocol chosenProtocol = ( GenericProtocol ) validUser.getReService()
                .findLatestByEndurant( chosenChildProtocolIdentifier );
        chosenProtocol = ( GenericProtocol ) validUser.getReService().greedyGet( chosenProtocol );

        if ( chosenProtocol.getGenPrtclToEquip() != null ) {
            for ( Object obj : chosenProtocol.getGenPrtclToEquip() ) {
                GenericEquipment genericEquipment = ( GenericEquipment ) validUser.getReService().greedyGet( obj );

                // we're only interested in making more fields in the form if there are any GenericParameters here.
                if ( genericEquipment.getParameters() != null && !genericEquipment.getParameters().isEmpty() ) {
                    out.println( "<fieldset>" );
                    out.println( "<legend>" + genericEquipment.getName() + "</legend>" );
                    out.println( "<ol>" );

                    // Provide the name of the equipment as a hidden variable
                    out.println(
                            "<input type=\"hidden\" name=\"equipmentName::" +
                                    genericEquipment.getEndurant().getIdentifier() + "::" + iii + "\" value=\"" +
                                    genericEquipment.getName() + "\"/><br/>" );

                    // Now allow a free-text description of the Equipment, to be added in the final stages to the
                    // EquipmentApplication element of this protocol's GPA.
                    String nameOfDescriptionField =
                            "equipmentDescription::" + genericEquipment.getEndurant().getIdentifier() + "::" + iii;
                    out.println( "<li>" );
                    out.println(
                            "<label for=\"" + nameOfDescriptionField + "\">Description of the " +
                                    genericEquipment.getName() + ":</label>" );
                    out.println(
                            "<textarea id=\"" + nameOfDescriptionField + "\" name=\"" + nameOfDescriptionField +
                                    "\" rows=\"5\" cols=\"40\">" );

                    if ( info.getGenericEquipmentInfo().get( genericEquipment.getEndurant().getIdentifier() ) != null ) {
                        String inputValue = info.getGenericEquipmentInfo()
                                .get( genericEquipment.getEndurant().getIdentifier() ).getFreeTextDescription();
                        if ( inputValue != null && inputValue.length() > 0 ) {
                            inputAlreadyPresent = true;
                            out.println( inputValue );
                        }
                    }
                    out.println( "</textarea><br/>" );
                    out.println( "</li>" );

                    // Now, retrieve all parameters (currently only valid for ComplexValue and AtomicValue).
                    for ( Object paramObj : genericEquipment.getParameters() ) {
                        if ( paramObj instanceof GenericParameter ) {
                            GenericParameter genericParameter = ( GenericParameter ) paramObj;
                            if ( genericParameter.getDefaultValue() instanceof ComplexValue ) {
                                // ComplexValue objects reference an OntologyTerm,
                                //  which in turn is associated with an OntologySource. Instead of displaying just the referenced
                                // OntologyTerm, a pull-down menu should be displayed of ALL of the OntologyTerms in the database
                                // associated with that OntologySource.
                                ComplexValue complexValue = ( ComplexValue ) ( ( GenericParameter ) paramObj ).getDefaultValue();
                                out.println( "<br/>" );

                                List genericList = validUser.getReService()
                                        .getAllLatestTermsWithSource(
                                                complexValue.get_defaultValue()
                                                        .getOntologySource()
                                                        .getEndurant().getIdentifier() );
                                List<OntologyTerm> ontologyTerms = genericList;
                                if ( !ontologyTerms.isEmpty() ) {

                                    // the ontology source for this complex value may have a description which tells the user
                                    // how to select the correct ontology term. Check for that.
                                    OntologySource ontologySource = complexValue.get_defaultValue().getOntologySource();
                                    ontologySource = ( OntologySource ) validUser.getReService()
                                            .findLatestByEndurant( ontologySource.getEndurant().getIdentifier() );

                                    String nameOfField = "parameterOfEquipment::" +
                                            genericEquipment.getEndurant().getIdentifier() + "::" +
                                            genericParameter.getEndurant().getIdentifier() + "::" + iii;
                                    String nameOfParameter = "term"; // default
                                    if ( genericParameter.getParameterType() != null ) {
                                        nameOfParameter = genericParameter.getParameterType().getTerm();
                                    } else if ( genericParameter.getName() != null &&
                                            genericParameter.getName().length() > 0 ) {
                                        nameOfParameter = genericParameter.getName();
                                    }

                                    String instructions =
                                            "<label for=\"" + nameOfField + "\">Please select your " + nameOfParameter +
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

                                    out.println( "<li>" );
                                    out.println( instructions );
                                    out.println( "<select id=\"" + nameOfField + "\" name=\"" + nameOfField + "\">" );
                                    for ( OntologyTerm ot : ontologyTerms ) {
                                        String inputStartValue = "<option value= \"" + ot.getEndurant().getIdentifier() + "\"";
                                        if ( info.getGenericEquipmentInfo() != null &&
                                                info.getGenericEquipmentInfo()
                                                        .get( genericEquipment.getEndurant().getIdentifier() ) != null ) {
                                            String inputValue = info.getGenericEquipmentInfo().get( genericEquipment.getEndurant().getIdentifier() ).
                                                    getParameterAndTerms().get( genericParameter.getEndurant().getIdentifier() );
                                            if ( inputValue != null && inputValue.equals( ot.getEndurant().getIdentifier() ) ) {
                                                inputAlreadyPresent = true;
                                                inputStartValue += " selected=\"selected\"";
                                            }
                                        }
                                        out.println( inputStartValue + ">" + ot.getTerm() + "</option>" );
                                    }
                                    out.println( "</select>" );
                                    out.println( "<br/>" );
                                    out.println( "</li>" );
                                }
                            }
                            if ( genericParameter.getDefaultValue() instanceof AtomicValue ) {
                                // AtomicValues just have a string value. Ask the user for it
                                out.println( "<br/>" );

                                // retrieve the unit used, if present.
                                String unitName = "";
                                if ( genericParameter.getUnit() != null &&
                                        genericParameter.getUnit().getTerm().length() > 0 ) {
                                    unitName = genericParameter.getUnit().getTerm();
                                }

                                String nameOfField = "atomicParameterOfEquipment::" +
                                        genericEquipment.getEndurant().getIdentifier() + "::" +
                                        genericParameter.getEndurant().getIdentifier() + "::" + iii;
                                String nameOfParameter = "value"; // default
                                if ( genericParameter.getParameterType() != null ) {
                                    nameOfParameter = genericParameter.getParameterType().getTerm();
                                } else
                                if ( genericParameter.getName() != null && genericParameter.getName().length() > 0 ) {
                                    nameOfParameter = genericParameter.getName();
                                }
                                String instructions =
                                        "<label for=\"" + nameOfField + "\">Please fill in the " + nameOfParameter;
                                if ( unitName.length() > 0 ) {
                                    instructions = instructions + " ( in " + unitName + ")";
                                }
                                instructions += ":</label>";

                                out.println( "<li>" );
                                out.println( instructions );
                                out.print( "<input id=\"" + nameOfField + "\" name=\"" + nameOfField + "\"" );
                                if ( info.getGenericEquipmentInfo() != null &&
                                        info.getGenericEquipmentInfo()
                                                .get( genericEquipment.getEndurant().getIdentifier() ) != null ) {
                                    String inputValue = info.getGenericEquipmentInfo().get( genericEquipment.getEndurant().getIdentifier() )
                                            .getParameterAndAtomics().get( genericParameter.getEndurant().getIdentifier() );
                                    if ( inputValue != null ) {
                                        inputAlreadyPresent = true;
                                        out.print( " value=\"" + inputValue + "\"" );
                                    }
                                }
                                out.println( "/><br/>" );
                                out.println( "<br/>" );
                                out.println( "</li>" );
                            }
                        }
                    }
                    out.println( "</ol>" );
                    out.println( "</fieldset>" );
                }
            }
        }
    }

%>
<br/>
<fieldset class="submit">
    <%-- no need to check the go2confirm value, as the next step is the confirm page anyway --%>
    <% if ( inputAlreadyPresent ) { %>
    Return to the Confirmation Page by clicking on the "Submit" button below. <br/>
    <input type="submit" value="Submit"/>
    <% } else { // don't allow the use of the back button when changing metadata. %>
    <input type="submit" value="Submit"/>
    <input type="button" value="Back" onclick="history.go(-1)"/>
    <% } %>
</fieldset>
</form>

<p>If a required factor for your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>

<jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
