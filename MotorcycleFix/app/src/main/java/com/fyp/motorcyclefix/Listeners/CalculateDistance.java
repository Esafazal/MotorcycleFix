package com.fyp.motorcyclefix.Listeners;

import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CalculateDistance {

    public static final String TAG = "calculateDistance";

    private static FirebaseFirestore workRef = FirebaseFirestore.getInstance();

    public static double calculateDistanceFormulae(GeoPoint victimeUserGeo, GeoPoint userGeo){

        // Radious of the earth
        final int R = 6371;
        Double lat1 = victimeUserGeo.getLatitude();
        Double lon1 = victimeUserGeo.getLongitude();
        Double lat2 = userGeo.getLatitude();
        Double lon2 = userGeo.getLongitude();

        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a =  Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;

        return distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public static void getStarRating(final String workshopId, final RatingBar ratingBar, final TextView suggestion){

        workRef.collection("bookings")
                .whereEqualTo("workshopId", workshopId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snap) {
                float rating = 0;
                int count = 0;
                float average = 0;

                for(QueryDocumentSnapshot snapshot : snap){
                    Booking booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();

                   try {
                       if(status.equals("completed")){
                           count++;
                           rating += booking.getStarRating();
                       }
                   } catch (Exception e){
                       Log.d(TAG, e.toString());
                   }

                }
                average = rating / count;
                ratingBar.setRating(average);
                suggestion.setText("("+count+"+)");

            }
        });
    }
}
