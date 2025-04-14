package com.example.a1;

import com.google.firebase.appdistribution.gradle.ApiService;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface retrofit_interface {
    @POST("/Registration/Confirm")
    Call<Fetch_confirm_registration> confirm_Registration(@Body HashMap<String,String> map);
    @POST("/Registration/Submit")
    Call<Fetch_submit_registration> submit_Registration(@Body HashMap<String,String> map);

    @POST("resend_email_otp")
    Call<Fetch_confirm_registration> resend_email_otp(@Body HashMap<String, String> request);

    @POST("resend_mobile_otp")
    Call<Fetch_confirm_registration> resend_mobile_otp(@Body HashMap<String, String> request);


    @POST("/Login/next")
    Call<Void> login_next(@Body HashMap<String, String> credentials);
    @POST("/Login/next2")
    Call<LoginResponse> login_next2(@Body HashMap<String, String> otpVerificationMap);
    @POST("/Login/next3")
    Call<ApiResponse> login_next3(@Body HashMap<String, String> forgetPassword);
    @POST("/Login/next4")
    Call<Void> login_next4(@Body HashMap<String, String> credentials);
    @POST("/Login/Password_changed")
    Call<Void> login_Password_change(@Body HashMap<String, String> credentials);


    @POST("Explore/IamActive")
    Call<Void> sendLocation(@Body ExploreFragment.LocationData locationData);
    @GET("/notifications")
    Call<List<Notification>> getNotifications();
    @GET("api/user/profile")
    Call<UserResponse> getUserProfile();

    @GET("api/user/call-history")
    Call<CallHistoryResponse> getCallHistory();
    @POST("/api/auth/logout")
    Call<Void> logout();

    @POST("/api/user/status")
    Call<StatusResponse> updateUserStatus(
            @Header("Authorization") String token,
            @Body StatusRequest statusRequest
    );

    @GET("reels")
    Call<List<Reel>> getReels();

    @POST("reels/{reelId}/like")
    Call<Void> likeReel(@Path("reelId") String reelId, @Query("like") boolean like);


    //Call<Place> getPlaceDetails(String placeId);
    @GET("api/places/{placeId}")
    Call<Place> getPlaceDetails(@Path("placeId") String placeId);


}
