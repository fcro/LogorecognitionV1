package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.Log;

import com.telecom.cottoncrosnier.logorecognition.Activity.MainActivity;
import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.image.MatManager;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by User on 17/11/2016.
 */

public class Brand implements Serializable {

    private final String mBrandName;
    private final URL mUrl;
    private final String mImgPrefix;
    private final String mInfo;


    public Brand(String brandName, URL url, String imgPrefix, String info) {
        this.mBrandName = brandName;
        this.mUrl = url;
        this.mImgPrefix = imgPrefix;
        this.mInfo = info;

        RefDescriptors.addDescriptor(mBrandName, this.computeDescriptor());
    }

    public Brand() {
        this.mBrandName = null;
        this.mUrl = null;
        this.mImgPrefix = null;
        this.mInfo = null;
    }


    public String getBrandName() {
        return mBrandName;
    }

    public URL getUrl() {
        return mUrl;
    }

    public String getImgPrefix() {
        return mImgPrefix;
    }

    public String getInfo() {
        return mInfo;
    }

    private Mat computeDescriptor() {
        Log.d("new brand", "computeDescriptor: " + mImgPrefix);
        MatManager mat = new MatManager(
                Utils.assetToCache(MainActivity.getContext(), "reference/logos/" + mImgPrefix + "0.png", mImgPrefix + ".png").getPath());

        return mat.getDescriptor();
    }

    public String toString() {
        return "[brandName = " + mBrandName +
                "; url = " + mUrl +
                "; imgPrefix = " + mImgPrefix +
                "; info = " + mInfo;
    }
}
