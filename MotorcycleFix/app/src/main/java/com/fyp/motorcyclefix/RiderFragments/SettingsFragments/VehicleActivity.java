package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.Adapters.VehicleAdapter;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VehicleActivity extends AppCompatActivity implements VehicleAdapter.OnItemClickListener{
    //contant for logging
    private static final String TAG = "vehicleActivity";
    //varibale declarations and initilizations
    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vehicleRef = db.collection("my_vehicle");
    private TextView noVehicle;
    private ProgressBar progressBar;
    private ImageView imageView;
    private String check = "";
    private  ArrayList<Vehicle> vehicleDaos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_vehicle_settings_activity);
        setTitle("My Vehicles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getting references to the widgets in the layout file
        imageView = findViewById(R.id.arrorIcon);
        progressBar = findViewById(R.id.VehicleActivityProgressbar);
        noVehicle = findViewById(R.id.noVehicleTextview);
        //getting bike selection via the getStringExtra method, which might throw null pointer exceptions
        try {
            check = getIntent().getStringExtra("bikeSelection").trim();
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
        //method call to get user vehicle details based on selection
        getUserVehicles();
    }

    private void getUserVehicles(){
        //arrayList initilization
        vehicleDaos = new ArrayList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //getting current user id
            final String userId = currentUser.getUid();
            //Query to get user vehicle info
            vehicleRef.whereEqualTo("userId", userId)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //if the user has n vehicle display message
                    if(queryDocumentSnapshots.isEmpty()){
                        noVehicle.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        //getting snapshot object into vehicle model
                        Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                        vehicle.setVehicleId(documentSnapshot.getId());
                        String vehicleUserId = vehicle.getUserId();
                        //if the user id matches the vehicle user id set up the recycler view
                        if(vehicleUserId.equals(userId)){
                            //method call to setup recycler view
                            setupRecyclerView(vehicle);
                        }
                    }
                }
            })      //log error message on failure
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VehicleActivity.this, "Please Register a Vehicle!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        }
    }

    private void setupRecyclerView(Vehicle vehicle){
        vehicleDaos.add(vehicle);
        //getting reference to the recycler view
        recyclerView = findViewById(R.id.vehicleSettingRecycler);
        //boost performance
        recyclerView.setHasFixedSize(true);
        //initializing layout manager and vehicle adapter
        layoutManager = new LinearLayoutManager(VehicleActivity.this);
        adapter = new VehicleAdapter(vehicleDaos);
        //setting the layout manager to the recycler view and the adapter
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        //onclick listener for the recycler view
        adapter.setOnItemClickListener(VehicleActivity.this);
    }

    //method to identify user clicks in the recycler view
    @Override
    public void onItemClick(int position) {
        //getting user clicked position
        Vehicle data = vehicleDaos.get(position);
        String vehicleId = data.getVehicleId();
        String makeModel = data.getManufacturer()+" "+data.getModel();
        try {
            //checking check variable value
            if(check.equals("bike")){
                //sending user selected value back to the previous class
                Intent resultIntent = new Intent();
                resultIntent.putExtra("bikeId", vehicleId);
                resultIntent.putExtra("makeModel", makeModel);
                //setting the selection was successfull
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                //goto my vehicle activity
                Intent intent = new Intent(VehicleActivity.this, MyVehicle.class);
                intent.putExtra("vehicleId", vehicleId);
                startActivity(intent);
            }
        } catch (Exception e){
            //log error
            Log.d(TAG, e.toString());
        }
    }


    public void addVehicleButtonClick(View view) {
        //if add vehicle button is clicked, goto addvehicle activity
        Intent intent = new Intent(this, AddVehicle.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
