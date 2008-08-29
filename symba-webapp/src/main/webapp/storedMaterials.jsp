<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt
--%>
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocolApplication" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.ActionTemplateParser" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialTemplateParser" %>
<%@ page import="java.util.List" %>

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
        (1) Introduction -> (2) Attach to an Experiment -> <font class="blueText">(3) Create Specimens</font>
        -> (4) Upload Data -> (5) Select Protocol -> (6) Confirm Your Submission -> (7) Completion
    </p>

    <p class="bigger">All submitted materials / specimens must be of the same investigation type and belong to the same
        experiment (the one you selected at the beginning of the form).</p>


    <%
        // The users may choose from among the pre-existing starting materials, or make their own.
        // Pre-existing starting materials are those that are not dummies, and which exactly match the
        // pattern described in the dummy materials.
        // The pre-existing values will be submitted using a separate submit button as the radio buttons
        // are kept separate.

        // Get all currently-existing pairs. Allow the user to choose any of these pairs to copy
        // and modify. These pairs are identified by retrieving all non-dummy material transformation GPAs.
        @SuppressWarnings( "unchecked" )
        List<GenericProtocolApplication> materialTransformations =
                ( List<GenericProtocolApplication> ) validUser.getSymbaEntityService()
                        .getLatestMaterialTransformations();

        if ( !materialTransformations.isEmpty() ) {

            // Print out those pairs with the clickable option, which is sent on to the form handler
            out.println( ActionTemplateParser.parseMaterialTransformationActions( materialTransformations,
                    validUser.getSymbaEntityService(), symbaFormSessionBean,
                    ActionTemplateParser.PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) );
        }
        // Get a list of current starting materials, to always provide the option of starting from scratch.
    %>

    <form action="enterSpecimen.jsp" method="post">
        <fieldset>
            <legend>Create a new specimen</legend>
            <p>You may create a brand-new specimen using one of the choices below.</p>
            <%
                out.println( ActionTemplateParser.parseMaterialTransformationDummyActions(
                        MaterialTemplateParser.getDummyBaseMaterials(
                                validUser.getSymbaEntityService(), symbaFormSessionBean.getTopLevelProtocolName() ),
                        validUser.getSymbaEntityService(), symbaFormSessionBean,
                        ActionTemplateParser.PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) );
            %>
        </fieldset>
        <fieldset class="submit">
            <input type="submit" value="Create New" onclick="disabled=true"/>
        </fieldset>
    </form>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
