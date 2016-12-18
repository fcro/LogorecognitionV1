package com.telecom.cottoncrosnier.logorecognition.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.telecom.cottoncrosnier.logorecognition.Utils;
import com.telecom.cottoncrosnier.logorecognition.activity.MainActivity;

import org.bytedeco.javacpp.opencv_features2d.DMatchVectorVector;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();


    public static DMatchVectorVector refineMatches(DMatchVectorVector oldMatches) {
        // Ratio of Distances
        double RoD = 0.6;
        DMatchVectorVector newMatches = new DMatchVectorVector();

        // Refine results 1: Accept only those matches, where best dist is < RoD
        // of 2nd best match.
        int sz = 0;
        newMatches.resize(oldMatches.size());

        double maxDist = 0.0, minDist = Double.MAX_VALUE; // infinity

        for (int i = 0; i < oldMatches.size(); i++) {
            newMatches.resize(i, 1);
            if (oldMatches.get(i, 0).distance() < RoD
                    * oldMatches.get(i, 1).distance()) {
                newMatches.put(sz, 0, oldMatches.get(i, 0));
                sz++;
                double distance = oldMatches.get(i, 0).distance();
                if (distance < minDist)
                    minDist = distance;
                if (distance > maxDist)
                    maxDist = distance;
            }
        }
        newMatches.resize(sz);

        // Refine results 2: accept only those matches which distance is no more
        // than 3x greater than best match
        sz = 0;
        DMatchVectorVector brandNewMatches = new DMatchVectorVector();
        brandNewMatches.resize(newMatches.size());
        for (int i = 0; i < newMatches.size(); i++) {
            // TODO: Move this weights into params
            // Since minDist may be equal to 0.0, add some non-zero value
            if (newMatches.get(i, 0).distance() <= 3 * minDist) {
                brandNewMatches.resize(sz, 1);
                brandNewMatches.put(sz, 0, newMatches.get(i, 0));
                sz++;
            }
        }
        brandNewMatches.resize(sz);
        return brandNewMatches;
    }

    public static long getBestMatch(List<DMatchVectorVector> matchesList) {
        long best = 0;

        for (DMatchVectorVector matches : matchesList) {
            if (matches.size() > best) {
                best = matches.size();
            }
        }

        return best;
    }

    public static File scaleBitmapDown(Context context, Uri bitmapUri, int maxDimension) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    MainActivity.getContext().getContentResolver(), bitmapUri);
            Log.d(TAG, "scaleBitmapDown: bitmap = " + bitmap.getWidth()+ " px * " + bitmap.getHeight());

            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            int resizedWidth = maxDimension;
            int resizedHeight = maxDimension;

            if (originalHeight > originalWidth) {
                resizedHeight = maxDimension;
                resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
            } else if (originalWidth > originalHeight) {
                resizedWidth = maxDimension;
                resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
            } else if (originalHeight == originalWidth) {
                resizedHeight = maxDimension;
                resizedWidth = maxDimension;
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);

            Log.d(TAG, "scaleBitmapDown: lastpathseg = " + bitmapUri.getLastPathSegment());
            return Utils.bitmapToCache(context, bitmap, bitmapUri.getLastPathSegment());
        } catch (IOException e) {
            Log.e(TAG, "scaleBitmapDown: IOE");
            return null;
        }
    }
}
