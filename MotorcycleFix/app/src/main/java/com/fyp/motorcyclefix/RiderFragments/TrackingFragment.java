package com.fyp.motorcyclefix.RiderFragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.fyp.motorcyclefix.Adapters.TrackingAdapter;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.Vehicle;
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
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackingFragment extends Fragment {
    //constant
    private static final String TAG = "trackingFragment";
    //varibale declarations and initilizations
    private RecyclerView recyclerView;
    private TrackingAdapter trackingAdapter;
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

        //getting reference to the widgets in layout
        progressBar = view.findViewById(R.id.viewTrackingProgressBar);
        message = view.findViewById(R.id.viewTrackingMessage);
        //method call to get bookings
        getPlacedBookings(view, getContext());

        return view;
    }

    private void getPlacedBookings(final View view, final Context context){
        final ArrayList<Booking> bookings = new ArrayList<>();
        //get  current user id
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        message.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        //Query to get bookings placed by current user
        bookingRef.whereEqualTo("userId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //check if the retruned object is null
                if(queryDocumentSnapshots.isEmpty()){
                    //displays "no bookings" message
                    message.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                for (QueryDocumentSnapshot bookingInfo : queryDocumentSnapshots) {
                    Booking booking = bookingInfo.toObject(Booking.class);
                    //getting user id
                    String bUserId = booking.getUserId().trim();
                    //check if current users id matches the queried user id
                    if(bUserId.equals(userId)){
                        //get bike model of the current user
                        getBikeModel(booking, view, bookings, context);
                    }
                }
            }
        })      //if the query isn't successful, display and log error
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "No Bookings Found", Toast.LENGTH_SHORT).show();
                        message.setVisibility(View.VISIBLE);
                        Log.d(TAG, e.toString());
                    }
                });
    }
    //get users bike make and model
    private void getBikeModel(final Booking booking, final View view, final ArrayList<Booking> trackingDaos, final Context context) {

        try {
            final String vehicleId = booking.getVehicleId().trim();
            //query to get information of the registered bike
            db.collection("my_vehicle").document(booking.getVehicleId())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                        String mVehicleId = documentSnapshot.getId();

                        if (mVehicleId.equals(vehicleId)) {
                            booking.setModel(vehicle.getManufacturer() + " " + vehicle.getModel());
                        }
                    //variable initilizations to pass into booking constructor
                    long bookId = booking.getBookingID();
                    String sType = booking.getServiceType();
                    String makeNModel = booking.getModel();
                    String status = booking.getStatus();
                    String message = booking.getMessage();
                    Date dateOfService = booking.getDateOfService();
                    String workshopId = booking.getWorkshopId();
                    String category = booking.getRepairCategory();
                    String description = booking.getRepairDescription();
                    String messageSeen = booking.getMessageSeen();
                    String viewColor = null;

                    if (status.equals("accepted") || status.equals("progress")) {
                        viewColor = String.valueOf(context.getResources().getColor(R.color.green));
                    }
                    if (status.equals("completed")) {
                        viewColor = String.valueOf(context.getResources().getColor(R.color.red));
                    }
                    //adding all the variables
                    trackingDaos.add(new Booking(bookId, sType, viewColor, makeNModel, message
                            , status, messageSeen, category, description, workshopId, dateOfService, ""));

                    //Tracking adapter initilizations, setup and passing values to the adapter
                    recyclerView = view.findViewById(R.id.tracking_recycler_view);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getActivity());
                    trackingAdapter = new TrackingAdapter(trackingDaos);

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(trackingAdapter);
                    //tracking recylcer view on item click listener
                    trackingAdapter.setOnItemClickListener(new TrackingAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            Booking data = trackingDaos.get(position);
                            //values passed into  the bundler object to pass data to next ativity
                            Bundle bundle = new Bundle();
                            bundle.putLong("bookingID", data.getBookingID());
                            bundle.putString("bookingStatus", data.getStatus());
                            bundle.putString("serviceType", data.getServiceType());
                            bundle.putString("serviceDate", String.valueOf(data.getDateOfService()));
                            bundle.putString("model", data.getModel());
                            bundle.putString("workshopID", data.getWorkshopId());
                            bundle.putString("repairCat", data.getRepairCategory());
                            bundle.putString("repairDesc", data.getRepairDescription());
                            bundle.putString("message", data.getMessage());

                            //display tracking summary
                            TrackingViewDetails trackingViewDetails = new TrackingViewDetails();
                            trackingViewDetails.setArguments(bundle);
                            trackingViewDetails.show(getActivity().getSupportFragmentManager(), "View Details");

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //log error
                            Log.d(TAG, e.toString());
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private AlertDialog.Builder showDialogBox() {
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rider_tracking_view_details, null);
        builder.setView(view)
                .setTitle("Booking Details")
                .setPositiveButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder;
    }


}
