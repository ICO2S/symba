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
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%--Imports so we can use the person object and the data portal utils --%>
<%@ page import="fugeOM.Common.Audit.Person" %>
<%@ page import="fugeOM.service.RealizableEntityServiceException" %>

<%-- Remove the validUser session bean, if any --%>
<c:remove var="validUser"/>

<%-- 
  See if the user name and password combination is valid. If not,
  redirect back to the login page with a message.
--%>
<c:if test="${empty param.userName || empty param.password}">
    <c:redirect url="login.jsp">
        <c:param name="errorMsg"
                 value="You must enter a User Name and Password."/>
    </c:redirect>
</c:if>

<%-- This allows the page to talk to a database --%>
<sql:setDataSource
        driver="org.postgresql.Driver"
        url="jdbc:postgresql://yourmachine:portnumber/dpi_security"
        user="user"
        password="pass"
        />

<%--This searches the database for the username/password combination entered 
using the parameter beans  --%>
<sql:query var="empInfo">
    SELECT * FROM users
    WHERE user_name = ? AND password = ?
    <sql:param value="${param.userName}"/>
    <sql:param value="${param.password}"/>
</sql:query>

<%-- If no rows are returned then the password is incorrect and you
must go back to the login page--%>
<c:if test="${empInfo.rowCount == 0}">
    <c:redirect url="login.jsp">
        <c:param name="errorMsg"
                 value="The User Name or Password you entered is not valid."/>
    </c:redirect>
</c:if>

<%--    
  Create an EmployeeBean and save it in 
  the session scope and redirect to the appropriate page.
--%>
<c:set var="dbValues" value="${empInfo.rows[0]}"/>
<jsp:useBean id="validUser" scope="session"
             class="net.sourceforge.symba.webapp.util.PersonBean">
    <c:set target="${validUser}" property="userName"
           value="${dbValues.user_name}"/>
    <c:set target="${validUser}" property="lsid"
           value="${dbValues.lsid}"/>
</jsp:useBean>

<jsp:useBean id="counter" class="net.sourceforge.symba.webapp.util.CounterBean" scope="application"/>

<jsp:useBean id="scp" class="net.sourceforge.symba.webapp.util.ScpBean" scope="application"/>

<%
    validUser.startRe();
    validUser.setLsid( validUser.getLsid().trim() );
    boolean errorFound = false;
    try {
        Person p = ( Person ) validUser.getReService().findLatestByEndurant( validUser.getLsid() );
        validUser.setEndurantLsid( p.getEndurant().getIdentifier() );
        validUser.setEmail( p.getEmail() );
        validUser.setLsid( p.getIdentifier() );
        validUser.setFirstName( p.getFirstName() );
        validUser.setLastName( p.getLastName() );

    } catch ( RealizableEntityServiceException e ) {
        errorFound = true;
        out.println( "Error talking to the database in order to retrieve user details. Please send this message to" );
        out.println( application.getAttribute( "helpEmail" ) + "<br/>" );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

    // todo now set the variables. A bit temporary, but will do until a real properties file setup is done
    scp.setDirectory( "/your/directory" );
    scp.setHostname( "yourmachine.com" );
    scp.setUsername( "user" );
    scp.setPassword( "pass" );

    application.setAttribute( "helpEmail", "helpdesk@cisban.ac.uk" );

    // now get the counts
    try {
        counter.setNumberOfExperiments( validUser.getReService().countLatestExperiments() );
        counter.setNumberOfDataFiles( validUser.getReService().countData() );
    } catch ( RealizableEntityServiceException e ) {
        out.println(
                "There was an error counting the number of experiments and data files. For help, please send this message to " );
        out.println( application.getAttribute( "helpEmail" ) );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

    if ( !errorFound ) {
%>

<%-- 
  Redirect to the main page or to the original URL, if
  invoked as a result of a access attempt to a protected
  page.
--%>
<c:choose>
    <c:when test="${!empty param.origURL}">
        <c:redirect url="${param.origURL}"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="home.jsp"/>
    </c:otherwise>
</c:choose>

<%
    }
%>
