package com.fyp.motorcyclefix.MechanicFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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
    //constant for logging
    private static final String TAG = "dashboard";
    //variable declarations and initlizations
    private TextView workshopName, location, onGoing, upComing, completed, cancelled, mVisitors, mUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RatingBar ratingBar;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mechanic_dashboard_fragment, container, false);
        //method call to initialize widgets
        initilizeWidgets(view);
        //get arguments set in the previous activity
        workshopName.setText(getArguments().getString("name"));
        location.setText(getArguments().getString("location"));
        //method call to get dashboard updates
        getDashboardUpdates();
        return view;
    }

    //method get data for satistics of the machanic
    private void getDashboardUpdates() {
        //getting current user id
        FirebaseUser user = mAuth.getCurrentUser();
        final String workshopId = user.getUid().trim();
        //Query to get all booking placed to the mehchanic current logged in
        db.collection("bookings").whereEqualTo("workshopId", workshopId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int upcomingBookings = 0;
                int completedBookings = 0;
                int cancelledBookings = 0;
                int bookingsInProgress = 0;
                float additonOfRating = 0;
                int allUsers = queryDocumentSnapshots.getDocuments().size();
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                   try {
                       Booking booking = snapshot.toObject(Booking.class);
                       String status = booking.getStatus().trim();
                       //filtering fetched data
                       if(status.equals("accepted")){
                           upcomingBookings++;
                       } else if(status.equals("completed")){
                           completedBookings++;
                           additonOfRating += booking.getStarRating();
                       } else if(status.equals("declined")){
                           cancelledBookings++;
                       } else if(status.equals("progress")){
                           bookingsInProgress++;
                       }
                   }//log error
                   catch (Exception e){
                       Log.d(TAG, "Get Dashboard updates: "+e.toString());
                   }
                }
                try {//getting average rating
                    float average = additonOfRating/completedBookings;
                    //getting the number of user clicks
                    getWorkshopClicks();
                    //setting the filtered and calculated results for basic analyticss
                    onGoing.setText(String.valueOf(bookingsInProgress));
                    upComing.setText(String.valueOf(upcomingBookings));
                    completed.setText(String.valueOf(completedBookings));
                    cancelled.setText(String.valueOf(cancelledBookings));
                    mUsers.setText(String.valueOf(allUsers));
                    ratingBar.setRating(average);
                    //log error
                } catch (Exception e){
                    Log.d(TAG, e.toString());
                    Toast.makeText(getActivity(), "Dashboard Updates not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //getting user clicks on the selected workshop
    private void getWorkshopClicks(){
        FirebaseUser user = mAuth.getCurrentUser();
        String workshopId = user.getUid();
            //Query to get clicks registered
           db.collection("my_workshop").document(workshopId)
                   .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   try {//getting value
                       long newCount = documentSnapshot.getLong("clicks");
                       mVisitors.setText(String.valueOf(newCount));
                     }//log error
                   catch (Exception e){
                       Log.d(TAG, e.toString());
                       Toast.makeText(getActivity(), "workshop document id space issue", Toast.LENGTH_SHORT).show();
                   }
               }
           });
    }
    //
    private void initilizeWidgets(View view){
        //getting references to the widgets
        workshopName = view.findViewById(R.id.dashboardWorkshopName);
        location = view.findViewById(R.id.dashboardWorkshopLocation);
        onGoing = view.findViewById(R.id.onGoing);
        upComing = view.findViewById(R.id.upComing);
        completed = view.findViewById(R.id.completed);
        cancelled = view.findViewById(R.id.cancelled);
        mVisitors = view.findViewById(R.id.mVisitors);
        mUsers = view.findViewById(R.id.mUsers);
        ratingBar = view.findViewById(R.id.dashboardRatingBar);
    }
}
