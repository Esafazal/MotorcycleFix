package com.fyp.motorcyclefix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    public void signIn(View view) {

        Intent intent;

        Bundle bundle = getIntent().getExtras();

        if (bundle.getString("type").equals("1")) {

            intent = new Intent(this, RiderPortal.class);
            startActivity(intent);

        }else {

            intent = new Intent(this, MechanicPortal.class);
            startActivity(intent);

        }
    }
}
