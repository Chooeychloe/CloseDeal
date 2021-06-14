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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneNumberVerificationActivity extends AppCompatActivity {


    private CountryCodePicker ccp;
    private Button sendOTPBtn, verifyOTP, resendOTP;
    private EditText phoneNumber, otpField;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken token;
    private String ccpNumber, userPhoneNumber, verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);


        sendOTPBtn = findViewById(R.id.sendBtn);
        phoneNumber = findViewById(R.id.phoneNumber);
        ccp = findViewById(R.id.ccp);


        otpField = findViewById(R.id.otpNumber);
        verifyOTP = findViewById(R.id.verifyOTP);
        resendOTP = findViewById(R.id.reSendBtn);
        resendOTP.setEnabled(false);
        auth = FirebaseAuth.getInstance();


        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phoneNumber.getText().toString().isEmpty()) {
                    phoneNumber.setError("Required");
                    return;
                }

                userPhoneNumber = "+"+ccp.getFullNumber().trim() + phoneNumber.getText().toString().trim();
                verifyPhoneNumber(userPhoneNumber);
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpField.getText().toString().isEmpty()) {
                    otpField.setError("Enter OTP");
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpField.getText().toString());
                authenticateUser(credential);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneNumberVerificationActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;

                ccp.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.GONE);
                sendOTPBtn.setVisibility(View.GONE);

                otpField.setVisibility(View.VISIBLE);
                verifyOTP.setVisibility(View.VISIBLE);
                resendOTP.setVisibility(View.VISIBLE);
                resendOTP.setEnabled(false);



                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("verificationStatus", "fully verified");
                hashMap.put("phone number", userPhoneNumber);
                dbRef.updateChildren(hashMap);

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOTP.setEnabled(true);

            }
        };

    }

    public void verifyPhoneNumber(String phoneNum) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setActivity(this)
                .setPhoneNumber(phoneNum)
                .setTimeout(180L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public void authenticateUser(PhoneAuthCredential credential) {

    }

}