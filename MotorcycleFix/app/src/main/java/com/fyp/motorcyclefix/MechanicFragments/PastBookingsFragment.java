package com.fyp.motorcyclefix.MechanicFragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Adapters.AcceptedBookingsAdapter;
import com.fyp.motorcyclefix.Adapters.PastBookingsAdapter;
import com.fyp.motorcyclefix.Dao.AcceptedBooking;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastBookingsFragment extends Fragment {

    //constant for logging
    public static final String TAG = "pastBookingsFragment";
    //variable initializations and declarations
    private RecyclerView mRecyclerView;
    private PastBookingsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<AcceptedBooking> bookings = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView noBookings;
    private ProgressBar progressBar;
    private Booking booking;

    public PastBookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_past_bookings_fragment, container, false);
        //getting referencing to the view widgets
        progressBar = view.findViewById(R.id.pastBookingsProgressBar);
        noBookings = view.findViewById(R.id.viewPastbookings);
        mRecyclerView = view.findViewById(R.id.past_bookings_recycler_view);
        //method call to get bookings
        getPastBookings();
        return view;
    }

    //method gets all the accepted and in progress bookings
    private void getPastBookings() {
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        //Query to get bookings
        db.collection("bookings").whereEqualTo("workshopId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //looping to get the snapshot into bookings model
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();
                    //if the status is either accepted or in progress get thw vehicle model
                    if (status.equals("completed")) {
                        progressBar.setVisibility(View.VISIBLE);
                        noBookings.setVisibility(View.GONE);
                        getVehicleMakeNModel(booking);
                    }
                }
            }
        })      //log error
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PastBookingsFragment", e.toString());
                    }
                });
    }
    //method gets the booking owners vehicle details
    private void getVehicleMakeNModel(final Booking booking) {
        //Query to get details
        db.collection("my_vehicle").document(booking.getVehicleId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                //method call to get the users vehicle
                getUserName(vehicle, booking);
            }
        });
    }

    //method gets riders details
    private void getUserName(final Vehicle vehicle, final Booking booking) {
        //Query to get users info
        db.collection("users").document(booking.getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //mapping the document snapshot into user model
                User user = documentSnapshot.toObject(User.class);

                try {
                    //getting all the values from the model
                long bID = booking.getBookingID();
                String sType = booking.getServiceType();
                Date dateOfService = booking.getDateOfService();
                String repairDesc = booking.getRepairDescription();
                String userName = user.getName();
                String makeNmodel = vehicle.getManufacturer()+" "+vehicle.getModel();
                String repairCat = booking.getRepairCategory();
                String status = booking.getStatus();
                String phoneNumber = String.valueOf(user.getPhoneNumber());
                String message = booking.getMessage();
                String userId = booking.getUserId();
                float rating = booking.getStarRating();

                //passing the variales into the arraylist model
                bookings.add(new AcceptedBooking(makeNmodel, userName, bID, phoneNumber, sType, dateOfService,
                        repairDesc, repairCat, "", "", message, status, userId, rating));
                } catch (Exception e) {
                    Log.e(TAG, "Setting Up Construtor: " + e.toString());
                    e.printStackTrace();
                }
                //setting up the recycller view
                mRecyclerView.setHasFixedSize(true);
                //creating a new layoutmanager objject
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new PastBookingsAdapter(bookings);
                mRecyclerView.setLayoutManager(mLayoutManager);
                //setting the adapter
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}


