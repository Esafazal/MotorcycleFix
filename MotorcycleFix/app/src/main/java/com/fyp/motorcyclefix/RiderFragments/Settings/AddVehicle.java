package com.fyp.motorcyclefix.RiderFragments.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;

public class AddVehicle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_addvehicle_form_activity);

    }

    public void chooseManufacturerCardClick(View view) {

        Intent intent = new Intent(this, ChooseManufacturer.class);
        startActivity(intent);
;
    }
}
