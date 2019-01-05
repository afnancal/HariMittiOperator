package com.globopex.harimittioperator.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afnan.libraryimagezoom.ImageSource;
import com.afnan.libraryimagezoom.SubsamplingScaleImageView;
import com.globopex.harimittioperator.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Afnan on 10/2/16.
 */
public class ImageFragmentImage extends Fragment {

    private static final String KEY_CONTENT = "TestFragment:Content";
    private String mContent = "";

    public ImageFragmentImage newInstance(String multiPicUrlBig) {
        ImageFragmentImage fragment = new ImageFragmentImage();

        fragment.mContent = multiPicUrlBig;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_image, container, false);

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) layout.findViewById(R.id.imageView);
        Bitmap bitmap = getBitmap(mContent);
        imageView.setImage(ImageSource.bitmap(bitmap));

        return layout;
    }

    private Bitmap getBitmap(String imageUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //from web
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
