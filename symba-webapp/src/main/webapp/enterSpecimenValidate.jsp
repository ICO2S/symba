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
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/symba-webapp/src/main/webapp/metaDataValidate.jsp $-->


<!-- This include will validate the user -->

<jsp:include page="checkUser.jsp"/>

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
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>
<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>
<%
    boolean automaticReturnToMetaData = false;
    //First check whether the user only wants to get a new Term to be loaded to the database.
    // In this case enter loead new Term to Database, and return in the end back to metadata.jsp
    // instead of confirm.jsp (like if the user had chosen to review its data):

    // The session-parameter for the new term should then not be handled prior to the automatic redirection to the 
    // data-entry-page. This can be done in validateLoadRequest, but we need that method to return the session bean
    String toBeIgnoredParameterName = "";
    if ( request.getParameter( "hiddennewterminfofield" ) != null &&
         request.getParameter( "hiddennewterminfofield" ).length() > 0 ) {
        String[] tmpArr = request.getParameter( "hiddennewterminfofield" ).split( ":::" );
        toBeIgnoredParameterName = tmpArr[2];
    }
    symbaFormSessionBean = OntologyLoader.validateLoadRequest( request, validUser, symbaFormSessionBean, false );
    if ( toBeIgnoredParameterName.length() > 0 ) {
        automaticReturnToMetaData = true; //will be checked at the end of this class
    }

    // store all material parameters
    symbaFormSessionBean =
            MaterialFormValidator.validate( request, symbaFormSessionBean, toBeIgnoredParameterName, false );

    if ( automaticReturnToMetaData ) {
        ActionHierarchyScheme ahs = new ActionHierarchyScheme();
        if ( request.getParameter( ahs.getElementTitle() ) != null ) {
            response.sendRedirect(
                    "enterSpecimen.jsp?" + ahs.getElementTitle() + "=" + request.getParameter( ahs.getElementTitle() ) );
            return;
        } else {
            ahs.setDummy( true );
            if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                response.sendRedirect(
                        "enterSpecimen.jsp?" + ahs.getElementTitle() + "=" +
                        request.getParameter( ahs.getElementTitle() ) );
                return;
            }

            response.sendRedirect( "enterSpecimen.jsp" ); // there should always be one of the two above parameters
            return;
        }
    }
%>
<c:redirect url="confirmSpecimen.jsp"/>
