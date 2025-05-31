package com.spark.bizmanager;

public class Product {
    private String id;
    private String name;
    private String price; // must match your field name
    private String imageUrl;

    // Empty constructor required for Firebase
    public Product() {
    }

    public Product(String id, String name, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }   // was getDescription in your adapter - fixed here
    public void setPrice(String price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
