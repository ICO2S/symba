<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate: 2007-09-09 09:25:30 +0100 (Sun, 09 Sep 2007) $-->
<!-- $LastChangedRevision: 4 $-->
<!-- $Author: allysonlister $-->
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp/src/main/webapp/rawDataVerify.jsp $-->

<%@ page import="org.apache.commons.fileupload.FileItem" %>

<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.RawDataInfoBean" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Enumeration" %>

<%--
 Authors: Oliver Shaw, Allyson Lister
--%>
<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<%--One of the properties in the previous form belongs in the RawDataBean--%>
<jsp:useBean id="investigationBean"
             class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>


<%

    // iterate through looking for the investigation details field
    // iterate through all parameters
    Enumeration enumeration = request.getParameterNames();
    while ( enumeration.hasMoreElements() ) {
        String parameterName = ( String ) enumeration.nextElement();
        if ( parameterName.startsWith( "actionListFactor" ) ) {
            int number = Integer.valueOf( parameterName.substring( 16 ) );
//                System.out.println( "number = " + number );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            String[] parsedStrings = request.getParameter( parameterName ).split( "::" );
            temp.setFactorChoice( parsedStrings[0] );
            temp.setChosenSecondLevelChildProtocolIdentifier( parsedStrings[1] );
            temp.setChosenSecondLevelChildProtocolName( parsedStrings[2] );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "actionList" ) ) {
            int number = Integer.valueOf( parameterName.substring( 10 ) );
//                System.out.println( "number = " + number );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            String[] parsedStrings = request.getParameter( parameterName ).split( "::" );
            temp.setActionEndurant( parsedStrings[0] );
            temp.setChosenChildProtocolIdentifier( parsedStrings[1] );
            temp.setChosenChildProtocolName( parsedStrings[2] );
            investigationBean.setDataItem( temp, number );
        }
    }
%>

<% if ( request.getParameter( "go2confirm" ) != null && request.getParameter( "go2confirm" ).trim().equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } else { %>
<c:redirect url="metaData.jsp"/>
<% } %>
