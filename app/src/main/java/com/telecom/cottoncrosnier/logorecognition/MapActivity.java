package com.telecom.cottoncrosnier.logorecognition;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by matthieu on 28/10/16.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final float HUE_IABLUE = 200.0f;
    LatLng latLng = new LatLng(48.756242, -3.453329);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MapActivity", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button button = (Button) findViewById(R.id.buttonPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(48.756242, -3.453329);

        mMap.addMarker(new MarkerOptions().position(latLng).title("test"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
