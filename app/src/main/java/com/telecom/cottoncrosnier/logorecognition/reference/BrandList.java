package com.telecom.cottoncrosnier.logorecognition.reference;

import java.util.List;

/**
 * Cette classe encapsule une List d'objets {@link Brand} initialisée au lancement de l'application.
 */
public class BrandList {

    private static List<Brand> mBrands;


    /**
     * Initialise la List des objets {@link Brand}.
     *
     * @param brands List des objets {@link Brand}.
     */
    public static void setBrands(List<Brand> brands) {
        mBrands = brands;
    }

    /**
     * Renvoie la List des objets {@link Brand}.
     *
     * @return List des objets {@link Brand}.
     */
    public static List<Brand> getBrands() {
        return mBrands;
    }
}
