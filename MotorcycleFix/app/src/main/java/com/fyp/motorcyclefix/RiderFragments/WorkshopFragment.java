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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.Dao.WorkshopDao;
import com.fyp.motorcyclefix.Patterns.WorkshopsAdapter;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragments.ViewWorkshopActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkshopFragment extends Fragment {

    private static final String TAG = "workshopFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private WorkshopsAdapter workshopsAdapter;
    private List<WorkshopDao> workshopDaoList;
    private List<String> workshopIDs;
    private StorageReference imgRef = FirebaseStorage.getInstance().getReference();
    private GeoPoint geoPoint;
    private String workshopId;
    private ProgressBar progressBar;


    public WorkshopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.rider_workshop_fragment, container, false);
        setHasOptionsMenu(true);

        workshopDaoList = new ArrayList<>();
        workshopIDs = new ArrayList<>();

        workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Workshop workshop = documentSnapshot.toObject(Workshop.class);

                    workshop.setDocumentId(documentSnapshot.getId());
                    workshopId = workshop.getDocumentId();

                    String data = "|";
                    for (String specialized : workshop.getSpecialized()) {

                        data += "| " + specialized + " |";

                    }
                        data += "|";
                    workshopDaoList.add(new WorkshopDao(R.drawable.reliability
                            , workshop.getWorkshopName()+" - "+workshop.getLocationName(), data));

                    workshopIDs.add(workshopId);

                }

                setUpRecyclerView(view);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

        return view;
    }



    private void setUpRecyclerView(View view) {

        progressBar = view.findViewById(R.id.findWorkshopProgressBar);

        RecyclerView recyclerView = view.findViewById(R.id.workshop_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());

        workshopsAdapter = new WorkshopsAdapter(workshopDaoList);

        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(workshopsAdapter);
        workshopsAdapter.setOnItemClickListener(new WorkshopsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String itemClick = String.valueOf(workshopIDs.get(position));

                Intent intent = new Intent(getActivity(), ViewWorkshopActivity.class);
                intent.putExtra("workshopId", itemClick);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_workshop, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                workshopsAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
}
