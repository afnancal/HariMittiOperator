package com.globopex.harimittioperator.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Afnan on 10/2/16.
 */
public class ImagePagerAdapter extends FragmentPagerAdapter {

    String[] multiplePicUrlBig;

    public ImagePagerAdapter(FragmentManager supportFragmentManager, String[] multiPicUrlBig) {
        super(supportFragmentManager);
        multiplePicUrlBig = multiPicUrlBig;
    }

    @Override
    public Fragment getItem(int position) {
        ImageFragmentImage imageFragmentImage = new ImageFragmentImage();
        return imageFragmentImage.newInstance(multiplePicUrlBig[position]);
    }

    @Override
    public int getCount() {
        return multiplePicUrlBig.length;
    }
}
