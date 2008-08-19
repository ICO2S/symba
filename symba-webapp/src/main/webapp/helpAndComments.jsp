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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<p class="centered">
    <a href="home.jsp">Return Home</a> :: <a href="help.jsp#intro"
                                             onClick="return popup(this, 'notes')">View Help</a><br>
    You are currently logged in as ${fn:escapeXml(validUser.userName)}<br>
    If you have any questions or comments, please
    <%
        out.println("<a href=\"mailto:" + application.getAttribute( "helpEmail" ) + "\">contact us</a>");
    %>
</p>

<div id="Logo" align=center><a href="http://www.cisban.ac.uk"><IMG SRC="pics/logo-small.jpg" border="0"></a>
</div>
