package com.fyp.motorcyclefix.MechanicFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.Adapters.AcceptedBookingsAdapter;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.Services.SendNotificationService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AcceptedBookingsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Booking> bookings = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView noBookings;
    private ProgressBar progressBar;

    public BookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_bookings_fragment, container, false);

        progressBar = view.findViewById(R.id.bookingsProgressBar);
        noBookings = view.findViewById(R.id.viewBookingsAccepted);
        mRecyclerView = view.findViewById(R.id.bookings_recycler_view);

        getAcceptedBookings();

        return view;
    }

    private void getAcceptedBookings(){
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        db.collection("bookings").whereEqualTo("status", "accepted")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    Booking booking = snapshot.toObject(Booking.class);
                    String workshopId = booking.getWorkshopId().trim();

                    if(workshopId.equals(userId)){
                        progressBar.setVisibility(View.VISIBLE);
                        noBookings.setVisibility(View.GONE);

                        getVehicleMakeNModel(booking);
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("bookingsFragment", e.toString());
                    }
                });
    }

    private void getVehicleMakeNModel(final Booking booking){
        db.collection("my_vehicle").document(booking.getVehicleId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);

                getUserName(vehicle, booking);
            }
        });
    }

    private void getUserName(final Vehicle vehicle, final Booking booking){
        db.collection("users").document(booking.getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                bookings.add(new Booking(booking.getBookingID(), booking.getServiceType(), booking.getDateOfService()
                        , booking.getRepairDescription(), user.getName()
                        , vehicle.getManufacturer()+" "+vehicle.getModel(), booking.getUserId()));

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new AcceptedBookingsAdapter(bookings);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);

                mAdapter.setOnItemClickListener(new AcceptedBookingsAdapter.OnItemClickListener() {

                    @Override
                    public void onEditNote(int position, Button sendNote) {
                        sendNote.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStartServiceClick(int position, Button completeService, Button startService) {
                        completeService.setBackgroundColor(getResources().getColor(R.color.red));
                        completeService.setClickable(true);
                        startService.setBackgroundColor(getResources().getColor(R.color.dimGreem));
                        startService.setClickable(false);
                        Toast.makeText(getActivity(), "Start Service Clicked", Toast.LENGTH_SHORT).show();

                         Booking book =  bookings.get(position);
                         String title = "Job Order Started";
                         String message = "This is to inform you that your bike service has been started.";

                        SendNotificationService.sendNotification(getContext(), book.getModel(), title, message);
                    }

                    @Override
                    public void onCompleteServiceClick(int position, Button startService, Button completeService) {

                    }

                    @Override
                    public void onSendNoteClick(int position, EditText editNote) {

                    }
                });
            }
        });

    }


}
