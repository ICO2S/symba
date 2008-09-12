package net.sourceforge.symba.webapp.util;

import net.sourceforge.symba.webapp.util.forms.schemes.protocol.ActionHierarchyScheme;

import java.io.File;
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

    /*
     * There are two ways of uploading files to symba
     * 1. Direct upload from the local (user's) computer
     * 2. Linking from a file already on the remote host
     *
     * Number 1 is more common, and in those cases, the variable dataFile is populated.
     * Number 2 is less common, and in those cases, the variable filenameToLink is populated.
     */
    private File dataFile;
    private String filenameToLink;

    private String endurantLsid;

    // Data file variables
    private String friendlyId;
    private String dataFileDescription;    // the description of the data file provided by the user.
    private String fileFormat;
    private String oldFilename; // the original filename, to make it easier for users to determine which file they're annotating

    private ActionHierarchyScheme nestedActions; // stores the complete hierarchy of actions associated with the assay

    private Map<String, GenericEquipmentSummary> genericEquipmentInfo; // the key is the equipment endurant id
    private Map<String, GenericProtocolApplicationSummary> genericProtocolApplicationInfo;  // the key is the GPA's Parent GenericProtocol endurant id. Input material information is also stored here

    public DatafileSpecificMetadataStore() {
        this.genericEquipmentInfo = new HashMap<String, GenericEquipmentSummary>();
        this.genericProtocolApplicationInfo = new HashMap<String, GenericProtocolApplicationSummary>();

        this.nestedActions = new ActionHierarchyScheme();
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

    public String getFilenameToLink() {
        return filenameToLink;
    }

    public void setFilenameToLink( String filenameToLink ) {
        this.filenameToLink = filenameToLink;
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

        this.nestedActions = new ActionHierarchyScheme();

        this.genericEquipmentInfo.clear();
        this.genericProtocolApplicationInfo.clear();
    }

    public String getDataFileDescription() {
        return dataFileDescription;
    }

    public void setDataFileDescription( String dataFileDescription ) {
        this.dataFileDescription = dataFileDescription;
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

    public ActionHierarchyScheme getNestedActions() {
        return nestedActions;
    }

    public void setNestedActions( ActionHierarchyScheme nestedActions ) {
        this.nestedActions = nestedActions;
    }
}
