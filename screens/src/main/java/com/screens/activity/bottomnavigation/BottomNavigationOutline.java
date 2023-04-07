package com.screens.activity.bottomnavigation;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.screens.R;
import com.screens.utils.Tools;

public class BottomNavigationOutline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_outline);
        initToolbar();

        (findViewById(R.id.nav_menu_1)).setOnClickListener(view -> refreshMenu(R.id.nav_menu_1));
        (findViewById(R.id.nav_menu_2)).setOnClickListener(view -> refreshMenu(R.id.nav_menu_2));
        (findViewById(R.id.nav_menu_3)).setOnClickListener(view -> refreshMenu(R.id.nav_menu_3));
        (findViewById(R.id.nav_menu_4)).setOnClickListener(view -> refreshMenu(R.id.nav_menu_4));
        (findViewById(R.id.nav_menu_5)).setOnClickListener(view -> refreshMenu(R.id.nav_menu_5));

        refreshMenu(R.id.nav_menu_3);
    }

    private void refreshMenu(int id) {
        int primary = ContextCompat.getColor(this, R.color.colorPrimary);
        int grey = ContextCompat.getColor(this, R.color.grey_40);

        LinearLayout nav_menu_1 = findViewById(R.id.nav_menu_1);
        ((ImageView) nav_menu_1.getChildAt(0)).setColorFilter(id == R.id.nav_menu_1 ? primary : grey, android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) nav_menu_1.getChildAt(1)).setTextColor(id == R.id.nav_menu_1 ? primary : grey);

        LinearLayout nav_menu_2 = findViewById(R.id.nav_menu_2);
        ((ImageView) nav_menu_2.getChildAt(0)).setColorFilter(id == R.id.nav_menu_2 ? primary : grey, android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) nav_menu_2.getChildAt(1)).setTextColor(id == R.id.nav_menu_2 ? primary : grey);

        LinearLayout nav_menu_3 = findViewById(R.id.nav_menu_3);
        ((ImageView) nav_menu_3.getChildAt(0)).setColorFilter(id == R.id.nav_menu_3 ? primary : grey, android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) nav_menu_3.getChildAt(1)).setTextColor(id == R.id.nav_menu_3 ? primary : grey);

        LinearLayout nav_menu_4 = findViewById(R.id.nav_menu_4);
        ((ImageView) nav_menu_4.getChildAt(0)).setColorFilter(id == R.id.nav_menu_4 ? primary : grey, android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) nav_menu_4.getChildAt(1)).setTextColor(id == R.id.nav_menu_4 ? primary : grey);

        LinearLayout nav_menu_5 = findViewById(R.id.nav_menu_5);
        ((ImageView) nav_menu_5.getChildAt(0)).setColorFilter(id == R.id.nav_menu_5 ? primary : grey, android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) nav_menu_5.getChildAt(1)).setTextColor(id == R.id.nav_menu_5 ? primary : grey);
    }

    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }
}