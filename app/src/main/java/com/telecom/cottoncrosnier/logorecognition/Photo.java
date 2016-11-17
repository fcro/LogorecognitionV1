package com.telecom.cottoncrosnier.logorecognition;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.telecom.cottoncrosnier.logorecognition.reference.Brand;

/**
 * Created by matthieu on 26/10/16.
 */

public class Photo {

    private Bitmap mBitmap;
    private Brand mBrand;
    private LatLng mCoordinates;


    public Photo(Bitmap bitmap, Brand brand, LatLng coordinates) {
        this.mBitmap = bitmap;
        this.mBrand = brand;
        this.mCoordinates = coordinates;
    }


    public Bitmap getBitmap(){
        return mBitmap;
    }

    public Brand getBrand() {
        return mBrand;
    }

    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public String toString() {
        return "[image = " + getBitmap().toString() +
                " ; mBrand = " + getBrand() +
                " ; description = " + getBrand() +
                " ; coordinates = " + getCoordinates().toString() + "]";
    }

}
