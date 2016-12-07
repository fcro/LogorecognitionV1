package com.telecom.cottoncrosnier.logorecognition;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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


    private static final String TAG = ArrayAdapter.class.getSimpleName();


    private Context context;
    private int layoutResourceId;
    private ArrayList<Photo> photoArrayList;

    private int selectedRow;

    public PhotoArrayAdapter(Context context, int layoutResourceId,
                             ArrayList<Photo> photoArrayList) {
        super(context, layoutResourceId, photoArrayList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.photoArrayList = photoArrayList;
        this.selectedRow = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PhotoHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PhotoHolder();
            holder.photoImage = (ImageView) row.findViewById(R.id.photo_image);
            holder.photoBrandName = (TextView) row.findViewById(R.id.photo_brandname);
            holder.photoBrandInfo = (TextView) row.findViewById(R.id.photo_brandinfo);

            row.setTag(holder);
        } else {
            if (position == selectedRow) {
                row.setBackgroundResource(R.drawable.rounded_bg);
            } else {
                row.setBackgroundColor(Color.TRANSPARENT);
            }

            holder = (PhotoHolder) row.getTag();
        }

        Photo photo = photoArrayList.get(position);
        holder.photoImage.setImageBitmap(photo.getBitmap());
        holder.photoBrandName.setText(photo.getBrand().getBrandName());
        holder.photoBrandInfo.setText(photo.getBrand().getInfo());

        return row;
    }

    private static class PhotoHolder {
        ImageView photoImage;
        TextView photoBrandName;
        TextView photoBrandInfo;
    }


    public void selectRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }
}