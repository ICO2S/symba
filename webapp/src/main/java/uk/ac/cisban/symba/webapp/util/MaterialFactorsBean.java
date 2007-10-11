package uk.ac.cisban.symba.webapp.util;

import java.util.ArrayList;

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

public class MaterialFactorsBean {

    private String materialName;

    // Filled ONLY during loadFuge, to allow discussion between loadMaterial and loadProtocols
    private String createdMaterial;

    private String materialType;

    // free text for treatment type, dose and length of treatment. One String per treatment type
    private ArrayList<String> treatmentInfo;

    // these are all stored as Identifiers in the characteristics array.
    private ArrayList<String> characteristics;


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

    public ArrayList<String> getTreatmentInfo() {
        return treatmentInfo;
    }

    public void setTreatmentInfo( ArrayList<String> treatmentInfo ) {
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

    public ArrayList<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics( ArrayList<String> characteristics ) {
        this.characteristics = characteristics;
    }

    public void addCharacteristic( String singleCharacteristics ) {
        this.characteristics.add( singleCharacteristics );
    }
}
