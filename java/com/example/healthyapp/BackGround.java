package com.example.healthyapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class BackGround extends Service {
    private final long oneDayMilies = 24*60*60*1000;//하루 시간
    private boolean play = true;
    private boolean noticeStep = true;
    private boolean noticeDistance = true;
    private boolean noticeGoal=true;
    private DBHelper helper;
    private SQLiteDatabase db;

    private Character character;
    private Setting setting;

    private Cursor cursor;
    private Handler update;

    private SensorManager sensorManager;
    private NotificationManager noticeManager;
    private Notification notice;
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
        initializeDB();
        gps=new GPS();
        sensorManager=(SensorManager)getSystemService(Activity.SENSOR_SERVICE);
        noticeManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        cSensor=new sensor(sensorManager);
        cSensor.onResume(sensorManager);
        gps.initGPS(getApplicationContext());

        update = new Handler(new Handler.Callback(){
            Intent intent;
            PendingIntent content;
            @Override
        public synchronized boolean handleMessage(Message msg) {
            switch(msg.what) {
                case 1://캐릭터 수정
                helper.update(character, db);
                break;
                case 2://목표달성 - 걸음
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    content = PendingIntent.getActivity(getApplicationContext()
                            , 0
                            , intent
                            , 0);
                    notice = new Notification.Builder(getApplicationContext())
                            .setContentTitle("목표 달성 완료!")
                            .setContentText("목표 걸음 수를 달성하셨어요")
                            .setSmallIcon(R.drawable.a1)
                            .setContentIntent(content)
                            .setWhen(System.currentTimeMillis())
                            .build();

                    noticeManager.notify(1, notice);
                    break;
                case 3://목표달성 - 걸음
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    content = PendingIntent.getActivity(getApplicationContext()
                            , 0
                            , intent
                            , 0);
                    notice = new Notification.Builder(getApplicationContext())
                            .setContentTitle("목표 달성 완료!")
                            .setContentText("목표 이동 거리를 달성하셨어요")
                            .setSmallIcon(R.drawable.a2)
                            .setContentIntent(content)
                            .setWhen(System.currentTimeMillis())
                            .build();
                    noticeManager.notify(2, notice);
                    break;
                case 4://달성실패
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    content = PendingIntent.getActivity(getApplicationContext()
                            , 0
                            , intent
                            , 0);
                    notice = new Notification.Builder(getApplicationContext())
                            .setContentTitle("목표 달성 실패!")
                            .setContentText("목표 달성을 못하셨네요~~")
                            .setSmallIcon(R.drawable.a2)
                            .setContentIntent(content)
                            .setWhen(System.currentTimeMillis())
                            .build();
                    noticeManager.notify(2, notice);
                    break;
            }
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
        long current_time = (System.currentTimeMillis()%oneDayMilies)/60000+540;//한국 표준으로 우선 고정
        long sleep_time = setting.getSleepTime();
        long wake_time = setting.getWakeTime();

        wake_time = wake_time - sleep_time < 0 ? oneDayMilies/60000 + (wake_time - sleep_time): wake_time - sleep_time;
        current_time = current_time - sleep_time < 0 ? oneDayMilies/60000 + (current_time - sleep_time): current_time - sleep_time;
        sleep_time = 0;
        //수면 시간을 0으로 하는 시간 간격 보정

        if(sleep_time < current_time && current_time < wake_time) return false;


        return true;
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

                    if(character.getStep() > setting.getStepGoal() && noticeStep)//목표달성
                    {
                        noticeStep = false;
                        update.sendMessage(Message.obtain(update, 2, 0, 0));
                    }


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
        play = false;
        currentexp.interrupt();
        backsystem.interrupt();
        super.onDestroy();
    }

    //경험치 관련(칼로리-> (파생) 거리)
    class currentExp extends Thread {
        private int count = 0;
        private float prekcal = 0;//이전 칼로리
        private float speed = 0;

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
                    speed = voting();

                    if (speed > 28) character.CalorieAcquisition(1 / 60f, 12);
                    else if (speed > 24) character.CalorieAcquisition(1 / 60f, 10);
                    else if (speed > 20) character.CalorieAcquisition(1 / 60f, 8);
                    else if (speed > 18) character.CalorieAcquisition(1 / 60f, 6);
                    else if (speed > 14) character.CalorieAcquisition(1 / 60f, 4);
                    else if (speed > 11)
                        character.CalorieAcquisition(1 / 60f, 11);// 평균적으로 100m 달리기 시 10~11초 걸리는 것을 감안
                    else if (speed > 8)
                        character.CalorieAcquisition(1 / 60f, 8); // 오랜 운동을 기준으로 하였음.
                    else if (speed > 6) character.CalorieAcquisition(1 / 60f, 5);
                    else if (speed > 4) character.CalorieAcquisition(1 / 60f, 3);
                    else if (speed > 2) character.CalorieAcquisition(1 / 60f, 2);
                    //초단위
                    if (speed > 2 && speed < 28)
                        character.setDistance(character.getDistance() + (speed / 3600));//거리 증가
                    //2km/h /3600 = 1/1800 km/s

                    if (character.getDistance() > setting.getDistanceGoal() && noticeDistance)//목표달성
                    {
                        noticeDistance = false;
                        update.sendMessage(Message.obtain(update, 3, 0, 0));
                    }


                    if (count == 60)//1분이 지날 시
                    {
                        //칼로리 경험치 합산
                        character.getLevel().expAcquisition((character.getCalories() - prekcal) / 10);
                        if (character.getLevel().getMaxExperience() < character.getLevel().getCurrentExperience())
                            character.getLevel().levelUp();
                        prekcal = character.getCalories();
                        count = 0;
                    }

                    if ((System.currentTimeMillis() % oneDayMilies) / 60000 + 540 > setting.getGoalTime() && noticeGoal && noticeDistance && noticeStep)//
                    {
                        noticeGoal = false;
                        update.sendMessage(Message.obtain(update, 4, 0, 0));
                    }


                    if (System.currentTimeMillis() % oneDayMilies < 1000)//일일 기록 초기화
                    {
                        prekcal = 0;
                        character.setDistance(0);
                        character.setCalories(0);
                        character.setStep(0);
                        noticeDistance = true;
                        noticeStep = true;
                        noticeGoal = true;
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
                cursor.getInt(cursor.getColumnIndex("wakeTime")));
        cursor.close();
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
