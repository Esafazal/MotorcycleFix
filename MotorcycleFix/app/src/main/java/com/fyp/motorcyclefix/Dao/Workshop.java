package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Workshop {

    private String workshopId;
    private String workshopName;
    private String openingHours;
    private GeoPoint location;
    private String locationName;
    private String address;
//    private int imageUrl;
    private ArrayList<String> specialized;

    public Workshop(){

    }

    public String getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(String workshopId) {
        this.workshopId = workshopId;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

//    public int getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(int imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public ArrayList<String> getSpecialized() {
        return specialized;
    }

    public void setSpecialized(ArrayList<String> specialized) {
        this.specialized = specialized;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
