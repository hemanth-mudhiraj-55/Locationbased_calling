package com.example.a1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface retrofit_interface {
    @POST("/Confirm")
    Call<fetch_confirm_registration> confirm_Registration(@Body HashMap<String,String> map);
    @POST("/Submit")
    Call<fetch_submit_registration> submit_Registration(@Body HashMap<String,String> map);
    @GET("notifications")
    Call<List<fetch_notifications>> getNotifications();
}
