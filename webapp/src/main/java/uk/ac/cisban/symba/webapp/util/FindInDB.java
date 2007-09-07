package uk.ac.cisban.symba.webapp.util;

import fugeOM.Common.Audit.Person;
import fugeOM.service.RealizableEntityService;
import fugeOM.service.RealizableEntityServiceException;

/*
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 *
 * $LastChangedDate:$
 * $LastChangedRevision:$
 * $Author:$
 * $HeadURL:$
 *
 */
public class FindInDB {
  
  /** Creates a new instance of FindInDB */
  public FindInDB() {
  }
  
    public Person findPerson(String ID, RealizableEntityService reService) 
  {
    Person person = null;
   // RealizableEntityService reService;
 //           ServiceLocator sl = ServiceLocator.instance();
  //reService = sl.getRealizableEntityService();
    try {
      person = ( Person ) reService.findLatestByEndurant(ID);
      
    } catch (RealizableEntityServiceException ex) {
      ex.printStackTrace();
    
    }
      return person;
  }
  
}
