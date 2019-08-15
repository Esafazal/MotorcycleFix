package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "addVehicle";

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

        selectedManufacturer = findViewById(R.id.choosenManufacturerHidden);
        selectedModel = findViewById(R.id.choosenModelHidden);
        registrationNo = findViewById(R.id.registrationNoEdit);
        powerGroup = findViewById(R.id.radioPower);



    }

    public void chooseManufacturerCardClick(View view) {

        Intent intent = new Intent(this, ChooseManufacturer.class);
        intent.putExtra("user", "rider");
        startActivityForResult(intent, 1);
    }

    public void chooseModelCardClick(View view) {

        selectedMake = selectedManufacturer.getText().toString();

        if(!selectedMake.equals("")){
            Intent intent = new Intent(this, ChooseModel.class);
            intent.putExtra("make", selectedMake);
            startActivityForResult(intent, 2);
        } else {
            Toast.makeText(this, "Please Choose Manufacturer First!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedManufacturer.setText(result);
            }
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                selectedModel.setText(result);
            }
        }
    }


    public void formAddVehicleButtonClick(View view) {

        selectedMod = selectedModel.getText().toString();

        if(selectedMake == null){
            Toast.makeText(AddVehicle.this, "Please select manufacturer", Toast.LENGTH_SHORT).show();
        }else if(selectedMod.equals("")){
            Toast.makeText(AddVehicle.this, "Please select model", Toast.LENGTH_SHORT).show();
        }else{
            addVehicleTOdb();
        }
    }

    public void addVehicleTOdb(){

        int selectedId = powerGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        final String powerType = radioSelected.getText().toString();

        String regNumber = registrationNo.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        final Vehicle vehicle = new Vehicle();
        vehicle.setManufacturer(selectedMake);
        vehicle.setModel(selectedMod);
        vehicle.setRegistrationNo(regNumber);
        vehicle.setPowerType(powerType);

        if(currentUser != null){
            String userId = currentUser.getUid();
            db.collection("my_vehicle").document(userId).set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddVehicle.this, "Added Your Vehicle!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AddVehicle.this, VehicleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });
        }


    }
}
