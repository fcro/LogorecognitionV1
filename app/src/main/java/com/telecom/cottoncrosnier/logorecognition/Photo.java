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
    private static final int INDEX_PHOTO_NUMBER = 2;

    public Photo(Bitmap img, String description, int photoNumber){
        photo = new ArrayList<>(3);

        photo.add(INDEX_IMG, img);
        photo.add(INDEX_DESCRIPTION, description);
        photo.add(INDEX_PHOTO_NUMBER, photoNumber);
    }

    public Bitmap getImage(){
        return (Bitmap) photo.get(INDEX_IMG);
    }

    public String getDescription(){
        return (String) photo.get(INDEX_DESCRIPTION);
    }

    public int getImgNumber(){
        return (int) photo.get(INDEX_PHOTO_NUMBER);
    }
}
