package com.screens.activity.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.screens.R;
import com.screens.utils.Tools;

public class AnimationBasic extends AppCompatActivity {

    private View object;
    private AnimatorSet mAnimatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_basic);

        object = findViewById(R.id.object);
        ViewGroup parent = (ViewGroup) object.getParent();
        int distance = parent.getHeight() - object.getTop();

        mAnimatorSet = new AnimatorSet();
        initToolbar();

        // ---------------- fade in ---------------------------

        findViewById(R.id.fade_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1)
            );
            restart();
        });

        findViewById(R.id.fade_in_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1),
                    animate("translationY", object.getHeight(), 0)
            );
            restart();
        });

        findViewById(R.id.fade_in_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1),
                    animate("translationY", -object.getHeight(), 0)
            );
            restart();
        });

        findViewById(R.id.fade_in_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1),
                    animate("translationX", -object.getWidth() / 2f, 0)
            );
            restart();
        });

        findViewById(R.id.fade_in_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1),
                    animate("translationX", object.getWidth() / 2f, 0)
            );
            restart();
        });

        // ---------------- fade out ---------------------------

        findViewById(R.id.fade_out).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0)
            );
            restart();
        });

        findViewById(R.id.fade_out_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("translationY", 0, object.getHeight())
            );
            restart();
        });

        findViewById(R.id.fade_out_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("translationY", 0, -object.getHeight())
            );
            restart();
        });

        findViewById(R.id.fade_out_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("translationX", 0, -object.getWidth() / 2f)
            );
            restart();
        });

        findViewById(R.id.fade_out_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("translationX", 0, object.getWidth() / 2f)
            );
            restart();
        });

        // ---------------- bounce ---------------------------

        findViewById(R.id.bounce_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1, 1),
                    animate("scaleX", 0.3f, 1.05f, 0.9f, 1),
                    animate("scaleY", 0.3f, 1.05f, 0.9f, 1)
            );
            restart();
        });

        findViewById(R.id.bounce_in_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1, 1),
                    animate("translationY", -object.getHeight(), 30, -10, 0)
            );
            restart();
        });

        findViewById(R.id.bounce_in_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("translationX", -object.getWidth(), 30, -10, 0),
                    animate("alpha", 0, 1, 1, 1)
            );
            restart();
        });

        findViewById(R.id.bounce_in_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("translationX", object.getMeasuredWidth() + object.getWidth(), -30, 10, 0),
                    animate("alpha", 0, 1, 1, 1)
            );
            restart();
        });

        findViewById(R.id.bounce_in_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("translationY", object.getMeasuredHeight(), -30, 10, 0),
                    animate("alpha", 0, 1, 1, 1)
            );
            restart();
        });

        // ---------------- zoom in ---------------------------

        findViewById(R.id.zoom_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("scaleX", 0.45f, 1),
                    animate("scaleY", 0.45f, 1),
                    animate("alpha", 0, 1)
            );
            restart();
        });

        findViewById(R.id.zoom_in_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("scaleX", 0.1f, 0.475f, 1),
                    animate("scaleY", 0.1f, 0.475f, 1),
                    animate("translationY", -object.getBottom(), 0),
                    animate("alpha", 0, 1, 1)
            );
            restart();
        });

        findViewById(R.id.zoom_in_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("scaleX", 0.1f, 0.475f, 1),
                    animate("scaleY", 0.1f, 0.475f, 1),
                    animate("translationX", -object.getRight(), 0),
                    animate("alpha", 0, 1, 1)
            );
            restart();
        });

        findViewById(R.id.zoom_in_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("scaleX", 0.1f, 0.475f, 1),
                    animate("scaleY", 0.1f, 0.475f, 1),
                    animate("translationX", object.getWidth() + object.getPaddingRight(), 0),
                    animate("alpha", 0, 1, 1)
            );
            restart();
        });

        findViewById(R.id.zoom_in_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1),
                    animate("scaleX", 0.1f, 0.475f, 1),
                    animate("scaleY", 0.1f, 0.475f, 1),
                    animate("translationY", object.getBottom(), 0)
            );
            restart();
        });

        // ---------------- zoom out ---------------------------

        findViewById(R.id.zoom_out).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0, 0),
                    animate("scaleX", 1, 0.3f, 0),
                    animate("scaleY", 1, 0.3f, 0)
            );
            restart();
        });

        findViewById(R.id.zoom_out_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("scaleX", 1, 0.475f, 0.1f),
                    animate("scaleY", 1, 0.475f, 0.1f),
                    animate("translationY", 0, object.getBottom())
            );
            restart();
        });

        findViewById(R.id.zoom_out_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("scaleX", 1, 0.475f, 0.1f),
                    animate("scaleY", 1, 0.475f, 0.1f),
                    animate("translationX", 0, -object.getRight())
            );
            restart();
        });

        findViewById(R.id.zoom_out_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("scaleX", 1, 0.475f, 0.1f),
                    animate("scaleY", 1, 0.475f, 0.1f),
                    animate("translationX", 0, object.getRight())
            );
            restart();
        });

        findViewById(R.id.zoom_out_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("scaleX", 1, 0.475f, 0.1f),
                    animate("scaleY", 1, 0.475f, 0.1f),
                    animate("translationY", 0, -object.getBottom())
            );
            restart();
        });

        // ---------------- slide in ---------------------------

        findViewById(R.id.slide_in_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1),
                    animate("translationY", -object.getBottom(), 0)
            );
            restart();
        });

        findViewById(R.id.slide_in_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1),
                    animate("translationX", -object.getRight(), 0)
            );
            restart();
        });

        findViewById(R.id.slide_in_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1),
                    animate("translationX", object.getRight(), 0)
            );
            restart();
        });

        findViewById(R.id.slide_in_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 0, 1, 1),
                    animate("translationY", object.getBottom(), 0)
            );
            restart();
        });

        // ---------------- slide out ---------------------------

        findViewById(R.id.slide_out_down).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("translationY", 0, object.getBottom())
            );
            restart();
        });

        findViewById(R.id.slide_out_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("translationX", 0, -object.getRight())
            );
            restart();
        });

        findViewById(R.id.slide_out_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("translationX", 0, object.getRight())
            );
            restart();
        });

        findViewById(R.id.slide_out_up).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 1, 0),
                    animate("translationY", 0, -object.getBottom())
            );
            restart();
        });

        // ---------------- rotate in ---------------------------

        findViewById(R.id.rotate_in).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("rotation", -200, 0),
                    animate("alpha", 0, 1)
            );
            restart();
        });

        findViewById(R.id.rotate_in_down_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getPaddingLeft();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("rotation", -90, 0),
                    animate("alpha", 0, 1),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_in_down_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getWidth() - object.getPaddingRight();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("rotation", 90, 0),
                    animate("alpha", 0, 1),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_in_up_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getPaddingLeft();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("rotation", 90, 0),
                    animate("alpha", 0, 1),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_in_up_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getWidth() - object.getPaddingRight();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("rotation", -90, 0),
                    animate("alpha", 0, 1),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        // ---------------- rotate out ---------------------------

        findViewById(R.id.rotate_out).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("rotation", 0, 200)
            );
            restart();
        });

        findViewById(R.id.rotate_out_down_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getPaddingLeft();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("rotation", 0, 90),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_out_down_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getWidth() - object.getPaddingRight();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("rotation", 0, -90),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_out_up_left).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getPaddingLeft();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("rotation", 0, -90),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        findViewById(R.id.rotate_out_up_right).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            float x = object.getWidth() - object.getPaddingRight();
            float y = object.getHeight() - object.getPaddingBottom();
            mAnimatorSet.playTogether(
                    animate("alpha", 1, 0),
                    animate("rotation", 0, 90),
                    animate("pivotX", x, x),
                    animate("pivotY", y, y)
            );
            restart();
        });

        // ---------------- flip in out ---------------------------

        findViewById(R.id.flip_in_x).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("rotationX", 90, -15, 15, 0),
                    animate("alpha", 0.25f, 0.5f, 0.75f, 1)
            );
            restart();
        });

        findViewById(R.id.flip_out_x).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("rotationX", 0, 90),
                    animate("alpha", 1, 0)
            );
            restart();
        });

        findViewById(R.id.flip_in_y).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("rotationY", 90, -15, 15, 0),
                    animate("alpha", 0.25f, 0.5f, 0.75f, 1)
            );
            restart();
        });

        findViewById(R.id.flip_out_y).setOnClickListener(view -> {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(
                    animate("rotationY", 0, 90),
                    animate("alpha", 1, 0)
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
        object.setPivotX(object.getWidth() / 2f);
        object.setPivotY(object.getHeight() / 2f);
    }

    private ObjectAnimator animate(String style, float... values) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(object, style, values).setDuration(1000);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler(getMainLooper()).postDelayed(() -> resetObject(), 500);
            }
        });
        return objectAnimator;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Animation Basic");
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
