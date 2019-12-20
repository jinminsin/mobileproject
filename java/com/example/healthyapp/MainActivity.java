package com.example.healthyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DBHelper helper;
    private SQLiteDatabase db;

    private Character character;

    private Cursor cursor;
    private Handler status;

    private ImageView characterImage;
    private TextView nameText;
    private TextView lvText;
    private TextView heightText;
    private TextView weightText;
    private TextView calorieText;
    private TextView expText;
    private ProgressBar progressbar;
    private boolean Play;
    private int playtime;
    private Thread system;
    private Intent background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        characterImage = findViewById(R.id.characterImage);

        nameText= findViewById(R.id.textName);
        lvText = findViewById(R.id.textLv);
        heightText = findViewById(R.id.textHeight);
        weightText = findViewById(R.id.textWeight);
        calorieText = findViewById(R.id.textCalorie);
        expText = findViewById(R.id.textExp);
        progressbar=findViewById(R.id.progressBar);

        initializeDB();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,0);

        status = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        cursor = db.rawQuery("SELECT * FROM status;", null);
                        cursor.moveToFirst();
                        character.getLevel().setLevel(cursor.getInt(cursor.getColumnIndex("level")));
                        character.getLevel().setCurrentExperience(cursor.getFloat(cursor.getColumnIndex("currentExp")));
                        character.setCalories(cursor.getFloat(cursor.getColumnIndex("calorie")));
                        cursor.close();

                        lvText.setText("Lv : " + character.getLevel().getLevel());
                        calorieText.setText("소모 칼로리 : " + String.format("%.2f", character.getCalories()) + " kcal");
                        expText.setText("EXP : (" + String.format("%.2f", character.getLevel().getCurrentExperience()) + "/" + character.getLevel().getMaxExperience() + ")");
                        progressbar.setMax(character.getLevel().getMaxExperience());
                        progressbar.setProgress((int) character.getLevel().getCurrentExperience());
                        break;
                    case 2://리셋일 경우
                        updateCharacter();

                        if (character.getCharacter() == 1) {
                            if (character.getLevel().getLevel() <= 5)
                                characterImage.setImageResource(R.drawable.a1);
                            else if (character.getLevel().getLevel() <= 10)
                                characterImage.setImageResource(R.drawable.a2);
                            else
                                characterImage.setImageResource(R.drawable.a3);
                        } else if (character.getCharacter() == 2) {
                            if (character.getLevel().getLevel() <= 5)
                                characterImage.setImageResource(R.drawable.b1);
                            else if (character.getLevel().getLevel() <= 10)
                                characterImage.setImageResource(R.drawable.b2);
                            else
                                characterImage.setImageResource(R.drawable.b3);
                        } else {
                            if (character.getLevel().getLevel() <= 5)
                                characterImage.setImageResource(R.drawable.c1);
                            else if (character.getLevel().getLevel() <= 10)
                                characterImage.setImageResource(R.drawable.c2);
                            else
                                characterImage.setImageResource(R.drawable.c3);
                        }

                        nameText.setText("어서오세요! " + character.getName() + " 님!");
                        lvText.setText("Lv : " + character.getLevel().getLevel());
                        heightText.setText("키 : " + character.getHeight());
                        weightText.setText("몸무게 : " + character.getWeight());
                        calorieText.setText("소모 칼로리 : " + String.format("%.2f", character.getCalories()) + " kcal");
                        expText.setText("EXP : (" + String.format("%.2f", character.getLevel().getCurrentExperience()) + "/" + character.getLevel().getMaxExperience() + ")");
                        progressbar.setMax(character.getLevel().getMaxExperience());
                        progressbar.setProgress((int) character.getLevel().getCurrentExperience());
                        break;
                }
                return true;
            }
        });

        if (character.getCharacter() == 0) {
            startActivityForResult(new Intent(MainActivity.this, FirstSetting.class), 0);
        }else {
            Play=true;
            system = new Thread(new updateScreen());
            system.setDaemon(true);
            system.start();
        }
    }

    private void askForPermission (String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "허가 승인", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "허가 거부", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onResume() {
        super.onResume();
        status.sendMessage(Message.obtain(status, 2, 0, 0));
        if(!system.isAlive() && character.getCharacter() != 0) {
            Play=true;
            system = new Thread(new updateScreen());
            system.setDaemon(true);
            system.start();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {//캐릭터 초기 설정
            if (resultCode == RESULT_OK) {
                status.sendMessage(Message.obtain(status, 2, 0, 0));
                Play = true;
                system.start();
                background = new Intent(this,BackGround.class);
                startService(background);
            }
        }

        if (requestCode == 1) {//앱 구동 설정
            if (resultCode != RESULT_CANCELED) {
                stopService(background);
                cursor = db.rawQuery("SELECT * FROM setting;", null);
                cursor.moveToFirst();
                playtime = cursor.getInt(cursor.getColumnIndex("appUpdateTime"));
                cursor.close();

                if(resultCode == 1)//시간 변경
                {
                    startService(background);
                }

                if(resultCode == 2)//캐릭터 삭제
                {
                    Play=false;
                    system.interrupt();
                    startActivityForResult(new Intent(MainActivity.this, FirstSetting.class), 0);
                }

                if(resultCode == 3)//목표 변경
                {
                    Play=false;
                    system.interrupt();
                    status.sendMessage(Message.obtain(status, 1, 0, 0));

                    Play = true;
                    system.start();
                    background = new Intent(this,BackGround.class);
                    startService(background);
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
                cursor.getFloat(cursor.getColumnIndex("currentExp")),
                cursor.getFloat(cursor.getColumnIndex("negativeExp")),
                cursor.getFloat(cursor.getColumnIndex("calorie")),
                cursor.getInt(cursor.getColumnIndex("dayStep")),
                cursor.getFloat(cursor.getColumnIndex("dayDistance")),
                cursor.getLong(cursor.getColumnIndex("last_exercised")));
        cursor.close();

        cursor = db.rawQuery("SELECT * FROM setting;", null);
        cursor.moveToFirst();
        playtime = cursor.getInt(cursor.getColumnIndex("appUpdateTime"));
        cursor.close();
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
        character.getLevel().setCurrentExperience(cursor.getFloat(cursor.getColumnIndex("currentExp")));
        character.setCalories(cursor.getFloat(cursor.getColumnIndex("calorie")));
        cursor.close();
    }

    public void ButtonClick(View view) {
        Intent option;
        switch (view.getId()) {
            case R.id.buttonNotice://일일 목표 설정 및 알림
                option = new Intent(MainActivity.this,NoticeGoal.class);
                startActivityForResult(option,1);
                break;
            case R.id.buttonDetail://자세히 보기 ( 오늘 하루 걸은 거리 및 걸음 횟수 등 )
                option = new Intent(MainActivity.this,DetailScreen.class);
                Play=false;
                system.interrupt();
                startActivity(option);
                break;
            case R.id.buttonSetting:// 수면 시간 설정, 갱신 간격 설정, 리셋(?)
                option = new Intent(MainActivity.this,AppSetting.class);
                startActivityForResult(option,1);
                break;
        }
    }

    public class updateScreen implements Runnable {
        @Override
        public void run() {
            while (Play) {
                try {
                    status.sendMessage(Message.obtain(status,1,0,0));
                    Thread.sleep(playtime*1000);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Play=false;
        stopService(background);
        sendBroadcast(new Intent("neverDie"));
        super.onDestroy();
    }
}
