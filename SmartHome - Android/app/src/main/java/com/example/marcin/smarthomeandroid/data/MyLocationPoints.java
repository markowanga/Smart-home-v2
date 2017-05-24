package com.example.marcin.smarthomeandroid.data;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 15.02.2017.
 *
 * Class with points near home - used to get information that car is approach the house,
 * and the house LatLang
 */

public class MyLocationPoints {
    public static final List<Pair<LatLng, LatLng>> points = createPoints();

    public static final LatLng home = new LatLng(49.843570, 18.599820);

    private static List<Pair<LatLng, LatLng>> createPoints() {
        List<Pair<LatLng, LatLng>> mPoints = new ArrayList<>();

        mPoints.add(new Pair<>(new LatLng(49.849609, 18.592459), new LatLng(49.845242, 18.593807)));
            // od Kapliczki
        mPoints.add(new Pair<>(new LatLng(49.835956, 18.610945), new LatLng(49.840038, 18.609282)));
            // od Konopnickiej
        mPoints.add(new Pair<>(new LatLng(49.838150, 18.607676), new LatLng(49.840038, 18.609282)));
            // od Srednicowej
        mPoints.add(new Pair<>(new LatLng(49.840875, 18.598996), new LatLng(49.843231, 18.599329)));
            // od Dworkowej

        return mPoints;
    }
}