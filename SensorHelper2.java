package com.example.gpsdemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.EditText;

import java.util.Vector;

public class SensorHelper2 implements SensorEventListener {

    private final SensorManager mSensorManager;
    private final Sensor mGyroscope;
    private final Activity act;
    private long timestamp = 0;
    public boolean turning = false;
    private static final float TURNING_SENSITIVITY = 1.5f;

    public SensorHelper2(final Activity act) {
        this.act = act;
        mSensorManager = (SensorManager)act.getSystemService(Activity.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void onResume() {
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    private double getDist(float[] dist) {
        return Math.sqrt(Math.pow(dist[0], 2) + Math.pow(dist[1], 2) + Math.pow(dist[2], 2));
    }

    public void onSecond(SensorEvent sensorEvent) {
        EditText txt;

        if (getDist(sensorEvent.values) > TURNING_SENSITIVITY) turning = true;
        else turning = false;

        // TIME
        txt = (EditText) act.findViewById(R.id.editText9);
        txt.setText(" " + (turning ? "True" : "False"));

        // Final
        txt = (EditText) act.findViewById(R.id.editText0);
        txt.setText(" " + ((MainActivity)act).voting());

        timestamp = sensorEvent.timestamp;
    }

    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {
        if (sensorEvent.timestamp - timestamp >= 100000000L) onSecond(sensorEvent);
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int i) {

    }
}
