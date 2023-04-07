package com.screens.activity.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.screens.R;
import com.screens.utils.Tools;

public class SplashLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        View logo = findViewById(R.id.logo);
        View form = findViewById(R.id.form);
        form.setVisibility(View.INVISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(600);
        alphaAnimation.setRepeatCount(2);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        logo.startAnimation(alphaAnimation);

        AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                form.setVisibility(View.VISIBLE);
                form.startAnimation(alphaAnimation2);

                logo.animate().translationY(-(form.getHeight()/2.0f))
                        .setInterpolator(new LinearOutSlowInInterpolator())
                        .setDuration(1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Tools.fullScreen(this);
    }
}