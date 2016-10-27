package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fcro on 27/10/2016.
 */

public class PhotoArrayAdapter extends ArrayAdapter<Photo> {

    Context context;
    int layoutResourceId;
    ArrayList<Photo> photoArrayList;

    public PhotoArrayAdapter(Context context, int layoutResourceId,
                             ArrayList<Photo> photoArrayList) {
        super(context, layoutResourceId, photoArrayList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.photoArrayList = photoArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PhotoHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PhotoHolder();
            holder.photoImage = (ImageView) row.findViewById(R.id.photo_image);
            holder.photoDescription = (TextView) row.findViewById(R.id.photo_description);

            row.setTag(holder);
        } else {
            holder = (PhotoHolder) row.getTag();
        }

        Photo photo = photoArrayList.get(position);
        holder.photoImage.setImageBitmap(photo.getImage());
        holder.photoDescription.setText(photo.getDescription());

        return row;
    }

    static class PhotoHolder {
        ImageView photoImage;
        TextView photoDescription;
    }
}