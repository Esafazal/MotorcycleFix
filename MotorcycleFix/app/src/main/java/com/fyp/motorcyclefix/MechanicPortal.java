package com.fyp.motorcyclefix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.MechanicFragments.MechanicSchedule;
import com.fyp.motorcyclefix.MechanicFragments.PastBookingsFragment;
import com.fyp.motorcyclefix.Services.CalculateDistance;
import com.fyp.motorcyclefix.Listeners.ShowEmergencyAlert;
import com.fyp.motorcyclefix.MechanicFragments.BookingFragments.BookingRequestFragment;
import com.fyp.motorcyclefix.MechanicFragments.BookingsFragment;
import com.fyp.motorcyclefix.MechanicFragments.DashboardFragment;
import com.fyp.motorcyclefix.MechanicFragments.ProfileFragment;
import com.fyp.motorcyclefix.MechanicFragments.ProfileFragments.AddWorkshop;
import com.fyp.motorcyclefix.MechanicFragments.WorkshopInfoFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.annotation.Nullable;

public class MechanicPortal extends AppCompatActivity {

    //constant tag
    public static final String TAG = "mechanicPortal";
    //variable declaration and initilization
    private MechanicSharedPreferencesConfig mechanicPreferenceConfig;
    private Fragment fragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    private FirebaseUser currentUser;
    private DrawerLayout drawer;

    @Override
    protected void onStart() {
        super.onStart();
        //firebase user instance is initilized and the user id is retrieved
        currentUser = mAuth.getCurrentUser();
        String newUser = currentUser.getUid();
        checkIsEmailVerified(currentUser);
        //initilization of firebase messaging to subscribe to a topic to get notifications sent to that particular topic.
        FirebaseMessaging.getInstance().subscribeToTopic(newUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mechanic_portal_activity);
        setTitle("Dashboard");

        /*Initilizing widgets from layout */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //method call to check for SOS messages
        getAnyEmergenciesSOS();

        navigationView.setCheckedItem(R.id.nav_dashboard);
        //method call to check if there is a registered workshop of current user mechanic
        checkWorkshopExistence("dash");
    }

    //bottom  navigation view with item click listener
    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.

            /* switch case to identify 5 button clicks, wihtin each case, a fragment instance
             is created and the loadFragment method is called with the fragment instance as
              the argument and lastly setting a suitable title  for the fragment class*/

            switch (item.getItemId()) {

                case R.id.nav_dashboard:
                    checkWorkshopExistence("dash");
                    closeDrawer();
                    return true;

                case R.id.nav_bookings:
                    setTitle("Bookings");
                    fragment = new BookingsFragment();
                    loadFragment(fragment);
                    closeDrawer();
                    return true;

                case R.id.nav_past_bookings:
                    setTitle("Past Bookings");
                    fragment = new PastBookingsFragment();
                    loadFragment(fragment);
                    closeDrawer();
                    return true;

                case R.id.nav_Schedule:
                    setTitle("My Schedule");
                    fragment = new MechanicSchedule();
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
                    checkWorkshopExistence("work");
                    closeDrawer();
                    return true;

                case R.id.nav_logout:
                   displayAlertDialogandLogout();
                    closeDrawer();
                    return true;
            }
            return false;
        }
    };
    //
    private void displayAlertDialogandLogout(){
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(MechanicPortal.this);
        builder.setMessage("Are you sure you want to logout?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(userId);
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

    //method contains a snapsho listner to listen to SOS messages from riders facing breakdowns nearby to current user
    private void getAnyEmergenciesSOS() {
        currentUser = mAuth.getCurrentUser();
        final String userId = currentUser.getUid();
        //Query
        db.collection("SOS").whereEqualTo("status", "pending")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        //loop to get location of rider facing breakdown
                        for(QueryDocumentSnapshot snap : snapshot){
                            SOS sos = snap.toObject(SOS.class);
                            String docId = snap.getId();
                            //
                            if(docId.equals(userId)){
                                return;
                            }
                            if(sos.getRejects() != null){
                                for(String user : sos.getRejects()) {
                                    if (user.equals(userId)) {
                                        return;
                                    }
                            }
                            }
                            GeoPoint sosGeopoint = sos.getGeoPoint();
                            //method call to get current user location
                            getCurrentUserGeoPoint(sosGeopoint, sos, userId);
                        }
                    }
                });
    }
    //
    private void getCurrentUserGeoPoint(final GeoPoint sosGeoPoint, final SOS sos, String id){
        //Query to get current user info
        db.collection("users").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User me = documentSnapshot.toObject(User.class);
                GeoPoint currentUserGeo = me.getGeoPoint();

                try {
                    //static method call to distance between distance between current user and rider facing breakdown
                    double distance = CalculateDistance.calculateDistanceFormulae(sosGeoPoint, currentUserGeo);
                    //checking if distance in less or equal to 1.2km
                    if (distance <= 1.2) {
                        //show alertDialog to show emergency
                        Intent intent = new Intent(MechanicPortal.this, ShowEmergencyAlert.class);
                        intent.putExtra("userId", sos.getUserId());
                        intent.putExtra("issue", sos.getIssue());
                        intent.putExtra("mark", sos.getLandmark());
                        intent.putExtra("lat", sos.getGeoPoint().getLatitude());
                        intent.putExtra("lng", sos.getGeoPoint().getLongitude());
                        startActivity(intent);
                    }
                } catch (Exception e){
                    Log.i(TAG, e.toString());
                }
            }
        });
    }

    //Method to check id current user has a registered workshop, if not prompt to add one.
    private void checkWorkshopExistence(final String tab) {
        //get current user id
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        //Query to get workshop details
        db.collection("my_workshop").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Workshop workshop = snapshot.toObject(Workshop.class);
                            String docId = snapshot.getId().trim();

                            if (docId.equals(userId)) {
                                String check = workshop.getWorkshopId();
                                Bundle bundle = new Bundle();

                                if (check == null) {
                                    setTitle("Add Workshop");
                                    fragment = new AddWorkshop();
                                    loadFragment(fragment);

                                } else if (tab.equals("work")) {
                                    setTitle("Workshop Details");
                                    fragment = new WorkshopInfoFragment();
                                    bundle.putString("workshopExists", "yes");
                                    fragment.setArguments(bundle);
                                    loadFragment(fragment);

                                } else if (tab.equals("dash")) {
                                    setTitle("Dashboard");
                                    fragment = new DashboardFragment();
                                    bundle.putString("name", workshop.getWorkshopName());
                                    bundle.putString("location", workshop.getLocationName());
                                    fragment.setArguments(bundle);
                                    loadFragment(fragment);
                                    bookingRequestListener();
                                }
                            }
                        }
                    }
                });
    }
    //
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //
    public boolean closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //this method will load the desired fragment when called
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayMechanic, fragment);
        fragmentTransaction.commit();
    }

    //Method with snapshop listener to check if there is a any pending booking request for the registered workshop
    private void bookingRequestListener() {
        //Query to check pending request
        db.collection("bookings").whereEqualTo("workshopId", userId)
                . whereEqualTo("status", "pending")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        //check if there is an exception
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }//looping to get snapshot object to booking model
                        for(QueryDocumentSnapshot snap : snapshot){
                            Booking booking = snap.toObject(Booking.class);
                            //creaking a bundle object to pass information to next fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", booking.getUserId());
                            bundle.putString("sType", booking.getServiceType());
                            bundle.putString("sDate", String.valueOf(booking.getDateOfService()));
                            bundle.putString("vId", booking.getVehicleId());
                            bundle.putString("sDesc", booking.getRepairDescription());
                            bundle.putString("rCat", booking.getRepairCategory());
                            bundle.putLong("bookingId", booking.getBookingID());
                            //Display booking via alertDialog
                            BookingRequestFragment fragment = new BookingRequestFragment();
                            fragment.setArguments(bundle);
                            fragment.show(getSupportFragmentManager(), "ViewBookingRequest");
                        }
                    }
                });
    }

    public void checkIsEmailVerified(final FirebaseUser user){
        user.reload();
        if(!user.isEmailVerified()){
            Snackbar snack = Snackbar.make(drawer,
                    "Please verify email address", Snackbar.LENGTH_INDEFINITE)
                    .setAction("verify", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SignUpActivity signUpActivity = new SignUpActivity();
                            signUpActivity.sendEmailVerification();
                            Toast.makeText(MechanicPortal.this,
                                    "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    });
            snack.show();
        }
    }
}
