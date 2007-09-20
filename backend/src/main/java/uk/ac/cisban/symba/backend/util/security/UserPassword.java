package uk.ac.cisban.symba.backend.util.security;

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

public class UserPassword
{
  private String username;
  private String password;
  private String endID;
  /** Creates a new instance of UserPassword */
  public UserPassword(String username, String password, String endID)
  {
    this.username = username;
    this.password = password;
    this.endID = endID;
    
      
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getEndID()
  {
    return endID;
  }

  public void setEndID(String endID)
  {
    this.endID = endID;
  }
  
}
