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

<%@ page import="fugeOM.Common.Ontology.OntologyTerm" %>
<%@ page import="fugeOM.Common.Protocol.GenericAction" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.RawDataInfoBean" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<jsp:useBean id="experiment" class="uk.ac.cisban.symba.webapp.util.ExperimentBean" scope="session">
</jsp:useBean>

<jsp:useBean id="rdb" class="uk.ac.cisban.symba.webapp.util.RawDataBean" scope="session">
</jsp:useBean>

<jsp:useBean id="counter" class="uk.ac.cisban.symba.webapp.util.CounterBean" scope="application">
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
    (4) Select Protocol -> <font class="blueText">(5) Confirm Your Submission</font> -> (6) Completion and Download
</p>

<p>


<h2>Your data is ready to be submitted to the repository <a
        href="help.jsp#confirmSubmission"
        onClick="return popup(this, 'notes')">[ Help ]</a></h2>

<p class="bigger">
    <font color="red">
        However, your changes are NOT saved until you click the "Confirm All" button below!
    </font>
</p>

<p class="bigger">
    Please check all of your details. Click on any item you wish to modify, which will take you back to the
    appropriate form page, where you can correct any mistakes.
</p>

<!--experiment bean-->
<%
    if ( experiment.getFuGE() == null ) {
%>
<p class="bigger">
    The experiment you are loading has the following details:
    <%
            out.println( "<ul>" );
            if ( experiment.getExperimentName() != null ) {
                out.println(
                        "<li>Name: <a class=\"bigger\" href=\"newExperiment.jsp\">" +
                                experiment.getExperimentName() +
                                "</a></li>" );
            }
            if ( experiment.getHypothesis() != null ) {
                out.println(
                        "<li>Hypothesis: <a class=\"bigger\" href=\"newExperiment.jsp\">" +
                                experiment.getHypothesis() +
                                "</a></li>" );
            }
            if ( experiment.getConclusion() != null ) {
                out.println(
                        "<li>Conclusions: <a class=\"bigger\" href=\"newExperiment.jsp\">" +
                                experiment.getConclusion() + "</a></li>" );
            }
            out.println( "</ul>" );
        }
    %>
</p>
<!--raw data bean-->
<p class="bigger">
    This experiment has the following data file(s) for your
    <%
        out.println( " <a class=\"bigger\" href=\"rawData.jsp\">" );
        out.println( rdb.getDataType() );
        out.println( "</a>:" );
    %>
</p>
<%
    for ( int iii = 0; iii < rdb.getAllDataBeans().size(); iii++ ) {
        RawDataInfoBean info = rdb.getDataItem( iii );
        out.println( "<hr/>" );
        out.println( "<p class=\"bigger\">Information for " );
        out.println( " <a class=\"bigger\" href=\"rawData.jsp\">" );
        out.println( info.getFriendlyId() );
        out.println( "</a>" );
        out.println( "</p>" );
        out.println( "<ul>" );
        if ( info.getDataName() != null && info.getDataName().length() > 0 ) {
            out.println( "<li>" );
            out.println( "You have described this file as follows: " );
            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
            out.println( info.getDataName() );
            out.println( "</a>" );
            out.println( "</li>" );
        }
        if ( info.getFactorChoice() != null && info.getFactorChoice().length() > 0 ) {
            GenericAction ga = ( GenericAction ) validUser.getReService()
                    .findLatestByEndurant( info.getFactorChoice() );
            out.println( "<li>" );
            out.println(
                    "Your workflow also required that you specify a factor associated with " +
                            "your data file. " );
            out.println( "The factor you have chosen is " );
            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
            String modified = ga.getName().trim();
            if ( modified.startsWith( "Step Containing the" ) ) {
                modified = modified.substring( 20 );
            }
            out.println( modified );
            out.println( "</a>" );
            out.println( "</li>" );
        }
        if ( info.getActionEndurant() != null && info.getActionEndurant().length() > 0 ) {
            GenericAction ga = ( GenericAction ) validUser.getReService()
                    .findLatestByEndurant( info.getActionEndurant() );
            out.println( "<li>" );
            out.println( "You have also assigned the data file to a particular step in your " );
            out.println( " workflow. The step you have assigned the file to is " );
            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
            String modified = ga.getName();
            if ( modified.startsWith( "Step Containing the" ) ) {
                modified = modified.substring( 20 );
            }
            out.println( modified );
            out.println( "</a>" );
            out.println( "</li>" );
        }
        if (info.getFileFormat() != null) {
            out.println( "<li>You have specified a file format for the external data file: " );
            out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
            out.println( info.getFileFormat());
            out.println( "</a>" );
            out.println( "</li>" );
        }
        if ( info.getMaterialFactorsBean() != null ) {
            out.println( "<li>" );
            out.println( "You have provided information about the material used in the experiment. " );
            out.println( "This material has the following properties " );
            out.println( "<ul>" );

            if ( info.getMaterialFactorsBean().getMaterialName() != null && info.getMaterialFactorsBean().getMaterialName().length() > 0) {
                out.println( "<li>Name/Identifying Number: " );
                out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                out.println( info.getMaterialFactorsBean().getMaterialName() );
                out.println( "</a>" );
                out.println( "</li>" );
            }

            if ( info.getMaterialFactorsBean().getMaterialType() != null ) {
                out.println( "<li>Material Type: " );
                out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                OntologyTerm ot = ( OntologyTerm ) validUser.getReService()
                        .findLatestByEndurant( info.getMaterialFactorsBean().getMaterialType() );
                out.println( ot.getTerm() );
                out.println( "</a>" );
                out.println( "</li>" );
            }

            if ( info.getMaterialFactorsBean().getTreatmentInfo() != null && !info.getMaterialFactorsBean().getTreatmentInfo().isEmpty() ) {
                out.println( "<li>Treatments: " );
                out.println( "<ol>" );
                for ( String treatment : info.getMaterialFactorsBean().getTreatmentInfo() ) {
                    out.println( "<li>" );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    out.println( treatment );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }
                out.println( "</ol>" );
                out.println( "</li>" );
            }
            if ( info.getMaterialFactorsBean().getCharacteristics() != null && !info.getMaterialFactorsBean().getCharacteristics().isEmpty() ) {
                out.println( "<li>General Characteristics: " );
                out.println( "<ol>" );
                for ( String characteristics : info.getMaterialFactorsBean().getCharacteristics() ) {
                    out.println( "<li>" );
                    out.println( "<a class=\"bigger\" href=\"metaData.jsp\">" );
                    OntologyTerm ot = ( OntologyTerm ) validUser.getReService()
                            .findLatestByEndurant( characteristics );
                    out.println( ot.getTerm() );
                    out.println( "</a>" );
                    out.println( "</li>" );
                }
                out.println( "</ol>" );
                out.println( "</li>" );
            }
            out.println( "</ul>" );
        }
        out.println( "</li>" );
        out.println( "</ul>" );
    }
%>

<p class="bigger">
    <font color="red">
        Remember, your changes are NOT saved until you click the "Confirm All" button below!
    </font>
</p>

<p class="bigger">
    <font color="red">
        Uploading large files may take several minutes.
    </font>
</p>

<FORM ACTION="fugeCommit.jsp" METHOD=POST>
    <input type="submit" value="CONFIRM ALL"/>
</FORM>
<br>

<jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>

</html>


