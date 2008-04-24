<%@ page import="uk.ac.cisban.symba.webapp.util.SymbaFormSessionBean" %>
<%@ page import="java.util.Map" %>
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

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session"/>

<c:remove var="symbaFormSessionBean"/>

<%

    // unchecked cast warning provided by javac when using generics in Lists/Sets and
    // casting from Object, even though runtime can handle this.
    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
    @SuppressWarnings( "unchecked" )
    Map<String, SymbaFormSessionBean> allPossibleMetadata = ( Map<String, SymbaFormSessionBean> ) session.getAttribute(
            "allPossibleMetadata" );

    if ( allPossibleMetadata == null ) {
        // we have reached this page in error. Just go to the home page
%>
<c:redirect url="home.jsp"/>
<%
} else if ( request.getParameter( "identifier" ) != null && request.getParameter( "identifier" ).length() > 0 &&
        allPossibleMetadata.containsKey( request.getParameter( "identifier" ) ) ) {
    // use the hidden value provided by view.jsp to get the right session bean out of the Map
    session.setAttribute( "symbaFormSessionBean", allPossibleMetadata.get( request.getParameter( "identifier" ) ) );
} else {
    // there was a problem - no SymbaFormSessionBean matches the provided identifier. Redirect to
    // beginNewSession.jsp, which will allow a reset of session variables
%>
<c:remove var="allPossibleMetadata"/>
<c:redirect url="beginNewSession.jsp"/>
<%
    }
    // Next, divert to rawData.jsp with all metadata filled in.
%>
<c:remove var="allPossibleMetadata"/>
<%
    if ( ( ( SymbaFormSessionBean ) session.getAttribute( "symbaFormSessionBean" ) ).getFuGE() == null ) {
        // the metadata was taken from an experiment the user doesn't have rights to change. Force them to fill in
        // a new experiment.
%>
<c:redirect url="newExperiment.jsp"/>
<%
    }
%>
<c:redirect url="rawData.jsp"/>

