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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

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
        // this variable is used so that we can make use of the SuppressWarnings java annotation
        List plainList = null;
        try {
            // Each inner list contains 3 objects: the first two are Strings, the third is a java.sql.Timestamp
            if ( request.getParameter( "experimentName" ) != null &&
                    request.getParameter( "experimentName" ).length() > 0 ) {
                out.println( "<h3>Search Term: " + request.getParameter( "experimentName" ) + "</h3>" );
                plainList = validUser.getReService()
                        .getAllLatestExperimentSummariesWithName( request.getParameter( "experimentName" ) );
            } else if ( request.getParameter( "ontologyTerm" ) != null &&
                    request.getParameter( "ontologyTerm" ).length() > 0 ) {
                out.println( "<!-- Search Term: " + request.getParameter( "ontologyTerm" ) + " -->" );
                plainList = validUser.getReService()
                        .getAllLatestExperimentSummariesWithOntologyTerm( request.getParameter( "ontologyTerm" ) );
            } else if ( request.getParameter( "showAll" ) != null && request.getParameter( "showAll" ).length() > 0 ) {
                plainList = validUser.getReService().getAllLatestExperimentSummaries();
            } else {
                plainList = validUser.getReService().getAllLatestExpSummariesWithContact( validUser.getEndurantLsid() );
            }
        } catch ( RealizableEntityServiceException e ) {
            out.println( "There was an error talking to the database. For help, please send this message to " );
            out.println( application.getAttribute( "helpEmail" ) );
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }

        // unchecked cast warning provided by javac when using generics in Lists/Sets and
        // casting from Object, even though runtime can handle this.
        // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
        @SuppressWarnings( "unchecked" )
        List<List<Object>> summaries = (List<List<Object>>) plainList;

        if ( summaries != null ) {
            if ( summaries.isEmpty() ) {
                out.println( "<h2>" );
                out.println( "You have no experiments at the moment, or your search term returned no results." );
                out.println( "If you wish, you may <a class=\"bigger\" href=\"newOrExisting.jsp\">deposit" );
                out.println(
                        "some data</a>, or go to the <a href=\"search.jsp\">Search Page</a> to search the database." );
                out.println( "</h2>" );
            } else {
                if ( summaries.size() == 1 ) {
                    out.println( "<h3>" + summaries.size() + " Experiment Retrieved</h3>" );
                } else {
                    out.println( "<h3>" + summaries.size() + " Experiments Retrieved</h3>" );
                }

                out.println( "<p class=\"bigger\">" );
                out.println( "Below are the details of the experiments that match your search. Please choose one." );
                out.println( "</p>" );
                out.println( "<ul>" );
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm z" );
                for ( List<Object> summary : summaries ) {
                    out.println( "<li>" );
                    out.println( "<a href=view.jsp?investigationID=" + summary.get( 0 ) + ">" + summary.get( 1 ) );
                    out.println( " (" + formatter.format( summary.get( 2 ) ) + ")</a>" );
                    out.println( "</li>" );
                }
                out.println( "</ul>" );
            }
        }
    %>
    <br><br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
