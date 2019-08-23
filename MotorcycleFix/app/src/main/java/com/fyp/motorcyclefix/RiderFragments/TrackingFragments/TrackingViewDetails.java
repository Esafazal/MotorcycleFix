package com.fyp.motorcyclefix.RiderFragments.TrackingFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrackingViewDetails extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String TAG = "trackingViewDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private TextView bStatus, sProvider, sType, bookingId, vehicle, sDate
            , sRepairS, sRepairD, sCategoryS, sCategoryD, messageS, mechanicNumber;
    private Button closeBtn;
    private EditText messageD;
    private long bID;
    private String mechanicMessage;

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
        messageS = view.findViewById(R.id.messageStat);
        messageD = view.findViewById(R.id.messageDynamic);
        mechanicNumber = view.findViewById(R.id.mechanicNumberDynamic);

        closeBtn.setOnClickListener(this);

        String bstat = getArguments().getString("bookingStatus").trim();
        bID = getArguments().getLong("bookingID");
        String type = getArguments().getString("serviceType");
        String date1 = getArguments().getString("serviceDate");
        String workID = getArguments().getString("workshopID");
        String model = getArguments().getString("model");
        String serviceCategory = getArguments().getString("repairCat");
        String serviceDescription = getArguments().getString("repairDesc");
        mechanicMessage = getArguments().getString("message");

      try {
          if (!serviceCategory.equals("")) {
              sRepairS.setVisibility(View.VISIBLE);
              sRepairD.setVisibility(View.VISIBLE);
          }

          if (!serviceDescription.equals("")) {
              sCategoryS.setVisibility(View.VISIBLE);
              sCategoryD.setVisibility(View.VISIBLE);
          }
          if (!mechanicMessage.equals("")) {
              messageS.setVisibility(View.VISIBLE);
              messageD.setVisibility(View.VISIBLE);
          }
      } catch (Exception e){
          Log.d(TAG, e.toString());
      }

        if(bstat.equals("pending")){
            bStatus.setTextColor(getResources().getColor(R.color.yelloOrange));
            bStatus.setText("Pending");

        } else if (bstat.equals("accepted")){
            bStatus.setTextColor(getResources().getColor(R.color.green));
            bStatus.setText("Accepted");

        }else if (bstat.equals("progress")){
            bStatus.setTextColor(getResources().getColor(R.color.green));
            bStatus.setText("In Progress...");
        } else{
            bStatus.setTextColor(getResources().getColor(R.color.red));
            bStatus.setText("Completed!");
        }

        String date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("E dd MMM", Locale.ENGLISH);
            Date d = new Date(date1);
            date = dateFormat.format(d);
        } catch (Exception e){
            e.printStackTrace();
        }
        

        bookingId.setText(String.valueOf(bID));
        sType.setText(type);
        sDate.setText(date);
        vehicle.setText(model);
        sCategoryD.setText(serviceCategory);
        sRepairD.setText(serviceDescription);
        messageD.setText(mechanicMessage);

        getWorkshopNVehicleName(workID);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(view).create();
    }


    private void getWorkshopNVehicleName(final String workshopID){

        workshopRef.document(workshopID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Workshop workshop = documentSnapshot.toObject(Workshop.class);
                sProvider.setText(workshop.getWorkshopName());

                db.collection("users").document(workshopID)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        String no = String.valueOf(user.getPhoneNumber());
                        mechanicNumber.setText("+94"+no);

                        if(mechanicMessage != null){
                            updateMessageSeen();
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, e.toString());
            }
        });
    }

    private void updateMessageSeen(){

        db.collection("bookings").document(String.valueOf(bID))
                .update("messageSeen", "seen").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
