package com.spark.bizmanager;
public class ImageModel {
    private String imageUrl;
    private String timestamp;
    private String key;  // Add this field to store Firebase key
    private String storagePath;  // Add this to store storage reference path

    public ImageModel() {
        // Required for Firebase
    }

    public ImageModel(String imageUrl, String timestamp) {
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}