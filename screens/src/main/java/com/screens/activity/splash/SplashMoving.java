package com.screens.activity.splash;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import com.screens.R;

public class SplashMoving extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_moving);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
        findViewById(R.id.logo).startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
        findViewById(R.id.logo_big).startAnimation(animation2);
    }
}