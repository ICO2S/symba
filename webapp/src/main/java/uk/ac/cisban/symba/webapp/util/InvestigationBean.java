package uk.ac.cisban.symba.webapp.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/*
 * FileBean.java
 *
 * Created on 20 December 2006, 10:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author ollyshaw
 */
public class InvestigationBean implements Serializable {

    private List<RawDataInfoBean> allDataBeans;
    private List<FileBean> allFileBeans;
    private String topLevelProtocolName;
    private String topLevelProtocolIdentifier; // In the case of SyMBA, this is the endurant's identifier.

    public InvestigationBean() {
        allDataBeans = new ArrayList<RawDataInfoBean>();
        allFileBeans = new ArrayList<FileBean>();
    }

    public List<RawDataInfoBean> getAllDataBeans() {
        return allDataBeans;
    }

    public void setAllDataBeans( List<RawDataInfoBean> allDataBeans ) {
        this.allDataBeans = allDataBeans;
    }

    public String getTopLevelProtocolName() {
        return topLevelProtocolName;
    }

    public void setTopLevelProtocolName( String topLevelProtocolName ) {
        this.topLevelProtocolName = topLevelProtocolName;
    }

    public List<FileBean> getAllFileBeans() {
        return allFileBeans;
    }

    public void setAllFileBeans( List<FileBean> allFileBeans ) {
        this.allFileBeans = allFileBeans;
    }

    public void addFile( FileBean fileBean ) {
        this.allFileBeans.add( fileBean );
    }

    public FileBean getFileBean( int value ) {
        return this.allFileBeans.get( value );
    }

    public void addDataItem( RawDataInfoBean info ) {
        this.allDataBeans.add( info );
    }

    public void setDataItem( RawDataInfoBean info, int value ) {
        this.allDataBeans.set( value, info );
    }

    public void clear() {
        this.allDataBeans = new ArrayList<RawDataInfoBean>();
        this.allFileBeans = new ArrayList<FileBean>();
        this.topLevelProtocolName = "";
        this.topLevelProtocolIdentifier = "";
    }

    public String getTopLevelProtocolIdentifier() {
        return topLevelProtocolIdentifier;
    }

    public void setTopLevelProtocolIdentifier( String topLevelProtocolIdentifier ) {
        this.topLevelProtocolIdentifier = topLevelProtocolIdentifier;
    }

}
