package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.JsonReader;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Cette classe permet de parcourir le fichier json d'index des marques. Elle crée une List
 * d'objets {@link Brand} initialisés selon le contenu du fichier json.
 */
public class JsonBrandReader {

    private static final String TAG = JsonBrandReader.class.getSimpleName();

    private JsonReader mReader;


    /**
     * Instancie un JsonBrandReader pour lire le fichier {@code jsonFile}.
     *
     * @param jsonFile fichier d'index des marques au format json.
     * @throws IOException si {@code jsonFile} est introuvable ou si son encodage n'est pas supporté.
     */
    public JsonBrandReader(File jsonFile) throws IOException {
        mReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"));
    }


    /**
     * Parcourt le fichier et renvoie la List des objets {@link Brand} instanciés.
     *
     * @return List des objets {@link Brand} instanciés.
     * @throws IOException si {@link #readBrandArray()} a levé une exception.
     */
    public List<Brand> readJsonStream() throws IOException {
        try {
            return readBrandArray();
        } finally {
            try {
                mReader.close();
            } catch (IOException e) {
                Log.e(TAG, "readJsonStream: could not close JsonReader");
            }
        }
    }

    /**
     * Parcourt les marques du fichier et les ajoute à une List sous forme d'objets {@link Brand}.
     *
     * @return List des objets {@link Brand} instanciés.
     * @throws IOException si une erreur se produit pendant la lecture du fichier.
     */
    private List<Brand> readBrandArray() throws IOException {
        List<Brand> brands = new ArrayList<Brand>();

        mReader.beginArray();
        while (mReader.hasNext()) {
            brands.add(readBrand());
        }
        mReader.endArray();

        return brands;
    }

    /**
     * Parcourt les attributs d'une marque pour instancier un objet {@link Brand}.
     *
     * @return objet {@link Brand} défini selon le fichier json.
     * @throws IOException si une erreur se produit pendant la lecture du fichier.
     */
    private Brand readBrand() throws IOException {
        String brandName = null;
        URL url = null;
        String imgPrefix = null;
        String info = null;

        mReader.beginObject();
        while (mReader.hasNext()) {
            String name = mReader.nextName();
            if (name.equals("brandName")) {
                brandName = mReader.nextString();
            } else if (name.equals("url")) {
                url = new URL("http://" + mReader.nextString());
            } else if (name.equals("imgPrefix")) {
                imgPrefix = mReader.nextString();
            } else if (name.equals("info")) {
                info = mReader.nextString();
            } else {
                throw new IOException("Malformatted metadata file");
            }
        }
        mReader.endObject();

        return new Brand(brandName, url, imgPrefix, info);
    }
}
