package com.example.a1;

public class StatusRequest {
    private String userId;
    private boolean isActive;
    private LocationData location;
    private String pincode; // New field for pincode

    public StatusRequest(String userId, boolean isActive, LocationData location, String pincode) {
        this.userId = userId;
        this.isActive = isActive;
        this.location = location;
        this.pincode = pincode;
    }

    // Getters
    public String getUserId() { return userId; }
    public boolean isActive() { return isActive; }
    public LocationData getLocation() { return location; }
    public String getPincode() { return pincode; }
}