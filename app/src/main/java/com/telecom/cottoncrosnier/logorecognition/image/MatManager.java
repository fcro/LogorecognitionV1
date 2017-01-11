package com.telecom.cottoncrosnier.logorecognition.image;

import android.util.Log;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_nonfree.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.NORM_L2;
import static org.bytedeco.javacpp.opencv_features2d.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * Class regroupant les methodes pour le traitement des matrices
 */
public class MatManager {

    private static final String TAG = MatManager.class.getSimpleName();

    private static final int N_FEATURES = 0; // nbr meilleures caracteristiques a retenir
    private static final int N_OCTAVE_LAYERS = 3; // nbr couches dans chaque octave
    private static final double CONTRAST_THRESHOLD = 0.04   ; // seuil de contraste pour filtrer les caracteristiques faibles en regions semi-uniformes
    private static final double EDGE_THRESHOLD = 10; // seuil de filtrage des caracteristiques des pointes
    private static final double SIGMA = 1.6; // sigma de la gaussienne (reduire si l'image est de faible resolution)

    private Mat mMat;
    private SIFT mSift;
    private Mat mDescriptor;

    /**
     * Constructeur, crée le sift et appel le calcul du descripteur
     * @param path chemin de l'image a traiter
     */
    public MatManager(String path) {
        Log.d(TAG, "MatManager() called with: path = [" + path + "]");
        this.mMat = imread(path);
        this.mSift = new SIFT(N_FEATURES, N_OCTAVE_LAYERS, CONTRAST_THRESHOLD, EDGE_THRESHOLD, SIGMA);
        this.mDescriptor = computeDescriptor();
    }

    /**
     * Calcul les keypoints d'une matrice
     * @param mat Matrice de la photo a traiter
     * @return keypoints de la matrice
     */
    private KeyPoint getKeypoints(Mat mat) {
        KeyPoint keypoints = new KeyPoint();

        Loader.load(opencv_calib3d.class);
        mSift.detect(mat, keypoints);

        Log.d(TAG, "getKeypoints:: keypoints size = " + keypoints.size());
        return keypoints;
    }

    /**
     * Calcul le descripteur
     * @return descripteur de la matrice
     */
    private Mat computeDescriptor() {
        Mat descriptor = new Mat();

        mSift.compute(mMat, getKeypoints(mMat), descriptor);

        return descriptor;
    }

    /**
     * Retourne le descripteur de l'image
     * @return descripteur de la matrice
     */
    public Mat getDescriptor() {
        return mDescriptor;
    }

    /**
     * Calcul les meilleurs matchs entre une matrice et la matrice de reference
     * @param otherDescriptor mat d'une image de reference
     * @return meilleurs matchs
     */
    public DMatchVectorVector getMatchesWith(Mat otherDescriptor) {
        DMatchVectorVector matches = new DMatchVectorVector();

        BFMatcher matcher = new BFMatcher(NORM_L2, false);
        matcher.knnMatch(mDescriptor, otherDescriptor, matches, 2);

        return ImageUtils.refineMatches(matches);
    }

    /**
     * Calcul le meilleurs match entre l'image de reference et les marques
     * @param otherDescriptors List de Mat des images de references
     * @return meilleurs match
     */
    public long getMatchesWith(List<Mat> otherDescriptors) {
        List<DMatchVectorVector> matchesArrayList = new ArrayList<DMatchVectorVector>();

        for (Mat descriptor : otherDescriptors) {
            matchesArrayList.add(getMatchesWith(descriptor));
        }

        return ImageUtils.getBestMatch(matchesArrayList);
    }
}
