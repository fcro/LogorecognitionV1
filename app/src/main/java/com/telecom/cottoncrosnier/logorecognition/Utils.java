package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthieu on 24/10/16.
 */

public class Utils {

    private final static String TAG = Utils.class.getSimpleName();

    public static boolean requestPermission(
            Activity activity, int requestCode, String... permissions) {
        boolean granted = true;
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        for (String s : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, s);
            boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            granted &= hasPermission;
            if (!hasPermission) {
                permissionsNeeded.add(s);
            }
        }

        if (granted) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    requestCode);
            return false;
        }
    }


    public static boolean permissionGranted(
            int requestCode, int permissionCode, int[] grantResults) {
        return requestCode == permissionCode
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }


    public static File assetToCache(Context context, String assetPath, String fileName) {
        InputStream is;
        FileOutputStream fos;
        int size;
        byte[] buffer;
        String filePath = context.getCacheDir() + "/" + fileName;
        File file = new File(filePath);
        AssetManager assetManager = context.getAssets();

        try {
            is = assetManager.open(assetPath);
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();

            fos = new FileOutputStream(filePath);
            fos.write(buffer);
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File galleryToCache(Context context, Uri imgPath, String fileName) {
        InputStream is;
        FileOutputStream fos;
        int size;
        byte[] buffer;
        String filePath = context.getCacheDir() + "/" + fileName;
        File file = new File(filePath);

        try {
            is = context.getContentResolver().openInputStream(imgPath);
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();

            fos = new FileOutputStream(filePath);
            fos.write(buffer);
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File bitmapToCache(Context context, Bitmap bitmap, String fileName) {
        File file = new File(context.getCacheDir(), fileName);

        try {
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);

            fos.flush();
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<String> filesStartWith(String[] files, String prefix) {
        List<String> fileList = new ArrayList<String>();

        for (String file : files) {
            if (file.startsWith(prefix)) {
                fileList.add(file);
            }
        }

        return fileList;
    }


    public static void toast(Context context, String text) {
        Toast.makeText(context, text,
                Toast.LENGTH_SHORT).show();
    }
}