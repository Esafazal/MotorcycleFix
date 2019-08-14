package com.fyp.motorcyclefix.RiderFragments.WorkshopFragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Dao.Booking;
import com.fyp.motorcyclefix.Dao.Workshop;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderPortal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class    ViewWorkshopActivity extends AppCompatActivity {

    private static final String TAG = "viewSelectedWorkshop";

    private TextView workshopName, specialized;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference workshopRef = db.collection("my_workshop");
    private DocumentReference bookingCount = db.collection("bookings").document("bookings_count");
    private String workshopId;
    private RadioGroup serviceTypeGroup;
    private DatePicker datePicker;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_view_workshop_activity);
        setTitle("");

        workshopName = findViewById(R.id.viewWorkshopName);
        specialized = findViewById(R.id.specializedIN);
        serviceTypeGroup = findViewById(R.id.radioServiceType);
        datePicker = findViewById(R.id.datePicker);
        progressBar = findViewById(R.id.bookingProgressBar);
        ImageView workshopImg = findViewById(R.id.workshopImg);

        workshopImg.setImageResource(R.drawable.reliability);


        Bundle bundle = getIntent().getExtras();
        workshopId = bundle.getString("workshopId");

        workshopRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Workshop workshop = documentSnapshot.toObject(Workshop.class);

                    String workshopIDs = documentSnapshot.getId();
                    String data = "|";
                    if (workshopIDs.equals(workshopId)) {

                        for (String special : workshop.getSpecialized()) {
                            data += "| " + special + " |";

                        }
                        data += "|";

                        workshopName.setText(workshop.getWorkshopName() + " - " + workshop.getLocationName());
                        specialized.setText(data);
                    }
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });


    }

    public void placeBookingClickHandler(View view) {
        progressBar.setVisibility(View.VISIBLE);
        sendBookingRequest();
    }

    private void sendBookingRequest() {

        final int selectedId = serviceTypeGroup.getCheckedRadioButtonId();
        RadioButton radioSelected = findViewById(selectedId);
        final String serviceType = radioSelected.getText().toString();
        final String pickedDate = datePicker.getMonth() + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            final String Uid = currentUser.getUid();

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
                            booking.setDateOfService(pickedDate);
                            booking.setUserId(Uid);
                            booking.setStatus("pending");
                            booking.setVehicleId(Uid);
                            booking.setDateOfBooking(null);
                            booking.setBookingID(count);

                            final String bookCount = String.valueOf(count);

                            db.collection("bookings").document(bookCount).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(ViewWorkshopActivity.this, "Booking Request Sent", Toast.LENGTH_SHORT).show();
                                    RiderPortal riderPortal = new RiderPortal();
//                                    riderPortal.loadFragment(new TrackingFragment());
                                    progressBar.setVisibility(View.GONE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewWorkshopActivity.this);
                                    builder.setTitle("BOOKING #"+bookCount)
                                            .setMessage("Your booking has been sent to "+workshopName.getText().toString()+
                                                    " for confirmation. You will be notified shortly." +
                                                        " \n Goto Trackings tab to view details.")
                                            .setPositiveButton("Alright, Cool.", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            }).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d(TAG, e.toString());
                                        }
                                    });

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
}
