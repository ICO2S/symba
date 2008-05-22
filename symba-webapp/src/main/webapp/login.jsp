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

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>

<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <h1>Welcome to SyMBA</h1>

    <%--<p>--%>
    <%--<font class="blueText">--%>
    <%--${fn:escapeXml(param.errorMsg)}--%>
    <%--</font>--%>
    <%--</p>--%>


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


    <p>If you do not have an account, please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>


</div>

<div id="Menu">
    <ul>
        <jsp:include page="menu-static.html"/>
    </ul>
</div>
</body>
</html>