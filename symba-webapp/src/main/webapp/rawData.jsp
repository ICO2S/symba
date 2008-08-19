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

    <script language="javascript">
        var upload_number = 1;
    </script>

    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>

</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
<p>
    (1) Introduction -> (2) Attach to an Experiment -> <font class="blueText">(3) Upload Data</font> ->
    (4) Select Protocol -> (5) Confirm Your Submission -> (6) Completion and Download
</p>

<c:if test="${param.errorMsg != null}">
    <font color="red">
        <c:out value="${param.errorMsg}"/>
    </font>
    <br/>
    <br/>
</c:if>

<p class="bigger">All submitted files must be of the same investigation type and belong to the same experiment.
    Large data files may take some time to upload.</p>

<form ENCTYPE="multipart/form-data" action="rawDataVerify.jsp" method="post">

<fieldset>

    <%
        // If the user is trying to re-enter data at this stage when they have already parse assay protocols, we know
        // that they are not allowed to add / change any more files. Therefore we can skip all of this, and just
        // print a summary.
        // However, if they are trying to re-enter data and they haven't yet submitted ChooseAction.jsp, we can allow
        // them to choose different filenames.
        if ( symbaFormSessionBean.isProtocolLocked() ) {

            if ( symbaFormSessionBean.getTopLevelProtocolName() != null ) {

                // they have submitted at least as far as ChooseAction.jsp already. Do not change anything in the
                // session, and carry on to the next requested page.
                out.println( "<legend>Please Review Your Data for " + symbaFormSessionBean.getTopLevelProtocolName() +
                             "</legend>" );
            } else {
                out.println( "There has been an error processing your form. Please contact " );
                out.println( application.getAttribute( "helpEmail" ) + "<br/>" );
            }

            if ( symbaFormSessionBean.isDataPresent() ) {
                // only print out this section if there are some filenames already. The only instance where there
                // shouldn't be filenames is if they are coming direct from view.jsp with all other metadata filled in.
                boolean alreadyPrintedHeader = false;

                for ( int iii = 0;
                      iii < symbaFormSessionBean.getDatafileSpecificMetadataStores().size(); iii++ ) {
                    if ( symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename() != null &&
                         symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename().length() >
                         0 ) {
                        if ( !alreadyPrintedHeader ) {
                            out.println( "<p>" );
                            out.println( "You have chosen the following data to associate with this investigation:" );
                            out.println( "</p>" );
                            out.println( "<ol>" );
                            alreadyPrintedHeader = true;
                        }
                        out.println( "<li>" );
                        out.println( "<strong>" );
                        out.println(
                                symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename() );
                        out.println( "</strong>" );
                        out.println( "</li>" );
                    } else {
                        break;
                    }
                }
                if ( alreadyPrintedHeader ) {
                    out.println( "</ol>" );
                }

                out.println( "<p>" );
                out.println( "You can no longer change any of the values above. If you have made a mistake," );
                out.println( "and wish to start over again, please " );
                out.println( "<a href=\"beginNewSession.jsp\">start again from the beginning</a>.<br/>" );
                out.println( "</p>" );
            }
        }

        // Protocol isn't locked yet. If either the protocol isn't locked OR the protocol is locked but the data hasn't been
        // inputted yet, then the user can upload a file.
        if ( !symbaFormSessionBean.isProtocolLocked() ||
             ( symbaFormSessionBean.isProtocolLocked() && !symbaFormSessionBean.isDataPresent() ) ) {
    %>
    <legend>Please add your data files</legend>
    <ol>
        <li>
            <label for="attachment0">Your File: </label>
            <input type="file" name="attachment0" id="attachment0"
                   onchange="document.getElementById('moreUploadsLink').style.display = 'block';"/>
            <br>

            <div id="moreUploads"/>
            <br>

            <div id="moreUploadsLink" style="display:none;">
                <a href="javascript:addFileInput();">Attach Another File</a>
            </div>
            <br>
        </li>
    </ol>
    <%
        }
    %>

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
    <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I would like to
    continue on to the next form page</strong><br/>
    <%
        }
        out.println( "<input type=\"submit\" value=\"Submit\"/>" );
    %>
</fieldset>
</form>
<fieldset class="submit">
    <%
        /**
         There should be no usage of the back button as history.go(-1) as this could lead to the wrong page. Instead,
         it must go to the previous jsp as a submit, so that the session details can be checked. However, in this case,
         you can reach rawData from either newExperiment.jsp, experiment.jsp, or the view.jsp. In the case of the first
         (newExperiment.jsp), symbaFormSessionBean.getFuGE() will be null. In both of the second cases, it will not be
         null. Therefore you can set the value of the form action based on this.
         */
        if ( symbaFormSessionBean.getFugeIdentifier() == null ||
             symbaFormSessionBean.getFugeIdentifier().length() == 0 ) {
            out.println( "<form name=\"changeNewExperiment\" action=\"newExperiment.jsp\" method=\"post\">" );
        } else {
            out.println( "<form name=\"changeExistingExperiment\" action=\"experiment.jsp\" method=\"post\">" );
        }
    %>
    If you wish to go back to the previous form page, <strong>without saving the changes on this page</strong>,
    use the "Back" button. You will, in any case, have the chance to review what you've written in at the end of
    the form, before final submission.<br/>
    <input type="submit" value="Back"/>
    <% out.println( "</form>" ); %>
</fieldset>
<br>

<jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
