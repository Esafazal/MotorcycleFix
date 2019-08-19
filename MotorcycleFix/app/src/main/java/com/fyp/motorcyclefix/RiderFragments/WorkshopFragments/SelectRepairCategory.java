package com.fyp.motorcyclefix.RiderFragments.WorkshopFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectRepairCategory extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_select_repair_category_activity_);
        setTitle("Select Repair Category");

        progressBar = findViewById(R.id.chooseRepairCategoryProgress);
        listView = findViewById(R.id.repairCategoryListView);

        getSelectedCategory();

    }

    private void getSelectedCategory() {
        db.collection("repairs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                List<String> category = new ArrayList<>();

                for(QueryDocumentSnapshot snap : snapshot){
                    category.add(snap.getId());
                }

                ListAdapter listAdapter = new ArrayAdapter<String>(SelectRepairCategory.this
                                        , android.R.layout.simple_list_item_1, category);

                progressBar.setVisibility(View.GONE);
                listView.setAdapter(listAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String selected = String.valueOf(parent.getItemAtPosition(position));

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result", selected);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        });
    }
}
