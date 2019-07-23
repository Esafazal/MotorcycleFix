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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText Email, Password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Email = findViewById(R.id.emailEditText);
        Password = findViewById(R.id.passwordEditText);
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

        createUser(email, password);
    }


    private void createUser(String email, String password){

        if(!validateForm(email, password)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Error Occured While Registering!", Toast.LENGTH_LONG).show();
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
}
