package uk.ac.cisban.symba.webapp.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 *
 */

public class RawDataInfoBean implements Serializable {

    private File afile;
    private byte[] barray;
    private String endurantLsid;
    private String protocolEndurant;
    private String actionEndurant;
    private String workflowName;

    // Data file variables
    private String friendlyId;
    private String dataName;    // the description of the data file provided by the user.
    private String factorChoice;
    private String fileFormat;
    private String oldFilename; // the original filename, to make it easier for users to determine which file they're annotating

    private String chosenChildProtocolName;
    private String chosenChildProtocolIdentifier; // In the case of SyMBA, this is the endurant's identifier.
    private String chosenSecondLevelChildProtocolName;  // only used when using 2nd-level protocols
    private String chosenSecondLevelChildProtocolIdentifier; // only used when using 2nd-level protocols. In the case of SyMBA, this is the endurant's identifier.

    private Map<String, GenericEquipmentSummary> genericEquipmentInfo; // the key is the equipment endurant id
    private Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo;  // the key is the GPA's Parent GenericProtocol endurant id

    private MaterialFactorsBean materialFactorsBean;

    public RawDataInfoBean() {
        genericEquipmentInfo = new HashMap<String, GenericEquipmentSummary>();
        genericProtocolApplicationInfo = new HashMap<String, GenericProtocolApplicationSummary>();
    }

    public File getAfile() {
        return afile;
    }

    public void setAfile( File afile ) throws IOException {

        this.afile = afile;
        byte[] local = new byte[( int ) afile.length()];
        DataInputStream dis = new DataInputStream( new FileInputStream( afile ) );
        dis.readFully( local );
        dis.close();
        setBarray( local );
    }

    public byte[] getBarray() {
        return barray;
    }

    private void setBarray( byte[] barray ) {
        this.barray = barray;
    }

    public String getEndurantLsid() {
        return endurantLsid;
    }

    public void setEndurantLsid( String endurantLsid ) {
        this.endurantLsid = endurantLsid;
    }

    public String getProtocolEndurant() {
        return protocolEndurant;
    }

    public void setProtocolEndurant( String protocolEndurant ) {
        this.protocolEndurant = protocolEndurant;
    }

    public String getActionEndurant() {
        return actionEndurant;
    }

    public void setActionEndurant( String actionEndurant ) {
        this.actionEndurant = actionEndurant;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName( String workflowName ) {
        this.workflowName = workflowName;
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public void setFriendlyId( String friendlyId ) {
        this.friendlyId = friendlyId;
    }

    public void clear() {
        this.actionEndurant = "";
        this.afile = null;
        this.barray = null;
        this.endurantLsid = "";
        this.friendlyId = "";
        this.protocolEndurant = "";
        this.actionEndurant = "";
        this.workflowName = "";
        this.dataName = "";
        this.fileFormat = "";
        this.oldFilename = "";

        this.chosenChildProtocolName = "";
        this.chosenChildProtocolIdentifier = "";
        this.chosenSecondLevelChildProtocolName = "";
        this.chosenSecondLevelChildProtocolIdentifier = "";
        this.genericEquipmentInfo.clear();
        this.genericProtocolApplicationInfo.clear();

        this.materialFactorsBean.clear();
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName( String dataName ) {
        this.dataName = dataName;
    }

    public String getFactorChoice() {
        return factorChoice;
    }

    public void setFactorChoice( String factorChoice ) {
        this.factorChoice = factorChoice;
    }

    public MaterialFactorsBean getMaterialFactorsBean() {
        return materialFactorsBean;
    }

    public void setMaterialFactorsBean( MaterialFactorsBean materialFactorsBean ) {
        this.materialFactorsBean = materialFactorsBean;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat( String fileFormat ) {
        this.fileFormat = fileFormat;
    }

    public String getOldFilename() {
        return oldFilename;
    }

    public void setOldFilename( String oldFilename ) {
        this.oldFilename = oldFilename;
    }

    public String getChosenChildProtocolName() {
        return chosenChildProtocolName;
    }

    public void setChosenChildProtocolName( String chosenChildProtocolName ) {
        this.chosenChildProtocolName = chosenChildProtocolName;
    }

    public String getChosenChildProtocolIdentifier() {
        return chosenChildProtocolIdentifier;
    }

    public void setChosenChildProtocolIdentifier( String chosenChildProtocolIdentifier ) {
        this.chosenChildProtocolIdentifier = chosenChildProtocolIdentifier;
    }

    public String getChosenSecondLevelChildProtocolName() {
        return chosenSecondLevelChildProtocolName;
    }

    public void setChosenSecondLevelChildProtocolName( String chosenSecondLevelChildProtocolName ) {
        this.chosenSecondLevelChildProtocolName = chosenSecondLevelChildProtocolName;
    }

    public String getChosenSecondLevelChildProtocolIdentifier() {
        return chosenSecondLevelChildProtocolIdentifier;
    }

    public void setChosenSecondLevelChildProtocolIdentifier( String chosenSecondLevelChildProtocolIdentifier ) {
        this.chosenSecondLevelChildProtocolIdentifier = chosenSecondLevelChildProtocolIdentifier;
    }

    public Map<String, GenericEquipmentSummary> getGenericEquipmentInfo() {
        return genericEquipmentInfo;
    }

    public void setGenericEquipmentInfo( Map<String, GenericEquipmentSummary> genericEquipmentInfo ) {
        this.genericEquipmentInfo = genericEquipmentInfo;
    }

    public void setGenericEquipmentInfoValue( String key, GenericEquipmentSummary summary ) {
        this.genericEquipmentInfo.put( key, summary );
    }

    public Map<String, GenericProtocolApplicationSummary> getGenericProtocolApplicationInfo() {
        return genericProtocolApplicationInfo;
    }

    public void setGenericProtocolApplicationInfo( Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo ) {
        this.genericProtocolApplicationInfo = genericProtocolApplicationInfo;
    }

    public void setGenericProtocolApplicationInfoValue( String key, GenericProtocolApplicationSummary summary ) {
        this.genericProtocolApplicationInfo.put( key, summary );
    }
}
