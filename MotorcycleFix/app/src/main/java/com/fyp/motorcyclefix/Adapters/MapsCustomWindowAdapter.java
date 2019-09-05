package com.fyp.motorcyclefix.Adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MapsCustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    //constant for logging
    public static final String TAG = "mapsCustomAdapter";
    //variable initlitizations and declarations
    private final View mWindow;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //constructor
    public MapsCustomWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.rider_maps_custom_info_window, null);
    }

    //method all the information that is needed to display in the info window
    private void renderInfoWindowDetails(final Marker marker, View view){
        //getting references to the widgets in the custom layout file
        ImageView windowImage = view.findViewById(R.id.mapsInfoboxIcon);
        final TextView title = view.findViewById(R.id.mapsInfoboxtitle);
        final TextView openHours = view.findViewById(R.id.mapsOpenHours);
        final RatingBar workshopRating = view.findViewById(R.id.mapsRatingbar);
        final TextView suggestion = view.findViewById(R.id.mapsSuggestion);
        //Query to get bookings for ratings calculation
        db.collection("bookings")
                .whereEqualTo("workshopId", marker.getSnippet())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snap) {
                //variables to assign calculated values
                float rating = 0;
                int count = 0;
                float average = 0;
                //looping the snapshop object and mapping to the booking model
                for (QueryDocumentSnapshot snapshot : snap) {
                    Booking booking = snapshot.toObject(Booking.class);
                    String status = booking.getStatus().trim();
                    try {
                        //getting all bookings in completed state and getting the total count
                        if (status.equals("completed")) {
                            count++;
                            rating += booking.getStarRating();
                        }
                        //log any errors thrown
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
                //averaging the star ratings
                average = rating / count;
                workshopRating.setRating(average);
                if (count != 0) {
                    //setting suggestion based on the average ratings
                    if (average <= 1.4) {
                        suggestion.setText("(Poor)");
                    } else if (average <= 2) {
                        suggestion.setText("(Average)");
                    } else if (average <= 3.4) {
                        suggestion.setText("(Good)");
                    } else if (average <= 4.4) {
                        suggestion.setText("(Very Good)");
                    } else {
                        suggestion.setText("(Excellent)");
                    }
                }
                //method call to get workshop title and openhours
                getWorkshopInfo(marker, title, openHours);
            }
        });
    }
    //method to get workshop info
    private void getWorkshopInfo(Marker marker, final TextView title, final TextView openHours){
        //Query to get workshops
        db.collection("my_workshop").document(marker.getSnippet())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workshop workshop = documentSnapshot.toObject(Workshop.class);
                //setting the retrieved values
                openHours.setText(workshop.getOpeningHours());
                title.setText(workshop.getWorkshopName());
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    //method to set the custom info window
    @Override
    public View getInfoContents(Marker marker) {
        //method call to get ratings
        renderInfoWindowDetails(marker, mWindow);
        return mWindow;
    }
}
