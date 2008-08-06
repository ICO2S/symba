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

<%@ page import="net.sourceforge.symba.webapp.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="net.sourceforge.fuge.common.ontology.OntologySource" %>
<%@ page import="net.sourceforge.fuge.common.description.Description" %>
<%@ page import="net.sourceforge.symba.webapp.util.loading.OntologyLoader" %>

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
    String toBeIgnoredParameterName = ""; //will not be handled, since newly created on DB
    boolean automaticReturnToMetaData = false;
    //First check whether the user only wants to get a new Term to be loaded to the database.
    // In this case enter loead new Term to Database, and return in the end back to metadata.jsp
    // instead of confirm.jsp (like if the user had chosen to review its data):

    String hiddennewterminfo = request.getParameter( "hiddennewterminfofield" );
    if ( hiddennewterminfo != null && hiddennewterminfo.length() > 0 ) {
        String[] tmpArr = hiddennewterminfo
                .split( ":::" );//splits userentry and selection(=parametername) from second String, which then is to be split further by "::"
        String mixedTermInfo = tmpArr[0];
        String userEntry = tmpArr[1];
        String selectionName =
                tmpArr[2]; //this equals the corresponding parametername in actual request (which has to be be ignored, only this one time)
        if ( mixedTermInfo.startsWith( "ontologyTextfield::" ) ) {
            String[] parsedStrings = mixedTermInfo.split( "::" );
            int number = Integer.valueOf( parsedStrings[1] ); //(c.f. iii from metaData.jsp)
            String otseLSID = parsedStrings[2];

            //The session-parameter for the new term should then not be handled prior to the automatic redirection to the data-entry-page MetaData.jsp:
            toBeIgnoredParameterName = selectionName;

            //Now we have to insert the single new Term into the DB (method now returns some info rg created oterm):
            String ontologyTermEndurantID =
                    OntologyLoader.loadOnlyNewOntologytermToDB( validUser, userEntry, otseLSID );
            //symbaFormSessionBean, validUser , scp).loadOnlyNewOntologytermToDB(userEntry, otseLSID);

            //finally we set a flag for a redirection back to metadata.jsp
            automaticReturnToMetaData = true;//will be checked at the end of this class

            //Now remove evtually preexisting userselections for this entry from the sessionBeans Materialfactorstore...:
            for ( DatafileSpecificMetadataStore dfsmds : symbaFormSessionBean.getDatafileSpecificMetadataStores() ) {
                HashMap<String, String> hmap = dfsmds.getMaterialFactorsStore().getCharacteristics();
                for ( String k : hmap.keySet() ) {
                    if ( k.equals( otseLSID ) ) {
                        hmap.remove( k );
                    }
                }
                String s = hmap.get( mixedTermInfo );
                hmap.remove( s );
                //note: selections of multiple selections will not be removed but kept and presented in addition to the new entry
            }
            //...and fill Materialfactorstore instead with the newly created Ontology-Entry (which then will be marked as selected option instantly by the metadata.jsp):
            OntologySource ontologySource =
                    ( OntologySource ) validUser.getSymbaEntityService().getLatestByEndurant( otseLSID );
            boolean multipleSel = false;
            for ( Object o : ontologySource.getDescriptions() ) {
                Description desc = ( Description ) o;
                if ( desc.getText().contains( "please choose all that apply" ) ) { //then its a multiple selection
                    multipleSel = true;
                }
            }
            if ( multipleSel ) {//adding to preexisting entries in respective HashSet:
                MaterialFactorsStore mfs = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number )
                        .getMaterialFactorsStore();
                if ( mfs.getMultipleCharacteristics().get( otseLSID ) != null ) {
                    HashSet<String> multipleCharacteristics;
                    multipleCharacteristics = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number )
                            .getMaterialFactorsStore().getMultipleCharacteristics().get( otseLSID );
                    multipleCharacteristics.add( ontologyTermEndurantID );
                } else {
                    LinkedHashSet<String> newHashSet = new LinkedHashSet<String>();
                    newHashSet.add( ontologyTermEndurantID );
                    mfs.addMultipleCharacteristics( otseLSID, newHashSet );
                }
            } else { // if single selection field:
                if ( ontologyTermEndurantID != null ) {
                    symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number ).getMaterialFactorsStore()
                            .addCharacteristic( otseLSID, ontologyTermEndurantID );
                }
            }
        } else {
            //here maybe throw some error (we should never get here, as long hidennewterminfofield is not used for other purposes)
        }
    } else {
        //field is empty, so no new term requested
    }

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
        } else if ( parameterName.startsWith( "OntologyReplacement::" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            String[] parsedStrings = parameterName.split( "::" );
            int number = Integer.valueOf( parsedStrings[2] );

            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );

            MaterialFactorsStore mfb = temp.getMaterialFactorsStore();
            mfb.putOntologyReplacementsPair( parsedStrings[1], request.getParameter( parameterName ) );
            temp.setMaterialFactorsStore( mfb );

            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "materialName" ) ) {
            if ( request.getParameter( parameterName ) != null && request.getParameter( parameterName ).length() > 0 &&
                 !parameterName.equals( toBeIgnoredParameterName ) ) {
                int number = Integer.valueOf( parameterName.substring( 12 ) );
                // take what is already there, and add only those fields that have not been made yet
                DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores()
                        .get( number );
                MaterialFactorsStore mfb = temp.getMaterialFactorsStore();
                mfb.setMaterialName( request.getParameter( parameterName ) );
                temp.setMaterialFactorsStore( mfb );
                symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
            }
        } else if ( parameterName.startsWith( "treatment" ) && request.getParameter( parameterName ).length() > 0 &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            // will generate new array each time (unless there are *no* treatments at all,
            // to prevent old choices from being copied multiple times into the array.
            int number = Integer.valueOf(
                    parameterName.substring(
                            9, parameterName.lastIndexOf( '-' ) ) );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            MaterialFactorsStore mfb = temp.getMaterialFactorsStore();
            mfb.addTreatmentInfo( request.getParameter( parameterName ) );
            temp.setMaterialFactorsStore( mfb );
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
        } else if ( parameterName.startsWith( "materialType" ) && !parameterName.equals( toBeIgnoredParameterName ) ) {
            int number = Integer.valueOf( parameterName.substring( 12 ) );
            // take what is already there, and add only those fields that have not been made yet
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            MaterialFactorsStore mfb = temp.getMaterialFactorsStore();
            mfb.setMaterialType( request.getParameter( parameterName ) );
            temp.setMaterialFactorsStore( mfb );
            symbaFormSessionBean.setDatafileSpecificMetadataStore( temp, number );
        } else if ( parameterName.startsWith( "characteristic" ) &&
                    !parameterName.equals( toBeIgnoredParameterName ) ) {
            // each characteristic cannot be empty (except a new one was created and it will redirect directly to metaData.jsp),
            // and might be multiple selections, which will be separated by commas

            if ( ( !automaticReturnToMetaData ) && ( request.getParameter( parameterName ) == null ||
                                                     request.getParameter( parameterName ).length() == 0 ) ) {
                //(if new term for characteristic was requested, we will be redirected to metadata.jsp automatically anyway due to automaticReturnToMetaData==true)
%>
<c:redirect url="metaData.jsp">
    <c:param name="errorMsg"
             value="You must enter a valid ontology term for each characteristic."/>
</c:redirect>
<%
            }

            int number;
            boolean multipleAllowed = false;
            if ( parameterName.startsWith( "characteristicMultiple" ) ) {
                number = Integer.valueOf(
                        parameterName.substring(
                                22, parameterName.lastIndexOf( '-' ) ) );
                multipleAllowed = true;
            } else {
                number = Integer.valueOf(
                        parameterName.substring(
                                14, parameterName.lastIndexOf( '-' ) ) );
            }
            DatafileSpecificMetadataStore temp = symbaFormSessionBean.getDatafileSpecificMetadataStores().get( number );
            MaterialFactorsStore mfb = temp.getMaterialFactorsStore();
            String ontologySourceEndurantID;
            for ( String singleParameter : request.getParameterValues( parameterName ) ) {
                String[] parsedStrings = singleParameter.split( "::" );
                ontologySourceEndurantID = parsedStrings[0];

                if ( multipleAllowed ) {
                    LinkedHashSet<String> tmp = mfb.getMultipleCharacteristics().get( ontologySourceEndurantID );
                    if ( tmp == null ) {
                        tmp = new LinkedHashSet<String>();
                        mfb.addMultipleCharacteristics( ontologySourceEndurantID, tmp );
                    }
                    if ( !tmp.contains( parsedStrings[1] ) ) {
                        tmp.add( parsedStrings[1] );
                    }
                } else {
                    mfb.addCharacteristic( ontologySourceEndurantID, parsedStrings[1] );
                }
            }//end for

            temp.setMaterialFactorsStore( mfb );
            symbaFormSessionBean
                    .setDatafileSpecificMetadataStore( temp, number ); //js number is "iii" from metaData.jsp
        } else {
            continue;
        }
    }//end while loop (enumeration of all Parameternames)

    // Under normal situations, the protocol is only locked once the first set of metadata has been entered. Until
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


%>
<%-- no need to check the go2confirm value, as the next step is the confirm page anyway --%>
<c:redirect url="confirm.jsp"/>

