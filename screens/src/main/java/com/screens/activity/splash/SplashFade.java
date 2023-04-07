package com.screens.activity.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.screens.R;
import com.screens.utils.Tools;

public class SplashFade extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_fade);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(800);
        alphaAnimation.setRepeatCount(100);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.logo).startAnimation(alphaAnimation);

        Tools.fullScreen(this);
    }
}