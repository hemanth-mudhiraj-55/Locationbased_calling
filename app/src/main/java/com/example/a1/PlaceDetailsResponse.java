package com.example.a1;

import java.util.List;

public class PlaceDetailsResponse {
    private String name;
    private String address;
    private float rating;
    private int totalRatings;
    private String description;
    private List<String> imageUrls;

    // Constructor
    public PlaceDetailsResponse(String name, String address, float rating,
                                int totalRatings, String description,
                                List<String> imageUrls) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "PlaceDetailsResponse{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", totalRatings=" + totalRatings +
                ", description='" + description + '\'' +
                ", imageUrls=" + imageUrls +
                '}';
    }
}