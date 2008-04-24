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

<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanFuGEHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.xml.XMLMarshaler" %>
<%@ page import="javax.xml.bind.JAXBException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.net.URISyntaxException" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">

    <%
        String sf = config.getServletContext().getRealPath( "schemaFiles/FuGE_M3_test_13_07_2006.xsd" );
        CisbanFuGEHelper cf = new CisbanFuGEHelper( validUser.getReService() );
        try {
            FuGE exp = cf.getLatestVersion( request.getParameter( "endurant" ) );

            XMLMarshaler xmlMarsh = new XMLMarshaler( sf );
            StringWriter stringOut = new StringWriter();
            PrintWriter pw = new PrintWriter( stringOut );
            xmlMarsh.FuGEToJaxb2( exp, pw );
            String s = stringOut.toString();
            s = s.replace( "<", "&lt;" );
            s = s.replace( ">", "&gt;" );
            out.println( "<pre><code>" );
            out.print( s );
            out.println( "</code></pre>" );
        } catch ( RealizableEntityServiceException e ) {
            out.println(
                    "There was an error retrieving the latest version of the experiment. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } catch ( JAXBException e ) {
            out.println( "There was an error creating the XML. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } catch ( SAXException e ) {
            out.println( "There was an error creating the XML. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } catch ( URISyntaxException e ) {
            out.println( "There was an error creating the XML. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }
    %>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>

</body>

</html>
