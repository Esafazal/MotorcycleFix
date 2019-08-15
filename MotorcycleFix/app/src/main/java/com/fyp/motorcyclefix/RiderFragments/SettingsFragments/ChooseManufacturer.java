package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChooseManufacturer extends AppCompatActivity {

    private static final String TAG = "chooseManufacturer";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bikeMakeRef = db.collection("bikes");
    private ProgressBar makeProgressBar;
    private Button selections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_choose_manufacturer_activity);
        setTitle("Choose Manufacturer");

        selections = findViewById(R.id.selectedManufacturers);
        makeProgressBar = findViewById(R.id.chooseMakeProgress);
        bikeMakeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                makeProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    List<String> bikeMake = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        bikeMake.add(document.getId());

                        final ListAdapter listAdapter = new ArrayAdapter<String>(ChooseManufacturer.this
                                , android.R.layout.simple_list_item_1, bikeMake);

                        final ListView listView = findViewById(R.id.manufacturerListView);
                        listView.setAdapter(listAdapter);

                        boolean isMechanic = userType();

                        if (isMechanic) {
                            selections.setVisibility(View.VISIBLE);
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            selections.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String selected = "";
                                    int cntChoice = listView.getCount();
                                    SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                                    ArrayList<String> sel = new ArrayList<>();

                                    for (int i = 0; i < cntChoice; i++) {
                                        if (sparseBooleanArray.get(i)) {
                                            selected += listView.getItemAtPosition(i).toString() + "\n";
                                            sel.add(listView.getItemAtPosition(i).toString());

                                        }
                                    }
                                    Intent resultIntent = new Intent();
                                    resultIntent.putStringArrayListExtra("resultList", sel);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();

                                }
                            });

                        } else {
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

                        }

                    }
                } else {
                    Log.d(TAG, "Error fetching documents: ", task.getException());
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

    private boolean userType() {

        boolean isMechanic;

        String type = getIntent().getStringExtra("user").trim();

        if (type.equals("mechanic")) {
            isMechanic = true;
        } else {

            isMechanic = false;
        }
        return isMechanic;
    }


}
