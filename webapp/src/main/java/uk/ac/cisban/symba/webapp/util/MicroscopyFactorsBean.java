package uk.ac.cisban.symba.webapp.util;

import java.util.ArrayList;

/**
 * User: allyson
 * Date: 24-Apr-2007
 */
public class MicroscopyFactorsBean {

    private String pdNumber;

    // Filled ONLY during loadFuge, to allow discussion between loadMaterial and loadProtocols
    private String createdMaterial;

    // this should be a controlled vocabulary, but I haven't received that CV yet from Glyn,
    // so currently just something that must be filled in manually.
    private String cellType;

    // free text for treatment type, dose and length of treatment. One String per treatment type
    private ArrayList<String> treatmentInfo;

    // if isTransfection is true, the user must select one or more DNA constructs
    // this should be a controlled vocabulary, but I haven't received that CV yet from Glyn,
    // so currently just something that must be filled in manually.
    // --
    // if isFluorescent is true, they type of fluorescent dye must be defined - there may be one or more
    // this should be a controlled vocabulary, but I haven't received that CV yet from Glyn,
    // so currently just something that must be filled in manually.
    // -
    // if isAntibodies is true, then up to 3 primary and 3 secondary antibody types can be selected.
    // this should be a controlled vocabulary, but I haven't received that CV yet from Glyn,
    // so currently just something that must be filled in manually.
    // --
    // these are all stored as Identifiers in the characteristics array.
    private ArrayList<String> characteristics;


    public String getPdNumber() {
        return pdNumber;
    }

    public void setPdNumber( String pdNumber ) {
        this.pdNumber = pdNumber;
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

    public String getCellType() {
        return cellType;
    }

    public void setCellType( String cellType ) {
        this.cellType = cellType;
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
