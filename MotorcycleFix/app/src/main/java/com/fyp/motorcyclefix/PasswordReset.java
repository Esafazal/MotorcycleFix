package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {
    //Variables for widgets, bundle and firebase
    private FirebaseAuth mAuth;
    private EditText Email;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset_activity);
        setTitle("Password Reset");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initilize firebase auth
        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.resetEmail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //method call to check  user type
       checkUserType();
       return true;
    }

    public void passwordResetClickHandler(View view) {
        //get user input from widget
        String email = Email.getText().toString().trim();
        //method call to reset password
        passwordReset(email);
    }

    public void passwordReset(String email){
        //boolean to validate form
        if(!validateForm(email)){
            return;
        }
        //send password reset link to users email address
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Reset Link Sent.", Toast.LENGTH_SHORT).show();
                    checkUserType();
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to Send Reset Link", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //method to check the type of user requesting to reset password
    public void checkUserType(){
        Intent intent;
        bundle = getIntent().getExtras();
        if (bundle.getString("type").equals("1")) {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("type", "1");
            startActivity(intent);

            //check if user type is mechanic
        } else if (bundle.getString("type").equals("2")) {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("type", "2");
            startActivity(intent);

        }
    }

    //method to validate password reset form
    private boolean validateForm(String email){

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

        return valid;
    }
}
