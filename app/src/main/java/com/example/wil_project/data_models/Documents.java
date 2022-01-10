package com.example.wil_project.data_models;

public class Documents {

    private String objectId;
    private int HitchhikerId;
    private String IdentityDocumentUrl;
    private String LicenseUrl;
    private String ProofOfAddressUrl;

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

    public String getIdentityDocumentUrl() {
        return IdentityDocumentUrl;
    }

    public void setIdentityDocumentUrl(String identityDocumentUrl) {
        IdentityDocumentUrl = identityDocumentUrl;
    }

    public String getLicenseUrl() {
        return LicenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        LicenseUrl = licenseUrl;
    }

    public String getProofOfAddressUrl() {
        return ProofOfAddressUrl;
    }

    public void setProofOfAddressUrl(String proofOfAddressUrl) {
        ProofOfAddressUrl = proofOfAddressUrl;
    }

}
