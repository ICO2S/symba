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

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<%-- The correct doctype and html elements are stored here --%>
<jsp:include page="header.jsp"/>
<head>
    <!-- The title, stylesheet, help popup function, and meta tags -->
    <jsp:include page="metas.html"/>
</head>
<body>

<h1>JSP Page</h1>
<script language="javascript">
    function add_more_upload() {
        var new_total = Math.round(document.Uploader.TOTAL_FILE.value) + 1;
        document.getElementById('attach_' +
                                document.Uploader.TOTAL_FILE.value).innerhtml = "<input type='file' size='60' name='theFile[" +
                                                                                new_total +
                                                                                "]' /></p><p id='attach_" +
                                                                                new_total + "'>";
        document.Uploader.TOTAL_FILE.value++;
    }
    function addFile( b ) {
        if ( b && b.parentNode &&
             b.parentNode.insertBefore &&
             document.createElement ) {

            var fileInput = document.createElement('input');
            fileInput.type = 'file';
            fileInput.name = 'filename[]';

            b.parentNode.insertBefore(fileInput, b);

            b.parentNode.insertBefore(document.createElement(' br'), b);
        }
    }
</script>
<form action="rawDataVerify.jsp" name="Uploader">
    File to Upload: <p id='attach'><input type="file" size="60" name="theFile[0]"/></p>

    <p id='attach_0'></p>
    <input type="hidden" name="TOTAL_FILE" value="0"/><br/>
    <input type="submit" name="submit" value="Upload"/>
    <input type='button' value='Upload More' onClick="add_more_upload()"/>
</form>

<form name="myForm" method="post" enctype="multipart/form-data">
    File to Upload 2: <input type="file" name="filename[]"/>
    <br/>
    <input type="button" value="Add another file" onclick="addFile(this);"/>
</form>

</body>
</html>
