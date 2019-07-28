package com.fyp.motorcyclefix.Dao;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class User {

    private String documentId;
    private String type;
    private String name;
    private String email;
    private String gender;
    private List<String> models;

    public User(){
        //public no arg constructor
    }

    public User(String type, String name, String email, String gender) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.gender = gender;
    }
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
}