package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;

    private Character character;

    private Cursor cursor;
    private Handler status;

    private TextView nameText;
    private TextView lvText;
    private TextView heightText;
    private TextView weightText;
    private TextView calorieText;
    private TextView expText;
    private boolean Play;
    private int playtime;
    private Thread system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText= findViewById(R.id.textName);
        lvText = findViewById(R.id.textLv);
        heightText = findViewById(R.id.textHeight);
        weightText = findViewById(R.id.textWeight);
        calorieText = findViewById(R.id.textCalorie);
        expText = findViewById(R.id.textExp);

        initializeDB();

        if (character.getCharacter() == 0) {
            startActivityForResult(new Intent(MainActivity.this, FirstSetting.class), 0);
        }else {
            Play=true;
            system = new Thread(new updateScreen());
            system.setDaemon(true);
            system.start();
        }
    }

    protected void onResume() {
        super.onResume();

        if(!system.isAlive()){
            status.sendMessage(Message.obtain(status, 1, 0, 0));
            Play=true;
            system.start();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {//캐릭터 초기 설정
            if (resultCode == RESULT_OK) {
                status.sendMessage(Message.obtain(status, 1, 0, 0));
                Play = true;
                system = new Thread(new updateScreen());
                system.setDaemon(true);
                system.start();
            }
        }

        if (requestCode == 2) {//앱 구동 설정
            if (resultCode != RESULT_CANCELED) {
                cursor = db.rawQuery("SELECT * FROM setting;", null);
                cursor.moveToFirst();
                playtime = cursor.getInt(cursor.getColumnIndex("appUpdateTime"));
                cursor.close();

                if (resultCode == 2)//기록 삭제
                {
                    character.setStep(0);
                    character.setDistance(0);
                    character.setCalories(0);
                    helper.update(character);
                }

                if(resultCode == 3)//캐릭터 삭제
                {
                    Play = false;
                    startActivityForResult(new Intent(MainActivity.this, FirstSetting.class), 0);
                }
            }
        }
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
                cursor.getInt(cursor.getColumnIndex("currentExp")),
                cursor.getFloat(cursor.getColumnIndex("negativeExp")),
                cursor.getInt(cursor.getColumnIndex("calorie")),
                cursor.getInt(cursor.getColumnIndex("dayStep")),
                cursor.getFloat(cursor.getColumnIndex("dayDistance")));
        cursor.close();

        cursor = db.rawQuery("SELECT * FROM setting;", null);
        cursor.moveToFirst();
        playtime = cursor.getInt(cursor.getColumnIndex("appUpdateTime"));
        cursor.close();

        status = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                updateCharacter();
                nameText.setText("어서오세요! "+character.getName()+" 님!");
                lvText.setText("Lv : " + character.getLevel().getLevel());
                heightText.setText("키 : " + character.getHeight());
                weightText.setText("몸무게 : " + character.getWeight());
                calorieText.setText("소모 칼로리 : " + character.getCalories());
                expText.setText("EXP : (" + character.getLevel().getCurrentExperience() + "/" + character.getLevel().getMaxExperience() + ")");
                return true;
            }
        });
    }

    public void updateCharacter()
    {
        cursor = db.rawQuery("SELECT * FROM status;", null);
        cursor.moveToFirst();
        character.setName(cursor.getString(cursor.getColumnIndex("name")));
        character.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
        character.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
        character.setCharacter(cursor.getInt(cursor.getColumnIndex("character")));
        character.getLevel().setLevel(cursor.getInt(cursor.getColumnIndex("level")));
        character.getLevel().setCurrentExperience(cursor.getInt(cursor.getColumnIndex("currentExp")));
        character.getLevel().setNegativeExperience(cursor.getFloat(cursor.getColumnIndex("negativeExp")));
        character.setCalories(cursor.getInt(cursor.getColumnIndex("calorie")));
        character.setStep(cursor.getInt(cursor.getColumnIndex("dayStep")));
        character.setDistance(cursor.getFloat(cursor.getColumnIndex("dayDistance")));
        cursor.close();
    }

    public void ButtonClick(View view) {
        Intent option;
        switch (view.getId()) {
            case R.id.buttonNotice://일일 목표 설정 및 알림
                option = new Intent(MainActivity.this,NoticeGoal.class);
                startActivity(option);
                break;
            case R.id.buttonDetail://자세히 보기 ( 오늘 하루 걸은 거리 및 걸음 횟수 등 )
                option = new Intent(MainActivity.this,DetailScreen.class);
                Play=false;
                system.interrupt();
                startActivity(option);
                break;
            case R.id.buttonSetting:// 수면 시간 설정, 갱신 간격 설정, 리셋(?), 기록 삭제
                option = new Intent(MainActivity.this,AppSetting.class);
                startActivityForResult(option,2);
                break;
        }
    }

    public class updateScreen implements Runnable {
        @Override
        public void run() {
            while (Play) {
                try {
                    status.sendMessage(Message.obtain(status,1,0,0));
                    Thread.sleep(playtime*100);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Play=false;
        super.onDestroy();
    }
}
