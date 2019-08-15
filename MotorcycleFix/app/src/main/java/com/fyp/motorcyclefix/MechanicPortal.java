package com.fyp.motorcyclefix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
import com.fyp.motorcyclefix.MechanicFragments.BookingsFragment;
import com.fyp.motorcyclefix.MechanicFragments.DashboardFragment;
import com.fyp.motorcyclefix.MechanicFragments.ProfileFragment;
import com.fyp.motorcyclefix.MechanicFragments.ProfileFragments.AddWorkshop;
import com.fyp.motorcyclefix.MechanicFragments.WorkshopInfoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MechanicPortal extends AppCompatActivity{

    private MechanicSharedPreferencesConfig mechanicPreferenceConfig;
    private Fragment fragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected (@NonNull MenuItem item){
                // Handle navigation view item clicks here.

                switch (item.getItemId()) {

                    case R.id.nav_dashboard:
                        setTitle("Dashboard");
                        fragment = new DashboardFragment();
                        loadFragment(fragment);
                        closeDrawer();
                        return true;

                    case R.id.nav_bookings:
                        setTitle("Bookings");
                        fragment = new BookingsFragment();
                        loadFragment(fragment);
                        closeDrawer();
                        return true;

                    case R.id.nav_profile:
                        setTitle("Profile");
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        closeDrawer();
                        return true;

                    case R.id.nav_workshopDetails:
                        checkWorkshopExistence();
                        closeDrawer();
                        return true;

                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MechanicPortal.this);
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
                        closeDrawer();
                        return true;
                }
                return false;
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_portal_activity);
        setTitle("Dashboard");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        mechanicPreferenceConfig = new MechanicSharedPreferencesConfig(getApplicationContext());

        navigationView.setCheckedItem(R.id.nav_dashboard);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayMechanic, new DashboardFragment()).commit();
    }

    private void checkWorkshopExistence(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        db.collection("my_workshop").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    String check = task.getResult().getString("workshopId");
                    Bundle bundle = new Bundle();

                    if(check == null){
                        setTitle("Add Workshop");
                        fragment = new AddWorkshop();
                        loadFragment(fragment);

                    } else {
                        setTitle("Workshop Details");
                        fragment = new WorkshopInfoFragment();
                        bundle.putString("workshopExists", "yes");
                        fragment.setArguments(bundle);
                        loadFragment(fragment);

                    }
                }
            }
        });
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

    public boolean closeDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayMechanic, fragment);
        fragmentTransaction.commit();
    }
}
