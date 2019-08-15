package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.TrackingDao;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.Patterns.VehicleAdapter;
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

public class VehicleActivity extends AppCompatActivity {

    private static final String TAG = "vehicleActivity";

    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vehicleRef = db.collection("my_vehicle");
    private TextView noVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_vehicle_settings_activity);
        setTitle("My Vehicles");

        noVehicle = findViewById(R.id.noVehicleTextview);

        final ArrayList<TrackingDao> trackingDaos = new ArrayList<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            final String userId = currentUser.getUid();

            vehicleRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                        Vehicle vehicle =documentSnapshot.toObject(Vehicle.class);
                        vehicle.setDocumentId(documentSnapshot.getId());

                        String documentId = vehicle.getDocumentId();

                        if(documentId.equals(userId)){

                            trackingDaos.add(new TrackingDao("", "", vehicle.getModel()));

                            recyclerView = findViewById(R.id.vehicleSettingRecycler);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(VehicleActivity.this);
                            adapter = new VehicleAdapter(trackingDaos);

                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            noVehicle.setVisibility(View.GONE);
                            adapter.setOnItemClickListener(new VehicleAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    String itemClick = String.valueOf(trackingDaos.get(position));

                                    Intent intent = new Intent(VehicleActivity.this, MyVehicle.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
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


    public void addVehicleButtonClick(View view) {

        Intent intent = new Intent(this, AddVehicle.class);
        startActivity(intent);
    }
}
