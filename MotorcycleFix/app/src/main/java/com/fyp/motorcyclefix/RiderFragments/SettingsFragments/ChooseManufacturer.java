package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;

public class ChooseManufacturer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_choose_manufacturer_activity);
        setTitle("Choose Manufacturer");

        String[] bikeManufacturers = {"Kawasaki", "Hero", "Honda", "Pulsar", "Bajaj", "TVS", "Suzuki", "Kawasaki", "Hero", "Honda", "Pulsar", "Bajaj", "TVS", "Suzuki"};
        ListAdapter listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, bikeManufacturers);

        ListView  listView = findViewById(R.id.manufacturerListView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String chosenManufactrer = String.valueOf(parent.getItemAtPosition(position));

                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", chosenManufactrer);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
//        Fragment fragment = new AddVehicleSubOptions();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.profileBodyFrame, fragment);
//        fragmentTransaction.commit()
    }

}
