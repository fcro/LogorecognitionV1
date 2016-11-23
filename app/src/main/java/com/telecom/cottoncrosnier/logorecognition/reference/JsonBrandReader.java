package com.telecom.cottoncrosnier.logorecognition.reference;

import android.util.JsonReader;

import com.telecom.cottoncrosnier.logorecognition.Activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 17/11/2016.
 */

public class JsonBrandReader {

    private JsonReader mReader;


    public JsonBrandReader(File jsonFile) throws IOException {
        mReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"));
    }


    public List<Brand> readJsonStream() throws IOException {
        try {
            return readBrandArray();
        } finally {
            mReader.close();
        }
    }

    private List<Brand> readBrandArray() throws IOException {
        List<Brand> brands = new ArrayList<Brand>();

        mReader.beginArray();
        while (mReader.hasNext()) {
            brands.add(readBrand());
        }
        mReader.endArray();

        return brands;
    }

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
