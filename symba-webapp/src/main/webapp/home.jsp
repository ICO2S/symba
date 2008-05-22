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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session">
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
    <h1>Welcome, ${fn:escapeXml(validUser.userName)}</h1>

    <c:if test="${param.msg != null}">
        <c:out value="${param.msg}"/>
    </c:if>

    <h3>Please select one of the following options: <a
            href="help.jsp#home"
            onClick="return popup(this, 'notes')"> [ Why? ]</a></h3>


    <ul>
        <li><a class="bigger" href="beginNewSession.jsp">Deposit some data (using a new or existing experiment)</a></li>
        <li><a class="bigger" href="ShowBasicResults.jsp">View all of your saved experiments</a></li>
        <li><a class="bigger" href="search.jsp">Search Experiments</a></li>
        <li><a class="bigger" href="helpBordered.jsp">How to use this site</a></li>
        <li><a class="bigger" href="personalData.jsp">Update your personal details</a></li>
    </ul>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>
</body>

</html>


