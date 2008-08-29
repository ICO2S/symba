package net.sourceforge.symba.webapp.util;

import java.util.*;

/**
 * This file is part of SyMBA.
 * SyMBA is covered under the GNU Lesser General Public License (LGPL).
 * Copyright (C) 2007 jointly held by Allyson Lister, Olly Shaw, and their employers.
 * To view the full licensing information for this software and ALL other files contained
 * in this distribution, please see LICENSE.txt
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL$
 */
public class GenericProtocolApplicationSummary {

    // the key is the parameter endurant identifier, and the value is the atomic value.
    private HashMap<String, String> parameterAndAtomics;
    // Fill this with any descriptions you wish to add to the GPA. The key is the description type (e.g.
    // ProtocolDescription), and the value is the description itself. This way you only allow one description per type
    private HashMap<String, String> descriptions;
    // todo The list of input materials allowed for this GPA (once GMMs are allowed)
    // The list of input complete materials allowed for this GPA
    private ArrayList<MaterialFactorsStore> inputCompleteMaterialFactors;
    // the list of pre-defined input complete materials that come as outputs of material transformations
    private Set<String> inputIdentifiersFromMaterialTransformations;
    // there are no output materials here, as this summary is only currently used for assays and not
    // material transformations.

    public GenericProtocolApplicationSummary() {
        this.parameterAndAtomics = new HashMap<String, String>();
        this.descriptions = new HashMap<String, String>();
        this.inputCompleteMaterialFactors = new ArrayList<MaterialFactorsStore>();
        this.inputIdentifiersFromMaterialTransformations = new HashSet<String>();
    }

    public Map<String, String> getParameterAndAtomics() {
        return parameterAndAtomics;
    }

    public void setParameterAndAtomics( HashMap<String, String> parameterAndAtomics ) {
        this.parameterAndAtomics = parameterAndAtomics;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions( HashMap<String, String> descriptions ) {
        this.descriptions = descriptions;
    }

    public void putDescription( String descriptionType, String description ) {
        this.descriptions.put( descriptionType, description );
    }

    public void putParameterAndAtomicPair( String parameterEndurantId, String atomicValue ) {
        this.parameterAndAtomics.put( parameterEndurantId, atomicValue );
    }

    public List<MaterialFactorsStore> getInputCompleteMaterialFactors() {
        return inputCompleteMaterialFactors;
    }

    public void setInputCompleteMaterialFactors( ArrayList<MaterialFactorsStore> inputCompleteMaterialFactors ) {
        this.inputCompleteMaterialFactors = inputCompleteMaterialFactors;
    }

    public void addInputCompleteMaterialFactor( MaterialFactorsStore inputCompleteMaterialFactor ) {
        this.inputCompleteMaterialFactors.add( inputCompleteMaterialFactor );
    }

    public void setInputCompleteMaterialFactor( MaterialFactorsStore inputCompleteMaterialFactor, int value ) {
        this.inputCompleteMaterialFactors.set( value, inputCompleteMaterialFactor );
    }

    public Set<String> getInputIdentifiersFromMaterialTransformations() {
        return inputIdentifiersFromMaterialTransformations;
    }

    public void setInputIdentifiersFromMaterialTransformations( HashSet<String> inputIdentifiersFromMaterialTransformations ) {
        this.inputIdentifiersFromMaterialTransformations = inputIdentifiersFromMaterialTransformations;
    }
}
