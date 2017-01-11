package com.telecom.cottoncrosnier.logorecognition.reference;

import android.content.Context;
import android.util.Log;

import com.telecom.cottoncrosnier.logorecognition.activity.MainActivity;
import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.image.MatManager;

import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Représente une marque. Chaque marque est identifiée par un nom, un site web, un préfixe de
 * fichier d'image et un texte d'information.</p>
 * <p>Il peut y avoir plusieurs images pour une même marque. Elles sont nommées
 * de cette façon : [prefix]0.png , [prefix]1.png ...</p>
 */
public class Brand implements Serializable {

    private static final String TAG = Brand.class.getSimpleName();

    private final String mBrandName;
    private final URL mUrl;
    private final String mImgPrefix;
    private final String mInfo;


    /**
     * Instancie un nouvel objet Brand.
     *
     * @param brandName nom de la marque.
     * @param url site web de la marque.
     * @param imgPrefix préfixe pour identifier les images d'une marque.
     * @param info courte description de la marque.
     */
    Brand(String brandName, URL url, String imgPrefix, String info) {
        this.mBrandName = brandName;
        this.mUrl = url;
        this.mImgPrefix = imgPrefix;
        this.mInfo = info;

        RefDescriptors.addDescriptors(mBrandName, this.computeDescriptors());
    }


    /**
     * Renvoie le nom de la marque.
     *
     * @return nom de la marque.
     */
    public String getBrandName() {
        return mBrandName;
    }

    /**
     * Renvoie l'URL du site web de la marque.
     *
     * @return URL du site web.
     */
    public URL getUrl() {
        return mUrl;
    }

    /**
     * Renvoie la description de la marque.
     *
     * @return description courte.
     */
    public String getInfo() {
        return mInfo;
    }

    /**
     * Renvoie le chemin vers la première image de la marque trouvée dans les références.
     *
     * @param context contexte d'appel pour récupérer le répertoire cache.
     * @return chemin vers une image de la marque.
     */
    public String getLogo(Context context) {
        return context.getCacheDir().getAbsolutePath() + "/" + mImgPrefix + "0.png";
    }

    /**
     * Créer et renvoie les descripteurs de chaque image de la marque dans une List de Mat.
     *
     * @return liste des descripteurs de la marque.
     */
    private List<Mat> computeDescriptors() {
        final String refRoot = "reference/logos";
        List<String> refAssets;
        List<Mat> descriptors = new ArrayList<Mat>();
        MatManager mat;

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
            File file = Utils.assetToCache(MainActivity.getContext(), refRoot + "/" + refFile, refFile);
            if (file != null) {
                mat = new MatManager(file.getPath());
                descriptors.add(mat.getDescriptor());
            }
        }

        return descriptors;
    }

    /**
     * Renvoie une chaîne contenant les attributs de la marque.
     *
     * @return chaîne contenant les attributs de la marque.
     */
    public String toString() {
        return "[brandName = " + mBrandName +
                "; url = " + mUrl +
                "; imgPrefix = " + mImgPrefix +
                "; info = " + mInfo + "]";
    }
}
