package com.example.a1;

import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface retrofit_interface {
    @POST("/Confirm")
    Call<Void> confirm_Registration(@Body HashMap<String,String> map);
    @POST("/Submit")
    Call<Void> submit_Registration(@Body HashMap<String,String> map);
}
