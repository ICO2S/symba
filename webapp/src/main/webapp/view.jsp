<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<%--Imports so we can use the person object and the data portal utils --%>
<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanFuGEHelper" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.SecurityEngineInterrogator" %>
<%@ page import="java.io.PrintWriter" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
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
    <%
        if ( request.getParameter( "investigationID" ) != null &&
                request.getParameter( "investigationID" ).length() > 0 ) {

            // This combo should always return an access granted message if actually connected.
//        String role = "demo";
//        String action = "Write";
//        String resource = "urn:lsid:carmen.org:file:1";

            SecurityEngineInterrogator interrogator = new SecurityEngineInterrogator();

            boolean passedSecurity = false;

            try {
                // everyone gets assigned to the allUsers role.
                passedSecurity = interrogator.hasPermission(
                        "symbaAllUsers", request.getParameter( "investigationID" ), "read" );
                // if allUsers are not allowed, check to see if the role for this particular user allows access
                if ( !passedSecurity ) {
                    passedSecurity = interrogator.hasPermission(
                            validUser.getEndurantLsid(), request.getParameter( "investigationID" ), "read" );
                }
            } catch ( Exception e ) {
                out.println( "<h3>" );
                out.println( "There has been an error processing the permissions associated with your selected" );
                out.println( "investigation." );
                out.println( "If you wish, you may <a class=\"bigger\" href=\"newOrExisting.jsp\">deposit" );
                out.println(
                        "some data</a>, or perform <a href=\"search.jsp\">another search</a>." );
                out.println( "</h3>" );
                out.println( e.getMessage() );
                e.printStackTrace();
            }

            if ( passedSecurity ) {
                // Ok to print out the experiment
                out.println( "For further searches, please visit our <a href=\"search.jsp\">Search Page</a><br/>" );
                out.println( "<h2>Your Data is shown below <a href=\"help.jsp#viewExperiments\"" );
                out.println( "onClick=\"return popup(this, 'notes')\"> [ Help? ]</a></h2>" );

                CisbanFuGEHelper cfh = new CisbanFuGEHelper( validUser.getReService() );
                // don't get latest version here: prettyHtml will get the latest version ONLY for those
                // objects it is displaying.
                FuGE fuge = ( FuGE ) validUser.getReService()
                        .findIdentifiable( request.getParameter( "investigationID" ) );
                cfh.prettyHtml( fuge, new PrintWriter( out ) );
                // Go directly to experimentValidate.jsp
                out.println( "<form action=\"experimentValidate.jsp\">" );
                out.println(
                        "<input type=\"hidden\" name=\"experimentList\" value=\"" +
                                fuge.getIdentifier() +
                                "\"/>" );
                out.println( "<input type=\"submit\" value=\"Add Data To This Experiment\"/>" );
                out.println( "</form>" );

                // View the xml format for this entry.
                out.println( "<form action=\"viewFugeML.jsp\">" );
                out.println(
                        "<input type=\"hidden\" name=\"endurant\" value=\"" + fuge.getEndurant().getIdentifier() +
                                "\"/>" );
                out.println( "<input type=\"submit\" size=\"10\" value=\"show XML\"/>" );
                out.println( "</form><br>" );
            } else {
                out.println( "<h3>" );
                out.println( "You do not have permission to view this experiment. Use your browser's \"Back\" button" );
                out.println( "to choose another experiment to view." );
                out.println( "Alternatively, you may <a class=\"bigger\" href=\"newOrExisting.jsp\">deposit" );
                out.println(
                        "some data</a>, or perform <a href=\"search.jsp\">another search</a>." );
                out.println( "</h3>" );

            }
        } else {
            out.println( "<h3>" );
            out.println( "This is the page that displays the results of a search." );
            out.println( "Please visit the <a href=\"search.jsp\">Search Page</a> to perform a search." );
            out.println( "</h3>" );

        }

    %>

    <br><br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>


