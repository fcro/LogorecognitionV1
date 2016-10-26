package com.telecom.cottoncrosnier.logorecognition;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by matthieu on 26/10/16.
 */

public class Photo {

    private ArrayList<Object> photo;

    private static final int INDEX_IMG = 0;
    private static final int INDEX_DESCRIPTION = 1;

    public Photo(Bitmap img, String description){
        photo = new ArrayList<>(3);

        photo.add(INDEX_IMG, img);
        photo.add(INDEX_DESCRIPTION, description);
    }

    public Bitmap getImage(){
        return (Bitmap) photo.get(INDEX_IMG);
    }

    public String getDescription(){
        return (String) photo.get(INDEX_DESCRIPTION);
    }

}
