package com.fyp.motorcyclefix.MechanicFragments.ProfileFragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.MechanicFragments.WorkshopInfoFragment;
import com.fyp.motorcyclefix.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWorkshop extends Fragment implements View.OnClickListener {

    private Button addWorkshop;


    public AddWorkshop() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_add_workshop_fragment, container, false);

        addWorkshop = view.findViewById(R.id.workshopAddButton);
        addWorkshop.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            askPermission();

        }

        return view;
    }

    @Override
    public void onClick(View v) {

        WorkshopInfoFragment fragment = new WorkshopInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("workshopExists", "no");
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayMechanic, fragment).commit();
    }

    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION
        , Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}