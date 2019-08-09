package com.fyp.motorcyclefix;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.RiderFragments.EmergencyFragment;
import com.fyp.motorcyclefix.RiderFragments.MapsFragment;
import com.fyp.motorcyclefix.RiderFragments.SettingsFragment;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragment;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RiderPortal extends AppCompatActivity {

    private Fragment selectedFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new MapsFragment();
                    loadFragment(selectedFragment);
                    setTitle("Nearby Workshops");
                    return true;
                case R.id.navigation_workshops:
                    selectedFragment = new WorkshopFragment();
                    loadFragment(selectedFragment);
                    setTitle("Find Workshops");
                    return true;
                case R.id.navigation_emergency:
                    selectedFragment = new EmergencyFragment();
                    loadFragment(selectedFragment);
                    setTitle("Emergency");
                    return true;
                case R.id.navigation_tracking:
                    selectedFragment = new TrackingFragment();
                    loadFragment(selectedFragment);
                    setTitle("Track Booking");
                    return true;
                case R.id.navigation_settings:
                    selectedFragment = new SettingsFragment();
                    loadFragment(selectedFragment);
                    setTitle("Profile and Settings");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_portal_activity);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        TextView mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Nearby Workshops");

        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new MapsFragment()).commit();
    }

    public void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelay, fragment);
        fragmentTransaction.commit();

    }

}
