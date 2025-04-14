package com.example.a1;

public class StatusResponse {
    private String message;
    private boolean success;
    private long timestamp;

    // Default constructor (needed for Gson/Retrofit)
    public StatusResponse() {}

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}