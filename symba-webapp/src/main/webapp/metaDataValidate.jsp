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


<!-- This include will validate the user -->

<jsp:include page="checkUser.jsp"/>

<%@ page import="net.sourceforge.fuge.collection.FuGE" %>
<%@ page import="net.sourceforge.fuge.common.audit.Person" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologySource" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologyTerm" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocol" %>
<%@ page import="net.sourceforge.fuge.common.protocol.GenericProtocolApplication" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.helper.FuGEMappingHelper" %>
<%@ page import="net.sourceforge.symba.mapping.hibernatejaxb2.xml.XMLMarshaler" %>
<%@ page import="net.sourceforge.symba.webapp.util.*" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.ActionTemplateParser" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialFormValidator" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MaterialTemplateParser" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.MetaDataWrapper" %>
<%@ page import="net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.AssayLoader" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.LoadPerson" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.MaterialTransformationLoader" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.OntologyLoader" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.bind.JAXBException" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="symbaFormSessionBean" class="net.sourceforge.symba.webapp.util.SymbaFormSessionBean" scope="session"/>
<jsp:useBean id="scp" class="net.sourceforge.symba.webapp.util.ScpBean" scope="application"/>
<jsp:useBean id="validUser" class="net.sourceforge.symba.webapp.util.PersonBean" scope="session"/>
<%
    boolean automaticReturnToMetaData = false;
    //First check whether the user only wants to get a new Term to be loaded to the database.
    // In this case enter loead new Term to Database, and return in the end back to metadata.jsp
    // instead of confirm.jsp (like if the user had chosen to review its data):

    String toBeIgnoredParameterName =
            OntologyLoader.validateLoadRequest( request, validUser, symbaFormSessionBean, true );
    if ( toBeIgnoredParameterName.length() > 0 ) {
        automaticReturnToMetaData = true;//will be checked at the end of this class
    }

    // store all material parameters
    symbaFormSessionBean =
            MaterialFormValidator.validate( request, symbaFormSessionBean, toBeIgnoredParameterName, true );
    // iterate through all parameters
    Enumeration enumeration = request.getParameterNames();
    while ( enumeration.hasMoreElements() ) {
        String parameterName = ( String ) enumeration.nextElement();
        if ( parameterName.startsWith( "actionListDescription::" ) &&
             request.getParameter( parameterName ).length() > 0 && !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[1] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            temp.setDataFileDescription( request.getParameter( parameterName ) );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "atomicParameterOfGPA::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            // get the endurant for the current equipment out.
            String GpaParentEndurantId = parsedStrings[1];
            String parameterEndurantId = parsedStrings[2];
            // if there is already an existing map key, add to that one.
            GenericProtocolApplicationSummary summary = ( temp.getGenericProtocolApplicationInfo() ).get(
                    GpaParentEndurantId );
            if ( summary == null ) {
                summary = new GenericProtocolApplicationSummary();
            }
            // now get the map of the parameter of the equipment, to assign an ontology term
            summary.putParameterAndAtomicPair( parameterEndurantId, request.getParameter( parameterName ) );
            temp.putGenericProtocolApplicationInfoValue( GpaParentEndurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "GPAProtocolDescription::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            // get the endurant for the current equipment out.
            String GpaParentEndurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericProtocolApplicationSummary summary = ( temp.getGenericProtocolApplicationInfo() ).get(
                    GpaParentEndurantId );
            if ( summary == null ) {
                summary = new GenericProtocolApplicationSummary();
            }
            // now add the description to the list of GPA descriptions
            summary.putDescription( "ProtocolDescription", request.getParameter( parameterName ) );
            temp.putGenericProtocolApplicationInfoValue( GpaParentEndurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "fileFormat" ) && !parameterName.equals( toBeIgnoredParameterName ) ) {
            int number = Integer.valueOf( parameterName.substring( 10 ) );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            temp.setFileFormat( request.getParameter( parameterName ) );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "parameterOfEquipment::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
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
            temp.putGenericEquipmentInfoValue( equipmentEndurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "atomicParameterOfEquipment::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[3] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
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
            temp.putGenericEquipmentInfoValue( equipmentEndurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "equipmentName::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            // get the endurant for the current equipment out.
            String endurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( endurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            summary.setEquipmentName( request.getParameter( parameterName ) );
            temp.putGenericEquipmentInfoValue( endurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "equipmentDescription::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            // get the endurant for the current equipment out.
            String endurantId = parsedStrings[1];
            // if there is already an existing map key, add to that one.
            GenericEquipmentSummary summary = ( temp.getGenericEquipmentInfo() ).get( endurantId );
            if ( summary == null ) {
                summary = new GenericEquipmentSummary();
            }
            summary.setFreeTextDescription( request.getParameter( parameterName ) );
            temp.putGenericEquipmentInfoValue( endurantId, summary );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else {
            continue;
        }
    }//end while loop (enumeration of all Parameternames)

    // Under normal situations, the protocol is only locked once the first parse of metadata has been entered. Until
    // that point, the user can go back and change things.
    symbaFormSessionBean.setConfirmationReached( true );
    symbaFormSessionBean.setProtocolLocked( true );

    if ( automaticReturnToMetaData ) {
//        automaticReturnToMetaData=false;
%>
<c:redirect url="metaData.jsp">
</c:redirect>
<%
    }

    if ( symbaFormSessionBean.isMaterialCharacteristicsIncomplete() ) {
%>
<c:redirect url="metaData.jsp">
    <c:param name="errorMsg"
             value="You must enter a valid ontology term for each characteristic."/>
</c:redirect>
<%
    }

%>
<%-- no need to check the go2confirm value, as the next step is the confirm page anyway --%>
<c:redirect url="confirm.jsp"/>

