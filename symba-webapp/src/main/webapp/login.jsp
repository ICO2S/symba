<%@ page import="java.util.ResourceBundle" %>
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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Load application resource bundle --%>
<%!
    ResourceBundle bundle = null;

    public void jspInit() {
        bundle = ResourceBundle.getBundle( "symba" );
    }

%>

<jsp:useBean id="softwareMeta" class="net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean"
             scope="application"/>

<%
    // parse the application attribute first, since it may be needed
    // if an error occurs during the initial database access.

    application.setAttribute( "helpEmail", bundle.getString( "net.sourceforge.symba.webapp.helpEmail" ) );
    softwareMeta.setName( bundle.getString( "net.sourceforge.symba.webapp.softwareName" ) );
    softwareMeta.setVersion( bundle.getString( "net.sourceforge.symba.webapp.softwareVersion" ) );

%>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>

<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <h1>Welcome to <% out.print(softwareMeta.getName()); %> </h1>

    <%--<p>--%>
    <%--<font class="blueText">--%>
    <%--${fn:escapeXml(param.errorMsg)}--%>
    <%--</font>--%>
    <%--</p>--%>

    <p class="bigger">
        <%
            out.println( bundle.getString( "net.sourceforge.symba.webapp.status" ) );
        %>
    </p>

    <form action="verify.jsp" method="post">

        <fieldset>
            <legend>Please log on to add or view experiments <a
                    href="help.jsp#logon"
                    onClick="return popup(this, 'notes')">[ why? ]</a></legend>

            <ol>

                <li>
                    <label for="userName">User name:</label>
                    <input id="userName" name="userName"><br/>
                </li>

                <li>
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password"><br/>
                </li>
            </ol>
        </fieldset>

        <fieldset class="submit">
            <input type="submit" value="Enter">
        </fieldset>

    </form>


    <%--  <form action="verify.jsp" method="post">
   <input name="userName"
          value="${fn:escapeXml(cookie.userName.value)}"
          size="10">
   Password:
   <input type="password" name="password"
          value="${fn:escapeXml(cookie.password.value)}"
          size="10">
   <input type="submit" value="Enter">
 <p>
   Remember my name and password:
   <input type="checkbox" name="remember"
          ${!empty cookie.userName ? 'checked' : ''}>
   <br> --%>


    <p>If you do not have an account, or if you have any questions or comments, please
        <%
            out.println( "<a href=\"mailto:" + application.getAttribute( "helpEmail" ) + "\">contact us</a>" );
        %>
    </p>

</div>

<div id="Menu">
    <ul>
        <jsp:include page="menu-static.html"/>
    </ul>
</div>
</body>
</html>
