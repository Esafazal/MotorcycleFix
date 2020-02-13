package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderPortal;
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

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "profileActivity";

    private EditText Name, Email, PhoneNo;
    private RadioGroup sexGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private User user;
    private String docId;
    private ProgressBar progressBar;
    private TextView emailView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_settings_activity);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = findViewById(R.id.MnameEdit);
        Email = findViewById(R.id.MemailEdit);
        PhoneNo = findViewById(R.id.MphoneEdit);
        sexGroup = findViewById(R.id.MradioSexProfile);
        progressBar = findViewById(R.id.riderProfileProgressBar);
        emailView = findViewById(R.id.emailText);

        user = new User();

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getProfileDetails();
    }

    private void getProfileDetails() {

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            docId = userId;

            userRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);

                    Name.setText(user.getName());
                    emailView.setText("Email ID (verified: "+currentUser.isEmailVerified()+")");
                    Email.setText(user.getEmail());
                    PhoneNo.setText(String.valueOf(user.getPhoneNumber()));

                    if (user.getGender().contentEquals("male")) {
                        sexGroup.check(R.id.MradioMaleProfile);
                    } else {
                        sexGroup.check(R.id.MradioFemaleProfile);
                    }
                    progressBar.setVisibility(View.GONE );
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Unable to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Please login again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "1");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        onBackPressed();
        return true;
    }
    //
    public void profileUpdateClickHandler(View view) {
        //
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
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
                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        user.setGeoPoint(geoPoint);
                        //
                        int selectedId = sexGroup.getCheckedRadioButtonId();
                        radioSelected = findViewById(selectedId);
                        String gender = radioSelected.getText().toString();
                        String name = Name.getText().toString();
                        String email = Email.getText().toString();
                        String type = "rider";
                        GeoPoint point = user.getGeoPoint();
                        long phone = Long.valueOf(PhoneNo.getText().toString());

                        user = new User(type, name, email, gender, point, phone, docId);

                        saveUserDetails();
                    }
                }
            });
    }

    //
    private void saveUserDetails(){
        //
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        userRef.document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, RiderPortal.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(ProfileActivity.this, "Couldn't update details!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
