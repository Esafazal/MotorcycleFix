package com.fyp.motorcyclefix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fyp.motorcyclefix.Dao.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "Signup Activity";

    private EditText Email, Password, Name, phoneNumber;
    private RadioGroup sexGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Bundle bundle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User userModel;
    private FusedLocationProviderClient mfusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Email = findViewById(R.id.emailEditText);
        Password = findViewById(R.id.passwordEditText);
        Name = findViewById(R.id.nameEditText);
        phoneNumber = findViewById(R.id.phoneNumber);
        sexGroup = findViewById(R.id.radioSex);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = findViewById(R.id.signUpProgressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", "1");
        startActivity(intent);
        return true;
    }

    public void registerClickHandler(View view) {

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        final String name = Name.getText().toString();
        final String phone = phoneNumber.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        createUser(email, password, name, phone);
    }


    private void createUser(final String email, final String password, final String name, final String phone){

        if(!validateForm(email, password, name, phone)){
            return;
        }

        //gender radio button, getting user selected option
        int selectedId = sexGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        final String gender = radioSelected.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            getLastKnownLocation(name, email, gender, Long.valueOf(phone));
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Email Already Exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void getLastKnownLocation(final String name, final String email, final String gender, final long phone) {

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

                    bundle = getIntent().getExtras();

                    //if user type is rider, execute the follwoing
                    if (bundle.getString("type").equals("1")) {
                        saveUserRider(name, email, gender, geoPoint,phone);

                        //if the user type is mechanic, execute the following
                    } else if (bundle.getString("type").equals("2")) {
                        saveUserMechanic(name, email, gender, geoPoint,phone);

                    }

                }
            }
        });
    }

    private void saveUserMechanic(String name, String email, String gender, GeoPoint geoPoint, long phone){

        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid().trim();
        String type ="mechanic";

        userModel = new User(type, name, email, gender, geoPoint, phone, userId);

        progressBar.setVisibility(View.GONE);
        db.collection("users").document(userId).set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Mechanic Registration Successful!", Toast.LENGTH_SHORT).show();
                        addWorkshoptoDBAsNull(userId);
                        Intent intent = (new Intent(getApplicationContext(), MechanicPortal.class));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Mechanic Registration Failed!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                    }
                });
    }

    private void saveUserRider(String name, String email, String gender, GeoPoint geoPoint,long phone){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        String type ="rider";
        userModel = new User(type, name, email, gender, geoPoint,phone, userId);

        progressBar.setVisibility(View.GONE);
        db.collection("users").document(userId).set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Rider Registration Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = (new Intent(getApplicationContext(), RiderPortal.class));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Rider Registration Failed!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                    }
                });

    }

    private void addWorkshoptoDBAsNull(String userID){

        HashMap<String, Object> emptyWorkshop = new HashMap<>();
        emptyWorkshop.put("workshopId", null);

        db.collection("my_workshop").document(userID).set(emptyWorkshop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpActivity.this, "added empty workshop", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm(String email, String password, String name, String phoneN) {

        boolean valid = true;

        if (email.isEmpty()) {
            Email.setError("Please Enter an Email Address!");
            Email.requestFocus();
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please Enter an Valid Email Address!");
            Email.requestFocus();
            valid = false;
        } else if (password.isEmpty()) {
            Password.setError("Please Enter a Password!");
            Password.requestFocus();
            valid = false;
        } else if (password.length() < 6) {
            Password.setError("Password Too Short!");
            Password.requestFocus();
            valid = false;

        } else if (name.isEmpty()) {
            Name.setError("Please enter name");
            Name.requestFocus();
            valid = false;

        } else if (phoneN.length() < 10) {
            phoneNumber.setError("Contains less than 10 digits");
            phoneNumber.requestFocus();
            valid = false;

        } else if (phoneN.length() > 10) {
            phoneNumber.setError("Contains more than 10 digits");
            phoneNumber.requestFocus();
            valid = false;
        }

            return valid;
        }
}
