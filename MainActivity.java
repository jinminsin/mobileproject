package com.example.healthyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper=new DBHelper(this);
    private SQLiteDatabase db = helper.getWritableDatabase();
    private Character character;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cursor=db.rawQuery("SELECT * FROM record;", null);
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
            startActivity(new Intent(MainActivity.this,FirstSetting.class));
        }

        Thread system = new Thread(new Calculate_Experience());
        system.setDaemon(true);
        system.start();
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
                    Thread.sleep(60000);
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

        public void update(String name,float height, float weight,int level,int experience,int character, int calorie) {
            // 읽고 쓰기가 가능하게 DB 열기
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE record SET name='"+name+"' WHERE _id = 1;");
            db.execSQL("UPDATE record SET height="+height+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET weight="+weight+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET level="+level+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET experience="+experience+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET character="+character+" WHERE _id = 1;");
            db.execSQL("UPDATE record SET calorie="+calorie+" WHERE _id = 1;");
            db.close();
        }
    }
}
