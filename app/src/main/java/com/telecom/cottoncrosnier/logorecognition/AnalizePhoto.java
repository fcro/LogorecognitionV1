package com.telecom.cottoncrosnier.logorecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

        mIsLocated = false;
        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);
        mTimer = checkIfIsLocated(); // regarde si on est localisé

        initListView();
        initButtuon(imgPath);

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

    private void initButtuon(final Uri imgPath) {
        final EditText editText = (EditText) findViewById(R.id.text_description);
        mButton = (Button) findViewById(R.id.button_description_ok);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();

                if (mId != INVALID_POSITION) {

                    if (Consts.cityName[mId].equals(Consts.cityName[5])) {// a refaire propre
                        if (mIsLocated) {
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_DESCRIPTION, editText.getText().toString());
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_POSITION, mGeo.getmLatLng());

                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            toast("please wait localisation");
                        }

                    } else {
                        if (editText.getText().toString().equals("")) {
                            setResult(RESULT_CANCELED, resultIntent);
                        } else {
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_DESCRIPTION, editText.getText().toString());
                            resultIntent.putExtra(MainActivity.KEY_PHOTO_POSITION, Consts.cityPosition[mId]);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                } else {
                    toast("please select a position");
                }

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
                if (mId == 5 && !mIsLocated) {
                    mButton.setText(R.string.wait_lacalisation);
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

        Message msg = mHandlereMessage.obtainMessage();
        msg.arg1 = 0;
        mHandlereMessage.sendMessage(msg);
    }

    // sert a changer le texte une fois le check réalisé
    protected final Handler mHandlereMessage = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                Log.d(TAG, "handleMessage: ");
                mButton.setText(R.string.ok_Button);
                mIsLocated = true;
                mTimer.cancel();
            }
        }
    };

}
