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
<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="fugeOM.Common.Audit.Person" %>
<%@ page import="fugeOM.Common.Description.Description" %>
<%@ page import="fugeOM.Common.Ontology.OntologySource" %>
<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanFuGEHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanProtocolCollectionHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.xml.XMLMarshaler" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.*" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
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

<form ENCTYPE="multipart/form-data" name="selectProt" action="metaDataValidate.jsp" method="post">
<%
    //        todo this entire page needs generification
    for ( int iii = 0; iii < investigationBean.getAllDataBeans().size(); iii++ ) {
        RawDataInfoBean info = investigationBean.getDataItem( iii );
        FileBean fileBean = investigationBean.getFileBean( iii );
        String selectDescName = "actionListDescription" + iii;

%>
<br/>
<hr/>
<h3>Information for <% out.print( info.getFriendlyId() ); %></h3>

<p>Original name: <% out.print( fileBean.getAFile().getName() ); %></p>
<label for="<% out.print(selectDescName); %>">Description of File <a href="help.jsp#cisbanIdentifiers"
                                                                     onClick="return popup(this, 'notes')">[ ?
    ]</a>:</label>
<textarea id="<% out.print(selectDescName); %>" name="<% out.print(selectDescName); %>"
          rows="5" cols="40"><% if ( info.getDataName() != null ) {
    out.println( info.getDataName() );
}%></textarea>
<br>

<%

        // sometimes, we may get GenericParameters from Dummy GPAs associated with the experiment. This is how we
        // differentiate GenericParameters that are not meant to be changed (== no Dummy GPA) from those that are
        // meant to be changed (== has dummy GPA). Search the GPAs
        // for dummies named with the addition of "XXX Dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        //
        // Only currently valid for AtomicValues.
        for ( Object obj : validUser.getReService().getAllLatestGenericProtocolApplications( true ) ) {
            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) validUser.getReService()
                    .greedyGet( obj );
//            out.println(
//                    "Comparing current GenericProtocolApplication name (" +
//                            genericProtocolApplication.getName().trim() +
//                            ") with info.getChosenChildProtocolName() (" + info.getChosenChildProtocolName() +
//                            ")<br/>" );
            if ( genericProtocolApplication.getName().trim().contains( info.getChosenChildProtocolName() ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Parameters Associated with Creating the Data File".
//                out.println( "Match Found<br/>" );
//                out.println( "Number of Parameters" + genericProtocolApplication.getParameterValues().size() + "<br/>" );
                // Now provide the choices requested in the dummy GenericParameter. Currently, only an AtomicValue
                // with a fixed unit type is allowed.
                for ( Object obj2 : genericProtocolApplication.getParameterValues() ) {
                    if ( obj2 instanceof AtomicParameterValue ) {
                        GenericParameter genericParameter = ( GenericParameter ) ( ( AtomicParameterValue ) obj2 ).getParameter();
                        if ( genericParameter.getDefaultValue() instanceof AtomicValue ) {
                            String displayName = genericParameter.getName().trim();

                            String value = "defaultGenericParameterValue" + iii;
                            String valueIdentifier = "defaultGenericParameterValueIdentifier" + iii;
                            String instructions =
                                    "<label for=\"" + value + "\">Please fill in the value of the " + displayName +
                                            ", in " +
                                            ( ( OntologyTerm ) validUser.getReService().findLatestByEndurant(
                                                    genericParameter.getUnit()
                                                            .getEndurant().getIdentifier() ) ).getTerm() +
                                            "</label>";
                            out.println( instructions );
                            out.println( "<input id=\"" + value + "\" name=\"" + value + "\"><br>" );
                            out.println(
                                    "<input type=\"hidden\" name=\"" + valueIdentifier + "\" value=\"" +
                                            genericParameter.getIdentifier() + "\"><br>" );

                            break; // todo only expects one generic parameter dummy per generic action.
                        }
                    }
                }
            }
        }

        out.println( "<br>" );
        out.println( "<br>" );

        // sometimes, we may get factors from Materials associated with the experiment. Search the materials
        // for dummies named with the addition of "XXX Dummy YYY SomeProtocol", where XXX and YYY may be anything, and
        // where SomeProtocol may ONLY be the exact, full protocol name of the GenericAction selected for this data file.
        for ( Object obj : validUser.getReService().getAllLatestGenericMaterials( true ) ) {
            GenericMaterial genericMaterial = ( GenericMaterial ) validUser.getReService().greedyGet( obj );
//            out.println(
//                    "Comparing current Material name (" + genericMaterial.getName().trim() +
//                            ") with info.getChosenChildProtocolName() (" + info.getChosenChildProtocolName() +
//                            ")<br/>" );
            if ( genericMaterial.getName().trim().contains( info.getChosenChildProtocolName() ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Material Characteristics".
//                out.println( "Match Found<br/>" );
                String displayName = genericMaterial.getName()
                        .substring( 0, genericMaterial.getName().indexOf( " Dummy" ) )
                        .trim();

                // will store the Material's name
                String matName = "materialName" + iii;
                out.println( "<br>" );
                out.println( "<label for=\"" + matName + "\">Name/ID of this Material (optional): </label>" );
                if ( investigationBean.getAllDataBeans() != null &&
                        investigationBean.getAllDataBeans().size() > iii &&
                        investigationBean.getDataItem( iii ).getMaterialFactorsBean() != null ) {
                    out.println(
                            "<input id=\"" + matName + "\" name=\"" + matName + "\" value=\"" +
                                    investigationBean.getDataItem( iii )
                                            .getMaterialFactorsBean()
                                            .getMaterialName() +
                                    "\"><br>" );
                } else {
                    out.println( "<input id=\"" + matName + "\" name=\"" + matName + "\"><br>" );
                }
                out.println( "<br>" );
                out.println(
                        "<p class=\"bigger\">Please enter some information about the " + displayName +
                                ", starting " +
                                "with treatment information. There should be a separate box for each treatment performed " +
                                "(optional). <em>NOTE: All treatments already entered will " +
                                "remain in the system. You may add additional treatments by entering them below.</em></p>" );

                if ( investigationBean.getAllDataBeans() != null &&
                        investigationBean.getAllDataBeans().size() > iii &&
                        investigationBean.getDataItem( iii ).getMaterialFactorsBean() != null &&
                        investigationBean.getDataItem( iii ).getMaterialFactorsBean().getTreatmentInfo() != null &&
                        !investigationBean.getDataItem( iii )
                                .getMaterialFactorsBean()
                                .getTreatmentInfo()
                                .isEmpty() ) {
                    out.println( "<ol>" );
                    for ( String singleTreatment : investigationBean.getDataItem( iii )
                            .getMaterialFactorsBean()
                            .getTreatmentInfo() ) {
                        out.println( "<li>Treatment already recorded: " + singleTreatment + "</li>" );
                    }
                    out.println( "</ol>" );
                }

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

                // Now, retrieve the materialType (singular) and all characteristics of this material.
                // Each references an OntologyTerm, which in turn is associated with an OntologySource.
                // Instead of displaying just the referenced OntologyTerm, a pull-down menu should be displayed
                // of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( genericMaterial.getMaterialType() != null ) {
                    out.println( "<br>" );
                    out.println( "<p class=\"bigger\">Next, please choose the correct Material Type.</p>" );
                    List genericList = validUser.getReService()
                            .getAllLatestTermsWithSource(
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

                        out.println( instructions );
                        out.println( "<select name=\"" + matType + "\">" );
                        for ( OntologyTerm ot : ontologyTerms ) {
                            out.println(
                                    "<option value= \"" + ot.getEndurant().getIdentifier() + "\">" + ot.getTerm() +
                                            "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br>" );
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
                        out.println(
                                "<label for=\"" + characteristicLabel +
                                        "\">Please select your " + ontologySource.getName() + "</label>" );
                        if ( multipleAllowed ) {
                            out.println( "<select multiple name=\"" + characteristicLabel + "\">" );
                        } else {
                            out.println( "<select name=\"" + characteristicLabel + "\">" );
                        }
                        for ( OntologyTerm ot : ontologyTerms ) {
                            out.println(
                                    "<option value= \"" + ot.getEndurant().getIdentifier() + "\">" + ot.getTerm() +
                                            "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br>" );
                    }
                    ontoCount++;
                }

                // only allow one dummy generic material per experiment, for now only!
                break;
            }
        }

        // we also allow developers to have a file format for their ExternalData associated with the experiment.
        // Search the ExternalData for dummies named with the name of the current experiment.
        for ( Object obj : validUser.getReService().getAllLatestExternalData() ) {
            ExternalData externalData = ( ExternalData ) validUser.getReService().greedyGet( obj );
            if ( externalData.getName().trim().contains( investigationBean.getTopLevelProtocolName().trim() ) &&
                    externalData.getName().trim().contains( "Dummy" ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "File Formats".
                String displayName = externalData.getName()
                        .substring( 0, externalData.getName().indexOf( " Dummy" ) )
                        .trim();

                // Now, retrieve the file format (singular). It references an OntologyTerm, which in turn is associated
                // with an OntologySource. Instead of displaying just the referenced OntologyTerm, a pull-down menu
                // should be displayed of ALL of the OntologyTerms in the database associated with that OntologySource.
                if ( externalData.getFileFormat() != null ) {
                    out.println( "<br>" );
                    out.println( "<p class=\"bigger\">Next, please choose the correct file format.</p>" );
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
                        out.println( "<select name=\"" + fileFormat + "\">" );
                        for ( OntologyTerm ot : ontologyTerms ) {
                            out.println(
                                    "<option value= \"" + ot.getEndurant().getIdentifier() + "\">" + ot.getTerm() +
                                            "</option>" );
                        }
                        out.println( "</select>" );
                        out.println( "<br>" );
                    }
                }

                // only allow one dummy external data per experiment, for now only!
                break;
            }

        }
    }

%>
<br>
<input type="submit" value="Submit" name="submit"/>
<input type="button" value="Back" onclick="history.go(-1)"/>
</form>
<br>

<p>If a required factor for your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>

<jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
