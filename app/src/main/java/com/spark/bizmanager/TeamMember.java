package com.spark.bizmanager;

public class TeamMember {
    private String id;
    private String name;
    private String position;
    private String joinDate;
    private String imageUrl;

    public TeamMember() {
        // Required for Firebase
    }

    public TeamMember(String id, String name, String position, String joinDate, String imageUrl) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.joinDate = joinDate;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}