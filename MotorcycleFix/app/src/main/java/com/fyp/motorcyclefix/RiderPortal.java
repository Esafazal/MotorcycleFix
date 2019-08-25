package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Listeners.CalculateDistance;
import com.fyp.motorcyclefix.Listeners.ShowEmergencyAlert;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragment;
import com.fyp.motorcyclefix.RiderFragments.MapsFragment;
import com.fyp.motorcyclefix.RiderFragments.SettingsFragment;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragment;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragments.GetServiceRating;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class RiderPortal extends AppCompatActivity {
    //constant tag
    public static final String TAG = "riderPortal";
    //variable declaration and initilization
    private Fragment selectedFragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        //firebase user instance is initilized and the user id is retrieved
        user = mAuth.getCurrentUser();
        String userId = user.getUid();
        //initilization of firebase messaging to subscribe to a topic to get notifications sent to that particular topic.
        FirebaseMessaging.getInstance().subscribeToTopic(userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_portal_activity);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Nearby Workshops");

        //initilizr firebase user instance and get user id
        user = mAuth.getCurrentUser();
        String userId = user.getUid();
        //method calls get workshop feedback and get any emergencies nearby
        leaveFeedback(userId);
        getAnyEmergenciesSOS(userId);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new MapsFragment()).commit();
    }

    //bottom  navigation view with item click listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            /* switch case to identify 5 button clicks, wihtin each case, a fragment instance
             is created and the loadFragment method is called with the fragment instance as
              the argument and lastly setting a suitable title  for the fragment class*/

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
                    setTitle("Track Bookings");
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


    //method contains a snapsho listner to listen to completed bookings of the current user
    private void leaveFeedback(String userId){
        //Query
        db.collection("bookings")
                .whereEqualTo("status", "completed")
                .whereEqualTo("userId", userId)
                .whereEqualTo("ratingStatus", "unrated")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                for(QueryDocumentSnapshot snap : snapshot){
                    Booking booking = snap.toObject(Booking.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("bookId", snap.getId());
                    bundle.putString("workId", booking.getWorkshopId());
                    //alertDialog is a triggered to get user rating for the service recieved
                    GetServiceRating getServiceRating = new GetServiceRating();
                    getServiceRating.setArguments(bundle);
                    getServiceRating.show(getSupportFragmentManager(), TAG);
                }
            }
        });
    }
    //method contains a snapsho listner to listen to SOS messages from riders facing breakdowns nearby to current user
    private void getAnyEmergenciesSOS(final String userId) {
        //Query
        db.collection("SOS").whereEqualTo("status", "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        //loop to get location of rider facing breakdown
                        for(QueryDocumentSnapshot snap : snapshot){
                            SOS sos = snap.toObject(SOS.class);

                            String docId = snap.getId();

                            if(docId.equals(userId)){
                                return;
                            }
                            GeoPoint sosGeopoint = sos.getGeoPoint();
                            //method call to get current user location
                            getCurrentUserGeoPoint(sosGeopoint, sos, userId);
                        }
                    }
                });
    }

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
                       Intent intent = new Intent(RiderPortal.this, ShowEmergencyAlert.class);
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
    //method to load the desired fragment when bottom navigation view buttons are clicked
    public void loadFragment(Fragment fragment) {
        //support fragment manager instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelay, fragment);
        fragmentTransaction.commit();

    }

}
