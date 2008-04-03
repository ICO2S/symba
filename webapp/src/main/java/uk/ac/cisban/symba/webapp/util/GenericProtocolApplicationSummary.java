package uk.ac.cisban.symba.webapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $Author:  $
 * $HeadURL: $
 */
public class GenericProtocolApplicationSummary {

    private Map<String, String> parameterAndAtomics; // the key is the parameter endurant identifier, and the value is the atomic value.
    private List<String> descriptions; // Fill this with any descriptions you wish to add to the GPA.

    public GenericProtocolApplicationSummary() {
        this.parameterAndAtomics = new HashMap<String, String>();
        this.descriptions = new ArrayList<String>();
    }

    public Map<String, String> getParameterAndAtomics() {
        return parameterAndAtomics;
    }

    public void setParameterAndAtomics( Map<String, String> parameterAndAtomics ) {
        this.parameterAndAtomics = parameterAndAtomics;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions( List<String> descriptions ) {
        this.descriptions = descriptions;
    }

    public void putParameterAndAtomicPair( String parameterEndurantId, String atomicValue ) {
        this.parameterAndAtomics.put( parameterEndurantId, atomicValue );
    }

    public void addDescription( String description ) {
        this.descriptions.add( description );
    }
}
