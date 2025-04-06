package com.as.eventalertandroid.handler;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.as.eventalertandroid.R;

import java.io.IOException;
import java.util.List;

public class LocationHandler {

    public static String getDistance(Context context, double distance) {
        String format;
        long d;
        if (distance > 1) {
            d = Math.round(distance);
            format = context.getString(R.string.distance_km_away_from_user);
        } else {
            d = Math.round(distance * 1000);
            format = context.getString(R.string.distance_m_away_from_user);
        }
        return String.format(format, d);
    }

    public static String getAddress(Geocoder geocoder, double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            return "";
        }
        return "";
    }

}
