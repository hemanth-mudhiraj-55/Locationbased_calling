package com.example.a1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings extends AppCompatActivity {
    private RecyclerView settingsRecyclerView;
    private SharedPreferences sharedPreferences;
    private ImageView themePreview;
    private SettingsAdapter adapter;
    private boolean isAuthenticated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Check authentication status
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        isAuthenticated = userPrefs.getBoolean("isLoggedIn", false);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSharedPreferences();
    }

    private void initializeViews() {
        settingsRecyclerView = findViewById(R.id.settingsRecyclerView);
        themePreview = findViewById(R.id.themePreview);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private static final int[] THEME_GRADIENTS = {
            R.drawable.bg_gradient_default,
            R.drawable.bg_gradient_ocean,
            R.drawable.bg_gradient_sunset,
            R.drawable.bg_gradient_forest,
            R.drawable.bg_gradient_royal,
            R.drawable.bg_gradient_violet
    };

    private void setupRecyclerView() {
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SettingOption> settingsOptions = createSettingsOptions();
        adapter = new SettingsAdapter(settingsOptions);
        settingsRecyclerView.setAdapter(adapter);
    }

    private List<SettingOption> createSettingsOptions() {
        List<SettingOption> options = new ArrayList<>();

        // Only add personal info and change password if authenticated
        if (isAuthenticated) {
            options.add(new SettingOption("Personal Information", R.drawable.ic_person, v -> {
                // Handle personal info click
            }));
            options.add(new SettingOption("Change Password", R.drawable.ic_lock, v -> {
                // Handle password change click
            }));
        }

        // Add these options regardless of authentication status
        options.add(new SettingOption("Notification Settings", R.drawable.ic_notifications, v -> {
            // Handle notifications click
        }));
        options.add(new SettingOption("App Theme", R.drawable.ic_palette, v -> showThemeSelectorDialog()));
        options.add(new SettingOption("Help & Support", R.drawable.ic_help, v -> {
            // Handle help click
        }));
        options.add(new SettingOption("About App", R.drawable.ic_info, v -> {
            // Handle about click
        }));

        return options;
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        updateThemePreview();
    }

    private void showThemeSelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_theme_selector, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        RecyclerView themeColorsRecyclerView = dialogView.findViewById(R.id.themeColorsRecyclerView);
        themeColorsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<ThemeColor> themeColors = Arrays.asList(
                new ThemeColor("Default", R.drawable.bg_gradient_default),
                new ThemeColor("Ocean", R.drawable.bg_gradient_ocean),
                new ThemeColor("Sunset", R.drawable.bg_gradient_sunset),
                new ThemeColor("Forest", R.drawable.bg_gradient_forest),
                new ThemeColor("Royal", R.drawable.bg_gradient_royal),
                new ThemeColor("Violet", R.drawable.bg_gradient_violet)
        );

        ThemeColorAdapter adapter = new ThemeColorAdapter(themeColors, selectedColor -> {
            saveSelectedTheme(selectedColor.getDrawableRes());
            dialog.dismiss();
        });
        themeColorsRecyclerView.setAdapter(adapter);
    }

    private void saveSelectedTheme(int themeResId) {
        sharedPreferences.edit()
                .putInt("selectedTheme", themeResId)
                .apply();
        updateThemePreview();
        recreate();
    }

    private void updateThemePreview() {
        if (sharedPreferences != null) {
            int selectedTheme = sharedPreferences.getInt("selectedTheme", R.drawable.bg_gradient_default);
            themePreview.setImageResource(selectedTheme);
        }
    }
}