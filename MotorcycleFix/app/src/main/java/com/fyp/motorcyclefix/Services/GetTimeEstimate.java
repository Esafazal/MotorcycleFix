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

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTimeEstimate {
    //oonstant for loggin
    public static final String TAG = "getTimeEstimate";
    //firestore database instance
    private static FirebaseFirestore workRef = FirebaseFirestore.getInstance();

    //method to get time estimate for generalService
    public static void getTimeEstimateForGeneralService(final TextView textView, final CardView estimateCard) {
        //Query to get data for the model
        workRef.collection("bookings").whereEqualTo("serviceType", "General Service")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                //varibles to keep track of data count and time difference
                int count = 0;
                long timeDiff = 0;
                try {
                    //if there is no data, display time estimate unknown
                    if(snapshot.isEmpty()){
                        estimateCard.setVisibility(View.VISIBLE);
                        textView.setText("unknown");
                    }
                    //looping to get start, end time of completed services
                    for (QueryDocumentSnapshot snap : snapshot) {
                        Booking booking = snap.toObject(Booking.class);
                        if(booking.getStatus().equals("completed")){
                            //incrementing count
                            count++;
                            //getting start and end time and getting thier time difference
                            Date startTime = booking.getServiceStartTime();
                            Date endTime = booking.getServiceEndTime();
                            long ID = booking.getBookingID();
                            Log.d(TAG, "ID: "+ID);
                            timeDiff += endTime.getTime() - startTime.getTime();
                        }
                    }//if count is zero, display time estimate as unknown
                    if(count == 0){
                        estimateCard.setVisibility(View.VISIBLE);
                        textView.setText("unknown");
                        return;
                    }
                    //getting average and getting hours and minutes
                    long average = timeDiff / count;
                    long diffMinutes = average / (60 * 1000) % 60;
                    long diffHours = average / (60 * 60 * 1000) % 24;
                    //setting the visibility of card and settinng the estimated time
                    estimateCard.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(diffHours)+"h "+ String.valueOf(diffMinutes)+"m");
                } catch (Exception e) {
                    //log error
                    Log.d(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        });
    }
    //method to get time estimate for wash and wax
    public static void getTimeEstimateForWashNWaxService(final TextView textView, final CardView estimateCard) {
        //Query to get data for the model
        workRef.collection("bookings").whereEqualTo("serviceType", "Wash and Wax")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                //varibles to keep track of data count and time difference
                int count = 0;
                long timeDiff = 0;
                try {//if there is no data, display time estimate unknown
                    if(snapshot.isEmpty()){
                        estimateCard.setVisibility(View.VISIBLE);
                        textView.setText("unknown");
                    }
                    //looping to get start, end time of completed services
                    for (QueryDocumentSnapshot snap : snapshot) {
                        Booking booking = snap.toObject(Booking.class);

                        if(booking.getStatus().equals("completed")){
                            //incrementing count
                            count++;
                            //getting start and end time and getting thier time difference
                            Date startTime = booking.getServiceStartTime();
                            Date endTime = booking.getServiceEndTime();
                            long ID = booking.getBookingID();
                            Log.d(TAG, "ID: "+ID);
                            timeDiff += endTime.getTime() - startTime.getTime();
                        }
                    }
                    //if count is zero, display time estimate as unknown
                    if(count == 0){
                        estimateCard.setVisibility(View.VISIBLE);
                        textView.setText("unknown");
                        return;
                    }
                    long average = timeDiff / count;
                    long diffMinutes = average / (60 * 1000) % 60;
                    long diffHours = average / (60 * 60 * 1000) % 24;
                    //setting the visibility of card and settinng the estimated time
                    estimateCard.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(diffHours)+"h "+ String.valueOf(diffMinutes)+"m");
                } catch (Exception e) {
                    //log error
                    Log.d(TAG, e.toString());
                }
            }
        });
    }
}
