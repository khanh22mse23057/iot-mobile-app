package com.screens.activity.button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.screens.R;
import com.screens.utils.Tools;

public class FabExtend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_extend);

        Tools.setSystemBarLight(this);
    }
}