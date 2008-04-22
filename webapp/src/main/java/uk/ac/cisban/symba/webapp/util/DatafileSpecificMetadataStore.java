package uk.ac.cisban.symba.webapp.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

public class DatafileSpecificMetadataStore {

    private File dataFile;
    private byte[] barray;
    private String endurantLsid;
    private String actionEndurant;

    // Data file variables
    private String friendlyId;
    private String dataFileDescription;    // the description of the data file provided by the user.
    private String factorChoice;
    private String fileFormat;
    private String oldFilename; // the original filename, to make it easier for users to determine which file they're annotating

    private String chosenChildProtocolName;
    private String chosenChildProtocolEndurant; // In the case of SyMBA, this is the endurant's identifier.
    private String chosenSecondLevelChildProtocolName;  // only used when using 2nd-level protocols
    private String chosenSecondLevelChildProtocolEndurant; // only used when using 2nd-level protocols. In the case of SyMBA, this is the endurant's identifier.

    private Map<String, GenericEquipmentSummary> genericEquipmentInfo; // the key is the equipment endurant id
    private Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo;  // the key is the GPA's Parent GenericProtocol endurant id

    private MaterialFactorsStore materialFactorsStore;

    public DatafileSpecificMetadataStore() {
        genericEquipmentInfo = new HashMap<String, GenericEquipmentSummary>();
        genericProtocolApplicationInfo = new HashMap<String, GenericProtocolApplicationSummary>();
    }

    public File getDataFile() {
        return dataFile;
    }

    public void setDataFile( File dataFile ) throws IOException {

        this.dataFile = dataFile;
        byte[] local = new byte[( int ) dataFile.length()];
        DataInputStream dis = new DataInputStream( new FileInputStream( dataFile ) );
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

    public String getActionEndurant() {
        return actionEndurant;
    }

    public void setActionEndurant( String actionEndurant ) {
        this.actionEndurant = actionEndurant;
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public void setFriendlyId( String friendlyId ) {
        this.friendlyId = friendlyId;
    }

    public void clear() {
        this.actionEndurant = "";
        this.dataFile = null;
        this.barray = null;
        this.endurantLsid = "";
        this.friendlyId = "";
        this.actionEndurant = "";
        this.dataFileDescription = "";
        this.fileFormat = "";
        this.oldFilename = "";

        this.chosenChildProtocolName = "";
        this.chosenChildProtocolEndurant = "";
        this.chosenSecondLevelChildProtocolName = "";
        this.chosenSecondLevelChildProtocolEndurant = "";
        this.genericEquipmentInfo.clear();
        this.genericProtocolApplicationInfo.clear();

        this.materialFactorsStore.clear();
    }

    public String getDataFileDescription() {
        return dataFileDescription;
    }

    public void setDataFileDescription( String dataFileDescription ) {
        this.dataFileDescription = dataFileDescription;
    }

    public String getFactorChoice() {
        return factorChoice;
    }

    public void setFactorChoice( String factorChoice ) {
        this.factorChoice = factorChoice;
    }

    public MaterialFactorsStore getMaterialFactorsStore() {
        return materialFactorsStore;
    }

    public void setMaterialFactorsStore( MaterialFactorsStore materialFactorsStore ) {
        this.materialFactorsStore = materialFactorsStore;
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

    public String getChosenChildProtocolEndurant() {
        return chosenChildProtocolEndurant;
    }

    public void setChosenChildProtocolEndurant( String chosenChildProtocolEndurant ) {
        this.chosenChildProtocolEndurant = chosenChildProtocolEndurant;
    }

    public String getChosenSecondLevelChildProtocolName() {
        return chosenSecondLevelChildProtocolName;
    }

    public void setChosenSecondLevelChildProtocolName( String chosenSecondLevelChildProtocolName ) {
        this.chosenSecondLevelChildProtocolName = chosenSecondLevelChildProtocolName;
    }

    public String getChosenSecondLevelChildProtocolEndurant() {
        return chosenSecondLevelChildProtocolEndurant;
    }

    public void setChosenSecondLevelChildProtocolEndurant( String chosenSecondLevelChildProtocolEndurant ) {
        this.chosenSecondLevelChildProtocolEndurant = chosenSecondLevelChildProtocolEndurant;
    }

    public Map<String, GenericEquipmentSummary> getGenericEquipmentInfo() {
        return genericEquipmentInfo;
    }

    public void setGenericEquipmentInfo( Map<String, GenericEquipmentSummary> genericEquipmentInfo ) {
        this.genericEquipmentInfo = genericEquipmentInfo;
    }

    public void putGenericEquipmentInfoValue( String key, GenericEquipmentSummary summary ) {
        this.genericEquipmentInfo.put( key, summary );
    }

    public Map<String, GenericProtocolApplicationSummary> getGenericProtocolApplicationInfo() {
        return genericProtocolApplicationInfo;
    }

    public void setGenericProtocolApplicationInfo( Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo ) {
        this.genericProtocolApplicationInfo = genericProtocolApplicationInfo;
    }

    public void putGenericProtocolApplicationInfoValue( String key, GenericProtocolApplicationSummary summary ) {
        this.genericProtocolApplicationInfo.put( key, summary );
    }
}
