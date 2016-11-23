package com.telecom.cottoncrosnier.logorecognition.image;

import android.util.Log;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_nonfree.*;
import org.bytedeco.javacpp.presets.opencv_core;

import static org.bytedeco.javacpp.opencv_core.NORM_L2;
import static org.bytedeco.javacpp.opencv_features2d.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * Created by fcro on 16/11/2016.
 */

public class MatManager {

    private static final String TAG = MatManager.class.getSimpleName();

    private static final int N_FEATURES = 0; // nbr meilleures caracteristiques a retenir
    private static final int N_OCTAVE_LAYERS = 3; // nbr couches dans chaque octave
    private static final double CONTRAST_THRESHOLD = 0.03; // seuil de contraste pour filtrer les caracteristiques faibles en regions semi-uniformes
    private static final int EDGE_THRESHOLD = 10; // seuil de filtrage des caracteristiques des pointes
    private static final double SIGMA = 1.6; // sigma de la gaussienne (reduire si l'image est de faible resolution)

    private Mat mMat;
    private SIFT mSift;
    private Mat mDescriptor;


    public MatManager(String path) {
        Log.d(TAG, "MatManager() called with: path = [" + path + "]");
        this.mMat = imread(path);
        this.mSift = new SIFT(N_FEATURES, N_OCTAVE_LAYERS, CONTRAST_THRESHOLD, EDGE_THRESHOLD, SIGMA);
        this.mDescriptor = computeDescriptor();
    }


    private Mat getMat() {
        return mMat;
    }

    public KeyPoint getKeypoints(Mat mat) {
        KeyPoint keypoints = new KeyPoint();

        Loader.load(opencv_calib3d.class);
        mSift.detect(mat, keypoints);

        return keypoints;
    }

    private Mat computeDescriptor() {
        Mat descriptor = new Mat();

        mSift.compute(mMat, getKeypoints(mMat), descriptor);

        return descriptor;
    }

    public Mat computeDescriptor(Mat mat, KeyPoint keypoints) {
        Mat descriptor = new Mat();

        mSift.compute(mat, keypoints, descriptor);

        return descriptor;
    }

    public Mat getDescriptor() {
        return mDescriptor;
    }

    public DMatch getMatchesWith(Mat otherDescriptor) {
        DMatch matches = new DMatch();

        BFMatcher matcher = new BFMatcher(NORM_L2, false);
        matcher.match(mDescriptor, otherDescriptor, matches);

        return matches;
    }
}
