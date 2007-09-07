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
<%@ page import="java.io.File" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="fileHold" class="uk.ac.cisban.symba.webapp.util.FileBean" scope="session">
</jsp:useBean>

<HTML>
<HEAD>
    <TITLE>SyMBA Upload</TITLE>
</HEAD>

<BODY>
<H1>Uploading Files</H1>
File name:


<%-- File afile = new File (request.getParameter("filer"));
out.println(afile.canRead());
out.println(afile.getAbsolutePath());
afile = new File(afile.getAbsolutePath());
out.println(afile.canRead());
afile = new File("file://" + afile.getAbsolutePath());
out.println(afile.canRead());
--%>

<%
    out.println( ServletFileUpload.isMultipartContent( request ) );
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload( factory );
    List items = upload.parseRequest( request );
    Iterator itr = items.iterator();
    while ( itr.hasNext() ) {
        FileItem item = ( FileItem ) itr.next();
        if ( item.isFormField() ) {
            out.println( "<p>doing field</p>" );
            out.println( item.getFieldName() );
            out.println( item.getString() );
            out.println( "<p>done doing field</p>" );
        } else {
            out.println( "<p>doing file</p>" );
            out.println( item.getSize() );
            String name = item.getName();
            name = name.replace( ":\\", "Q" );
            String[] args = name.split( "\\." );
            //File afile = File.createTempFile(args[0],"."+args[1]);
            out.println( "<p>" );
            out.println( config.getServletContext().getRealPath( "//temp" ) );
            out.println( "<p>" );
            out.println( config.getServletContext().getRealPath( "temp//" ) );
            out.println( "<p>" );
            out.println( config.getServletContext().getRealPath( "//temp//" ) );
            out.println( "<p>" );
            String tempDir = config.getServletContext().getRealPath( "temp" );
            File afile = new File( tempDir + File.separator + "random." + args[1] );
            //File afile = File.createTempFile(args[0],"."+args[1]);
            item.write( afile );
            out.println( "<p>done doing file</p>" );
            out.println( "<p>" + item.getName() + "</p>" );
            out.println( "<p>" + afile.getAbsolutePath() + "</p>" );
            fileHold.setAFile( afile );
            //TODO make a listener, read line by line
            //TestFile ts = new TestFile(afile,out);
            //Scanner scan = new Scanner(afile);
            //while (scan.hasNext())
            //  {
            //  String s = scan.nextLine();
            //   s.replace('<','$');
            //                   s.replace('>','$');
            //  out.println(s);
            // }
        }
    }

%>
<form action="testFileDownload.jsp">
    <input type="submit" value="bb" name="bb"/>
</form>
</BODY>
</HTML>
