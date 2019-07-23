package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;

public class AddVehicle extends AppCompatActivity {

    TextView selectedManufacturer;
    TextView selectedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_addvehicle_form_activity);
        setTitle("Add New Vehicle");


    }

    public void chooseManufacturerCardClick(View view) {

        Intent intent = new Intent(this, ChooseManufacturer.class);
        startActivityForResult(intent, 1);
    }

    public void chooseModelCardClick(View view) {

        Intent intent = new Intent(this, ChooseModel.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedManufacturer = findViewById(R.id.choosenManufacturerHidden);
                selectedManufacturer.setText(result);
            }
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedModel = findViewById(R.id.choosenModelHidden);
                selectedModel.setText(result);
            }
        }
    }


    public void formAddVehicleButtonClick(View view) {

        if(selectedManufacturer == null){
            Toast.makeText(AddVehicle.this, "Please select manufacturer", Toast.LENGTH_SHORT).show();
        }else if(selectedModel == null){
            Toast.makeText(AddVehicle.this, "Please select model", Toast.LENGTH_SHORT).show();
        }else{
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
