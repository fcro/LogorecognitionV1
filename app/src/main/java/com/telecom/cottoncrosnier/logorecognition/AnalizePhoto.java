package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;

import java.io.IOException;
import java.util.List;

/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhoto extends Activity {

    private final static String TAG = AnalizePhoto.class.getSimpleName();

    private Address mAddress;
    private Brand mBrand;
    private LatLng mLatLng;

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
        new ExifAddressTask(this, new ExifResultListener()).execute(imgPath);

        Button button = (Button) findViewById(R.id.button_description_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                resultIntent.putExtra(MainActivity.KEY_PHOTO_BRAND, mBrand);
                //resultIntent.putExtra(MainActivity.KEY_PHOTO_COORDINATES, mAddress.getLocality());
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


    private void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_SHORT).show();
    }

    private class ExifResultListener {
        private void onResultSucceeded(Address address) {
            if (address != null) {
                Log.d(TAG, "onResultSucceeded() called with: address = [" + address.toString() + "]");
                mAddress = address;

            }
        }
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


    private class ExifAddressTask extends AsyncTask<Uri, Long, Address> {

        private Context mAnalizePhotoContext;
        private ExifResultListener mListener;
        private Uri mImgPath;
        private LatLng mLatLng;
        private Address mAddress;

        private ExifAddressTask(Context context, ExifResultListener listener) {
            mAnalizePhotoContext = context;
            mListener = listener;
        }

        @Override
        protected Address doInBackground(Uri... params) {
            mImgPath = params[0];

            mLatLng = getLatLngFromExif();
            Log.d(TAG, "doInBackground: lalltng" +mLatLng);

            if (mLatLng != null) {
                mAddress = getAddressFromLatLng();
            }
            //Log.d(TAG, "doInBackground: adresse"+mAddress.getLocality());
            return mAddress;
        }

        private LatLng getLatLngFromExif() {
            double[] d_latLng = new double[2];
            LatLng latLng = null;

            String[] attributes = {MediaStore.Images.ImageColumns.LATITUDE,
                    MediaStore.Images.ImageColumns.LONGITUDE};

            Cursor cursor = mAnalizePhotoContext.getContentResolver().query(mImgPath, attributes, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                d_latLng[0] = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE));
                d_latLng[1] = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE));
                Log.d(TAG, "getLatLngFromExif: latlng = " + d_latLng[0] + " , " + d_latLng[1]);

                latLng = new LatLng(d_latLng[0], d_latLng[1]);
                cursor.close();

            } else {
                Log.d(TAG, "cursor null");
            }

            return latLng;
        }

        private Address getAddressFromLatLng() {
            Geocoder gcd;
            List<Address> addresses;
            Address address = null;

            try {
                gcd = new Geocoder(mAnalizePhotoContext);
                addresses = gcd.getFromLocation(mLatLng.getLat(), mLatLng.getLng(), 1);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0);
                    Log.d(TAG, "getLatLngFromExif: address = " + address.getLocality()
                            + ", " + address.getCountryName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return address;
        }

        @Override
        protected void onPostExecute(Address address) {
            mListener.onResultSucceeded(address);
        }
    }

    private class LatLng{

        private double lat;
        private double lng;

        public LatLng(double lat, double lng){
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
