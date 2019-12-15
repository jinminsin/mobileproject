package com.example.gpsdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;

public class GPSHelper {

    private static Location prevLocation = new Location("starting_point");
    public static float distanceGPS = 0;

    public static void initGPS(final Activity act) {
        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged (Location location) {
                distanceGPS = location.distanceTo(prevLocation) * 3.6f;
                showNewLocation(act, location);
                prevLocation = location;
            }

            @Override
            public void onStatusChanged (String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled (String provider) {
            }

            @Override
            public void onProviderDisabled (String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public static void showNewLocation(Activity act, Location location) { // ver 1
        EditText txt;

        // Longitude
        txt = (EditText) act.findViewById(R.id.editText1);
        txt.setText(" " + location.getLongitude());

        // Latitude
        txt = (EditText) act.findViewById(R.id.editText2);
        txt.setText(" " + location.getLatitude());

        // Altitude
        txt = (EditText) act.findViewById(R.id.editText3);
        if (location.hasAltitude()) {
            txt.setText(" " + location.getAltitude());
        } else {
            txt.setText("Unknown");
        }

        // Distance
        txt = (EditText) act.findViewById(R.id.editText4);
        txt.setText(" " + String.valueOf(distanceGPS));

    } // showNewLocation
}
