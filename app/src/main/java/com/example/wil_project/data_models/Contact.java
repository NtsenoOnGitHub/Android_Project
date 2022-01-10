package com.example.wil_project.data_models;

public class Contact {

    private String objectId;
    private int HitchhikerId;
    private String CellphoneNumber;
    private String AlternativeNumber;
    private String NextOfKinCellphoneNumber;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getHitchhikerId() {
        return HitchhikerId;
    }

    public void setHitchhikerId(int hitchhikerId) {
        HitchhikerId = hitchhikerId;
    }

    public String getCellphoneNumber() {
        return CellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        CellphoneNumber = cellphoneNumber;
    }

    public String getAlternativeNumber() {
        return AlternativeNumber;
    }

    public void setAlternativeNumber(String alternativeNumber) {
        AlternativeNumber = alternativeNumber;
    }

    public String getNextOfKinCellphoneNumber() {
        return NextOfKinCellphoneNumber;
    }

    public void setNextOfKinCellphoneNumber(String nextOfKinCellphoneNumber) {
        NextOfKinCellphoneNumber = nextOfKinCellphoneNumber;
    }

}
