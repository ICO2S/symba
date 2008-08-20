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

<%@ page import="net.sourceforge.fuge.collection.FuGE" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.MaterialTransformationLoader" %>
<%@ page import="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>
<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>
<jsp:useBean id="softwareMeta" class="net.sourceforge.symba.webapp.util.SoftwareMetaInformationBean" scope="application"/>
<%
    // store the new specimen in the database
    MaterialTransformationLoader mtl = new MaterialTransformationLoader(symbaFormSessionBean, validUser, softwareMeta );
    FuGE fugeWithSpecimen = mtl.load();

    // clear the session bean.
    symbaFormSessionBean = new SymbaFormSessionBean();

    // put in the information about the newly-stored FuGE experiment
    symbaFormSessionBean.setFuGE( fugeWithSpecimen );
    symbaFormSessionBean.setFugeEndurant( fugeWithSpecimen.getEndurant().getIdentifier() );
    symbaFormSessionBean.setFugeIdentifier( fugeWithSpecimen.getIdentifier() );
%>
<c:redirect url="storedMaterials.jsp"/>