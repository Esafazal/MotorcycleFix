package com.fyp.motorcyclefix.Services;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class GetTimeEstimate {

    public static final String TAG = "getTimeEstimate";

    private static FirebaseFirestore workRef = FirebaseFirestore.getInstance();

    public static void getTimeEstimateForGeneralService(final TextView textView, final CardView estimateCard) {

        workRef.collection("bookings").whereEqualTo("serviceType", "General Service")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                int count = 1;
                long timeDiff = 0;

                try {
                    for (QueryDocumentSnapshot snap : snapshot) {
                        Booking booking = snap.toObject(Booking.class);

                        if(booking.getStatus().equals("completed")){
                            count++;
                            Date startTime = booking.getServiceStartTime();
                            Date endTime = booking.getServiceEndTime();

                            timeDiff += endTime.getTime() - startTime.getTime();
                        }
                    }

                    long average = timeDiff / count;
                    Date date = new Date(average);
                    estimateCard.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf( date.getTime()));
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    public static void getTimeEstimateForWashNWaxService(final TextView textView, final CardView estimateCard) {
        workRef.collection("bookings").whereEqualTo("serviceType", "Wash and Wax")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {

                int count = 1;
                long timeDiff = 0;

                try {
                    for (QueryDocumentSnapshot snap : snapshot) {
                        Booking booking = snap.toObject(Booking.class);

                        if(booking.getStatus().equals("completed")){
                            count++;

                            Date startTime = booking.getServiceStartTime();
                            Date endTime = booking.getServiceEndTime();

                            timeDiff += endTime.getTime() - startTime.getTime();
                        }
                    }
                    long average = timeDiff / count;
                    Date date = new Date(average);
                    estimateCard.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf( date.getTime()));
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

    }
}
