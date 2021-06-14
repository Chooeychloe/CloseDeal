package com.f.closedeal.Activities.StartUpActivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.Activities.TermsAndCondition.TermsAndConditionActivity;
import com.f.closedeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUp extends AppCompatActivity {
    Button loginBtn, signupBtn;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_sign_up);
        loginBtn = findViewById(R.id.login_btn);
        signupBtn = findViewById(R.id.signup_btn);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.f.closedeal.Activities.HomeActivity.class);

        dialog = new Dialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLoginDialog();

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSignUpDialog();

            }
        });

    }

    private void openSignUpDialog() {
        dialog.setContentView(R.layout.signup_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView close = dialog.findViewById(R.id.closeBtn);
        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button terms_condition = dialog.findViewById(R.id.terms_condition_btn);
        dialog.show();

        close.setOnClickListener(v -> dialog.dismiss());
        okBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
        terms_condition.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TermsAndConditionActivity.class);
            startActivity(intent);
        });
    }

    private void openLoginDialog() {

        dialog.setContentView(R.layout.login_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView close = dialog.findViewById(R.id.closeBtn);
        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button terms_condition = dialog.findViewById(R.id.terms_condition_btn);
        dialog.show();

        close.setOnClickListener(v -> dialog.dismiss());
        okBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
        terms_condition.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TermsAndConditionActivity.class);
            startActivity(intent);
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            updateUI();
        }
    }
    private void updateUI() {

        startActivity(HomeActivity);
        finish();

    }
}