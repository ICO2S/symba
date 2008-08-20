<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialTemplateParser" %>
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

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<!-- The title, stylesheet, help popup function, and meta tags -->
<jsp:include page="metas.html"/>
<head>
</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> <font class="blueText">(3) Create Specimens</font>
        -> (4) Upload Data -> (5) Select Protocol -> (6) Confirm Your Submission -> (7) Completion
    </p>

    <h3>Please fill in the details of your specimen</h3>

    <form name="selectMetadata" action="enterSpecimenValidate.jsp" method="post">
        <fieldset>
            <legend>New Specimen</legend>

            <%
                ActionHierarchyScheme ahs = new ActionHierarchyScheme();

                if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                    // base the form on the existing specimen
                    out.println( MaterialTemplateParser.createSpecimenFormContents( validUser,
                            symbaFormSessionBean.getSpecimenToBeUploaded(),
                            request.getParameter( ahs.getElementTitle() ),
                            false ) );
                } else {
                    ahs.setDummy( true );
                    if ( request.getParameter( ahs.getElementTitle() ) != null ) {
                        // base the form on the existing specimen
                        out.println( MaterialTemplateParser.createSpecimenFormContents( validUser,
                                symbaFormSessionBean.getSpecimenToBeUploaded(),
                                request.getParameter( ahs.getElementTitle() ), true ) );
                    }
                }
            %>
        </fieldset>
        <br/>
        <fieldset class="submit">
            <input type="submit" value="Submit" onclick="disabled=true"/>
        </fieldset>
    </form>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
