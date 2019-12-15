package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final Integer APP_PERMISSION = 1;
    SensorHelper sensor;

    private void askForPermission (String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensor = new SensorHelper(this);
        setContentView(R.layout.activity_main);
    } // onCreate

    @Override
    protected void onResume () {
        super.onResume();
        sensor.onResume();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, APP_PERMISSION);

        GPSHelper.initGPS(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensor.onDestroy();
    }

}
