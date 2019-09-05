package com.fyp.motorcyclefix.MechanicFragments.BookingFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptedBookings extends Fragment {

    private EditText sendNoteEditText;
    private Button sendNote, startService, completeService;


    public AcceptedBookings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_accepted_bookings_fragment, container, false);

        sendNote = view.findViewById(R.id.btnLeaveNote);
        sendNoteEditText = view.findViewById(R.id.bookingLeaveNotePast);
        String note = sendNoteEditText.getText().toString();
        startService = view.findViewById(R.id.bookingDeclineBooking);
        completeService = view.findViewById(R.id.bookingCompleteService);

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeService.setBackgroundColor(getResources().getColor(R.color.red));
                completeService.setClickable(true);
            }
        });


        sendNoteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNote.setVisibility(View.VISIBLE);
                sendNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code here
                    }
                });
            }
        });

        return view;
    }

}
