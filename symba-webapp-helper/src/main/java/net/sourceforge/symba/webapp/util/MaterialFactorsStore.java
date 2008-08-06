package net.sourceforge.symba.webapp.util;

import java.util.*;

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

public class MaterialFactorsStore {

    private String materialName;

    // Filled ONLY during loadFuge, to allow discussion between loadMaterial and loadProtocols
    private String createdMaterial;

    private String materialType;

    // free text for treatment type, dose and length of treatment. One String per treatment type
    // having a linked hash set preserves the order while at the same time preventing treatment duplicates
    private LinkedHashSet<String> treatmentInfo;

    // key = ontology source endurant lsid, value = ontology term endurant lsid. Stores singleton characteristics
    private HashMap<String,String> characteristics;

    // key = ontology source endurant lsid, value = ontology term endurant lsids. Stores those characteristics that
    // are allowed to have multiple selects.
    private HashMap<String,LinkedHashSet<String>> multipleCharacteristics;

    // the OntologyReplacement details
    private Map<String,String> ontologyReplacements;

    public MaterialFactorsStore() {
        this.treatmentInfo = new LinkedHashSet<String>();
        this.characteristics = new HashMap<String, String>();
        this.ontologyReplacements = new HashMap<String,String>();
        this.multipleCharacteristics = new HashMap<String, LinkedHashSet<String>>();
    }

    public void clear() {
        this.materialName = "";
        this.createdMaterial = "";
        this.materialType = "";
        this.treatmentInfo.clear();
        this.characteristics.clear();
        this.ontologyReplacements.clear();
        this.multipleCharacteristics.clear();
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

    public HashMap<String, String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics( HashMap<String, String> characteristics ) {
        this.characteristics = characteristics;
    }

    public void addCharacteristic(String ontologySourceEndurant, String ontologyTermEndurant) {
        this.characteristics.put(ontologySourceEndurant, ontologyTermEndurant);
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

    public HashMap<String, LinkedHashSet<String>> getMultipleCharacteristics() {
        return multipleCharacteristics;
    }

    public void setMultipleCharacteristics(HashMap<String, LinkedHashSet<String>> multipleCharacteristics) {
        this.multipleCharacteristics = multipleCharacteristics;
    }

    public void addMultipleCharacteristics(String ontologySourceEndurant, LinkedHashSet<String> ontologyTermEndurants) {
        this.multipleCharacteristics.put(ontologySourceEndurant, ontologyTermEndurants);
    }
}
