package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;

public class MyVehicle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_my_vehicle_activity);
        setTitle("Edit Vehicle");


    }

    public void updateVehicleButtonClick(View view) {

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();

    }
}
