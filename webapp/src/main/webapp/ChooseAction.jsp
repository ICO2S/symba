<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
This file is part of SyMBA.
SyMBA is covered under the GNU Lesser General Public License (LGPL).
Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
To view the full licensing information for this software and ALL other files contained
in this distribution, please see LICENSE.txt
--%>
<!-- $LastChangedDate: 2008-01-29 16:25:14 +0000 (Tue, 29 Jan 2008) $-->
<!-- $LastChangedRevision: 50 $-->
<!-- $Author: allysonlister $-->
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp/src/main/webapp/metaData.jsp $-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="fugeOM.service.RealizableEntityServiceException" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanProtocolCollectionHelper" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session"/>

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


<!-- We have chosen to separate the selection of a factor (aka generic action of the top-level protocol)
    so that the appropriate action can be taken on the next page if there is a dummy GenericParameter
    in the generic action chosen on this page.
-->
<form name="selectProtocol" action="ChooseActionValidate.jsp" method="post">

<fieldset>
<legend>Please select the factors appropriate to your data:
    <a href="help.jsp#protocol" onClick="return popup(this, 'notes')">[ Help ]</a></legend>

<ol>
<%

    // re-print the existing values if present
    if ( symbaFormSessionBean.isProtocolLocked() ) {
        for ( int iii = 0; iii < symbaFormSessionBean.getDatafileSpecificMetadataStores().size(); iii++ ) {
            // if there is a template store, then set session variables FOR THIS PAGE ONLY for the ones that are only in
            //  the templateStore right now. The rest will get filled normally in metaDataValidate.jsp
            if ( session.getAttribute( "templateStore" ) != null ) {
                DatafileSpecificMetadataStore info = ( DatafileSpecificMetadataStore ) session.getAttribute(
                        "templateStore" );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenActionEndurant( info.getChosenActionEndurant() );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenChildProtocolEndurant( info.getChosenChildProtocolEndurant() );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenChildProtocolName( info.getChosenChildProtocolName() );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenSecondLevelActionEndurant( info.getChosenSecondLevelActionEndurant() );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenSecondLevelChildProtocolEndurant( info.getChosenSecondLevelChildProtocolEndurant() );
                symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .setChosenSecondLevelChildProtocolName( info.getChosenSecondLevelChildProtocolName() );
            }
            if ( symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getChosenChildProtocolName() !=
                    null ) {
                out.println( "<ol><li>" );
                out.println( "<strong>" );
                // oldFilename will not be in the template, but alwaysin the session value
                out.println(
                        symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename() + ": " );
                out.println( "</strong><br/>" );
                out.println( "You have already chosen the following protocol: " );
                out.println( "<strong>" );
                out.println(
                        symbaFormSessionBean.getDatafileSpecificMetadataStores()
                                .get( iii )
                                .getChosenChildProtocolName() + ". " );
                out.println( "</strong><br/>" );
                if ( symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( iii )
                        .getChosenSecondLevelChildProtocolName() != null ) {
                    out.println( "<br/>" );
                    out.println( "You have already chosen the following child of the above protocol: " );
                    out.println( "<strong>" );
                    out.println(
                            symbaFormSessionBean.getDatafileSpecificMetadataStores()
                                    .get( iii )
                                    .getChosenSecondLevelChildProtocolName() + ". " );
                    out.println( "</strong><br/>" );
                }
                out.println( "You can no longer change anything on this page. If you have made a mistake," );
                out.println( "and wish to start over again, please " );
                out.println( "<a href=\"beginNewSession.jsp\">start again from the beginning</a>.<br/>" );
                out.println( "</li>" );
                out.println( "</ol>" );
            } else {
                // oldFilename will not be in the template, but alwaysin the session value
                out.println(
                        "Error trying to retrieve the protocol for the file: " +
                                symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename() );
                out.println( "Please send this message to " + application.getAttribute( "helpEmail" ) + "<br/>" );
            }
        }
    } else {
        for ( int iii = 0; iii < symbaFormSessionBean.getDatafileSpecificMetadataStores().size(); iii++ ) {

            // selectAmongTopLevelActions will be filled with the actions provided directly inside the top-level protocol
            String selectAmongTopLevelActions = "actionList" + iii;
            List<String> selectAmongTopLevelActionsList = new ArrayList<String>();

            // selectAmongLowerLevelActions will be filled with the actions provided in the protocols listed as actions in the top-level protocol
            String selectAmongLowerLevelActions = "actionListFactor" + iii;
            List<String> selectAmongLowerLevelActionsList = new ArrayList<String>();

            // Print out any protocol that is present as a GenericAction in another protocol, and can therefore
            // have output/input files.

            // Start by identifying all protocols that live underneath the top-level protocol
            CisbanProtocolCollectionHelper cpc = new CisbanProtocolCollectionHelper(
                    validUser.getReService(), new CisbanIdentifiableHelper(
                    validUser.getReService(), new CisbanDescribableHelper( validUser.getReService() ) ) );

            // loop through these protocols. If they are actions of the top-level protocol, put them into the select
            // menu for selectAmongTopLevelActions. If they are actions one level below those of the top-level protocol, put them in
            // the select menu for selectAmongLowerLevelActions.
//        out.println(
//                "Searching for child protocols for top level protocol " + symbaFormSessionBean.getTopLevelProtocolEndurant() +
//                        " with name " + symbaFormSessionBean.getTopLevelProtocolName() + "<br/>" );
            try {
                GenericProtocol topLevelProtocol = ( GenericProtocol ) validUser.getReService()
                        .findLatestByEndurant( symbaFormSessionBean.getTopLevelProtocolEndurant() );
                Set<Protocol> associatedProtocols = cpc.getChildProtocols( topLevelProtocol, null );

                // always add the top-level protocol, as the getChildProtocols method does not do this.
                associatedProtocols.add( topLevelProtocol );

//        out.println( "Number of associated protocols: " + associatedProtocols.size() + "<br/>" );

                for ( Protocol protocol : associatedProtocols ) {

                    GenericProtocol gp = ( GenericProtocol ) protocol;

//            out.println(
//                    "Associated protocol: " + gp.getEndurant().getIdentifier() +
//                            " with name " + gp.getName() + "<br/>" );

                    // unchecked cast warning provided by javac when using generics in Lists/Sets and
                    // casting from Object, even though runtime can handle this.
                    // see http://forum.java.sun.com/thread.jspa?threadID=707244&messageID=4118661
                    @SuppressWarnings( "unchecked" )
                    Set<GenericAction> genericActions = ( Set<GenericAction> ) gp.getGenericActions();

                    for ( int count = 1; count <= genericActions.size(); count++ ) {
                        for ( GenericAction genericAction : genericActions ) {
                            if ( count == genericAction.getActionOrdinal() ) {
                                // If it is an action of the top-level protocol, print out the name in the
                                // selectAmongTopLevelActions menu. If it is such an action, then the containing
                                // protocol will match the symbaFormSessionBean.getTopLevelProtocolName()
                                if ( gp.getName().trim()
                                        .equals( symbaFormSessionBean.getTopLevelProtocolName().trim() ) ) {
                                    String modified = genericAction.getName();
                                    modified = modified.trim();
                                    if ( modified.startsWith( "Step Containing the" ) ) {
                                        modified = modified.substring( 20 );
                                    }

                                    String inputStartValue =
                                            "<option value= \"" + genericAction.getEndurant().getIdentifier() + "::" +
                                                    genericAction.getGenProtocolRef().getEndurant().getIdentifier() +
                                                    "::" +
                                                    genericAction.getGenProtocolRef().getName() + "\"";
                                    selectAmongTopLevelActionsList.add(
                                            inputStartValue + ">" + modified + "</option>" );
                                }
                                // Otherwise, print out the name in the selectAmongLowerLevelActions menu.
                                // Currently only works for Actions that are no more than one removed from the top-level actions.
                                else {
                                    String inputStartValue =
                                            "<option value= \"" + genericAction.getEndurant().getIdentifier() + "::" +
                                                    genericAction.getGenProtocolRef().getEndurant().getIdentifier() +
                                                    "::" +
                                                    genericAction.getGenProtocolRef().getName() + "\"";
                                    selectAmongLowerLevelActionsList.add(
                                            inputStartValue + ">" + genericAction.getName() + "</option>" );
                                }
                                break;
                            }
                        }
                    }
                }

                // now print out both select menus, where present. The top-level menu will always be present, but the
                // lower-level menu is not always there.

                // Top-Level Menu
                boolean hasLabel = false;
                out.println( "<li>" );
                out.println(
                        symbaFormSessionBean.getDatafileSpecificMetadataStores().get( iii ).getOldFilename() +
                                ": <br/>" );
                for ( String selectChoice : selectAmongTopLevelActionsList ) {
                    if ( !hasLabel ) {
                        out.println(
                                "<label for=\"" + selectAmongTopLevelActions +
                                        "\">Please select the protocol that produced your data:</label>" );
                        out.println( "<!-- Top Level -->" );
                        out.println(
                                "<select id=\"" + selectAmongTopLevelActions + "\" name=\"" +
                                        selectAmongTopLevelActions +
                                        "\">" );
                        hasLabel = true;
                    }
                    out.println( selectChoice );
                }
                // ensure that the select menu has been printed before closing it
                if ( hasLabel ) {
                    out.println( "</select>" );
                    out.println( "<br/>" );
                    out.println( "</li>" );
                } else {
                    out.flush();
                    out.println(
                            "<p class=\"bigger\">There has been an error listing all of the factors important to your data " +
                                    "file. Please contact the <a href=\"mailto:helpdesk@cisban.ac.uk\">helpdesk</a>, " +
                                    "letting us know what workflow you were trying to associate with your data file, and that it" +
                                    "had something to do with the top-level pull-down menu</p>" );
                }

                // Lower-Level Menu
                hasLabel = false;
                if ( !selectAmongLowerLevelActionsList.isEmpty() ) {
                    out.println( "<li>" );
                    for ( String selectChoice : selectAmongLowerLevelActionsList ) {
                        if ( !hasLabel ) {
                            out.println(
                                    "<label for=\"" + selectAmongLowerLevelActions +
                                            "\">Please select the child protocol of the protocol above that produced your data:" +
                                            "</label>" );
                            out.println( "<!-- Lower Level -->" );
                            out.println(
                                    "<select id=\"" + selectAmongLowerLevelActions + "\" name=\"" +
                                            selectAmongLowerLevelActions + "\">" );
                            hasLabel = true;
                        }
                        out.println( selectChoice );
                    }
                    // ensure that the select menu has been printed before closing it
                    if ( hasLabel ) {
                        out.println( "</select>" );
                        out.println( "<br/>" );
                        out.println( "</li>" );
                    } else {
                        out.flush();
                        out.println(
                                "<p class=\"bigger\">There has been an error listing all of the factors important to your data " +
                                        "file. Please contact the <a href=\"mailto:helpdesk@cisban.ac.uk\">helpdesk</a>, " +
                                        "letting us know what workflow you were trying to associate with your data file, and that it" +
                                        "had something to do with the lower-level pull-down menu</p>" );
                    }
                }
            } catch ( RealizableEntityServiceException e ) {
                out.println( "Error selecting your protocol options. Please send this message to" );
                out.println( application.getAttribute( "helpEmail" ) + "<br/>" );
                System.out.println( e.getMessage() );
                e.printStackTrace();
            }
        }
    }
%>
<br/>

</ol>
</fieldset>

<fieldset class="submit">
    <!--ONCLICK="disabled=true"-->
    <%
        // in this case, we only want to present them with this option if the have reached the
        // confirmation page. Otherwise, we want to force them to move forward linearly.
        // Presence of all data is shown with the variable symbaFormSessionBean.get
        if ( symbaFormSessionBean.isConfirmationReached() ) { %>
    Do you wish to make changes to the information associated with the above data? <br/>
    <input type="radio" name="go2confirm" class="reset-radio" value="true" checked="checked"/> <strong>I am
    happy with the entire submission: return to the Confirmation Page</strong><br/>
    <input type="radio" name="go2confirm" class="reset-radio" value="false"/><strong>I would like to continue on to
    the next form page</strong><br/>
    <%
        }
        out.println( "<input type=\"submit\" value=\"Submit\"/>" );
    %>
</fieldset>
</form>
<fieldset class="submit">
    <%--
     There should be no usage of the back button as history.go(-1) as this could lead to the wrong page. Instead,
     it must go to the previous jsp as a submit, so that the session details can be checked.
    --%>
    <form name="lookAtRawData" action="rawData.jsp" method="post">
        If you wish to go back to the previous form page, <strong>without saving the changes on this page</strong>,
        use the "Back" button. You will, in any case, have the chance to review what you've written in at the end of
        the form, before final submission.<br/>
        <input type="submit" value="Back"/>
    </form>
</fieldset>
<br/>
<jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>