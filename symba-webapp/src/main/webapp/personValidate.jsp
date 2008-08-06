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

<%@ page import="net.sourceforge.symba.webapp.util.loading.LoadPerson" %>


<!-- this page is soly used for the logic flow. There is nothing that can be displayed in this page -->

<!-- import the core jstl library -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<!-- sets all the page person bean fields based on the param bean (holding the
parameters passed to the page. This is automatic as long as the
forms field id's match EXACTLY the beans fields. -->

<jsp:setProperty name="validUser" property="*"/>


<%

    //create a new LoadPerson object
    LoadPerson lp = new LoadPerson( validUser.getEntityService() );

    //and use the LoadPerson objects method to load/update the information
    //into the database
    session.setAttribute( "validUser", lp.loadInDB( validUser ) );

%>
<c:redirect url="detailsChanged.jsp">
    <c:param name="msg"
             value="YOU HAVE UPDATED YOUR DETAILS"/>
</c:redirect>
