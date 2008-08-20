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

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="symbaFormSessionBean" scope="session" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean"/>

<%-- Sets experimentName, hypothesis, and conclusion --%>
<jsp:setProperty name="symbaFormSessionBean" property="*"/>

<%
    //Now un-parse the pre-existing FuGE properties of the SymbaFormSessionBean, just in case
    // someone has started down that route, but then realized they want to start a new experiment.
    // Pressing "Submit" from newExperiment.jsp is enough to convince us that they're serious about the
    // new experiment (versus attaching the data file to a pre-existing experiment)
    symbaFormSessionBean.setFuGE( null );
    symbaFormSessionBean.setFugeEndurant( "" );
    symbaFormSessionBean.setFugeIdentifier( "" );

    if ( symbaFormSessionBean.getExperimentName() == null ) {
%>
<c:redirect url="newExperiment.jsp">
    <c:param name="errorMsg"
             value="You must enter an Experiment Name."/>
</c:redirect>
<%
    }
%>

<% if ( request.getParameter( "go2confirm" ) != null &&
        request.getParameter( "go2confirm" ).trim().equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } else { %>
<c:redirect url="chooseInvestigation.jsp"/>
<% } %>