<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate:$-->
<!-- $LastChangedRevision:$-->
<!-- $Author:$-->
<!-- $HeadURL:$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Remove the session beans --%>
<c:remove var="experiment"/>
<c:remove var="rdb"/>

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

    <h2>New Experiment <a href="help.jsp#newExperiment"
                          onClick="return popup(this, 'notes')"> [ What Is Classed As An Experiment? ]</a></h2>

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <%-- Remove the fuge value in the experiment variable --%>
    <%
        experiment.setFuGE( null );
    %>

    <h3>Please add some information about your experiment:</h3>

    <form name="metaForm" action="newExperimentValidate.jsp">

        <label for="experimentName">Experiment Name:(<font color="red">*</font>)</label>
        <input id="experimentName"
               name="experimentName"
                <% if ( experiment.getExperimentName() != null && experiment.getExperimentName().length() > 0 ) {
                    out.println( "value=\"" + experiment.getExperimentName() + "\">" );
                } else {
                    out.println( ">" );
                } %>
        <br>

        <label for="hypothesis">Hypothesis:</label>
        <textarea id="hypothesis" rows="5" cols="40" name="hypothesis"
                <% if ( experiment.getHypothesis() != null && experiment.getHypothesis().length() > 0 ) {
                    // putting the brackets here means non-essential whitespace is not shown
                    out.println( ">" + experiment.getHypothesis() + "</textarea>" );
                } else {
                    out.println( "></textarea>" );
                } %>
        <br>

        <label for="conclusion">Conclusions:</label>
        <textarea id="conclusion" rows="5" cols="40" name="conclusion"
                <% if ( experiment.getConclusion() != null && experiment.getConclusion().length() > 0 ) {
                    // putting the brackets here means non-essential whitespace is not shown
                    out.println( ">" + experiment.getConclusion() + "</textarea>" );
                } else {
                    out.println( "></textarea>" );
                } %>
        <br>

        <input type="submit" value="Submit"/>
        <%--<% if ( experiment.getExperimentName() != null ) { %>--%>
        <!--<input type="hidden" name="godirect" value="true"/>-->
        <!--<input type="submit" value="Go Back to Confirmation Page"/>-->
        <%--<% } %>--%>
        <input type="button" value="Back" onclick="history.go(-1)">
    </form>
    <br>

    <p>
        (<font color="red">*</font>) Sections marked with an asterisk must be filled in. All other
        sections are optional.
    </p>

    <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="file" name="afile"/>
    <br><br>

    <input type="submit" value = "Submit"/>
    --%>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
