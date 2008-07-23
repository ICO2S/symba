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

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="net.sourceforge.symba.webapp.util.FileRetrieve" %>
<%@ page import="java.io.File" %>
<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
<%@ page import="net.sourceforge.symba.webapp.util.LsidFilenameConverter" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<jsp:useBean id="scp" class="net.sourceforge.symba.webapp.util.ScpBean" scope="application">
</jsp:useBean>

<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<html>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>


<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <h2>Click on the link to download the file</h2>

    <%--
    This example uses JSTL, uncomment the taglib directive above.
    To test, display the page like this: index.jsp?sayHello=true&name=Murphy
    --%>
    <%
        String tempDir = config.getServletContext().getRealPath( "temp" );
        FileRetrieve fr = new FileRetrieve();
        String LSID = request.getParameter( "identifier" );
        if ( scp.getRemoteDataStoreOs().equals( "dos" ) ) {
            LSID = LsidFilenameConverter.convert( LSID, scp.getLsidColonReplacement() );
        }
        String friendly = request.getParameter( "friendly" );
        try {
            File afile = fr.getFile( LSID, friendly, tempDir, scp );
            out.println(
                    " <a target=\"_blank\" href=\"" + "temp" + "/" + afile.getName() + "\">" +
                            afile.getName() + "</a>" );
        } catch ( RealizableEntityServiceException e ) {
            out.println( "There was an error retrieving your file. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            e.printStackTrace();
        }
    %>
    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
