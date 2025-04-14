package com.example.a1;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class Place {
    private String id;
    private String name;
    private String address;
    private String category;
    private float rating;
    private int totalRatings;
    private String phoneNumber;
    private boolean isOpen;
    private List<String> imageUrls;
    private LatLng latLng;

    // Constructor
    public Place(String id, String name, LatLng latLng) {
        this.id = id;
        this.name = name;
        this.latLng = latLng;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCategory() { return category; }
    public float getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isOpen() { return isOpen; }
    public List<String> getImageUrls() { return imageUrls; }
    public LatLng getLatLng() { return latLng; }

    public void setAddress(String address) { this.address = address; }
    public void setCategory(String category) { this.category = category; }
    public void setRating(float rating) { this.rating = rating; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setOpen(boolean open) { isOpen = open; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}