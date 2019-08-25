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

    public static final String TAG = "workshopFragment";

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

        workshop = new Workshop();
        if(WorkshopExist.equals("no")){
            update.setText("Add Workshop");
            progressBar.setVisibility(View.GONE);
            getLastKnownLocation();

        } else {
            getWorshopDetails();
        }

        wSpecialized.setOnClickListener(this);

        return view;
    }

    private void registerWorkshop(final GeoPoint geoPoint, long clicks) {
        FirebaseUser cUser = mAuth.getCurrentUser();
        String userId = cUser.getUid();
        ArrayList<String> special = new ArrayList<>();

        String workshopName = wName.getText().toString();
        String workshopAddress = wAddress.getText().toString();
        String workshopOpenH = wOpenHours.getText().toString();
        String workshopPlace = wPlace.getText().toString();
        special.addAll(workshop.getSpecialized());

        workshop.setWorkshopName(workshopName);
        workshop.setAddress(workshopAddress);
        workshop.setOpeningHours(workshopOpenH);
        workshop.setLocationName(workshopPlace);
        workshop.setSpecialized(special);
        workshop.setLocation(geoPoint);
        workshop.setWorkshopId(userId);
        workshop.setClicks(clicks);

        db.collection("my_workshop").document(userId).set(workshop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MechanicPortal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }

    private void getWorshopDetails(){
        FirebaseUser cUser = mAuth.getCurrentUser();
        String userId = cUser.getUid();
        db.collection("my_workshop").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                workshop = documentSnapshot.toObject(Workshop.class);
               try {
                   final GeoPoint geoPoint = workshop.getLocation();

                   wName.setText(workshop.getWorkshopName());
                   wAddress.setText(workshop.getAddress());
                   wOpenHours.setText(workshop.getOpeningHours());
                   wPlace.setText(workshop.getLocationName());
                   wLat.setText(String.valueOf(geoPoint.getLatitude()));
                   wLng.setText(String.valueOf(geoPoint.getLongitude()));
                   wSpecialized.setText(workshop.getSpecialized().toString());

                   progressBar.setVisibility(View.GONE);
                   update.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           registerWorkshop(geoPoint, workshop.getClicks());
                           startActivity(new Intent(getActivity(), MechanicPortal.class));
                       }
                   });
               }
               catch (Exception e){
                   progressBar.setVisibility(View.GONE);
                   Log.d(TAG, e.toString());
                   Toast.makeText(getActivity(), "Token Space Issue!", Toast.LENGTH_SHORT).show();
               }
            }
        });

    }

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            askPermission();

        }

        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    final GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    String lat = String.valueOf(geoPoint.getLatitude());
                    String lng = String.valueOf(geoPoint.getLongitude());

                    wLat.setText(lat);
                    wLng.setText(lng);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.workshopSpecializedEditT) {
            Intent intent = new Intent(getActivity(), ChooseManufacturer.class);
            intent.putExtra("user", "mechanic");
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                workshopSpecialized.addAll(data.getStringArrayListExtra("resultList"));
                workshop.setSpecialized(workshopSpecialized);
                wSpecialized.setText(workshopSpecialized.toString());
            }
        }
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION }, 1);


    }
}
