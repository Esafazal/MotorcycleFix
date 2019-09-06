package com.fyp.motorcyclefix.RiderFragments.TrackingFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GetServiceRating extends AppCompatDialogFragment implements View.OnClickListener {
    //constant
    public static final String TAG = "getServiceRating";
    //variable declarations and initializations
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button doneBtn;
    private RatingBar ratingBar;
    private String bookingId, workshopId;
    private String workshopName;
    private TextView displayMessage;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rider_get_service_rating_fragment, null);
        //getting references to the widgets in the layout file
        doneBtn = view.findViewById(R.id.closeRating);
        ratingBar = view.findViewById(R.id.getRating);
        displayMessage = view.findViewById(R.id.ratingReview);

        try {
            //getting arguments set previosuly via get arguments method
            bookingId = getArguments().getString("bookId");
            workshopId = getArguments().getString("workId");
            getWorkshopName(workshopId);
        } catch (Exception e) {
            //log error
            Log.d(TAG, e.toString());
        }
        //click listener when user presses done
        doneBtn.setOnClickListener(this);
        //creating a alertdialog builder object to get user rating via a popup dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.setView(view).create();
    }
    //method  to get workshop name
    private void getWorkshopName(String workshopId) {
        //Query  to get workshop
        db.collection("my_workshop").document(workshopId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workshop workshop = documentSnapshot.toObject(Workshop.class);
                //getting the workshop name and location
                workshopName = workshop.getWorkshopName() + " - " + workshop.getLocationName();
                //setting them
                displayMessage.setText("How was the service at " + workshopName);
            }
        });
    }
    //saving user rating to the database
    @Override
    public void onClick(View v) {
        final float rating  = ratingBar.getRating();
        //creating a hashmap object and passing it to the database
        Map<String, Object> ratingUpdate = new HashMap<>();
        ratingUpdate.put("starRating", rating);
        ratingUpdate.put("ratingStatus", "rated");
        //saving the rating
        db.collection("bookings")
                .document(bookingId)
                .update(ratingUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Customer rating entry: "+String.valueOf(rating));
                //close dialog
                dismiss();
            }
        });
    }
}
