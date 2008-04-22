<%@ page import="fugeOM.Common.Ontology.OntologySource" %>
<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.*" %>
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
    <p>

    <h3>Search for experiments <a
            href="help.jsp#search"
            onClick="return popup(this, 'notes')"> [ Help ] </a></h3>

    <h4>Please select the search appropriate for your query</h4>

    <form action="ShowBasicResults.jsp" method="get">
        <p>Please click "Show All" if you wish to view all of the experiments in the database. Please be aware that this may
            take some time to retrieve.</p>
        <!-- The id attribute is used internally for labels, etc, while the name attribute is passed to the receiving page -->
        <input type="hidden" name="showAll" value="showAll"/>
        <input type="submit" value="Show all"/>
    </form>
    <br/>
    <hr/>

    <form action="ShowBasicResults.jsp" method="get">
        <p>Please click "Show Yours" if you wish to view all of your experiments.</p>
        <!-- The id attribute is used internally for labels, etc, while the name attribute is passed to the receiving page -->
        <input type="hidden" name="showYours" value="showYours"/>
        <input type="submit" value="Show Yours"/>
    </form>
    <br/>
    <hr/>

    <form action="ShowBasicResults.jsp" method="get">
        <p>Please put the word you wish to search for in the text field below. Partial matches
            to your search term will also be found.</p>
        <br/>
        <input id="experimentName" name="experimentName"/>
        <input type="submit" value="Search"/>
    </form>
    <br/>
    <br/>
    <%
        // Search based on OntologyTerm. First, provide pull-downs grouped by OntologySource

        // Get a list of all of the latest ontology sources
        List<OntologySource> ontologySources = ( List<OntologySource> ) validUser.getReService()
                .getAllLatestOntologySources();

        // Go through each source, retrieving all terms associated with it and putting those terms in
        // a pull-down menu.
//        out.println( ontologySources.size() + " Sources Found.<br/>" );
        for ( OntologySource ontologySource : ontologySources ) {
//            out.println( ontologySource.getName() + "<br/>" );
            List<OntologyTerm> ontologyTerms = ( List<OntologyTerm> ) validUser.getReService()
                    .getAllLatestTermsWithSource( ontologySource.getEndurant().getIdentifier() );
//            out.println( ontologyTerms.size() + " Terms Found.<br/>" );
            List<String> ids = new ArrayList<String>();
            List<String> names = new ArrayList<String>();
            // for some reason, not all ontology terms get displayed in the pull-down menu if we iterate through
            // the OntologyTerms directly with the out.println statements. Therefore instead, we store the values
            // we're interested in and then display them after storage.
            for ( OntologyTerm ontologyTerm : ontologyTerms ) {
                ids.add( ontologyTerm.getEndurant().getIdentifier() );
                names.add( ontologyTerm.getTerm() );
            }
            String modifiedSourceName = ontologySource.getName();
            out.println( "<p>Please select your " + modifiedSourceName + "</p>" );
            out.println( "<form action=\"ShowBasicResults.jsp\" method=\"get\">" );
            out.println( "<select name=\"ontologyTerm\">" );
            int counter = 0;
            for ( String id : ids ) {
                out.println(
                        "<option value=\"" + id + "\">" +
                                names.get(counter) +
                                "</option>" );
                counter++;
            }
            out.println( "</select>" );
            out.println( "<input type=\"submit\" value=\"Search\"/>" );
            out.println("</form>");
            out.println( "<br/>" );
            out.println( "<hr/>" );
        }

        // Now, list all OntologyTerms that do not have a source in a single pull-down menu.
        List<OntologyTerm> unsourcedTerms = ( List<OntologyTerm> ) validUser.getReService()
                .getAllLatestUnsourcedOntologyTerms();
        out.println(
                "<form action=\"ShowBasicResults.jsp\" method=\"get\">\n" +
                        "<p>Please select from the following keywords</p>" );
        out.println( "<select name=\"ontologyTerm\">" );
        for ( OntologyTerm ontologyTerm : unsourcedTerms ) {
            out.println(
                    "<option value=\"" + ontologyTerm.getEndurant().getIdentifier() + "\">" +
                            ontologyTerm.getTerm() +
                            "</option>" );
        }
        out.println( "</select>" );
        out.println( "<br/>" );
        out.println( "<input type=\"submit\" value=\"Search\"/>" );
        out.println( "</form>" );
        out.println( "<br/>" );
    %>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
