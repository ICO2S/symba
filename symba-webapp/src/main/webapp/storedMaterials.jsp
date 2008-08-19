<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt
--%>
<!-- $LastChangedDate: 2008-08-06 09:48:48 +0100 (Wed, 06 Aug 2008) $-->
<!-- $LastChangedRevision: 193 $-->
<!-- $Author: allysonlister $-->
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp/src/main/webapp/rawData.jsp $-->

<%@ page import="net.sourceforge.fuge.collection.FuGE" %>
<%@ page import="net.sourceforge.fuge.common.audit.Person" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologySource" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologyTerm" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocol" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocolApplication" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.helper.FuGEMappingHelper" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.xml.XMLMarshaler" %>
<%@ page import="net.sourceforge.symba.webapp.util.*" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.ActionTemplateParser" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialFormValidator" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialTemplateParser" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MetaDataWrapper" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.AssayLoader" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.LoadPerson" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.MaterialTransformationLoader" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.OntologyLoader" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.bind.JAXBException" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>


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

        // Get all currently-existing pairs. Allow the user to choose any of these pairs to copy
        // and modify. These pairs are identified by retrieving all non-dummy material transformation GPAs.
        @SuppressWarnings( "unchecked" )
        List<GenericProtocolApplication> materialTransformations = (List<GenericProtocolApplication>) validUser.getSymbaEntityService()
                .getLatestMaterialTransformations();

        if ( !materialTransformations.isEmpty() ) {

            out.println( "<fieldset>" );
            out.println( "<legend>Create a specimen based on existing specimens</legend>" );

            out.println( "<p>Below are a list of currently existing specimens in the database." );
            out.println( "If what you need isn't on the list below, click on the one nearest to your" );
            out.println( "needs, and you will be able to modify it." );
            out.println( "</p>" );
            out.println( "<p>" );
            out.println( "If all of the specimens you require to describe a particular assay" );
            out.println( "are present below, then please move " );
            // using newOrExisting won't delete any session variables, as linking to beginNewSession would.
            if ( symbaFormSessionBean.getFugeIdentifier() == null ||
                 symbaFormSessionBean.getFugeIdentifier().length() == 0 ) {
                out.println( "<a href=\"newExperiment.jsp\">on to describing that assay in SyMBA</a>" );
            } else {
                out.println( "<a href=\"experiment.jsp\">back to describing that assay in SyMBA</a>" );
            }
            out.println( "</p>" );
            // Print out those pairs with the clickable option, which is sent on to the form handler
            out.println( ActionTemplateParser.parseMaterialTransformationActions( materialTransformations,
                    validUser.getSymbaEntityService(), symbaFormSessionBean,
                    ActionTemplateParser.PROTOCOL_TYPE.MATERIAL_TRANSFORMATION ) );
            out.println( "</fieldset>" );
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
            <input type="submit" value="Submit" onclick="disabled=true"/>
        </fieldset>
    </form>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
