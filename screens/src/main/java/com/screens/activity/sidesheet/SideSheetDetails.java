package com.screens.activity.sidesheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.screens.R;
import com.screens.adapter.AdapterGridAlbums;
import com.screens.adapter.AdapterListSectioned;
import com.screens.data.DataGenerator;
import com.screens.model.Image;
import com.screens.model.People;
import com.screens.utils.Tools;
import com.screens.widget.SpacingItemDecoration;

import java.util.List;

public class SideSheetDetails extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterGridAlbums mAdapter;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_sheet_details);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_3);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        drawer = findViewById(R.id.drawer_layout);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        // open drawer at start
        new Handler(this.getMainLooper()).postDelayed(() -> drawer.openDrawer(GravityCompat.END), 1000);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 4), true));
        recyclerView.setHasFixedSize(true);

        List<Image> items = DataGenerator.getImageDate2(this);

        //set data and list adapter
        mAdapter = new AdapterGridAlbums(this, items);
        mAdapter.setLayoutId(R.layout.item_grid_card);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener((view, obj, position) -> {
            Tools.displayImageOriginal(SideSheetDetails.this, findViewById(R.id.image_main), obj.image);
            drawer.openDrawer(GravityCompat.END);
        });

        Tools.displayImageOriginal(this, findViewById(R.id.image_main), items.get(items.size()-1).image);

        Tools.displayImageOriginal(this, findViewById(R.id.image_1), R.drawable.image_5);
        Tools.displayImageOriginal(this, findViewById(R.id.image_2), R.drawable.image_6);
        Tools.displayImageOriginal(this, findViewById(R.id.image_3), R.drawable.image_7);
        Tools.displayImageOriginal(this, findViewById(R.id.image_4), R.drawable.image_8);
        Tools.displayImageOriginal(this, findViewById(R.id.image_5), R.drawable.image_10);
        Tools.displayImageOriginal(this, findViewById(R.id.image_6), R.drawable.image_12);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else if (item.getItemId() == R.id.action_filter || item.getItemId() == R.id.action_search) {
            drawer.openDrawer(GravityCompat.END);
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}