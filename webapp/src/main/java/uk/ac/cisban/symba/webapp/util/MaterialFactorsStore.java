package uk.ac.cisban.symba.webapp.util;

import java.util.*;

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

public class MaterialFactorsStore {

    private String materialName;

    // Filled ONLY during loadFuge, to allow discussion between loadMaterial and loadProtocols
    private String createdMaterial;

    private String materialType;

    // free text for treatment type, dose and length of treatment. One String per treatment type
    // having a linked hash set preserves the order while at the same time preventing treatment duplicates
    private LinkedHashSet<String> treatmentInfo;

    // these are all stored as Identifiers in the characteristics array.
    private LinkedHashSet<String> characteristics;

    // the OntologyReplacement details
    private Map<String,String> ontologyReplacements;

    public MaterialFactorsStore() {
        this.treatmentInfo = new LinkedHashSet<String>();
        this.characteristics = new LinkedHashSet<String>();
        this.ontologyReplacements = new HashMap<String,String>();
    }

    public void clear() {
        this.materialName = "";
        this.createdMaterial = "";
        this.materialType = "";
        this.treatmentInfo.clear();
        this.characteristics.clear();
        this.ontologyReplacements.clear();
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName( String materialName ) {
        this.materialName = materialName;
    }

    public String getCreatedMaterial() {
        return createdMaterial;
    }

    public void setCreatedMaterial( String createdMaterial ) {
        this.createdMaterial = createdMaterial;
    }

    public LinkedHashSet<String> getTreatmentInfo() {
        return treatmentInfo;
    }

    public void setTreatmentInfo( LinkedHashSet<String> treatmentInfo ) {
        this.treatmentInfo = treatmentInfo;
    }

    public void addTreatmentInfo( String singleTreatment ) {
        this.treatmentInfo.add( singleTreatment );
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType( String materialType ) {
        this.materialType = materialType;
    }

    public LinkedHashSet<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics( LinkedHashSet<String> characteristics ) {
        this.characteristics = characteristics;
    }

    public void addCharacteristic( String singleCharacteristics ) {
        this.characteristics.add( singleCharacteristics );
    }

    public Map<String, String> getOntologyReplacements() {
        return ontologyReplacements;
    }

    public void setOntologyReplacements( Map<String, String> ontologyReplacements ) {
        this.ontologyReplacements = ontologyReplacements;
    }

    public void putOntologyReplacementsPair( String key, String value ) {
        this.ontologyReplacements.put( key, value );
    }
}
