<!-- This file is part of SyMBA.-->
<!-- SyMBA is covered under the GNU Lesser General Public License (LGPL).-->
<!-- Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.-->
<!-- To view the full licensing information for this software and ALL other files contained-->
<!-- in this distribution, please see LICENSE.txt-->
<!-- $LastChangedDate$-->
<!-- $LastChangedRevision$-->
<!-- $Author$-->
<!-- $HeadURL$-->

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

<jsp:useBean id="investigationBean"
             class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>


<%
    // now start the form handling
    System.out.println( ServletFileUpload.isMultipartContent( request ) );
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload( factory );
    List items = upload.parseRequest( request );

    // If the user is trying to re-enter data at this stage, we know that they are not allowed to
    // change anything in rawData.jsp. Therefore we can skip all of this.
    if ( investigationBean.getTopLevelProtocolName() != null && investigationBean.getTopLevelProtocolIdentifier() != null ) {
        String parameterValue = null;
        for ( Object itemObj : items ) {
            FileItem item = ( FileItem ) itemObj;
            if ( item.isFormField() && item.getFieldName().equals( "go2confirm" ) ) {
                parameterValue = item.getString();
                break;
            }
        }
        if ( parameterValue != null && parameterValue.equals( "true" ) ) { %>
<c:redirect url="confirm.jsp"/>
<% } else { %>
<c:redirect url="ChooseAction.jsp"/>
<% }
}

    // to ensure that there are no problems with the files, delete all files from the investigationBean
    // if any are present.
    investigationBean.setAllFileBeans( new ArrayList<FileBean>() );

    // first, iterate through looking for the investigation details field
    Iterator itr = items.iterator();
    String investigationName = null;
    String investigationIdentifier = null;
    while ( itr.hasNext() ) {
        FileItem item = ( FileItem ) itr.next();
        if ( item.isFormField() && item.getFieldName().equals( "investigationType" ) ) {
            // currently only one non-file field
            // set the experimental investigation type
            investigationName = item.getString().substring( 0, item.getString().indexOf( "::Identifier::" ) );
//            System.err.println( "Investigation Name:" + investigationName + "END");
            investigationIdentifier = item.getString().substring( item.getString().indexOf( "::Identifier::" ) + 14 );
//            System.err.println( "Investigation Identifier:" + investigationIdentifier  + "END");
        }
    }

    investigationBean.setTopLevelProtocolName( investigationName );
    investigationBean.setTopLevelProtocolIdentifier( investigationIdentifier );

    // sort out the 3-letter code for the friendly identifiers
    String threeLetterCode;
    if ( investigationBean.getTopLevelProtocolName().contains( "Microarray" ) ) {
        threeLetterCode = "MCA";
    } else if ( investigationBean.getTopLevelProtocolName().contains( "Proteomic Analysis" ) ) {
        threeLetterCode = "2DG";
    } else if ( investigationBean.getTopLevelProtocolName().contains( "Yeast Robot" ) ) {
        threeLetterCode = "YST";
    } else if ( investigationBean.getTopLevelProtocolName().contains( "Microscopy" ) ) {
        threeLetterCode = "MIC";
    } else if ( investigationBean.getTopLevelProtocolName().contains( "Carmen" ) ) {
        threeLetterCode = "CAR";
    } else {
        threeLetterCode = "DEF";
    }

    // go through each part of the form - this time we're only interested in the files.
    for ( Object object : items ) {
        FileItem item = ( FileItem ) object;
        if ( !item.isFormField() && item.getSize() > 0 ) {
            RawDataInfoBean localRdib = new RawDataInfoBean();
            // create the friendly identifier
            localRdib.setFriendlyId(
                    validUser.getLastName().substring( 0, 3 ) + "_" + threeLetterCode + "_" +
                            String.valueOf( Math.random() ).substring( 2, 6 ) );

            localRdib.setOldFilename( item.getName() );
//            System.out.println( "SIZE OF FILE " + item.getSize() );
            String name = item.getName();
            name = name.replace( ":\\", "Q" );
            name = name.replace( "\\", "X" );
            String[] args = name.split( "\\." );
            String tempDir = config.getServletContext().getRealPath( "temp" );

            File afile = new File( tempDir + File.separator + localRdib.getFriendlyId() + "." + args[1] );

//            System.out.println( localRdib.getFriendlyId() );
            item.write( afile );

            localRdib.setAfile( afile );
            FileBean localFileBean = new FileBean();
            localFileBean.setAFile( afile );
            //System.out.println( item.getName() );

            investigationBean.addFile( localFileBean );
            investigationBean.addDataItem( localRdib );
        }
    }

    if ( investigationBean.getAllFileBeans().isEmpty() ) {
%>

<c:redirect url="rawData.jsp">
    <c:param name="errorMsg"
             value="You must enter at least one data file."/>
</c:redirect>
<%
} else {
%>
<c:redirect url="ChooseAction.jsp">
    <c:param name="msg"
             value="You have entered some data"/>
</c:redirect>

<%
    }
%>
