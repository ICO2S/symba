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

<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialFormValidator" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.OntologyLoader" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>
<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>
<%
    boolean automaticReturnToMetaData = false;
    //First check whether the user only wants to get a new Term to be loaded to the database.
    // In this case enter loead new Term to Database, and return in the end back to metadata.jsp
    // instead of confirm.jsp (like if the user had chosen to review its data):

    // The session-parameter for the new term should then not be handled prior to the automatic redirection to the 
    // data-entry-page. This can be done in validateLoadRequest, but we need that method to return the session bean
    String toBeIgnoredParameterName = "";
    if ( request.getParameter( "hiddennewterminfofield" ) != null &&
         request.getParameter( "hiddennewterminfofield" ).length() > 0 ) {
        String[] tmpArr = request.getParameter( "hiddennewterminfofield" ).split( ":::" );
        toBeIgnoredParameterName = tmpArr[2];
    }
    symbaFormSessionBean = OntologyLoader.validateLoadRequest( request, validUser, symbaFormSessionBean, false );
    if ( toBeIgnoredParameterName.length() > 0 ) {
        automaticReturnToMetaData = true; //will be checked at the end of this class
    }

    // store all material parameters
    symbaFormSessionBean =
            MaterialFormValidator.validate( request, symbaFormSessionBean, toBeIgnoredParameterName, false );

    if ( automaticReturnToMetaData ) {
        ActionHierarchyScheme ahs = new ActionHierarchyScheme();
        if ( request.getParameter( ahs.getElementTitle() ) != null ) {
            response.sendRedirect(
                    "enterSpecimen.jsp?" + ahs.getElementTitle() + "=" + request.getParameter( ahs.getElementTitle() ) );
            return;
        } else {
            ahs.setDummy( true );
            if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                response.sendRedirect(
                        "enterSpecimen.jsp?" + ahs.getElementTitle() + "=" +
                        request.getParameter( ahs.getElementTitle() ) );
                return;
            }

            response.sendRedirect( "enterSpecimen.jsp" ); // there should always be one of the two above parameters
            return;
        }
    }
%>
<c:redirect url="confirmSpecimen.jsp"/>
