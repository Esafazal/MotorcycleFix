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

public class ChooseModel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_choose_model_activity);
        setTitle("Choose Model");

        String[] bikeModel = {"Bajaj 125", "discovery", "avenger 150", "CT 100"};
        ListAdapter listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, bikeModel);

        ListView listView = findViewById(R.id.modelListView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String chosenModel = String.valueOf(parent.getItemAtPosition(position));

                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", chosenModel);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
