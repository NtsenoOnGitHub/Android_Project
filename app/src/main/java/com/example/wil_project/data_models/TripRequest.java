package com.example.wil_project.data_models;

public class TripRequest {

    private String objectId;
    private int HitchhikerId;
    private int DriverId;
    private int RouteId;
    //private boolean Status;
    private String Name;
    private String Surname;
    private String CellphoneNumber;
    private String DeparturePoint;
    private String DestinationPoint;
    private String PhotoUrl;
    private String PickupSpot;

    public String getPickupSpot() {
        return PickupSpot;
    }

    public void setPickupSpot(String pickupSpot) {
        PickupSpot = pickupSpot;
    }

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

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getRouteId() {
        return RouteId;
    }

    public void setRouteId(int routeId) {
        RouteId = routeId;
    }

//    public boolean isStatus() {
//        return Status;
//    }

//    public void setStatus(boolean status) {
//        Status = status;
//    }

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

    public String getCellphoneNumber() {
        return CellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        CellphoneNumber = cellphoneNumber;
    }

    public String getDeparturePoint() {
        return DeparturePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        DeparturePoint = departurePoint;
    }

    public String getDestinationPoint() {
        return DestinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        DestinationPoint = destinationPoint;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
