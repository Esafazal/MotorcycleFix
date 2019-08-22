package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Booking {

    private long bookingID;
    private String serviceType;
    private String dateOfService;
    private String repairCategory;
    private String repairDescription;
    private String userId;
    private String workshopId;
    private @ServerTimestamp
    java.util.Date dateOfBooking;
    private String vehicleId;
    private String status;
    private String model;
    private Date serviceStartTime;
    private Date serviceEndTime;
    private float starRating;
    private String ratingStatus;
    private String message;

    public Booking() {

    }

    public Booking(long bookingID, String serviceType, String dateOfService, String repairDescription
            , String userId, String vehicleId, String model, String status, String  message) {

        this.bookingID = bookingID;
        this.serviceType = serviceType;
        this.dateOfService = dateOfService;
        this.repairDescription = repairDescription;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.model = model;
        this.status = status;
        this.message = message;
    }

    public long getBookingID() {
        return bookingID;
    }

    public void setBookingID(long bookingID) {
        this.bookingID = bookingID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public Date getDateOfBooking() {
        return dateOfBooking;
    }

    public void setDateOfBooking(Date dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(String workshopId) {
        this.workshopId = workshopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepairDescription() {
        return repairDescription;
    }

    public void setRepairDescription(String repairDescription) {
        this.repairDescription = repairDescription;
    }

    public String getRepairCategory() {
        return repairCategory;
    }

    public void setRepairCategory(String repairCategory) {
        this.repairCategory = repairCategory;
    }

    public Date getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(Date serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public Date getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(Date serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public float getStarRating() {
        return starRating;
    }

    public void setStarRating(float starRating) {
        this.starRating = starRating;
    }

    public String getRatingStatus() {
        return ratingStatus;
    }

    public void setRatingStatus(String ratingStatus) {
        this.ratingStatus = ratingStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
