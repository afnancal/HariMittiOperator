package com.globopex.harimittioperator.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Afnan on 19-Jan-17.
 */

public class SaveImage {

    private final String path;
    private String PACKAGE_NAME;
    private Context context;

    public SaveImage(Context context) {
        this.context = context;
        PACKAGE_NAME = context.getPackageName();
        path = Environment.getExternalStorageDirectory() + "/Android/data/" + PACKAGE_NAME + "/";

    }

    public void ImageSaved(Bitmap finalBitmap, String image_file_name) {

        File myDir = new File(path);
        myDir.mkdirs();
        /*Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";*/
        File file = new File(myDir, image_file_name);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getSavedImage(String image_file_name) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path + image_file_name, options);
        return bitmap;
    }

}
