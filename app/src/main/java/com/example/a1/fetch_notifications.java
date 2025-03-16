package com.example.a1;

public class fetch_notifications {
    private String title;
    private String message;
    private String timestamp;


    public fetch_notifications(String title, String message, String timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}