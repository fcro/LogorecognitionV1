package com.telecom.cottoncrosnier.logorecognition.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.telecom.cottoncrosnier.logorecognition.R;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;


/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhotoActivity extends Activity {

    private final static String TAG = AnalizePhotoActivity.class.getSimpleName();

    private Address mAddress;
    private Brand mBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);
        Log.d(TAG, "onCreate:");

        mBrand = BrandList.getBrand("pimkie");
        Log.d(TAG, "brand = " + mBrand);

        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);

        Button button = (Button) findViewById(R.id.button_description_ok);
        button.setOnClickListener(new View.OnClickListener() {
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

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        try{
                 Bitmap bitmap = scaleBitmapDown(
                             MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath),
                            600);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }



    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

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
