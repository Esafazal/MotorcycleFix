package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class User {

    private String userId;
    private String type;
    private String name;
    private String email;
    private String gender;
    private List<String> models;
    private GeoPoint geoPoint;
    private long phoneNumber;

    public User(){
        //public no arg constructor
    }

    public User(String type, String name, String email, String gender, long phoneNumber, String userId) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public User(String type, String name, String email, String gender, GeoPoint geoPoint, long phoneNumber, String userId) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.geoPoint = geoPoint;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
