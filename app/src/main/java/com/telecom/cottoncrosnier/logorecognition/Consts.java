package com.telecom.cottoncrosnier.logorecognition;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matthieu on 27/10/16.
 */

public class Consts {

    public final static String brand[] = {
            "facebook",
            "pimkie",
            "orange",
            "telecom"
    };

    public final static String webSiteBrand[] = {
            "https://www.facebook.com",
            "https://www.pimkie.fr",
            "https://www.orange.fr/portail",
            "http://www.telecom-lille.fr"
    };

    public final static String cityName[] = {
            "Paris",
            "New York",
            "Londres",
            "Sydney",
            "Tokyo",
            "Current position"
    };

    public final static LatLng cityPosition[] = {
            new LatLng(48.853, 2.35),//paris
            new LatLng(40.7127837, -74.00594130000002),//new york
            new LatLng(51.5073509, 0.12775829999998223),//londres
            new LatLng(-33.86563, 151.0236),//sydney
            new LatLng(35.7090259, 139.73199249999993),//tokyo

    };

}
