package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.Log;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fcro on 23/11/2016.
 */

public class RefDescriptors {

    private static final String TAG = RefDescriptors.class.getSimpleName();

    private static Map<String, List<Mat>> descriptors = new HashMap<String, List<Mat>>();


    public static void addDescriptor(String brandName, Mat descriptor) {
        ArrayList descriptorArrayList;

        if (!descriptors.containsKey(brandName)) {
            descriptorArrayList = new ArrayList();
            descriptorArrayList.add(descriptor);
            descriptors.put(brandName, descriptorArrayList);
        } else {
            descriptors.get(brandName).add(descriptor);
        }
    }

    public static void addDescriptors(String brandName, List<Mat> descriptorList) {
        Log.d(TAG, "addDescriptors() called with: brandName = [" + brandName + "]");
        if (!descriptors.containsKey(brandName)) {
            descriptors.put(brandName, descriptorList);
        } else {
            descriptors.get(brandName).addAll(descriptorList);
        }
    }

    public static List<Mat> getDescriptors(String brandName) {
        return descriptors.get(brandName);
    }
}
