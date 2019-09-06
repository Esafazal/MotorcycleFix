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
import com.fyp.motorcyclefix.Dao.InfoWindow;
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
    private View mWindow;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //constructor
    public MapsCustomWindowAdapter(Context context) {
        mContext = context;
        try {
            mWindow = LayoutInflater.from(context).inflate(R.layout.rider_maps_custom_info_window, null);
        } catch (Exception e){
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    //method all the information that is needed to display in the info window
    private void renderInfoWindowDetails(final Marker marker, View view){
        //getting references to the widgets in the custom layout file
        ImageView windowImage = view.findViewById(R.id.mapsInfoboxIcon);
        final TextView title = view.findViewById(R.id.mapsInfoboxtitle);
        final TextView openHours = view.findViewById(R.id.mapsOpenHours);
        final RatingBar workshopRating = view.findViewById(R.id.mapsRatingbar);
        final TextView suggestion = view.findViewById(R.id.mapsSuggestion);

        InfoWindow infoWindow = (InfoWindow) marker.getTag();
        title.setText(marker.getTitle());
        openHours.setText(infoWindow.getOpenHours());
        workshopRating.setRating(infoWindow.getRating());
        suggestion.setText(infoWindow.getSuggestion());

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
