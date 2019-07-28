package com.fyp.motorcyclefix.MechanicFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.R;

public class BookingRequestFragment extends Fragment {

    public BookingRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mechanic_booking_request_fragment, container, false);
    }


}
