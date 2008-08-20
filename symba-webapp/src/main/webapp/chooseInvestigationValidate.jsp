<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt

Stores the information from the chooseInvestigation.jsp form in the SymbaFormSessionBean.
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

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%

    // Start the form handling: check for go2confirm. If present, we will redirect. If not present, we change
    // the session (if not already locked) and then redirect to the next stage of the form.
    // If the protocol is locked and go2confirm is anything other than true AND if
    // the metadata is coming from another experiment, then there is nothing to createMaterialForDataFormContents
    // from the previous page. Go straight to the next step in the form.
    if ( request.getParameter( "go2confirm" ) != null &&
         request.getParameter( "go2confirm" ).trim().equals( "true" ) ) {
%>
<c:redirect url="confirm.jsp"/>
<% } else if (
        symbaFormSessionBean.isProtocolLocked() && symbaFormSessionBean.isMetadataFromAnotherExperiment() ) {
%>
<c:redirect url="materialTransformationOrAssay.jsp"/>
<%
    }

    // If the user reaches here, then the previous page has allowed the user enter information.
    // we can allow them to change the SymbaFormSessionBean

    if ( !symbaFormSessionBean.isMetadataFromAnotherExperiment() ) {
        // get the investigation from the user-inputted information
        if ( request.getParameter( "investigationType" ) != null ) {
            String valueFromForm = request.getParameter( "investigationType" );
            String investigationEndurant =
                    valueFromForm.substring( 0, valueFromForm.indexOf( "::Identifier::" ) );
            String investigationName = valueFromForm.substring( valueFromForm.indexOf( "::Identifier::" ) + 14 );
            symbaFormSessionBean.setTopLevelProtocolName( investigationName );
            symbaFormSessionBean.setTopLevelProtocolEndurant( investigationEndurant );
        }

    }
%>
<c:redirect url="materialTransformationOrAssay.jsp"/>

