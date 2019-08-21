package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Listeners.CalculateDistance;
import com.fyp.motorcyclefix.Listeners.ShowEmergencyAlert;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragment;
import com.fyp.motorcyclefix.RiderFragments.MapsFragment;
import com.fyp.motorcyclefix.RiderFragments.SettingsFragment;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragment;
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

    public static final String TAG = "riderPortal";

    private Fragment selectedFragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        FirebaseMessaging.getInstance().subscribeToTopic(userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_portal_activity);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        TextView mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Nearby Workshops");

        getAnyEmergenciesSOS();

        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new MapsFragment()).commit();
    }

    private void getAnyEmergenciesSOS() {
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        db.collection("SOS").whereEqualTo("status", "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                        for(QueryDocumentSnapshot snap : snapshot){
                            SOS sos = snap.toObject(SOS.class);

                            String docId = snap.getId();

                            if(docId.equals(userId)){
                                return;
                            }
                            GeoPoint sosGeopoint = sos.getGeoPoint();
                            getCurrentUserGeoPoint(sosGeopoint, sos, userId);
                        }
                    }
                });
    }

    private void getCurrentUserGeoPoint(final GeoPoint sosGeoPoint, final SOS sos, String id){
        db.collection("users").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User me = documentSnapshot.toObject(User.class);
                GeoPoint currentUserGeo = me.getGeoPoint();

               try {
                   double distance = CalculateDistance.calculateDistanceFormulae(sosGeoPoint, currentUserGeo);

                   if (distance <= 1.2) {
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

    public void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelay, fragment);
        fragmentTransaction.commit();

    }

}
