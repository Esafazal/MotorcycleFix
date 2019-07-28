package com.fyp.motorcyclefix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.Configs.MechanicSharedPreferencesConfig;
import com.fyp.motorcyclefix.MechanicFragments.BookingRequestFragment;
import com.fyp.motorcyclefix.MechanicFragments.BookingsFragment;
import com.fyp.motorcyclefix.MechanicFragments.ProfileFragment;
import com.fyp.motorcyclefix.MechanicFragments.WorkshopInfoFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicPortal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MechanicSharedPreferencesConfig mechanicPreferenceConfig;
    private Fragment fragment;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_portal_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mechanicPreferenceConfig = new MechanicSharedPreferencesConfig(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.mechanic_portal, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            fragment = new BookingRequestFragment();
            loadFragment(R.id.frameLayBookingRequests, fragment);

        } else if (id == R.id.nav_bookings) {
            fragment = new BookingsFragment();
            loadFragment(R.id.frameLayMechanic, fragment);

        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
            loadFragment(R.id.frameLayMechanic, fragment);

        }else if (id == R.id.nav_workshopDetails) {
            fragment = new WorkshopInfoFragment();
            loadFragment(R.id.frameLayMechanic, fragment);

        }else if (id == R.id.nav_logout) {
//            mechanicPreferenceConfig.writeMechanicLoginStatus(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?")
                    .setTitle("Confirmation")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("type", "2");
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(int viewID, Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(viewID, fragment);
        fragmentTransaction.commit();
    }
}
