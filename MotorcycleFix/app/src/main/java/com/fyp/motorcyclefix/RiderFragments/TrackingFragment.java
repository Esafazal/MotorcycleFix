package com.fyp.motorcyclefix.RiderFragments;


import android.app.AlertDialog;
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

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.Adapters.TrackingAdapter;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.TrackingFragments.TrackingViewDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

        progressBar = view.findViewById(R.id.viewTrackingProgressBar);
        message = view.findViewById(R.id.viewTrackingMessage);

        final ArrayList<Booking> bookings = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        bookingRef.whereEqualTo("userId", userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot bookingInfo : queryDocumentSnapshots) {
                    Booking booking = bookingInfo.toObject(Booking.class);

                    String bUserId = booking.getUserId().trim();

                    if(bUserId.equals(userId)){
                        message.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        getBikeModel(booking, view, bookings);
                    }

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "No Bookings Found", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        return view;
    }

    public void getBikeModel(final Booking booking, final View view, final ArrayList<Booking> trackingDaos) {

        final String vehicleId = booking.getVehicleId().trim();

        db.collection("my_vehicle").whereEqualTo("userId", booking.getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {

                            Vehicle vehicle = snap.toObject(Vehicle.class);

                            String mVehicleId = snap.getId();

                            if (mVehicleId.equals(vehicleId)) {
                                booking.setModel(snap.getString("manufacturer")+" "+snap.getString("model"));
                            }
                        }

                        long bookId = booking.getBookingID();
                        String sType = booking.getServiceType();
                        String makeNModel = booking.getModel();
                        String status = booking.getStatus();
                        String message = booking.getMessage();
                        String dateOfService = booking.getDateOfService();
                        String workshopId = booking.getWorkshopId();
                        String category = booking.getRepairCategory();
                        String description = booking.getRepairDescription();
                        String messageSeen = booking.getMessageSeen();
                        String viewColor = null;

                        if(status.equals("accepted") || status.equals("progress")){
                            viewColor = String.valueOf(getResources().getColor(R.color.green));
                        }
                        if(status.equals("completed")){
                            viewColor = String.valueOf(getResources().getColor(R.color.red));
                        }

                        trackingDaos.add(new Booking(bookId, sType, viewColor, makeNModel, message
                                        , status, messageSeen, category, description, workshopId, dateOfService, ""));

                        recyclerView = view.findViewById(R.id.tracking_recycler_view);
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getActivity());
                        trackingAdapter = new TrackingAdapter(trackingDaos);

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(trackingAdapter);


                        trackingAdapter.setOnItemClickListener(new TrackingAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {

                                Booking data = trackingDaos.get(position);
                                Bundle bundle = new Bundle();

                                bundle.putLong("bookingID", data.getBookingID());
                                bundle.putString("bookingStatus", data.getStatus());
                                bundle.putString("serviceType", data.getServiceType());
                                bundle.putString("serviceDate", data.getDateOfService());
                                bundle.putString("model", data.getModel());
                                bundle.putString("workshopID", data.getWorkshopId());
                                bundle.putString("repairCat", data.getRepairCategory());
                                bundle.putString("repairDesc", data.getRepairDescription());
                                bundle.putString("message", data.getMessage());

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

                        Log.d(TAG, e.toString());
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private AlertDialog.Builder showDialogBox() {

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
