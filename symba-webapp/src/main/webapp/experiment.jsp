<%@ page import="java.util.List" %>
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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<%-- The session is or isn't cleared (depending on the route the user has taken to get here)
 directly prior to the loading of this page --%>

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

    <form name="selectProt" action="experimentValidate.jsp">
        <fieldset>
            <legend>Select the experiment to which this data pertains:
                <a href="help.jsp#existingExperiment" onClick="return popup(this, 'notes')">
                    [ What Choice Should I Make? ]</a></legend>

            <ol>

                <li>
                    <label for="fugeIdentifier">Existing experiments: </label>
                    <select id="fugeIdentifier" name="fugeIdentifier">
                        <%
                            for ( Object obj : validUser.getSymbaEntityService()
                                    .getSummariesWithContact( validUser.getEndurantLsid() ) ) {
                                // unchecked cast warning provided by javac when using generics in Lists/Sets and
                                // casting from Object, even though runtime can handle this.
                                // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                                @SuppressWarnings( "unchecked" )
                                List<String> summary = ( List<String> ) obj;
                                String optionValueStart = "<option value=\"" + summary.get( 0 ) + "\"";
                                if ( symbaFormSessionBean.getFugeIdentifier() != null &&
                                     symbaFormSessionBean.getFugeIdentifier().equals( summary.get( 0 ) ) ) {
                                    optionValueStart += " selected=\"selected\"";
                                }
                                out.println( optionValueStart + ">" + summary.get( 1 ) + "</option>" );
                            }
                        %>
                    </select><br>
                </li>
            </ol>
        </fieldset>

        <fieldset class="submit">
            <%
                // in this case, we only want to present them with this option if the have reached the
                // confirmation page. Otherwise, we want to force them to move forward linearly.
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

    <jsp:include page="helpAndComments.jsp"/>

</div>
<jsp:include page="menu.jsp"/>

</body>
</html>
