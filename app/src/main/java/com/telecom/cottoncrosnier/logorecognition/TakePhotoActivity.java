package com.telecom.cottoncrosnier.logorecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by matthieu on 24/10/16.
 */

public class TakePhotoActivity extends Activity {

    private static final String TAG = TakePhotoActivity.class.getSimpleName();

    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    public static int mPhotoNumber = 0;

    public static String FILE_NAME = "temp.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_activity);
        startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult:: result actvity");

            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, Uri.fromFile(getCameraFile()));
            resultIntent.putExtra(MainActivity.KEY_PHOTO_NUMBER, mPhotoNumber);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Utils.permissionGranted(
                requestCode,
                CAMERA_PERMISSIONS_REQUEST,
                grantResults)) {
            startCamera();
        }
    }

    public void startCamera() {
        if (Utils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoNumber++;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = mPhotoNumber + FILE_NAME;
        return new File(dir, fileName);
    }

}
