package com.globopex.harimittioperator.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.ImageView;

import com.globopex.harimittioperator.view.AlertDialoge;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Afnan on 19-Jan-17.
 */

public class DownloadImage {

    private Activity activity;
    private ImageView imageView;
    private NetworkAvailability networkAvailability;
    private AlertDialoge alertDialoge;

    public DownloadImage() {
    }

    public void setDownloadImage(Activity activity, ImageView imageView, String imageUrl) {
        this.activity = activity;
        this.imageView = imageView;

        networkAvailability = new NetworkAvailability(activity);
        alertDialoge = new AlertDialoge(activity);
        new DownloadImg().execute(imageUrl);
    }

    public Bitmap getBitmap(String imageUrl) {
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

    // DownloadImage AsyncTask
    private class DownloadImg extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!networkAvailability.isNetworkAvailable()) {
                this.cancel(true);
                alertDialoge.showAlertDialog("single", "No internet", "Please check your internet connection !");
            }
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap
            imageView.setImageBitmap(result);
        }

    }

}
