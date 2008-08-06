<%@ page import="net.sourceforge.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>
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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%--One of the properties in the previous form belongs in the RawDataBean--%>
<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>


<%

    // if go2confirm is set to *any* value, then we should not change anything in the session, as ChooseAction.sjp
    // is not allowed to be changed after setting. Further, even if go2confirm is null, don't allow any changes
    // if the protocol has already been locked.
    if ( request.getParameter( "go2confirm" ) == null && !symbaFormSessionBean.isProtocolLocked() ) {

        // iterate through looking for the investigation details field
        // iterate through all parameters
        Enumeration enumeration = request.getParameterNames();
        while ( enumeration.hasMoreElements() ) {
            String parameterName = ( String ) enumeration.nextElement();
            if ( parameterName.startsWith( "actionListOneLevelUp" ) ) {
                int number = Integer.valueOf( parameterName.substring( 20 ) );
//                System.out.println( "number = " + number );
                // take what is already there, and add only those fields that have not been made yet
                DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( number );
                if ( temp == null ) {
                    temp = new DatafileSpecificMetadataStore();
                }
                String[] parsedStrings = request.getParameter( parameterName ).split( "::" );
                temp.getOneLevelUpActionSummary().setChosenActionEndurant( parsedStrings[0] );
                temp.getOneLevelUpActionSummary().setChosenActionName( parsedStrings[1] );
                temp.getOneLevelUpActionSummary().setChosenChildProtocolEndurant( parsedStrings[2] );
                temp.getOneLevelUpActionSummary().setChosenChildProtocolName( parsedStrings[3] );
                symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
            } else if ( parameterName.startsWith( "actionListAssay" ) ) {
                int number = Integer.valueOf( parameterName.substring( 15 ) );
//                System.out.println( "number = " + number );
                // take what is already there, and add only those fields that have not been made yet
                DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( number );
                if ( temp == null ) {
                    temp = new DatafileSpecificMetadataStore();
                }
                String[] parsedStrings = request.getParameter( parameterName ).split( "::" );
                temp.getAssayActionSummary().setChosenActionEndurant( parsedStrings[0] );
                temp.getAssayActionSummary().setChosenActionName( parsedStrings[1] );
                temp.getAssayActionSummary().setChosenChildProtocolEndurant( parsedStrings[2] );
                temp.getAssayActionSummary().setChosenChildProtocolName( parsedStrings[3] );
                symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
            }
        }
    } else if ( request.getParameter( "go2confirm" ) != null &&
                request.getParameter( "go2confirm" ).trim().equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } %>

<c:redirect url="metaData.jsp"/>
