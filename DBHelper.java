package com.example.healthyapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "status.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE status " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, height FLOAT, weight FLOAT, character INTEGER ,level INTEGER, currentExp INTEGER, negativeExp FLOAT, calorie INTEGER, last_exercised INTEGER, dayStep INTEGER, dayDistance FLOAT);");
        //캐릭터(이름, 키, 몸무게, 캐릭터 이미지, 레벨, 성장도, 비만도, 칼로리, 일일 누적 걸음, 일일 누적 거리)

        sqLiteDatabase.execSQL("CREATE TABLE setting " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "stepNotice INTEGER,distanceNotice INTEGER,stepGoal INTEGER, distanceGoal INTEGER,goalTime INTEGER, appUpdateTime INTEGER, sleepTime INTEGER, wakeTime INTEGER, setWidget INTEGER);");
        //설정(걸음 수 알림, 걸음거리 알림, 걸음 수 목표, 거리 목표, 갱신주기, 수면시간, 기상시간, 위젯 세팅)

        sqLiteDatabase.execSQL("INSERT INTO status" +
                " (name, height, weight, character, level, currentExp, negativeExp, calorie, last_exercised, dayStep, dayDistance)" +
                " VALUES ('human',0,0,0,0,0,0,0,0,0,0);");//초기레코드 캐릭터

        sqLiteDatabase.execSQL("INSERT INTO setting" +
                " (stepNotice, distanceNotice, stepGoal, distanceGoal, goalTime, appUpdateTime, sleepTime, wakeTime, setWidget)" +
                " VALUES (0,0,0,0,0,60,0,0,0);");//초기레코드 세팅

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void update(Character character) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE status SET name='"+character.getName()+"' WHERE _id = 1;");
        db.execSQL("UPDATE status SET height="+character.getHeight()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET weight="+character.getWeight()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET level="+character.getLevel().getLevel()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET currentExp="+character.getLevel().getCurrentExperience()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET negativeExp="+character.getLevel().getNegativeExperience()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET character="+character.getCharacter()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET calorie="+character.getCalories() +" WHERE _id = 1;");
        db.execSQL("UPDATE status SET last_exercised="+character.getLevel().getLast_exercised()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET dayStep="+character.getStep()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET dayDistance="+character.getDistance()+" WHERE _id = 1;");

        db.close();
    }

    public void update(Setting setting) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE status SET stepNotice="+((setting.isStepNotice() == true) ? 1 : 0)+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET distanceNotice="+((setting.isDistanceNotice() == true) ? 1 : 0)+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET stepGoal="+setting.getStepGoal()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET distanceGoal="+setting.getDistanceGoal()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET goalTime="+setting.getGoalTime()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET appUpdateTime="+setting.getAppUpdateTime()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET sleepTime="+setting.getSleepTime()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET wakeTime="+setting.getWakeTime()+" WHERE _id = 1;");
        db.execSQL("UPDATE status SET setWidget="+((setting.isSetWidget() == true) ? 1 : 0)+" WHERE _id = 1;");
        db.close();
    }
}
