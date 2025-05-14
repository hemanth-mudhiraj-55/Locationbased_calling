package com.example.a1;

import static im.zego.uikit.libuikitreport.CommonUtils.getApplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceCardManager {
    private final Context context;
    private final CardView placeCard;
    private final LinearLayout imageContainer;
    private final TextView placeName, placeAddress, ratingText;
    private final RatingBar placeRating;
    private final ImageView placeSave;
    private final ImageButton btnClose;
    private final ZegoSendCallInvitationButton btnCall,btnviocecall;

    private boolean isSaved = false;
    private String sender;
    private Place currentPlace;
    private final retrofit_interface apiService;

    public PlaceCardManager(View rootView) {
        this.context = rootView.getContext();
        this.placeCard = rootView.findViewById(R.id.place_card);
        this.imageContainer = rootView.findViewById(R.id.image_container);
        this.placeName = rootView.findViewById(R.id.place_name);
        this.placeAddress = rootView.findViewById(R.id.place_address);
        this.placeRating = rootView.findViewById(R.id.place_rating);
        this.ratingText = rootView.findViewById(R.id.rating_text);
        this.placeSave = rootView.findViewById(R.id.place_save);
        this.btnClose = rootView.findViewById(R.id.btn_close_card);
        this.btnCall = rootView.findViewById(R.id.btn_call);
        this.btnviocecall = rootView.findViewById(R.id.btn_voice_call);
        //this.btnShare = rootView.findViewById(R.id.btn_share);

        apiService = RetrofitClient.getApiService();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        sender = sharedPreferences.getString("userId", null);


        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        long appID = Long.parseLong(context.getString(R.string.Zego_app_id));
        String appSign = context.getString(R.string.Zego_app_sign);

        Log.e("Sender id",".............................................."+sender);

        ZegoUIKitPrebuiltCallService.init(
                getApplication(),
                appID,
                appSign,
                sender,
                sender,
                callInvitationConfig
        );
        setupListeners();
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> hideCard());

        placeSave.setOnClickListener(v -> {
            isSaved = !isSaved;
            updateSaveButton();
            showToast(isSaved ? "Saved to favorites" : "Removed from favorites");
        });



//        btnShare.setOnClickListener(v -> {
//            if (currentPlace != null) {
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT,
//                        "Check out " + currentPlace.getName() + " at " +
//                                currentPlace.getAddress() + "\n\n" +
//                                "https://maps.google.com/?q=" + currentPlace.getLatLng().latitude +
//                                "," + currentPlace.getLatLng().longitude);
//                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
//            }
//        });
    }

    private void setupVideoCall(String recevier) {
        Log.e("Enterd",".............................................................................."+recevier);
//        Log.e("VideoCall",recevier);
        btnCall.setIsVideoCall(true);
        btnCall.setResourceID("zego_uikit_call");
        btnCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(recevier,recevier)));

    }

    private void setupVoiceCall(String recevier) {
        btnviocecall.setIsVideoCall(false);
        btnviocecall.setResourceID("zego_uikit_call");
        btnviocecall.setInvitees(Collections.singletonList(new ZegoUIKitUser(recevier,recevier)));
    }

    public void showPlaceCard(String placeId) {
        fetchPlaceDetails(placeId);
    }

    private void fetchPlaceDetails(String placeId) {
        // Assuming you have an API endpoint to get place details by ID
        Call<Place> call = apiService.getPlaceDetails(placeId);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPlace = response.body();
                    updateCardContent();
                    placeCard.setVisibility(View.VISIBLE);
                } else {
                    showToast("Failed to load place details");
                    Log.e("PlaceCardManager", "Failed to fetch place details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                showToast("Network error. Please try again.");
                Log.e("PlaceCardManager", "Error fetching place details", t);
            }
        });
    }

    private void updateCardContent() {
        if (currentPlace == null) return;

        placeName.setText(currentPlace.getName());
        placeAddress.setText(currentPlace.getAddress());

        if (currentPlace.getRating() > 0) {
            placeRating.setRating(currentPlace.getRating());
            ratingText.setText(String.format("%.1f (%d)",
                    currentPlace.getRating(),
                    currentPlace.getTotalRatings()));
        }

        loadImages(currentPlace.getImageUrls());
        updateSaveButton();
    }

    private void loadImages(List<String> imageUrls) {
        imageContainer.removeAllViews();

        if (imageUrls == null || imageUrls.isEmpty()) {
            addDefaultImage();
            return;
        }

        int imageSize = (int) (context.getResources().getDisplayMetrics().density * 200);
        int margin = (int) (context.getResources().getDisplayMetrics().density * 8);

        for (String imageUrl : imageUrls) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    imageSize,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, margin, 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageView);

            imageContainer.addView(imageView);
        }
    }

    private void addDefaultImage() {
        ImageView defaultImage = new ImageView(context);
        defaultImage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        defaultImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        defaultImage.setImageResource(R.drawable.ic_placeholder);
        imageContainer.addView(defaultImage);
    }

    private void updateSaveButton() {
        placeSave.setImageResource(isSaved ?
                R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);
    }

    public void hideCard() {
        placeCard.setVisibility(View.GONE);
    }
    public void showCard() {
        if (placeCard != null) {
            placeCard.setVisibility(View.VISIBLE);
        }
    }

    public boolean isCardVisible() {
        return placeCard.getVisibility() == View.VISIBLE;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void setDetails(String Place_name) {
        String firstWord = Place_name.trim().split("[^a-zA-Z]+")[0];
        placeName.setText(firstWord);
        placeAddress.setText(Place_name);
        placeRating.setRating(4.5F);
    }

    public void set_Receiver_UID(String receiverUid) {
        setupVoiceCall(receiverUid);
        setupVideoCall(receiverUid);
    }
}