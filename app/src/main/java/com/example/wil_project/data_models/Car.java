package com.example.wil_project.data_models;

public class Car {

    private String objectId;
    private int DriverId;
    private int Seats;
    private String CarName;
    private String CarRegistration;
    private String CarDisk;
    private String CarModel;
    private String PhotoUrl;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getSeats() {
        return Seats;
    }

    public void setSeats(int seats) {
        Seats = seats;
    }

    public String getCarName() {
        return CarName;
    }

    public void setCarName(String carName) {
        CarName = carName;
    }

    public String getCarRegistration() {
        return CarRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        CarRegistration = carRegistration;
    }

    public String getCarDisk() {
        return CarDisk;
    }

    public void setCarDisk(String carDisk) {
        CarDisk = carDisk;
    }

    public String getCarModel() {
        return CarModel;
    }

    public void setCarModel(String carModel) {
        CarModel = carModel;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
