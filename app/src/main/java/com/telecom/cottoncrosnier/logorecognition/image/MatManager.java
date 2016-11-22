package com.telecom.cottoncrosnier.logorecognition.image;

import android.util.Log;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_nonfree.*;

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


    public MatManager(String path) {
        Log.d(TAG, "MatManager() called with: path = [" + path + "]");
        this.mMat = imread(path);
        this.mSift = new SIFT(N_FEATURES, N_OCTAVE_LAYERS, CONTRAST_THRESHOLD, EDGE_THRESHOLD, SIGMA);
    }


    private Mat getMat() {
        return mMat;
    }

    private KeyPoint getKeypoints(Mat mat) {
        KeyPoint keypoints = new KeyPoint();

        Loader.load(opencv_calib3d.class);
        mSift.detect(mat, keypoints);

        return keypoints;
    }

    private Mat getDescriptor(Mat mat, KeyPoint keypoints) {
        Mat descriptor = new Mat();

        mSift.compute(mat, keypoints, descriptor);

        return descriptor;
    }

    public DMatch getMatchesWith(MatManager otherMat) {
        DMatch matches = new DMatch();

        Mat descriptor = getDescriptor(getMat(), getKeypoints(getMat()));
        Mat otherDescriptor =
                otherMat.getDescriptor(otherMat.getMat(), otherMat.getKeypoints(otherMat.getMat()));

        BFMatcher matcher = new BFMatcher(NORM_L2, false);
        matcher.match(descriptor, otherDescriptor, matches);

        return matches;
    }
}
