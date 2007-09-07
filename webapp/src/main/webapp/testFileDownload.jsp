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
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>
<jsp:useBean id="fileHold" class="uk.ac.cisban.symba.webapp.util.FileBean" scope="session">
</jsp:useBean>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>

<h1>JSP Page</h1>


<%
    //String conPath = config.getServletContext().getContext(fileHold.getAFile().getAbsolutePath());
    out.println( fileHold.getAFile().getName() );
    //out.println("<p>"+conPath+"</p>s");
    //out.println("<a href=\""+fileHold.getAFile().getAbsolutePath()+"\">your file here</a>");
    //out.println(" <a href=\""+File.separator+"temp"+File.separator+fileHold.getAFile().getName()+"\">your file here</a>");
    out.println( " <a href=\"" + "temp" + "/" + fileHold.getAFile().getName() + "\">your file here</a>" );
%>

</body>
</html>
