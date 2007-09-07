package uk.ac.cisban.symba.webapp.util;

import fugeOM.Collection.FuGE;
import fugeOM.Common.Protocol.Protocol;
import fugeOM.ServiceLocator;
import fugeOM.service.RealizableEntityServiceException;
import java.util.LinkedList;
import java.util.List;
import fugeOM.service.RealizableEntityService;

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

/**
 * This class carries out a number of searches over exisiting FuGE data
 *
 */
public class CannedSearch {
 private List experiments;
 private List protocols;
 
 /** Creates a new instance of CannedSearch */
 public CannedSearch()
 {
  setExperiments(new LinkedList());
  protocols = new LinkedList();
 }
 
 public static void main(String[] args) throws RealizableEntityServiceException
 {
  new CannedSearch().listAllProtocols();
 }
 
 public void listExperimentsByPerson( String id) throws RealizableEntityServiceException
 {
  
  
  
  ServiceLocator sl = ServiceLocator.instance();
  RealizableEntityService reService = (sl.getRealizableEntityService());
  
  //Person p = new FindInDB().findPerson(EndurantId, reService);
  // Long id = p.getId();
  // List exps = reService.getExperimentsByPerson( id );
  
  
  //need to passs on the person endurant
  List exps =  reService.getAllExperimentsWithContact(id);
  for ( int x = 0; x<exps.size();x++)
  {
   FuGE fuge = ( FuGE ) exps.get(x);
   System.out.println( "(listExperimentsByPerson) Experiment in database: " + fuge.getIdentifier() );
   System.out.println( "(listExperimentsByPerson) Experiment in database: " + fuge.getName() );
   getExperiments().add(fuge);
  }
 }
 
 public List getExperiments()
 {
  return experiments;
 }
 
 public void setExperiments(List experiments)
 {
  this.experiments = experiments;
 }
 
 public void listAllProtocols() throws RealizableEntityServiceException
 {
  
  ServiceLocator sl = ServiceLocator.instance();
  RealizableEntityService reService = (sl.getRealizableEntityService());
  protocols = reService.getAllProtocols();
  for ( int x = 0; x<protocols.size();x++)
  {
   Protocol protocol = ( Protocol ) protocols.get(x);
   System.out.println( "(listAllProtocols) Experiment in database: " + protocol.getIdentifier() );
   System.out.println( "(listAllProtocols) Experiment in database: " + protocol.getName());
   
  }
 }
 
 public List getProtocols()
 {
  return protocols;
 }
 
 public void setProtocols(List protocols)
 {
  this.protocols = protocols;
 }
 
 
}
