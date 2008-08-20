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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<p class="centered">
    <a href="home.jsp">Return Home</a> :: <a href="help.jsp#intro"
                                             onClick="return popup(this, 'notes')">View Help</a><br>
    You are currently logged in as ${fn:escapeXml(validUser.userName)}<br>
    If you have any questions or comments, please
    <%
        out.println("<a href=\"mailto:" + application.getAttribute( "helpEmail" ) + "\">contact us</a>");
    %>
</p>

<div id="Logo" align=center><a href="http://www.cisban.ac.uk"><IMG SRC="pics/logo-small.jpg" border="0"></a>
</div>
