package com.example.a1;

public class Reel {
    private String reelId;
    private String videoUrl;
    private String username;
    private String caption;
    private String location;
    private String profileImageUrl;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private String audioTrackName;

    // Constructor, getters and setters
    public Reel() {}

    public Reel(String reelId, String videoUrl, String username, String caption,
                String location, String profileImageUrl, int likeCount,
                int commentCount, String audioTrackName) {
        this.reelId = reelId;
        this.videoUrl = videoUrl;
        this.username = username;
        this.caption = caption;
        this.location = location;
        this.profileImageUrl = profileImageUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.audioTrackName = audioTrackName;
        this.isLiked = false;
    }

    // Add all getters and setters here...

    public String getReelId() {
        return reelId;
    }

    public void setReelId(String reelId) {
        this.reelId = reelId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getAudioTrackName() {
        return audioTrackName;
    }

    public void setAudioTrackName(String audioTrackName) {
        this.audioTrackName = audioTrackName;
    }
}