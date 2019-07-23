package com.fyp.motorcyclefix.Dao;

public class TrackingDao {

    private String bookingID;
    private String serviceType;
    private String bikeModel;

    public TrackingDao(String bookingID, String serviceType, String bikeModel) {
        this.bookingID = bookingID;
        this.serviceType = serviceType;
        this.bikeModel = bikeModel;
    }

    public void changeBikeModel(String bikeModel){
        this.bikeModel = bikeModel;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getBikeModel() {
        return bikeModel;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setBikeModel(String bikeModel) {
        this.bikeModel = bikeModel;
    }
}
