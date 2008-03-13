<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate: 2008-01-29 16:25:14 +0000 (Tue, 29 Jan 2008) $-->
<!-- $LastChangedRevision: 50 $-->
<!-- $Author: allysonlister $-->
<!-- $HeadURL: https://symba.svn.sourceforge.net/svnroot/symba/trunk/webapp/src/main/webapp/metaData.jsp $-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanProtocolCollectionHelper" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

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

<h3>Please select the factors appropriate to your data file: <a
        href="help.jsp#protocol"
        onClick="return popup(this, 'notes')">[ Help ]</a></h3>

<!-- We have chosen to separate the selection of a factor (aka generic action of the top-level protocol)
    so that the appropriate action can be taken on the next page if there is a dummy GenericParameter
    in the generic action chosen on this page.
-->
<form ENCTYPE="multipart/form-data" name="selectProt" action="ChooseActionValidate.jsp" method="post">

<%

    for ( int iii = 0; iii < investigationBean.getAllDataBeans().size(); iii++ ) {

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
//                "Searching for child protocols for top level protocol " + investigationBean.getTopLevelProtocolIdentifier() +
//                        " with name " + investigationBean.getTopLevelProtocolName() + "<br/>" );
        GenericProtocol topLevelProtocol = ( GenericProtocol ) validUser.getReService()
                .findLatestByEndurant( investigationBean.getTopLevelProtocolIdentifier() );
        Set<Protocol> associatedProtocols = cpc.getChildProtocols( topLevelProtocol, null );

        // always add the top-level protocol, as the getChildProtocols method does not do this.
        associatedProtocols.add( topLevelProtocol );

//        out.println( "Number of associated protocols: " + associatedProtocols.size() + "<br/>" );

        for ( Protocol protocol : associatedProtocols ) {

            GenericProtocol gp = ( GenericProtocol ) protocol;

//            out.println(
//                    "Associated protocol: " + gp.getEndurant().getIdentifier() +
//                            " with name " + gp.getName() + "<br/>" );

            Set<GenericAction> genericActions = ( Set<GenericAction> ) gp.getGenericActions();

            for ( int count = 1; count <= genericActions.size(); count++ ) {
                for ( GenericAction genericAction : genericActions ) {
                    if ( count == genericAction.getActionOrdinal() ) {
                        // If it is an action of the top-level protocol, print out the name in the
                        // selectAmongTopLevelActions menu. If it is such an action, then the containing
                        // protocol will match the investigationBean.getTopLevelProtocolName()
                        if ( gp.getName().trim()
                                .equals( investigationBean.getTopLevelProtocolName().trim() ) ) {
                            String modified = genericAction.getName();
                            modified = modified.trim();
                            if ( modified.startsWith( "Step Containing the" ) ) {
                                modified = modified.substring( 20 );
                            }

                            selectAmongTopLevelActionsList.add(
                                    "<option value= \"" + genericAction.getEndurant().getIdentifier() + "::" +
                                            genericAction.getGenProtocolRef().getEndurant().getIdentifier() + "::" +
                                            genericAction.getGenProtocolRef().getName() + "\">" +
                                            modified +
                                            "</option>" );
                        }
                        // Otherwise, print out the name in the selectAmongLowerLevelActions menu.
                        // Currently only works for Actions that are no more than one removed from the top-level actions.
                        else {
                            selectAmongLowerLevelActionsList.add(
                                    "<option value= \"" + genericAction.getEndurant().getIdentifier() + "\">" +
                                            genericAction.getName() +
                                            "</option>" );
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
        for ( String selectChoice : selectAmongTopLevelActionsList ) {
            if ( !hasLabel ) {
                out.println(
                        "<label for=\"" + selectAmongTopLevelActions +
                                "\">Please select your factor (Top Level):</label>" );
                out.println( "<select name=\"" + selectAmongTopLevelActions + "\">" );
                hasLabel = true;
            }
            out.println( selectChoice );
        }
        // ensure that the select menu has been printed before closing it
        if ( hasLabel ) {
            out.println( "</select>" );
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
            for ( String selectChoice : selectAmongLowerLevelActionsList ) {
                if ( !hasLabel ) {
                    out.println(
                            "<label for=\"" + selectAmongLowerLevelActions +
                                    "\">Please select your factor (Lower Level):</label>" );
                    out.println( "<select name=\"" + selectAmongLowerLevelActions + "\">" );
                    hasLabel = true;
                }
                out.println( selectChoice );
            }
            // ensure that the select menu has been printed before closing it
            if ( hasLabel ) {
                out.println( "</select>" );
            } else {
                out.flush();
                out.println(
                        "<p class=\"bigger\">There has been an error listing all of the factors important to your data " +
                                "file. Please contact the <a href=\"mailto:helpdesk@cisban.ac.uk\">helpdesk</a>, " +
                                "letting us know what workflow you were trying to associate with your data file, and that it" +
                                "had something to do with the lower-level pull-down menu</p>" );
            }
        }
    }

%>
<br/><br/>
<!--ONCLICK="disabled=true"-->
<input type="submit" value="Submit"/>
<input type="button" value="Back" onclick="history.go(-1)"/>
</form>
<br/>
<jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>