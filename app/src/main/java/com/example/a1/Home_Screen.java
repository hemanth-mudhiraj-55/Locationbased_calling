package com.example.a1;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home_Screen extends AppCompatActivity {

    BottomNavigationView bottomNavigationView ;
    ExploreFragment exploreFragment = new ExploreFragment();
    Contribute contribute = new Contribute();
    NotificationsFragment notificationsFragment = new NotificationsFragment();
    profile profile = new profile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        bottomNavigationView =findViewById(R.id.home_nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,exploreFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Use if-else to handle item selection
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_explore) {
                    selectedFragment = exploreFragment;
                } else if (itemId == R.id.navigation_contribute) {
                    selectedFragment = contribute;
                } else if (itemId == R.id.navigation_notifications) {
                    selectedFragment = notificationsFragment;
                } else if (itemId == R.id.navigation_Profile) {
                    selectedFragment = profile;
                }

                // Replace the current fragment with the selected fragment
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, selectedFragment)
                            .commit();
                }

                return true; // Return true to indicate the item selection is handled
            }
        });


    }
}