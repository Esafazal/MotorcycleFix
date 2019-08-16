package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.Exclude;

public class Vehicle {

    private String userId;
    private String manufacturer;
    private String model;
    private String registrationNo;
    private String powerType;
    private String purchasedYear;
    private String vehicleId;

    public Vehicle(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getPowerType() {
        return powerType;
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    public String getPurchasedYear() {
        return purchasedYear;
    }

    public void setPurchasedYear(String purchasedYear) {
        this.purchasedYear = purchasedYear;
    }
}
