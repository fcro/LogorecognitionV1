package com.telecom.cottoncrosnier.logorecognition;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matthieu on 26/10/16.
 */

public class Photo {

    private Bitmap mBitmap;
    private String mBrand;
    private String mDescription;
    private LatLng mCoordinates;


    public Photo(Bitmap bitmap, String brand, String description, LatLng coordinates) {
        this.mBitmap = bitmap;
        this.mBrand = brand;
        this.mDescription = description;
        this.mCoordinates = coordinates;
    }


    public Bitmap getBitmap(){
        return mBitmap;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getDescription(){
        return mDescription;
    }

    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public String toString() {
        return "[image  = " + getBitmap().toString() +
                " ; mBrand = " + getBrand() +
                " ; description = " + getDescription() +
                " ; coordinates = " + getCoordinates().toString() + "]";
    }

}
