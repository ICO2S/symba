package uk.ac.cisban.symba.webapp.util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * Stores all of the information needed for a single user's session that is directly dealing with data and metadata
 * upload.
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class MetadataStore {

    private List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores;
    private List<File> fileStores;
    private String topLevelProtocolName;
    private String topLevelProtocolIdentifier; // the endurant's identifier.
    private String experimentName;
    private String hypothesis;
    private String conclusion;

    public MetadataStore() {
        datafileSpecificMetadataStores = new ArrayList<DatafileSpecificMetadataStore>();
        fileStores = new ArrayList<File>();
    }

    public List<DatafileSpecificMetadataStore> getDatafileSpecificMetadataStores() {
        return datafileSpecificMetadataStores;
    }

    public void setDatafileSpecificMetadataStores( List<DatafileSpecificMetadataStore> datafileSpecificMetadataStores ) {
        this.datafileSpecificMetadataStores = datafileSpecificMetadataStores;
    }

    public String getTopLevelProtocolName() {
        return topLevelProtocolName;
    }

    public void setTopLevelProtocolName( String topLevelProtocolName ) {
        this.topLevelProtocolName = topLevelProtocolName;
    }

    public void addDataItem( DatafileSpecificMetadataStore info ) {
        this.datafileSpecificMetadataStores.add( info );
    }

    public void setDataItem( DatafileSpecificMetadataStore info, int value ) {
        this.datafileSpecificMetadataStores.set( value, info );
    }

    public void clear() {
        this.datafileSpecificMetadataStores = new ArrayList<DatafileSpecificMetadataStore>();
        this.fileStores = new ArrayList<File>();
        this.topLevelProtocolName = "";
        this.topLevelProtocolIdentifier = "";
        this.experimentName = "";
        this.hypothesis = "";
        this.conclusion = "";
    }

    public String getTopLevelProtocolIdentifier() {
        return topLevelProtocolIdentifier;
    }

    public void setTopLevelProtocolIdentifier( String topLevelProtocolIdentifier ) {
        this.topLevelProtocolIdentifier = topLevelProtocolIdentifier;
    }

    public List<File> getFileStores() {
        return fileStores;
    }

    public void setFileStores( List<File> fileStores ) {
        this.fileStores = fileStores;
    }

    public void addFile( File fileStore ) {
        this.fileStores.add( fileStore );
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName( String experimentName ) {
        this.experimentName = experimentName;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis( String hypothesis ) {
        this.hypothesis = hypothesis;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion( String conclusion ) {
        this.conclusion = conclusion;
    }

}
