<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%--Imports so we can use the person object and the data portal utils --%>
<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanFuGEHelper" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

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
        List<String> ids;
        if ( request.getParameter( "experimentName" ) != null &&
                request.getParameter( "experimentName" ).length() > 0 ) {
            out.println( "<h3>Search Term: " + request.getParameter( "experimentName" ) + "</h3>" );
            ids = validUser.getReService()
                    .getAllLatestExperimentIdsWithName( request.getParameter( "experimentName" ) );
        } else if ( request.getParameter( "ontologyTerm" ) != null &&
                request.getParameter( "ontologyTerm" ).length() > 0 ) {
            out.println( "<!-- Search Term: " + request.getParameter( "ontologyTerm" ) + " -->" );
            ids = validUser.getReService()
                    .getAllLatestExperimentIdsWithOntologyTerm( request.getParameter( "ontologyTerm" ) );
        } else if ( request.getParameter( "showAll" ) != null && request.getParameter( "showAll" ).length() > 0 ) {
            ids = validUser.getReService().getAllLatestExperimentIds();
        } else {
            ids = validUser.getReService().getAllLatestExpIdsWithContact( validUser.getEndurantLsid() );
        }
        if ( ids.isEmpty() ) {
    %>

    <h2>
        You have no experiments at the moment, or your search term returned no results.
        If you wish, you may <a class="bigger" href="newOrExisting.jsp">deposit
        some data</a>, or go to the <a href="search.jsp">Search Page</a> to search the database.
    </h2>
    <%
    } else {
        if ( ids.size() == 1 ) {
            out.println( "<h3>" + ids.size() + " Experiment Retrieved</h3>" );
        } else {
            out.println( "<h3>" + ids.size() + " Experiments Retrieved</h3>" );
        }
//        for (String id : ids) {
//            out.println(id + "<br/>");
//        }
        out.println( "For further searches, please visit our <a href=\"search.jsp\">Search Page</a><br/>" );
    %>
    <h2>Your Data is shown below <a
            href="help.jsp#viewExperiments"
            onClick="return popup(this, 'notes')"> [ Help? ]</a></h2>

    <%
            for ( Object obj : ids ) {
                CisbanFuGEHelper cfh = new CisbanFuGEHelper( validUser.getReService() );
                String identifier = ( String ) obj;
                // don't get latest version here: prettyHtml will get the latest version ONLY for those
                // objects it is displaying.
                FuGE fuge = ( FuGE ) validUser.getReService().findIdentifiable( identifier );
                cfh.prettyHtml( fuge, new PrintWriter( out ) );
                // Go directly to experimentValidate.jsp
                out.println( "<form action=\"experimentValidate.jsp\">" );
                out.println(
                        "<input type=\"hidden\" name=\"experimentList\" value=\"" + fuge.getEndurant().getIdentifier() +
                                "\"/>" );
                out.println( "<input type=\"submit\" value=\"Add Data To This Experiment\"/>" );
                out.println( "</form>" );

                // View the xml format for this entry.
                out.println( "<form action=\"viewFugeML.jsp\">" );
                out.println(
                        "<input type=\"hidden\" name=\"endurant\" value=\"" + fuge.getEndurant().getIdentifier() +
                                "\"/>" );
                out.println( "<input type=\"submit\" size=\"10\" value=\"show XML\"/>" );
                out.println( "</form><br>" );
            }
        }
    %>

    <br><br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>


