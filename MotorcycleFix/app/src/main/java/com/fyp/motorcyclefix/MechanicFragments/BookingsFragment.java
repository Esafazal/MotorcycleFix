package com.fyp.motorcyclefix.MechanicFragments;


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
public class BookingsFragment extends Fragment {

    private EditText sendNoteEditText;
    private Button sendNote, startService, completeService;

    public BookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_bookings_fragment, container, false);

        sendNote = view.findViewById(R.id.btnLeaveNote);
        sendNoteEditText = view.findViewById(R.id.bookingLeaveNote);
        String note = sendNoteEditText.getText().toString();
        startService = view.findViewById(R.id.bookingStartService);
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
                sendNote.setOnClickListener(
                        new View.OnClickListener() {
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