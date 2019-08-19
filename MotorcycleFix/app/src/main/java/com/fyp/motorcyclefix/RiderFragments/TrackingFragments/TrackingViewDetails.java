package com.fyp.motorcyclefix.RiderFragments.TrackingFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class TrackingViewDetails extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String TAG = "trackingViewDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private TextView bStatus, sProvider, sType, bookingId, vehicle, sDate, sRepairS, sRepairD, sCategoryS, sCategoryD;
    private Button closeBtn;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.rider_tracking_view_details, null);

        bStatus = view.findViewById(R.id.bookingStatus);
        sProvider = view.findViewById(R.id.serviceProvider);
        sType = view.findViewById(R.id.serviceType);
        bookingId = view.findViewById(R.id.bookingId);
        vehicle = view.findViewById(R.id.vehicle);
        sDate = view.findViewById(R.id.serviceDate);
        sRepairS = view.findViewById(R.id.description);
        sRepairD = view.findViewById(R.id.descriptionDynamic);
        sCategoryS = view.findViewById(R.id.serviceCategoryStat);
        sCategoryD = view.findViewById(R.id.serviceCategoryDynamic);
        closeBtn = view.findViewById(R.id.closeButton);

        closeBtn.setOnClickListener(this);

        String bstat = getArguments().getString("bookingStatus").trim();
        long bID = getArguments().getLong("bookingID");
        String type = getArguments().getString("serviceType");
        String date = getArguments().getString("serviceDate");
        String workID = getArguments().getString("workshopID");
        String model = getArguments().getString("model");
        String serviceCategory = getArguments().getString("repairCat");
        String serviceDescription = getArguments().getString("repairDesc");

        if(!serviceCategory.equals("")){
            sRepairS.setVisibility(View.VISIBLE);
            sRepairD.setVisibility(View.VISIBLE);
        }
        if(!serviceDescription.equals("")){
            sCategoryS.setVisibility(View.VISIBLE);
            sCategoryD.setVisibility(View.VISIBLE);
        }

        if(bstat.equals("pending")){
            bStatus.setTextColor(getResources().getColor(R.color.orange));

        } else if (bstat.equals("accepted")){
            bStatus.setTextColor(getResources().getColor(R.color.green));
        }else if (bstat.equals("progress")){
            bStatus.setTextColor(getResources().getColor(R.color.colorAccent));
        } else{
            bStatus.setTextColor(getResources().getColor(R.color.red));
        }

        bookingId.setText(String.valueOf(bID));
        bStatus.setText(bstat);
        sType.setText(type);
        sDate.setText(date);
        vehicle.setText(model);
        sCategoryD.setText(serviceCategory);
        sRepairD.setText(serviceDescription);

        getWorkshopNVehicleName(workID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(view).create();
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


    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
