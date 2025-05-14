package com.example.a1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;

public class SessionManagment {
    private static final String SHARE_PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_LOGIN_COUNT = "login_count";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManagment(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user, String authToken) {
        // Save all user details and authentication token
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_UID, user.getUid());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_MOBILE, user.getMobile());
        editor.putString(KEY_PROFILE_PIC, user.getProfilePic());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_TOKEN, authToken);
        editor.putFloat(KEY_BALANCE, (float) user.getBalance());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setUserId(sharedPreferences.getString(KEY_USER_ID, null));
        user.setUid(sharedPreferences.getString(KEY_UID, null));
        user.setName(sharedPreferences.getString(KEY_NAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setMobile(sharedPreferences.getString(KEY_MOBILE, null));
        user.setProfilePic(sharedPreferences.getString(KEY_PROFILE_PIC, null));
        user.setGender(sharedPreferences.getString(KEY_GENDER, null));
        user.setBalance(sharedPreferences.getFloat(KEY_BALANCE, 0));
        return user;
    }

    public void updateUserProfile(String name, String profilePic, String gender) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PROFILE_PIC, profilePic);
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    public void updateBalance(double balance) {
        editor.putFloat(KEY_BALANCE, (float) balance);
        editor.apply();
    }

    public void incrementLoginCount() {
        int currentCount = sharedPreferences.getInt(KEY_LOGIN_COUNT, 0);
        if (currentCount < 2) { // Allow max 3 devices (0, 1, 2)
            editor.putInt(KEY_LOGIN_COUNT, currentCount + 1);
            editor.apply();
        }
    }

    public void decrementLoginCount() {
        int currentCount = sharedPreferences.getInt(KEY_LOGIN_COUNT, 0);
        if (currentCount > 0) {
            editor.putInt(KEY_LOGIN_COUNT, currentCount - 1);
            editor.apply();
        }
    }

    public int getLoginCount() {
        return sharedPreferences.getInt(KEY_LOGIN_COUNT, 0);
    }

    public void logoutUser() {
        decrementLoginCount();
        editor.clear();
        editor.apply();

        // Redirect to login page
        Intent intent = new Intent(context, Login_pg.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // User model class for session management
    public static class User {
        private String userId;
        private String uid;
        private String name;
        private String email;
        private String mobile;
        private String profilePic;
        private String gender;
        private double balance;

        // Getters and Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }
}