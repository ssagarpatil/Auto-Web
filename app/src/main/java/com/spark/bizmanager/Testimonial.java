package com.spark.bizmanager;

public class Testimonial {
    public String name;
    public String email;
    public String message;
    private String key; // For Firebase reference

    // Empty constructor required for Firebase
    public Testimonial() {
    }

    public Testimonial(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}