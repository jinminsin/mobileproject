package com.example.gpsdemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.EditText;

import java.util.Vector;

public class SensorHelper implements SensorEventListener {
    private static final int AVG_NUM = 5;

    private final SensorManager mSensorManager;
    private final Sensor mLinearAcc;
    private final Activity act;
    private double[] speed = {0.0, 0.0, 0.0};
    private double[] tickX = new double[AVG_NUM];
    private double[] tickY = new double[AVG_NUM];
    private double[] tickZ = new double[AVG_NUM];
    private long timestamp = 0;
    public double dist;

    public SensorHelper(final Activity act) {
        this.act = act;
        mSensorManager = (SensorManager)act.getSystemService(Activity.SENSOR_SERVICE);
        mLinearAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void onResume() {
        mSensorManager.registerListener(this, mLinearAcc, SensorManager.SENSOR_DELAY_UI);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    private double getDist(double[] dist) {
        return Math.sqrt(Math.pow(dist[0], 2) + Math.pow(dist[1], 2) + Math.pow(dist[2], 2));
    }

    public void onSecond(SensorEvent sensorEvent) {
        EditText txt;

        for (int i = 1; i < AVG_NUM; i++) {
            tickX[i] = tickX[i-1];
            tickY[i] = tickY[i-1];
            tickZ[i] = tickZ[i-1];
        }
        tickX[AVG_NUM-1] = speed[0];
        tickY[AVG_NUM-1] = speed[1];
        tickZ[AVG_NUM-1] = speed[2];
        double[] speedSum = {0.0, 0.0, 0.0};
        for (int i = 0; i < AVG_NUM; i++) {
            speedSum[0] += tickX[i];
            speedSum[1] += tickY[i];
            speedSum[2] += tickZ[i];
        }
        speedSum[0] /= AVG_NUM;
        speedSum[1] /= AVG_NUM;
        speedSum[2] /= AVG_NUM;

        // X
        txt = (EditText) act.findViewById(R.id.editText5);
        txt.setText(" " + (speed[0] / 100.0));

        // Y
        txt = (EditText) act.findViewById(R.id.editText6);
        txt.setText(" " + (speed[1] / 100.0));

        // Z
        txt = (EditText) act.findViewById(R.id.editText7);
        txt.setText(" " + (speed[2] / 100.0));

        // TIME
        dist = getDist(speedSum) * 3.6;
        txt = (EditText) act.findViewById(R.id.editText8);
        txt.setText(" " + dist);

        speed[0] = 0;
        speed[1] = 0;
        speed[2] = 0;
        timestamp = sensorEvent.timestamp;
    }

    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {
        if (sensorEvent.timestamp - timestamp >= 100000000L) onSecond(sensorEvent);
        speed[0] += sensorEvent.values[0];
        speed[1] += sensorEvent.values[1];
        speed[2] += sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int i) {

    }
}
