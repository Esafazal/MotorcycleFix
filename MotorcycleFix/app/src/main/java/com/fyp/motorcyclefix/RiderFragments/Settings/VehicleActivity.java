package com.fyp.motorcyclefix.RiderFragments.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.TrackingDao;
import com.fyp.motorcyclefix.Patterns.VehicleAdapter;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;

public class VehicleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_vehicle_settings_activity);

        ArrayList<TrackingDao> trackingDaos = new ArrayList<>();

        trackingDaos.add(new TrackingDao("", "", "Bugati Bike R15"));

        recyclerView = findViewById(R.id.vehicleSettingRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new VehicleAdapter(trackingDaos);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    public void addVehicleButtonClick(View view) {

        Intent intent = new Intent(this, AddVehicle.class);
        startActivity(intent);
    }
}
