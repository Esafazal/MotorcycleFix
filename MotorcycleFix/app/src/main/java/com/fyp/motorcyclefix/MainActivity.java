package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void riderButton(View view) {

        intent = new Intent(this, LoginActivity.class);

        intent.putExtra("type", "1");
        startActivity(intent);
    }

    public void mechanicButton(View view) {

        intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uId = currentUser.getUid();
            userRef.document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String type = documentSnapshot.getString("type");
                    if (type != null) {

                        if (type.contentEquals("rider")) {
                            Toast.makeText(MainActivity.this, "Hi Rider!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), RiderPortal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        else if (type.contentEquals("mechanic")) {
                            Toast.makeText(MainActivity.this, "Hi Mechanic!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MechanicPortal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }
    }
}
