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

<%@ page import="net.sourceforge.symba.webapp.util.forms.MetaDataWrapper" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<!-- The title, stylesheet, help popup function, and meta tags -->
<jsp:include page="metas.html"/>
<head>
    <script language="javascript">
        var treatment_number = 1;
    </script>

</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> (3) Upload Data ->
        <font class="blueText">(4) Select Protocol</font> -> (5) Confirm Your Submission -> (6) Completion and Download
    </p>

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <h3>Please select the factors appropriate to your data file: <a
            href="help.jsp#protocol"
            onClick="return popup(this, 'notes')">[ Help ]</a></h3>

    <%--
    Ensure you do not have a multipart/form-data for the form's ENCTYPE if you wish to use
    POST, and then a request.getParameter() in the following page. It will cause all request.getParameter()
    to return null. See this page for more details:
    http://www.programmersheaven.com/mb/jsp/363016/363016/ReadMessage.aspx
    --%>
    <form name="selectMetadata" action="metaDataValidate.jsp" method="post">
        <!--Will store info if new term is requested -->
        <%
            out.println( MetaDataWrapper.createMetaDataFormContents( symbaFormSessionBean, validUser, session ) );
        %>
        <br/>
        <fieldset class="submit">
            <%
                // no need to check the go2confirm value, as the next step is the confirm page anyway
                // in this case, we only want to present them with this option if the have reached the
                // confirmation page. Otherwise, we want to force them to move forward linearly.
                // Presence of all data is shown with the variable symbaFormSessionBean.get
                if ( symbaFormSessionBean.isConfirmationReached() ) {
                    out.println( "Return to the Confirmation Page by clicking on the \"Submit\" button below. <br/>" );
                }
                /**
                 There should be no usage of the back button as history.go(-1) as this could lead to the wrong page. Instead,
                 it must go to the previous jsp as a submit, so that the session details can be checked.
                 **/
            %>
            <input type="submit" value="Submit"/>
        </fieldset>
    </form>
    <fieldset class="submit">
        <form name="lookAtChooseAction" action="ChooseAction.jsp" method="post">
            If you wish to go back to the previous form page, <strong>without saving the changes on this page</strong>,
            use
            the "Back" button. You will, in any case, have the chance to review what you've written in at the end of the
            form, before final submission.<br/>
            <input type="submit" value="Back"/>
        </form>
    </fieldset>

    <p>If a required factor for your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a>
    </p>

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>
</body>
</html>
