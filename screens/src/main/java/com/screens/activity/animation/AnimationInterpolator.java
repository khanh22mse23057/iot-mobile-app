package com.screens.activity.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.screens.R;
import com.screens.utils.Tools;

public class AnimationInterpolator extends AppCompatActivity {

    private View object;
    private AnimatorSet mAnimatorSet;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_interpolator);

        object = findViewById(R.id.object);
        int valueInPixels = (int) getResources().getDimension(R.dimen.spacing_large);
        distance = Tools.getScreenWidth() - (valueInPixels * 3);

        mAnimatorSet = new AnimatorSet();
        initToolbar();

        findViewById(R.id.inter_linear).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new LinearInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_accelerate).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new AccelerateInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_decelerate).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new DecelerateInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_accelerate_decelerate).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new AccelerateDecelerateInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_overshoot).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new OvershootInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_anticipate).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new AnticipateInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_anticipate_overshoot).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new AnticipateOvershootInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_bounce).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new BounceInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_fast_out_linear_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new FastOutLinearInInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_fast_out_slow_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new FastOutSlowInInterpolator())
            );
            restart();
        });

        findViewById(R.id.inter_linear_out_slow_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate(new LinearOutSlowInInterpolator())
            );
            restart();
        });
    }

    private void restart() {
        mAnimatorSet.end();
        mAnimatorSet.cancel();

        resetObject();

        // start
        mAnimatorSet.start();
    }

    private void resetObject() {
        // reset object
        object.setAlpha(1);
        object.setScaleX(1);
        object.setScaleY(1);
        object.setTranslationX(0);
        object.setTranslationY(0);
        object.setRotation(0);
        object.setRotationY(0);
        object.setRotationX(0);
    }

    private ObjectAnimator animate(TimeInterpolator interpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(object, "translationX", 0, distance);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler(getMainLooper()).postDelayed(() -> resetObject(), 1500);
            }
        });
        return objectAnimator;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animation Interpolator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
