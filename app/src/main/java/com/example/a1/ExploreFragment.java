package com.example.a1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    // Place Card Variables
    private PlaceCardManager placeCardManager;
    private retrofit_interface apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        apiService = RetrofitClient.getApiService();
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

        // Initialize Place Card Manager
        placeCardManager = new PlaceCardManager(view);
        placeCardManager.hideCard();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup search functionality
        setupSearchBar();

        // Setup location services
        setupLocationServices();

        // Setup location switch
        setupLocationSwitch();

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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

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

                if (placeCardManager != null) {
                    // Make sure card is visible before loading data
                    placeCardManager.showCard();
                    placeCardManager.setDetails(address);
                    // Load and display place details
                    placeCardManager.showPlaceCard(placeId);
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
        locationSwitch.setEnabled(false);
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (currentLocation != null) {
                    getPincodeAndSendLocation();
                } else {
                    locationSwitch.setChecked(false);
                    Toast.makeText(requireContext(),
                            "Current location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocationPermission() {
        // Check if permission is already granted
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed with location operations
            enableMyLocation();
            moveToCurrentLocation();
        } else {
            // Request the permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getPincodeAndSendLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationSwitch.setChecked(false);
            requestLocationPermission();
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
    }

    private void sendLocationToServer(Location location, String pincode) {
        LocationData locationData = new LocationData(
                location.getLatitude(),
                location.getLongitude(),
                pincode
        );

        apiService.sendLocation(locationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    locationSwitch.setChecked(false);
                    Toast.makeText(requireContext(),
                            "Failed to share location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                locationSwitch.setChecked(false);
                Toast.makeText(requireContext(),
                        "Network error", Toast.LENGTH_SHORT).show();
                Log.e("NetworkError", "Failed to send location", t);
            }
        });
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
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show();
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && mMap != null) {
                        currentLocation = location;
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f));
                        locationSwitch.setEnabled(true);
                    } else {
                        Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error getting location", Toast.LENGTH_SHORT).show();
                    Log.e("LocationError", "Failed to get location", e);
                });
    }

    private void handlePincodeError() {
        locationSwitch.setChecked(false);
        Toast.makeText(requireContext(), "Could not determine pincode", Toast.LENGTH_SHORT).show();
    }

    private void handleGeocoderError(IOException e) {
        locationSwitch.setChecked(false);
        Toast.makeText(requireContext(), "Address service unavailable", Toast.LENGTH_SHORT).show();
        Log.e("GeocoderError", "Geocoding failed", e);
    }

    public static class LocationData {
        private double latitude;
        private double longitude;
        private String pincode;

        public LocationData(double latitude, double longitude, String pincode) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.pincode = pincode;
        }
    }
}