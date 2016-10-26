package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by matthieu on 24/10/16.
 */

public class AnalizePhoto extends Activity {

    private final static String TAG = AnalizePhoto.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);

        final Intent intent = getIntent();

        Bundle b = intent.getExtras();

        final Uri imgPath = b.getParcelable(MainActivity.KEY_PHOTO_PATH);

        Log.d(TAG, "onCreate: uri = " + imgPath.toString());

        final EditText editText = (EditText) findViewById(R.id.text_description);
        Button button = (Button) findViewById(R.id.button_description_ok);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();

                if (editText.getText().toString().equals("")) {
                    setResult(RESULT_CANCELED, resultIntent);
                } else {
                    resultIntent.putExtra(MainActivity.KEY_PHOTO_PATH, imgPath);
                    resultIntent.putExtra(MainActivity.KEY_PHOTO_DESCRIPTION, editText.getText().toString());
                    setResult(RESULT_OK, resultIntent);
                }
                Log.d(TAG, "onClick: text = " + editText.getText().toString());
                finish();
            }
        });

    }
}
