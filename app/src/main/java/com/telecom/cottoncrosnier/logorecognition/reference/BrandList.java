package com.telecom.cottoncrosnier.logorecognition.reference;

import java.util.List;

/**
 * Created by fcro on 18/11/2016.
 */

public class BrandList {

    private static List<Brand> mBrands;


    public static void setBrands(List<Brand> brands) {
        mBrands = brands;
    }

    public static List<Brand> getBrands() {
        return mBrands;
    }

    public static Brand getBrand(String brandName) {
        for (int i = 0; i < mBrands.size(); ++i) {
            if (mBrands.get(i).getBrandName().toLowerCase().equals(brandName.toLowerCase()))
                return mBrands.get(i);
        }

        return null;
    }
}
