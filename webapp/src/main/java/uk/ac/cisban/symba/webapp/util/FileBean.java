package uk.ac.cisban.symba.webapp.util;

import java.io.File;

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
public class FileBean {
  private File aFile;
  
  /** Creates a new instance of FileBean */
  public FileBean() {
  }

  public File getAFile() {
    return aFile;
  }

  public void setAFile(File aFile) {
    this.aFile = aFile;
  }
  
  public void finalize() throws Throwable
  {
    aFile.delete();
    super.finalize();
  }
  
  
}
