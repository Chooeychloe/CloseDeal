package com.f.closedeal.Activities.InProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.Activities.HomeActivity;
import com.f.closedeal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOTPActivity extends AppCompatActivity {

    private EditText otpEdit;
    private String OTP;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        Button sendBtn = findViewById(R.id.sendBtn);
        otpEdit = findViewById(R.id.otp);

        auth = FirebaseAuth.getInstance();

        OTP = getIntent().getStringExtra("auth");
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = otpEdit.getText().toString();
                if (!verificationCode.isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            sendToMain();

                        } else {
                            Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
    private void sendToMain() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

    }

}