package com.fyp.motorcyclefix.RiderFragments.WorkshopFragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.R;

public class ViewWorkshopActivity extends AppCompatActivity {

    private TextView workshopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_view_workshop_activity);

//        Bundle bundle = getIntent().getExtras();
//        workshopName = findViewById(R.id.workshopName);
//        workshopName.setText(bundle.getString("workshop"));

    }

    public void placeBookingClickHandler(View view) {
        finish();
    }
}
