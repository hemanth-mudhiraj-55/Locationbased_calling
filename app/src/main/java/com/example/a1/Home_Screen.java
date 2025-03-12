package com.example.a1;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize Toolbar and set it as the Action Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.home_nav_view);

        // Set up NavController with the Navigation Graph
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment); // Replace with your NavHostFragment ID

        // Connect BottomNavigationView with NavController
        NavigationUI.setupWithNavController(navView, navController);
    }
}