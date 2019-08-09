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
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyVehicle extends AppCompatActivity {

    private static final String TAG = "myVehicle";

    private TextView chosenMake, chosenModel;
    private EditText registrationNo, purchasedYear;
    private RadioGroup powerGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vehicleRef = db.collection("my_vehicle");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_my_vehicle_activity);
        setTitle("Edit Vehicle");

        chosenMake = findViewById(R.id.myVehicleChosenManufacturer);
        chosenModel = findViewById(R.id.myVehicleChosenModel);
        registrationNo = findViewById(R.id.registrationNoEdit);
        purchasedYear = findViewById(R.id.myVehiclePurchaseYear);
        powerGroup = findViewById(R.id.radioPowerUpdate);

        getVehicleDetails();
    }

    public void getVehicleDetails(){

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String userId = currentUser.getUid();

            vehicleRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    String make = documentSnapshot.getString("manufacturer");
                    String model = documentSnapshot.getString("model");
                    String regNumber = documentSnapshot.getString("registratioNo");
                    String powerType = documentSnapshot.getString("powerType");

                    chosenMake.setText(make);
                    chosenModel.setText(model);
                    registrationNo.setText(regNumber);

                    if(powerType.equals("petrol")){
                        powerGroup.check(R.id.radioPetrolUpdate);

                    } else if(powerType.equals("diesal")){
                        powerGroup.check(R.id.radioDiesalUpdate);

                    } else {
                        powerGroup.check(R.id.radioElectricUpdate);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MyVehicle.this, "Unable to fetch data!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        } else {
            Toast.makeText(this, "Please login again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "1");
            startActivity(intent);
            finish();
        }

    }

    public void updateVehicleButtonClick(View view) {

        int selectedId = powerGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        String Power = radioSelected.getText().toString();
        String updateMake = chosenMake.getText().toString();
        String updateModel = chosenModel.getText().toString();
        String updateRegNo = registrationNo.getText().toString();
        String updatePurchased = purchasedYear.getText().toString();

        Vehicle vehicle = new Vehicle();
        vehicle.setPowerType(Power);
        vehicle.setManufacturer(updateMake);
        vehicle.setModel(updateModel);
        vehicle.setRegistrationNo(updateRegNo);
        vehicle.setPurchasedYear(updatePurchased);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        vehicleRef.document(userId).set(vehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(MyVehicle.this, "Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyVehicle.this, VehicleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyVehicle.this, "Couldn't Update", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }
}
