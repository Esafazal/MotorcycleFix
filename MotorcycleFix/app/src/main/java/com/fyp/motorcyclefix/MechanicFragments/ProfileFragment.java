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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.MechanicPortal;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    //constant
    private static final String TAG = "mechanicProfileActivity";
    //vairable declarations and initilization
    private EditText Name, Email, PhoneNo;
    private Button update;
    private RadioGroup sexGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private User user;
    private String docId;
    private ProgressBar progressBar;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_profile_fragment, container, false);
        //widget references
        Name = view.findViewById(R.id.MnameEdit);
        Email = view.findViewById(R.id.MemailEdit);
        PhoneNo = view.findViewById(R.id.MphoneEdit);
        sexGroup = view.findViewById(R.id.MecRadioSexProfile);
        update = view.findViewById(R.id.MupdateButton);
        progressBar = view.findViewById(R.id.mechanicProfleProgressbar);
        //initilization
        user = new User();
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //method call to get user info
        getProfileDetails();
        //Update button onclick listner
        update.setOnClickListener(this);

        return view;
    }
    //method to get user profile details
    private void getProfileDetails() {
        //get current user logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            docId = userId;
            //Query to get user info
            userRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user1 = documentSnapshot.toObject(User.class);
                    //setting the fetched values into the widgets
                    Name.setText(user1.getName());
                    Email.setText(user1.getEmail());
                    PhoneNo.setText(String.valueOf(user1.getPhoneNumber()));
                    //checking user gender and setting it
                    if (user1.getGender().contentEquals("male")) {
                        sexGroup.check(R.id.MecradioMaleProfile);
                    } else {
                        sexGroup.check(R.id.MecradioFemaleProfile);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            })      //log error
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Unable to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            //if the user isn't logged in prompt to login again
            Toast.makeText(getActivity(), "Please login again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "1");
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View v) {
        //on update button clicked method call to update new details
        getLastKnownLocation(v);

    }

    private void getLastKnownLocation(final View view) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //
            askPermission();
        }

        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    user.setGeoPoint(geoPoint);

//                    int selectedId = sexGroup.getCheckedRadioButtonId();
//                    radioSelected = view.findViewById(selectedId);
//                    String gender = radioSelected.getText().toString();
                    String gender = "male";
                    String name = Name.getText().toString();
                    String email = Email.getText().toString();
                    String type = "mechanic";
                    GeoPoint point = user.getGeoPoint();
                    long phone = Long.valueOf(PhoneNo.getText().toString());

                    user = new User(type, name, email, gender, point, phone, docId);

                    saveUserDetails();
                }
            }
        });
    }

    private void saveUserDetails(){

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String userId = currentUser.getUid();

        userRef.document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MechanicPortal.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(getActivity(), "Couldn't update details!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Asks for permission to access gps
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity() , new String[] { Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }

}
