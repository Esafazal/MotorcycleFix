package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SOS {

    private String userId;
    private String issue;
    private String landmark;
    private GeoPoint geoPoint;
    private String status;
    private @ServerTimestamp
    java.util.Date time;
    private String helperId;
    private List<String> rejects;
    private GeoPoint helperLocation;

    public SOS() {
    }

    public GeoPoint getHelperLocation() {
        return helperLocation;
    }

    public void setHelperLocation(GeoPoint helperLocation) {
        this.helperLocation = helperLocation;
    }

    public List<String> getRejects() {
        return rejects;
    }

    public void setRejects(List<String> rejects) {
        this.rejects = rejects;
    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
