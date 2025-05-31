package com.spark.bizmanager;

public class TeamModel {
    private String employeeName;
    private String awardName;
    private String date;
    private String imageUrl;
    private String key; // Added to store Firebase key

    // Empty constructor required for Firebase
    public TeamModel() {
    }

    public TeamModel(String employeeName, String awardName, String date, String imageUrl) {
        this.employeeName = employeeName;
        this.awardName = awardName;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}