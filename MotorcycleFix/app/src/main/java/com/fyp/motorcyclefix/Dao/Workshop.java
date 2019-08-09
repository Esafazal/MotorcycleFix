package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Workshop {

    private String documentId;
    private String workshopName;
    private String openingHours;
    private GeoPoint location;
    private String locationName;
    private double lat;
    private double lng;
//    private int imageUrl;
    private List<String> specialized;

    public Workshop(){

    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

//    public int getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(int imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public List<String> getSpecialized() {
        return specialized;
    }

    public void setSpecialized(List<String> specialized) {
        this.specialized = specialized;
    }
}
