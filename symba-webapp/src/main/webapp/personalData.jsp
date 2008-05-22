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


<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>

<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">

    <c:if test="${param.msg != null}">
        <c:out value="${param.msg}"/>
    </c:if>

    <!-- uses a personBean with session scope. The bean will have already been
created at the login stage. The useBean tag allows the bean to be utilised in 
any necissary scripting code in a jsp (ie stuff not in the JSTL -->
    <jsp:useBean id="validUser" scope="session" class="net.sourceforge.symba.webapp.util.PersonBean">
    </jsp:useBean>

    <h3>Update my details<a
            href="help.jsp#personalData"
            onClick="return popup(this, 'notes')"> [ Why? ] </a></h3>


        <!-- This is the form presented to the user. The information on the form is pre
filled from information held in the session persistant person bean -->


        <form action="personValidate.jsp">

            <label for="firstName">my first name:</label>
            <input id="firstName" name="firstName" value="${fn:escapeXml(validUser.firstName)}"><br>

            <label for="lastName">my last name:</label>
            <input id="lastName" name="lastName" value="${fn:escapeXml(validUser.lastName)}"><br>

            <label for="email">my email:</label>
            <input id="email" name="email" value="${fn:escapeXml(validUser.email)}"><br>

            <input type="submit" value="Update">
            <input type="button" value="Cancel" onclick="history.go(-1)"/>

        </form>

        <%--
        <form name="personSubmit" action="personValidate.jsp">
          first name<input type="text" name="firstName" value="${fn:escapeXml(validUser.firstName)}" /><br>
          last name<input type="text" name="lastName" value="${fn:escapeXml(validUser.lastName)}" /><br>
          email <input type="text" name="email" value="${fn:escapeXml(validUser.email)}" /><br>
          address<input type="text" name="address" value="${fn:escapeXml(validUser.address)}" /><br>
          <input type="submit" value="submit" name="submit" />
        </form>
        --%>


    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
