package uk.ac.cisban.symba.webapp.util;

import java.io.*;

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
    private String friendlyId;
    private String dataName;
    private String factorChoice;

    // this will only be filled if it is a Microscopy protocol
    private MaterialFactorsBean materialFactorsBean;

    public RawDataInfoBean() {
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
}
