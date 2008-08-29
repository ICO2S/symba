<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt

The user has already selected an investigation. Check that investigation for material transformations and assays.
If both are present, allow the user to choose at this stage which protocol they are interested in (the form
branches at this stage). If there is only one or the other type, automatically divert to that branch.
--%>
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<%@ page import="net.sourceforge.symba.webapp.util.forms.ActionTemplateParser" %>


<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>

    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>

</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <p>
        (1) Introduction -> <font class="blueText">(2) Attach to an Experiment</font> -> (3) Create Specimens
        -> (4) Upload Data -> (5) Select Protocol -> (6) Confirm Your Submission -> (7) Completion
    </p>

    <form action="materialTransformationOrAssayValidate.jsp" method="post">

        <%
            // Immediately redirect if the protocol is locked due to copying from another experiment or due to
            // the user progressing too far to change this value.
            if ( symbaFormSessionBean.isProtocolLocked() ) {

                if ( symbaFormSessionBean.isMetadataFromAnotherExperiment() ) {
                    // if we have metadata from another experiment, we can immediately redirect
                    // to the next page along, after setting the template store appropriately.
                    if ( session.getAttribute( "templateStore" ) == null ) {
                        if ( !symbaFormSessionBean.getDatafileSpecificMetadataStores().isEmpty() ) {
                            session.setAttribute( "templateStore",
                                    symbaFormSessionBean.getDatafileSpecificMetadataStores().get( 0 ) );
        %>
        <c:redirect url="rawData.jsp"/>
        <%

                    }
                }
            }

            // Otherwise, irrespective of if the metadata is from another experiment, redirect if
            // the user has already made their decision as to what sort of protocol type to use, and they're
            // just reviewing their choices.
            if ( !symbaFormSessionBean.getDatafileSpecificMetadataStores().isEmpty() ) {
        %>
        <c:redirect url="rawData.jsp"/>
        <%
                }
            }
        %>
        <fieldset>
            <%
                // finally, if they haven't put anything into the session yet, we ask them which protocol type they want
                // if both are available. Otherwise, we automatically redirect to the type of protocol that is
                // available.
                ActionTemplateParser.PROTOCOL_TYPE protocolTypePresent =
                        ActionTemplateParser.hasMaterialTransformation(
                                validUser.getSymbaEntityService(), symbaFormSessionBean.getTopLevelProtocolName() );

                symbaFormSessionBean.setProtocolType( protocolTypePresent );

                if ( protocolTypePresent == ActionTemplateParser.PROTOCOL_TYPE.ALL ) {
                    out.println( "<legend>Type of Protocol</legend>" );
                    out.println( "<ol>" );
                    out.println( "<li>" );
                    String listLabel = "protocolType";
                    out.println( "<label for=\"" + listLabel + "\">" );
                    out.println( "Please choose the type of information you wish to provide:" );
                    out.println( "</label>" );
                    out.println( "<select id=\"" + listLabel + "\" name=\"" + listLabel + "\">" );
                    out.println( "<option value=\"materialTransformation\">Specimen or Material Measurement</option>" );
                    out.println( "<option value=\"assay\">Assay / Datafile Creation</option>" );
                    out.println( "</select>" );
                    out.println( "</li>" );
                    out.println( "</ol>" );
                } else if ( protocolTypePresent == ActionTemplateParser.PROTOCOL_TYPE
                        .MATERIAL_TRANSFORMATION ) {
            %>
            <c:redirect url="storedMaterials.jsp"/>
            <%
            } else if ( protocolTypePresent == ActionTemplateParser.PROTOCOL_TYPE.ASSAY ) {
            %>
            <c:redirect url="rawData.jsp"/>
            <%
                } else {
                    out.println(
                            "Your chosen investigation seems to have none of the recognized protocol types. Please" );
                    out.println( "<a href=\"mailto:" + application.getAttribute( "helpEmail" ) + "\">contact us</a>" );
                }
            %>

        </fieldset>

        <fieldset class="submit">

            <%
                // we only allow the user to see this page when there has been no decision made the first time through.
                // Otherwise, all other choices could be invalidated.
                out.println( "<input type=\"submit\" value=\"Submit\" onclick=\"disabled=true\" />" );
            %>
        </fieldset>
    </form>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
