<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate:$-->
<!-- $LastChangedRevision:$-->
<!-- $Author:$-->
<!-- $HeadURL:$-->

<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

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
            <input type="submit" value="Show all"/>
        </form>
        <!--
        <form action = "form.jsp" method = "get">
        Investigator : &nbsp;&nbsp;&nbsp;&nbsp;
        <select name="investigator">
        <option value="Olly Shaw">Olly Shaw</option>
        <option value="Ally Lister">Ally Lister</option>
        <option value="Anil Wipat">Anil Wipat</option>
        </select>
        <br>
        Instrument used :
        <select name="instrument">
        <option value="Big stick">Big stick</option>
        <option value="Little stick">Little stick</option>
        <option value="Cardboard box">Cardboard box</option>
        </select>
        <br>
        Hypothesis string : &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="text" name = "hypothesis"/>
        <input type="submit" value = "Submit"/>
        </form>
        -->
    </p>

    <br>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
