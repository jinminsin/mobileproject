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

    public static void initGPS(final Activity act) {
        LocationManager locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged (Location location) {
                showNewLocation(act, location);
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
        // Provider
        EditText txt = (EditText) act.findViewById(R.id.editText1);
        txt.setText(location.getProvider());

        // Accuracy
        txt = (EditText) act.findViewById(R.id.editText2);
        if (location.hasAccuracy()) {
            txt.setText(" " + location.getAccuracy());
        } else {
            txt.setText("Unknown");
        }

        // Longitude
        txt = (EditText) act.findViewById(R.id.editText3);
        txt.setText(" " + location.getLongitude());

        // Latitude
        txt = (EditText) act.findViewById(R.id.editText4);
        txt.setText(" " + location.getLatitude());

        // Altitude
        txt = (EditText) act.findViewById(R.id.editText5);
        if (location.hasAltitude()) {
            txt.setText(" " + location.getAltitude());
        } else {
            txt.setText("Unknown");
        }

        // Time
        txt = (EditText) act.findViewById(R.id.editText6);
        txt.setText(" " + location.getTime());

        // Bearing
        txt = (EditText) act.findViewById(R.id.editText7);
        if (location.hasBearing()) {
            txt.setText(" " + location.getBearing());
        } else {
            txt.setText("Unknown");
        }

        // Speed
        txt = (EditText) act.findViewById(R.id.editText8);
        if (location.hasSpeed()) {
            txt.setText(" " + location.getSpeed());
        } else {
            txt.setText("Unknown");
        }

        // Extras
        txt = (EditText) act.findViewById(R.id.editText9);
        if (location.getExtras() != null) {
            txt.setText(location.getExtras().toString());
        } else {
            txt.setText("None");
        }
    } // showNewLocation
}
