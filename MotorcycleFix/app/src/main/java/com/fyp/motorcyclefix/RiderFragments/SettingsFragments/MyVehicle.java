package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyVehicle extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    //contant for logging
    private static final String TAG = "myVehicle";
    //varibale declarations and initilizations
    private TextView chosenMake, chosenModel;
    private EditText registrationNo, purchasedYear;
    private RadioGroup powerGroup;
    private RadioButton radioSelected;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vehicleRef = db.collection("my_vehicle");
    private String documentId;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_my_vehicle_activity);
        setTitle("Edit Vehicle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getting references to the widgets in the layout file
        chosenMake = findViewById(R.id.myVehicleChosenManufacturer);
        chosenModel = findViewById(R.id.myVehicleChosenModel);
        registrationNo = findViewById(R.id.registrationNoEdit);
        purchasedYear = findViewById(R.id.myVehiclePurchaseYear);
        powerGroup = findViewById(R.id.radioPowerUpdate);
        //getting stored values via intent object
        documentId = getIntent().getStringExtra("vehicleId");
        //method call to get veicle details
        getVehicleDetails(documentId);
    }

    public void getVehicleDetails(String id){
        //getting current user id
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Query to get registered vehicle info
            vehicleRef.document(id)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                        vehicle = documentSnapshot.toObject(Vehicle.class);
                        //setting make and model
                        chosenMake.setText(vehicle.getManufacturer());
                        chosenModel.setText(vehicle.getModel());
                        registrationNo.setText(vehicle.getRegistrationNo());
                        //checking the power type chosen and setting it
                        if(vehicle.getPowerType().equals("petrol")){
                            powerGroup.check(R.id.radioPetrolUpdate);
                        } else if(vehicle.getPowerType().equals("diesal")){
                            powerGroup.check(R.id.radioDiesalUpdate);
                        } else {
                            powerGroup.check(R.id.radioElectricUpdate);
                        }

                }
            })      //log error
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyVehicle.this, "Unable to fetch data!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        } else {
            //If the user isn't right, ask to reauthenticatee
            Toast.makeText(this, "Please login again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "1");
            startActivity(intent);
            finish();
        }

    }

    //method to update vehicle details
    public void updateVehicleButtonClick(View view) {
        //getting user chosen and entered values from the widgets
        int selectedId = powerGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        String Power = radioSelected.getText().toString();
        String updateMake = chosenMake.getText().toString();
        String updateModel = chosenModel.getText().toString();
        String updateRegNo = registrationNo.getText().toString();
        String updatePurchased = purchasedYear.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        //setting retrived values to the vehicle model
        Vehicle vehicle = new Vehicle();
        vehicle.setPowerType(Power);
        vehicle.setManufacturer(updateMake);
        vehicle.setModel(updateModel);
        vehicle.setRegistrationNo(updateRegNo);
        vehicle.setPurchasedYear(updatePurchased);
        vehicle.setUserId(userId);
        //Query to udate vehcile details
        vehicleRef.document(documentId).set(vehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyVehicle.this, "Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyVehicle.this, VehicleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        })      // log error
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyVehicle.this, "Couldn't Update", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    //method to handle delete vehicle
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater =getMenuInflater();
         inflater.inflate(R.menu.rider_remove_vehicle, menu);
         //getting reference to the menu item
         MenuItem removeVehicle = menu.findItem(R.id.action_remove);
         //click listener to handle remove click
         removeVehicle.setOnMenuItemClickListener(this);

      return true;
    }

    //method to handle menu item click
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        //display alert dialog to get confirmation before removing vehicle
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to remove your vehicle "
                +vehicle.getManufacturer()+" "+vehicle.getModel()+"?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       deleteUserVehicle();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        return true;
    }
    //
    private void deleteUserVehicle(){
        //Query to remove vehicle from database
        db.collection("my_vehicle").document(documentId)
                .update("userId", null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //display success message
                Toast.makeText(MyVehicle.this, "Vehicle Removed!", Toast.LENGTH_SHORT).show();
                //goto vehicle activity
                startActivity(new Intent(MyVehicle.this, VehicleActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
