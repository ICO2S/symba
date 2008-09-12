<%-- 
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt

This file processes which Investigation the user wishes to update / create. Originally, this was also the time at
which the data files were added. However, now that we are allowing material transformations as well as data
creation (assay) protocols to be filled in, we cannot ask for the data file until we know it is a protocol that
creates them.
--%>
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocol" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>

    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>

</head>
<body>

<jsp:include page="visibleHeader.jsp"/>

<div id="Content">
<p>
    (1) Introduction -> <font class="blueText">(2) Attach to an Experiment</font> -> (3) Create Specimens
    -> (4) Upload Data -> (5) Select Protocol -> (6) Confirm Your Submission -> (7) Completion
</p>

<c:if test="${param.errorMsg != null}">
    <font color="red">
        <c:out value="${param.errorMsg}"/>
    </font>
    <br/>
    <br/>
</c:if>

<p class="bigger">All submitted files must be of the same investigation type and belong to the same experiment.</p>

<form action="chooseInvestigationValidate.jsp" method="post">

    <fieldset>

        <%
            // If the user is trying to re-enter data at this stage when they have already parse assay protocols, we know
            // that they are not allowed to change anything in in this page. Therefore we can skip all of this, and just
            // print a summary.
            if ( symbaFormSessionBean.isProtocolLocked() ) {

                if ( symbaFormSessionBean.getTopLevelProtocolName() != null ) {

                    // they have submitted at least as far as ChooseAction.jsp already. Do not change anything in the
                    // session, and carry on to the next requested page.
                    out.println( "<legend>Please Review Your Choice of Investigation</legend>" );
                    out.println( "<ol><li>" );
                    out.println( "You have already chosen to add your data file to the " );
                    out.println( "<strong>" );
                    out.println( symbaFormSessionBean.getTopLevelProtocolName() + ". " );
                    out.println( "</strong>" );
                    out.println( "</li>" );
                } else {
                    out.println( "There has been an error processing your form. Please contact " );
                    out.println( application.getAttribute( "helpEmail" ) + "<br/>" );
                }

            } else {

                // Protocol isn't locked yet, therefore we can let them choose an investigation type
                out.println( "<legend>Please Choose Your Investigation" );
                out.println(
                        "<a href=\"help.jsp#rawData\" onClick=\"return popup(this, 'notes')\"> [ Why? ]</a></legend>" );
        %>
        <ol>
            <li>
                <label for="investigationType">Investigation Type: </label>
                <select id="investigationType" name="investigationType">
                    <%
                        // There will never be Protocol Dummies.
                        Map<String, String> topLevelNames = new HashMap<String, String>();
                        for ( Object obj : validUser.getSymbaEntityService().getLatestGenericProtocols() ) {
                            GenericProtocol gp = ( GenericProtocol ) obj;
                            if ( !gp.getName().contains( "Component" ) ) {
                                topLevelNames.put( gp.getEndurant().getIdentifier(), gp.getName() );
                            }
                        }
                        // Now add the option element to the HTML
                        for ( String key : topLevelNames.keySet() ) {
                            String inputStartValue =
                                    "<option value=\"" + key + "::Identifier::" + topLevelNames.get( key ) + "\"";
                            if ( symbaFormSessionBean.getTopLevelProtocolEndurant() != null &&
                                 key.equals( symbaFormSessionBean.getTopLevelProtocolEndurant() ) ) {
                                out.println(
                                        inputStartValue + " selected=\"selected\">" + topLevelNames.get( key ) +
                                        "</option>" );
                            } else {
                                out.println( inputStartValue + ">" + topLevelNames.get( key ) + "</option>" );
                            }
                        }
                    %>
                </select><br>
            </li>
            <%

               }

            %>

    </fieldset>

    <fieldset class="submit">
        <!--ONCLICK="disabled=true"-->
        <%
            // in this case, we only want to present them with this option if they have reached the
            // confirmation page. Otherwise, we want to force them to move forward linearly.
            if ( symbaFormSessionBean.isConfirmationReached() ) { %>
        Do you wish to make changes to the information associated with the above data? You will, in any case, have the
        chance to review what you've written in at the end of the form, before final submission.<br/>
        <input type="radio" name="go2confirm" class="reset-radio" value="true" checked="checked"/> <strong>I am
        happy with the entire submission: return to the Confirmation Page</strong><br/>
        <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I would like to
        continue on to the next form page</strong><br/>
        <%
            }
            out.println( "<input type=\"submit\" value=\"Submit\" onclick=\"this.disabled=true\"/>" );
        %>
    </fieldset>
</form>
<fieldset class="submit">
    <%
        /**
         There should be no usage of the back button or history.go(-1) as this could lead to the wrong page. Instead,
         it must go to the previous jsp as a submit, so that the session details can be checked. However, in this case,
         you can reach chooseInvestigation from either newExperiment.jsp, experiment.jsp, or the view.jsp. In the case
         of the first (newExperiment.jsp), symbaFormSessionBean.getFugeIdentifier() will be null. In both of the second
         cases, it will not be null. Therefore you can set the value of the form action based on this.
         */
        if ( symbaFormSessionBean.getFugeIdentifier() == null ||
             symbaFormSessionBean.getFugeIdentifier().length() == 0 ) {
            out.println( "<form name=\"changeNewExperiment\" action=\"newExperiment.jsp\" method=\"post\">" );
        } else {
            out.println( "<form name=\"changeExistingExperiment\" action=\"experiment.jsp\" method=\"post\">" );
        }
    %>
    If you wish to go back to the previous form page, <strong>without saving the changes on this page</strong>,
    use the "Back" button below.<br/>
    <input type="submit" value="Back" onclick="disabled=true"/>
    <% out.println( "</form>" ); %>
</fieldset>
<br>

<p>If your protocol is not here please
    <%
        out.println( "<a href=\"mailto:" + application.getAttribute( "helpEmail" ) + "\">contact us</a>" );
    %>
</p>

<jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
