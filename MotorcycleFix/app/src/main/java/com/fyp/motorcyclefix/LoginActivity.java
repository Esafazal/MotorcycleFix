package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Configs.MechanicSharedPreferencesConfig;
import com.fyp.motorcyclefix.Configs.RiderSharedPreferenceConfig;
import com.fyp.motorcyclefix.Dao.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    //constant for logging
    public static final String TAG = "loginActivity";
    //variable declarations and initilizations
    private RiderSharedPreferenceConfig riderPreferenceConfig;
    private MechanicSharedPreferencesConfig mechanicPreferenceConfig;
    public EditText Email, Password;
    public FirebaseAuth mAuth;
    public ProgressBar progressBar;
    public Bundle bundle;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference userRef = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //set title of the activity and back button
        setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initilize firebase instance and view widgets
        mAuth = FirebaseAuth.getInstance();
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        progressBar = findViewById(R.id.signInProgressBar);

        //delete once development completed
//        Email.setText("esafazal72@gmail.com");
//        Password.setText("123456");

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //goto previous activity when back button is pressed
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void signInClickHandler(View view) {

        //get user input valuues
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        bundle = getIntent().getExtras();

        //check if user type is rider
        if (bundle.getString("type").equals("1")) {
            signInUser(email, password);

            //check if user type is mechanic
        } else if (bundle.getString("type").equals("2")) {
            signInUser(email, password);

        }
    }


    public void signInUser(String email, String password) {

        //check if the validtion is false
        if (!validateForm(email, password)) {
            return;
        }
        //make progress bar visible and and sign in user with username and password
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        //get userID of current user and method call to check user type and login to portal
                        String uId = user.getUid();
                        checkUserTypeAndLoginUser(uId);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void checkUserTypeAndLoginUser(final String uId){
        //get user info from "Users" collection and check type of user
        userRef.document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String type = user.getType();
                //check if users collection is empty
                if(type == null){
                    Toast.makeText(LoginActivity.this, "No Users Available!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.GONE);
                bundle = getIntent().getExtras();
                //if user type is rider then goto rider portal activity
                if (bundle.getString("type").equals("1") && type.contentEquals("rider")) {
                    Toast.makeText(LoginActivity.this, "Hi Rider!", Toast.LENGTH_SHORT).show();
                    Intent intent = (new Intent(getApplicationContext(), RiderPortal.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    //if user type is mechanic then goto mechanic portal activity
                } else if (bundle.getString("type").equals("2") && type.contentEquals("mechanic")) {
                    Toast.makeText(LoginActivity.this, "Hi Mechanic!", Toast.LENGTH_SHORT).show();
                    Intent intent = (new Intent(getApplicationContext(), MechanicPortal.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    //if user type is none of above then display toast
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "checkUserTypeAndLoginUser: "+e.toString());
                        Toast.makeText(LoginActivity.this, "serious fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method to validate login form
    public boolean validateForm(String email, String password) {

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
        }

        return valid;
    }

    public void signUpClickHandler(View view) {

//        startActivity(new Intent(this, SignUpActivity.class));
        checkUserType(SignUpActivity.class);
    }

    public void forgotPasswordClickHandler(View view) {

        //method call to check user type
        checkUserType(PasswordReset.class);

    }
    //check user type and goto respective portal
    public void checkUserType(Class activity) {

        Intent intent;
        bundle = getIntent().getExtras();

        if (bundle.getString("type").equals("1")) {
            intent = new Intent(this, activity);
            intent.putExtra("type", "1");
            startActivity(intent);

        } else if (bundle.getString("type").equals("2")) {
            intent = new Intent(this, activity);
            intent.putExtra("type", "2");
            startActivity(intent);

        }
    }

}
