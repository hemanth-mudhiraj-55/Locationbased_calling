package com.example.a1;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    // Map and Location Variables
    private GoogleMap mMap;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private final int FINE_PERMISSION_CODE = 1;
    private ImageButton currentLocationBtn;
    private Switch locationSwitch;
    private Location currentLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;
    private Marker currentMarker;

    // Search Variables
    private RecyclerView recyclerView;
    private List<place_details> placeDetails = new ArrayList<>();
    private Place_adapter placeAdapter;
    private EditText searchBar;
    private RequestQueue requestQueue;
    private String lastSearchedText = "";
    private String currentSearchText = "";
    private final AtomicBoolean is_active_now = new AtomicBoolean(false);


    // Place Card Variables
    private PlaceCardManager placeCardManager;
    private retrofit_interface apiService;
    private String userUid,receiverUid;
    private boolean isUserAuthenticated;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        apiService = RetrofitClient.getApiService();

        // Check authentication status from LoginResponse
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        isUserAuthenticated = sharedPreferences.getBoolean("isLoggedIn", false);
        userUid = sharedPreferences.getString("userId", null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_explore, container, false);

        // Initialize views
        currentLocationBtn = view.findViewById(R.id.btn_location);
        locationSwitch = view.findViewById(R.id.switch_active);
        searchBar = view.findViewById(R.id.SearchBar);
        recyclerView = view.findViewById(R.id.scroll);
        requestQueue = Volley.newRequestQueue(requireContext());

        // Hide switch if user is not authenticated
        if (!isUserAuthenticated) {
            locationSwitch.setVisibility(View.GONE);
            // Center the location button in the layout
            ViewGroup.LayoutParams params = currentLocationBtn.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
            }
            currentLocationBtn.requestLayout();
        }

        // Initialize Place Card Manager
        placeCardManager = new PlaceCardManager(view);
        placeCardManager.hideCard();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup search functionality
        setupSearchBar();

        // Setup location services
        setupLocationServices();

        // Setup location switch (only if authenticated)
        if (isUserAuthenticated) {
            setupLocationSwitch();
        }

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        placeAdapter = new Place_adapter(placeDetails, item -> {
            currentSearchText = item.getDescription();
            searchBar.setText(currentSearchText);
            lastSearchedText = currentSearchText;
            recyclerView.setVisibility(View.GONE);
            moveToSearchedLocation(currentSearchText, item.getPlaceId());
        });
        recyclerView.setAdapter(placeAdapter);
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString();
                if (s.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchPlace(s.toString());
                } else {
                    clearSearchResults();
                    placeCardManager.hideCard();
                }
            }
        });
    }

    private void searchPlace(String query) {
        lastSearchedText = query;
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            encodedQuery = query;
        }

        String url = getString(R.string.gomaps_link) + encodedQuery +
                "&key=" + getString(R.string.gomaps_api);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                this::handleSearchResponse,
                error -> {
                    Log.e("VolleyError", "Search failed", error);
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(request);
    }

    private void handleSearchResponse(JSONObject response) {
        try {
            List<place_details> newPlaces = new ArrayList<>();
            JSONArray predictions = response.getJSONArray("predictions");

            for (int i = 0; i < predictions.length(); i++) {
                JSONObject place = predictions.getJSONObject(i);
                newPlaces.add(new place_details(
                        place.getString("description"),
                        place.getString("place_id")
                ));
            }

            placeDetails.clear();
            placeDetails.addAll(newPlaces);
            placeAdapter.updateData(placeDetails);

        } catch (JSONException e) {
            Log.e("JSONError", "Error parsing response", e);
            Toast.makeText(requireContext(), "Error processing results", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToSearchedLocation(String address, String placeId) {



        if (!Geocoder.isPresent()) {
            Toast.makeText(requireContext(), "Geocoder service not available", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Clear previous marker
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                // Add new marker
                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(address));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

                String sender = preferences.getString("userId", null);

                HashMap<String, String> loginMap = new HashMap<>();
                loginMap.put("uid", sender); // Make sure 'sender' variable contains the correct UID

                if (placeCardManager != null) {
                    Log.d("CallFlow", "Call button clicked"); // Debug 1

                    // Create a new thread for synchronous network call (since you can't do it on main thread)
                    new Thread(() -> {
                        try {
                            // Make synchronous API call
                            Response<recevier_call_uid> response = apiService.Call_Recevier_UID(loginMap).execute();

                            // Handle response on UI thread
                            requireActivity().runOnUiThread(() -> {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.d("CallFlow", "API call successful"); // Debug 2
                                    recevier_call_uid result = response.body();

                                    if (response.code() == 1000) {
                                        Toast.makeText(requireContext(), "No Active User Found at that Particular Location", Toast.LENGTH_LONG).show();
                                    }

                                    if ("success".equals(result.getStatus())) {
                                        Log.d("CallFlow", "Receiver found: " + result.getReceiver_uid()); // Debug 3
                                        Log.d("CallFlow", "Distance: " + result.getDistance() + " meters"); // Debug 4

                                        // Handle successful call initiation
                                        receiverUid = result.getReceiver_uid();
                                        // Start call with the receiverUid

                                    } else {
                                        Log.e("CallFlow", "API error: " + result.getMessage()); // Debug 5
                                    }
                                } else {
                                    Log.e("CallFlow", "API response not successful: " + response.code()); // Debug 6
                                    try {
                                        Log.e("CallFlow", "Error body: " + response.errorBody().string()); // Debug 7
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Update UI after API call completes
                                placeCardManager.set_Receiver_UID(receiverUid);
                                placeCardManager.showCard();
                                placeCardManager.setDetails(address);
                                Log.e("Receiver UID", "..............................................................." + receiverUid);

                                placeCardManager.showPlaceCard(placeId);
                            });
                        } catch (IOException e) {
                            requireActivity().runOnUiThread(() -> {
                                Log.e("CallFlow", "API call failed", e); // Debug 8
                                if (e instanceof java.net.SocketTimeoutException) {
                                    Toast.makeText(requireContext(), "Request timed out", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                } else {
                    Log.e("ExploreFragment", "PlaceCardManager not initialized");
                }
            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("GeocoderError", "Geocoding failed", e);
            Toast.makeText(requireContext(), "Geocoding service unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearSearchResults() {
        placeDetails.clear();
        placeAdapter.updateData(placeDetails);
        recyclerView.setVisibility(View.GONE);
    }

    private void setupLocationServices() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        setupLocationPermission();
        setupMapFragment();
        setupLocationButton();
    }

    private void setupLocationPermission() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted && mMap != null) {
                        enableMyLocation();
                    } else {
                        Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupLocationButton() {
        currentLocationBtn.setOnClickListener(v -> moveToCurrentLocation());
    }

    private void setupLocationSwitch() {
        locationSwitch.setEnabled(true);
        AtomicBoolean isProcessing = new AtomicBoolean(false); // NEW: Track ongoing operations
        AtomicBoolean userInitiated = new AtomicBoolean(false); // NEW: Track user vs programmatic changes

        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Only process user-initiated changes (ignore programmatic changes)
            if (!buttonView.isPressed()) {
                return;
            }

            // Prevent concurrent operations
            if (isProcessing.get()) {
                return;
            }
            isProcessing.set(true); // NEW: Mark operation as in progress

            if (isChecked) {
                // Switch turned ON by user
                locationSwitch.setChecked(false); // Temporarily keep it off until confirmed
                Log.d("LocationSwitch", "User turned ON switch");

                checkServerAvailability(available -> {
                    if (available) {
                        if (currentLocation != null) {
                            // We have location - send to server
                            getPincodeAndSendLocation(() -> {
                                locationSwitch.setChecked(true);
                                is_active_now.set(true);
                                isProcessing.set(false); // NEW: Mark operation complete
                            });
                        } else {
                            // Need to get location first
                            moveToCurrentLocation(() -> {
                                if (currentLocation != null) {
                                    getPincodeAndSendLocation(() -> {
                                        locationSwitch.setChecked(true);
                                        is_active_now.set(true);
                                        isProcessing.set(false); // NEW: Mark operation complete
                                    });
                                } else {
                                    Toast.makeText(requireContext(),
                                            "Could not get current location",
                                            Toast.LENGTH_SHORT).show();
                                    isProcessing.set(false); // NEW: Mark operation complete
                                }
                            });
                        }
                    } else {
                        Toast.makeText(requireContext(),
                                "Server is not available. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                        isProcessing.set(false); // NEW: Mark operation complete
                    }
                });
            } else {
                // Switch turned OFF by user
                Log.d("LocationSwitch", "User turned OFF switch");
                sendLocationDeletionToServer(() -> {
                    is_active_now.set(false);
                    isProcessing.set(false); // NEW: Mark operation complete
                });
            }
        });
    }

    private void checkServerAvailability(Consumer<Boolean> callback) {
        SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);


        if (authToken == null) {
            callback.accept(false);
            return;
        }
        apiService.checkServerAvailability("Bearer " + authToken).enqueue(new Callback<ServerAliveResponse>() {
            @Override
            public void onResponse(Call<ServerAliveResponse> call, Response<ServerAliveResponse> response) {
                callback.accept(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ServerAliveResponse> call, Throwable t) {
                callback.accept(false);
            }
        });
    }
    private void getPincodeAndSendLocation(Runnable onComplete) {
        if (currentLocation == null) {
            locationSwitch.setChecked(false);
            return;
        }

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);

            if (!addresses.isEmpty() && addresses.get(0).getPostalCode() != null) {
                String pincode = addresses.get(0).getPostalCode();
                sendLocationToServer(currentLocation, pincode);
            } else {
                handlePincodeError();
            }
        } catch (IOException e) {
            handleGeocoderError(e);
        }
        onComplete.run();
    }

    private void sendLocationToServer(Location location, String pincode) {
        Log.d("sendLocationToServer","Entered into sendLocationToServer");
        SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        String authToken = preferences.getString("authToken", null);
        String userUid = preferences.getString("userId", null);

// Check if token or UID is missing
        if (authToken == null || userUid == null) {
            locationSwitch.setChecked(false);
            Toast.makeText(requireContext(), "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Prepare location data
        LocationData locationData = new LocationData(
                userUid,
                location.getLatitude(),
                location.getLongitude(),
                pincode
        );

        // 4. Attach token in the Authorization header
        apiService.sendLocation("Bearer " + authToken, locationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            "You are now active",
                            Toast.LENGTH_LONG).show();
                    locationSwitch.setChecked(true);
                    logUserActivity(true);
                } else {
                    locationSwitch.setChecked(false);
                    Toast.makeText(requireContext(),
                            "Failed to activate: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                locationSwitch.setChecked(false);
                Toast.makeText(requireContext(),
                        "Network error. Please check your connection.",
                        Toast.LENGTH_SHORT).show();
                Log.e("NetworkError", "Failed to send location", t);
            }
        });
    }

    private void sendLocationDeletionToServer(Runnable onComplete) {
        // Start with 3 retry attempts
        sendLocationDeletionToServerWithRetry(3);
        onComplete.run();
    }

    private void sendLocationDeletionToServerWithRetry(int retryCount) {
        Log.d("LocationDeletion", "Starting deletion attempt. Retries left: " + retryCount);

        checkServerAvailabilityWithTimeout(available -> {
            if (available) {
                Log.d("LocationDeletion", "Server available - proceeding with deletion");
                performLocationDeletion();
            } else if (retryCount > 0) {
                Log.w("LocationDeletion", "Server unavailable - retrying. Attempts left: " + (retryCount - 1));

                // Debug toast (only show in debug builds)
                if (BuildConfig.DEBUG) {
                    Toast.makeText(requireContext(),
                            "Server not responding. Retrying... (" + retryCount + " attempts left)\n" +
                                    "Thread: " + Thread.currentThread().getName(),
                            Toast.LENGTH_SHORT).show();
                }

                // Retry after 2 seconds with debug logging
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.d("LocationDeletion", "Executing retry attempt after delay");
                    sendLocationDeletionToServerWithRetry(retryCount - 1);
                }, 2000);

            } else {
                Log.e("LocationDeletion", "Server unavailable - no retries left");

                runOnUiThread(() -> {
                    locationSwitch.setChecked(true); // Revert switch to ON position
                    Toast.makeText(requireContext(),
                            "Server unavailable. Please try again later.",
                            Toast.LENGTH_LONG).show();

                    Log.d("LocationDeletion", "Switch reverted to ON position due to failure");
                });
            }
        }, 180000); // 5 second timeout for server check

        Log.d("LocationDeletion", "Scheduled server availability check with timeout");
    }

    // Added helper method for clarity
    private void runOnUiThread(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }
    private void checkServerAvailabilityWithTimeout(Consumer<Boolean> callback, long timeoutMs) {
        Log.d("ServerCheck", "Starting server availability check with timeout: " + timeoutMs + "ms");
        final boolean[] callbackCalled = {false};

        // Set timeout handler
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            if (!callbackCalled[0]) {
                Log.w("ServerCheck", "Timeout reached before server responded");
                callbackCalled[0] = true;
                callback.accept(false);
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, timeoutMs);
        Log.d("ServerCheck", "Timeout handler scheduled");

        // Perform actual server availability check
        SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);
        Log.d("ServerCheck", "Auth token exists: " + (authToken != null));

        if (authToken == null) {
            Log.e("ServerCheck", "No auth token available");
            if (!callbackCalled[0]) {
                callbackCalled[0] = true;
                timeoutHandler.removeCallbacks(timeoutRunnable);  // Clean up timeout
                Log.d("ServerCheck", "Immediate failure - no auth token");
                callback.accept(false);
            }
            return;
        }

        Log.d("ServerCheck", "Making server availability request");
        long requestStartTime = System.currentTimeMillis();
        apiService.checkServerAvailability("Bearer " + authToken).enqueue(new Callback<ServerAliveResponse>() {
            @Override
            public void onResponse(Call<ServerAliveResponse> call, Response<ServerAliveResponse> response) {
                long responseTime = System.currentTimeMillis() - requestStartTime;
                Log.d("ServerCheck", "Server responded in " + responseTime + "ms. Code: " + response.code());

                if (!callbackCalled[0]) {
                    callbackCalled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);  // Cancel timeout
                    boolean isAvailable = response.isSuccessful();
                    Log.d("ServerCheck", "Server available: " + isAvailable);
                    callback.accept(isAvailable);
                }
            }

            @Override
            public void onFailure(Call<ServerAliveResponse> call, Throwable t) {
                long failureTime = System.currentTimeMillis() - requestStartTime;
                Log.e("ServerCheck", "Request failed after " + failureTime + "ms", t);
                Log.d("ServerCheck", "Request URL: " + call.request().url());

                if (!callbackCalled[0]) {
                    callbackCalled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);  // Cancel timeout
                    callback.accept(false);
                }
            }
        });
    }

    private void performLocationDeletion() {
        Log.d("LocationDeletion", "Starting location deletion process");

        SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);
        String userUid = preferences.getString("userId", null);

        // Add validation for token and UID
        if (authToken == null || userUid == null) {
            handleDeletionFailure("Session expired. Please log in again.");
            return;
        }

        // Create request with only the authenticated user's UID
        InactiveRequest request = new InactiveRequest(userUid);

        apiService.deleteLocation("Bearer " + authToken, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handleDeletionSuccess();
                } else {
                    // Handle specific error cases
                    if (response.code() == 403) {
                        handleDeletionFailure("Authentication failed. Please log in again.");
                    } else {
                        handleDeletionFailure("Server error. Please try again.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleDeletionFailure("Network error. Please check your connection.");
            }
        });
    }

    // Helper method to extract error message
    private String getErrorMessage(Response<Void> response) {
        try {
            // Try to parse error body if available
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.d("LocationDeletion", "Error response body: " + errorBody);
                return "Server error: " + errorBody;
            }
            return "Server error: " + response.code();
        } catch (IOException e) {
            Log.e("LocationDeletion", "Error reading error body", e);
            return "Server error: " + response.code();
        }
    }

    private void handleDeletionSuccess() {
        Toast.makeText(requireContext(),
                "You are now inactive",
                Toast.LENGTH_LONG).show();
        logUserActivity(false);
    }

    private void handleDeletionFailure(String errorMessage) {
        locationSwitch.setChecked(true); // Revert switch to ON position
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }


    // Add this data class if you don't have it already
    class InactiveRequest {
        private String uid;

        public InactiveRequest(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            moveToCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(28.6139, 77.2090);
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        checkAndRequestLocationPermission();
    }

    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableMyLocation() {
        if (mMap == null) return;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void moveToCurrentLocation() {
        moveToCurrentLocation(() -> {});
    }

    private void moveToCurrentLocation(Runnable onComplete) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show();
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            onComplete.run();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && mMap != null) {
                        currentLocation = location;
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f));
//                        if (isUserAuthenticated) {
//                            locationSwitch.setEnabled(true);
//                        }
                    } else {
                        Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT).show();
                    Log.e("LocationError", "Failed to get location", e);
                    onComplete.run();
                });
    }

    private void handlePincodeError() {
        locationSwitch.setChecked(false);
        Toast.makeText(requireContext(), "Could not determine pincode", Toast.LENGTH_SHORT).show();
    }
    private synchronized void setLocationSwitchState(boolean enabled) {
        if (locationSwitch != null) {
            locationSwitch.setChecked(enabled);
        }
    }

    private void handleGeocoderError(IOException e) {
        locationSwitch.setChecked(false);
        Toast.makeText(requireContext(), "Address service unavailable", Toast.LENGTH_SHORT).show();
        Log.e("GeocoderError", "Geocoding failed", e);
    }

    private void logUserActivity(boolean isActive) {
        Log.d("UserActivity", "User " + userUid + " is now " + (isActive ? "active" : "inactive"));
    }

    @Override
    public void onDestroy() {
        if (locationPermissionLauncher != null) {
            locationPermissionLauncher.unregister();
        }
        super.onDestroy();
    }

    public static class LocationData {
        private String uid;
        private double latitude;
        private double longitude;
        private String pincode;

        public LocationData(String uid, double latitude, double longitude, String pincode) {
            this.uid = uid;
            this.latitude = latitude;
            this.longitude = longitude;
            this.pincode = pincode;
        }
    }
}