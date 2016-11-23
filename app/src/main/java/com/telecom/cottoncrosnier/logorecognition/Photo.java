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

    public Photo(Bitmap bitmap, Brand brand) {
        this.mBitmap = bitmap;
        this.mBrand = brand;
    }


    public Bitmap getBitmap(){
        return mBitmap;
    }

    public Brand getBrand() {
        return mBrand;
    }

    public String toString() {
        return "[image = " + getBitmap().toString() +
                " ; mBrand = " + getBrand() +
                " ; description = " + getBrand();
    }

}
