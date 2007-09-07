<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate:$-->
<!-- $LastChangedRevision:$-->
<!-- $Author:$-->
<!-- $HeadURL:$-->

<%@ page import="fugeOM.Common.Protocol.GenericProtocol" %>

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>

    <script language="javascript">
        var upload_number = 1;
    </script>

    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>

</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <p>
        (1) Introduction -> (2) Attach to an Experiment -> <font class="blueText">(3) Upload Data</font> ->
        (4) Select Protocol -> (5) Confirm Your Submission -> (6) Completion and Download
    </p>

    <H3>Please enter your data <a
            href="help.jsp#rawData"
            onClick="return popup(this, 'notes')"> [ Why? ]</A></H3>

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <p class="bigger">All submitted files must be of the same data type and belong to the same experiment, large data
        files may take some time to upload</p>


    <form ENCTYPE="multipart/form-data" action="rawDataVerify.jsp" method="post">

        <br>
        <label for="dataType">Investigation Type: </label>

        <select name="dataType">
            <%
                for ( Object obj : validUser.getReService().getAllLatestGenericProtocols() ) {
                    GenericProtocol gp = ( GenericProtocol ) obj;
                    if ( !gp.getName().contains( "Component" ) ) {
            %>
            <option value="<%out.println(gp.getName());%>"><%out.println( gp.getName() );%></option>
            <%
                    }
                }
            %>
        </select><br>


        <label for="attachment0">Your File: </label>
        <input type="file" name="attachment0" id="attachment0"
               onchange="document.getElementById('moreUploadsLink').style.display = 'block';"/>
        <br>

        <div id="moreUploads"></div>
        <br>

        <div id="moreUploadsLink" style="display:none;"><a href="javascript:addFileInput();">Attach Another File</a>
        </div>
        <br>
        <!--ONCLICK="disabled=true"-->
        <input type="submit" value="Submit"/>
        <input type="button" value="Back" onclick="history.go(-1)"/>

        <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="file" name="afile"/>
        <br><br>

        <input type="submit" value = "Submit"/>
        --%>
    </form>
    <br>

    <p>If your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
