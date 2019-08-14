package com.fyp.motorcyclefix.RiderFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragments.ViewWorkshopActivity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private ProgressBar progressBar;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private ArrayList<Workshop> workshopList = new ArrayList<>();
    private Workshop workshop;
    private FusedLocationProviderClient mlocationProviderClient;
    private Marker marker;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rider_maps_fragment, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        progressBar = view.findViewById(R.id.mapsProgressBar);
        mlocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (mapFragment == null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    private void prepareInfoWindow(){

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.rider_maps_custom_info_window, null);

                ImageView windowImage = view.findViewById(R.id.mapsInfoboxIcon);
                TextView title = view.findViewById(R.id.mapsInfoboxtitle);
                TextView openHours = view.findViewById(R.id.mapsOpenHours);
                RatingBar workshopRating = view.findViewById(R.id.mapsRatingbar);

                return view;
            }
        });
    }

    private void getWorkshopLocations() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        workshop = snapshot.toObject(Workshop.class);
                        workshopList.add(workshop);

                        GeoPoint geoPoint = snapshot.getGeoPoint("location");
                        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions()
                                .title(workshop.getWorkshopName())
                                .snippet(workshop.getOpeningHours())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_motorcycle_sexy_round))
                                .position(latLng);

                        marker = mMap.addMarker(markerOptions);

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View view = getLayoutInflater().inflate(R.layout.rider_maps_custom_info_window, null);

                                ImageView windowImage = view.findViewById(R.id.mapsInfoboxIcon);
                                TextView title = view.findViewById(R.id.mapsInfoboxtitle);
                                TextView openHours = view.findViewById(R.id.mapsOpenHours);
                                RatingBar workshopRating = view.findViewById(R.id.mapsRatingbar);

                                title.setText(marker.getTitle());
                                openHours.setText("Open hours: " + marker.getSnippet());


                                return view;
                            }
                        });
                    }


                }
            });
        }

    }

    private void getCurrentUserPosition() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mlocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                    getWorkshopLocations();

                }
            }
        });
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
        infoWindowClickHandler();

    }

    private void infoWindowClickHandler(){
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                progressBar.setVisibility(View.VISIBLE);
                GeoPoint loc = new GeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);

                getWorkshopId(loc);
            }
        });
    }

    private void getWorkshopId(final GeoPoint loc) {

        workshopRef.whereEqualTo("location", loc).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    String workID = snapshot.getId();
                    workshop = snapshot.toObject(Workshop.class);

                    if(workshop.getLocation().equals(loc)){

                        Intent intent = new Intent(getActivity(), ViewWorkshopActivity.class);
                        intent.putExtra("workshopId", workID);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);

                    } else{

                        Toast.makeText(getActivity(), "Locations don't match!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mapFragment.onResume();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mapFragment.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapFragment.onStop();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapFragment.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapFragment.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapFragment.onLowMemory();
//    }
}
