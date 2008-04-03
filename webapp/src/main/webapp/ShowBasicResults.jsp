<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate: 2008-02-28 15:15:03 +0000 (Thu, 28 Feb 2008) $-->
<!-- $LastChangedRevision: 70 $-->
<!-- $Author: allysonlister $-->
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp/src/main/webapp/verify.jsp $-->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.List" %>

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

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
    List<List<String>> idsAndNames;
    if ( request.getParameter( "experimentName" ) != null &&
            request.getParameter( "experimentName" ).length() > 0 ) {
        out.println( "<h3>Search Term: " + request.getParameter( "experimentName" ) + "</h3>" );
        idsAndNames = validUser.getReService()
                .getAllLatestExperimentSummariesWithName( request.getParameter( "experimentName" ) );
    } else if ( request.getParameter( "ontologyTerm" ) != null &&
            request.getParameter( "ontologyTerm" ).length() > 0 ) {
        out.println( "<!-- Search Term: " + request.getParameter( "ontologyTerm" ) + " -->" );
        idsAndNames = validUser.getReService()
                .getAllLatestExperimentSummariesWithOntologyTerm( request.getParameter( "ontologyTerm" ) );
    } else if ( request.getParameter( "showAll" ) != null && request.getParameter( "showAll" ).length() > 0 ) {
        idsAndNames = validUser.getReService().getAllLatestExperimentSummaries();
    } else {
        idsAndNames = validUser.getReService().getAllLatestExpSummariesWithContact( validUser.getEndurantLsid() );
    }
    if ( idsAndNames.isEmpty() ) {
        out.println( "<h2>" );
        out.println( "You have no experiments at the moment, or your search term returned no results." );
        out.println( "If you wish, you may <a class=\"bigger\" href=\"newOrExisting.jsp\">deposit" );
        out.println( "some data</a>, or go to the <a href=\"search.jsp\">Search Page</a> to search the database." );
        out.println( "</h2>" );
    } else {
        if ( idsAndNames.size() == 1 ) {
            out.println( "<h3>" + idsAndNames.size() + " Experiment Retrieved</h3>" );
        } else {
            out.println( "<h3>" + idsAndNames.size() + " Experiments Retrieved</h3>" );
        }

        out.println("<p class=\"bigger\">");
        out.println("Below are the details of the experiments that match your search. Please choose one.");
        out.println("</p>");
        out.println("<ul>");
        for (List<String> idAndName : idsAndNames){
            out.println("<li>");
            out.println("<a href=view.jsp?investigationID=" + idAndName.get(0) + ">" + idAndName.get(1) + "</a>");
            out.println("</li>");
        }
        out.println("</ul>");
    }
%>
    <br><br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
