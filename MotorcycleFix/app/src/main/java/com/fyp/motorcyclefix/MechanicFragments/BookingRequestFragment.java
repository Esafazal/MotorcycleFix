package com.fyp.motorcyclefix.MechanicFragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Dao.Vehicle;
import com.fyp.motorcyclefix.MechanicPortal;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.Services.SendNotificationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingRequestFragment extends AppCompatDialogFragment {

    private Button acceptBtn, declineBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId, vehicleId, serviceType, serviceDate, sDesc, rCategory;
    private long bookingId;
    private TextView makeModel, uName, sType, sDate, rDetailD, rCategoryS, rCategoryD, rDetailS;

    public BookingRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_booking_request_fragment, null);

        initilizeWidgets(view);

        userId = getArguments().getString("userId");
        bookingId = getArguments().getLong("bookingId");
        vehicleId = getArguments().getString("vId");
        serviceType = getArguments().getString("sType");
        serviceDate = getArguments().getString("sDate");
        sDesc = getArguments().getString("sDesc");
        rCategory = getArguments().getString("rCat");
        vehicleId = getArguments().getString("vId");

        if(!rCategory.equals("")){
            rCategoryS.setVisibility(View.VISIBLE);
            rCategoryD.setVisibility(View.VISIBLE);
        }
        if(!sDesc.equals("")){
            rDetailS.setVisibility(View.VISIBLE);
            rDetailD.setVisibility(View.VISIBLE);
        }

        String date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("E dd MMM", Locale.ENGLISH);
            Date d = new Date(serviceDate);
            date = dateFormat.format(d);
        } catch (Exception e){
            e.printStackTrace();
        }

        getUserVehicle();
        sType.setText(serviceType);
        sDate.setText(serviceDate);
        rDetailD.setText(sDesc);
        rCategoryD.setText(rCategory);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineBooking();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.setView(view).create();
    }


    private void initilizeWidgets(View view) {
        acceptBtn = view.findViewById(R.id.bookingAcceptOrder);
        declineBtn = view.findViewById(R.id.bookingDeclineBooking);
        makeModel = view.findViewById(R.id.reqBikeModel);
        uName = view.findViewById(R.id.reqRiderName);
        sType = view.findViewById(R.id.reqSTypeDynamic);
        sDate = view.findViewById(R.id.reqSDateDynamic);
        rDetailD = view.findViewById(R.id.reqRepairDetialDynamic);
        rDetailS = view.findViewById(R.id.repairDetailStatic);
        rCategoryS = view.findViewById(R.id.repairCatStatic);
        rCategoryD = view.findViewById(R.id.repairCatDynamic);
    }

    private void getUserVehicle() {
        db.collection("my_vehicle").document(vehicleId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                makeModel.setText(vehicle.getManufacturer()+" "+vehicle.getModel());

                getUserName();
            }
        });

    }

    private void getUserName() {
        db.collection("users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                uName.setText(user.getName());
            }
        });

    }

    private void declineBooking() {

        Map<String, Object> update = new HashMap<>();
        update.put("status", "declined");
        update.put("userId", null);

        db.collection("bookings").document(String.valueOf(bookingId))
                .update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Accepted Booking", Toast.LENGTH_SHORT).show();
                String title = "Booking Rejected";
                String message = "This is to inform your order has been declined, So please Place another order.";
                SendNotificationService.sendNotification(getContext(), userId, title, message);

                startActivity(new Intent(getActivity(), MechanicPortal.class));
            }
        });
    }

    private void acceptBooking() {

        db.collection("bookings").document(String.valueOf(bookingId))
                .update("status", "accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Accepted Booking", Toast.LENGTH_SHORT).show();
                String title = "Booking Accepted";
                String message = "This is to inform your order has been accepted.";
                SendNotificationService.sendNotification(getContext(), userId, title, message);

                startActivity(new Intent(getActivity(), MechanicPortal.class));
            }
        });
    }


}
