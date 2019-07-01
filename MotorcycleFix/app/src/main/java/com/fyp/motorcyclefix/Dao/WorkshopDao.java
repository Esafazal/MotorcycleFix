package com.fyp.motorcyclefix.Dao;

public class WorkshopDao {

    private int workshopImg;
    private String workshopName;
    private String specalized;


    public WorkshopDao(int workshopImg, String workshopName, String specalized) {
        this.workshopImg = workshopImg;
        this.workshopName = workshopName;
        this.specalized = specalized;
    }

    public int getWorkshopImg() {
        return workshopImg;
    }

    public void setWorkshopImg(int workshopImg) {
        this.workshopImg = workshopImg;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getSpecalized() {
        return specalized;
    }

    public void setSpecalized(String specalized) {
        this.specalized = specalized;
    }
}
