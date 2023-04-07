package com.screens.activity.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.screens.R;
import com.screens.utils.Tools;

public class SplashTikTok extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_tik_tok);
        Tools.fullScreen(this);
    }
}