package com.fyp.motorcyclefix.Dao;

import java.util.Date;

public class AcceptedBooking {

    private String bikeMakeModel;
    private String riderName;
    private long bookingNo;
    private String riderNumber;
    private String serviceType;
    private Date date;
    private String repairDescription;
    private String repairCategory;
    private String startColor;
    private String endColor;
    private String message;
    private String status;
    private String userId;
    private float rating;

    public AcceptedBooking(String bikeMakeModel, String riderName, long bookingNo, String riderNumber
            , String serviceType, Date date, String repairDescription, String repairCategory
            , String startColor, String endColor, String message, String status, String userId, float rating) {
        this.bikeMakeModel = bikeMakeModel;
        this.riderName = riderName;
        this.bookingNo = bookingNo;
        this.riderNumber = riderNumber;
        this.serviceType = serviceType;
        this.date = date;
        this.repairDescription = repairDescription;
        this.repairCategory = repairCategory;
        this.startColor = startColor;
        this.endColor = endColor;
        this.message = message;
        this.status = status;
        this.userId = userId;
        this.rating = rating;

    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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

    public long getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(long bookingNo) {
        this.bookingNo = bookingNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBikeMakeModel() {
        return bikeMakeModel;
    }

    public void setBikeMakeModel(String bikeMakeModel) {
        this.bikeMakeModel = bikeMakeModel;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }


    public String getRiderNumber() {
        return riderNumber;
    }

    public void setRiderNumber(String riderNumber) {
        this.riderNumber = riderNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

    public String getStartColor() {
        return startColor;
    }

    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
