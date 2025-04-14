package com.example.a1;

public class place_details {
    private String description;
    private String placeId;

    public place_details(String description, String placeId) {
        this.description = description;
        this.placeId = placeId;
    }

    public String getDescription() {
        return description;
    }

    public String getPlaceId() {
        return placeId;
    }
}