package com.stylingandroid.viewpageranimator.sample;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.stylingandroid.viewpageranimator.ViewPagerAnimator;
import com.stylingandroid.viewpageranimator.sample.adapter.PagerAdapter;
import com.stylingandroid.viewpageranimator.sample.adapter.PagerAdapterFactory;

public class MainActivity extends AppCompatActivity {
    private PagerAdapterFactory factory;
    private ViewPagerAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        factory = new PagerAdapterFactory(this);

        ViewPager viewPager = setupViewpager();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setupWithViewPager(viewPager);
    }

    private ViewPager setupViewpager() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        final PagerAdapter pagerAdapter = factory.getPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);

        animator = ViewPagerAnimator.ofArgb(viewPager, pagerAdapter::getColour, viewPager::setBackgroundColor);

        return viewPager;
    }

    @Override
    protected void onDestroy() {
        animator.destroy();
        super.onDestroy();
    }
}
