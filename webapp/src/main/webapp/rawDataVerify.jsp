<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate:$-->
<!-- $LastChangedRevision:$-->
<!-- $Author:$-->
<!-- $HeadURL:$-->

<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>

<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.RawDataInfoBean" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.FileBean" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>

<%--
 Authors: Oliver Shaw, Allyson Lister
--%>
<!-- This include will validate the user -->
<jsp:include page="checkUser.jsp"/>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="validUser" class="uk.ac.cisban.symba.webapp.util.PersonBean" scope="session">
</jsp:useBean>

<%--One of the properties in the previous form belongs in the RawDataBean--%>
<jsp:useBean id="rdb"
             class="uk.ac.cisban.symba.webapp.util.RawDataBean" scope="session">
</jsp:useBean>


<%
    // to ensure that there are no problems with the files, delete all files from the rdb
    // if any are present.
    rdb.setAllFileBeans( new ArrayList<FileBean>() );
    rdb.setAllDataBeans( new ArrayList<RawDataInfoBean>() );

    // now start the form handling
    System.out.println( ServletFileUpload.isMultipartContent( request ) );
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload( factory );
    List items = upload.parseRequest( request );

    // first, iterate through looking for the data type field
    Iterator itr = items.iterator();
    String expType = null;
    while ( itr.hasNext() ) {
        FileItem item = ( FileItem ) itr.next();
        if ( item.isFormField() && item.getFieldName().equals( "dataType" ) ) {
            // currently only one non-file field
//            System.out.println( "doing field" );
            System.out.println( item.getFieldName() );
            System.out.println( item.getString() );
            // set the experimental data type
            expType = item.getString();
//            System.out.println( "done doing field" );
        }
    }

    // set the data type in the bean
    rdb.setDataType( expType );
    // sort out the 3-letter code for the friendly identifiers
    String threeLetterCode;
    if ( rdb.getDataType().contains( "Microarray" ) ) {
        threeLetterCode = "MCA";
    } else if ( rdb.getDataType().contains( "Proteomic Analysis" ) ) {
        threeLetterCode = "2DG";
    } else if ( rdb.getDataType().contains( "Yeast Robot" ) ) {
        threeLetterCode = "YST";
    } else if ( rdb.getDataType().contains( "Microscopy" ) ) {
        threeLetterCode = "MIC";
    } else {
        threeLetterCode = "DEF";
    }

    // go through each part of the form again - this time we're only interested in the files.
    for ( Object object : items ) {
        FileItem item = ( FileItem ) object;
        if ( !item.isFormField() && item.getSize() > 0 ) {
            RawDataInfoBean localRdib = new RawDataInfoBean();
            // create the friendly identifier
            localRdib.setFriendlyId(
                    validUser.getLastName().substring( 0, 3 ) + "_" + threeLetterCode + "_" +
                            String.valueOf( Math.random() ).substring( 2, 6 ) );

//            System.out.println( "doing file" );
            System.out.println( "SIZE OF FILE " + item.getSize() );
            String name = item.getName();
            name = name.replace( ":\\", "Q" );
            name = name.replace( "\\", "X" );
            String[] args = name.split( "\\." );
            String tempDir = config.getServletContext().getRealPath( "temp" );

            //Allys old Line
            File afile = new File( tempDir + File.separator + localRdib.getFriendlyId() + "." + args[1] );

            //File afile = new File(tempDir+File.separator+"random."+args[1]);

//            System.out.println( localRdib.getFriendlyId() );
            item.write( afile );

            // todo do we really need RawDataInfoBean objects to hold the file anymore? Can its work be transferred
            // to the FileBean?
            localRdib.setAfile( afile );
            FileBean localFileBean = new FileBean();
            localFileBean.setAFile( afile );
            //System.out.println( item.getName() );

            rdb.addFile( localFileBean );
            rdb.addDataItem( localRdib );

            System.out.println( "done doing file" );
            //TODO make a listener, read line by line
        }
    }

    if ( rdb.getAllFileBeans().isEmpty() ) {
%>

<c:redirect url="rawData.jsp">
    <c:param name="errorMsg"
             value="You must enter at least one data file."/>
</c:redirect>
<%
} else {
%>
<c:redirect url="metaData.jsp">
    <c:param name="msg"
             value="You have entered some data"/>
</c:redirect>

<%
    }
%>
