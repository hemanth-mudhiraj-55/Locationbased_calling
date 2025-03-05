package com.example.a1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class search_bar extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText search;
    place_adapter placeAdapter;
    List<place_details> placeDetailsList;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_home); // ✅ Set correct layout
       // EdgeToEdge.enable(this);


        search = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.scroll);

        // ✅ Initialize list before using it
        placeDetailsList = new ArrayList<>();
        placeAdapter = new place_adapter(placeDetailsList);

        // ✅ Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(placeAdapter);

        requestQueue = Volley.newRequestQueue(this);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() > 2) { // ✅ Prevents unnecessary API calls
                    searchPlace(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void searchPlace(String input) {

            // ✅ Encode input to prevent URL errors


            // ✅ Correct API URL
            String url = "https://maps.gomaps.pro/maps/api/place/queryautocomplete/json?input=" + input + "&key=AlzaSyuN5lGWKdnjdep8QvSrYf3r-CwfRwUvudG";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray predictions = response.getJSONArray("predictions");

                                placeDetailsList.clear(); // ✅ Clear old results

                                for (int i = 0; i < predictions.length(); i++) {
                                    JSONObject place = predictions.getJSONObject(i);
                                    String description = place.getString("description");

                                    placeDetailsList.add(new place_details(description)); // ✅ Add data to list
                                }

                                placeAdapter.notifyDataSetChanged(); // ✅ Update RecyclerView
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError", "Error: " + error.getMessage());
                            Toast.makeText(search_bar.this, "Error fetching places", Toast.LENGTH_SHORT).show();
                        }
                    });

            requestQueue.add(jsonObjectRequest); // ✅ Add request to queue

    }
}
