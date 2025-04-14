package com.example.a1;

// CallHistoryItem.java
public class CallHistoryItem {
    private String phoneNumber;
    private String callTime;
    private String callDuration;
    private boolean isIncoming;

    public CallHistoryItem(String phoneNumber, String callTime,
                           String callDuration, boolean isIncoming) {
        this.phoneNumber = phoneNumber;
        this.callTime = callTime;
        this.callDuration = callDuration;
        this.isIncoming = isIncoming;
    }

    // Getters
    public String getPhoneNumber() { return phoneNumber; }
    public String getCallTime() { return callTime; }
    public String getCallDuration() { return callDuration; }
    public boolean isIncoming() { return isIncoming; }
}
