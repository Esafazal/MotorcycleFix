package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private RiderSharedPreferenceConfig riderPreferenceConfig;
    private MechanicSharedPreferencesConfig mechanicPreferenceConfig;
    private EditText Email, Password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //set title of the activity and back button
        setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //create sharepreference config instances
        riderPreferenceConfig = new RiderSharedPreferenceConfig(getApplicationContext());
        mechanicPreferenceConfig = new MechanicSharedPreferencesConfig(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        progressBar = findViewById(R.id.signInProgressBar);

        //delete once development completed
        Email.setText("esafazal72@gmail.com");
        Password.setText("12344321");

        Intent intent;
        bundle = getIntent().getExtras();

        if (bundle.getString("type").equals("1")) {

            if(riderPreferenceConfig.readRiderLoginStatus()){
                startActivity(new Intent(this, RiderPortal.class));
            }

        } else if (bundle.getString("type").equals("2")) {

            if(mechanicPreferenceConfig.readMechanicLoginStatus()){
                startActivity(new Intent(this, MechanicPortal.class));
            }
        }



    }

    public boolean onOptionsItemSelected(MenuItem item){
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
            signInRider(email, password);

            //check if user type is mechanic
        } else if (bundle.getString("type").equals("2")) {
            signInMechanic(email, password);

        }
    }

    private void signInRider(String email, String password){

        if(!validateForm(email, password)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(getApplicationContext(), RiderPortal.class));
                    riderPreferenceConfig.writeRiderLoginStatus(true);
                    finish();
//                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }
            }
        });
        progressBar.setVisibility(View.GONE);

    }

    private void signInMechanic(String email, String password){

        if(!validateForm(email, password)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(getApplicationContext(), MechanicPortal.class));
                    riderPreferenceConfig.writeRiderLoginStatus(true);
                    finish();
//                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                }
            }
        });
        progressBar.setVisibility(View.GONE);

    }

    private boolean validateForm(String email, String password){

        boolean valid = true;

        if(email.isEmpty()){
            Email.setError("Please Enter an Email Address!");
            Email.requestFocus();
            valid = false;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Please Enter an Valid Email Address!");
            Email.requestFocus();
            valid = false;
        }

        else if(password.isEmpty()){
            Password.setError("Please Enter a Password!");
            Password.requestFocus();
            valid = false;
        }

        else if(password.length() < 6){
            Password.setError("Password Too Short!");
            Password.requestFocus();
            valid = false;
        }

        return valid;
    }

    public void signUpClickHandler(View view) {

        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void forgotPasswordClickHandler(View view) {

        Intent intent;
        bundle = getIntent().getExtras();

        if (bundle.getString("type").equals("1")) {
            intent = new Intent(this, PasswordReset.class);
            intent.putExtra("type", "1");
            startActivity(intent);

        } else if (bundle.getString("type").equals("2")) {
            intent = new Intent(this, PasswordReset.class);
            intent.putExtra("type", "2");
            startActivity(intent);

        }

    }


}
