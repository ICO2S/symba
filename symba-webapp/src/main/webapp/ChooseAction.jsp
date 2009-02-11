<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="net.sourceforge.fuge.bio.material.Material" %>
<%@ page import="net.sourceforge.fuge.collection.FuGE" %>
<%@ page import="net.sourceforge.fuge.common.audit.Person" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologySource" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologyTerm" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocol" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocolApplication" %>
<%@ page import="net.sourceforge.fuge.common.protocol.Protocol" %>
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


    <!-- We have chosen to separate the selection of a factor (aka generic action of the top-level protocol)
        so that the appropriate action can be taken on the next page if there is a dummy GenericParameter
        in the generic action chosen on this page.
    -->

    <form name="selectProtocol" action="ChooseActionValidate.jsp" method="post">

        <fieldset>
            <legend>Please select the factors appropriate to your data:
                <a href="help.jsp#protocol" onClick="return popup(this, 'notes')">[ Help ]</a></legend>

            <ol>
                <%

                    // if there is a template store, then parse session variables FOR THIS PAGE ONLY for the ones that
                    // are only in the templateStore right now (for each data file). The rest will get filled normally
                    // in metaDataValidate.jsp
                    // todo

                    out.println(
                            ActionTemplateParser.parseAssayActions( validUser.getEntityService(), validUser.getSymbaEntityService(),
                                    symbaFormSessionBean,
                                    ActionTemplateParser.PROTOCOL_TYPE.ASSAY ) );
                %>
                <br/>

            </ol>
        </fieldset>

        <fieldset class="submit">
            <!--ONCLICK="disabled=true"-->
            <%
                // in this case, we only want to present them with this option if the have reached the
                // confirmation page. Otherwise, we want to force them to move forward linearly.
                // Presence of all data is shown with the variable symbaFormSessionBean.get
                if ( symbaFormSessionBean.isConfirmationReached() ) { %>
            Do you wish to make changes to the information associated with the above data? <br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="true" checked="checked"/> <strong>I am
            happy with the entire submission: return to the Confirmation Page</strong><br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I would like to continue on
            to
            the next form page</strong><br/>
            <%
                }
//        out.println( "<input type=\"submit\" value=\"Submit\" onclick=\"this.disabled='disabled'\"/>" );
                out.println( "<input type=\"submit\" value=\"Submit\"/>" );
            %>
        </fieldset>
    </form>
    <fieldset class="submit">
        <%--
         There should be no usage of the back button as history.go(-1) as this could lead to the wrong page. Instead,
         it must go to the previous jsp as a submit, so that the session details can be checked.
        --%>
        <form name="lookAtRawData" action="rawData.jsp" method="post">
            If you wish to go back to the previous form page, <strong>without saving the changes on this page</strong>,
            use the "Back" button. You will, in any case, have the chance to review what you've written in at the end of
            the form, before final submission.<br/>
            <input type="submit" value="Back" />
        </form>
    </fieldset>
    <br/>
    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>