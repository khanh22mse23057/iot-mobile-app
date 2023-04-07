package com.screens.activity.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.screens.R;
import com.screens.utils.Tools;

public class SplashLogoBig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_logo_big);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }
}