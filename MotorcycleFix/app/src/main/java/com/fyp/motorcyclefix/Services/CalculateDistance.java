package com.fyp.motorcyclefix.Services;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class CalculateDistance {
    //constant for logging
    public static final String TAG = "calculateDistance";
    //firebaseFirestore instance
    private static FirebaseFirestore workRef = FirebaseFirestore.getInstance();

    public static double calculateDistanceFormulae(GeoPoint victimeUserGeo, GeoPoint userGeo) {
        Double distance = 0.0;
     try {
         // Radious of the earth
         final int R = 6371;
         //variables to hold two geo coordinates
         Double lat1 = victimeUserGeo.getLatitude();
         Double lon1 = victimeUserGeo.getLongitude();
         Double lat2 = userGeo.getLatitude();
         Double lon2 = userGeo.getLongitude();
         //calculation using Haversine Formulae
         Double latDistance = toRad(lat2 - lat1);
         Double lonDistance = toRad(lon2 - lon1);
         Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                 Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                         Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

         Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
         //final distance between two coordinates
         distance = R * c;
         //logg error
     } catch (Exception e){
         Log.d(TAG, e.toString());
     }
        return distance;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

}
