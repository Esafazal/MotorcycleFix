package com.fyp.motorcyclefix.RiderFragments.EmergencyFragements;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.MapsFragment;
import com.fyp.motorcyclefix.RiderPortal;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ShowUsersReadyToHelp extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_show_users_ready_to_help_);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        docId = getIntent().getStringExtra("docId");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.SOSMapFrame);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            this.getSupportFragmentManager().beginTransaction().replace(R.id.SOSMapFrame, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
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
        getAllUsersNeabybyRider();
        getUserReadyToHelp();
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
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                }
            }
        });
    }

    private void getUserReadyToHelp(){
        //Query to get users
        db.collection("SOS").whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("status", "accepted")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //looping through the querydocumentSnapsot to map to a model
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            SOS sos = snapshot.toObject(SOS.class);
                            getHelperContactDetails(sos);
                        }
                    }
                });
    }

    private void getHelperContactDetails(final SOS sos){
        db.collection("users").document(sos.getHelperId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                showHelperPosition(sos.getHelperLocation(), user);
            }
        });
    }

    private void showHelperPosition(GeoPoint helperLocation, User user) {
        if(helperLocation != null) {
            LatLng latLng = new LatLng(helperLocation.getLatitude(), helperLocation.getLongitude());
            //creating a marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(user.getName()+" ("+user.getType()+")")
                    .snippet(String.valueOf(user.getPhoneNumber()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //setting marker in the map
            mMap.addMarker(markerOptions);
            //info window click handler
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: +94"+marker.getSnippet()));
                    startActivity(intent);
                }
            });
        }
    }

    private void getAllUsersNeabybyRider(){
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            User user = snapshot.toObject(User.class);
                            GeoPoint userLoc = user.getGeoPoint();
                            LatLng latLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng));
                        }
                    }
                });
    }

    public void onResolvedButtonClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUsersReadyToHelp.this);
        builder.setTitle("Hope we helped!")
                .setMessage("Were you able to leave the motorcycle in a safe place?")
                .setPositiveButton("Yes, Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("SOS").document(docId)
                                .update("status", "resolved").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(ShowUsersReadyToHelp.this, RiderPortal.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                })
                .setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(ShowUsersReadyToHelp.this, RiderPortal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
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


}
