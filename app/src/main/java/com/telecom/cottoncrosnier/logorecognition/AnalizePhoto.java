package com.telecom.cottoncrosnier.logorecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhoto extends Activity {

    private final static String TAG = AnalizePhoto.class.getSimpleName();

    private static final int INVALID_POSITION = -1;
    private ListView mListView;
    private ArrayAdapter<String> mCityNameAdapter;
    private int mId;
    private Geolocation mGeo;
    private Button mButton;
    private boolean mIsLocated;
    private Timer mTimer;
    private LatLng mLatLng;
    private Address mAddress;
    private Brand mBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);
        Log.d(TAG, "onCreate:");

        if (Utils.requestPermission(this, 0,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mLatLng = null;
            mGeo = new Geolocation(this);
            mGeo.startGeo();
        }

        mBrand = BrandList.getBrand("pimkie");
        Log.d(TAG, "brand = " + mBrand);

        mIsLocated = false;
        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);
        mTimer = checkIfIsLocated(); // regarde si on est localisé

        initListView();
        initButton(imgPath);

        new ExifAddressTask(this, new ExifResultListener()).execute(imgPath);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Utils.permissionGranted(requestCode, 0, grantResults)) {
            Log.d(TAG, "onRequestPermissionsResult: permissionGranted");
            mGeo = new Geolocation(this);
            mGeo.startGeo();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mGeo.stopGeo();
    }

    private void initButton(final Uri imgPath) {
        final EditText editText = (EditText) findViewById(R.id.text_description);
        mButton = (Button) findViewById(R.id.button_description_ok);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();

                if (mId == INVALID_POSITION) {
                    toast("Please select a position");
                    return;
                }

                if (editText.getText().toString().equals("")) {
                    toast("Please enter a description");
                    return;
                }


                resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                resultIntent.putExtra(MainActivity.KEY_PHOTO_BRAND, mBrand);

                if (mId == mCityNameAdapter.getCount() - 1) {
                    // dernier item = localisation GPS
                    if (mIsLocated) {
                        resultIntent.putExtra(MainActivity.KEY_PHOTO_COORDINATES, mGeo.getmLatLng());
                    } else {
                        toast("please wait for localisation");
                        return;
                    }

                } else {
                    if (mId < Consts.cityPosition.length) {
                        resultIntent.putExtra(MainActivity.KEY_PHOTO_COORDINATES, Consts.cityPosition[mId]);
                    } else {
                        resultIntent.putExtra(MainActivity.KEY_PHOTO_COORDINATES, mLatLng);
                    }
                }

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void initListView() {
        mId = INVALID_POSITION;
        mListView = (ListView) findViewById(R.id.listView);
        mCityNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);
        for (String s : Consts.cityName) {
            mCityNameAdapter.add(s);
        }
        mListView.setAdapter(mCityNameAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mId = i;
                if (mId == mCityNameAdapter.getCount()-1 && !mIsLocated) {
                    mButton.setText(R.string.wait_localisation);
                } else {
                    mButton.setText(R.string.ok_Button);
                }
                Log.d(TAG, "onItemClick: item = " + mCityNameAdapter.getItem(i));
            }
        });
    }

    private void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_SHORT).show();
    }

    /*
    Regarde toutes les 500ms si on est localiser dans un thread different
    une fois localisé, on arrete ce thread
     */
    private Timer checkIfIsLocated() {
        Log.d(TAG, "checkIfIsLocated: ");
        Timer timer = new Timer();
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mGeo.getmLatLng() != null) {
                        Log.d(TAG, "run: ");
                        setIsLocated();
                    }
                }
            }, 0, 500);
        } catch (Exception e) {
            //chaussure
        }

        return timer;
    }

    private void setIsLocated() {
        Log.d(TAG, "setIsLocated: ");

        Message msg = mHandlerMessage.obtainMessage();
        msg.arg1 = 0;
        mHandlerMessage.sendMessage(msg);
    }

    // sert a changer le texte une fois le check réalisé
    protected final Handler mHandlerMessage = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                Log.d(TAG, "handleMessage: ");
                mButton.setText(R.string.ok_Button);
                mIsLocated = true;
                mTimer.cancel();
            }
        }
    };





    private class ExifResultListener {
        private void onResultSucceeded(Address address) {
            if (address != null) {
                mAddress = address;
                mLatLng = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
                mCityNameAdapter.add(mAddress.getLocality());
            }
        }
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

            if (mLatLng != null) {
                mAddress = getAddressFromLatLng();
            }
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
                Log.d(TAG, "latlng = " + d_latLng[0] + " , " + d_latLng[1]);

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
                addresses = gcd.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
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
}
