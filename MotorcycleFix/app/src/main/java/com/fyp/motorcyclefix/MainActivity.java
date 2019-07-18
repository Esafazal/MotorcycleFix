package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void riderButton(View view) {

        intent = new Intent(this, LoginActivity.class);

        intent.putExtra("type", "1");
        startActivity(intent);
    }

    public void mechanicButton(View view) {

        intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }
}
