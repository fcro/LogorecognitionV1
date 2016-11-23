package com.telecom.cottoncrosnier.logorecognition.reference;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fcro on 23/11/2016.
 */

public class RefDescriptors {

    private static Map<String, Mat> descriptors = new HashMap<String, Mat>();


    public static void addDescriptor(String brandName, Mat descriptor) {
        descriptors.put(brandName, descriptor);
    }

    public static Mat getDescriptor(String brandName) {
        return descriptors.get(brandName);
    }
}
