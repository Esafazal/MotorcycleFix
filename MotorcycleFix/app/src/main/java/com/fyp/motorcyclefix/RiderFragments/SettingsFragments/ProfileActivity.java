package com.fyp.motorcyclefix.RiderFragments.SettingsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.LoginActivity;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderPortal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "profileActivity";

    private EditText Name, Email, PhoneNo;
    private RadioGroup sexGroup;
    private RadioButton radioSelected;
    private Button updateBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_settings_activity);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = findViewById(R.id.nameEdit);
        Email = findViewById(R.id.emailEdit);
        PhoneNo = findViewById(R.id.phoneEdit);
        sexGroup = findViewById(R.id.radioSexProfile);

        getProfileDetails();
    }

    private void getProfileDetails() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String userId = currentUser.getUid();

            userRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String gender = documentSnapshot.getString("gender");

                    Name.setText(name);
                    Email.setText(email);
                    if(gender.contentEquals("male")){
                        sexGroup.check(R.id.radioMaleProfile);
                    } else{
                        sexGroup.check(R.id.radioFemaleProfile);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Unable to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else{
            Toast.makeText(this, "Please login again!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "1");
            startActivity(intent);
            finish();

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }

    public void profileUpdateClickHandler(View view) {

        int selectedId = sexGroup.getCheckedRadioButtonId();
        radioSelected = findViewById(selectedId);
        String gender = radioSelected.getText().toString();
        String name = Name.getText().toString();
        String email = Email.getText().toString();
        String type = "rider";

        User user = new User(type, name, email, gender);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        userRef.document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, RiderPortal.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(ProfileActivity.this, "Couldn't update details!", Toast.LENGTH_SHORT).show();
                    }
                });



    }
}
