package com.example.healthyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class neverStopService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if(intent.getAction().equals("neverDie")) {
            Handler timer = new Handler();
            timer.postDelayed(new task(context), 10000);
        }
    }

    class task implements Runnable {
        Context context;
        public task(Context context)
        {
            this.context=context;
        }
        @Override
        public void run() {
                context.startService(new Intent(context, BackGround.class));
        }
    }
}
