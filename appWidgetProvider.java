package com.example.healthyapp;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

public class appWidgetProvider extends AppWidgetProvider {
    private static boolean play;
    private static DBHelper helper;
    private static SQLiteDatabase db;
    private static Cursor cursor;
    private static Character character;

    ////////서비스ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    public static class UpdateService extends Service implements Runnable  {

        private Handler mHandler;

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
            db.close();
        }

        @Override
        public void onCreate() {
            play=true;
            initializeDB();
            super.onCreate();
            mHandler = new Handler();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            mHandler.post(this);
            return START_NOT_STICKY;
        }

        @Override
        public boolean onUnbind(Intent intent) {
            return true;
        }


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void run() {
            while (play) {
                ImageHelper imageHelper = new ImageHelper();
                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget_layout);

                views.setTextViewText(R.id.w_textView2, "exp : " + String.format("( %.2f / %d )", character.getLevel().getCurrentExperience(), character.getLevel().getMaxExperience())); //경험치 뷰 설정
                views.setTextViewText(R.id.w_textView1, "레벨 : " + character.getLevel().getLevel()); //레벨 뷰 설정

                /*캐릭터 이미지 뷰 설정*/
                if (character.getCharacter() == 1) { //캐릭터 타입 1
                    if (character.getLevel().getLevel() <= 5) { //조건 //지금은 2초단위로 변경되도록 해놨음. 레벨단위로 수정필요. 레벨은 data2
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a1);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else if (character.getLevel().getLevel() <= 10) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a2);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a3);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    }

                } else if (character.getCharacter() == 2) { //캐릭터 타입 2
                    if (character.getLevel().getLevel() <= 5) { //조건 //지금은 2초단위로 변경되도록 해놨음. 레벨단위로 수정필요. 레벨은 data2
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b1);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else if (character.getLevel().getLevel() <= 10) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b2);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b3);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    }
                } else { //캐릭터 타입 3
                    if (character.getLevel().getLevel() <= 5) { //조건 //지금은 2초단위로 변경되도록 해놨음. 레벨단위로 수정필요. 레벨은 data2
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.c1);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else if (character.getLevel().getLevel() <= 10) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.c2);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.c3);
                        bitmap = imageHelper.getRoundedCornerBitmap(bitmap, 120);
                        views.setImageViewBitmap(R.id.w_imageView1, bitmap);
                    }
                }
                /*캐릭터 이미지 뷰 설정 끝*/

                //위젯업데이트
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                ComponentName testWidge = new ComponentName(this, appWidgetProvider.class);
                appWidgetManager.updateAppWidget(testWidge, views);
            }

            updateCharacter();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ;
            }
        }//run

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
    }//UpdateService


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intent = new Intent(context,UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(final Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        play=false;
        Intent intent = new Intent(context,UpdateService.class);
        context.stopService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

    }
}
