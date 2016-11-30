package com.telecom.cottoncrosnier.logorecognition.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.telecom.cottoncrosnier.logorecognition.R;
import com.telecom.cottoncrosnier.logorecognition.image.MatManager;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;
import com.telecom.cottoncrosnier.logorecognition.reference.RefDescriptors;


/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhotoActivity extends Activity {

    private final static String TAG = AnalizePhotoActivity.class.getSimpleName();

    private Brand mBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);
        Log.d(TAG, "onCreate:");

        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);

        TextView brandNameTextView = (TextView) findViewById(R.id.text_brandname);
        TextView brandInfoTextView = (TextView) findViewById(R.id.text_brandinfo);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
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

        selectBestBrand(new MatManager(imgPath.getPath()));
        Log.d(TAG, "onCreate: mBrand = " + mBrand.getBrandName());

        brandNameTextView.setText(mBrand.getBrandName());
        brandInfoTextView.setText(mBrand.getInfo());

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = scaleBitmapDown(
                    BitmapFactory.decodeFile(mBrand.getLogo(this), options),
                    400);

            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
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



    private void selectBestBrand(final MatManager mat) {
        new Runnable() {
            @Override
            public void run() {

                Brand bestBrand = BrandList.getBrand("apple");
                long bestMatches = 0;

                for (Brand brand : BrandList.getBrands()) {
                    Log.d(TAG, "onCreate: bestBrand = " + bestBrand.getBrandName());
                    Log.d(TAG, "onCreate: bestMatches = " + bestMatches);
                    long matches =
                            mat.getMatchesWith(RefDescriptors.getDescriptors(brand.getBrandName()));
                    Log.d(TAG, "onCreate: brand = " + brand.getBrandName());
                    Log.d(TAG, "onCreate: matches = " + matches);
                    if (matches > bestMatches) {
                        bestMatches = matches;
                        bestBrand = brand;
                    }
                }

                mBrand = bestBrand;
            }
        }.run();
    }

}
