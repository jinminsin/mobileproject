package com.example.healthyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;


public class NoticeGoal extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;

    private Setting setting;

    private Cursor cursor;

    private EditText stepGoal;
    private EditText distanceGoal;
    private RadioButton stepNoticeOn;
    private RadioButton stepNoticeOff;
    private RadioGroup stepNotice;
    private RadioButton distanceNoticeOn;
    private RadioButton distanceNoticeOff;
    private RadioGroup distanceNotice;
    private Spinner goalHour;
    private Spinner goalMinute;

    private ArrayAdapter<String> sHour;
    private ArrayAdapter<String> sMinute;
    private ArrayList<String> hour;
    private ArrayList<String> minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_goal);

        initializeDB();

        stepGoal = findViewById(R.id.stepGoal);
        distanceGoal=findViewById(R.id.distanceGoal);
        stepNotice = findViewById(R.id.stepNotice);
        distanceNotice = findViewById(R.id.distanceNotice);
        stepNoticeOn = findViewById(R.id.stepNoticeOn);
        distanceNoticeOn = findViewById(R.id.distanceNoticeOn);
        stepNoticeOff = findViewById(R.id.stepNoticeOff);
        distanceNoticeOff = findViewById(R.id.distanceNoticeOff);
        goalHour=findViewById(R.id.goalHour);
        goalMinute=findViewById(R.id.goalMinute);

        stepGoal.setText(String.format("%d",setting.getStepGoal()));
        if(setting.isStepNotice())
        {
            stepNoticeOn.setChecked(true);
        }else
        {
            stepNoticeOff.setChecked(true);
        }
        distanceGoal.setText(String.format("%d",setting.getDistanceGoal()));
        if(setting.isDistanceNotice())
        {
            distanceNoticeOn.setChecked(true);
        }else
        {
            distanceNoticeOff.setChecked(true);
        }

        hour=new ArrayList<>();
        for(int i=0;i<24;i++)
            hour.add(String.format("%02d",i));

        minute=new ArrayList<>();
        for(int i=0;i<60;i++)
            minute.add(String.format("%02d",i));

        sHour=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,hour);
        sMinute= new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,minute);

        goalHour.setAdapter(sHour);
        goalMinute.setAdapter(sMinute);

        goalHour.setSelection(setting.getHours(setting.getGoalTime()));
        goalMinute.setSelection(setting.getMinutes(setting.getGoalTime()));
    }

    public void onClickbutton(View view) {
        switch(view.getId())
        {
            case R.id.completebutton:
                setting.setStepGoal(Integer.parseInt(stepGoal.getText().toString()));
                setting.setDistanceGoal(Integer.parseInt(distanceGoal.getText().toString()));
                switch(stepNotice.getCheckedRadioButtonId())
                {
                    case R.id.stepNoticeOn:
                        setting.setStepNotice(true);
                        break;
                    case R.id.stepNoticeOff:
                        setting.setStepNotice(false);
                }

                switch(distanceNotice.getCheckedRadioButtonId())
                {
                    case R.id.distanceNoticeOn:
                        setting.setDistanceNotice(true);
                        break;
                    case R.id.distanceNoticeOff:
                        setting.setDistanceNotice(false);
                }
                setting.setGoalTime(goalHour.getSelectedItemPosition()*60+goalMinute.getSelectedItemPosition());
                helper.update(setting, db);
                finish();
                break;
            case R.id.resetbutton:
                stepGoal.setText(String.format("%d",setting.getStepGoal()));
                if(setting.isStepNotice())
                {
                    stepNoticeOn.setChecked(true);
                }else
                {
                    stepNoticeOff.setChecked(true);
                }
                distanceGoal.setText(String.format("%d",setting.getDistanceGoal()));
                if(setting.isDistanceNotice())
                {
                    distanceNoticeOn.setChecked(true);
                }else
                {
                    distanceNoticeOff.setChecked(true);
                }
                goalHour.setSelection(setting.getHours(setting.getGoalTime()));
                goalMinute.setSelection(setting.getMinutes(setting.getGoalTime()));
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
}
