package com.f.closedeal.Activities.InProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.Activities.HomeActivity;
import com.f.closedeal.Activities.SettingsActivity;
import com.f.closedeal.Activities.StartUpActivities.LoginSignUp;
import com.f.closedeal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedbackTxt;
    private Button feedbackBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDb;
    private ProgressDialog pd;
    private String uid, name;
    private FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedbackBtn = findViewById(R.id.feedback_btn);
        feedbackTxt = findViewById(R.id.feedbackTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        uid = fUser.getUid();
        checkUserStatus();

        userDb = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDb.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pd = new ProgressDialog(this);

        feedbackBtn.setOnClickListener(v -> {
            String feedback = feedbackTxt.getText().toString().trim();

            if (TextUtils.isEmpty(feedback)) {
                Toast.makeText(FeedbackActivity.this, "Empty feedback", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadData(feedback);
        });

    }

    private void uploadData(String feedback) {

        pd.setMessage("Submitting feedback...");
        pd.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("feedback", feedback);


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Feedbacks");
        ref1.push().setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(FeedbackActivity.this, "Feedback has been submitted.", Toast.LENGTH_SHORT).show();
                        feedbackTxt.setText("");


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {

        } else {
            startActivity(new Intent(getApplicationContext(), LoginSignUp.class));

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_create_group).setVisible(false);
        menu.findItem(R.id.action_add_group_participant).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {

            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();

            firebaseAuth.signOut();
            checkUserStatus();


        } else if (id == R.id.action_settings) {
            startActivity(new Intent(FeedbackActivity.this, SettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

}