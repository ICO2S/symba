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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<jsp:useBean id="softwareMeta" class="net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean"
             scope="application"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>
<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> (3) Upload Data ->
        (4) Select Protocol -> <font class="blueText">(5) Confirm Your Submission</font> -> (6) Completion and Download
    </p>

    <p>


    <h2>Your specimen is ready to be submitted to the repository</h2>

    <p class="bigger">
        <font color="red">
            However, your changes are NOT saved until you click the "Confirm All" button below!
        </font>
    </p>

    <p class="bigger">
        Please check all of your details. Click on any item you wish to modify, which will take you back to the
        appropriate form page, where you can correct any mistakes.
    </p>

    <%
        // ensure that there is at least either a pre-existing experiment or a new experiment name before
        // displaying. If there isn't, send the user back to the newOrExisting.jsp page and clear their sesssion.
        if ( symbaFormSessionBean.getFuGE() == null && symbaFormSessionBean.getExperimentName() == null ) {
    %>
    <c:remove var="symbaFormSessionBean"/>
    <c:redirect url="newOrExisting.jsp"/>
    <%
        }

        symbaFormSessionBean.displayHtml( out, validUser.getSymbaEntityService() );
    %>

    <p class="bigger">
        <font color="red">
            Remember, your changes are NOT saved until you click the "Confirm All" button below!
        </font>
    </p>

    <FORM ACTION="loadSpecimen.jsp" METHOD=POST>
        <input type="submit" value="CONFIRM ALL"/>
    </FORM>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>


