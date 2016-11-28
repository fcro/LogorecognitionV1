package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.Log;

import com.telecom.cottoncrosnier.logorecognition.Activity.MainActivity;
import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.image.MatManager;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 17/11/2016.
 */

public class Brand implements Serializable {

    private static final String TAG = Brand.class.getSimpleName();

    private final String mBrandName;
    private final URL mUrl;
    private final String mImgPrefix;
    private final String mInfo;


    public Brand(String brandName, URL url, String imgPrefix, String info) {
        this.mBrandName = brandName;
        this.mUrl = url;
        this.mImgPrefix = imgPrefix;
        this.mInfo = info;

        RefDescriptors.addDescriptors(mBrandName, this.computeDescriptors());
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

    private List<Mat> computeDescriptors() {
        Log.d(TAG, "computeDescriptor: " + mImgPrefix);

        final String refRoot = "reference/logos";
        List<String> refAssets;
        List<Mat> descriptors = new ArrayList<Mat>();

        try {
            refAssets = Utils.filesStartWith(
                    MainActivity.getContext().getAssets().list(refRoot), mImgPrefix);
        } catch (IOException e) {
            Log.e(TAG, "computeDescriptors:: MainActivity.getContext().getAssets().list(refRoot)");
            e.printStackTrace();

            return null;
        }

        for (String refFile : refAssets) {
            Log.d(TAG, "computeDescriptors:: refFile = " + refFile);
            MatManager mat = new MatManager(
                    Utils.assetToCache(MainActivity.getContext(), refRoot + "/" + refFile, refFile).getPath());
            descriptors.add(mat.getDescriptor());
        }

        return descriptors;
    }

    public String toString() {
        return "[brandName = " + mBrandName +
                "; url = " + mUrl +
                "; imgPrefix = " + mImgPrefix +
                "; info = " + mInfo + "]";
    }
}
