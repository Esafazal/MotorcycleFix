package com.fyp.motorcyclefix.MechanicFragments;


import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Adapters.AcceptedBookingsAdapter;
import com.fyp.motorcyclefix.Dao.AcceptedBooking;
import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.NotificationService.SendNotificationService;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingsFragment extends Fragment implements AcceptedBookingsAdapter.OnItemClickListener{
    //constant for logging
    public static final String TAG = "bookingsFragment";
    //variable initializations and declarations
    private RecyclerView mRecyclerView;
    private AcceptedBookingsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<AcceptedBooking> bookings = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView noBookings;
    private ProgressBar progressBar;
    private Booking booking;

    public BookingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_bookings_fragment, container, false);
        //getting referencing to the view widgets
        progressBar = view.findViewById(R.id.bookingsProgressBar);
        noBookings = view.findViewById(R.id.viewBookingsAccepted);
        mRecyclerView = view.findViewById(R.id.bookings_recycler_view);
        //method call to get bookings
        getAcceptedBookings(getContext());
        return view;
    }
    //method gets all the accepted and in progress bookings
    private void getAcceptedBookings(final Context context){
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        //Query to get bookings
        db.collection("bookings").whereEqualTo("workshopId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //looping to get the snapshot into bookings model
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();
                    //if the status is either accepted or in progress get thw vehicle model
                    if(status.equals("accepted") || status.equals("progress")){
                        progressBar.setVisibility(View.VISIBLE);
                        noBookings.setVisibility(View.GONE);
                        getVehicleMakeNModel(booking, context);
                    }
                }
            }
        })      //log error
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("bookingsFragment", e.toString());
                    }
                });
    }
    //method gets the booking owners vehicle details
    private void getVehicleMakeNModel(final Booking booking, final Context context){
        //Query to get details
        db.collection("my_vehicle").document(booking.getVehicleId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                //method call to get the users vehicle
                getUserName(vehicle, booking, context);
            }
        });
    }
    //method gets riders details
    private void getUserName(final Vehicle vehicle, final Booking booking, final Context context){
        //Query to get users info
        db.collection("users").document(booking.getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //mapping the document snapshot into user model
                User user = documentSnapshot.toObject(User.class);
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
                String startColor;
                String endColor;
                //checking booking status
                if(status.equals("accepted")){
                  startColor =  String.valueOf(context.getResources().getColor(R.color.green));
                  endColor =  String.valueOf(context.getResources().getColor(R.color.dimRed));
                } else {
                    startColor =  String.valueOf(context.getResources().getColor(R.color.dimGreem));
                    endColor =  String.valueOf(context.getResources().getColor(R.color.red));
                }
                //passing the variales into the arraylist model
                bookings.add(new AcceptedBooking(makeNmodel, userName, bID, phoneNumber, sType, dateOfService,
                        repairDesc, repairCat, startColor, endColor, message, status, userId, rating));

                //setting up the recycller view
                mRecyclerView.setHasFixedSize(true);
                //creating a new layoutmanager objject
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new AcceptedBookingsAdapter(bookings);
                mRecyclerView.setLayoutManager(mLayoutManager);
                //setting the adapter
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
                //handling clicks in the recycler
                mAdapter.setOnItemClickListener(BookingsFragment.this);
            }
        });

    }

    @Override
    public void onEditNote(int position, Button sendNote) {
        sendNote.setVisibility(View.VISIBLE);
    }

    //method handles start service click
    @Override
    public void onStartServiceClick(final int position, final Button completeService, final Button startService) {
        //display confirmation message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please note the customer is informed of the service start time" +
                ", Please make sure to update end time for collection. Do you want to start?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        completeService.setBackgroundColor(getResources().getColor(R.color.red));
                        completeService.setEnabled(true);
                        startService.setBackgroundColor(getResources().getColor(R.color.dimGreem));
                        startService.setClickable(false);
                        updateStartTimeandStatus(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    //method handles complete service click
    @Override
    public void onCompleteServiceClick(final int position, final Button startService, final Button completeService) {
        //display confirmation message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please note the customer is informed that the service is completed " +
                "and ready for collection. Are you sure the service is complete?")
                .setTitle("Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEndTimeandStatus(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    //method to send message to rider
    @Override
    public void onSendNoteClick(int position, final EditText editNote, final Button btnSend) {
        ///get string entered by mechanic
        final String note = editNote.getText().toString();
        if(note.isEmpty()){
            editNote.setError("Please enter message!");
            editNote.requestFocus();
        }
        final String bookId = String.valueOf(booking.getBookingID());
        //query
        db.collection("bookings").document(bookId)
                .update("message", note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                btnSend.setVisibility(View.GONE);
                editNote.setEnabled(false);
                String title = "Message From Mechanic!";
                SendNotificationService.sendNotification(getContext(), booking.getUserId(), title, note);
            }
        });
    }
    //method handles the recording of service end time
    private void updateEndTimeandStatus(int position){
        AcceptedBooking book =  bookings.get(position);
        String title = "Service Completed";
        String message = "This is to inform you that your bike has been serviced and is ready for collection.";
        //Static method call to send notification
        SendNotificationService.sendNotification(getContext(), book.getUserId(), title, message);
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new java.sql.Timestamp(time);
        Map<String, Object> update = new HashMap<>();
        update.put("status", "completed");
        update.put("serviceEndTime", timestamp);
        //Query update bookings
        db.collection("bookings")
                .document(String.valueOf(book.getBookingNo()))
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Service Ended!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayMechanic, new BookingsFragment())
                                .commit();
                    }
                });
    }
    //method handles the recording of service start time
    private void updateStartTimeandStatus(int position){
        AcceptedBooking book =  bookings.get(position);
        String title = "Job Order Started";
        String message = "This is to inform you that your bike service has been started.";
        //Static method call to send notification
        SendNotificationService.sendNotification(getContext(), book.getUserId(), title, message);
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new java.sql.Timestamp(time);
        Map<String, Object> update = new HashMap<>();
        update.put("status", "progress");
        update.put("serviceStartTime", timestamp);
        //Query to update bookings
        db.collection("bookings")
                .document(String.valueOf(book.getBookingNo()))
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Service Started!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
