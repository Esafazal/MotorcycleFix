package com.fyp.motorcyclefix.Services;

import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GetWorkshopRating {

    public static final String TAG = "getWorkshopRating";

    private static FirebaseFirestore bookingRef = FirebaseFirestore.getInstance();

    public static void getStarRating(final String workshopId, final RatingBar ratingBar, final TextView suggestion) {

        bookingRef.collection("bookings")
                .whereEqualTo("workshopId", workshopId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snap) {
                float rating = 0;
                int count = 0;
                float average = 0;

                for (QueryDocumentSnapshot snapshot : snap) {
                    Booking booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();

                    try {
                        if (status.equals("completed")) {
                            count++;
                            rating += booking.getStarRating();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }

                }
                average = rating / count;
                ratingBar.setRating(average);
                suggestion.setText("(" + count + ")");

            }
        });
    }
}
