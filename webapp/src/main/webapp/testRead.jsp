<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate:$-->
<!-- $LastChangedRevision:$-->
<!-- $Author:$-->
<!-- $HeadURL:$-->

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD html 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="fugeOM.Common.Audit.Person" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.FindInDB" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>

<h1>JSP Page</h1>

<%--
This example uses JSTL, uncomment the taglib directive above.
To test, display the page like this: index.jsp?sayHello=true&name=Murphy
--%>
<%--
<c:if test="${param.sayHello}">
    <!-- Let's welcome the user ${param.name} -->
    Hello ${param.name}!
</c:if>
--%>
<jsp:useBean id="validUser" scope="session" class="uk.ac.cisban.symba.webapp.util.PersonBean">

</jsp:useBean>
<%
    // Person p = new FindInDB().findPerson("14", validUser.getReService());
    Person p = new FindInDB().findPerson( validUser.getLsid(), validUser.getReService() );
    out.println( "user LSID :" + validUser.getLsid() );

    if ( p != null ) {
        out.println( p.getFirstName() );
        out.println( p.getEmail() );
        out.println( p.getLastName() );
        out.println( p.getId() );
        out.println( p.getIdentifier() );
    } else {
        out.println( "not found" );
    }

%>

<br>
<br>
${fn:escapeXml(validUser.userName)}<br>
${fn:escapeXml(validUser.firstName)}<br>
${fn:escapeXml(validUser.lastName)}<br>
${fn:escapeXml(validUser.address)}<br>
${fn:escapeXml(validUser.email)}<br>
${fn:escapeXml(validUser.identifier)}<br>

</body>
</html>
