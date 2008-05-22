<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%
    // immediately redirect if the user doesn't have any experiments yet.
    try {
    if ( validUser.getReService().getAllLatestExpSummariesWithContact( validUser.getEndurantLsid() ).isEmpty() ) {
%>

<c:redirect url="newExperiment.jsp"/>

<%
    }
%>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <p>
        (1) Introduction -> <font class="blueText">(2) Attach to an Experiment</font> -> (3) Upload Data -> (4)
        Confirmation of Upload ->
        (5) Select Protocol -> (6) Confirm Your Submission -> (8) Completion and Download
    </p>

    <%
    } catch( RealizableEntityServiceException e) {
        out.println( "There was an error checking to see if you already had an experiment in the database. For help, please send this message to " );
        out.println( application.getAttribute( "helpEmail" ) );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

    %>
    
    <h3>Do you wish to create a new experiment or add to an existing one? <a
            href="help.jsp#newOrExist"
            onClick="return popup(this, 'notes')"> [ What Choice Do I Make? ] </a>
    </h3>

    <ul>
        <li><a class="bigger" href="newExperiment.jsp"> A new experiment </a></li>
        <li><a class="bigger" href="experiment.jsp"> An existing experiment </a></li>
    </ul>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>