package com.screens.activity.splash;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.screens.R;

import java.util.ArrayList;
import java.util.List;

public class SplashLight extends AppCompatActivity {

    List<ImageView> images = new ArrayList<>();
    ImageView prevImage;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_light);

        images.add(findViewById(R.id.image_bg_1));
        images.add(findViewById(R.id.image_bg_2));
        images.add(findViewById(R.id.image_bg_3));

        prevImage = images.get(index);
        animateLoading();
    }

    private void animateLoading() {
        new Handler().postDelayed(() -> {
            if(prevImage != null) prevImage.setColorFilter(ContextCompat.getColor(SplashLight.this, R.color.colorPrimaryLight));
            ImageView img = images.get(index);
            img.setColorFilter(ContextCompat.getColor(SplashLight.this, R.color.grey_10));
            prevImage = img;
            index++;
            if(index > images.size()-1) index = 0;
            animateLoading();
        }, 500);
    }


}