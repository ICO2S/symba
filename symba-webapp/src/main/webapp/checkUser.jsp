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

 <jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--Verify that the user is logged in-->
<c:if test="${validUser == null || validUser.userName == null}">
    <jsp:forward page="login.jsp">
        <jsp:param name="origURL" value="${pageContext.request.requestURL}"/>
        <jsp:param name="errorMsg" value="Please log in first."/>
    </jsp:forward>
</c:if>
