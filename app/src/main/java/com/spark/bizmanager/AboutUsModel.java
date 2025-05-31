package com.spark.bizmanager;

public class AboutUsModel {
    private String id;
    private String name;
    private String position;
    private String date;
    private String imageUrl;

    // Required empty constructor for Firebase
    public AboutUsModel() {
    }

    public AboutUsModel(String id, String name, String position, String date, String imageUrl) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
}
