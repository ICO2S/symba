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
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp/src/main/webapp/metaData.jsp $-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<!-- The title, stylesheet, help popup function, and meta tags -->
<jsp:include page="metas.html"/>
<head>
</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> <font class="blueText">(3) Create Specimens</font>
        -> (4) Upload Data -> (5) Select Protocol -> (6) Confirm Your Submission -> (7) Completion
    </p>

    <h3>Please fill in the details of your specimen</h3>

    <form name="selectMetadata" action="enterSpecimenValidate.jsp" method="post">
        <fieldset>
            <legend>New Specimen</legend>

            <%
                ActionHierarchyScheme ahs = new ActionHierarchyScheme();

                if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                    // base the form on the existing specimen
                    out.println( MaterialTemplateParser.createSpecimenFormContents( validUser,
                            symbaFormSessionBean.getSpecimenToBeUploaded(),
                            request.getParameter( ahs.getElementTitle() ),
                            false ) );
                } else {
                    ahs.setDummy( true );
                    if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                        // base the form on the existing specimen
                        out.println( MaterialTemplateParser.createSpecimenFormContents( validUser,
                                symbaFormSessionBean.getSpecimenToBeUploaded(),
                                request.getParameter( ahs.getElementTitle() ), true ) );
                    }
                }
            %>
        </fieldset>
        <br/>
        <fieldset class="submit">
            <input type="submit" value="Submit" onclick="disabled=true"/>
        </fieldset>
    </form>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
