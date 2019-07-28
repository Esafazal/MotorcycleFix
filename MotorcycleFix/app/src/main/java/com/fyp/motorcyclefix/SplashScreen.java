package com.fyp.motorcyclefix;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#FFFFFF"))
                .withLogo(R.drawable.logo);

        View splashIt = config.create();

        setContentView(splashIt);

    }

}
