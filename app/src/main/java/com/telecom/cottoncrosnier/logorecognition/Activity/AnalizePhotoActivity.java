package com.telecom.cottoncrosnier.logorecognition.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.telecom.cottoncrosnier.logorecognition.R;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.service.AnalyzePhotoService;


/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhotoActivity extends Activity {

    private final static String TAG = AnalizePhotoActivity.class.getSimpleName();

    private Brand mBrand;

    private BroadcastReceiver mBroadcastReceiver;

    private TextView mBrandNameTextView;
    private TextView mBrandInfoTextView;
    private ImageView mImageView;
    private Button mButton;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);
        Log.d(TAG, "onCreate:");

        mBrandNameTextView = (TextView) findViewById(R.id.text_brandname);
        mBrandInfoTextView = (TextView) findViewById(R.id.text_brandinfo);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mButton = (Button) findViewById(R.id.button_description_ok);

        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);

        mProgressDialog = ProgressDialog.show(
                this, getString(R.string.progress_analyzing), getString(R.string.wait));

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayResult((Brand) intent.getSerializableExtra(AnalyzePhotoService.ANALYZE_RESULT));
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AnalyzePhotoService.BROADCAST_ACTION_ANALYZE));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                resultIntent.putExtra(MainActivity.KEY_PHOTO_BRAND, mBrand);
                //Log.d(TAG, "onClick: lat"+mLatLng.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Intent analyzeIntent = new Intent(this, AnalyzePhotoService.class);
        analyzeIntent.setData(imgPath);
        startService(analyzeIntent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }



    private void displayResult(Brand brand) {
        Log.d(TAG, "displayResult:: brand = " + brand.getBrandName());

        mBrand = brand;

        mProgressDialog.dismiss();

        mButton.setVisibility(View.VISIBLE);

        mBrandNameTextView.setText(mBrand.getBrandName());
        mBrandInfoTextView.setText(mBrand.getInfo());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = scaleBitmapDown(
                BitmapFactory.decodeFile(mBrand.getLogo(this), options),
                300);

        mImageView.setImageBitmap(bitmap);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }



    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

}
