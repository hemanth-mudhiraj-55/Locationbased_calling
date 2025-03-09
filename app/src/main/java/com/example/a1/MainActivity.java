package com.example.a1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar (optional)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Use the WindowInsetsController API to enable edge-to-edge experience for Android 11 (API level 30) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11 (API 30) and higher
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                // Hide the status bar and navigation bar to allow content to extend to the edges
                insetsController.hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                // Optionally, allow swiping to show system bars
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // For Android 5.0 to Android 10
            // For older versions, use the system UI flags for immersive mode
            getWindow().getDecorView().setSystemUiVisibility(
                    android.view.View.SYSTEM_UI_FLAG_FULLSCREEN |
                            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        // Set the content view
        setContentView(R.layout.activity_main);

        // Transition to the registration screen after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iHome = new Intent(MainActivity.this, registration.class);
                startActivity(iHome);
                finish();
            }
        }, 3000);
    }
}
