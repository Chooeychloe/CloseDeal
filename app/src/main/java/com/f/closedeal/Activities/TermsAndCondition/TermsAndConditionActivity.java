package com.f.closedeal.Activities.TermsAndCondition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.R;

public class TermsAndConditionActivity extends AppCompatActivity {

    ActionBar actionBar;
    RelativeLayout tos, dp, cg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Terms and Condition");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tos = findViewById(R.id.termsOfService);
        dp = findViewById(R.id.dataPolicy);
        cg = findViewById(R.id.communityGuidelines);

        tos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TermsofServiceActivity.class));
            }
        });
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DataPolicyActivity.class));
            }
        });
        cg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommunityGuidelinesActivity.class));
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}