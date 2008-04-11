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

<%@ page import="uk.ac.cisban.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="investigationBean" class="uk.ac.cisban.symba.webapp.util.InvestigationBean" scope="session">
</jsp:useBean>

<%
    // iterate through all parameters
    Enumeration enumeration = request.getParameterNames();
    while ( enumeration.hasMoreElements() ) {
        String parameterName = ( String ) enumeration.nextElement();
        if ( parameterName.startsWith( "actionListDescription::" ) && request.getParameter( parameterName ).length() > 0 ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[1] );
//                System.out.println( "number = " + number );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            temp.setDataName( request.getParameter( parameterName ) );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "atomicParameterOfGPA::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String GpaParentEndurantId = parsedStrings[1];
            String parameterEndurantId = parsedStrings[2];
            // if there is already an existing map key, add to that one.
            GenericProtocolApplicationSummary summary = ( temp.getGenericProtocolApplicationInfo() ).get( GpaParentEndurantId );
            if ( summary == null ) {
                summary = new GenericProtocolApplicationSummary();
            }
            // now get the map of the parameter of the equipment, to assign an ontology term
            summary.putParameterAndAtomicPair( parameterEndurantId, request.getParameter( parameterName ) );
            temp.setGenericProtocolApplicationInfoValue( GpaParentEndurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "GPATextBox::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String GpaParentEndurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericProtocolApplicationSummary summary = ( temp.getGenericProtocolApplicationInfo() ).get( GpaParentEndurantId );
            if ( summary == null ) {
                summary = new GenericProtocolApplicationSummary();
            }
            // now add the description to the list of GPA descriptions
            summary.addDescription( request.getParameter( parameterName ) );
            temp.setGenericProtocolApplicationInfoValue( GpaParentEndurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "OntologyReplacement::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
//                System.out.println( "number = " + number );

            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );

            if ( temp.getMaterialFactorsBean() == null )
                temp.setMaterialFactorsBean( new MaterialFactorsBean() );
            MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
            mfb.putOntologyReplacementsPair( parsedStrings[1], request.getParameter( parameterName ) );
            temp.setMaterialFactorsBean( mfb );

            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "materialName" ) ) {
            if ( request.getParameter( parameterName ) != null && request.getParameter( parameterName ).length() > 0 ) {
                int number = Integer.valueOf( parameterName.substring( 12 ) );
                // take what is already there, and add only those fields that have not been made yet
                RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
                if ( temp.getMaterialFactorsBean() == null )
                    temp.setMaterialFactorsBean( new MaterialFactorsBean() );
                MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
                mfb.setMaterialName( request.getParameter( parameterName ) );
                temp.setMaterialFactorsBean( mfb );
                investigationBean.setDataItem( temp, number );
            }
        } else if ( parameterName.startsWith( "treatment" ) && request.getParameter( parameterName ).length() > 0 ) {
            // will generate new array each time (unless there are *no* treatments at all,
            // to prevent old choices from being copied multiple times into the array.
            int number = Integer.valueOf(
                    parameterName.substring(
                            9, parameterName.lastIndexOf( '-' ) ) );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            if ( temp.getMaterialFactorsBean() == null ) {
                temp.setMaterialFactorsBean( new MaterialFactorsBean() );
            }
            MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
            if ( mfb.getTreatmentInfo() == null ) mfb.setTreatmentInfo( new ArrayList<String>() );
            int pos = Collections.binarySearch( mfb.getTreatmentInfo(), request.getParameter( parameterName ) );
            if ( pos < 0 ) mfb.addTreatmentInfo( request.getParameter( parameterName ) );
            temp.setMaterialFactorsBean( mfb );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "fileFormat" ) ) {
            int number = Integer.valueOf( parameterName.substring( 10 ) );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            temp.setFileFormat( request.getParameter( parameterName ) );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "parameterOfEquipment::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String equipmentEndurantId = parsedStrings[1];
            String parameterEndurantId = parsedStrings[2];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( equipmentEndurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            // now get the map of the parameter of the equipment, to assign an ontology term
            summary.putParameterAndTermPair( parameterEndurantId, request.getParameter( parameterName ) );
            temp.setGenericEquipmentInfoValue( equipmentEndurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "atomicParameterOfEquipment::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String equipmentEndurantId = parsedStrings[1];
            String parameterEndurantId = parsedStrings[2];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( equipmentEndurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            // now get the map of the parameter of the equipment, to assign an ontology term
            summary.putParameterAndAtomicPair( parameterEndurantId, request.getParameter( parameterName ) );
            temp.setGenericEquipmentInfoValue( equipmentEndurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "equipmentName::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String endurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( endurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            summary.setEquipmentName( request.getParameter( parameterName ) );
            temp.setGenericEquipmentInfoValue( endurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "equipmentDescription::" ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            // get the endurant for the current equipment out.
            String endurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( endurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            summary.setFreeTextDescription( request.getParameter( parameterName ) );
            temp.setGenericEquipmentInfoValue( endurantId, summary );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "materialType" ) ) {
            int number = Integer.valueOf( parameterName.substring( 12 ) );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            if ( temp.getMaterialFactorsBean() == null )
                temp.setMaterialFactorsBean( new MaterialFactorsBean() );
            MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
            mfb.setMaterialType( request.getParameter( parameterName ) );
            temp.setMaterialFactorsBean( mfb );
            investigationBean.setDataItem( temp, number );
        } else if ( parameterName.startsWith( "characteristic" ) ) {
            // each characteristic cannot be empty
            if ( request.getParameter( parameterName ) == null || request.getParameter( parameterName ).length() == 0 ) {
%>
<c:redirect url="metaData.jsp">
    <c:param name="errorMsg"
             value="You must enter a valid ontology term for each characteristic."/>
</c:redirect>
<%
            }
            int number = Integer.valueOf(
                    parameterName.substring(
                            14, parameterName.lastIndexOf( '-' ) ) );
            // take what is already there, and add only those fields that have not been made yet
            RawDataInfoBean temp = investigationBean.getAllDataBeans().get( number );
            if ( temp.getMaterialFactorsBean() == null )
                temp.setMaterialFactorsBean( new MaterialFactorsBean() );
            MaterialFactorsBean mfb = temp.getMaterialFactorsBean();
            if ( mfb.getCharacteristics() == null ) mfb.setCharacteristics( new ArrayList<String>() );
            int pos = Collections.binarySearch( mfb.getCharacteristics(), request.getParameter( parameterName ) );
            if ( pos < 0 ) mfb.addCharacteristic( request.getParameter( parameterName ) );
            temp.setMaterialFactorsBean( mfb );
            investigationBean.setDataItem( temp, number );
        } else {
            continue;
        }
    }
%>

<%-- no need to check the go2confirm value, as the next step is the confirm page anyway --%>
<c:redirect url="confirm.jsp"/>
