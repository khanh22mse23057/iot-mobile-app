package com.screens.activity.intro;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.screens.R;
import com.screens.data.DataGenerator;
import com.screens.model.Image;
import com.screens.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class IntroDepth extends AppCompatActivity {

    private int max_step = 0;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button btnNext;
    private List<Image> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_depth);

        items = DataGenerator.getIntroOrangeData(this);
        max_step = items.size();

        initComponent();
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);

        // adding bottom dots
        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setPageTransformer(false, new PageTransformer());
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem() + 1;
            if (current < max_step) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                finish();
            }
        });

        ((ImageButton) findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[max_step];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int height = 8;
            int width = i == current_index ? (height * 15) : (height * 4);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width, height));
            params.setMargins(2, 10, 2, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_rectangle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        dots[current_index].setImageResource(R.drawable.shape_rectangle);
        dots[current_index].setColorFilter(getResources().getColor(R.color.orange_700), PorterDuff.Mode.SRC_IN);
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);

            if (position == max_step - 1) {
                btnNext.setText(getString(R.string.DONE));

            } else {
                btnNext.setText(getString(R.string.NEXT));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_intro, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(items.get(position).name);
            ((TextView) view.findViewById(R.id.description)).setText(items.get(position).brief);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(items.get(position).image);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return max_step;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public static class PageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            if (position < -1) {    // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) {    // [-1,0]
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) {    // (0,1]
                view.setTranslationX(-position * view.getWidth());
                view.setAlpha(1 - Math.abs(position));
                view.setScaleX(1 - Math.abs(position));
                view.setScaleY(1 - Math.abs(position));

            } else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);

            }
        }
    }
}