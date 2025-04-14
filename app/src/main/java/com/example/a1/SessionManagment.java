package com.example.a1;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARE_PREF_NAME ="session";
    String SESSION_KEY="session_key";

    public SessionManagment(Context context){
        sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);

    }

    public void SaveSession(User user){
        //Saves session of the user Whenever the user Logged in


    }
    public String getSession(){
        return "User_uid";
    }
}
