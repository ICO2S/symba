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
public class RawDataBean implements Serializable {

    private List<RawDataInfoBean> allDataBeans;
    private List<FileBean> allFileBeans;
    private String dataType;

    public RawDataBean() {
        allDataBeans = new ArrayList<RawDataInfoBean>();
    }

    public List<RawDataInfoBean> getAllDataBeans() {
        return allDataBeans;
    }

    public void setAllDataBeans( List<RawDataInfoBean> allDataBeans ) {
        this.allDataBeans = allDataBeans;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType( String dataType ) {
        this.dataType = dataType;
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

    public RawDataInfoBean getDataItem( int value ) {
        return this.allDataBeans.get( value );
    }

    public void clear() {
        this.allDataBeans = new ArrayList<RawDataInfoBean>();
        this.allFileBeans = new ArrayList<FileBean>();
        this.dataType = "";
    }
}
