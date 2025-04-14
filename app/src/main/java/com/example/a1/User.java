package com.example.a1;

public class User {
    private String name;
    private String email;
    private String uid;
    private String mobile;
    private String profile_pic;

    // Getters and Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }


    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUid() { return uid; }
    public String getMobile() { return mobile; }
    public String getProfile_pic() { return profile_pic; }
}