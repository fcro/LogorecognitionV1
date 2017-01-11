package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.Log;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette classe encapsule une liste qui associe chaque marque à la List des descripteurs de ses
 * images.
 */
public class RefDescriptors {

    private static final String TAG = RefDescriptors.class.getSimpleName();

    private static Map<String, List<Mat>> descriptors = new HashMap<String, List<Mat>>();


    /**
     * <p>Ajoute un descripteur à la marque {@code brandName}.</p>
     * <p>Si la marque n'existait pas dans la List, elle est créée.</p>
     *
     * @param brandName nom de la marque.
     * @param descriptorList List des descripteurs des images de la marque.
     */
    static void addDescriptors(String brandName, List<Mat> descriptorList) {
        Log.d(TAG, "addDescriptors() called with: brandName = [" + brandName + "]");
        if (!descriptors.containsKey(brandName)) {
            descriptors.put(brandName, descriptorList);
        } else {
            descriptors.get(brandName).addAll(descriptorList);
        }
    }

    /**
     * Renvoie la List des descripteurs de la marque {@code brandName}.
     *
     * @param brandName nom de la marque.
     * @return List des descripteurs de la marque.
     */
    public static List<Mat> getDescriptors(String brandName) {
        return descriptors.get(brandName);
    }
}
