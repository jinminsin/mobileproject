package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class DetailScreen extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;

    private Character character;
    private Setting setting;

    private Cursor cursor;
    private Handler status;

    private TextView nameText;
    private TextView lvText;
    private TextView heightText;
    private TextView weightText;
    private TextView sleepTimeText;
    private TextView stepGoalText;
    private TextView distanceGoalText;

    private TextView calorieText;
    private TextView distanceText;
    private TextView stepText;

    private TextView negativeExpText;
    private TextView lastExercisedText;
    private TextView positiveExpText;
    private TextView maxExpText;

    private boolean Play=true;
    private Thread system;
    private int appUpdateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);

        nameText= findViewById(R.id.textName);
        lvText = findViewById(R.id.textLv);
        heightText = findViewById(R.id.textHeight);
        weightText = findViewById(R.id.textWeight);

        calorieText = findViewById(R.id.textCalorie);
        sleepTimeText = findViewById(R.id.textSleepTime);
        stepGoalText = findViewById(R.id.textStepGoal);
        distanceGoalText = findViewById(R.id.textDistanceGoal);
        distanceText = findViewById(R.id.textDistance);
        stepText = findViewById(R.id.textStep);
        negativeExpText = findViewById(R.id.textNegativeExp);
        lastExercisedText = findViewById(R.id.textLastExercise);
        positiveExpText = findViewById(R.id.textCurrentExp);
        maxExpText = findViewById(R.id.textMaxExp);

        initializeDB();

        status = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                updateCharacter();
                lvText.setText("Lv : " + character.getLevel().getLevel());
                calorieText.setText("칼로리 : " +  String.format("%.2f",character.getCalories())+" kcal");
                stepText.setText("누적 걸음 수 : "+character.getStep()+" 걸음");
                distanceText.setText("누적 이동 거리 : "+String.format("%.2f",character.getDistance())+" km");
                negativeExpText.setText("비만도 : "+String.format("%.2f",character.getLevel().getNegativeExperience()));
                lastExercisedText.setText((new SimpleDateFormat("- yyyy/MM/dd (E) HH:mm")).format(character.getLevel().getLast_exercised()));
                positiveExpText.setText("현재 경험치 : "+String.format("%.2f",character.getLevel().getCurrentExperience()));
                maxExpText.setText("필요 경험치 : "+character.getLevel().getMaxExperience());
                return true;
            }
        });

        system = new Thread(new updateScreen());
        system.setDaemon(true);
        system.start();
    }

    public void initializeDB()
    {
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
        setting = new Setting(cursor.getInt(cursor.getColumnIndex("stepNotice"))==1,
                cursor.getInt(cursor.getColumnIndex("distanceNotice"))==1,
                cursor.getInt(cursor.getColumnIndex("stepGoal")),
                cursor.getInt(cursor.getColumnIndex("distanceGoal")),
                cursor.getInt(cursor.getColumnIndex("goalTime")),
                cursor.getInt(cursor.getColumnIndex("appUpdateTime")),
                cursor.getInt(cursor.getColumnIndex("sleepTime")),
                cursor.getInt(cursor.getColumnIndex("wakeTime")));
        cursor.close();
        db.close();

        nameText.setText("이름 : "+character.getName());
        lvText.setText("Lv : " + character.getLevel().getLevel());
        heightText.setText("키 : " + character.getHeight()+" cm");
        weightText.setText("몸무게 : " + character.getWeight()+ " kg");
        sleepTimeText.setText("수면 시간 : "+String.format("%02d",setting.getHours(setting.getSleepTime()))+
                ":"+String.format("%02d",setting.getMinutes(setting.getSleepTime()))+"~"+
                String.format("%02d",setting.getHours(setting.getWakeTime()))+":"+
                String.format("%02d",setting.getMinutes(setting.getWakeTime())));
        stepGoalText.setText("일일 목표 걸음 수 : "+setting.getStepGoal()+" 걸음");
        distanceGoalText.setText("일일 목표 이동 거리 : "+setting.getDistanceGoal()+ " km");

        calorieText.setText("칼로리 : " + String.format("%.2f",character.getCalories())+" kcal");
        stepText.setText("누적 걸음 수 : "+character.getStep()+" 걸음");
        distanceText.setText("누적 이동 거리 : "+String.format("%.2f",character.getDistance())+" km");

        negativeExpText.setText("비만도 : "+String.format("%.2f",character.getLevel().getNegativeExperience()));
        lastExercisedText.setText((new SimpleDateFormat("- yyyy/MM/dd (E) HH:mm")).format(character.getLevel().getLast_exercised()));
        positiveExpText.setText("현재 경험치 : "+String.format("%.2f",character.getLevel().getCurrentExperience()));
        maxExpText.setText("필요 경험치 : "+String.format("%.2f",character.getLevel().getCurrentExperience()));
    }

    public void updateCharacter()
    {
        db = helper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM status;", null);
        cursor.moveToFirst();
        character.setName(cursor.getString(cursor.getColumnIndex("name")));
        character.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
        character.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
        character.setCharacter(cursor.getInt(cursor.getColumnIndex("character")));
        character.getLevel().setLevel(cursor.getInt(cursor.getColumnIndex("level")));
        character.getLevel().setCurrentExperience(cursor.getFloat(cursor.getColumnIndex("currentExp")));
        character.getLevel().setNegativeExperience(cursor.getFloat(cursor.getColumnIndex("negativeExp")));
        character.setCalories(cursor.getFloat(cursor.getColumnIndex("calorie")));
        character.setStep(cursor.getInt(cursor.getColumnIndex("dayStep")));
        character.setDistance(cursor.getFloat(cursor.getColumnIndex("dayDistance")));
        character.getLevel().setLast_exercised(cursor.getLong(cursor.getColumnIndex("last_exercised")));
        cursor.close();
        db.close();
    }

    public class updateScreen implements Runnable {
        @Override
        public void run() {
            while (Play) {
                try {
                    status.sendMessage(Message.obtain(status,1,0,0));
                    Thread.sleep(setting.getAppUpdateTime()*1000);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    public void ButtonClick(View view) {
        finish();
    }

}
