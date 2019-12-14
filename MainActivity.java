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

    private TextView lvText;
    private TextView heightText;
    private TextView weightText;
    private TextView calorieText;
    private TextView ExpText;
    private boolean Play=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        lvText = findViewById(R.id.textLv);
        heightText = findViewById(R.id.textheight);
        weightText = findViewById(R.id.textweight);
        calorieText = findViewById(R.id.textcalorie);
        ExpText = findViewById(R.id.textExp);

        cursor = db.rawQuery("SELECT * FROM record;", null);
        cursor.moveToFirst();
        character = new Character(cursor.getString(cursor.getColumnIndex("name")),
                cursor.getFloat(cursor.getColumnIndex("height")),
                cursor.getFloat(cursor.getColumnIndex("weight")),
                cursor.getInt(cursor.getColumnIndex("character")),
                cursor.getInt(cursor.getColumnIndex("level")),
                cursor.getInt(cursor.getColumnIndex("experience")),
                cursor.getInt(cursor.getColumnIndex("calorie")));

        cursor.close();

        status = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                lvText.setText("Lv : " + character.getLevel().getLevel());
                heightText.setText("키 : " + character.getHeight());
                weightText.setText("몸무게 : " + character.getWeight());
                calorieText.setText("소모 칼로리 : " + character.getCalories());
                ExpText.setText("EXP : (" + character.getLevel().getCurrentExperience() + "/" + character.getLevel().getMaxExperience() + ")");
                return true;
            }
        });

        if (character.getCharacter() == 0) {
            startActivityForResult(new Intent(MainActivity.this, FirstSetting.class), 0);
        }

        Thread system = new Thread(new Calculate_Experience());
        system.setDaemon(true);
        system.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                character.setName(data.getStringExtra("name"));
                character.setHeight(data.getFloatExtra("height", 0));
                character.setWeight(data.getFloatExtra("weight", 0));
                character.setCharacter(data.getIntExtra("character", 0));
                character.getLevel().setLevel(1);
                helper.update(character);
            }
        }
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGPS:
                break;
            case R.id.buttondetail:
                break;
            case R.id.buttonsetting:
                break;
        }
    }

    public class Calculate_Experience implements Runnable {
        @Override
        public void run() {
            while (Play) {
                try {
                    status.sendMessage(Message.obtain(status,1,0,0));
                    Thread.sleep(60000);
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
