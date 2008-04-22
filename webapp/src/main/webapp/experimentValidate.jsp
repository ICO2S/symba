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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" scope="session" class="uk.ac.cisban.symba.webapp.util.SymbaFormSessionBean"/>

<jsp:setProperty name="symbaFormSessionBean" property="fugeIdentifier"/>

<%
    FuGE fuge = ( FuGE ) validUser.getReService().findIdentifiable( symbaFormSessionBean.getFugeIdentifier() );
    symbaFormSessionBean.setFuGE( fuge );
    symbaFormSessionBean.setFugeEndurant( fuge.getEndurant().getIdentifier() );
%>

<% if ( request.getParameter( "go2confirm" ) != null && request.getParameter( "go2confirm" ).trim().equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } else { %>
<c:redirect url="rawData.jsp"/>
<% } %>