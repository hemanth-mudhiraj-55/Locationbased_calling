package com.example.a1;

public class place_details {

    private String description; // Renamed for clarity

    public place_details(String description) {
        this.description = description;
    }

    public String getDescription() { // ✅ Now it matches the adapter usage
        return description;
    }
}
