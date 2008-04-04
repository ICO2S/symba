<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<%@ page import="fugeOM.Common.Protocol.GenericProtocol" %>
<%@ page import="java.util.*" %>

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

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <p class="bigger">All submitted files must be of the same investigation type and belong to the same experiment.
        Large data files may take some time to upload.</p>


    <form ENCTYPE="multipart/form-data" action="rawDataVerify.jsp" method="post">

        <fieldset>
            <legend>Please Choose Your Investigation and Upload Your Data
                <a href="help.jsp#rawData" onClick="return popup(this, 'notes')"> [ Why? ]</a></legend>

            <ol>

                <li>
                    <label for="investigationType">Investigation Type: </label>
                    <select id="investigationType" name="investigationType">
                        <%
                            // There may be Protocol Dummies. How to ensure that non-dummy version of dummies don't
                            // show up here? A pre-filtering step is done, where only those that should be used are kept.
                            Map<String, String> topLevelNames = new HashMap<String, String>();
                            Set<String> dummies = new HashSet<String>();
                            for ( Object obj : validUser.getReService().getAllLatestGenericProtocols() ) {
                                GenericProtocol gp = ( GenericProtocol ) obj;
                                if ( !gp.getName().contains( "Component" ) ) {
                                    if ( gp.getName().contains( " Dummy" ) ) {
                                        dummies.add( gp.getName() );
                                    }
                                    topLevelNames.put( gp.getName(), gp.getEndurant().getIdentifier() );
                                }
                            }
                            // If there are any with " Dummy", remove all that don't have the " Dummy" and then remove the
                            // word "Dummy" before displaying.
                            for ( String currentDummy : dummies ) {
                                String fixedName = currentDummy.substring( 0, currentDummy.indexOf( " Dummy" ) ) + currentDummy.substring( currentDummy.indexOf( " Dummy" ) + 6 );
                                topLevelNames.remove( fixedName );
                                // now rename the existing dummy key by removing and adding with the new name
                                String value = topLevelNames.get( currentDummy );
                                topLevelNames.remove( currentDummy );
                                topLevelNames.put( fixedName, value );

                            }

                            // Now add the option element to the HTML
                            for ( String key : topLevelNames.keySet() ) {
                                out.println( "<option value=\"" + key + "::Identifier::" + topLevelNames.get( key ) + "\">" + key + "</option>" );
                            }
                        %>
                    </select><br>
                </li>

                <li>
                    <label for="attachment0">Your File: </label>
                    <input type="file" name="attachment0" id="attachment0"
                           onchange="document.getElementById('moreUploadsLink').style.display = 'block';"/>
                    <br>

                    <div id="moreUploads"/>
                    <br>

                    <div id="moreUploadsLink" style="display:none;">
                        <a href="javascript:addFileInput();">Attach Another File</a>
                    </div>
                    <br>
                </li>
            </ol>
        </fieldset>

        <fieldset class="submit">
            <!--ONCLICK="disabled=true"-->
            <input type="submit" value="Submit"/>
            <input type="button" value="Back" onclick="history.go(-1)"/>

            <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="file" name="afile"/>
            <br><br>

            <input type="submit" value = "Submit"/>
            --%>
        </fieldset>
    </form>
    <br>

    <p>If your protocol is not here please <a href="mailto:helpdesk@cisban.ac.uk">contact us</a></p>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
