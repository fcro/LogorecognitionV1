package com.telecom.cottoncrosnier.logorecognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_PHOTO_PATH = "key_photo";
    public static final String KEY_PHOTO_DESCRIPTION = "key_description";

    private final static int TAKE_PHOTO_REQUEST = 1;
    private static final int GALLERY_IMAGE_REQUEST = 2;
    private final static int ANALYZE_PHOTO_REQUEST = 3;
    private final static int VIEW_BROWSER_REQUEST = 4;

    private ImageView mMainImage;
    private TextView mImageDescription;
    private Button mWebSiteButton;

    private ArrayList<Photo> mArrayPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mMainImage = (ImageView) findViewById(R.id.main_image);
        mImageDescription = (TextView) findViewById(R.id.image_details_text);
        mWebSiteButton = (Button) findViewById(R.id.button_website);
        mArrayPhoto = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        Intent startTakePhoto = new Intent(MainActivity.this, TakePhotoActivity.class);
        startActivityForResult(startTakePhoto, TAKE_PHOTO_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imgPath = data.getData();
            int photoNumber = 3;
            Log.d(TAG, "onActivityResult:: imgPath = " + imgPath.toString());
            Log.d(TAG, "onActivityResult:: photoNumber = " + photoNumber);

            startAnalyze(imgPath);

        } else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult:: gallery & canceled");
            toast(getString(R.string.toast_gallery_canceled));

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Uri imgPath = b.getParcelable(KEY_PHOTO_PATH);

            Log.d(TAG, "onActivityResult:: imgPath = " + imgPath.toString());

            startAnalyze(imgPath);

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: take photo & canceled");
            toast(getString(R.string.toast_photo_canceled));

        } else if (requestCode == ANALYZE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: analyse & ok");


            Bundle b = data.getExtras();
            Uri imgPath = b.getParcelable(KEY_PHOTO_PATH);
            String description = b.getString(KEY_PHOTO_DESCRIPTION);


            try {
                Bitmap bitmap =
                        Utils.scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath),
                                3000);
                mMainImage.setImageBitmap(bitmap);
                mImageDescription.setText(description);

                mArrayPhoto.add(new Photo(bitmap, description));
                toast(getString(R.string.toast_photo_ok));

                initWebSiteButton(description);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == ANALYZE_PHOTO_REQUEST && resultCode == RESULT_CANCELED) {
            toast("analyze not ok");
        } else if (requestCode == VIEW_BROWSER_REQUEST && resultCode == RESULT_OK) {
            toast("view web site ok");
        } else if (requestCode == VIEW_BROWSER_REQUEST && resultCode == RESULT_CANCELED) {
            toast("Brand unknown");
        }
    }

    private void startAnalyze(Uri uri) {

        Intent startAnalyze = new Intent(MainActivity.this, AnalizePhoto.class);

        startAnalyze.putExtra(KEY_PHOTO_PATH, uri);

        startActivityForResult(startAnalyze, ANALYZE_PHOTO_REQUEST);
    }

    private void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_LONG).show();
    }

    private void initWebSiteButton(final String descritpion) {

        mWebSiteButton.setVisibility(View.VISIBLE);
        mWebSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startWebBrowser = new Intent(MainActivity.this, LaunchBrowserActivity.class);
                startWebBrowser.putExtra(KEY_PHOTO_DESCRIPTION, descritpion);
                startActivityForResult(startWebBrowser, VIEW_BROWSER_REQUEST);
            }
        });
    }



}
