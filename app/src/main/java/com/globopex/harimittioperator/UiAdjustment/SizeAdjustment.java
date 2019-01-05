package com.globopex.harimittioperator.UiAdjustment;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Afnan on 26/8/15.
 */
public class SizeAdjustment {

    public int getVerticalRatio(int pix, Context con) {
        DisplayMetrics metrics = con.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int pixm = (int) (((double) pix / (double) 480) * height);
        if (pixm == 0) {
            pixm = 1;
        }
        return pixm;
    }

    public int getHorizontalRatio(int pix, Context con) {
        DisplayMetrics metrics = con.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int pixm = (int) (((double) pix / (double) 320) * width);
        if (pixm == 0) {
            pixm = 1;
        }
        return pixm;
    }

    public float getTextRatio(int pix, Context con) {
        DisplayMetrics metrics = con.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        float pixm = (float) (((double) pix / (double) 320) * width);
        if (pixm == 0.0f) {
            pixm = 1;
        }
        return pixm;
    }

}
