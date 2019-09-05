package com.fyp.motorcyclefix.RiderFragments.WorkshopFragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.NotificationService.SendNotificationService;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.SettingsFragments.VehicleActivity;
import com.fyp.motorcyclefix.RiderPortal;
import com.fyp.motorcyclefix.Services.GetTimeEstimate;
import com.fyp.motorcyclefix.Services.GetWorkshopRating;
import com.fyp.motorcyclefix.SignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Calendar;
import java.util.Date;

public class ViewWorkshopActivity extends AppCompatActivity implements View.OnClickListener {
    //constant  for logging
    private static final String TAG = "viewWorkshopActivity";
    //varibale declarations and initilizations
    private TextView workshopName, specialized, chosenRepair, suggestion, ETA;
    private RatingBar ratingBar;
    private EditText repairDescription;
    private CardView repairCategory, estimateCard;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private DocumentReference bookingCount = db.collection("bookings").document("bookings_count");
    private String workshopId;
    private RadioGroup serviceTypeGroup;
    private DatePicker datePicker;
    private ProgressBar progressBar;
    private TextView bikeMod;
    private String bikeId = null;
    private ConstraintLayout placeBookingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_view_workshop_activity);
        //enabling back button
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //setting custom title bar to select bike
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        //getting view from the title bar
        View view = getSupportActionBar().getCustomView();
        //getting references to the widgets in the layout file
        chosenRepair = findViewById(R.id.chosenRepairType);
        repairCategory = findViewById(R.id.repairTypeCard);
        repairDescription = findViewById(R.id.repairDescription);
        datePicker = findViewById(R.id.datePicker);
        ratingBar = findViewById(R.id.ratingBar);
        suggestion = findViewById(R.id.reviewsCount);
        ETA = findViewById(R.id.ETA);
        estimateCard = findViewById(R.id.timeEstimateCard);
        placeBookingContainer = findViewById(R.id.PlaceBookingContainer);
        //formatting date and setting max and min date of service
        Date date = new Date();
        long minDate = date.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        date = calendar.getTime();
        long maxDate = date.getTime();
         try {
             //setting min and max date
             datePicker.setMinDate(minDate);
             datePicker.setMaxDate(maxDate);
         }//log error
         catch (Exception e){
             Log.d(TAG, "Date conversion: "+e.toString());
         }
        //getting references to the buttons in the layout file
        RadioButton repairButton = findViewById(R.id.radioRepair);
        RadioButton generalService = findViewById(R.id.radioGeneral);
        RadioButton washNWax = findViewById(R.id.radioPolish);
        //Button onclick listeners
        repairButton.setOnClickListener(this);
        generalService.setOnClickListener(this);
        washNWax.setOnClickListener(this);

        bikeMod = view.findViewById(R.id.toolbarTextview);
        bikeMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWorkshopActivity.this, VehicleActivity.class);
                intent.putExtra("bikeSelection", "bike");
                startActivityForResult(intent, 1);
            }
        });
        //method call to get workshop details
        getWorkshopDetails();
    }

    //call back methdd to retrieve values from another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request code 1 represents bike make and model
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String makeModel = data.getStringExtra("makeModel");
                bikeMod.setText(makeModel);
                bikeId = data.getStringExtra("bikeId").trim();
            }
            //request code 2 represents repair  type
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                chosenRepair.setText(result);
            }
        }
    }
    //method to choose repair type by the user
    private void getSelectedRepairType() {
        repairCategory.setVisibility(View.VISIBLE);
        //click listener to goto selecttRepairCategory activity
        repairCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWorkshopActivity.this, SelectRepairCategory.class);
                //expecting a result back once the user is done choose the repair type
                startActivityForResult(intent, 2);
            }
        });
    }

    //Method to get workshop details
    private void getWorkshopDetails() {
        //getting references to the view widgets in teh layout file
        workshopName = findViewById(R.id.viewWorkshopName);
        specialized = findViewById(R.id.specializedIN);
        serviceTypeGroup = findViewById(R.id.radioServiceType);
        progressBar = findViewById(R.id.bookingProgressBar);
        ImageView workshopImg = findViewById(R.id.workshopImg);
        workshopImg.setImageResource(R.drawable.reliability);  //setting an image for the workshop
        //getting information stored in the bundle
        workshopId = getIntent().getStringExtra("workshopId");
        //getting working rating from the algorithm library
        GetWorkshopRating.getStarRating(workshopId, ratingBar, suggestion);
        //Query  to the Firestore database to get workshop information based on the adapter onClickListener
        workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Workshop workshop = documentSnapshot.toObject(Workshop.class);
                    String workshopIDs = documentSnapshot.getId();
                    //getting workshop specialization as a String
                    String data = "|";
                    if (workshopIDs.equals(workshopId)) {
                        for (String special : workshop.getSpecialized()) {
                            //appending the values retrived via speical variable to data variable
                            data += "| " + special + " |";
                        }
                        data += "|";
                        //setting workshop name and specialization
                        workshopName.setText(workshop.getWorkshopName() + " - " + workshop.getLocationName());
                        specialized.setText(data);
                    }
                }
            }
        })      //log error
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    //method to handle place booking click event
    public void placeBookingClickHandler(View view) {
        //if user hasn't chosen a bike, display toast
        if(bikeId == null){
            Toast.makeText(this, "Please Select a Bike!", Toast.LENGTH_LONG).show();
        } else if(!currentUser.isEmailVerified()){
            Snackbar.make(placeBookingContainer, "", Snackbar.LENGTH_INDEFINITE)
                    .setAction("verify", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SignUpActivity signUpActivity = new SignUpActivity();
                            signUpActivity.sendEmailVerification();
                            Toast.makeText(ViewWorkshopActivity.this,
                                    "Verification email sent to " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            //method call to send booking request
            sendBookingRequest();
            //method call to dispatch notification to mechanic
            sendNotification();
        }
       
    }
    //method to send booking request to workshop
    private void sendBookingRequest() {
        //getting service type chosen
        final int selectedId = serviceTypeGroup.getCheckedRadioButtonId();
        RadioButton radioSelected = findViewById(selectedId);
        final String serviceType = radioSelected.getText().toString();
        //getting user selected date
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        //checking if there is a logged in user and getting his/her user id
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            final String Uid = currentUser.getUid();
            //Query to send save the booking
            updateBookingCount().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    bookingCount.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            long count = documentSnapshot.getLong("count");
                            Booking booking = new Booking();
                            booking.setServiceType(serviceType);
                            booking.setWorkshopId(workshopId);
                            booking.setDateOfService(calendar.getTime());
                            booking.setUserId(Uid);
                            booking.setStatus("pending");
                            booking.setVehicleId(bikeId);
                            booking.setDateOfBooking(null);
                            booking.setBookingID(count);
                            booking.setRepairCategory(chosenRepair.getText().toString());
                            booking.setRepairDescription(repairDescription.getText().toString());
                            booking.setStarRating(0);
                            booking.setRatingStatus("unrated");

                            String bookCount = String.valueOf(count);
                            addBookingandUpdateCount(bookCount, booking);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, e.toString());
                        }
                    });
        }
    }

    private void sendNotification(){
        String title = "NEW BOOKING";
        String message = "Please review the booking and resolve ASAP!";
        SendNotificationService.sendNotification(this, workshopId, title, message);
    }
    //method to add the bookign and update the booking count
    private void addBookingandUpdateCount(final String bookCount, Booking booking) {
        //Query to save the booking
        db.collection("bookings").document(bookCount).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewWorkshopActivity.this, "Booking Request Sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                //display message via alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewWorkshopActivity.this);
                builder.setTitle("BOOKING #" + bookCount)
                        //display message
                        .setMessage("Your booking has been sent to " + workshopName.getText().toString() +
                                " for confirmation. You will be notified shortly." +
                                " \n Goto Trackings tab to view details.")
                        .setPositiveButton("Alright, Cool.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //when user clicks okay, the fragment is dismissed
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, e.toString());
                    }
                });
    }

    public Task<Void> updateBookingCount() {
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot bookingSnapshot = transaction.get(bookingCount);
                long newCount = bookingSnapshot.getLong("count") + 1;
                transaction.update(bookingCount, "count", newCount);
                return null;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.radioRepair:
                estimateCard.setVisibility(View.GONE);
                getSelectedRepairType();
                return;

            case R.id.radioGeneral:
                repairCategory.setVisibility(View.GONE);
                GetTimeEstimate.getTimeEstimateForGeneralService(ETA, estimateCard);
                return;

            case R.id.radioPolish:
                repairCategory.setVisibility(View.GONE);
                GetTimeEstimate.getTimeEstimateForWashNWaxService(ETA, estimateCard);
                return;
        }
    }

}
