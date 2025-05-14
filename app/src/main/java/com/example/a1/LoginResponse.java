package com.example.a1;

import java.util.List;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String email;
    private String uid;
    private String name;
    private String mobile;
    private double balance;
    private User user;
    private List<Recharge> recentRecharges;
    private List<Call> recentCalls;
    private List<WishlistItem> wishlistItems;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        if (user != null && user.getEmail() != null) {
            return user.getEmail();
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        if (user != null && user.getUserId() != null) {
            return user.getUserId();
        }
        return uid;
    }

    public void setUserId(String userId) {
        this.uid = userId;
    }

    public String getName() {
        if (user != null && user.getName() != null) {
            return user.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        if (user != null && user.getMobile() != null) {
            return user.getMobile();
        }
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Recharge> getRecentRecharges() {
        return recentRecharges;
    }

    public void setRecentRecharges(List<Recharge> recentRecharges) {
        this.recentRecharges = recentRecharges;
    }

    public List<Call> getRecentCalls() {
        return recentCalls;
    }

    public void setRecentCalls(List<Call> recentCalls) {
        this.recentCalls = recentCalls;
    }

    public List<WishlistItem> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<WishlistItem> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    public String getProfilePic() {
        return "helo";
    }

    public static class User {
        private String email;
        private String uid;  // Changed from userId to uid to match response
        private String name;
        private String mobile;
        private double balance;
        private String profile_pic;  // Added new field
        private int LoginCount;     // Added new field

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUserId() {  // Changed from getUserId to getUid
            return uid;
        }

        public void setUserId(String uid) {  // Changed from setUserId to setUid
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public int getLoginCount() {
            return LoginCount;
        }

        public void setLoginCount(int loginCount) {
            LoginCount = loginCount;
        }


        public String getProfilePic() {
            return "helo";
        }

        public String getGender() {
            return "helo";
        }
    }

    public static class Recharge {
        private String rechargeId;
        private double amount;
        private String date;
        private String status;

        public String getRechargeId() {
            return rechargeId;
        }

        public void setRechargeId(String rechargeId) {
            this.rechargeId = rechargeId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Call {
        private String callId;
        private String phoneNumber;
        private String duration;
        private String date;
        private String type; // incoming, outgoing, missed

        public String getCallId() {
            return callId;
        }

        public void setCallId(String callId) {
            this.callId = callId;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class WishlistItem {
        private String itemId;
        private String name;
        private String description;
        private double price;
        private String imageUrl;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}