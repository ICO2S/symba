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
    <h3>Please enter your data <a
            href="help.jsp#rawData"
            onClick="return popup(this, 'notes')"> [ Why? ]</a></h3>

    <p>All submitted files must be of the same data type</p>


    <form action="rawDataVerify.jsp">

        <br>
        <label for="dataType">Data Type: </label>

        <select name="dataType">

            <option value="Micro Array">Micro Array</option>
            <option value="2D gel">2D gel</option>
            <option value="SBML model">SBML model</option>
        </select><br>


        <label for="afile">Your 1st File: </label>
        <input type="file" id="afile" name="afile"><br>
        <label for="afile">Your 2nd File: </label>
        <input type="file" id="afile" name="afile"><br>

        <input type="submit" value="Submit"/>
        <input type="button" value="Back" onclick="history.go(-1)">


        <%--File : &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="file" name="afile"/>
        <br><br>

        <input type="submit" value = "Submit"/>
        --%>
    </form>

    <jsp:include page="helpAndComments.jsp"/>

</div>

<jsp:include page="menu.jsp"/>

</body>
</html>
