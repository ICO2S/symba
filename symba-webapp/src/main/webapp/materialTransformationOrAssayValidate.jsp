<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt

 Divert the user to the appropriate form path: to the assay protocol metadata form, or the material transformation
 metadata form (based on their response in the previous page).
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
    if ( request.getParameter( "protocolType" ) != null ) {
        if ( request.getParameter( "protocolType" ).equals( "materialTransformation" ) ) {
%>
<c:redirect url="storedMaterials.jsp"/>
<%
        } else if ( request.getParameter( "protocolType" ).equals( "assay" ) ) {
%>
<c:redirect url="rawData.jsp"/>
<%
        }
    }
%>