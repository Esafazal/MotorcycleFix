package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddVehicle extends AppCompatActivity {
    //constant
    private static final String TAG = "addVehicle";
    //variable declarations and initilizations
    private TextView selectedManufacturer, selectedModel;
    private RadioGroup powerGroup;
    private RadioButton radioSelected;
    private EditText registrationNo;
    private String selectedMake, selectedMod;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_addvehicle_form_activity);
        setTitle("Add New Vehicle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //layout widget references
        selectedManufacturer = findViewById(R.id.choosenManufacturerHidden);
        selectedModel = findViewById(R.id.choosenModelHidden);
        registrationNo = findViewById(R.id.registrationNoEdit);
        powerGroup = findViewById(R.id.radioPower);
    }

    public void chooseManufacturerCardClick(View view) {
        //display list of manufactures
        Intent intent = new Intent(this, ChooseManufacturer.class);
        intent.putExtra("user", "rider");
        startActivityForResult(intent, 1);
    }

    public void chooseModelCardClick(View view) {
        //getting the selected manufacurer
        selectedMake = selectedManufacturer.getText().toString();
        //checking if a manufacturer is chosen
        if(!selectedMake.equals("")){
            //display list of  models based on the selected manufacturer
            Intent intent = new Intent(this, ChooseModel.class);
            intent.putExtra("make", selectedMake);
            startActivityForResult(intent, 2);
        } else {
            //display error
            Toast.makeText(this, "Please Choose Manufacturer First!", Toast.LENGTH_SHORT).show();
        }
    }
    //method retrieves the chosen manufacturer and model via this call back method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //gets the selected manufacturer and sets it to the designated widget
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedManufacturer.setText(result);
            }
            //get the selected model and sets it to the designated widget
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedModel.setText(result);
            }
        }
    }
    //method checks for null variables and executes method to add the vehicle to DB
    public void formAddVehicleButtonClick(View view) {
        //gets selected model
        selectedMod = selectedModel.getText().toString();
        if(selectedMake == null){
            Toast.makeText(AddVehicle.this, "Please select manufacturer", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedMod.equals("")){
            Toast.makeText(AddVehicle.this, "Please select model", Toast.LENGTH_SHORT).show();
            return;
        }
        //method call to add vehicle to database
            addVehicleTOdb();
    }

    public void addVehicleTOdb(){
        //getting all user inputs from the view widgets and assign them to string variables
        int selectedId = powerGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        final String powerType = radioSelected.getText().toString();
        String regNumber = registrationNo.getText().toString();
        //validate form for registration number
        if(!validateForm(regNumber)){
            return;
        }
        //get current user id
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        //instance of vehicle model to save in the database
        final Vehicle vehicle = new Vehicle();
        vehicle.setManufacturer(selectedMake);
        vehicle.setModel(selectedMod);
        vehicle.setRegistrationNo(regNumber);
        vehicle.setPowerType(powerType);
        vehicle.setUserId(userId);
        //checking if there is a valid user
        if(currentUser != null){
            //Query to save vehicle in the database
            db.collection("my_vehicle").document().set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //on success display success message and goto VehicleActvity class
                    Toast.makeText(AddVehicle.this, "Added Your Vehicle!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddVehicle.this, VehicleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            })  //on failure log error message
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });
        }


    }
    //method to validate edittexts in forms
    private boolean validateForm(String regNumber) {
        boolean valid = true;
        if(regNumber.isEmpty()){
            registrationNo.setError("Please enter vehicle registration number!");
            registrationNo.requestFocus();
            valid = false;
        }
        if(regNumber.length() < 8){
            registrationNo.setError("Please enter a valid registration number! WP CAA 3306 ");
            registrationNo.requestFocus();
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
