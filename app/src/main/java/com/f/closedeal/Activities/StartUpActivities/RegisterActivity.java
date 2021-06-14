package com.f.closedeal.Activities.StartUpActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.Activities.HomeActivity;
import com.f.closedeal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout userEmail, userPassword, userPassword2;

    private FirebaseAuth firebaseAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);


        userEmail = findViewById(R.id.signup_email);
        userPassword = findViewById(R.id.signup_password);
        userPassword2 = findViewById(R.id.signup_password2);
        Button regBtn = findViewById(R.id.signup_next_btn);
        Button loginBtn = findViewById(R.id.signup_login_btn);
        firebaseAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Please wait...");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();

            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getEditText().getText().toString().trim();
                String password = userPassword.getEditText().getText().toString().trim();
                String password2 = userPassword2.getEditText().getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    userEmail.setError("Invalid Email");
                    userEmail.setFocusable(true);
                } else if (password.length() < 8) {
                    userPassword.setError("Password must be at least 8 characters");
                    userPassword.setFocusable(true);
                } else if (!password2.equals(password)) {
                    userPassword2.setError("Passwords not matched");
                    userPassword2.setFocusable(true);
                } else {
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        pd.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String email = user.getEmail();
                    String uid = user.getUid();

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", "");
                    hashMap.put("onlineStatus", "online");
                    hashMap.put("typingTo", "noOne");
                    hashMap.put("phone number", "");
                    hashMap.put("date of birth", "");
                    hashMap.put("image", "");
                    hashMap.put("verificationStatus", "notVerified");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");
                    reference.child(uid).setValue(hashMap);

                    Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}