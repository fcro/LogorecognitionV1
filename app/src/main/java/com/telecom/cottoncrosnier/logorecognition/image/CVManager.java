package com.telecom.cottoncrosnier.logorecognition.image;

import android.content.Context;
import android.content.res.AssetManager;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_nonfree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.bytedeco.javacpp.opencv_core.NORM_L2;
import static org.bytedeco.javacpp.opencv_features2d.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * Cette classe sert juste à implémenter quelques appels de JavaCV pour l'instant
 * Il faudra la refactorer suivant comment on veut l'appeler, stocker les résultats, etc. (limite la scinder en plusieurs classes)
 *
 * Created by fcro on 16/11/2016.
 */

public class CVManager {

    private static final int N_FEATURES = 0; // nbr meilleures caracteristiques a retenir
    private static final int N_OCTAVE_LAYERS = 3; // nbr couches dans chaque octave
    private static final double CONTRAST_THRESHOLD = 0.03; // seuil de contraste pour filtrer les caracteristiques faibles en regions semi-uniformes
    private static final int EDGE_THRESHOLD = 10; // seuil de filtrage des caracteristiques des pointes
    private static final double SIGMA = 1.6; // sigma de la gaussienne (reduire si l'image est de faible resolution)

    private Context mContext;
    private Mat mMat;
    private Mat mDescriptor;
    private SIFT mSift;
    private KeyPoint mKeyPoints;
    private DMatch mMatches;


    public CVManager(Context context) {
        this.mContext = context;
        this.mMat = new Mat();
        this.mDescriptor = new Mat();
        this.mSift = new SIFT(N_FEATURES, N_OCTAVE_LAYERS, CONTRAST_THRESHOLD, EDGE_THRESHOLD, SIGMA);
        this.mKeyPoints = new KeyPoint();
        this.mMatches = new DMatch();
    }


    public KeyPoint getKeypoints() {
        Loader.load(opencv_calib3d.class);
        mSift.detect(mMat, mKeyPoints);

        return mKeyPoints;
    }

    public Mat getDescriptor() {
        mSift.compute(mMat, mKeyPoints, mDescriptor);

        return mDescriptor;
    }

    public DMatch getMatchesWith(Mat descriptor) {
        BFMatcher matcher = new BFMatcher(NORM_L2, false);
        matcher.match(mDescriptor, descriptor, mMatches);

        return mMatches;
    }

    public Mat getMat(File file) {
        mMat = imread(file.getAbsolutePath());

        return mMat;
    }

    public void assetToFile(String assetPath, File file) {
        InputStream is;
        FileOutputStream fos;
        int size;
        byte[] buffer;
        AssetManager assetManager = mContext.getAssets();

        try {
            is = assetManager.open(assetPath);
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();

            fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}