package com.example.healthyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;
    private Character character;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        cursor=db.rawQuery("SELECT * FROM record;", null);
        cursor.moveToFirst();
        character = new Character(cursor.getString(cursor.getColumnIndex("name")),
                cursor.getFloat(cursor.getColumnIndex("height")),
                cursor.getFloat(cursor.getColumnIndex("weight")),
                cursor.getInt(cursor.getColumnIndex("character")),
                cursor.getInt(cursor.getColumnIndex("level")),
                cursor.getInt(cursor.getColumnIndex("experience")),
                cursor.getInt(cursor.getColumnIndex("calorie")));

        cursor.close();

        if(character.getCharacter() == 0)
        {
            startActivityForResult(new Intent(MainActivity.this,FirstSetting.class),0);
        }

        Thread system = new Thread(new Calculate_Experience());
        system.setDaemon(true);
        system.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0)
        {
            if(resultCode == RESULT_OK)
            {

            }
        }
    }

    public void ButtonClick(View view) {
        switch(view.getId())
        {
            case R.id.buttonGPS:
                break;
            case R.id.buttondetail:
                break;
            case R.id.buttonsetting:
                break;
        }
    }

    public class Calculate_Experience implements Runnable
    {
        @Override
        public void run() {
            while(true) {
                try {
                    Log.i("Thread Check","Thread OK");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(@Nullable Context context) {
            super(context, "record.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE record " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT, height FLOAT, weight FLOAT, character INTEGER ,level INTEGER, experience INTEGER, calorie INTEGER);");

            sqLiteDatabase.execSQL("INSERT INTO record" +
                    " (name, height, weight, character, level, experience, calorie)" +
                    " VALUES ('human',0,0,0,0,0,0);");//초기레코드
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public void update() {
            // 읽고 쓰기가 가능하게 DB 열기
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE record SET name='"+character.getName()+"' WHERE _id = 1;");
            db.execSQL("UPDATE record SET height="+character.getHeight()+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET weight="+character.getWeight()+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET level="+character.getLevel().getLevel()+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET experience="+character.getLevel().getCurrentExperience()+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET character="+character.getCharacter()+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET calorie="+character.getCalories() +" WHERE _id = 1;");
            db.close();
        }
    }
}
