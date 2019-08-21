package com.fyp.motorcyclefix.Listeners;

import com.google.firebase.firestore.GeoPoint;

public class CalculateDistance {

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
}
