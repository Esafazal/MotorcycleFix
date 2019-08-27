package com.fyp.motorcyclefix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fyp.motorcyclefix.Dao.User;
import com.fyp.motorcyclefix.Services.CheckNetworkConnection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //Variables for widgets, model and firebase/firestore
    private Intent intent;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //initilize to get reference to layout widgets
        progressBar = findViewById(R.id.mainActivityProgressBar);
        constraintLayout = findViewById(R.id.mainActivityConstraint);

    }

    public void riderButton(View view) {

        //goto login activity with a value of  one denoting rider
        intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    public void mechanicButton(View view) {
        //goto login activity with a value of  one denoting mechanic
        intent = new Intent(this, LoginActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Firebase user intance to get current user logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //check if network connection is available, if not display snackbar message
        boolean isConnected = CheckNetworkConnection.isNetworkAvailable(this);
        if(!isConnected){
            Snackbar.make(constraintLayout, "NO CONNECTION!", Snackbar.LENGTH_INDEFINITE).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        // check if there is a logged in user
        if (currentUser != null) {
            //get user id of the logged in user and get details of the user from firestore
            String uId = currentUser.getUid();
            userRef.document(uId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //map the document snapshot to user model and get user type
                    User user = documentSnapshot.toObject(User.class);
                    String type = user.getType();

                    //User type not null check, type of user, display toast and goto rider/mechanic portal
                    if (type != null) {
                        if (type.contentEquals("rider")) {
                            Toast.makeText(MainActivity.this, "Hi RiderPortalService!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), RiderPortal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        else if (type.contentEquals("mechanic")) {
                            Toast.makeText(MainActivity.this, "Hi Mechanic!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MechanicPortal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        } else{
            progressBar.setVisibility(View.GONE);
        }
    }
}
