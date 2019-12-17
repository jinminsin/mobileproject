package com.example.healthyapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackGround extends Service {
    // 캐릭터 객체(Background) -> 성장도 및 비만도 계산 -> Database -> 캐릭터 객체(MainActivity)
    public BackGround() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        super.onStartCommand(intent, flags, startid);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
