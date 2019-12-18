package com.example.healthyapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BackGround extends Service {
    private final long oneDayMilies = 24*60*60*1000;//하루 시간
    private boolean play = true;
    private DBHelper helper;
    private SQLiteDatabase db;

    private Character character;
    private Setting setting;

    private Cursor cursor;
    private Handler update;

    private SensorManager sensorManager;
    private sensor cSensor;//calculate sensor
    private GPS gps;// Find Accurancy between GPS and sensor

    private Thread backsystem;
    private Thread currentexp;

    // 캐릭터 객체(Background) -> 성장도 및 비만도 계산 -> Database -> 캐릭터 객체(MainActivity)
    public BackGround() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        super.onStartCommand(intent, flags, startid);
        sendBroadcast(new Intent("resetModeOff"));
        initializeDB();
        gps=new GPS();
        sensorManager=(SensorManager)getSystemService(Activity.SENSOR_SERVICE);
        cSensor=new sensor(sensorManager);
        cSensor.onResume(sensorManager);
        gps.initGPS(getApplicationContext());

        update = new Handler(new Handler.Callback(){
        @Override
        public synchronized boolean handleMessage(Message msg) {
            helper.update(character);
            return true;
        }
    });

        backsystem = new Thread(new backSystem());
        backsystem.start();
        currentexp = new Thread(new currentExp());
        currentexp.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean checkSleepMode()
    {
        long current_time = System.currentTimeMillis() % oneDayMilies;
        long sleep_time = setting.getSleepTime()*1000;
        long wake_time = setting.getWakeTime()*1000;

        if(sleep_time < wake_time && current_time > sleep_time && current_time < wake_time)// 수면시간 < 기상시간
            return true;

        if(sleep_time > wake_time && current_time + oneDayMilies > sleep_time && current_time < wake_time)// 수면시간 > 기상시간(수면 도중 00시를 넘을 때) 현재시간이 00시 이후일 때
            return true;

        if(sleep_time > wake_time && current_time > sleep_time && current_time < wake_time + oneDayMilies)// 수면시간 > 기상시간(수면 도중 00시를 넘을 때) 현재시간이 00시 이전일 때
            return true;

        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    //만보기 시스템, 이동 관련 -> (파생) 비만도 조정, 캐릭터 스테이터스 갱신
    class backSystem extends Thread {
        private int count = 0;
        private long preNegative = System.currentTimeMillis();

        //비만도 경험치 상승치 기준점, 기록과 다르게 움직여야 하므로
        public void run() {
            while (play) {
                if(checkSleepMode()){
                    if (cSensor.getStep() > 0) {
                        character.setStep(character.getStep() + cSensor.getStep());
                        character.getLevel().setDecrement_count(0);//이동하였으므로
                        character.getLevel().setLast_exercisedtoCurrent();//최근 기록을 기록
                        cSensor.setStep(0);//센서에 기록된 걸음 수는 초기화함.
                    } else
                        count++;//이동하지 않을 시에 증가

                    if (count % 1800 == 0)// 30분 미 이동시
                    {
                        character.getLevel().countDecrement();//카운팅
                        character.getLevel().negativeAcquisition(preNegative);//비만도 상승
                        preNegative = System.currentTimeMillis();
                    }
                }

                try {
                    update.sendMessage(Message.obtain(update, 1, 0, 0));//DB 갱신
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        cSensor.onDestroy(sensorManager);
        sendBroadcast(new Intent("neverDie"));
        play = false;

        currentexp.interrupt();
        backsystem.interrupt();

        super.onDestroy();
    }


    //경험치 관련(칼로리-> (파생) 거리)
    class currentExp extends Thread {
        private int count = 0;
        private float prekcal = 0;//이전 칼로리

        public void run() {
            while (play) {
                if (checkSleepMode()) {
                    count++;//초당 카운트
                /*
               걷기 (느리게) - 3km/h 147 2.0
               걷기 (중간) - 5km/h 243 3.3
               걷기 (운동) - 5.5km/h 279 3.8
               자전거 (여유롭게) - <16km/h 294 4.0
               걷기(힘차게) - 6.5km/h 368 5.0
               스케이팅 - 16km/h 368 5.0
               자전거 (느린속도) - 18km/h 441 6.0
               달리기(조깅) - 8km/h 588 8.0
               달리기 - 10km/h 735 10.0
               자전거 (빠른속도) - 24km/h 735 10.0
               달리기 - 11km/h 845 11.5
               자전거 (매우 빠른속도) - 28km/h 882 12.0
               달리기 - 13km/h 992 13.5
               달리기 - 14.5km/h 1102 15.0
               달리기 - 16km/h 1176 16.0
               */
                    if (voting() > 28) character.CalorieAcquisition(1 / 60f, 12);
                    else if (voting() > 24) character.CalorieAcquisition(1 / 60f, 10);
                    else if (voting() > 20) character.CalorieAcquisition(1 / 60f, 8);
                    else if (voting() > 18) character.CalorieAcquisition(1 / 60f, 6);
                    else if (voting() > 14) character.CalorieAcquisition(1 / 60f, 4);
                    else if (voting() > 11)
                        character.CalorieAcquisition(1 / 60f, 11);// 평균적으로 100m 달리기 시 10~11초 걸리는 것을 감안
                    else if (voting() > 8)
                        character.CalorieAcquisition(1 / 60f, 8); // 오랜 운동을 기준으로 하였음.
                    else if (voting() > 6) character.CalorieAcquisition(1 / 60f, 5);
                    else if (voting() > 4) character.CalorieAcquisition(1 / 60f, 3);
                    else if (voting() > 2) character.CalorieAcquisition(1 / 60f, 2);
                    //초단위
                    if(voting() > 2 && voting() < 28)
                    character.setDistance(character.getDistance() + (voting() / 3600));//거리 증가

                    if (count == 60)//1분이 지날 시
                    {
                        //칼로리 경험치 합산
                        character.getLevel().expAcquisition((character.getCalories() - prekcal) / 10);
                        prekcal = character.getCalories();
                        count = 0;
                    }
                }

                if (System.currentTimeMillis() % oneDayMilies < 1000)//일일 기록 초기화
                {
                    prekcal = 0;
                    character.setDistance(0);
                    character.setCalories(0);
                    character.setStep(0);
                }

                try {
                    update.sendMessage(Message.obtain(update, 1, 0, 0));//DB 갱신
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }


    public void initializeDB() {
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        cursor = db.rawQuery("SELECT * FROM status;", null);
        cursor.moveToFirst();
        character = new Character(cursor.getString(cursor.getColumnIndex("name")),
                cursor.getFloat(cursor.getColumnIndex("height")),
                cursor.getFloat(cursor.getColumnIndex("weight")),
                cursor.getInt(cursor.getColumnIndex("character")),
                cursor.getInt(cursor.getColumnIndex("level")),
                cursor.getFloat(cursor.getColumnIndex("currentExp")),
                cursor.getFloat(cursor.getColumnIndex("negativeExp")),
                cursor.getFloat(cursor.getColumnIndex("calorie")),
                cursor.getInt(cursor.getColumnIndex("dayStep")),
                cursor.getFloat(cursor.getColumnIndex("dayDistance")),
                cursor.getLong(cursor.getColumnIndex("last_exercised")));
        cursor.close();

        cursor = db.rawQuery("SELECT * FROM setting;", null);
        cursor.moveToFirst();
        setting = new Setting(cursor.getInt(cursor.getColumnIndex("stepNotice")) == 1,
                cursor.getInt(cursor.getColumnIndex("distanceNotice")) == 1,
                cursor.getInt(cursor.getColumnIndex("stepGoal")),
                cursor.getInt(cursor.getColumnIndex("distanceGoal")),
                cursor.getInt(cursor.getColumnIndex("goalTime")),
                cursor.getInt(cursor.getColumnIndex("appUpdateTime")),
                cursor.getInt(cursor.getColumnIndex("sleepTime")),
                cursor.getInt(cursor.getColumnIndex("wakeTime")),
                cursor.getInt(cursor.getColumnIndex("setWidget")) == 1);
        cursor.close();
        db.close();
    }

    public float voting() {
        // less than 2 = does not count
        // more than 28 = does not count

        // check for turning
        if (cSensor.turning) return 0;

        // check for if nonzero gps is within sensor, if so return gps
        if (gps.distanceGPS > 0 && gps.distanceGPS - cSensor.getDistance() < 5) return gps.distanceGPS;

        // otherwise, return sensor
        return cSensor.getDistance();
    }
}
