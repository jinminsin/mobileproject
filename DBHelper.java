package com.example.healthyapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

    public void update(Character character) {
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
