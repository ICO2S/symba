<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%--Imports so we can use the person object and the data portal utils --%>
<%@ page import="fugeOM.Common.Audit.Person" %>
<%@ page import="fugeOM.service.RealizableEntityService" %>

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

<%-- This allows the page to talk to a database, now one on postgress--%>
<!--url="jdbc:postgresql://localhost:5433/dpi_security"-->
<!--url="jdbc:postgresql://elephant:5433/dpi_security"-->

<!-- Note on metagenome: -->
<!-- using metagenome doesn't work properly - some problem with the metagenome tomcat server talking to-->
<!-- the metagenome postgres instance. Changing to localhost solves the problem, but means that the-->
<!-- tomcat instance has to be on metagenome in order to talk to the correct database!!! -->
<!--url="jdbc:postgresql://metagenome:5433/dpi_security"-->
<!--url="jdbc:postgresql://petrinets.ncl.ac.uk:5434/dpi_security"-->
<!--url="jdbc:postgresql://localhost:5433/dpi_security"-->
<sql:setDataSource
        driver="org.postgresql.Driver"
        url="jdbc:postgresql://petrinets.ncl.ac.uk:5434/dpi_security"
        user="dpi"
        password="c15b4n_dpi"
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
             class="uk.ac.cisban.symba.webapp.util.PersonBean">
    <c:set target="${validUser}" property="userName"
           value="${dbValues.user_name}"/>
    <c:set target="${validUser}" property="lsid"
           value="${dbValues.lsid}"/>
</jsp:useBean>

<jsp:useBean id="experiment" scope="session"
             class="uk.ac.cisban.symba.webapp.util.ExperimentBean">
</jsp:useBean>

<jsp:useBean id="counter" class="uk.ac.cisban.symba.webapp.util.CounterBean" scope="application">
</jsp:useBean>

<jsp:useBean id="scp" class="uk.ac.cisban.symba.webapp.util.ScpBean" scope="application">
</jsp:useBean>

<%
    validUser.startRe();
    validUser.setLsid( validUser.getLsid().trim() );
    RealizableEntityService reService = validUser.getReService();
//    List <Person>people = reService.getAllPeople();
//    for (Person p: people)
//      {
//         System.out.println(p.getFirstName());
//      }
    Person p = ( Person ) validUser.getReService().findLatestByEndurant( validUser.getLsid() );
    if ( p != null ) {
        System.out.println( "it's not null" );
        validUser.setEndurantLsid( p.getEndurant().getIdentifier() );
        validUser.setEmail( p.getEmail() );
        validUser.setLsid( p.getIdentifier() );
        validUser.setFirstName( p.getFirstName() );
        validUser.setLastName( p.getLastName() );

    } else {
        System.out.println( "it is null" );
    }

    // todo now set the variables. A bit temporary, but will do until a real properties file setup is done
    scp.setDirectory( "/data/dpi/sandbox/" );
    scp.setHostname( "cisbclust.ncl.ac.uk" );
    scp.setUsername( "dpi" );
    scp.setPassword( "c15b4n_dpi" );

    // now get the counts
    counter.setNumberOfExperiments( validUser.getReService().countLatestExperiments() );
    counter.setNumberOfDataFiles( validUser.getReService().countData() );
%>


<%--Code for the cookie code, will be implemented in the production version --%>
<%--<c:choose>
  <c:when test="${!empty param.remember}">
    <ora:addCookie name="userName" 
      value="${param.userName}"
      maxAge="2592000" />
    <ora:addCookie name="password" 
      value="${param.password}"
      maxAge="2592000" />
  </c:when>
  <c:otherwise>
    <ora:addCookie name="userName" 
      value="${param.userName}"
      maxAge="0" />
    <ora:addCookie name="password" 
      value="${param.password}"
      maxAge="0" />
  </c:otherwise>
</c:choose>--%>

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
