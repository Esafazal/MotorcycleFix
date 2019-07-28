package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChooseModel extends AppCompatActivity {

    private static final String TAG = "chooseModel";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bikeModelRef = db.collection("bikes");
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_choose_model_activity);
        setTitle("Choose Model");

        bundle = getIntent().getExtras();
        final String selectedMake = bundle.getString("make");

       bikeModelRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

               List<String> modelList = new ArrayList<>();

               for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                   User user =documentSnapshot.toObject(User.class);
                   user.setDocumentId(documentSnapshot.getId());

                   String documentId = user.getDocumentId();

                   if(documentId.equals(selectedMake)){

                       for(String model : user.getModels()){
                           modelList.add(model);
                       }
                   }
               }

               ListAdapter listAdapter = new ArrayAdapter<String>(ChooseModel.this, android.R.layout.simple_list_item_1, modelList);

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
       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d(TAG, e.toString());
                   }
               });


//        String[] bikeModel = {"Bajaj 125", "discovery", "avenger 150", "CT 100"};

    }
}
