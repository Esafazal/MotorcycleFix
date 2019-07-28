package com.fyp.motorcyclefix.MechanicFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastBookingsFragment extends Fragment {


    public PastBookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mechanic_past_bookings_fragment, container, false);
    }

}
