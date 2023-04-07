package com.screens.activity.motion;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.screens.R;

import com.screens.fragment.FragmentMotionSearchBar;
import com.screens.utils.Tools;

public class MotionSearchBarExpand extends AppCompatActivity {

    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_search_bar_expand);
        fragmentManager = getSupportFragmentManager();
        FragmentMotionSearchBarExpand fragment = new FragmentMotionSearchBarExpand();
        fragment.setFragmentMan(fragmentManager);

        fragmentManager.beginTransaction()
                .add(R.id.content, fragment)
                .commit();

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.onSearchBarClicked();
            }
        }, 1000);
    }

    public static class FragmentMotionSearchBarExpand extends Fragment {

        private CardView searchBar;

        public FragmentManager fragmentMan;

        public FragmentMotionSearchBarExpand() {
            // Required empty public constructor
        }

        public void setFragmentMan(FragmentManager fragmentMan) {
            this.fragmentMan = fragmentMan;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_motion_search_bar_expand, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            searchBar = view.findViewById(R.id.search_bar);
            ViewCompat.setTransitionName(searchBar, "simple_fragment_transition");

            (view.findViewById(R.id.bt_search)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchBarClicked();
                }
            });
            (view.findViewById(R.id.lyt_content)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchBarClicked();
                }
            });
            (view.findViewById(R.id.bt_mic)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchBarClicked();
                }
            });
        }

        public void onSearchBarClicked() {
            FragmentMotionSearchBar simpleFragmentB = FragmentMotionSearchBar.newInstance();
            fragmentMan.beginTransaction()
                    .addSharedElement(searchBar, ViewCompat.getTransitionName(searchBar))
                    .addToBackStack(null)
                    .replace(R.id.content, simpleFragmentB)
                    .commit();
        }

    }

}