package com.fyp.motorcyclefix.RiderFragments.EmergencyFragements;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragment;
import com.fyp.motorcyclefix.Services.CalculateDistance;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.NotificationService.SendNotificationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendSOSNotification extends AppCompatDialogFragment implements View.OnClickListener {
    //constant for logging
    public static final String TAG = "sendSOSNotification";
    //variable declarations and initialization
    private EditText describe, landmark;
    private Button sos, closeSos;
    private TextView summary;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private User user;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rider_send_sosnotification_fragment, null);
        ///getting references to the widgets
        describe = view.findViewById(R.id.describe);
        landmark = view.findViewById(R.id.landmark);
        sos = view.findViewById(R.id.sendSosBtn);
        summary = view.findViewById(R.id.dispatchMessage);
        closeSos = view.findViewById(R.id.closeSendSosBtn);
        //location provider client intialization
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //click listener interface
        sos.setOnClickListener(this);
        closeSos.setOnClickListener(this);
        //Alert dialog builder to display SOS message
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(view).create();
    }

    //method to identify and execute button click events
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendSosBtn:
                triggerSOS();
                return;
            case R.id.closeSendSosBtn:
//                startActivity(new Intent(getActivity(), ShowUsersReadyToHelp.class));
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.framelay, new EmergencyFragment()).commit();
        }
    }

    //method to get user input and trigger sos messae
    private void triggerSOS(){
        String entrydesc = describe.getText().toString();
        String entryMark = landmark.getText().toString();
        //validate emergency form
        if (!validateForm(entrydesc, entryMark)) {
            return;
        }
        //dialog builder to get confirmation from user
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please review this move since many users will be notified of your bike breakdown," +
                " hence make sure not to misuse this feature!. ARE YOU SURE YOU WANT TO PROCEED?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCurrentLocation();
                        //view widgets visibility setting
                        sos.setVisibility(View.GONE);
                        describe.setVisibility(View.GONE);
                        landmark.setVisibility(View.GONE);
                        closeSos.setVisibility(View.VISIBLE);
                        summary.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

    }

    //form validation methdod
    public boolean validateForm(String desc, String mark) {
        boolean valid = true;
        if (desc.isEmpty()) {
            describe.setError("Please Enter Specific Issue!");
            describe.requestFocus();
            valid = false;
        } else if (mark.isEmpty()) {
            landmark.setError("Please Enter a Landmark!");
            landmark.requestFocus();
            valid = false;
        }
        return valid;
    }


    private void getCurrentLocation() {
        //get current user id
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        //check location access permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //get permission to access location
            askPermission();
        }
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint victimUserGeo = new GeoPoint(location.getLatitude(), location.getLongitude());
                    //method call to find  users nearby and save sos to database
                    getAllUsersClosetToRider(victimUserGeo, userId);
                    saveSOSToDB(victimUserGeo, userId);
                }
            }
        });
    }

    //find  all users nearby to the rider facing breakdown
    private void getAllUsersClosetToRider(final GeoPoint victimUserGeo, final String currentUserId) {
        //Query to get all users location
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (QueryDocumentSnapshot snap : snapshot) {
                    user = snap.toObject(User.class);
                    //getting document id
                    String recipientId = snap.getId();
                    GeoPoint userGeo = user.getGeoPoint();
                    if (!recipientId.equals(currentUserId)) {
                        //checking if user geo coordinate is not null and calling method to calculate distance
                        if(userGeo != null) {
                            calculateDistanceFormulae(victimUserGeo, userGeo, recipientId);
                        }
                    }
                }
            }
        });
    }

    //method to calculate haversine distance
    private void calculateDistanceFormulae(GeoPoint victimeUserGeo, GeoPoint userGeo, String recipientId) {
        //Method  call Haversine distance and assigning returned value to double varible
        double distance = CalculateDistance.calculateDistanceFormulae(victimeUserGeo, userGeo);
        //checks the distance threshold and sends notification requests
        if (distance <= 1.2) {
            sendSOSRequestNotification(victimeUserGeo, recipientId);
        }
    }

    private void sendSOSRequestNotification(GeoPoint victimeUserGeo, String recipientId) {
        //building notification message
        String title = "HELP!";
        String message = "Would you like to help a rider who is facing an " +
                "unexpected breakdown and also happens to be nearby you!";
        //method call to execue notification service
        SendNotificationService.sendNotification(getActivity(), recipientId, title, message);

    }

    private void saveSOSToDB(GeoPoint victimUserGeo, final String victimUserId) {
        //saving emergency in realtime database
        final SOS sos = new SOS();
        sos.setGeoPoint(victimUserGeo);
        sos.setIssue(describe.getText().toString());
        sos.setLandmark(landmark.getText().toString());
        sos.setUserId(victimUserId);
        sos.setStatus("pending");
        sos.setTime(null);
        //Query to save SOS
        db.collection("SOS").document().set(sos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "All nearby users have been informed " +
                                ", please await assistance.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //method t request location permisson
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


}
