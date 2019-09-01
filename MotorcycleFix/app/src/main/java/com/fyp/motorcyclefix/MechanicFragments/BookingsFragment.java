package com.fyp.motorcyclefix.MechanicFragments;


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
    //
    public static final String TAG = "bookingsFragment";
    //
    private RecyclerView mRecyclerView;
    private AcceptedBookingsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Booking> bookings = new ArrayList<>();
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
        //
        progressBar = view.findViewById(R.id.bookingsProgressBar);
        noBookings = view.findViewById(R.id.viewBookingsAccepted);
        mRecyclerView = view.findViewById(R.id.bookings_recycler_view);
        //
        getAcceptedBookings();
        return view;
    }

    private void getAcceptedBookings(){
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        db.collection("bookings").whereEqualTo("workshopId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();

                    if(status.equals("accepted") || status.equals("progress")){
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

                long bID = booking.getBookingID();
                String sType = booking.getServiceType();
                Date dateOfService = booking.getDateOfService();
                String repairDesc = booking.getRepairDescription();
                String userName = user.getName();
                String makeNmodel = vehicle.getManufacturer()+" "+vehicle.getModel();
                String uID = booking.getUserId();
                String status = booking.getStatus();
                String phoneNumber = String.valueOf(user.getPhoneNumber());
                String startColor;
                String endColor;

                if(status.equals("accepted")){
                  startColor =  String.valueOf(getResources().getColor(R.color.green));
                  endColor =  String.valueOf(getResources().getColor(R.color.dimRed));
                } else {
                    startColor =  String.valueOf(getResources().getColor(R.color.dimGreem));
                    endColor =  String.valueOf(getResources().getColor(R.color.red));
                }
                bookings.add(new Booking(bID, sType, dateOfService, repairDesc, userName
                        , makeNmodel, uID, status, phoneNumber,startColor, endColor));

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mAdapter = new AcceptedBookingsAdapter(bookings);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);

                mAdapter.setOnItemClickListener(BookingsFragment.this);
            }
        });

    }

    @Override
    public void onEditNote(int position, Button sendNote) {
        sendNote.setVisibility(View.VISIBLE);
    }

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
                        completeService.setClickable(true);
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

    private void updateEndTimeandStatus(int position){
        Booking book =  bookings.get(position);
        String title = "Service Completed";
        String message = "This is to inform you that your bike has been serviced and is ready for collection.";

        SendNotificationService.sendNotification(getContext(), book.getModel(), title, message);
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new java.sql.Timestamp(time);
        Map<String, Object> update = new HashMap<>();
        update.put("status", "completed");
        update.put("serviceEndTime", timestamp);

        db.collection("bookings")
                .document(String.valueOf(book.getBookingID()))
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

    private void updateStartTimeandStatus(int position){
        Booking book =  bookings.get(position);
        String title = "Job Order Started";
        String message = "This is to inform you that your bike service has been started.";

        SendNotificationService.sendNotification(getContext(), book.getModel(), title, message);
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new java.sql.Timestamp(time);
        Map<String, Object> update = new HashMap<>();
        update.put("status", "progress");
        update.put("serviceStartTime", timestamp);

        db.collection("bookings")
                .document(String.valueOf(book.getBookingID()))
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
