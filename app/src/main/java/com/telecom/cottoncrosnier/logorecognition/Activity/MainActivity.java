package com.telecom.cottoncrosnier.logorecognition.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.telecom.cottoncrosnier.logorecognition.image.Photo;
import com.telecom.cottoncrosnier.logorecognition.activity.adapter.PhotoArrayAdapter;
import com.telecom.cottoncrosnier.logorecognition.R;
import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.reference.BrandList;
import com.telecom.cottoncrosnier.logorecognition.reference.JsonBrandReader;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_PHOTO_PATH = "key_path";
    public static final String KEY_PHOTO_BRAND = "key_brand";
    public static final String KEY_URL = "key_url";

    private static final int TAKE_PHOTO_REQUEST = 1;
    private static final int GALLERY_IMAGE_REQUEST = 2;
    private static final int ANALYZE_PHOTO_REQUEST = 3;
    private static final int VIEW_BROWSER_REQUEST = 4;

    private static final int INVALID_POSITION = -1;

    private static Context context;

    private ArrayList<Photo> mArrayPhoto;
    private ArrayAdapter<Photo> mPhotoAdapter;
    private ListView mPhotoListView;

    private ProgressDialog mProgressDialog;

    private int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BrandList.setBrands(new JsonBrandReader(
                            Utils.assetToCache(context, "reference/metadata/brands.json", "brands.json"))
                            .readJsonStream());

                    showButton();
                } catch (IOException e) {} finally {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        mProgressDialog = ProgressDialog.show(
                this, getString(R.string.progress_initializing), getString(R.string.wait));

        mArrayPhoto = new ArrayList<>();
        mPhotoAdapter = new PhotoArrayAdapter(
                this, R.layout.listview_row, mArrayPhoto);

        initListView();
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
        Log.d(TAG, "onOptionsItemSelected: id = " + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.view_website) {
            startBrowser();
            return true;
        } else if (id == R.id.delete_photo) {
            deletePhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imgPath = data.getData();
            Log.d(TAG, "onActivityResult:: imgPath = " + imgPath.toString());

            startAnalyze(Uri.fromFile(Utils.galleryToCache(context, imgPath, imgPath.getFragment())));

        } else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult:: gallery & canceled");
            Utils.toast(this,getString(R.string.toast_gallery_canceled));

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Uri imgPath = b.getParcelable(KEY_PHOTO_PATH);

            Log.d(TAG, "onActivityResult:: imgPath = " + imgPath.toString());

            startAnalyze(imgPath);

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: take photo & canceled");
            Utils.toast(this,getString(R.string.toast_photo_canceled));

        } else if (requestCode == ANALYZE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: analyse & ok");


            Bundle b = data.getExtras();
            Uri imgPath = b.getParcelable(KEY_PHOTO_PATH);
            Brand brand = (Brand) b.getSerializable(KEY_PHOTO_BRAND);
            Log.d(TAG, "brand = " + brand.toString());
            try {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                        MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath),
                        300,
                        300);

                addImage(new Photo(bitmap, brand));
                Utils.toast(this,getString(R.string.toast_photo_ok));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == ANALYZE_PHOTO_REQUEST && resultCode == RESULT_CANCELED) {
            Utils.toast(this, "analyze not ok");
        } else if (requestCode == VIEW_BROWSER_REQUEST && resultCode == RESULT_OK) {
//            toast("view web site ok");
            Log.d(TAG, "onActivityResult: view web site ok");
        } else if (requestCode == VIEW_BROWSER_REQUEST && resultCode == RESULT_CANCELED) {
            Utils.toast(this,"Brand unknown");
        }
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


    private void startAnalyze(Uri uri) {
        Intent startAnalyze = new Intent(MainActivity.this, AnalizePhotoActivity.class);
        startAnalyze.putExtra(KEY_PHOTO_PATH, uri);
        startActivityForResult(startAnalyze, ANALYZE_PHOTO_REQUEST);
    }

    private void startBrowser() {
        Log.d(TAG, "onOptionsItemSelected: view website");
        if (mId != INVALID_POSITION) {
            Intent startWebBrowser = new Intent(MainActivity.this, LaunchBrowserActivity.class);
            startWebBrowser.putExtra(KEY_URL, mPhotoAdapter.getItem(mId).getBrand().getUrl().toString());
            startActivityForResult(startWebBrowser, VIEW_BROWSER_REQUEST);
        } else {
            Utils.toast(this,"please select an item");
        }
    }

    private void addImage(Photo photo) {
        Log.d(TAG, "addImage() called with: photo = [" + photo.toString() + "]");
        mPhotoAdapter.add(photo);
    }

    private void initListView() {
        mId = INVALID_POSITION;
        mPhotoListView = (ListView) findViewById(R.id.img_list_view);
        mPhotoListView.setAdapter(mPhotoAdapter);
        mPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position = [" + position + "] view = [" +
                        view.getClass().getCanonicalName() + "]");
                mId = position;
                ((PhotoArrayAdapter) mPhotoAdapter).selectRow(mId);
                mPhotoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initFloatingButton() {
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

        fab.setVisibility(View.VISIBLE);
    }

    private void deletePhoto() {
        if (mId == INVALID_POSITION) {
            Utils.toast(this,"please select an item");
            return;
        }

        mPhotoAdapter.remove(mArrayPhoto.get(mId));

        final int arrayAdapterSize = mArrayPhoto.size();
        if (arrayAdapterSize <= mId) {
            if (arrayAdapterSize == 0) {
                mId = INVALID_POSITION;
            } else {
                mId = arrayAdapterSize - 1;
                mPhotoListView.performItemClick(
                        mPhotoListView.getChildAt(mId),
                        mId,
                        mPhotoListView.getItemIdAtPosition(mId));
            }
        }
    }

    private void showButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                // close progressdialog

                initFloatingButton();
                // init + show floatingActionButton
            }
        });
    }

    public static Context getContext() {
        return context;
    }
}



