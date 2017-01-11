package com.telecom.cottoncrosnier.logorecognition.image;

import android.graphics.Bitmap;

import com.telecom.cottoncrosnier.logorecognition.reference.Brand;

/**
 * Classe qui décrit une photo avec une image et une marque.
 */
public class Photo {

    private Bitmap mBitmap;
    private Brand mBrand;

    /**
     * Constructeur, initialise l'image et la marque.
     * @param bitmap photo.
     * @param brand marque sur la photo.
     */
    public Photo(Bitmap bitmap, Brand brand) {
        this.mBitmap = bitmap;
        this.mBrand = brand;
    }

    /**
     * Retourne la photo.
     *
     * @return photo.
     */
    public Bitmap getBitmap(){
        return mBitmap;
    }

    /**
     * Retourne la marque sur la photo.
     *
     * @return marque.
     */
    public Brand getBrand() {
        return mBrand;
    }

    /**
     * Retourne une phrase décrivant la photo (image, marque, information de la marque).
     *
     * @return phrase pour décrire une photo.
     */
    public String toString() {
        return "[image = " + getBitmap().toString() +
                " ; mBrand = " + getBrand() +
                " ; description = " + getBrand().getInfo();
    }

}
