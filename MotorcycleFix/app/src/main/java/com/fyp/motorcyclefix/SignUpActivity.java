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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.fyp.motorcyclefix.Dao.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    //TAG Constant
    private static final String TAG = "signupActivty";
    //Variables for widgets, model and firebase/firestore
    private EditText Email, Password, Name, phoneNumber;
    private RadioGroup sexGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;
    private Bundle bundle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User userModel;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private ConstraintLayout signupConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize widgets
        Email = findViewById(R.id.emailEditText);
        Password = findViewById(R.id.passwordEditText);
        Name = findViewById(R.id.nameEditText);
        phoneNumber = findViewById(R.id.phoneNumber);
        sexGroup = findViewById(R.id.radioSex);
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        progressBar = findViewById(R.id.signUpProgressBar);
        signupConstraintLayout = findViewById(R.id.signUpContainer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //go back to login activity
        onBackPressed();
        return true;
    }

    public void registerClickHandler(View view) {

        //get user inputs from widgets
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        final String name = Name.getText().toString();
        final String phone = phoneNumber.getText().toString();

        //boolean to validate user inputs to widgets
        if(!validateForm(email, password, name, phone)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //method call to create user
        createUser(email, password, name, phone);
    }


    private void createUser(final String email, final String password, final String name, final String phone){

        //gender radio button, getting user selected option
        int selectedId = sexGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        final String gender = radioSelected.getText().toString();

        //create user in firebase auth with username and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //method call to get location of registering use
                            getLastKnownLocation(name, email, gender, Long.valueOf(phone));
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Create User method: "+task.getException());
                            Toast.makeText(getApplicationContext(), "Email Already Exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void getLastKnownLocation(final String name, final String email, final String gender, final long phone) {
        //check if user has given permission to use gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //method call to request permission
            askPermission();
            progressBar.setVisibility(View.GONE);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SignUpActivity.this, "Please resubmit form.", Toast.LENGTH_LONG).show();
                }
            });

        }
        //get last known location of user
        mfusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //check if location was retrieved
                if(task.isSuccessful()){
                    Location location = task.getResult();

                    try {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        bundle = getIntent().getExtras();
                        //Firebase user instance to get session of current user

                        FirebaseUser user = mAuth.getCurrentUser();
                        final String userId = user.getUid().trim();

                        //if user type is rider, method call to save rider details
                        if (bundle.getString("type").equals("1")) {
                            saveUserRider(name, email, gender, geoPoint,phone,userId, user);

                            //if the user type is mechanic, method call to save Mechnaic details
                        } else if (bundle.getString("type").equals("2")) {
                            saveUserMechanic(name, email, gender, geoPoint,phone, userId);
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SignUpActivity.this, "Please resubmit form.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void saveUserMechanic(String name, String email, String gender, GeoPoint geoPoint, long phone, final String userId){
        //user type
        String type ="mechanic";
        //initlizing usermodel constructor
        userModel = new User(type, name, email, gender, geoPoint, phone, userId);

        progressBar.setVisibility(View.GONE);
        //Save additional details of mechanic to "Users" collection in firestore database
        db.collection("users").document(userId).set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Mechanic Registration Successful!"
                                , Toast.LENGTH_SHORT).show();
                        //sending verification email to user
                        sendEmailVerification();
                        //Method call to add workshop as empty
                        addWorkshoptoDBAsNull(userId);
                        //goto mechnic portal activity
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
                        Toast.makeText(SignUpActivity.this, "Mechanic Registration Failed!"
                                , Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());

                    }
                });
    }

    private void saveUserRider(String name, String email, String gender, GeoPoint geoPoint
            , long phone, String userId, final FirebaseUser user){
        //user type
        String type ="rider";
        //initlizing usermodel constructor
        userModel = new User(type, name, email, gender, geoPoint,phone, userId);

        progressBar.setVisibility(View.GONE);
        //Save additional details of rider to "Users" collection in firestore database
        db.collection("users").document(userId).set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this,
                                "Rider Registration Successful!", Toast.LENGTH_SHORT).show();
                        //sending verification email to user
                        sendEmailVerification();
                        //goto rider portal activity
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
        //To insert data as key and value pairs
        HashMap<String, Object> emptyWorkshop = new HashMap<>();
        emptyWorkshop.put("workshopId", null);

        //Add empty worksho to "my_workshop" collection, Denoting mechanic signed up, but didnt't register workshop
        db.collection("my_workshop").document(userID).set(emptyWorkshop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "added empty workshop", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method to check if the edit texts(widgets) are null, valid and length
    private boolean validateForm(String email, String password, String name, String phoneN) {

        boolean valid = true;

        if (name.isEmpty()) {
            Name.setError("Please enter name");
            Name.requestFocus();
            valid = false;
        } else if(email.isEmpty()) {
            Email.setError("Please Enter an Email Address!");
            Email.requestFocus();
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please Enter an Valid Email Address!");
            Email.requestFocus();
            valid = false;
        } else if (phoneN.length() < 10) {
            phoneNumber.setError("Contains less than 10 digits");
            phoneNumber.requestFocus();
            valid = false;
        } else if (password.isEmpty()) {
            Password.setError("Please Enter a Password!");
            Password.requestFocus();
            valid = false;
        } else if (password.length() < 6) {
            Password.setError("Password Too Short!");
            Password.requestFocus();
            valid = false;
        }

        return valid;
    }

    // Asks for permission to access gps
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(this , new String[] { Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION }, 1);
    }
    //method is used to send verification email to user
    public void sendEmailVerification(){
        //getting currently logged in user
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isComplete()) {
                        //log errors
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(SignUpActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            })//log  severe errors
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Email verification: " + e.toString());
                            e.printStackTrace();
                        }
                    });
        } else {
            Toast.makeText(this, "no users available", Toast.LENGTH_SHORT).show();
        }
    }
}
