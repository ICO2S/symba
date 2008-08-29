<%@ page import="java.util.Enumeration" %>
<%@ page import="net.sourceforge.symba.webapp.util.DatafileSpecificMetadataStore" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme" %>
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

    // if go2confirm is parse to *any* value, then we should not change anything in the session, as ChooseAction.jsp
    // is not allowed to be changed after setting. Further, even if go2confirm is null, don't allow any changes
    // if the protocol has already been locked.
    if ( request.getParameter( "go2confirm" ) == null && !symbaFormSessionBean.isProtocolLocked() ) {

        // iterate through, retrieving the action hierarchy for each data file.
        Enumeration enumeration = request.getParameterNames();
        while ( enumeration.hasMoreElements() ) {
            ActionHierarchyScheme dummyAhs = new ActionHierarchyScheme();
            dummyAhs.setAssay( true );
            dummyAhs.setDummy( true );

            String parameterName = ( String ) enumeration.nextElement();

            if ( parameterName.startsWith( dummyAhs.getElementTitle() ) ) {

                dummyAhs.parse( parameterName );
                dummyAhs.parseValueAttribute( request.getParameter( parameterName ) );

                // take what is already there, and add only those fields that have not been made yet
                DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( dummyAhs.getDatafileNumber() );
                if ( temp == null ) {
                    temp = new DatafileSpecificMetadataStore();
                }
                temp.setNestedActions( dummyAhs );
                symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, dummyAhs.getDatafileNumber() );
            }
        }
    } else if ( request.getParameter( "go2confirm" ) != null &&
                request.getParameter( "go2confirm" ).trim().equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } %>

<c:redirect url="metaData.jsp"/>
