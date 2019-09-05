package com.fyp.motorcyclefix.Listeners;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderPortal;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ShowEmergencyAlert extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private TextView name, number, issue, landmark;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private double lat;
    private double lng;
    private String userId;
    private long phoneNo;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private Button accept, reject, close;
    private SupportMapFragment mapFragment;
    private GeoPoint geoPoint;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_emergency_alert_activity);

        initializeWidgets();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.miniFrameMap);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        userId = getIntent().getStringExtra("userId");
        getUsername();
        String issue1 = getIntent().getStringExtra("issue");
        String landMark1 = getIntent().getStringExtra("mark");
        docId = getIntent().getStringExtra("docId");
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        issue.setText(issue1);
        landmark.setText(landMark1);

//        number.setOnClickListener(this);
        accept.setOnClickListener(this);
        close.setOnClickListener(this);
        reject.setOnClickListener(this);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.miniFrameMap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    private void getUsername() {
        db.collection("users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user1 = documentSnapshot.toObject(User.class);
                phoneNo = user1.getPhoneNumber();
                name.setText(user1.getName());
                number.setText("+94" + user1.getPhoneNumber());
            }
        });

    }

    private void initializeWidgets(){
        name = findViewById(R.id.riderNameDynamic);
        number = findViewById(R.id.riderPhoneDynamic);
        issue = findViewById(R.id.issueDynamic);
        landmark = findViewById(R.id.landmarkDynamic);
        accept = findViewById(R.id.acceptedSOS);
        close = findViewById(R.id.closeSOS);
        reject = findViewById(R.id.rejectSos);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(checkPermission()){
            mMap.setMyLocationEnabled(true);
        } else{
            askPermission();

            if(checkPermission()){
                mMap.setMyLocationEnabled(true);
            }
        }
        getCurrentUserPosition();
    }

    private void getCurrentUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    //
                    Location location = task.getResult();
                    geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                    //
                    showUserInTrouble();
                }
            }
        });
    }

    private void showUserInTrouble() {
        //passing the globle  variable containing lat lng coordinates
        LatLng latLng = new LatLng(lat, lng);
        //
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("I'm Here!");
        mMap.addMarker(markerOptions);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.acceptedSOS:
                changeStatusInDB();
                return;

            case R.id.rejectSos:
                addUserToRejectList();
                return;

            case R.id.closeSOS:
                onBackPressed();
                return;
        }
    }

    private void addUserToRejectList() {
        db.collection("SOS").document(docId)
                .update("rejects", FieldValue.arrayUnion(currentUser.getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        });
    }

    private void changeStatusInDB() {
        Map<String, Object> update = new HashMap<>();
        update.put("status", "accepted");
        update.put("helperId", currentUser.getUid());
        update.put("helperLocation", geoPoint);

        db.collection("SOS").document(docId)
                .update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
            }
        });
    }

}
