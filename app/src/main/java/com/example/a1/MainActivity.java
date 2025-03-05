package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Use getSupportActionBar() to avoid null pointer exception
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
