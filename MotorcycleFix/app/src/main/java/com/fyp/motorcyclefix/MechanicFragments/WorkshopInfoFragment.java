package com.fyp.motorcyclefix.MechanicFragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.MechanicPortal;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.SettingsFragments.ChooseManufacturer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class WorkshopInfoFragment extends Fragment implements View.OnClickListener {
    //constant for logging
    public static final String TAG = "workshopFragment";
    //vairable declarations and initilizations
    private EditText wName, wAddress, wPlace, wOpenHours, wLat, wLng;
    private TextView wSpecialized;
    private Button update;
    private FusedLocationProviderClient  mfusedLocationProviderClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Workshop workshop;
    private ArrayList<String> workshopSpecialized = new ArrayList<>();
    private ProgressBar progressBar;

    public WorkshopInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_workshop_info_fragment_, container, false);
        //widget references
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        String WorkshopExist = getArguments().getString("workshopExists");
        update = view.findViewById(R.id.workshopUpdateButton);
        wName = view.findViewById(R.id.workshopNameEditT);
        wAddress = view.findViewById(R.id.workshopAddressEditT);
        wPlace = view.findViewById(R.id.workshopLocationEditT);
        wOpenHours = view.findViewById(R.id.workshopOpenhoursEditT);
        wLat = view.findViewById(R.id.workshopLatEditT);
        wLng = view.findViewById(R.id.workshopLngEditT);
        wSpecialized = view.findViewById(R.id.workshopSpecializedEditT);
        progressBar = view.findViewById(R.id.mechanicWorkshopProgressBar);
        //
        workshop = new Workshop();
        if(WorkshopExist.equals("no")){
            update.setText("Add Workshop");
            progressBar.setVisibility(View.GONE);
            //method calll to get location of workshopand dd workshop
            getLastKnownLocation();

        } else {
            //method call to get workshop details
            getWorshopDetails();
        }
        //click listener when user clicks to choose manufacutrers
        wSpecialized.setOnClickListener(this);

        return view;
    }
    //method to add workshop
    private void registerWorkshop(final GeoPoint geoPoint, long clicks) {
        FirebaseUser cUser = mAuth.getCurrentUser();
        String userId = cUser.getUid();
        ArrayList<String> special = new ArrayList<>();
        //getting values entered by user
        String workshopName = wName.getText().toString();
        String workshopAddress = wAddress.getText().toString();
        String workshopOpenH = wOpenHours.getText().toString();
        String workshopPlace = wPlace.getText().toString();
        //getting mechanic speciliation
        if(workshop.getSpecialized() != null){
            special.addAll(workshop.getSpecialized());
        }
        //check if the validtion is false
        if(!validateForm(workshopName, workshopAddress, workshopOpenH, workshopPlace)){
            return;
        }
        //Setting workshop information to workshop model
        workshop.setWorkshopName(workshopName);
        workshop.setAddress(workshopAddress);
        workshop.setOpeningHours(workshopOpenH);
        workshop.setLocationName(workshopPlace);
        workshop.setSpecialized(special);
        workshop.setLocation(geoPoint);
        workshop.setWorkshopId(userId);
        workshop.setClicks(clicks);
        //Query to update details
        db.collection("my_workshop").document(userId).set(workshop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //goto mechanic portal after updating details
                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MechanicPortal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void getWorshopDetails(){
        //
        FirebaseUser cUser = mAuth.getCurrentUser();
        String userId = cUser.getUid();
        //Query to get workshop info
        db.collection("my_workshop").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workshop = documentSnapshot.toObject(Workshop.class);
               try {
                   final GeoPoint geoPoint = workshop.getLocation();
                    //setting the fetched values to the widgets
                   wName.setText(workshop.getWorkshopName());
                   wAddress.setText(workshop.getAddress());
                   wOpenHours.setText(workshop.getOpeningHours());
                   wPlace.setText(workshop.getLocationName());
                   wLat.setText(String.valueOf(geoPoint.getLatitude()));
                   wLng.setText(String.valueOf(geoPoint.getLongitude()));
                   wSpecialized.setText(workshop.getSpecialized().toString());
                    //click listener for update button, to save details
                   progressBar.setVisibility(View.GONE);
                   update.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           registerWorkshop(geoPoint, workshop.getClicks());
                           startActivity(new Intent(getActivity(), MechanicPortal.class));
                       }
                   });
               }    //log error
               catch (Exception e){
                   progressBar.setVisibility(View.GONE);
                   Log.d(TAG, e.toString());
                   Toast.makeText(getActivity(), "Token Space Issue!", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
    //get last known location of the user
    private void getLastKnownLocation() {
        //check if user has given permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //get user permission
            askPermission();
        }
        //Query to get location
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    //getting workshop location
                    final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    String lat = String.valueOf(geoPoint.getLatitude());
                    String lng = String.valueOf(geoPoint.getLongitude());
                    //setting the location fetched
                    wLat.setText(lat);
                    wLng.setText(lng);
                    //updating details when the user clicks on the updte button
                    final long click = 0;
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            registerWorkshop(geoPoint, click);
                        }
                    });
                }
            }
        });
    }

    //method to validate form
    private boolean validateForm(String workshopName, String workshopAddress, String openHours, String place) {
        boolean valid = true;
        if (workshopName.isEmpty()) {
            wName.setError("Please enter name of your workshop!");
            wName.requestFocus();
            valid = false;
        } else if (workshopAddress.isEmpty()) {
            wAddress.setError("Please enter address to your workshop!");
            wAddress.requestFocus();
            valid = false;
        } else if (place.isEmpty()) {
            wPlace.setError("Please enter name of location!");
            wPlace.requestFocus();
            valid = false;
        }else if (openHours.isEmpty()) {
            wOpenHours.setError("Please enter open hours!");
            wOpenHours.requestFocus();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //get result in return from choose manufacturer
        if (id == R.id.workshopSpecializedEditT) {
            Intent intent = new Intent(getActivity(), ChooseManufacturer.class);
            intent.putExtra("user", "mechanic");
            startActivityForResult(intent, 1);
        }
    }
    //method to get results from call back method
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //get returned results by select model class
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                workshopSpecialized.addAll(data.getStringArrayListExtra("resultList"));
                workshop.setSpecialized(workshopSpecialized);
                wSpecialized.setText(workshopSpecialized.toString());
            }
        }
    }

    //method to get gps permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }
}
