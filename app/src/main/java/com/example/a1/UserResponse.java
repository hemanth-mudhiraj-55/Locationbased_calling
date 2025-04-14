package com.example.a1;

// UserResponse.java
public class UserResponse {
    private String uid;  // Added uid field
    private String name;
    private String email;
    private String phone;
    private String location;
    private double balance;
    private String profileImageUrl;

    // Updated constructor to include uid
    public UserResponse(String uid, String name, String email, String phone,
                        String location, double balance, String profileImageUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.balance = balance;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters
    public String getUid() { return uid; }  // Added getter for uid
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public double getBalance() { return balance; }
    public String getProfileImageUrl() { return profileImageUrl; }
}