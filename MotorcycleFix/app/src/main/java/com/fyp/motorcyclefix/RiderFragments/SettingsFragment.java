package com.fyp.motorcyclefix.RiderFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.Settings.ProfileActivity;
import com.fyp.motorcyclefix.RiderFragments.Settings.VehicleActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private CardView profileSetting;
    private CardView vehicleSetting;
    private CardView logoutSetting;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rider_settings_fragment, container, false);

        onMenuItemClick(view);
        return view;
    }

    private void onMenuItemClick(View view) {

        profileSetting = view.findViewById(R.id.settingProfileCard);
        vehicleSetting = view.findViewById(R.id.settingVehicleCard);
        logoutSetting = view.findViewById(R.id.settingLogoutCard);

        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        vehicleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), VehicleActivity.class);
                startActivity(intent);
            }
        });

        logoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });
    }

}
