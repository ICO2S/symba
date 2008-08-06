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

<%@ page import="net.sourceforge.fuge.collection.FuGE" %>

<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.bind.JAXBException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.xml.XMLMarshaler" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.helper.FuGEMappingHelper" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">

    <%
        String sf = config.getServletContext().getRealPath( "schemaFiles/xmlSchema.xsd" );
        FuGEMappingHelper fugeMappingHelper = new FuGEMappingHelper();
        // todo test latest version
        FuGE exp = ( FuGE ) validUser.getSymbaEntityService().getLatestByEndurant( request.getParameter( "endurant" ) );
//            FuGE exp = fugeMappingHelper.getLatestVersion( request.getParameter( "endurant" ) );
        try {
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
