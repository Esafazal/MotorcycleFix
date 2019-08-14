package com.fyp.motorcyclefix.RiderFragments.TrackingFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrackingViewDetails extends AppCompatDialogFragment {

    private static final String TAG = "trackingViewDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private TextView bStatus, sProvider, sType, bookingId, vehicle, sDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rider_tracking_view_details, null);

        bStatus = view.findViewById(R.id.bookingStatus);
        sProvider = view.findViewById(R.id.serviceProvider);
        sType = view.findViewById(R.id.serviceType);
        bookingId = view.findViewById(R.id.bookingId);
        vehicle = view.findViewById(R.id.vehicle);
        sDate = view.findViewById(R.id.serviceDate);


        String bstat = getArguments().getString("bookingStatus").trim();
        String bID = getArguments().getString("bookingID");
        String type = getArguments().getString("serviceType");
        String date = getArguments().getString("serviceDate");
        String workID = getArguments().getString("workshopID");
        String model = getArguments().getString("model");

        if(bstat.equals("pending")){
            bStatus.setTextColor(getResources().getColor(R.color.orange));

        } else if (bstat.equals("accepted")){
            bStatus.setTextColor(getResources().getColor(R.color.green));
        }else if (bstat.equals("progress")){
            bStatus.setTextColor(getResources().getColor(R.color.colorAccent));
        } else{
            bStatus.setTextColor(getResources().getColor(R.color.red));
        }

        bookingId.setText(bID);
        bStatus.setText(bstat);
        sType.setText(type);
        sDate.setText(date);
        vehicle.setText(model);
        getWorkshopNVehicleName(workID);


        return showAlertDialog(view).create();
    }

    private AlertDialog.Builder showAlertDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Booking Details")
                .setPositiveButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder;
    }

    private void getWorkshopNVehicleName(String workshopID){

        workshopRef.document(workshopID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String workshopName = documentSnapshot.getString("workshopName");
                sProvider.setText(workshopName);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, e.toString());
            }
        });
    }



}
