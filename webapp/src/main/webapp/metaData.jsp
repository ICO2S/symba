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

<%@ page import="fugeOM.Bio.Material.GenericMaterial" %>
<%@ page import="fugeOM.Common.Description.Description" %>
<%@ page import="fugeOM.Common.Ontology.OntologySource" %>
<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.GenericAction" %>
<%@ page import="fugeOM.Common.Protocol.GenericProtocol" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.RawDataInfoBean" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.FileBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<jsp:useBean id="rdb" class="uk.ac.cisban.symba.webapp.util.RawDataBean" scope="session">
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
    for ( int iii = 0; iii < rdb.getAllDataBeans().size(); iii++ ) {
        RawDataInfoBean info = rdb.getDataItem( iii );
        FileBean fileBean = rdb.getFileBean( iii );
        String selectDescName = "actionListDescription" + iii;
        String selectFactorName = "actionListFactor" + iii;
        String selectName = "actionList" + iii;

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
    List<String> allFulls = new ArrayList<String>();
    // Print out any protocol that is a component of a larger protocol, and can therefore
    // have output/input files.
    boolean hasOne = false;
    for ( Object obj : validUser.getReService().getAllLatestGenericProtocols() ) {
        GenericProtocol gp = ( GenericProtocol ) obj;
//        out.println( "<p> Got gp " + gp.getName() + "</p>" );
        Set aSet = ( Set ) gp.getGenericActions();
        for ( int count = 1; count <= aSet.size(); count++ ) {
            for ( Object obj2 : aSet ) {
                GenericAction ga = ( GenericAction ) obj2;
                if ( count == ga.getActionOrdinal() ) {
//                    out.println( "<p> Getting ga " + ga.getName() + "</p>" );
                    if ( gp.getName().trim().equals( rdb.getDataType().trim() ) ) {
                        String modified = ga.getName();
                        modified.trim();
                        if ( modified.startsWith( "Step Containing the" ) ) {
                            modified = modified.substring( 20 );
                        }
                        allFulls.add(
                                "<option value= \"" + ga.getEndurant().getIdentifier() + "\">" +
                                        modified +
                                        "</option>" );
                    } else if ( gp.getName().trim().contains( "Component of " + rdb.getDataType().trim() ) ) {
                        if ( !hasOne ) {
%>
<label for="<% out.print(selectFactorName); %>">Please select your factor:</label>
<select name="<% out.print(selectFactorName); %>">
    <%
                                hasOne = true;
                            }
                            out.println(
                                    "<option value= \"" + ga.getEndurant().getIdentifier() + "\">" +
                                            ga.getName() +
                                            "</option>" );
                        }
                        break;
                    }
                }
            }
        }
        if ( hasOne ) {
    %>
</select>
<%
    }
%>
<br>
<%
    boolean hasLabel = false;
    for ( String s : allFulls ) {
        if ( !hasLabel ) {
%>
<label for="<% out.print(selectName); %>">Please select your factor:</label>
<select name="<% out.print(selectName); %>">

    <%
                hasLabel = true;
            }
            out.println( s );
        }
        if ( hasLabel ) {
    %>
</select>
<%
        } else {
            out.println(
                    "<p class=\"bigger\">There has been an error listing all of the factors important to your data " +
                            "file. Please contact the <a href=\"mailto:helpdesk@cisban.ac.uk\">helpdesk</a>, " +
                            "letting us know what workflow you were trying to associate with your data file</p>" );
        }

        // sometimes, we may get factors from Materials associated with the experiment. Search the materials
        // for dummies named with the name of the current experiment.
        for ( Object obj : validUser.getReService().getAllLatestGenericMaterials() ) {
            GenericMaterial genericMaterial = ( GenericMaterial ) validUser.getReService().greedyGet( obj );
            if ( genericMaterial.getName().trim().contains( rdb.getDataType().trim() ) &&
                    genericMaterial.getName().trim().contains( "Dummy" ) ) {
                // The displayName is just the name for this group of questions we are about to give to the user.
                // Should be something like "Material Characteristics".
                String displayName = genericMaterial.getName()
                        .substring( 0, genericMaterial.getName().indexOf( " Dummy" ) )
                        .trim();

                // will store the Material's name
                String matName = "materialName" + iii;
                out.println( "<br>" );
                out.println( "<label for=\"" + matName + "\">Name/ID of this Material (optional): </label>" );
                if ( rdb.getAllDataBeans() != null && rdb.getAllDataBeans().size() > iii &&
                        rdb.getDataItem( iii ).getMaterialFactorsBean() != null ) {
                    out.println(
                            "<input id=\"" + matName + "\" name=\"" + matName + "\" value=\"" +
                                    rdb.getDataItem( iii ).getMaterialFactorsBean().getMaterialName() + "\"><br>" );
                } else {
                    out.println( "<input id=\"" + matName + "\" name=\"" + matName + "\"><br>" );
                }
                out.println( "<br>" );
                out.println(
                        "<p class=\"bigger\">Please enter some information about the " + displayName + ", starting " +
                                "with treatment information. There should be a separate box for each treatment performed " +
                                "(optional). <em>NOTE: All treatments already entered will " +
                                "remain in the system. You may add additional treatments by entering them below.</em></p>" );

                if ( rdb.getAllDataBeans() != null && rdb.getAllDataBeans().size() > iii &&
                        rdb.getDataItem( iii ).getMaterialFactorsBean() != null &&
                        rdb.getDataItem( iii ).getMaterialFactorsBean().getTreatmentInfo() != null &&
                        !rdb.getDataItem( iii ).getMaterialFactorsBean().getTreatmentInfo().isEmpty() ) {
                    out.println( "<ol>" );
                    for ( String singleTreatment : rdb.getDataItem( iii )
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
                        String instructions =  "<label for=\"" + matType + "\">Please select your material type:</label>" ;
                        for ( Object descObj : ontologySource.getDescriptions() ) {
                            Description desc = ( Description ) descObj;
                            if ( desc.getText().startsWith( "Instructions: " ) ) {
                                instructions =  "<label for=\"" + matType + "\">" + desc.getText().substring( 14 ) + "</label>";
                                foundInstructions = true;
                            }
                        }

                        out.println(instructions);
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

                // only allow one generic material per experiment, for now only!
                break;
            }

        }
    }

%>
<br>
<input type="submit" value="Submit" name="submit"></input>
<input type="button" value="Back" onclick="history.go(-1)"></input>
</form>
<br>

<p>If a required factor for your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>

<jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
