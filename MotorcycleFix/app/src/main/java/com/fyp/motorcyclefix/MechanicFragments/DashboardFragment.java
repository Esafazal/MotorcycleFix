package com.fyp.motorcyclefix.MechanicFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final String TAG = "dashboard";

    private TextView workshopName, location, onGoing, upComing, completed, cancelled, mVisitors, mUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_dashboard_fragment, container, false);

        initilizeWidgets(view);
        workshopName.setText(getArguments().getString("name"));
        location.setText(getArguments().getString("location"));

        getDashboardUpdates();

        return view;
    }

    private void getDashboardUpdates() {
        FirebaseUser user = mAuth.getCurrentUser();
        final String workshopId = user.getUid().trim();

        db.collection("bookings").whereEqualTo("workshopId", workshopId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int coming = 0;
                int came = 0;
                int nope = 0;
                int happening = 0;
                int allUsers = queryDocumentSnapshots.getDocuments().size();

                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    Booking booking = snapshot.toObject(Booking.class);

                    String status = booking.getStatus().trim();

                       if(status.equals("accepted")){
                           coming++;
                       }
                       else if(status.equals("completed")){
                           came++;
                       }
                       else if(status.equals("declined")){
                           nope++;
                       }
                       else if(status.equals("progress")){
                           happening++;
                       }
                }
                try {
                    getWorkshopClicks();
                    onGoing.setText(String.valueOf(happening));
                    upComing.setText(String.valueOf(coming));
                    completed.setText(String.valueOf(came));
                    cancelled.setText(String.valueOf(nope));
                    mUsers.setText(String.valueOf(allUsers));


                } catch (Exception e){
                    Log.d(TAG, e.toString());
                    Toast.makeText(getActivity(), "Dashboard Updates not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getWorkshopClicks(){
        FirebaseUser user = mAuth.getCurrentUser();
        String workshopId = user.getUid();


           db.collection("my_workshop").document(workshopId)
                   .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {

                   try {
                       long newCount = documentSnapshot.getLong("clicks");
                       mVisitors.setText(String.valueOf(newCount));
                     }
                   catch (Exception e){
                       Log.d(TAG, e.toString());
                       Toast.makeText(getActivity(), "workshop document id space issue", Toast.LENGTH_SHORT).show();
                   }
               }
           });
    }


    private void initilizeWidgets(View view){

        workshopName = view.findViewById(R.id.dashboardWorkshopName);
        location = view.findViewById(R.id.dashboardWorkshopLocation);
        onGoing = view.findViewById(R.id.onGoing);
        upComing = view.findViewById(R.id.upComing);
        completed = view.findViewById(R.id.completed);
        cancelled = view.findViewById(R.id.cancelled);
        mVisitors = view.findViewById(R.id.mVisitors);
        mUsers = view.findViewById(R.id.mUsers);
    }

}
