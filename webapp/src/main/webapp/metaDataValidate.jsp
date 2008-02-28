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

<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.MaterialFactorsBean" %>
<%@ page import="uk.ac.cisban.symba.webapp.util.RawDataInfoBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

<%
    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload( factory );
    List items = upload.parseRequest( request );

    // first, iterate through - there should be no files to do - just using the field methods
    for ( Object object : items ) {
        FileItem item = ( FileItem ) object;
        if ( item.isFormField() ) {
            if ( item.getFieldName().startsWith( "actionListDescription" ) && item.getString().length() > 0 ) {
                int number = Integer.valueOf( item.getFieldName().substring( 21 ) );
//                System.out.println( "number = " + number );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                temp.setDataName( item.getString() );
                investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "defaultGenericParameterValueIdentifier" ) ) {
                // this section needs to go in front of the defaultGenericParameterValue section, otherwise
                // that section will accidentally take this field.
                int number = Integer.valueOf( item.getFieldName().substring( 38 ) );
//                System.out.println( "number = " + number );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                temp.setAtomicValueIdentifier( item.getString() );
                investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "defaultGenericParameterValue" ) ) {
                int number = Integer.valueOf( item.getFieldName().substring( 28 ) );
//                System.out.println( "number = " + number );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                temp.setAtomicValue( item.getString() );
                investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "materialName" ) ) {
                if ( item.getString() != null && item.getString().length() > 0 ) {
                    int number = Integer.valueOf( item.getFieldName().substring( 12 ) );
                    // take what is already there, and add only those fields that have not been made yet
                    RawDataInfoBean temp = investigationBean.getDataItem( number );
                    if ( temp.getMaterialFactorsBean() == null )
                        temp.setMaterialFactorsBean( new MaterialFactorsBean() );
                    MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
                    mfb.setMaterialName( item.getString() );
                    temp.setMaterialFactorsBean( mfb );
                    investigationBean.setDataItem( temp, number );
                }
            } else if ( item.getFieldName().startsWith( "treatment" ) && item.getString().length() > 0 ) {
                    // will generate new array each time (unless there are *no* treatments at all,
                    // to prevent old choices from being copied multiple times into the array.
                    int number = Integer.valueOf(item.getFieldName().substring( 9, item.getFieldName().lastIndexOf( '-' ) ) );
                    // take what is already there, and add only those fields that have not been made yet
                    RawDataInfoBean temp = investigationBean.getDataItem( number );
                    if ( temp.getMaterialFactorsBean() == null ) {
                        temp.setMaterialFactorsBean( new MaterialFactorsBean() );
                    }
                    MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
                    if ( mfb.getTreatmentInfo() == null ) mfb.setTreatmentInfo( new ArrayList<String>() );
                    int pos = Collections.binarySearch( mfb.getTreatmentInfo(), item.getString() );
                    if ( pos < 0 ) mfb.addTreatmentInfo( item.getString() );
                    temp.setMaterialFactorsBean( mfb );
                    investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "fileFormat" ) ) {
                int number = Integer.valueOf( item.getFieldName().substring( 10 ) );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                temp.setFileFormat( item.getString() );
                investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "materialType" ) ) {
                int number = Integer.valueOf( item.getFieldName().substring( 12 ) );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                if ( temp.getMaterialFactorsBean() == null )
                    temp.setMaterialFactorsBean( new MaterialFactorsBean() );
                MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
                mfb.setMaterialType( item.getString() );
                temp.setMaterialFactorsBean( mfb );
                investigationBean.setDataItem( temp, number );
            } else if ( item.getFieldName().startsWith( "characteristic" ) ) {
                // each characteristic cannot be empty
                if ( item.getString() == null || item.getString().length() == 0 ) {
%>
<c:redirect url="metaData.jsp">
    <c:param name="errorMsg"
             value="You must enter a valid ontology term for each characteristic."/>
</c:redirect>
<%
                }
                int number = Integer.valueOf(
                        item.getFieldName().substring(
                                14, item.getFieldName().lastIndexOf( '-' ) ) );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getDataItem( number );
                if ( temp.getMaterialFactorsBean() == null )
                    temp.setMaterialFactorsBean( new MaterialFactorsBean() );
                MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
                if ( mfb.getCharacteristics() == null ) mfb.setCharacteristics( new ArrayList<String>() );
                int pos = Collections.binarySearch( mfb.getCharacteristics(), item.getString() );
                if ( pos < 0 ) mfb.addCharacteristic( item.getString() );
                temp.setMaterialFactorsBean( mfb );
                investigationBean.setDataItem( temp, number );
            } else {
                continue;
            }
        }
    }
%>

<c:redirect url="confirm.jsp">
</c:redirect>
