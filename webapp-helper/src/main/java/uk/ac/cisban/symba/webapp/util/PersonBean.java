package uk.ac.cisban.symba.webapp.util;

import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityService;

import java.io.Serializable;

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
public class PersonBean implements Serializable {

    // The bean follows standard coding practice, eg empty constructor
    //then private fields with public getters and setters based on Field names
    //The only exception is the startREService method. This is a slight tweak to
    // ensure that the reService is started.

    private String firstName;
    private String lastName;
    private String email;
    private String lsid;
    private String endurantLsid;
    private String name;

    //from the EmployeeBean (the old version) 
    private String password;
    private String userName;
    private ServiceLocator sl;
    private RealizableEntityService reService;

    /**
     * Creates a new instance of PersonBean
     */
    public PersonBean() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public ServiceLocator getSl() {
        return sl;
    }

    public void setSl( ServiceLocator sl ) {
        this.sl = sl;
    }

    public RealizableEntityService getReService() {
        return reService;
    }

    public void setReService( RealizableEntityService reService ) {
        this.reService = reService;
    }

    public String getLsid() {
        return lsid;
    }

    public void setLsid( String lsid ) {
        this.lsid = lsid;
    }

    //The only non conforming method, starts up the reService.
    // The re service is then used throughtout and is available throughout the
    //session
    public void startRe() {
        ServiceLocator sl = ServiceLocator.instance();
        setReService( sl.getRealizableEntityService() );
    }

    public String getEndurantLsid() {
        return endurantLsid;
    }

    public void setEndurantLsid( String endurantLsid ) {
        this.endurantLsid = endurantLsid;
    }


}
