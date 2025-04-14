package com.example.a1;

public class temp{

}
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Looper;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Switch;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.Priority;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ExploreFragmet extends Fragment implements OnMapReadyCallback {
//
//    // Constants
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
//    private static final int LOCATION_UPDATE_INTERVAL = 10000;
//    private static final int FASTEST_LOCATION_UPDATE_INTERVAL = 5000;
//
//    // UI Components
//    private GoogleMap mMap;
//    private Switch switchActive;
//    private ImageButton btnLocation;
//    private EditText searchBar;
//    private RecyclerView recyclerView;
//
//    // Location Tracking
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationCallback googleLocationCallback;
//    private Location currentLocation;
//    private boolean isTrackingActive = false;
//    private String presentPincode;
//    private boolean shouldRequestLocation = false;
//    private ActivityResultLauncher<String[]> locationPermissionRequest;
//
//    // Data
//    private List<place_details> placeDetailsList = new ArrayList<>();
//    private Place_adapter placeAdapter;
//    private RequestQueue requestQueue;
//    private PlaceCardManager placeCardManager;
//
//    public interface LocationResultCallback {
//        void onLocationSuccess(Location location);
//        void onLocationFailure();
//    }
//
//    //✅ Method is verified
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setupMapFragment();
//
//
//        locationPermissionRequest = registerForActivityResult(
//                new ActivityResultContracts.RequestMultiplePermissions(),
//                result -> {
//                    Boolean fineLocationGranted = result.getOrDefault(
//                            Manifest.permission.ACCESS_FINE_LOCATION, false);
//                    Boolean coarseLocationGranted = result.getOrDefault(
//                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
//
//                    if (fineLocationGranted != null && fineLocationGranted) {
//                        handlePermissionGranted();
//                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
//                        handlePermissionGranted();
//                    } else {
//                        handlePermissionDenied();
//                    }
//                });
//    }
//    //✅ Method is verified
//    private void handlePermissionDenied() {
//        showToast("Location permission denied");
//        switchActive.setChecked(false);
//    }
//
//
//    //✅ Method is verified
//    private void handlePermissionGranted() {
//        showToast("Location permission granted");
//        if (shouldRequestLocation) {
//            getLastLocation(null);
//            shouldRequestLocation = false;
//        }
//        if (switchActive.isChecked()) {
//            startTracking();
//        }
//    }
//
//
//    //✅ Method is verified
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_explore, container, false);
//        initializeViews(view);
//        setupLocationServices();
//        setupRecyclerView();
//        setupListeners();
//        return view;
//    }
//
//    //✅ Method is verified
//    private void initializeViews(View view) {
//        placeCardManager = new PlaceCardManager(view);
//        btnLocation = view.findViewById(R.id.btn_location);
//        switchActive = view.findViewById(R.id.switch_active);
//
//        recyclerView = view.findViewById(R.id.scroll);
//        requestQueue = Volley.newRequestQueue(requireContext());// fetch recyclerview place names
//    }
//
//
//    //✅ Method is verified
//    private void setupLocationServices() {
//        // provides a simple way to fetch the device's last known location or receive continuous
//        // location updates with high accuracy
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//        setupGoogleLocationCallback();
//    }
//
//    //✅ Method is verified
//    private void setupGoogleLocationCallback() {
//
//        // A listener that waits for location updates and runs your code when a new location is available.
//        // Continuous monitoring of the Location updates
//        googleLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null || !isTrackingActive) return;
//
//                Location location = locationResult.getLastLocation();
//                if (location != null) {
//                    handleNewLocation(location);
//                }
//            }
//        };
//    }
//
//
//    //✅ Method is verified
//    // This method sets up a Google Map fragment in apps screen.
//    private void setupMapFragment() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//    }
//
//    //✅ Method is verified
//    // This fxn is related to place details List
//    private void setupRecyclerView() {
//        placeAdapter = new Place_adapter(placeDetailsList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(placeAdapter);
//    }
//
//    //✅ Method is verified
//    private void setupListeners() {
//        btnLocation.setOnClickListener(v -> getLocationWithPermissionCheck());
//
//        switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                startTracking();
//            } else {
//                stopTracking();
//            }
//        });
//
//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void afterTextChanged(Editable s) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 2) {
//                    searchPlace(s.toString());
//                } else {
//                    placeDetailsList.clear();
//                    placeAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//    }
//
//    //✅ Method is verified
//    private void getLocationWithPermissionCheck() {
//        if (checkLocationPermissions()) {
//            getLastLocation(null);
//        } else {
//            shouldRequestLocation = true;
//            requestLocationPermissions();
//        }
//    }
//
//    //✅ Method is verified
//    private void startTracking() {
//        if (!checkLocationPermissions()) {
//            shouldRequestLocation = true;
//            requestLocationPermissions();
//            switchActive.setChecked(false);
//            return;
//        }
//
//        getLastLocation(new LocationResultCallback() {
//            @Override
//            public void onLocationSuccess(Location location) {
//                startActiveTracking(location);
//            }
//
//            @Override
//            public void onLocationFailure() {
//                switchActive.setChecked(false);
//                showToast("Couldn't get location. Please try again.");
//            }
//        });
//    }
//
//    //✅ Method is verified
//    private void stopTracking() {
//        isTrackingActive = false;
//        try {
//            fusedLocationProviderClient.removeLocationUpdates(googleLocationCallback);
//            updateUserStatusOnServer(false, null, null);
//            showToast("Location tracking disabled");
//        } catch (Exception e) {
//            Log.e("Tracking", "Error stopping updates", e);
//        }
//    }
//
//    //✅ Method is verified
//    private void startActiveTracking(Location location) {
//        isTrackingActive = true;
//        currentLocation = location;
//
//        LocationData locationData = createLocationData(location);
//        presentPincode = getPincodeFromLocation(locationData);
//
//        showLocationOnMap(locationData, "Current Location");
//        startContinuousLocationUpdates();
//        updateUserStatusOnServer(true, locationData, presentPincode);
//
//        showToast("Active tracking enabled");
//    }
//
//    //✅ Method is verified
//    private void startContinuousLocationUpdates() {
//        if (!checkLocationPermissions()) {
//            Log.e("Location", "No location permissions");
//            switchActive.setChecked(false);
//            showToast("Location permission required");
//            return;
//        }
//
//        try {
//            LocationRequest locationRequest = new LocationRequest.Builder(
//                    Priority.PRIORITY_HIGH_ACCURACY,
//                    LOCATION_UPDATE_INTERVAL
//            )
//                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_UPDATE_INTERVAL)
//                    .setWaitForAccurateLocation(true)
//                    .build();
//
//            fusedLocationProviderClient.requestLocationUpdates(
//                    locationRequest,
//                    googleLocationCallback,
//                    Looper.getMainLooper()
//            );
//        } catch (SecurityException e) {
//            Log.e("Location", "SecurityException", e);
//            switchActive.setChecked(false);
//            showToast("Location permission required");
//        }
//    }
//
//
//    //✅ Method is verified
//    private void getLastLocation(LocationResultCallback callback) {
//        if (!checkLocationPermissions()) {
//            if (callback != null) {
//                callback.onLocationFailure();
//            }
//            return;
//        }
//
//        // Check for nullability of fusedLocationProviderClient
//        if (fusedLocationProviderClient == null) {
//            if (callback != null) {
//                callback.onLocationFailure();
//            }
//            Log.e("Location", "FusedLocationProviderClient is null");
//            return;
//        }
//
//        try {
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(location -> {
//                        if (location != null) {
//                            currentLocation = location;
//                            if (callback != null) {
//                                callback.onLocationSuccess(location);
//                            }
//                        } else {
//                            Log.d("Location", "Last location is null, requesting new location");
//                            // Consider requesting a fresh location here
//                            if (callback != null) {
//                                callback.onLocationFailure();
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Location", "Error getting location", e);
//                        if (callback != null) {
//                            callback.onLocationFailure();
//                        }
//                    });
//        } catch (SecurityException e) {
//            Log.e("Location", "Security exception: " + e.getMessage());
//            if (callback != null) {
//                callback.onLocationFailure();
//            }
//        }
//    }
//
//
//    //✅ Method is verified
//    private boolean checkLocationPermissions() {
//        return ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(requireContext(),
//                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    //✅ Method is verified
//    private void requestLocationPermissions() {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            new AlertDialog.Builder(requireContext())
//                    .setTitle("Location Permission Needed")
//                    .setMessage("This app needs location permissions to track your position")
//                    .setPositiveButton("OK", (dialog, which) -> launchPermissionRequest())
//                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                    .show();
//        } else {
//            launchPermissionRequest();
//        }
//    }
//
//
//    //✅ Method is verified
//    private void handleNewLocation(Location location) {
//        if (location == null) return;
//
//        currentLocation = location;
//        LocationData locationData = createLocationData(location);
//        presentPincode = getPincodeFromLocation(locationData);
//
//        showLocationOnMap(locationData, "Current Location");
//        Log.d("LocationUpdate", "New pincode: " + presentPincode);
//    }
//
//
//    //✅ Method is verified
//    private LocationData createLocationData(Location location) {
//        return new LocationData(location.getLatitude(), location.getLongitude());
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        if (checkLocationPermissions()) {
//            enableMyLocationOnMap();
//        }
//    }
//
//    private void enableMyLocationOnMap() {
//        try {
//            mMap.setMyLocationEnabled(true);
//        } catch (SecurityException e) {
//            Log.e("Map", "SecurityException", e);
//        }
//    }
//
//
//    //✅ Method is verified
//    private void showLocationOnMap(LocationData locationData, String title) {
//        if (mMap == null) return;
//
//        LatLng latLng = new LatLng(locationData.getLat(), locationData.getLng());
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//    }
//
//    //✅ Method is verified
//    private void searchPlace(String query) {
//        String url = buildPlacesApiUrl(query);
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null,
//                this::handleSearchResponse,
//                this::handleSearchError
//        );
//
//        requestQueue.add(request);
//    }
//
//
//    //✅ Method is verified
//    private String buildPlacesApiUrl(String query) {
//        return R.string.gomaps_link + query +"&key="+R.string.gomaps_api;
//    }
//
//    //✅ Method is verified
//    private void handleSearchResponse(JSONObject response) {
//        try {
//            placeDetailsList.clear();
//            JSONArray predictions = response.getJSONArray("predictions");
//
//            for (int i = 0; i < predictions.length(); i++) {
//                JSONObject place = predictions.getJSONObject(i);
//                placeDetailsList.add(new place_details(place.getString("description")));
//            }
//
//            placeAdapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            handleSearchError(new VolleyError("Error parsing response"));
//        }
//    }
//
//    //✅ Method is verified
//    private void handleSearchError(VolleyError error) {
//        showToast("Search error: " + error.getMessage());
//        Log.e("Search", "Error searching places", error);
//    }
//
//    //✅ Method is verified
//    private String getPincodeFromLocation(LocationData locationData) {
//        Context context = getContext();
//        if (context == null) return "Unknown";
//
//        try {
//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(
//                    locationData.getLat(),
//                    locationData.getLng(),
//                    1
//            );
//
//            if (addresses != null && !addresses.isEmpty()) {
//                return addresses.get(0).getPostalCode() != null ?
//                        addresses.get(0).getPostalCode() : "Unknown";
//            }
//        } catch (IOException e) {
//            Log.e("Geocoder", "Error getting pincode", e);
//        }
//        return "Unknown";
//    }
//
//
//    private void updateUserStatusOnServer(boolean isActive, LocationData locationData, String pincode) {
//        String userId = getCurrentUserId();
//        String token = getAuthToken();
//
//        if (userId == null || token == null) {
//            Log.e("StatusUpdate", "User not authenticated");
//            return;
//        }
//
//        StatusRequest request = new StatusRequest(userId, isActive, locationData, pincode);
//        RetrofitClient.getApiService()
//                .updateUserStatus("Bearer " + token, request)
//                .enqueue(new StatusUpdateCallback());
//    }
//
//    private class StatusUpdateCallback implements Callback<StatusResponse> {
//        @Override
//        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
//            if (response.isSuccessful() && response.body() != null) {
//                // Handle successful response
//            } else {
//                try {
//                    String error = response.errorBody() != null ?
//                            response.errorBody().string() : "Unknown error";
//                    Log.e("StatusUpdate", "Error: " + error);
//                } catch (IOException e) {
//                    Log.e("StatusUpdate", "Error parsing error", e);
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<StatusResponse> call, Throwable t) {
//            Log.e("StatusUpdate", "API call failed", t);
//        }
//    }
//
//    private void launchPermissionRequest() {
//        locationPermissionRequest.launch(new String[]{
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        stopTracking();
//        if (requestQueue != null) {
//            requestQueue.cancelAll(tag -> true);
//        }
//    }
//
//    private String getCurrentUserId() {
//        // Implement actual user ID retrieval
//        return "user_id_placeholder";
//    }
//
//    private String getAuthToken() {
//        // Implement actual token retrieval
//        return "auth_token_placeholder";
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//}