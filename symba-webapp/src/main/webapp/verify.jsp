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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%--Import the ResourceBundle class so that we can load *.properties files --%>
<%@ page import="net.sourceforge.fuge.collection.FuGE" %>

<%--Imports so we can use the person object and the data portal utils --%>
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

<%-- Remove the validUser session bean, if any --%>
<c:remove var="validUser"/>

<%-- Load application resource bundle --%>
<%!
    ResourceBundle bundle = null;

    public void jspInit() {
        bundle = ResourceBundle.getBundle( "symba" );
    }

%>

<%--
  See if the user name and password combination is valid. If not,
  redirect back to the login page with a message.
--%>
<c:if test="${empty param.userName || empty param.password}">
    <c:redirect url="login.jsp">
        <c:param name="errorMsg"
                 value="You must enter a User Name and Password."/>
    </c:redirect>
</c:if>

<%-- This allows the page to talk to a database --%>
<sql:setDataSource
        driver="<%=bundle.getString(\"net.sourceforge.symba.webapp.security.driver\")%>"
        url="<%=bundle.getString(\"net.sourceforge.symba.webapp.security.url\")%>"
        user="<%=bundle.getString(\"net.sourceforge.symba.webapp.security.username\")%>"
        password="<%=bundle.getString(\"net.sourceforge.symba.webapp.security.password\")%>"
        />

<%--This searches the database for the username/password combination entered
using the parameter beans  --%>
<sql:query var="empInfo">
    SELECT * FROM users
    WHERE user_name = ? AND password = ?
    <sql:param value="${param.userName}"/>
    <sql:param value="${param.password}"/>
</sql:query>

<%-- If no rows are returned then the password is incorrect and you
must go back to the login page--%>
<c:if test="${empInfo.rowCount == 0}">
    <c:redirect url="login.jsp">
        <c:param name="errorMsg"
                 value="The User Name or Password you entered is not valid."/>
    </c:redirect>
</c:if>

<%--
  Retrieve the information from the security database.
--%>
<c:set var="dbValues" value="${empInfo.rows[0]}"/>
<jsp:useBean id="validUser" scope="session"
             class="net.sourceforge.symba.webapp.util.PersonBean">
    <c:set target="${validUser}" property="userName"
           value="${dbValues.user_name}"/>
    <c:set target="${validUser}" property="lsid"
           value="${dbValues.lsid}"/>
</jsp:useBean>

<jsp:useBean id="counter" class="net.sourceforge.symba.webapp.util.CounterBean" scope="application"/>

<jsp:useBean id="scp" class="net.sourceforge.symba.webapp.util.ScpBean" scope="application"/>

<%

    validUser.startServices();
    validUser.setEndurantLsid( validUser.getLsid().trim() );
    Person p = ( Person ) validUser.getSymbaEntityService().getLatestByEndurant( validUser.getEndurantLsid() );
    validUser.setLsid( p.getIdentifier() );
    validUser.setEmail( p.getEmail() );
    validUser.setFirstName( p.getFirstName() );
    validUser.setLastName( p.getLastName() );

    // parse the values within the SCP Bean
    scp.setDirectory( bundle.getString( "net.sourceforge.symba.webapp.scp.directory" ) );
    scp.setHostname( bundle.getString( "net.sourceforge.symba.webapp.scp.hostname" ) );
    scp.setUsername( bundle.getString( "net.sourceforge.symba.webapp.scp.username" ) );
    scp.setPassword( bundle.getString( "net.sourceforge.symba.webapp.scp.password" ) );

    scp.setRemoteDataStoreOs( bundle.getString( "net.sourceforge.symba.webapp.scp.remote.data.store.os" ) );
    if ( scp.getRemoteDataStoreOs().equals( "dos" ) ) {
        if ( bundle.getString( "net.sourceforge.symba.webapp.scp.lsid.colon.replacement" ) != null &&
             bundle.getString( "net.sourceforge.symba.webapp.scp.lsid.colon.replacement" ).length() > 0 ) {
            scp.setLsidColonReplacement(
                    bundle.getString( "net.sourceforge.symba.webapp.scp.lsid.colon.replacement" ) );
        } else {
            scp.setLsidColonReplacement( "__" ); // provide a default value.
        }
    } else {
        scp.setLsidColonReplacement( "" ); // provide an empty value for unix.
    }

    // now get the counts
    counter.setNumberOfExperiments( validUser.getSymbaEntityService().countExperiments() );
    counter.setNumberOfDataFiles( validUser.getSymbaEntityService().countData() );
%>

<%--
  Redirect to the main page or to the original URL, if
  invoked as a result of a access attempt to a protected
  page.
--%>
<c:choose>
    <c:when test="${!empty param.origURL}">
        <c:redirect url="${param.origURL}"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="home.jsp"/>
    </c:otherwise>
</c:choose>
