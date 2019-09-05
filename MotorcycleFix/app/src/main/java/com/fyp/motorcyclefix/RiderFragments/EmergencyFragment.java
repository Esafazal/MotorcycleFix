package com.fyp.motorcyclefix.RiderFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.Dao.SOS;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragements.SendSOSNotification;
import com.fyp.motorcyclefix.RiderFragments.EmergencyFragements.ShowUsersReadyToHelp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment {

    public static final String TAG = "emergencyFragment";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.rider_emergency_fragment, container, false);
        final Button customButton = view.findViewById(R.id.custom_button);
        Switch switchEnableButton = view.findViewById(R.id.switch_enable_button);
        //
        if(!switchEnableButton.isChecked()){
            customButton.setEnabled(false);
        }
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConfirmationAndProceed();
            }
        });
        //
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

    @Override
    public void onStart() {
        super.onStart();
        checkIfEmergencyResolved();
    }

    //
    private void getConfirmationAndProceed(){
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you really facing a breakdown? Please DO NOT misuse this feature and it can" +
                " become a nuisance to users, use it responsibly!")
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

    private void checkIfEmergencyResolved(){
        final String user = currentUser.getUid();
        db.collection("SOS")
                .whereEqualTo("userId", user)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                  SOS sos = snapshot.toObject(SOS.class);
                  if(sos.getUserId().equals(user)
                          && sos.getStatus().equals("accepted")
                          || sos.getStatus().equals("pending")){
                      //goto showUserReadyToHelp activity whihc displays map
                      Intent intent = new Intent(getContext(), ShowUsersReadyToHelp.class);
                      intent.putExtra("docId", snapshot.getId());
                      startActivity(intent);
                  }
              }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "No SOS: "+e.toString());
                    }
                });
    }

}
