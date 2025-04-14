package com.example.a1;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }
}