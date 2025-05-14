package com.example.a1;

public class recevier_call_uid {
    private String status;
    private String receiver_uid;
    private double distance;
    private String message;

    // Constructor
    public recevier_call_uid(String status, String receiver_uid, double distance, String message) {
        this.status = status;
        this.receiver_uid = receiver_uid;
        this.distance = distance;
        this.message = message;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}