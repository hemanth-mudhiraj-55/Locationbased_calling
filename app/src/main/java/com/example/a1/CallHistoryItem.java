package com.example.a1;

public class CallHistoryItem {
    public enum CallType {
        OUTGOING,
        INCOMING,
        MISSED,
        REJECTED,
        BLOCKED,
        UNKNOWN
    }

    private String number;
    private String duration;
    private String time;
    private CallType callType;
    private long timestamp;
    private boolean isVideoCall;

    // Constructor with all fields
    public CallHistoryItem(String number, String duration, String time,
                           CallType callType, long timestamp, boolean isVideoCall) {
        this.number = number;
        this.duration = duration;
        this.time = time;
        this.callType = callType;
        this.timestamp = timestamp;
        this.isVideoCall = isVideoCall;
    }

    // Constructor for backward compatibility
    public CallHistoryItem(String phoneNumber, String callTime,
                           String callDuration, boolean isIncoming) {
        this(phoneNumber, callDuration, callTime,
                isIncoming ? CallType.INCOMING : CallType.OUTGOING,
                System.currentTimeMillis(),
                false);
    }

    // Getters
    public String getNumber() { return number; }
    public String getDuration() { return duration; }
    public String getTime() { return time; }
    public CallType getCallType() { return callType; }
    public long getTimestamp() { return timestamp; }
    public boolean isVideoCall() { return isVideoCall; }

    // Helper methods
    public boolean isIncoming() {
        return callType == CallType.INCOMING || callType == CallType.MISSED;
    }

    // Setters (if needed)
    public void setNumber(String number) { this.number = number; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setTime(String time) { this.time = time; }
    public void setCallType(CallType callType) { this.callType = callType; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setVideoCall(boolean videoCall) { isVideoCall = videoCall; }
}