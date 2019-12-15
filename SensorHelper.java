package com.example.gpsdemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHelper implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mLinearAcc;

    public SensorHelper(final Activity act) {
        mSensorManager = (SensorManager)act.getSystemService(Activity.SENSOR_SERVICE);
        mLinearAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void onResume() {
        mSensorManager.registerListener(this, mLinearAcc, SensorManager.SENSOR_DELAY_UI);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int i) {

    }
}
