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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- The session is or isn't cleared (depending on the route the user has taken to get here)
 directly prior to the loading of this page --%>
<jsp:useBean id="symbaFormSessionBean" class="uk.ac.cisban.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<jsp:include page="visibleHeader.html"/>

<div id="Content">
    <p>
        (1) Introduction -> <font class="blueText">(2) Attach to an Experiment</font> -> (3) Upload Data ->
        (4) Select Protocol -> (5) Confirm Your Submission -> (6) Completion and Download
    </p>

    <h2>New Experiment <a href="help.jsp#newExperiment"
                          onClick="return popup(this, 'notes')"> [ What Is Classed As An Experiment? ]</a></h2>

    <c:if test="${param.errorMsg != null}">
        <font color="red">
            <c:out value="${param.errorMsg}"/>
        </font>
        <br/>
        <br/>
    </c:if>

    <form name="metaForm" action="newExperimentValidate.jsp" method="post">

        <fieldset>
            <legend>Please Name Your Experiment</legend>

            <ol>

                <li>
                    <label for="experimentName">Experiment Name:<em>required</em></label>
                    <input id="experimentName"
                           name="experimentName"
                    <%
                        if ( symbaFormSessionBean.getExperimentName() != null &&
                                symbaFormSessionBean.getExperimentName().length() > 0 ) {
                            out.println(
                                    "value=\"" + symbaFormSessionBean.getExperimentName() + "\">" );
                        } else {
                            out.println( ">" );
                        }
                    %>
                    <br>
                </li>

                <li>
                    <label for="hypothesis">Hypothesis:</label>
                    <textarea id="hypothesis" rows="5" cols="40" name="hypothesis"
                    <%
                        if ( symbaFormSessionBean.getHypothesis() != null &&
                                symbaFormSessionBean.getHypothesis().length() > 0 ) {
                            // putting the brackets here means non-essential whitespace is not shown
                            out.println( ">" + symbaFormSessionBean.getHypothesis() );
                        } else {
                            out.println( ">" );
                        }
                        out.println( "</textarea>" );
                    %>
                    <br>
                </li>

                <li>
                    <label for="conclusion">Conclusions:</label>
                    <textarea id="conclusion" rows="5" cols="40" name="conclusion"
                    <%
                        if ( symbaFormSessionBean.getConclusion() != null &&
                                symbaFormSessionBean.getConclusion().length() > 0 ) {
                            // putting the brackets here means non-essential whitespace is not shown
                            out.println(
                                    ">" + symbaFormSessionBean.getConclusion() + "</textarea>" );
                        } else {
                            out.println( "></textarea>" );
                        }
                    %>
                    <br>
                </li>
            </ol>
        </fieldset>

        <fieldset class="submit">
            <%
                // in this case, we only want to present them with this option if the have reached the
                // confirmation page. Otherwise, we want to force them to move forward linearly.
                // Presence of all data is shown with the variable symbaFormSessionBean.get
                if ( symbaFormSessionBean.isConfirmationReached() ) { %>
            Would you like to change more about your experiment, or go straight back to the
            confirmation page? Just make your choice and hit "Submit". <br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="true" checked="checked"/> <strong>I'm
            finished making changes: go back to the Confirmation Page</strong><br/>
            <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I'd like to make more
            changes: continue on to the next form page</strong><br/>
            <% } // don't allow the use of the back button in the first step. %>
            <input type="submit" value="Submit"/>
        </fieldset>
    </form>
    <br>

    <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="file" name="afile"/>
    <br><br>

    <input type="submit" value = "Submit"/>
    --%>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>
