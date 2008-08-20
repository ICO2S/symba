package net.sourceforge.symba.webapp.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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

    // If it is an experiment with no material transformations, this value is filled ONLY during loadFuge, to allow
    // discussion between loadMaterial and loadProtocols. However, if this experiment type contains material
    // transformations, then the material is already chosen from a list of pre-existing ones, and this value
    // is filled prior to entering the LoadFuge class.
    private String createdMaterialEndurant;

    private String materialType;

    // free text for treatment type, dose and length of treatment. One String per treatment type
    // having a linked hash parse preserves the order while at the same time preventing treatment duplicates
    private LinkedHashSet<String> treatmentInfo;

    // key = ontology source endurant lsid, value = ontology term endurant lsid. Stores singleton characteristics
    private HashMap<String,String> characteristics;

    // key = ontology source endurant lsid, value = ontology term endurant lsids. Stores those characteristics that
    // are allowed to have multiple selects.
    private HashMap<String,LinkedHashSet<String>> multipleCharacteristics;

    // place to store the top-level OI for a descriptor set (newly-created OIs each time)
    private String descriptorOiEndurant;
    // place to store the ontology information for those things for a descriptor set (newly-created OIs each time)
    // Contains information for a single descriptor set - all that is allowed in SyMBA at the moment
    // key = ontology source endurant lsid, value = term::termAccession. Stores singleton characteristics
    private HashMap<String,String> novelCharacteristics;

    // place to store the ontology information for those things for a descriptor set (newly-created OIs each time)
    // Contains information for a single descriptor set - all that is allowed in SyMBA at the moment
    // key = ontology source endurant lsid, value = multiple term::termAccession pairs. Stores those characteristics
    // that are allowed to have multiple selects.
    private HashMap<String,LinkedHashSet<String>> novelMultipleCharacteristics;

    // the OntologyReplacement details
    private Map<String,String> ontologyReplacements;

    public MaterialFactorsStore() {
        this.treatmentInfo = new LinkedHashSet<String>();
        this.ontologyReplacements = new HashMap<String,String>();
        this.characteristics = new HashMap<String, String>();
        this.novelCharacteristics = new HashMap<String, String>();
        this.multipleCharacteristics = new HashMap<String, LinkedHashSet<String>>();
        this.novelMultipleCharacteristics = new HashMap<String, LinkedHashSet<String>>();
    }

    public void clear() {
        this.materialName = "";
        this.createdMaterialEndurant = "";
        this.materialType = "";
        this.descriptorOiEndurant = "";
        this.treatmentInfo.clear();
        this.ontologyReplacements.clear();
        this.characteristics.clear();
        this.novelCharacteristics.clear();
        this.multipleCharacteristics.clear();
        this.novelMultipleCharacteristics.clear();
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName( String materialName ) {
        this.materialName = materialName;
    }

    public String getCreatedMaterialEndurant() {
        return createdMaterialEndurant;
    }

    public void setCreatedMaterialEndurant( String createdMaterialEndurant ) {
        this.createdMaterialEndurant = createdMaterialEndurant;
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

    public String getDescriptorOiEndurant() {
        return descriptorOiEndurant;
    }

    public void setDescriptorOiEndurant( String descriptorOiEndurant ) {
        this.descriptorOiEndurant = descriptorOiEndurant;
    }

    public HashMap<String, String> getNovelCharacteristics() {
        return novelCharacteristics;
    }

    public void setNovelCharacteristics( HashMap<String, String> novelCharacteristics ) {
        this.novelCharacteristics = novelCharacteristics;
    }

    public void addNovelCharacteristic(String ontologySourceEndurant, String ontologyTermEndurant) {
        this.novelCharacteristics.put(ontologySourceEndurant, ontologyTermEndurant);
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

    public HashMap<String, LinkedHashSet<String>> getNovelMultipleCharacteristics() {
        return novelMultipleCharacteristics;
    }

    public void setNovelMultipleCharacteristics( HashMap<String, LinkedHashSet<String>> novelMultipleCharacteristics ) {
        this.novelMultipleCharacteristics = novelMultipleCharacteristics;
    }

    public void addNovelMultipleCharacteristics(String ontologySourceEndurant, LinkedHashSet<String> ontologyTermEndurants) {
        this.novelMultipleCharacteristics.put(ontologySourceEndurant, ontologyTermEndurants);
    }

}
