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

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

    <h3>Your New Details Are: <a
            href="help.jsp#detailsChanged"
            onClick="return popup(this, 'notes')"> [ Learn more ] </a></h3>

    <p class="bigger">Your first name is <font class="blueText"> ${fn:escapeXml(validUser.firstName)}</font></p>

    <p class="bigger">Your last name is <font class="blueText">${fn:escapeXml(validUser.lastName)}</font></p>

    <p class="bigger">Your email address is <font class="blueText">${fn:escapeXml(validUser.email)}</font></p>


    <form name="done" action="home.jsp">
        <input type="submit" value="Confirm" name="confirm"/>
        <input type="button" value="Change" onclick="history.go(-1)"/>
    </form>

    <br>
    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
