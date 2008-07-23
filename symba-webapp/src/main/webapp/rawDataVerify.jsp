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

<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="net.sourceforge.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.File" %>

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

<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>

<%
    // now start the form handling: check for go2confirm. If present, we will redirect (depending on its value).
    // If not present, we change the session.
//    System.out.println( ServletFileUpload.isMultipartContent( request ) );
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload( factory );
    List items = null;
    try {
        items = upload.parseRequest( request );
    } catch ( FileUploadException e ) {
        out.println( "Error trying to parse the upload request for your files. Please send this error message to" );
        out.println( application.getAttribute( "helpEmail" ) + "<br/>" );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }

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
<% } else if ( ( parameterValue != null && parameterValue.equals( "false" ) ) ||
        ( parameterValue == null && symbaFormSessionBean.isProtocolLocked() &&
                symbaFormSessionBean.isDataPresent() ) ) { %>
<c:redirect url="ChooseAction.jsp"/>
<%
    }

    // If the user reaches here, then the previous page has allowed the user to update their files.
    // we can allow them to change the SymbaFormSessionBean

    // If the protocol hasn't been locked yet, ensure that there are no problems with the files by deleting all files
    // and associated file-specific metadata from the SymbaFormSessionBean, if any are present. If the protocol
    // has been locked, the only way to get here is if the user has copied metadata from a pre-existing experiment
    // AND they haven't uploaded data yet. In the first case, we should (as already mentioned) delete all files
    // and associated file-specific metadata, in case the user keeps going back and forth, reloading data files.
    // In the second case, we should delete the existing datafiles, but retain the first set of file-specific
    // metadata as a template for the rest, then only allow changes to the data file portion of it.
    if ( session.getAttribute( "templateStore" ) == null ) {
        if ( symbaFormSessionBean.isProtocolLocked() && symbaFormSessionBean.isMetadataFromAnotherExperiment() ) {
            session.setAttribute( "templateStore", symbaFormSessionBean.getDatafileSpecificMetadataStores().get( 0 ) );
        }
    }
    symbaFormSessionBean.setDatafileSpecificMetadataStores( new ArrayList<DatafileSpecificMetadataStore>() );

    if ( !symbaFormSessionBean.isMetadataFromAnotherExperiment() ) {
        // first, iterate through looking for the investigation details field
        Iterator itr = items.iterator();
        String investigationName = null;
        String investigationEndurant = null;
        while ( itr.hasNext() ) {
            FileItem item = ( FileItem ) itr.next();
            if ( item.isFormField() && item.getFieldName().equals( "investigationType" ) ) {
                // currently only one non-file field
                // set the experimental investigation type
                investigationEndurant = item.getString().substring( 0, item.getString().indexOf( "::Identifier::" ) );
//            System.err.println( "Investigation Name:" + investigationName + "END");
                investigationName = item.getString()
                        .substring( item.getString().indexOf( "::Identifier::" ) + 14 );
//            System.err.println( "Investigation Identifier:" + investigationEndurant  + "END");
            }
        }

        symbaFormSessionBean.setTopLevelProtocolName( investigationName );
        symbaFormSessionBean.setTopLevelProtocolEndurant( investigationEndurant );
    }

    // sort out the 3-letter code for the friendly identifiers
    String threeLetterCode;
    if ( symbaFormSessionBean.getTopLevelProtocolName().contains( "Microarray" ) ) {
        threeLetterCode = "MCA";
    } else if ( symbaFormSessionBean.getTopLevelProtocolName().contains( "Proteomic Analysis" ) ) {
        threeLetterCode = "2DG";
    } else if ( symbaFormSessionBean.getTopLevelProtocolName().contains( "Yeast Robot" ) ) {
        threeLetterCode = "YST";
    } else if ( symbaFormSessionBean.getTopLevelProtocolName().contains( "Microscopy" ) ) {
        threeLetterCode = "MIC";
    } else if ( symbaFormSessionBean.getTopLevelProtocolName().contains( "Carmen" ) ) {
        threeLetterCode = "CAR";
    } else {
        threeLetterCode = "DEF";
    }

    // go through each part of the form - this time we're only interested in the files.
    boolean errorDuringUpload = false;
    for ( Object object : items ) {
        FileItem item = ( FileItem ) object;
        if ( !item.isFormField() && item.getSize() > 0 ) {
            DatafileSpecificMetadataStore localFileMetadataStore = new DatafileSpecificMetadataStore();

            // create the friendly identifier
            localFileMetadataStore.setFriendlyId(
                    validUser.getLastName().substring( 0, 3 ) + "_" + threeLetterCode + "_" +
                            String.valueOf( Math.random() ).substring( 2, 6 ) );

            localFileMetadataStore.setOldFilename( item.getName() );

//            System.out.println( "SIZE OF FILE " + item.getSize() );
            String name = item.getName();
            name = name.replace( ":\\", "Q" );
            name = name.replace( "\\", "X" );
            String[] args = name.split( "\\." );
            String tempDir = config.getServletContext().getRealPath( "temp" );

            File localFile = new File(
                    tempDir + File.separator + localFileMetadataStore.getFriendlyId() + "." + args[1] );


            try {
                item.write( localFile ); // exception will be thrown here if the file is not part of the local filesystem.
                localFileMetadataStore.setDataFile( localFile );
                if ( !localFile.exists() || localFile.length() == 0 ) {
                    errorDuringUpload = true;
                } else {
                    symbaFormSessionBean.addDatafileSpecificMetadataStore( localFileMetadataStore );
                }
            } catch ( Exception e ) {
                // assume that the reason the file couldn't be found is because it's already on the remote server
                // (where SyMBA's data store lives), and the user is passing the remote location. We will warn
                // the user about this assumption on the next page.
                localFileMetadataStore.setFilenameToLink( item.getName() );
                symbaFormSessionBean.addDatafileSpecificMetadataStore( localFileMetadataStore );
            }

        }
    }

    if ( !errorDuringUpload ) {
        symbaFormSessionBean.setDataPresent( true );
    } else {
%>

<c:redirect url="rawData.jsp">
    <c:param name="errorMsg"
             value="There must be at least one data file or location of the data file on the remote server. All provided data files must exist and be non-empty."/>
</c:redirect>
<%
    }
%>
<c:redirect url="ChooseAction.jsp">
    <c:param name="msg"
             value="You have entered some data"/>
</c:redirect>

