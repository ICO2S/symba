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

<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
<%@ page import="net.sourceforge.symba.webapp.util.*" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<jsp:useBean id="counter" class="net.sourceforge.symba.webapp.util.CounterBean" scope="application"/>

<jsp:useBean id="scp" class="net.sourceforge.symba.webapp.util.ScpBean" scope="application"/>

<%
    boolean errorFound = false;
    LoadFuge lf = new LoadFuge( symbaFormSessionBean, validUser, scp );
    try {
        lf.load();
    } catch ( RealizableEntityServiceException e ) {
        errorFound = true;
        out.println( "There was an error talking to the database. For help, please send this message to " );
        out.println( application.getAttribute( "helpEmail" ) );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

    // Update the counts
    try {
        counter.setNumberOfExperiments( validUser.getReService().countLatestExperiments() );
        counter.setNumberOfDataFiles( validUser.getReService().countData() );
    } catch ( RealizableEntityServiceException e ) {
        errorFound = true;
        out.println(
                "There was an error counting the number of experiments and data files. For help, please send this message to " );
        out.println( application.getAttribute( "helpEmail" ) );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

%>
<%-- Remove all user-specific session beans associated with data upload --%>
<c:remove var="symbaFormSessionBean"/>

<%
    // don't redirect if there has been an exception
    if ( !errorFound ) {
%>

<c:redirect url="download.jsp">
    <c:param name="msg"
             value="Submission Saved and Complete"/>
</c:redirect>
<%
    }
%>
