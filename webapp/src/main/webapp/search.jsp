<%@ page import="fugeOM.Bio.Data.ExternalData" %>
<%@ page import="fugeOM.Bio.Material.GenericMaterial" %>
<%@ page import="fugeOM.Collection.FuGE" %>
<%@ page import="fugeOM.Common.Audit.Person" %>
<%@ page import="fugeOM.Common.Description.Description" %>
<%@ page import="fugeOM.Common.Ontology.OntologySource" %>
<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.*" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanDescribableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanFuGEHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanIdentifiableHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.helper.CisbanProtocolCollectionHelper" %>
<%@ page import="uk.ac.cisban.symba.backend.util.conversion.xml.XMLMarshaler" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.*" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.*" %>
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

    <p>Please select your search criteria to review and retrieve your previous experiments</p>

    <form action="view.jsp" method="get">
        <p>Please click "Show All" if you wish to view all of your experiments.</p>
        <!-- The id attribute is used internally for labels, etc, while the name attribute is passed to the receiving page -->
        <input type="hidden" id="showAll" name="showAll"/>
        <input type="submit" value="Show all"/>
    </form>
    <br/>
    <br/>

    <form action="view.jsp" method="get">
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
        for ( OntologySource ontologySource : ontologySources ) {
            List<OntologyTerm> ontologyTerms = ( List<OntologyTerm> ) validUser.getReService()
                    .getAllLatestTermsWithSource( ontologySource.getEndurant().getIdentifier() );
            out.println(
                    "<form action=\"view.jsp\" method=\"get\">\n" + "<p>Please select your " +
                            ontologySource.getName() + "</p>" );
            out.println( "<select name=\"ontologyTerm\">" );
            for ( OntologyTerm ontologyTerm : ontologyTerms ) {
                out.println(
                        "<option value= \"" + ontologyTerm.getEndurant().getIdentifier() + "\">" +
                                ontologyTerm.getTerm() +
                                "</option>" );
            }
            out.println( "</select>" );
            out.println( "<input type=\"submit\" value=\"Search "+ ontologySource.getName() +"\"/>" );
            out.println( "<br/>" );
            out.println( "<hr/>" );
        }

        // Now, list all OntologyTerms that do not have a source in a single pull-down menu.
        List<OntologyTerm> unsourcedTerms = ( List<OntologyTerm> ) validUser.getReService()
                .getAllLatestUnsourcedOntologyTerms( );
        out.println(
                "<form action=\"view.jsp\" method=\"get\">\n" + "<p>Please select from within the list of ontology terms " +
                        "without a source ontology</p>" );
        out.println( "<select name=\"ontologyTerm\">" );
        for ( OntologyTerm ontologyTerm : unsourcedTerms ) {
            out.println(
                    "<option value= \"" + ontologyTerm.getEndurant().getIdentifier() + "\">" +
                            ontologyTerm.getTerm() +
                            "</option>" );
        }
        out.println( "</select>" );
        out.println( "<br/>" );
        out.println( "<input type=\"submit\" value=\"Search Terms Without a Source\"/>" );
    %>
    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
