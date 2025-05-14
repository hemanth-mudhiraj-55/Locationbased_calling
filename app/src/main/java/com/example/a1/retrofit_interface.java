package com.example.a1;

import com.google.firebase.appdistribution.gradle.ApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface retrofit_interface {
    @POST("/Registration/Confirm")
    Call<Fetch_confirm_registration> confirm_Registration(@Body HashMap<String,String> map);
    @POST("/Registration/Submit")
    Call<LoginResponse> submit_Registration(@Body HashMap<String,String> map);

    @POST("/Resend/EmailOTP")
    Call<Fetch_confirm_registration> resend_email_otp(@Body HashMap<String, String> request);


    @POST("/Login/next")
    Call<Void> login_next(@Body HashMap<String, String> credentials);
    @POST("/Login/Email_OTP_Verification/next")
    Call<LoginResponse> login_next2(@Body HashMap<String, String> otpVerificationMap);
    @POST("/Login/next3")
    Call<ApiResponse> login_next3(@Body HashMap<String, String> forgetPassword);
    @POST("/Login/next4")
    Call<Void> login_next4(@Body HashMap<String, String> credentials);
    @POST("/Login/Password_changed")
    Call<Void> login_Password_change(@Body HashMap<String, String> credentials);


    @POST("/Explore/IamActive")
    Call<Void> sendLocation(
            @Header("Authorization") String authToken, // Include token
            @Body ExploreFragment.LocationData locationData
    );

    @POST("/Explore/IamInactive")
    Call<Void> deleteLocation(
            @Header("Authorization") String authToken,
            @Body ExploreFragment.InactiveRequest request
    );

    @POST("/api/Does_Server_Alive")
    Call<ServerAliveResponse> checkServerAvailability(@Header("Authorization") String authToken);

    @POST("/Call/Recevier_UID")
    Call<recevier_call_uid> Call_Recevier_UID(@Body HashMap<String,String> map);

    @POST("/profile/logout")
    Call<LogoutResponse> logoutUser(@Header("Authorization") String token);

    @POST("api/places/search")
    Call<PlaceDetailsResponse> searchPlaceDetails(@Body PlaceSearchRequest request);
    @GET("/notifications")
    Call<List<Notification>> getNotifications();
    @GET("api/user/profile")
    Call<UserResponse> getUserProfile();

    @GET("api/user/call-history")
    Call<CallHistoryResponse> getCallHistory();


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


    Call<LoginResponse> verify_dual_otp(HashMap<String, String> otpMap);

    Call<Fetch_confirm_registration> resend_mobile_otp(HashMap<String, String> request);
}
