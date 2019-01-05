package com.globopex.harimittioperator.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.globopex.harimittioperator.R;
import com.globopex.harimittioperator.adapters.ImagePagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * Created by Afnan on 10/2/16.
 */
public class ImageActivity extends FragmentActivity {

    private String[] multiPlantImgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle bundle = getIntent().getExtras();
        multiPlantImgs = bundle.getStringArray("multiPlantImgs");


        FragmentPagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), multiPlantImgs);

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        PageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

    }

}
