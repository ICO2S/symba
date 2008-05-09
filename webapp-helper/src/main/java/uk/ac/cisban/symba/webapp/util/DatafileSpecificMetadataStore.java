package uk.ac.cisban.symba.webapp.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
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
    private String endurantLsid;

    // Data file variables
    private String friendlyId;
    private String dataFileDescription;    // the description of the data file provided by the user.
    private String fileFormat;
    private String oldFilename; // the original filename, to make it easier for users to determine which file they're annotating

    private ActionSummary assayActionSummary; // information regarding the selected action associated with the assay
    private ActionSummary oneLevelUpActionSummary; // information regarding the selected action one level up from the assay, IF PRESENT.

    private Map<String, GenericEquipmentSummary> genericEquipmentInfo; // the key is the equipment endurant id
    private Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo;  // the key is the GPA's Parent GenericProtocol endurant id

    private MaterialFactorsStore materialFactorsStore;

    public DatafileSpecificMetadataStore() {
        genericEquipmentInfo = new HashMap<String, GenericEquipmentSummary>();
        genericProtocolApplicationInfo = new HashMap<String, GenericProtocolApplicationSummary>();

        assayActionSummary = new ActionSummary();
        oneLevelUpActionSummary = new ActionSummary();

        materialFactorsStore = new MaterialFactorsStore();
    }

    public File getDataFile() {
        return dataFile;
    }

    public void setDataFile( File dataFile ) throws IOException {

        this.dataFile = dataFile;
    }

    public void clearDataFile() {
        this.dataFile = null;
    }

    public String getEndurantLsid() {
        return endurantLsid;
    }

    public void setEndurantLsid( String endurantLsid ) {
        this.endurantLsid = endurantLsid;
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public void setFriendlyId( String friendlyId ) {
        this.friendlyId = friendlyId;
    }

    public void clear() {
        this.dataFile = null;
        this.endurantLsid = "";
        this.friendlyId = "";
        this.dataFileDescription = "";
        this.fileFormat = "";
        this.oldFilename = "";

        this.assayActionSummary = new ActionSummary();
        this.oneLevelUpActionSummary = new ActionSummary();

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

    public ActionSummary getAssayActionSummary() {
        return assayActionSummary;
    }

    public void setAssayActionSummary( ActionSummary assayActionSummary ) {
        this.assayActionSummary = assayActionSummary;
    }

    public ActionSummary getOneLevelUpActionSummary() {
        return oneLevelUpActionSummary;
    }

    public void setOneLevelUpActionSummary( ActionSummary oneLevelUpActionSummary ) {
        this.oneLevelUpActionSummary = oneLevelUpActionSummary;
    }
}
