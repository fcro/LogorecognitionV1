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
import com.telecom.cottoncrosnier.logorecognition.image.ImageUtils;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;
import com.telecom.cottoncrosnier.logorecognition.service.AnalyzePhotoService;

/**
 * Activité gerant l'analyse des photos
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

    /**
     * Appelée au demarrage de l'activité, initialise l'affichage et appelle l'analyse de la photo
     * @param savedInstanceState Elements sauvegardés lors du dernier arret de l'activité (non utilisé)
     */
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
                this, getString(R.string.progress_analyzing), getString(R.string.progress_wait));

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

                if (mBrand != null) {
                    resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                    resultIntent.putExtra(MainActivity.KEY_PHOTO_BRAND, mBrand);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_CANCELED, resultIntent);
                }

                finish();
            }
        });

        Intent analyzeIntent = new Intent(this, AnalyzePhotoService.class);
        analyzeIntent.setData(imgPath);
        startService(analyzeIntent);
    }

    /**
     * Affiche les resultats
     * @param brand marque a afficher
     */
    private void displayResult(Brand brand) {
        Log.d(TAG, "displayResult:: brand = " + brand);

        mBrand = brand;

        mProgressDialog.dismiss();
        mButton.setVisibility(View.VISIBLE);

        if (mBrand != null) {
            mBrandNameTextView.setText(mBrand.getBrandName());
            mBrandInfoTextView.setText(mBrand.getInfo());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = ImageUtils.scaleBitmapDown(
                    BitmapFactory.decodeFile(mBrand.getLogo(this), options),
                    300);

            mImageView.setImageBitmap(bitmap);
        } else {
            mBrandNameTextView.setText(R.string.no_result);
            mButton.setText(R.string.button_nok);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
}
