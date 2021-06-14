package com.f.closedeal.Activities.StartUpActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.f.closedeal.Activities.HomeActivity;
import com.f.closedeal.R;

public class SplashScreen extends AppCompatActivity {

    //Variables
    ImageView backgroundImage;

    //Animations
    Animation sideAnim, bottomAnim;

    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //Hooks
        backgroundImage = findViewById(R.id.background_image);

        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //set Animations on elements
        backgroundImage.setAnimation(sideAnim);
        backgroundImage.setAnimation(bottomAnim);
        new Handler().postDelayed(() -> {

            onBoardingScreen = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

            if(isFirstTime){

                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime",false);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                startActivity(intent);
            }

            else {

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
            finish();

        },5000);


    }
}