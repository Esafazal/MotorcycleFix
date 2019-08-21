package com.fyp.motorcyclefix.RiderFragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragements.SendSOSNotification;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment {

    public static final String TAG = "emergencyFragment";


    public EmergencyFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.rider_emergency_fragment, container, false);

        final Button customButton = view.findViewById(R.id.custom_button);
        Switch switchEnableButton = view.findViewById(R.id.switch_enable_button);

        if(!switchEnableButton.isChecked()){
            customButton.setEnabled(false);
        }

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConfirmationAndProceed();
            }
        });

        switchEnableButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customButton.setEnabled(true);
                } else {
                    customButton.setEnabled(false);
                }
            }
        });

        return view;
    }

    private void getConfirmationAndProceed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you really facing a breakdown? Please DO NOT misuse this feature and it can" +
                " become a nuisance to users, use is responsibly!")
                .setTitle("Warning")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendSOSNotification sendSOSNotification = new SendSOSNotification();
                        sendSOSNotification.show(getActivity().getSupportFragmentManager(), TAG);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

}
