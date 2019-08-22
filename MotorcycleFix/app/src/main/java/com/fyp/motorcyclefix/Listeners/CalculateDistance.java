package com.fyp.motorcyclefix.Listeners;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class CalculateDistance {

    public static final String TAG = "calculateDistance";

    private static FirebaseFirestore workRef = FirebaseFirestore.getInstance();

    public static double calculateDistanceFormulae(GeoPoint victimeUserGeo, GeoPoint userGeo) {

        // Radious of the earth
        final int R = 6371;
        Double lat1 = victimeUserGeo.getLatitude();
        Double lon1 = victimeUserGeo.getLongitude();
        Double lat2 = userGeo.getLatitude();
        Double lon2 = userGeo.getLongitude();

        Double latDistance = toRad(lat2 - lat1);
        Double lonDistance = toRad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = R * c;

        return distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public static void getStarRating(final String workshopId, final RatingBar ratingBar, final TextView suggestion) {

        workRef.collection("bookings")
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
                suggestion.setText("(" + count + "+)");

            }
        });
    }

    public static void getTimeEstimateForGeneralService(final TextView textView,  final CardView estimateCard) {

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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
