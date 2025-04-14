package com.example.a1;

public class Fetch_registration_data {
    String uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password,genderSpinner;

    public Fetch_registration_data(String uniqueUserId, String fullName, String email, String emailOtp, String mobileNumber, String mobileOtp, String password, String genderSpinner) {
        this.uniqueUserId = uniqueUserId;
        this.fullName = fullName;
        this.email = email;
        this.emailOtp = emailOtp;
        this.mobileNumber = mobileNumber;
        this.mobileOtp = mobileOtp;
        this.password = password;
        this.genderSpinner = genderSpinner;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileOtp() {
        return mobileOtp;
    }

    public void setMobileOtp(String mobileOtp) {
        this.mobileOtp = mobileOtp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGenderSpinner() {
        return genderSpinner;
    }

    public void setGenderSpinner(String genderSpinner) {
        this.genderSpinner = genderSpinner;
    }
}
