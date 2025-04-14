package com.example.a1;

public class LocationData {
    private double lat;  // Latitude
    private double lng;  // Longitude

    public LocationData(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getters
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}