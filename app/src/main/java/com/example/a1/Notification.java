package com.example.a1;

public class Notification {
    private int id;
    private String title;
    private String body;
    private String timestamp;
    private boolean isSelected;

    public Notification(String title, String body, String timestamp) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.isSelected = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTimestamp() { return timestamp; }
    public boolean isSelected() { return isSelected; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setSelected(boolean selected) { isSelected = selected; }
}