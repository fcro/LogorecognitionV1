package com.telecom.cottoncrosnier.logorecognition.reference;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by User on 17/11/2016.
 */

public class Brand implements Serializable {

    private final String mBrandName;
    private final URL mUrl;
    private final List<String> mImgPrefix;
    private final String mInfo;


    public Brand(String brandName, URL url, List<String> imgPrefix, String info) {
        this.mBrandName = brandName;
        this.mUrl = url;
        this.mImgPrefix = imgPrefix;
        this.mInfo = info;
    }


    public String getBrandName() {
        return mBrandName;
    }

    public URL getUrl() {
        return mUrl;
    }

    public List<String> getImgPrefix() {
        return mImgPrefix;
    }

    public String getInfo() {
        return mInfo;
    }

    public String toString() {
        return "[brandName = " + mBrandName +
                "; url = " + mUrl +
                "; imgPrefix = " + mImgPrefix +
                "; info = " + mInfo;
    }
}
