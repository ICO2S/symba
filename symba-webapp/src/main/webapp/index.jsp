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

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD html 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>SyMBA</title>
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

<c:redirect url="home.jsp">

</c:redirect>

</body>
</html>
