package com.fyp.motorcyclefix.RiderFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.TrackingDao;
import com.fyp.motorcyclefix.Patterns.TrackingAdapter;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragments.TrackingViewDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackingFragment extends Fragment {

    private static final String TAG = "trackingFragment";

    private RecyclerView recyclerView;
    private TrackingAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookingRef = db.collection("bookings");
    private ProgressBar progressBar;
    private TextView message;

    public TrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.rider_tracking_fragment, container, false);

        progressBar = view.findViewById(R.id.viewTrackingProgressBar);
        message = view.findViewById(R.id.viewTrackingMessage);

        final ArrayList<TrackingDao> trackingDaos = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        bookingRef.whereEqualTo("userId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot bookingInfo : queryDocumentSnapshots) {

                    Booking booking = bookingInfo.toObject(Booking.class);
                    booking.setDocumentId(bookingInfo.getId());

                    getBikeModel(booking, view, trackingDaos);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "No Bookings Found", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        return view;
    }

    public void getBikeModel(final Booking booking, final View view, final ArrayList<TrackingDao> trackingDaos) {

       String vehicle = booking.getVehicleId();

        db.collection("my_vehicle").document(vehicle).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //THE FUCKING MODEL VARIABLE IS ALWAYS NULL
                String model = documentSnapshot.getString("model");

                trackingDaos.add(new TrackingDao("BOOKING #" + booking.getDocumentId(), booking.getServiceType(), booking.getVehicleId()));

                recyclerView = view.findViewById(R.id.tracking_recycler_view);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getActivity());
                adapter = new TrackingAdapter(trackingDaos);

                progressBar.setVisibility(View.GONE);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new TrackingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        TrackingViewDetails trackingViewDetails = new TrackingViewDetails();
                        trackingViewDetails.show(getFragmentManager(), "View Details");
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
