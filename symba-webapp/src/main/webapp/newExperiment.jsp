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
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- The session is or isn't cleared (depending on the route the user has taken to get here)
 directly prior to the loading of this page --%>
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

    <h2>New Experiment <a href="help.jsp#newExperiment"
                          onClick="return popup(this, 'notes')"> [ What Is Classed As An Experiment? ]</a></h2>

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <form name="metaForm" action="newExperimentValidate.jsp" method="post">

        <fieldset>
            <legend>Please Name Your Experiment</legend>

            <ol>

                <li>
                    <label for="experimentName">Experiment Name:<em>required</em></label>
                    <input id="experimentName"
                           name="experimentName"
                    <%
                        if ( symbaFormSessionBean.getExperimentName() != null &&
                                symbaFormSessionBean.getExperimentName().length() > 0 ) {
                            out.println(
                                    "value=\"" + symbaFormSessionBean.getExperimentName() + "\">" );
                        } else {
                            out.println( ">" );
                        }
                    %>
                    <br>
                </li>

                <li>
                    <label for="hypothesis">Hypothesis:</label>
                    <textarea id="hypothesis" rows="5" cols="40" name="hypothesis"
                    <%
                        if ( symbaFormSessionBean.getHypothesis() != null &&
                                symbaFormSessionBean.getHypothesis().length() > 0 ) {
                            // putting the brackets here means non-essential whitespace is not shown
                            out.println( ">" + symbaFormSessionBean.getHypothesis() );
                        } else {
                            out.println( ">" );
                        }
                        out.println( "</textarea>" );
                    %>
                    <br>
                </li>

                <li>
                    <label for="conclusion">Conclusions:</label>
                    <textarea id="conclusion" rows="5" cols="40" name="conclusion"
                    <%
                        if ( symbaFormSessionBean.getConclusion() != null &&
                                symbaFormSessionBean.getConclusion().length() > 0 ) {
                            // putting the brackets here means non-essential whitespace is not shown
                            out.println(
                                    ">" + symbaFormSessionBean.getConclusion() + "</textarea>" );
                        } else {
                            out.println( "></textarea>" );
                        }
                    %>
                    <br>
                </li>
            </ol>
        </fieldset>

        <fieldset class="submit">
            <%
                // in this case, we only want to present them with this option if the have reached the
                // confirmation page. Otherwise, we want to force them to move forward linearly.
                // Presence of all data is shown with the variable symbaFormSessionBean.get
                if ( symbaFormSessionBean.isConfirmationReached() ) { %>
            Would you like to change more about your experiment, or go straight back to the
            confirmation page? Just make your choice and hit "Submit". <br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="true" checked="checked"/> <strong>I'm
            finished making changes: go back to the Confirmation Page</strong><br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I'd like to make more
            changes: continue on to the next form page</strong><br/>
            <% } // don't allow the use of the back button in the first step. %>
            <input type="submit" value="Submit"/>
        </fieldset>
    </form>
    <br>

    <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="file" name="afile"/>
    <br><br>

    <input type="submit" value = "Submit"/>
    --%>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
