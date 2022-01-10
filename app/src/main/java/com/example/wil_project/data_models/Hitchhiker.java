package com.example.wil_project.data_models;

public class Hitchhiker {

    private String objectId;
    private int HitchhikerId;
    private boolean ApprovalStatus;
    private double Funds;
    private String Name;
    private String Surname;
    private String Gender;
    private String IdentityNumber;
    private String Username;
    private String Password;
    private String PhotoUrl;

    public double getFunds() {
        return Funds;
    }

    public void setFunds(double funds) {
        Funds = funds;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isApprovalStatus() {
        return ApprovalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        ApprovalStatus = approvalStatus;
    }

    public int getHitchhikerId() {
        return HitchhikerId;
    }

    public void setHitchhikerId(int hitchhikerId) {
        HitchhikerId = hitchhikerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getIdentityNumber() {
        return IdentityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        IdentityNumber = identityNumber;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

}
