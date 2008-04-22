package uk.ac.cisban.symba.webapp.util;

import java.util.HashMap;
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

    // the key is the parameter endurant identifier, and the value is the atomic value.
    private Map<String, String> parameterAndAtomics;
    // Fill this with any descriptions you wish to add to the GPA. The key is the description type (e.g.
    // ProtocolDescription), and the value is the description itself. This way you only allow one description per type
    private Map<String,String> descriptions;

    public GenericProtocolApplicationSummary() {
        this.parameterAndAtomics = new HashMap<String, String>();
        this.descriptions = new HashMap<String, String>();
    }

    public Map<String, String> getParameterAndAtomics() {
        return parameterAndAtomics;
    }

    public void setParameterAndAtomics( Map<String, String> parameterAndAtomics ) {
        this.parameterAndAtomics = parameterAndAtomics;
    }

    public Map<String,String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions( Map<String,String> descriptions ) {
        this.descriptions = descriptions;
    }

    public void putDescription( String descriptionType, String description ) {
        this.descriptions.put( descriptionType, description );
    }

    public void putParameterAndAtomicPair( String parameterEndurantId, String atomicValue ) {
        this.parameterAndAtomics.put( parameterEndurantId, atomicValue );
    }
}
