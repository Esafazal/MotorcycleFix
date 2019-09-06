package com.fyp.motorcyclefix.RiderFragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fyp.motorcyclefix.Adapters.MapsCustomWindowAdapter;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.InfoWindow;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragments.ViewWorkshopActivity;
import com.fyp.motorcyclefix.RiderPortal;
import com.fyp.motorcyclefix.SignUpActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    //log constant
    public static final String TAG = "mapsFragment";
    //variable declarations and initilization
    private ProgressBar progressBar;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private ArrayList<Workshop> workshopList = new ArrayList<>();
    private Workshop workshop;
    private FusedLocationProviderClient mlocationProviderClient;
    private Marker marker;
    private FrameLayout mapsLayout;
    private Snackbar snack;

    //default constructor
    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflating the fragment into the activity
        View view = inflater.inflate(R.layout.rider_maps_fragment, container, false);
        //getting reference to the framelayout, in which the map will be displayed
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        progressBar = view.findViewById(R.id.mapsProgressBar);
        mapsLayout = view.findViewById(R.id.mapsLayout);
        //getting last known location of user
        mlocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //checking if map fragment object is null, if so, initilize it
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIsEmailVerified(currentUser);
    }

    //this method is called when the map is ready to be displayed
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initilizing the googleMap object
        mMap = googleMap;
        //checking if user has given given permission to get location
        if (checkPermission()) {
            //set the find my  location  button as true
            mMap.setMyLocationEnabled(true);
        } else {
            //ask permission from user to access location
            askPermission();
            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
            }
        }
        //method call to get user current location
        getCurrentUserPosition();
        //method  call to view workshop clicked
        infoWindowClickHandler(getActivity());

    }

    //method gets the last known locations of the current user
    private void getCurrentUserPosition() {
        //checks if user has granted permission to use gps
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //ask for location permission to display user current location
            askPermission();
        }
        //using the locationprovider client to get user lastknown location
        mlocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    //get the current user locations and initilizing it tolatLng object
                    Location location = task.getResult();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //setting the camera view of the map to the location of the current user
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    //method call to get all the locations of workshops
                    getWorkshopLocations();
                }
            }
        });
    }

    private void getWorkshopLocations() {
        //check if there is a user logged in
        if (currentUser != null) {
            //Query to getworkshop locations
            workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        //map the queryDocumentSnapshot object to workshop model class
                        workshop = snapshot.toObject(Workshop.class);
                        workshopList.add(workshop);
                        //getting the workshop coordinates
                        GeoPoint geoPoint = snapshot.getGeoPoint("location");
                        try {//creating a new LatLng object to pass the geocoordinates recieved
                            LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            //creating a marker on the map to display workshop
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(workshop.getWorkshopName())
                                    .snippet(workshop.getWorkshopId())
                                    .icon(vectorToBitmap(R.drawable.ic_actual_bike, getResources().getColor(R.color.colorblack)))
                                    .position(latLng);
                            //method call to get ratings and suggestion
                            getRatingAndSuggestion(workshop, markerOptions);

                        } catch (Exception e) {
                            //logs any exceptions
                            Log.d(TAG, "Marker: " + e.toString());
                        }
                    }
                }
            });
        }
    }

    private void infoWindowClickHandler(final Context context) {
        //setting info window click listener
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                progressBar.setVisibility(View.VISIBLE);
                //passing the workshop geo location coordinates
                GeoPoint loc = new GeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                getWorkshopId(loc, context);
            }
        });
    }

    private void getWorkshopId(final GeoPoint loc, final Context context) {
        //Query to get workshop details based on the location of the workshop passed in
        workshopRef.whereEqualTo("location", loc).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String workID = snapshot.getId();
                    //getting workshop details into workshop Model
                    workshop = snapshot.toObject(Workshop.class);
                    //checking if the passed locationg geo coordinates matches the coordinates queried
                    if (workshop.getLocation().equals(loc)) {
                        //display workshop activity class for user to make a booking
                        Intent intent = new Intent(context, ViewWorkshopActivity.class);
                        intent.putExtra("workshopId", workID);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                    } else {
                        //display error message
                        Toast.makeText(getActivity(), "Locations don't match!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //method converts vector graphics to bitmap
    public BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        //creating a new drawable object to pass resource file
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        //creating a bitmap object
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        //creating a canvas object and passing the bitmap object as the argument
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        //return converted  vector to bitmap resource
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void checkIsEmailVerified(final FirebaseUser user){
        user.reload();
        if(!user.isEmailVerified()){
            snack = Snackbar.make(mapsLayout,
                    "Please verify email address", Snackbar.LENGTH_INDEFINITE)
                    .setAction("verify", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SignUpActivity signUpActivity = new SignUpActivity();
                            signUpActivity.sendEmailVerification();
                            Toast.makeText(getActivity(), "Verification email sent to "
                                    + user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    });
            View view = snack.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }
    }

    private void getRatingAndSuggestion(final Workshop workshop, final MarkerOptions markerOptions){
        final InfoWindow infoWindow = new InfoWindow();
        //Query to get bookings for ratings calculation
        db.collection("bookings")
                .whereEqualTo("workshopId", workshop.getWorkshopId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snap) {
                //variables to assign calculated values
                float rating = 0;
                int count = 0;
                float average = 0;
                //looping the snapshop object and mapping to the booking model
                for (QueryDocumentSnapshot snapshot : snap) {
                    Booking booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();
                    try {
                        //getting all bookings in completed state and getting the total count
                        if (status.equals("completed")) {
                            count++;
                            rating += booking.getStarRating();
                        }
                        //log any errors thrown
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
                //averaging the star ratings
                average = rating / count;
                infoWindow.setRating(average);
                infoWindow.setOpenHours(workshop.getOpeningHours());
                if (count != 0) {
                    if (average <= 1.4) {
                        infoWindow.setSuggestion("(Poor)");
                    } else if (average <= 2) {
                        infoWindow.setSuggestion("(Average)");
                    } else if (average <= 3.4) {
                        infoWindow.setSuggestion("(Good)");
                    } else if (average <= 4.4) {
                        infoWindow.setSuggestion("(Very Good)");
                    } else {
                        infoWindow.setSuggestion("(Excellent)");
                    }
                } else {
                    infoWindow.setSuggestion("(No Ratings)");
                }
                //custom maps info window adapter  object is created to pass data
                mMap.setInfoWindowAdapter(new MapsCustomWindowAdapter(getContext()));
                //passing the markerOptions object to the market object as the parameter
                marker = mMap.addMarker(markerOptions);
                marker.setTag(infoWindow);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       if(snack != null){
           snack.dismiss();
       }
    }
}
