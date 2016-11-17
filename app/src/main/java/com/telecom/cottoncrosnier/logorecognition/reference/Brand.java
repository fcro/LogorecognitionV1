package com.telecom.cottoncrosnier.logorecognition.reference;

import java.net.URL;
import java.util.List;

/**
 * Created by User on 17/11/2016.
 */

public final class Brand {

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


    public String getmBrandName() {
        return mBrandName;
    }

    public URL getmUrl() {
        return mUrl;
    }

    public List<String> getmImgPrefix() {
        return mImgPrefix;
    }

    public String getmInfo() {
        return mInfo;
    }
}
