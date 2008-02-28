<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.CannedSearch" %>
<%@ page import="java.util.List" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<%-- Remove the session beans --%>
<c:remove var="experiment"/>
<c:remove var="investigationBean"/>

<jsp:useBean id="experiment" class="uk.ac.cisban.symba.webapp.util.ExperimentBean" scope="session">
</jsp:useBean>

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
        (1) Introduction -> <font class="blueText">(2) Attach to an Experiment</font> -> (3) Upload Data ->
        (4) Select Protocol -> (5) Confirm Your Submission -> (6) Completion and Download
    </p>

    <h2>Select exisiting experiment <a
            href="help.jsp#existingExperiment"
            onClick="return popup(this, 'notes')"> [ What Choice Should I Make? ]</a></h2>

    <h3>Select the experiment to which this data pertains:</h3>

    <form name="selectProt" action="experimentValidate.jsp">
        <label for="experimentList">Existing experiments: </label>
        <select id="experimentList" name="experimentList">
            <%
                CannedSearch ts = new CannedSearch();
                // ts.listExperimentsByPerson(validUser.getCreatedMaterial());
                ts.listExperimentsByPerson( validUser.getEndurantLsid() );
                List exList = ts.getExperiments();
                for ( int x = 0; x < exList.size(); x++ ) {
                    FuGE fug = ( FuGE ) exList.get( x );

                    out.println(
                            "<option value=\"" + fug.getEndurant().getIdentifier() + "\">" +
                                    fug.getName() + "</option>" );
                }
            %>
        </select><br>
        <input type="submit" value="Select" name="submit"/>
        <input type="button" value="Back" onclick="history.go(-1)">
    </form>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>

</body>
</html>
