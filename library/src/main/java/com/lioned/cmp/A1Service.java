package com.lioned.cmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lioned.log.Log;

public class A1Service extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.iv(Log.TAG, "onCreate: ");
    }
}