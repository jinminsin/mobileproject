package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

public class AppSetting extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;

    private Setting setting;

    private Cursor cursor;

    private CheckBox characterReset;
    private RadioButton setWidgetOn;
    private RadioButton setWidgetOff;
    private RadioGroup setWidget;
    private Spinner updateTime;
    private Spinner sleepHour;
    private Spinner sleepMinute;
    private Spinner wakeHour;
    private Spinner wakeMinute;

    private ArrayAdapter<String> sTime;
    private ArrayList<String> time;
    private ArrayAdapter<String> sHour;
    private ArrayAdapter<String> sMinute;
    private ArrayList<String> hour;
    private ArrayList<String> minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

        initializeDB();

        characterReset=findViewById(R.id.checkCharacterDelete);
        updateTime = findViewById(R.id.update);
        sleepHour=findViewById(R.id.sleepHour);
        sleepMinute=findViewById(R.id.sleepMinute);
        wakeHour=findViewById(R.id.wakeHour);
        wakeMinute=findViewById(R.id.wakeMinute);

        time=new ArrayList<>();
        for(int i=10,digit=10;i<=300;i+=digit) {
            time.add(String.format("%3d", i));
            if(i==60)
                digit=30;
        }

        hour=new ArrayList<>();
        for(int i=0;i<24;i++)
            hour.add(String.format("%02d",i));

        minute=new ArrayList<>();
        for(int i=0;i<60;i++)
            minute.add(String.format("%02d",i));

        sTime=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,time);
        sHour=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,hour);
        sMinute= new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,minute);

        sleepHour.setAdapter(sHour);
        sleepMinute.setAdapter(sMinute);
        wakeHour.setAdapter(sHour);
        wakeMinute.setAdapter(sMinute);
        updateTime.setAdapter(sTime);

        updateTime.setSelection(setting.getAppUpdateTime()<=60?setting.getAppUpdateTime()/10 - 1:(setting.getAppUpdateTime()-60)/30 + 5);
        sleepHour.setSelection(setting.getHours(setting.getSleepTime()));
        sleepMinute.setSelection(setting.getMinutes(setting.getSleepTime()));
        wakeHour.setSelection(setting.getHours(setting.getWakeTime()));
        wakeMinute.setSelection(setting.getMinutes(setting.getWakeTime()));
    }

    public void onClickbutton(View view) {
        switch(view.getId()){
            case R.id.completebutton:
                setting.setAppUpdateTime(Integer.parseInt(updateTime.getSelectedItem().toString().trim()));
                setting.setSleepTime(sleepHour.getSelectedItemPosition()*60+sleepMinute.getSelectedItemPosition());
                setting.setWakeTime(wakeHour.getSelectedItemPosition()*60+wakeMinute.getSelectedItemPosition());

                if(characterReset.isChecked()){
                    helper.update(setting,db);
                    setResult(2);
                    finish();}

                helper.update(setting,db);
                setResult(1);
                finish();
                break;
            case R.id.resetbutton:
                characterReset.setChecked(false);
                updateTime.setSelection(setting.getAppUpdateTime()<=60?setting.getAppUpdateTime()/10 - 1:(setting.getAppUpdateTime()-60)/30 + 5);
                sleepHour.setSelection(setting.getHours(setting.getSleepTime()));
                sleepMinute.setSelection(setting.getMinutes(setting.getSleepTime()));
                wakeHour.setSelection(setting.getHours(setting.getWakeTime()));
                wakeMinute.setSelection(setting.getMinutes(setting.getWakeTime()));
                break;
        }
    }

    public void initializeDB()
    {
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

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
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
