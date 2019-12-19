package com.example.healthyapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class sensor implements SensorEventListener {
    private static final int AVG_NUM = 5;
    private float[] speed = {0, 0, 0};
    private float[] tickX = new float[AVG_NUM];
    private float[] tickY = new float[AVG_NUM];
    private float[] tickZ = new float[AVG_NUM];

    public boolean turning = false;
    private static final float TURNING_SENSITIVITY = 1.5f;

    private final Sensor mLinearAcc;
    private final Sensor mGyroscope;

    private long gyroTimeStamp;
    private long accTimeStamp;

    private float distance;
    private int step=0;

    public sensor(SensorManager SensorManager) {
        mLinearAcc = SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void onResume(SensorManager SensorManager)
    {
        SensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
        SensorManager.registerListener(this, mLinearAcc, SensorManager.SENSOR_DELAY_UI);
    }

    public void onDestroy(SensorManager SensorManager) {
        SensorManager.unregisterListener(this);
    }

    public float getDist(float[] dist) {
        return (float)Math.sqrt(Math.pow(dist[0], 2) + Math.pow(dist[1], 2) + Math.pow(dist[2], 2));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (sensorEvent.timestamp - gyroTimeStamp > 100000000L) sensorAct(sensorEvent);
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if(sensorEvent.timestamp - accTimeStamp > 100000000L) sensorAct(sensorEvent);
            speed[0] += sensorEvent.values[0];
            speed[1] += sensorEvent.values[1];
            speed[2] += sensorEvent.values[2];

            if(distance > 4 && distance < 14 && step < 3) step++;// 만보기 - 자전거 이상은 제외할 수 있도록, 보통 사람이라면 1초에 3발자국 정도 걷는다고 가정
        }
    }

    public void sensorAct(SensorEvent sensorEvent)
    {
        switch(sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                speed[0]/=(AVG_NUM*2);
                speed[1]/=(AVG_NUM*2);
                speed[2]/=(AVG_NUM*2);

                for (int i = 1; i < AVG_NUM; i++) {
                    tickX[i] = tickX[i-1];
                    tickY[i] = tickY[i-1];
                    tickZ[i] = tickZ[i-1];
                }
                tickX[AVG_NUM-1] = speed[0];
                tickY[AVG_NUM-1] = speed[1];
                tickZ[AVG_NUM-1] = speed[2];

                float[] speedSum = {0, 0, 0};
                for (int i = 0; i < AVG_NUM; i++) {
                    speedSum[0] += tickX[i];
                    speedSum[1] += tickY[i];
                    speedSum[2] += tickZ[i];
                }
                speedSum[0] /= AVG_NUM;
                speedSum[1] /= AVG_NUM;
                speedSum[2] /= AVG_NUM;

                speed[0]=0;
                speed[1]=0;
                speed[2]=0;

                distance = getDist(speedSum)*3.6f;
                accTimeStamp = sensorEvent.timestamp;
                break;
            case Sensor.TYPE_GYROSCOPE:

                if (getDist(sensorEvent.values) > TURNING_SENSITIVITY) turning = true;
                else turning = false;

                gyroTimeStamp = sensorEvent.timestamp;
                break;
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
