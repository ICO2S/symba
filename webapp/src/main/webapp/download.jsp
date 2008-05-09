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

<%@ page import="uk.ac.cisban.symba.webapp.util.DatafileSpecificMetadataStore" %>

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<jsp:useBean id="symbaFormSessionBean" class="uk.ac.cisban.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> (3) Upload Data ->
        (4) Select Protocol -> (5) Confirm Your Submission -> <font class="blueText">(6) Completion and Download</font>
    </p>

    <p>


    <h2>Congratulations! Your data has been submitted to the repository.</h2>

    <p class="bigger">Depending on your centre's policy, the file with the new identifier may now have
        to be downloaded. <a
            href="help.jsp#completeDeposition"
            onClick="return popup(this, 'notes')">[ Why Do I Need To Do This? ]</a></p>

    <p class="bigger">
        You may download your data files by going to
        <a class="bigger" href="ShowBasicResults.jsp">your saved experiments</a> and chosing your just-loaded
        experiment. From there, you can download each data file.
    </p>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>

</body>

</html>
