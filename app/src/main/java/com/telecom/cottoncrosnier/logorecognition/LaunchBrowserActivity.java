package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by matthieu on 27/10/16.
 */

public class LaunchBrowserActivity extends Activity {

    private static final String TAG = LaunchBrowserActivity.class.getSimpleName();

    private final static int VIEW_BROWSER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String url = b.getString(MainActivity.KEY_URL);

        launchBrowser(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_BROWSER_REQUEST) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void launchBrowser(String url) {
        Log.d(TAG, "launchBrowser() called with: url = [" + url + "]");
        toast(url);
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), VIEW_BROWSER_REQUEST);
    }

    private void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_LONG).show();
    }


}
