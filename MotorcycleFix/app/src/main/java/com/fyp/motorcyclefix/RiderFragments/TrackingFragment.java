package com.fyp.motorcyclefix.RiderFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.TrackingDao;
import com.fyp.motorcyclefix.Patterns.TrackingAdapter;
import com.fyp.motorcyclefix.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackingFragment extends Fragment {

    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public TrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rider_tracking_fragment, container, false);

        ArrayList<TrackingDao> trackingDaos = new ArrayList<>();

        trackingDaos.add(new TrackingDao("Booking #001",
                "Repair Service", "Hero Hunk"));
        trackingDaos.add(new TrackingDao("Booking #002",
                "General Service", "Hero Hunk"));
            trackingDaos.add(new TrackingDao("Booking #003",
                "Other nonsense", "XCD 125"));

        recyclerView = view.findViewById(R.id.tracking_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        adapter = new TrackingAdapter(trackingDaos);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
