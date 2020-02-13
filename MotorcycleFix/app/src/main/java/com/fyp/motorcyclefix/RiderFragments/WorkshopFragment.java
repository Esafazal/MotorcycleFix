package com.fyp.motorcyclefix.RiderFragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Adapters.WorkshopsAdapter;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.Dao.WorkshopDao;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragments.ViewWorkshopActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkshopFragment extends Fragment implements WorkshopsAdapter.OnItemClickListener{
    //constant for Logging
    private static final String TAG = "workshopFragment";
    //variable declarations  and initilizaions
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private WorkshopsAdapter workshopsAdapter;
    private List<WorkshopDao> workshopDaoList;
    private List<String> workshopIDs;
    private String workshopId;
    private ProgressBar progressBar;

    public WorkshopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.rider_workshop_fragment, container, false);
        setHasOptionsMenu(true);
        //initilize list objects
        workshopDaoList = new ArrayList<>();
        workshopIDs = new ArrayList<>();
        //method call to get all workshops
        getWorkshops(view);
        return view;
    }

    //method get all workshops information
    private void getWorkshops(final View view){
        //Query to get workshops
        workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //looping the query document snapshot and mapping the results to a workshop model
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Workshop workshop = documentSnapshot.toObject(Workshop.class);
                    workshop.setWorkshopId(documentSnapshot.getId());
                    workshopId = workshop.getWorkshopId();
                    //getting workshop specillization list into a string variable
                    String data = "|";
                    if(workshop.getSpecialized() != null){
                        for (String specialized : workshop.getSpecialized()) {
                            data += "| " + specialized + " |";
                        }
                    }
                    //if the user does not select a specilization, it implies the mechanic repairs any bike in general
                    if(workshop.getSpecialized() == null){
                        data += "All Models";
                    }
                    data += "|";
                    //if the workshopName is not null, load workshop to the adapter
                    if(workshop.getWorkshopName() != null){
                        //adding workshop to the adapter arraylist
                        workshopDaoList.add(new WorkshopDao(R.drawable.reliability
                                , workshop.getWorkshopName()+" - "+workshop.getLocationName(), data));
                        //adding workshop id's
                        workshopIDs.add(workshopId);
                    }
                }
                //method call to pass data to recycler view
                setUpRecyclerView(view);
            }
        })       //Log error message on failure
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    //method to setup the workshops adapter for the recycler view
    private void setUpRecyclerView(View view) {
        //initilize widgets and views
        progressBar = view.findViewById(R.id.findWorkshopProgressBar);
        //getting a reference to the recycler view from the layout
        RecyclerView recyclerView = view.findViewById(R.id.workshop_recycler_view);
        //setHasFixedSize method is used for speed and efficiency purposes
        recyclerView.setHasFixedSize(true);
        //creating a new object of the layout manager and initlizing it as a linearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        //instance of workshops adapter class
        workshopsAdapter = new WorkshopsAdapter(workshopDaoList);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);
        //passing the arrayList to the workshops adapter call
        recyclerView.setAdapter(workshopsAdapter);
        //item click listener for recycler view
        workshopsAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        String workshopId = String.valueOf(workshopIDs.get(position));
        //method call to increment click count
        incrementClick(workshopId);
        //goto viewWorkshopAcvitiy class with value of workshop id
        Intent intent = new Intent(getActivity(), ViewWorkshopActivity.class);
        intent.putExtra("workshopId", workshopId);
        startActivity(intent);
    }

    //method to handle workshop search function
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_workshop, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //creating a new menu item object and initilizing to the menu item created in the resorces folder
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //setting up a listener to listen to text events, to filter results in realtime as the user types
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            //method to detect changes in text and respond immidietly
            @Override
            public boolean onQueryTextChange(String newText) {
               try {
                   workshopsAdapter.getFilter().filter(newText);
               } catch (Exception e){
                   Log.d(TAG, e.toString());
               }
                return false;
            }
        });

    }
    //Method increments click count which is used for mechanic dashboard analytics
    private void incrementClick(final String workshopId){
        final CollectionReference workshopClickRef = db.collection("my_workshop");
        workshopClickRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                   String id2 = snapshot.getId();

                   if(workshopId.equals(id2)){
                       long count = snapshot.getLong("clicks");
                       long newCount = count + 1;
                       workshopClickRef.document(workshopId).update("clicks",newCount).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               Toast.makeText(getActivity(), "Click registered!", Toast.LENGTH_SHORT).show();
                           }
                       });
                }
                }
            }
        });
    }

}
