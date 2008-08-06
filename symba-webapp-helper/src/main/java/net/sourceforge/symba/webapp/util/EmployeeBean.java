package net.sourceforge.symba.webapp.util;

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

import net.sourceforge.fuge.common.audit.Person;

import java.io.Serializable;

/**
 * This class contains information about an employee.
 */
public class EmployeeBean implements Serializable {

    // Properties
    private String emailAddr;
    private String password;
    private String userName;
    private String lsid;
    private Person person;

    private String address;
    private String firstName;
    private String lastName;

    public EmployeeBean() {
    }

    /*public EmployeeBean()
    {
     sl = ServiceLocator.instance();
     re = sl.getRealizableEntityService();
    }*/

    /**
     * @return emailAddr property value
     */
    public String getEmailAddr() {
        return emailAddr;
    }

    /**
     * Sets the emailAddr property value.
     * @param emailAddr the new value of emailAddr
     */
    public void setEmailAddr( String emailAddr ) {
        this.emailAddr = emailAddr;
    }

    /**
     * @return the password property value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password property value.
     * @param password the new password value
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * @return the userName property value
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the userName property value.
     * @param userName the new userName value
     */
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getLsid() {
        return lsid;
    }

    public void setLsid( String lsid ) {
        this.lsid = lsid;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson( Person person ) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
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
}