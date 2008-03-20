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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<html>
<head><title>Simple jsp page</title></head>
<body>

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
        out.println( "<h2>" );
        out.println( "You have no experiments at the moment, or your search term returned no results." );
        out.println( "If you wish, you may <a class=\"bigger\" href=\"newOrExisting.jsp\">deposit" );
        out.println( "some data</a>, or go to the <a href=\"search.jsp\">Search Page</a> to search the database." );
        out.println( "</h2>" );
    } else {
        if ( ids.size() == 1 ) {
            out.println( "<h3>" + ids.size() + " Experiment Retrieved</h3>" );
        } else {
            out.println( "<h3>" + ids.size() + " Experiments Retrieved</h3>" );
        }

        out.println("<ul>");
        for (String id : ids ){
            out.println("<li>");
            out.println("<a href=view.jsp?investigationID=" + id + ">" + id + "</a>");
            out.println("</li>");
        }
        out.println("</ul>");
    }
%>
</body>
</html>